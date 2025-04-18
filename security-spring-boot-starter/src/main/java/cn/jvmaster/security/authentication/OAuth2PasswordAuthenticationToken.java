package cn.jvmaster.security.authentication;

import cn.jvmaster.security.constant.AuthorizationGrantTypeBuilder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

/**
 * 密码模式秘钥
 * @author AI
 * @date 2025/4/15 10:02
 * @version 1.0
**/
public class OAuth2PasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    /**
     * 用户名
     */
    private final String username;
    /**
     * 用户密码
     */
    private final String password;
    /**
     * 授权范围
     */
    private final Set<String> scopes;

    public OAuth2PasswordAuthenticationToken(String username, String password, Authentication clientPrincipal, @Nullable Set<String> scopes, @Nullable Map<String, Object> additionalParameters) {
        super(AuthorizationGrantTypeBuilder.PASSWORD, clientPrincipal, additionalParameters);
        this.scopes = Collections.unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
        this.username = username;
        this.password = password;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
