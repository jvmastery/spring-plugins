package cn.jvmaster.qiniu.starter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 七牛云配置类
 * @author AI
 * @date 2025/3/18 17:10
 * @version 1.0
**/
@Configuration
@EnableConfigurationProperties(QiniuProperties.class)
@ComponentScan(basePackages = "cn.jvmaster.qiniu.controller")
public class QiniuAutoConfiguration {

    private final QiniuProperties qiniuProperties;

    public QiniuAutoConfiguration(QiniuProperties qiniuProperties) {
        this.qiniuProperties = qiniuProperties;
    }

}
