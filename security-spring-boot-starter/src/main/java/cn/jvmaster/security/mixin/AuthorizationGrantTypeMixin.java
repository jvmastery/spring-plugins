package cn.jvmaster.security.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AuthorizationGrantType类的mixin
 * 由于AuthorizationGrantType没有默认构造函数，反序列化时会出错，通过mixin来指定下
 * @author AI
 * @date 2024/3/12 21:46
 */
public class AuthorizationGrantTypeMixin {

    @JsonCreator
    public AuthorizationGrantTypeMixin(@JsonProperty("value") String value) {
    }

}
