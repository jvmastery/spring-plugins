package cn.jvmaster.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 声明自动配置类
 * @author AI
 * @date 2025/4/14 9:23
 * @version 1.0
**/
@Configuration
@ComponentScan(basePackages = "cn.jvmaster.spring")
public class SpringAutoScanConfiguration {

}
