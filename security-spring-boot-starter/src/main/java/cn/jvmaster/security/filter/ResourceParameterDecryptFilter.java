package cn.jvmaster.security.filter;

import cn.jvmaster.spring.domain.RequestAesKey;
import cn.jvmaster.security.util.AuthorizationUtils;
import cn.jvmaster.spring.wrapper.DecryptHttpServletRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 对授权后的请求进行参数解析
 * 这里的加密秘钥根据授权时，传的秘钥来
 * @author AI
 * @date 2025/6/18 10:32
 * @version 1.0
**/
public class ResourceParameterDecryptFilter extends AbstractParameterDecryptFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request instanceof DecryptHttpServletRequestWrapper) {
            // 已经解密过了，则不需要再进行处理
            filterChain.doFilter(request, response);
            return;
        }

        RequestAesKey requestAesKey = AuthorizationUtils.getRequestAesKey();
        if (requestAesKey == null) {
            filterChain.doFilter(request, response);
            return;
        }

        request.setAttribute(RequestAesKey.class.getName(), requestAesKey);
        buildDecryptHttpServletRequestWrapper(request, response, filterChain, requestAesKey.key(), requestAesKey.iv());
    }
}
