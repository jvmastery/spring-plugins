package cn.jvmaster.security.domain;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

/**
 * 不透明令牌用户信息
 * @author AI
 * @date 2024/7/20 22:07
 */
public record OAuth2IntrospectionAuthenticatedPrincipal(String name,
                                                         String clientId,
                                                         Map<String, Object> attributes,
                                                         Collection<GrantedAuthority> authorities,
                                                         Principal principal) implements OAuth2AuthenticatedPrincipal {

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}