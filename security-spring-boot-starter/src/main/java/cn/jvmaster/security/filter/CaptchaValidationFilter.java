package cn.jvmaster.security.filter;

import cn.jvmaster.spring.constant.SessionVariables;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 验证码验证拦截器
 * @author AI
 * @date 2024/10/5 17:27
 */
public class CaptchaValidationFilter extends OncePerRequestFilter {

    public static final String SPRING_FORM_CAPTCHA_KEY = "captcha";

    private final AuthenticationFailureHandler failureHandler;

    private final PathPatternRequestMatcher requestMatcher;

    public CaptchaValidationFilter(String loginPage) {
        this.requestMatcher = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, loginPage);
        this.failureHandler = new SimpleUrlAuthenticationFailureHandler(loginPage + "?error");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!this.requestMatcher.matches(request)) {
            // 不是指定的请求，不做验证
            filterChain.doFilter(request, response);
            return;
        }

        // 从Session中获取验证码
        String captchaCode = (String) request.getSession().getAttribute(SessionVariables.CAPTCHA_CODE);
        // 从请求参数中获取用户输入的验证码
        String captchaInput = request.getParameter(SPRING_FORM_CAPTCHA_KEY);

        // 验证验证码
        if (captchaCode == null || !captchaCode.equalsIgnoreCase(captchaInput)) {
            // 验证失败，触发登录失败处理
            failureHandler.onAuthenticationFailure(request, response, new CaptchaValidationException("验证码错误"));
            return;
        }

        // 如果验证码验证通过，继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    // 自定义验证码异常
    public static class CaptchaValidationException extends AuthenticationException {
        public CaptchaValidationException(String msg) {
            super(msg);
        }
    }
}
