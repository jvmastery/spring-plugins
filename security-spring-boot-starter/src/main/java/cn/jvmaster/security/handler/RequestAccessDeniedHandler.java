package cn.jvmaster.security.handler;

import cn.jvmaster.core.domain.BaseResponse;
import cn.jvmaster.security.constant.AuthorizationCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * 请求授权失败处理
 * @author AI
 * @date 2024/4/14 8:21
 */
public class RequestAccessDeniedHandler implements AccessDeniedHandler {

    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        BaseResponse<String> baseResponse = new BaseResponse<>(AuthorizationCode.INVALID_CSRF.getCode(), accessDeniedException.getMessage());
        mappingJackson2HttpMessageConverter.write(baseResponse, null, new ServletServerHttpResponse(response));
    }
}
