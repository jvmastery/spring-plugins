package cn.jvmaster.security.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * 授权认证工具类
 * @author AI
 * @date 2025/4/15 10:18
 * @version 1.0
**/
public class OAuth2AuthenticationUtils {

    /**
     * 获取表单数据
     * @param request   request
     * @return MultiValueMap
     */
    public static MultiValueMap<String, String> getFormParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameterMap.forEach((key, values) -> {
            String queryString = StringUtils.hasText(request.getQueryString()) ? request.getQueryString() : "";
            if (!queryString.contains(key)) {
                for(String value : values) {
                    parameters.add(key, value);
                }
            }

        });
        return parameters;
    }

    /**
     * 抛出异常
     * @param code              代码
     * @param parameterName     错误字段名称
     * @param parameterSpecification    地址
     */
    public static void throwError(String code, String parameterName, String parameterSpecification) {
        OAuth2Error error = new OAuth2Error(code, "OAuth 2.0 Parameter: " + parameterName, parameterSpecification);
        throw new OAuth2AuthenticationException(error);
    }

    /**
     * 获取客户端token信息
     * @param authentication    authentication
     * @return  OAuth2ClientAuthenticationToken
     */
    public static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken)authentication.getPrincipal();
        }

        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        } else {
            throw new OAuth2AuthenticationException("invalid_client");
        }
    }

    /**
     * 创建accessToken
     * @param builder   builder
     * @param token token
     * @param accessTokenContext    accessTokenContext
     * @return  OAuth2AccessToken
     */
    public static <T extends OAuth2Token> OAuth2AccessToken accessToken(OAuth2Authorization.Builder builder, T token, OAuth2TokenContext accessTokenContext) {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(TokenType.BEARER, token.getTokenValue(), token.getIssuedAt(), token.getExpiresAt(), accessTokenContext.getAuthorizedScopes());
        OAuth2TokenFormat accessTokenFormat = accessTokenContext.getRegisteredClient().getTokenSettings().getAccessTokenFormat();
        builder.token(accessToken, (metadata) -> {
            if (token instanceof ClaimAccessor claimAccessor) {
                metadata.put(Token.CLAIMS_METADATA_NAME, claimAccessor.getClaims());
            }

            metadata.put(Token.INVALIDATED_METADATA_NAME, false);
            metadata.put(OAuth2TokenFormat.class.getName(), accessTokenFormat.getValue());
        });
        return accessToken;
    }
}
