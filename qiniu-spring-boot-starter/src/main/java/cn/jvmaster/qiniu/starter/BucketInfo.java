package cn.jvmaster.qiniu.starter;

/**
 * 存储空间信息
 * @author AI
 * @date 2025/3/18 16:54
 * @version 1.0
**/
public class BucketInfo {

    /**
     * 存储空间名称
     */
    private String name;

    /**
     * 存储空间访问地址前缀
     */
    private String url;

    /**
     * 回调地址
     */
    private String callbackUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}
