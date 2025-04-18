package cn.jvmaster.security;

import cn.jvmaster.security.authentication.OAuth2PasswordAuthenticationConverter;
import cn.jvmaster.security.authentication.OAuth2PasswordAuthenticationProvider;
import cn.jvmaster.security.filter.CaptchaValidationFilter;
import cn.jvmaster.security.handler.RememberMeAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * spring authorization 配置
 * @author AI
 * @date 2024/1/5 15:11
 * @version 1.0
**/
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityProperties securityProperties;

    public SecurityConfig(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * OAuth2 授权端拦截器配置
     * @param http http
     * @return  SecurityFilterChain
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      AuthenticationSuccessHandler authenticationSuccessHandler,
                                                                      OpaqueTokenIntrospector opaqueTokenIntrospector,
                                                                      AuthenticationConfiguration authenticationConfiguration,
                                                                      OAuth2AuthorizationService authorizationService,
                                                                      OAuth2TokenGenerator<?> tokenGenerator) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        http
            .securityMatcher(endpointsMatcher)
            .with(authorizationServerConfigurer, authorizationServer -> {
                authorizationServer
                    .tokenEndpoint(oAuth2TokenEndpointConfigurer ->
                        {
                            try {
                                oAuth2TokenEndpointConfigurer
                                    .accessTokenResponseHandler(authenticationSuccessHandler)
                                    // 向下兼容，增加一个password模式
                                    .accessTokenRequestConverter(new OAuth2PasswordAuthenticationConverter())
                                    .authenticationProvider(new OAuth2PasswordAuthenticationProvider(authenticationConfiguration.getAuthenticationManager(), authorizationService, tokenGenerator));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    )
                    .tokenGenerator(tokenGenerator)
                    .authorizationEndpoint(item -> {
//                        item.errorResponseHandler()
                    })

            ;
            })
            .authorizeHttpRequests(authorize ->
                    authorize.anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
            // 记住我
            .rememberMe(rememberMeConfigurer ->
                    rememberMeConfigurer
                        .authenticationSuccessHandler(new RememberMeAuthenticationSuccessHandler())
            )
            // 登录失败则，重定向到登录页面
            .exceptionHandling((exceptions) -> exceptions
                    .defaultAuthenticationEntryPointFor(
                            new LoginUrlAuthenticationEntryPoint("/login"),
                            new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                    )
            )
            //
            .oauth2ResourceServer(oAuth2ResourceServerConfigurer ->
                    oAuth2ResourceServerConfigurer.opaqueToken(opaqueTokenConfigurer -> {
                        opaqueTokenConfigurer.introspector(opaqueTokenIntrospector);
                    }))
        ;

        return http.build();
    }

    /**
     * 资源接口拦截器配置
     * @param http  http
     * @param persistentTokenRepository 记住我持久化令牌存储
     * @param accessDeniedHandler      授权拒绝处理器
     * @param opaqueTokenIntrospector 不透明令牌配置
     * @return  SecurityFilterChain
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
                                                          PersistentTokenRepository persistentTokenRepository,
                                                          AccessDeniedHandler accessDeniedHandler,
                                                          OpaqueTokenIntrospector opaqueTokenIntrospector)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login", "/captcha").permitAll()
                        .anyRequest().authenticated()
                )
                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                .formLogin(httpSecurityFormLoginConfigurer ->
                    httpSecurityFormLoginConfigurer
                            .loginPage("/login")
//                            .successHandler()
//                            .failureHandler(new LoginFailureHandler())
                )
                .rememberMe(rememberMeConfigurer ->
                    rememberMeConfigurer
                        .tokenRepository(persistentTokenRepository)
                )
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .accessDeniedHandler(accessDeniedHandler)
                )
                .oauth2ResourceServer(oAuth2ResourceServerConfigurer ->
                        oAuth2ResourceServerConfigurer.opaqueToken(opaqueTokenConfigurer -> {
                            opaqueTokenConfigurer.introspector(opaqueTokenIntrospector);
                        }))
        ;

        if (securityProperties.getLogin() != null) {
            if (securityProperties.getLogin().isUseCaptcha()) {
                http.addFilterBefore(new CaptchaValidationFilter("/login"), UsernamePasswordAuthenticationFilter.class);
            }
        }

        return http.build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /**
     *  配置静态资源目录，拦截器忽略静态资源目录
     */
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/js/**",
                        "/css/**",
                        "/css/**",
                        "/images/**",
                        "/favicon.ico"
                );
    }
}
