package cn.jvmaster.security.filter;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.security.authentication.RequestValidatorAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 权限认证拦截器
 * @author AI
 * @date 2025/4/24 17:29
 * @version 1.0
**/
public class AuthorityValidationFilter extends OncePerRequestFilter {
    private final AuthorizationManager<HttpServletRequest> authorizationManager;

    public AuthorityValidationFilter(AuthorizationManager<HttpServletRequest> authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth instanceof RequestValidatorAuthenticationToken authenticationToken && authenticationToken.isAccessAuthority()) {
            // 开放接口或者仅登录接口
            filterChain.doFilter(request, response);
            return;
        }

        AuthorizationResult authorizationResult = authorizationManager.authorize(() -> currentAuth, request);
        if (authorizationResult == null || !authorizationResult.isGranted()) {
            throw new AccessDeniedException(Code.NOT_AUTHORIZATION_REQUEST.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
