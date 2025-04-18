package cn.jvmaster.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

/**
 * 记住我  获取成功后回调
 * 由于RememberMeAuthenticationFilter在OAuth2AuthorizationEndpointFilter之后，导致第一次"/oauth2/authorize"请求无法被正确处理
 * 这里进行一下处理
 * @author AI
 * @date 2024/9/10 21:40
 */
public class RememberMeAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final String DEFAULT_AUTHORIZATION_ENDPOINT_URI = "/oauth2/authorize";
    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if (request.getRequestURI().contains(DEFAULT_AUTHORIZATION_ENDPOINT_URI)) {
            SavedRequest savedRequest = this.requestCache.getRequest(request, response);
            if (savedRequest == null) {
                // 进行处理
                requestCache.saveRequest(request, response);
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}
