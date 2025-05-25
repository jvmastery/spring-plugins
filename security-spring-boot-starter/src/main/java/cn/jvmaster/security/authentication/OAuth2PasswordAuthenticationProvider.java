package cn.jvmaster.security.authentication;

import cn.jvmaster.security.constant.AuthorizationGrantTypeBuilder;
import cn.jvmaster.security.util.AuthorizationUtils;
import java.security.Principal;
import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

/**
 * 密码模式认证提供者
 * @author AI
 * @date 2025/4/15 10:14
 * @version 1.0
**/
public class OAuth2PasswordAuthenticationProvider implements AuthenticationProvider {
    private final AuthenticationManager authenticationManager;
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    public OAuth2PasswordAuthenticationProvider(AuthenticationManager authenticationManager, OAuth2AuthorizationService authorizationService, OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2PasswordAuthenticationToken passwordAuthentication = (OAuth2PasswordAuthenticationToken) authentication;
        OAuth2ClientAuthenticationToken clientPrincipal = AuthorizationUtils.getAuthenticatedClientElseThrowInvalidClient(passwordAuthentication);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        if (registeredClient == null) {
            return null;
        }

        // 用户名密码认证
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(passwordAuthentication.getUsername(), passwordAuthentication.getPassword());
        Authentication userAuthentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        // 授权范围认证
        Set<String> requestedScopes = passwordAuthentication.getScopes();
        Set<String> allowedScopes = registeredClient.getScopes();
        if (!allowedScopes.containsAll(requestedScopes)) {
            throw new OAuth2AuthenticationException("invalid_scope");
        }

        // 创建授权信息
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
            .principalName(userAuthentication.getName())
            .authorizationGrantType(AuthorizationGrantTypeBuilder.PASSWORD)
            .attribute(Principal.class.getName(), userAuthentication)
            .authorizedScopes(requestedScopes)
            ;

        // 生成token
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext
            .builder()
            .registeredClient(registeredClient)
            .principal(userAuthentication)
            .authorizationServerContext(AuthorizationServerContextHolder.getContext())
            .authorizedScopes(requestedScopes)
            .authorizationGrantType(AuthorizationGrantTypeBuilder.PASSWORD)
            .authorizationGrant(authentication)
            ;

        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            AuthorizationUtils.throwError("server_error", "The token generator failed to generate the access token.", "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2");
            return null;
        }
        OAuth2AccessToken accessToken = AuthorizationUtils.accessToken(authorizationBuilder, generatedAccessToken, tokenContext);

        // 生成刷新token
        OAuth2RefreshToken currentRefreshToken = null;
        if (!registeredClient.getTokenSettings().isReuseRefreshTokens()) {

            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build());
            if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                AuthorizationUtils.throwError("server_error", "The token generator failed to generate the refresh token.", "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2");
                return null;
            }

            currentRefreshToken = (OAuth2RefreshToken) generatedRefreshToken;
            authorizationBuilder.refreshToken(currentRefreshToken);
        }

        authorizationBuilder.token(generatedAccessToken);

        // 创建授权信息
        OAuth2Authorization authorization = authorizationBuilder.build();
        this.authorizationService.save(authorization);

        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, currentRefreshToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
