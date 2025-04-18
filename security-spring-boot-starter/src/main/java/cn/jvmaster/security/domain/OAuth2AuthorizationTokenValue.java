package cn.jvmaster.security.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * OAuth2AuthorizationToken对象属性
 * @author AI
 * @date 2024/3/13 21:52
 */
public class OAuth2AuthorizationTokenValue {

    /**
     * token的值
     */
    private String value;

    /**
     * 签发时间
     */
    private Instant issuedAt;

    /**
     * 有效时间
     */
    private Instant expiresAt;

    /**
     * 元数据
     */
    private Map<String, Object> metaData;

    /**
     * token类型
     */
    private String tokenType;

    /**
     * 范围
     */
    private Set<String> scopes;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }
}
