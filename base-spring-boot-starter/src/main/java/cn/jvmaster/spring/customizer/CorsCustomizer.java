package cn.jvmaster.spring.customizer;

import java.util.List;

/**
 * 跨域配置
 * @author AI
 * @date 2025/6/23 17:59
 * @version 1.0
**/
public interface CorsCustomizer {

    /**
     * 添加跨域配置
     * @param allowedOriginList 允许的源
     */
    void execute(List<String> allowedOriginList);

}
