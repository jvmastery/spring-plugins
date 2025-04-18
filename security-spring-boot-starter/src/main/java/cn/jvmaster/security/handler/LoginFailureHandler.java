package cn.jvmaster.security.handler;

import cn.jvmaster.core.domain.BaseResponse;
import cn.jvmaster.security.constant.AuthorizationCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * 登录失败回调，账号错误相关错误
 * @author AI
 * @date 2024/3/23 21:05
 */
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        BaseResponse result;
        if(exception instanceof LockedException) {
            // 账号锁定，status = 1
            result = new BaseResponse(AuthorizationCode.USER_LOCKED);
        } else if(exception instanceof DisabledException || exception instanceof AccountExpiredException) {
            // 账号不可用，status != 0
            result = new BaseResponse(AuthorizationCode.USER_UNAVAILABLE);
        } else {
            result = new BaseResponse(AuthorizationCode.USER_LOGIN_ERROR.getCode(), exception.getMessage());
        }

        mappingJackson2HttpMessageConverter.write(result, null, new ServletServerHttpResponse(response));
    }
}
