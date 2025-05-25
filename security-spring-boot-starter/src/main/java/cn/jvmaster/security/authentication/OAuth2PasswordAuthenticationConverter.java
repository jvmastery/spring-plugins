package cn.jvmaster.security.authentication;

import cn.jvmaster.security.constant.AuthorizationGrantTypeBuilder;
import cn.jvmaster.security.util.AuthorizationUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * 密码授权认证转换器， 主要为了兼容以前的授权功能
 * 不推荐使用
 * @author AI
 * @date 2025/4/15 9:54
 * @version 1.0
**/
public class OAuth2PasswordAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = AuthorizationUtils.getFormParameters(request);
        String grantType = parameters.getFirst("grant_type");
        if (!AuthorizationGrantTypeBuilder.PASSWORD.getValue().equals(grantType)) {
            return null;
        } else {
            Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();

            // 用户名
            String username = parameters.getFirst("username");
            if (!StringUtils.hasText(username) || parameters.get("username").size() != 1) {
                AuthorizationUtils.throwError("invalid_request", "username", "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2");
            }

            // 密码
            String password = parameters.getFirst("password");
            if (!StringUtils.hasText(password) || parameters.get("password").size() != 1) {
                AuthorizationUtils.throwError("invalid_request", "password", "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2");
            }

            String scope = parameters.getFirst("scope");
            if (StringUtils.hasText(scope) && parameters.get("scope").size() != 1) {
                AuthorizationUtils.throwError("invalid_request", "OAuth 2.0 Parameter: scope", "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2");
            }

            Set<String> requestedScopes = null;
            if (StringUtils.hasText(scope)) {
                requestedScopes = new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
            }

            Map<String, Object> additionalParameters = new HashMap<>();
            parameters.forEach((key, value) -> {
                if (!"grant_type".equals(key) && !"scope".equals(key)) {
                    additionalParameters.put(key, value.size() == 1 ? value.getFirst() : value.toArray(new String[0]));
                }
            });

            return new OAuth2PasswordAuthenticationToken(username, password, clientPrincipal, requestedScopes, additionalParameters);
        }
    }


}
