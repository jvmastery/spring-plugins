package cn.jvmaster.qiniu.controller;

import cn.jvmaster.core.util.StringUtils;
import cn.jvmaster.qiniu.starter.QiniuProperties;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 构建token使用
 * @author AI
 * @date 2025/3/18 17:19
 * @version 1.0
**/
@RequestMapping("/qiniu/auth")
@RestController("qiniuTokenController")
public class TokenController {

    private final QiniuProperties qiniuProperties;

    public TokenController(QiniuProperties qiniuProperties) {
        this.qiniuProperties = qiniuProperties;
    }

    /**
     * 获取授权token
     */
    @PostMapping("token")
    public Map<String, Object> getToken() {
        Auth auth = Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getSecretKey());
        StringMap policy = new StringMap();
        policy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"fsize\":$(fsize)}");
        if (StringUtils.isNotEmpty(qiniuProperties.getBucket().getCallbackUrl())) {
            // 设置了回调
            policy.put("callbackUrl", qiniuProperties.getBucket().getCallbackUrl());
            policy.put("callbackBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize)}");
            policy.put("callbackBodyType", "application/json");
        }

        // 获取授权token
        String token = auth.uploadToken(qiniuProperties.getBucket().getName(), null, qiniuProperties.getTokenExpire().getSeconds(), policy);
        return new LinkedHashMap<>() {{
            put("token", token);
            put("expire", qiniuProperties.getTokenExpire().getSeconds());
            put("host", qiniuProperties.getBucket().getUrl());
        }};
    }

}
