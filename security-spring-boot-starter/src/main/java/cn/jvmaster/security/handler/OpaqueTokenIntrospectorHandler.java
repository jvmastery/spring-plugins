package cn.jvmaster.security.handler;

import cn.jvmaster.security.domain.OAuth2IntrospectionAuthenticatedPrincipal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

/**
 * 解析不透明令牌
 * @author AI
 * @date 2024/7/15 20:02
 */
public class OpaqueTokenIntrospectorHandler implements OpaqueTokenIntrospector {

    private final OAuth2AuthorizationService oAuth2AuthorizationService;

    public OpaqueTokenIntrospectorHandler(OAuth2AuthorizationService oAuth2AuthorizationService) {
        this.oAuth2AuthorizationService = oAuth2AuthorizationService;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        OAuth2Authorization oAuth2Authorization = oAuth2AuthorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (oAuth2Authorization == null) {
            throw new BadOpaqueTokenException("授权已经过期，请重新进行登录");
        }

        return new OAuth2IntrospectionAuthenticatedPrincipal(oAuth2Authorization.getPrincipalName(),
                oAuth2Authorization.getRegisteredClientId(),
                new HashMap<>(),
                new ArrayList<>(),
                oAuth2Authorization.getAttribute(Principal.class.getName()));
    }
}
