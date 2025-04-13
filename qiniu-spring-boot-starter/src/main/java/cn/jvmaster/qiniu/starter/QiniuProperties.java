package cn.jvmaster.qiniu.starter;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 七牛云配置信息
 * @author AI
 * @date 2025/3/18 16:49
 * @version 1.0
**/
@ConfigurationProperties("spring.qiniu")
public class QiniuProperties {

    /**
     * 七牛云accessKey
     */
    private String accessKey;

    /**
     * 七牛云secretKey
     */
    private String secretKey;

    /**
     * token过期时间
     */
    private Duration tokenExpire = Duration.ofHours(1);

    /**
     * oss配置信息
     */
    private BucketInfo bucket = new BucketInfo();

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Duration getTokenExpire() {
        return tokenExpire;
    }

    public void setTokenExpire(Duration tokenExpire) {
        this.tokenExpire = tokenExpire;
    }

    public BucketInfo getBucket() {
        return bucket;
    }

    public void setBucket(BucketInfo bucket) {
        this.bucket = bucket;
    }
}
