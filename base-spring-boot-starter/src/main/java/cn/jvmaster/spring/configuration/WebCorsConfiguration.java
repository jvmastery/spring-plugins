package cn.jvmaster.spring.configuration;

import cn.jvmaster.core.util.StringUtils;
import cn.jvmaster.spring.customizer.CorsCustomizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 跨域配置
 * @author AI
 * @date 2025/6/23 17:11
 * @version 1.0
**/
@Configuration
public class WebCorsConfiguration {

    /**
     *
     */
    @Value("${allow-origin:}")
    private String allowOrigin;

    @Bean
    public CorsConfigurationSource corsConfigurationSource(Optional<CorsCustomizer> corsCustomizer) {
        CorsConfiguration config = new CorsConfiguration();
        // 允许的来源
        List<String> allowedOriginList = new ArrayList<>();
        allowedOriginList.add("http://localhost*");
        allowedOriginList.add("http://127.0.0.1*");
        allowedOriginList.add("http://192.168.8*");
        allowedOriginList.add("http://60.169.69.39*");

        if (StringUtils.isNotEmpty(allowOrigin)) {
            allowedOriginList.addAll(Arrays.asList(allowOrigin.split(",")));
        }
        corsCustomizer.ifPresent(item -> item.execute(allowedOriginList));

        config.setAllowedOriginPatterns(allowedOriginList);
        // 允许的请求方法
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许携带的请求头
        config.setAllowedHeaders(List.of("*"));
        // 是否允许携带 cookie（重要！对认证很关键）
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 配置所有路径都应用上述 CORS 配置
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
