package cn.jvmaster.security;

import cn.jvmaster.redis.service.RedisOperationService;
import cn.jvmaster.redis.starter.EnableRedis;
import cn.jvmaster.security.customizer.UserCustomizer;
import cn.jvmaster.security.encoder.AuthorizationPasswordEncoder;
import cn.jvmaster.security.handler.AccessTokenSuccessResponseHandler;
import cn.jvmaster.security.handler.LoginFailureHandler;
import cn.jvmaster.security.handler.OpaqueTokenIntrospectorHandler;
import cn.jvmaster.security.handler.RequestAccessDeniedHandler;
import cn.jvmaster.security.service.RedisOAuth2AuthorizationConsentService;
import cn.jvmaster.security.service.RedisOAuth2AuthorizationService;
import cn.jvmaster.security.service.RedisRememberMePersistentTokenRepository;
import cn.jvmaster.security.service.SecurityUserDetailsService;
import cn.jvmaster.security.token.RedisTokenGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * 安全功能配置属性
 * @author AI
 * @date 2025/4/14 11:00
 * @version 1.0
**/
@Configuration
@EnableRedis
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration {

    /**
     * 密码加密功能
     * @return  PasswordEncoder
     */
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new AuthorizationPasswordEncoder();
    }

    /**
     * 授权信息的获取
     * @param redisTemplate         缓存
     * @param registeredClientRepository    客户端信息获取
     * @return  OAuth2AuthorizationService
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RegisteredClientRepository.class)
    public OAuth2AuthorizationService oAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate, RegisteredClientRepository registeredClientRepository) {
        return new RedisOAuth2AuthorizationService(redisTemplate, registeredClientRepository);
    }

    /**
     * 设置服务确认功能
     * @return  OAuth2AuthorizationConsentService
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService(RedisTemplate<String, OAuth2AuthorizationConsent> redisTemplate) {
        return new RedisOAuth2AuthorizationConsentService(redisTemplate);
    }

    /**
     * 记住我功能的存储
     * @return PersistentTokenRepository
     */
    @Bean
    @ConditionalOnMissingBean
    public PersistentTokenRepository persistentTokenRepository(RedisTemplate<String, PersistentRememberMeToken> redisTemplate) {
        return new RedisRememberMePersistentTokenRepository(redisTemplate);
    }

    /**
     * 授权成功处理
     * @return  AuthenticationSuccessHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AccessTokenSuccessResponseHandler();
    }

    /**
     * 授权失败处理
     * @return  AuthenticationFailureHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new LoginFailureHandler();
    }

    /**
     * 使用不透明令牌
     * @param oAuth2AuthorizationService    授权服务处理
     * @return  OpaqueTokenIntrospector
     */
    @Bean
    @ConditionalOnMissingBean
    public OpaqueTokenIntrospector opaqueTokenIntrospector(OAuth2AuthorizationService oAuth2AuthorizationService) {
        return new OpaqueTokenIntrospectorHandler(oAuth2AuthorizationService);
    }

    /**
     * 令牌生成器
     * @return  OAuth2TokenGenerator
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2TokenGenerator<?> oAuth2TokenGenerator() {
        return new RedisTokenGenerator();
    }

    /**
     * 记住我获取成功后回调
     * @return  SavedRequestAwareAuthenticationSuccessHandler
     */
//    @Bean
//    @ConditionalOnMissingBean
//    public RememberMeAuthenticationSuccessHandler rememberMeAuthenticationSuccessHandler() {
//        return new RememberMeAuthenticationSuccessHandler();
//    }

    /**
     * 请求失败处理
     * @return  AccessDeniedHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public AccessDeniedHandler accessDeniedHandler() {
        return new RequestAccessDeniedHandler();
    }

    /**
     * 定义用户信息获取接口
     * @return UserDetailsService
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(UserCustomizer.class)
    public UserDetailsService userDetailsService(UserCustomizer<?> customizer, RedisOperationService<Object> redisOperationService) {
        return new SecurityUserDetailsService(customizer, redisOperationService);
    }
}
