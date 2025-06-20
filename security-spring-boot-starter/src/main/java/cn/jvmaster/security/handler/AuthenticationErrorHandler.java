package cn.jvmaster.security.handler;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.domain.BaseResponse;
import cn.jvmaster.security.constant.AuthorizationCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * 授权验证异常统一回调处理
 * @author AI
 * @date 2025/4/11 17:51
 * @version 1.0
**/
public class AuthenticationErrorHandler implements AuthenticationFailureHandler {
    public static final Logger log = LoggerFactory.getLogger(AuthenticationErrorHandler.class);
    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error(exception.getMessage(), exception);

        BaseResponse<String> baseResponse = BaseResponse.build(AuthorizationCode.CLIENT_ERROR.getCode(), exception.getMessage() == null ? "授权异常" : exception.getMessage(), null);
        if (exception.getAuthenticationRequest() instanceof OAuth2RefreshTokenAuthenticationToken) {
            // 刷新秘钥
            baseResponse.setCode(Code.TOKEN_FAILED_ERROR.getCode());
            baseResponse.setMsg(Code.TOKEN_FAILED_ERROR.getMessage());
        }

        if (exception instanceof OAuth2AuthenticationException oAuth2AuthenticationException) {
            baseResponse.setData(oAuth2AuthenticationException.getError().getDescription());
        }

        mappingJackson2HttpMessageConverter.write(baseResponse, null, new ServletServerHttpResponse(response));
    }
}
