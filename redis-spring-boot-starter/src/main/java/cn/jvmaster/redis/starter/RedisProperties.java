package cn.jvmaster.redis.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redis配置属性
 * @author AI
 * @date 2024/11/25 16:21
 * @version 1.0
**/
@ConfigurationProperties("spring.redis")
public class RedisProperties {

    /**
     * 缓存key统一前缀
     */
    private String prefix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
