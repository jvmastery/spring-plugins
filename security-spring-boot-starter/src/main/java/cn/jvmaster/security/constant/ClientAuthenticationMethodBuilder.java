package cn.jvmaster.security.constant;

import cn.jvmaster.core.constant.Variables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;

/**
 * 客户端认证方法
 * @author AI
 * @date 2024/1/22 21:54
 */
public class ClientAuthenticationMethodBuilder {

    private static final Map<String, ClientAuthenticationMethod> INSTANCE = new HashMap<>();

    static {
        /*
          基于Basic消息头认证
         */
        addMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        /*
          POST请求进行认证
         */
        addMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);
        /*
          基于jwt进行认证，对jwt进行客户端密码+签名算法进行签名
         */
        addMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
        /*
          基于jwt进行认证，请求方使用私钥对jwt进行签名，授权服务器使用对应的公钥进行认证
         */
        addMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
        /*
          公共客户端
         */
        addMethod(ClientAuthenticationMethod.NONE);
    }

    /**
     * 添加方法
     * @param method 认证方法
     */
    private static void addMethod(ClientAuthenticationMethod method) {
        INSTANCE.put(method.getValue(), method);
    }

    /**
     * 获取对应的方法
     * @param methods 认证方法
     */
    public static List<ClientAuthenticationMethod> getMethod(String methods) {
        if(!StringUtils.hasLength(methods)) {
            return new ArrayList<>();
        }

        return Arrays.stream(methods.split(Variables.WORD_SEPARATOR))
                .map(INSTANCE::get)
                .collect(Collectors.toList());
    }
}
