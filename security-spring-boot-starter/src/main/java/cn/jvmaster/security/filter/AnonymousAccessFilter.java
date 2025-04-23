package cn.jvmaster.security.filter;

import cn.jvmaster.core.util.RegexUtils;
import cn.jvmaster.security.OpenIpCustomizer;
import cn.jvmaster.security.annotation.OpenApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

/**
 * 增加匿名可访问过滤器
 * 针对存在{@link OpenApi}注解的方法，可以不登录直接进行访问
 * @author AI
 * @date 2025/4/23 11:18
 * @version 1.0
**/ 
public class AnonymousAccessFilter extends OncePerRequestFilter {

    private final HandlerMapping handlerMapping;
    private final List<OpenIpCustomizer> openIpCustomizerList;

    public AnonymousAccessFilter(HandlerMapping handlerMapping, List<OpenIpCustomizer> openIpCustomizerList) {
        this.handlerMapping = handlerMapping;
        this.openIpCustomizerList = openIpCustomizerList;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        // 如果已经登录，直接放行
        if (currentAuth != null && currentAuth.isAuthenticated()
            && !(currentAuth instanceof AnonymousAuthenticationToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 当前登录接口
        HandlerExecutionChain chain;
        try {
            chain = handlerMapping.getHandler(request);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        // 判断是否是匿名可访问接口
        if (chain != null && chain.getHandler() instanceof HandlerMethod handlerMethod) {
            OpenApi openApi = handlerMethod.hasMethodAnnotation(OpenApi.class) ?
                handlerMethod.getMethodAnnotation(OpenApi.class) :
                handlerMethod.getBeanType().getDeclaredAnnotation(OpenApi.class);
            if (openApi != null) {
                // 是开放接口，判断是否在白名单内
                boolean isOpen = false;
                String[] ips = openApi.ips();
                for (String ip : ips) {
                    String ipPattern = ip.replace(".", "\\.").replace("*", ".*");
                    if (RegexUtils.matches(ip, ipPattern)) {
                        isOpen = true;
                        break;
                    }
                }

                if (!isOpen && openIpCustomizerList != null) {
                    // 根据自定义配置来确定是否是开放接口
                    for (OpenIpCustomizer openIpCustomizer : openIpCustomizerList) {
                        if (openIpCustomizer.check(request, handlerMethod)) {
                            isOpen = true;
                            break;
                        }
                    }
                }

                if (isOpen) {
                    // 标记为已经认证
                    assert currentAuth != null;
                    Authentication anonymousAuth = UsernamePasswordAuthenticationToken.authenticated(currentAuth.getPrincipal(),
                        currentAuth.getCredentials(),
                        currentAuth.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(anonymousAuth);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
