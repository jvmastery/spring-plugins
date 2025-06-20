package cn.jvmaster.security.filter;

import cn.jvmaster.core.util.RegexUtils;
import cn.jvmaster.security.annotation.RequestValidator;
import cn.jvmaster.security.authentication.RequestValidatorAuthenticationToken;
import cn.jvmaster.security.constant.Permission;
import cn.jvmaster.security.customizer.OpenIpCustomizer;
import cn.jvmaster.security.util.ServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

/**
 * 增加匿名可访问过滤器
 * 针对存在注解的方法，可以不登录直接进行访问
 * @author AI
 * @date 2025/4/23 11:18
 * @version 1.0
 **/
public class RequestValidatorFilter extends OncePerRequestFilter {

    private final HandlerMapping handlerMapping;
    private final List<OpenIpCustomizer> openIpCustomizerList;

    public RequestValidatorFilter(HandlerMapping handlerMapping, List<OpenIpCustomizer> openIpCustomizerList) {
        this.handlerMapping = handlerMapping;
        this.openIpCustomizerList = openIpCustomizerList;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
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
            RequestValidator requestValidator = handlerMethod.hasMethodAnnotation(RequestValidator.class) ?
                handlerMethod.getMethodAnnotation(RequestValidator.class) :
                handlerMethod.getBeanType().getDeclaredAnnotation(RequestValidator.class);
            if (requestValidator != null) {
                // 构建自定义认证token
                assert currentAuth != null;
                RequestValidatorAuthenticationToken authenticationToken = new RequestValidatorAuthenticationToken(currentAuth.getPrincipal(),
                    currentAuth.getCredentials(),
                    currentAuth.getAuthorities(),
                    requestValidator);
                authenticationToken.setDetails(currentAuth.getDetails());

                validOpenApi(requestValidator, authenticationToken, request, handlerMethod);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }

        filterChain.doFilter(request, response);
    }

    /**
     * 验证是否是开放接口
     * @param requestValidator      注解
     * @param authenticationToken   自定义认证token
     * @param request               当前请求
     * @param handlerMethod         当前请求对应的方法
     */
    private void validOpenApi(RequestValidator requestValidator,
                            RequestValidatorAuthenticationToken authenticationToken,
                            HttpServletRequest request,
                            HandlerMethod handlerMethod) {
        if (!requestValidator.value().equals(Permission.OPEN_API)) {
            return;
        }

        // 是开放接口，判断是否在白名单内
        boolean isOpen = false;
        String[] ips = requestValidator.data();
        String currentIp = ServletUtils.getRemoteAddress(request);

        for (String ip : ips) {
            String ipPattern = ip.replace(".", "\\.").replace("*", ".*");
            if (RegexUtils.matches(currentIp, ipPattern)) {
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
            authenticationToken.setIp(currentIp);
        }
    }

}
