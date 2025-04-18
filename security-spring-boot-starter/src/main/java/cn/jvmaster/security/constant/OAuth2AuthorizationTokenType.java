package cn.jvmaster.security.constant;

import cn.jvmaster.security.domain.OAuth2AuthorizationTokenValue;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2DeviceCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.OAuth2UserCode;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.util.StringUtils;

/**
 * 定义token类型，不同token类型对应的实体类
 * @author AI
 * @date 2024/3/10 21:19
 */
public enum OAuth2AuthorizationTokenType {

    STATE(OAuth2ParameterNames.STATE, null) {
        @Override
        public String getTokenValue(OAuth2Authorization authorization) {
            String authorizationState = authorization.getAttribute(OAuth2ParameterNames.STATE);
            if (StringUtils.hasText(authorizationState)) {
                return authorizationState;
            }

            return null;
        }

        @Override
        public OAuth2Token getOAuth2Token(OAuth2AuthorizationTokenValue tokenValue) {
            return null;
        }
    },
    AUTHORIZATION_CODE(OAuth2ParameterNames.CODE, OAuth2AuthorizationCode.class) {
        @Override
        public OAuth2Token getOAuth2Token(OAuth2AuthorizationTokenValue tokenValue) {
            return new OAuth2AuthorizationCode(tokenValue.getValue(), tokenValue.getIssuedAt(), tokenValue.getExpiresAt());
        }
    },
    ACCESS_TOKEN(OAuth2TokenType.ACCESS_TOKEN.getValue(), OAuth2AccessToken.class) {
        @Override
        public OAuth2AuthorizationTokenValue getTokenValue(OAuth2Authorization.Token<? extends OAuth2Token> token) {
            OAuth2AuthorizationTokenValue auth2AuthorizationTokenValue = super.getTokenValue(token);
            OAuth2AccessToken oAuth2AccessToken = (OAuth2AccessToken) token.getToken();
            auth2AuthorizationTokenValue.setTokenType(oAuth2AccessToken.getTokenType().getValue());
            auth2AuthorizationTokenValue.setScopes(oAuth2AccessToken.getScopes());

            return auth2AuthorizationTokenValue;
        }

        @Override
        public OAuth2Token getOAuth2Token(OAuth2AuthorizationTokenValue tokenValue) {
            OAuth2AccessToken.TokenType tokenType;
            if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(tokenValue.getTokenType())) {
                tokenType = OAuth2AccessToken.TokenType.BEARER;
            } else {
                return null;
            }

            return new OAuth2AccessToken(tokenType, tokenValue.getValue(), tokenValue.getIssuedAt(), tokenValue.getExpiresAt(), tokenValue.getScopes());
        }
    },
    ID_TOKEN(OidcParameterNames.ID_TOKEN, OidcIdToken.class) {
        @Override
        @SuppressWarnings("unchecked")
        public OAuth2Token getOAuth2Token(OAuth2AuthorizationTokenValue tokenValue) {
            return new OidcIdToken(tokenValue.getValue(),
                    tokenValue.getIssuedAt(),
                    tokenValue.getExpiresAt(),
                    (Map<String, Object>) tokenValue.getMetaData().get(OAuth2Authorization.Token.CLAIMS_METADATA_NAME)
            );
        }
    },
    REFRESH_TOKEN(OAuth2TokenType.REFRESH_TOKEN.getValue(), OAuth2RefreshToken.class) {
        @Override
        public OAuth2Token getOAuth2Token(OAuth2AuthorizationTokenValue tokenValue) {
            return new OAuth2RefreshToken(tokenValue.getValue(), tokenValue.getIssuedAt(), tokenValue.getExpiresAt());
        }
    },
    DEVICE_CODE(OAuth2ParameterNames.DEVICE_CODE, OAuth2DeviceCode.class) {
        @Override
        public OAuth2Token getOAuth2Token(OAuth2AuthorizationTokenValue tokenValue) {
            return new OAuth2DeviceCode(tokenValue.getValue(), tokenValue.getIssuedAt(), tokenValue.getExpiresAt());
        }
    },
    USER_CODE(OAuth2ParameterNames.USER_CODE, OAuth2UserCode.class) {
        @Override
        public OAuth2Token getOAuth2Token(OAuth2AuthorizationTokenValue tokenValue) {
            return new OAuth2UserCode(tokenValue.getValue(), tokenValue.getIssuedAt(), tokenValue.getExpiresAt());
        }
    }

    ;

    /**
     * 类型
     */
    private final String tokenType;

    /**
     * 对应的对象类型
     */
    private final Class<? extends OAuth2Token> oAuth2Token;

    OAuth2AuthorizationTokenType(String tokenType, Class<? extends OAuth2Token> oAuth2Token) {
        this.tokenType = tokenType;
        this.oAuth2Token = oAuth2Token;
    }

    public String getTokenType() {
        return tokenType;
    }

    /**
     * 获取
     * @param authorization   authorization
     */
    public OAuth2Authorization.Token<? extends OAuth2Token> getToken(OAuth2Authorization authorization) {
        if (oAuth2Token == null) {
            return null;
        }

        return authorization.getToken(oAuth2Token);
    }

    /**
     * 获取token value对象
     * @return  OAuth2AuthorizationTokenValue
     */
    public OAuth2AuthorizationTokenValue getTokenValue(OAuth2Authorization.Token<? extends OAuth2Token> token) {
        OAuth2AuthorizationTokenValue oAuth2AuthorizationTokenValue = new OAuth2AuthorizationTokenValue();
        oAuth2AuthorizationTokenValue.setValue(token.getToken().getTokenValue());
        oAuth2AuthorizationTokenValue.setExpiresAt(token.getToken().getExpiresAt());
        oAuth2AuthorizationTokenValue.setIssuedAt(token.getToken().getIssuedAt());
        oAuth2AuthorizationTokenValue.setMetaData(token.getMetadata());

        return oAuth2AuthorizationTokenValue;
    }

    /**
     * 获取token的值
     * @param authorization authorization
     * @return token的值
     */
    public String getTokenValue(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<? extends OAuth2Token> token = getToken(authorization);
        if (token != null) {
            return token.getToken().getTokenValue();
        }

        return null;
    }

    /**
     * 反序列化构建对象
     * @param tokenValue  tokenValue
     * @return  OAuth2Token
     */
    public abstract OAuth2Token getOAuth2Token(OAuth2AuthorizationTokenValue tokenValue);

    public static final Map<String, OAuth2AuthorizationTokenType> INSTANCE = new HashMap<>();

    static {
        for (OAuth2AuthorizationTokenType item : OAuth2AuthorizationTokenType.values()) {
            INSTANCE.put(item.getTokenType(), item);
        }
    }

    public static OAuth2AuthorizationTokenType getInstance(String type) {
        return INSTANCE.get(type);
    }
}
