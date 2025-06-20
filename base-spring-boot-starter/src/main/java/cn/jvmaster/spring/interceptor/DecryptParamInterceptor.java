package cn.jvmaster.spring.interceptor;

import cn.jvmaster.spring.wrapper.DecryptHttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 对加密的参数进行解密操作
 * @author AI
 * @date 2025/6/17 15:17
 * @version 1.0
**/
@Component
public class DecryptParamInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String encryptedParam = request.getParameter("data");

        HttpServletRequest wrappedRequest = new DecryptHttpServletRequestWrapper(request, new HashMap<>());
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(wrappedRequest));

        return true;
    }
}
