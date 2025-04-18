package cn.jvmaster.security.constant;

import cn.jvmaster.core.constant.Variables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.StringUtils;

/**
 * 授权类型常量
 * @author AI
 * @date 2024/1/25 21:41
 */
public class AuthorizationGrantTypeBuilder {

    private static final Map<String, AuthorizationGrantType> INSTANCE = new HashMap<>();
    public static final AuthorizationGrantType PASSWORD = new AuthorizationGrantType("password");

    static {
        // 客户端模式
        addGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);
        // 授权码模式
        addGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        // 设备授权码模式
        addGrantType(AuthorizationGrantType.DEVICE_CODE);
        // jwt模式
        addGrantType(AuthorizationGrantType.JWT_BEARER);
        // 刷新密钥模式
        addGrantType(AuthorizationGrantType.REFRESH_TOKEN);
        // 密码模式
        addGrantType(PASSWORD);
    }

    /**
     * 添加方法
     * @param grantType 授权类型
     */
    private static void addGrantType(AuthorizationGrantType grantType) {
        INSTANCE.put(grantType.getValue(), grantType);
    }

    /**
     * 获取对应的方法
     * @param grantTypes 授权类型
     */
    public static List<AuthorizationGrantType> getTypes(String grantTypes) {
        if(!StringUtils.hasLength(grantTypes)) {
            return new ArrayList<>();
        }

        return Arrays.stream(grantTypes.split(Variables.WORD_SEPARATOR))
                .map(INSTANCE::get)
                .collect(Collectors.toList());
    }
}
