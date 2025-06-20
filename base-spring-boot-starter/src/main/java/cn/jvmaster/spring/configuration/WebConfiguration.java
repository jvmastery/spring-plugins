package cn.jvmaster.spring.configuration;

import cn.jvmaster.spring.interceptor.DecryptParamInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web请求配置
 * @author AI
 * @date 2025/6/17 15:21
 * @version 1.0
**/
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final DecryptParamInterceptor decryptParamInterceptor;

    public WebConfiguration(DecryptParamInterceptor decryptParamInterceptor) {
        this.decryptParamInterceptor = decryptParamInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(decryptParamInterceptor).addPathPatterns("/**");
    }
}
