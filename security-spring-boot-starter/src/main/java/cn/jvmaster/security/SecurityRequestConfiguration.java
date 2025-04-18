package cn.jvmaster.security;

import cn.jvmaster.security.controller.LoginController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * security 控制层方法
 * @author AI
 * @date 2025/4/14 17:07
 * @version 1.0
**/
@Configuration
public class SecurityRequestConfiguration {

    /**
     * 登录接口
     * @return LoginController
     */
    @Bean
    public LoginController loginController() {
        return new LoginController();
    }

}
