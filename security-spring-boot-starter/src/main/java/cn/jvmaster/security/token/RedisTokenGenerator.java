package cn.jvmaster.security.token;

import cn.jvmaster.core.util.RandomUtils;
import java.time.Instant;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

/**
 * 自定义token生成器
 * @author AI
 * @date 2024/7/15 20:10
 */
public class RedisTokenGenerator implements OAuth2TokenGenerator<OAuth2Token> {
    public static final int TOKEN_LENGTH = 32;

    @Override
    public OAuth2Token generate(OAuth2TokenContext context) {
        // accessToken和refreshToken都会通过该方法进行生成,需要独立进行判断
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType()) &&
                OAuth2TokenFormat.REFERENCE.equals(context.getRegisteredClient().getTokenSettings().getAccessTokenFormat())) {
            // accessToken 生成
            return buildAccessToken(context);
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(context.getTokenType()) && !isPublicClientForAuthorizationCodeGrant(context)) {
            // refreshToken 生成
            return buildRefreshToken(context);
        }

        return null;
    }

    /**
     * 判断是不是public client
     * @param context   token上下文
     * @return  是否是公共客户端
     */
    private boolean isPublicClientForAuthorizationCodeGrant(OAuth2TokenContext context) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(context.getAuthorizationGrantType()) &&
                (context.getAuthorizationGrant().getPrincipal() instanceof OAuth2ClientAuthenticationToken clientPrincipal)) {
            return clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE);
        }
        return false;
    }

    /**
     * 构建access token {@link OAuth2AccessTokenGenerator}
     * @param context   token上下文
     */
    private OAuth2Token buildAccessToken(OAuth2TokenContext context) {
        RegisteredClient registeredClient = context.getRegisteredClient();

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(registeredClient.getTokenSettings().getAccessTokenTimeToLive());

        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, RandomUtils.randomString(TOKEN_LENGTH), issuedAt, expiresAt, context.getAuthorizedScopes());
    }

    /**
     * 构建刷新密钥 {@link OAuth2RefreshTokenGenerator}
     * @param context   token上下文
     */
    private OAuth2RefreshToken buildRefreshToken(OAuth2TokenContext context) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(context.getRegisteredClient().getTokenSettings().getRefreshTokenTimeToLive());
        return new OAuth2RefreshToken(RandomUtils.randomString(TOKEN_LENGTH), issuedAt, expiresAt);
    }
}
