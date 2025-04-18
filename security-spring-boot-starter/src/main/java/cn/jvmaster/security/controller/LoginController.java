package cn.jvmaster.security.controller;

import cn.jvmaster.core.util.RsaUtils;
import cn.jvmaster.spring.constant.SessionVariables;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 登录控制器，跳转到自定义登录页面
 * @author AI
 * @date 2024/3/14 22:43
 */
@Controller
public class LoginController {

    public static final String DEFAULT_LOGIN_PAGE_URL = "/login";

    public static final String LOGOUT_URL = DEFAULT_LOGIN_PAGE_URL + "?logout";
    public static final String ERROR_URL = DEFAULT_LOGIN_PAGE_URL + "?error";

    private HttpServletRequest request;

    /**
     *  跳转到登录页面
     */
    @GetMapping("/login")
    public String login(Model model) {
        // csrf设置
//        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//        Map<String, Object> hiddenInputs = new HashMap<>();
//        if (token != null) {
//            hiddenInputs.put(token.getParameterName(), token.getToken());
//        }
//
//        // 隐藏字段
//        model.addAttribute("hiddenInputs", hiddenInputs);

         // 退出页面
        if (matches(request, LOGOUT_URL)) {
            model.addAttribute("logoutMsg", "已退出");
        }

        // 错误回跳页面
        if (matches(request, ERROR_URL)) {
            model.addAttribute("errorMsg", getLoginErrorMessage());
        }

        // 添加rsa加密公钥
        KeyPair keyPair = (KeyPair) request.getSession().getAttribute(SessionVariables.CURRENT_KEY_PAIR);
        if (keyPair == null) {
            keyPair = RsaUtils.generate();
            request.getSession().setAttribute(SessionVariables.CURRENT_KEY_PAIR, keyPair);
        }
        model.addAttribute("publicKey", Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));

        return "login";
    }

    /**
     * 生成图形验证码
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, HttpServletRequest request) throws IOException {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");

        // 三个参数分别为宽、高、位数
        GifCaptcha captcha = new GifCaptcha(130, 48, 5);
        // 设置字体
        // 设置类型，纯数字、纯字母、字母数字混合
        captcha.setCharType(Captcha.TYPE_DEFAULT);

        // 验证码存入session
        request.getSession().setAttribute(SessionVariables.CAPTCHA_CODE, captcha.text());

        // 输出图片流
        captcha.out(response.getOutputStream());
    }

    /**
     * 退出页面
     */
    private boolean isLogoutSuccess() {
        return matches(request, LOGOUT_URL);
    }

    /**
     * 获取错误信息
     */
    private String getLoginErrorMessage() {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "Invalid credentials";
        }
        if (!(session
                .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) instanceof AuthenticationException exception)) {
            return "Invalid credentials";
        }
        if (!StringUtils.hasText(exception.getMessage())) {
            return "Invalid credentials";
        }
        return exception.getMessage();
    }

    /**
     * 判断是什么类型的页面类型
     * @param url       页面地址
     * @return          地址和当前请求是否匹配
     */
    private boolean matches(HttpServletRequest request, String url) {
        if (!"GET".equals(request.getMethod()) || url == null) {
            return false;
        }
        String uri = request.getRequestURI();
        int pathParamIndex = uri.indexOf(';');
        if (pathParamIndex > 0) {
            // strip everything after the first semi-colon
            uri = uri.substring(0, pathParamIndex);
        }
        if (request.getQueryString() != null) {
            uri += "?" + request.getQueryString();
        }
        if ("".equals(request.getContextPath())) {
            return uri.equals(url);
        }
        return uri.equals(request.getContextPath() + url);
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
