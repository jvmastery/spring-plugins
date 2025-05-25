package cn.jvmaster.security.customizer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;

/**
 * 提供对外配置信息
 * @author AI
 * @date 2025/4/21 16:40
 * @version 1.0
**/
public interface SecurityCustomizer {

    /**
     * 对授权服务进行配置
     */
    void customize(HttpSecurity http, OAuth2AuthorizationServerConfigurer authorizationServerConfigurer);

    /**
     * 对资源服务进行配置
     */
    void customize(HttpSecurity http);
}
