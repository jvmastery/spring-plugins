package cn.jvmaster.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全相关配置属性
 * @author AI
 * @date 2025/4/14 14:49
 * @version 1.0
**/
@ConfigurationProperties("spring.security")
public class SecurityProperties {
    /**
     * 登录属性
     */
    private Login login = new Login();

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    /**
     * 登录配置属性
     */
    public static class Login {

        /**
         * 使用验证码
         */
        private boolean useCaptcha = true;

        public boolean isUseCaptcha() {
            return useCaptcha;
        }

        public void setUseCaptcha(boolean useCaptcha) {
            this.useCaptcha = useCaptcha;
        }
    }
}
