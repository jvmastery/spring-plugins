package cn.jvmaster.security.domain;

import cn.jvmaster.security.constant.OAuth2AuthorizationTokenType;
import cn.jvmaster.spring.domain.RequestAesKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

/**
 * OAuth2Authorization对应的实体类
 * OAuth2Authorization内部很多类没有默认构造函数，反序列化会存在问题，因此这里做次转换
 * @author AI
 * @date 2024/3/13 21:38
 */
public class RedisOAuth2Authorization {

    private String id;

    /**
     * 客户端key
     */
    private String registeredClientId;

    /**
     * 对象名称
     */
    private String principalName;

    /**
     * 参数加密秘钥信息
     */
    private RequestAesKey requestAesKey;

    /**
     * 授权类型
     */
    private String authorizationGrantType;

    /**
     * 授权范围
     */
    private Set<String> authorizedScopes;

    /**
     * 参数
     */
    private Map<String, Object> attributes;

    /**
     * 状态
     */
    private String state;

    /**
     * 最大过期时间
     */
    private Instant maxExpiresAt;

    /**
     * token数据
     */
    private Map<OAuth2AuthorizationTokenType, OAuth2AuthorizationTokenValue> tokenValuesMap = new HashMap<>();

    public RedisOAuth2Authorization() {
    }

    public RedisOAuth2Authorization(OAuth2Authorization authorization) {
        this.id = authorization.getId();
        this.registeredClientId = authorization.getRegisteredClientId();
        this.principalName = authorization.getPrincipalName();
        this.authorizationGrantType = authorization.getAuthorizationGrantType().getValue();
        this.authorizedScopes = authorization.getAuthorizedScopes();
        this.attributes = authorization.getAttributes();
        this.state = OAuth2AuthorizationTokenType.STATE.getTokenValue(authorization);

        Instant maxExpiresAt = null;
        for (OAuth2AuthorizationTokenType tokenType : OAuth2AuthorizationTokenType.values()) {
            OAuth2Authorization.Token<? extends OAuth2Token> token = tokenType.getToken(authorization);
            if (token != null) {
                tokenValuesMap.put(tokenType, tokenType.getTokenValue(token));

                maxExpiresAt = getMaxExpiresAt(maxExpiresAt, token.getToken().getExpiresAt());
            }
        }

        this.maxExpiresAt = maxExpiresAt;
    }

    /**
     * 获取最小的到期时间
     * @param lastExpiresAt     最后到期时间
     * @param currentExpiresAt  当前到期时间
     */
    private Instant getMaxExpiresAt(Instant lastExpiresAt, Instant currentExpiresAt) {
        if (lastExpiresAt == null) {
            return currentExpiresAt;
        }

        if (currentExpiresAt == null) {
            return lastExpiresAt;
        }

        return lastExpiresAt.isBefore(currentExpiresAt) ? currentExpiresAt : lastExpiresAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegisteredClientId() {
        return registeredClientId;
    }

    public void setRegisteredClientId(String registeredClientId) {
        this.registeredClientId = registeredClientId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    public void setAuthorizationGrantType(String authorizationGrantType) {
        this.authorizationGrantType = authorizationGrantType;
    }

    public Set<String> getAuthorizedScopes() {
        return authorizedScopes;
    }

    public void setAuthorizedScopes(Set<String> authorizedScopes) {
        this.authorizedScopes = authorizedScopes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Instant getMaxExpiresAt() {
        return maxExpiresAt;
    }

    public void setMaxExpiresAt(Instant maxExpiresAt) {
        this.maxExpiresAt = maxExpiresAt;
    }

    public Map<OAuth2AuthorizationTokenType, OAuth2AuthorizationTokenValue> getTokenValuesMap() {
        return tokenValuesMap;
    }

    public void setTokenValuesMap(Map<OAuth2AuthorizationTokenType, OAuth2AuthorizationTokenValue> tokenValuesMap) {
        this.tokenValuesMap = tokenValuesMap;
    }

    public RequestAesKey getRequestAesKey() {
        return requestAesKey;
    }

    public void setRequestAesKey(RequestAesKey requestAesKey) {
        this.requestAesKey = requestAesKey;
    }
}
