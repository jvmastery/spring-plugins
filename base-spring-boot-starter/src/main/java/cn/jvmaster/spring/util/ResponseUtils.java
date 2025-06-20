package cn.jvmaster.spring.util;

import cn.jvmaster.core.util.AesUtils;
import cn.jvmaster.core.util.StringUtils;
import cn.jvmaster.spring.domain.RequestAesKey;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 构建返回数据工具类
 * @author AI
 * @date 2025/6/19 16:29
 * @version 1.0
**/
public class ResponseUtils {
    public static final String ENCRYPT_HEADER = "ECD:";

    /**
     * 构建返回数据
     * 判断返回数据是否需要加密
     * @param data 返回数据对象
     */
    public static Object buildResponseData(Object data) {
        if (data == null || RequestContextHolder.getRequestAttributes() == null) {
            return data;
        }

        RequestAesKey aesKey = (RequestAesKey) RequestContextHolder.getRequestAttributes().getAttribute(RequestAesKey.class.getName(), RequestAttributes.SCOPE_REQUEST);
        if (aesKey == null) {
            return data;
        }

        // 返回数据，添加标记头，表示这是加密后的数据
        return ENCRYPT_HEADER + StringUtils.toHex(AesUtils.encryptAes(StringUtils.toString(data), aesKey.key(), aesKey.iv()));
    }

}
