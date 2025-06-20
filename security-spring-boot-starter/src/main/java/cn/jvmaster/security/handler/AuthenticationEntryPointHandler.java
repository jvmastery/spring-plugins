package cn.jvmaster.security.handler;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.domain.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 定义AuthenticationException统一返回
 * @author AI
 * @date 2025/6/17 13:57
 * @version 1.0
**/
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    public static final Logger log = LoggerFactory.getLogger(AuthenticationEntryPointHandler.class);
    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error(authException.getMessage(), authException);
        BaseResponse<String> baseResponse = BaseResponse.build(authException instanceof InvalidBearerTokenException ? Code.TOKEN_FAILED_ERROR.getCode() : -1,
            authException.getMessage(),
            null);
        mappingJackson2HttpMessageConverter.write(baseResponse, null, new ServletServerHttpResponse(response));
    }
}
