package cn.jvmaster.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.method.HandlerMethod;

/**
 * 标记开放接口
 * @author AI
 * @date 2025/4/23 13:57
 * @version 1.0
**/
public interface OpenIpCustomizer {

    /**
     * 验证请求是否允许访问
     */
    boolean check(HttpServletRequest request, HandlerMethod handlerMethod);
}
