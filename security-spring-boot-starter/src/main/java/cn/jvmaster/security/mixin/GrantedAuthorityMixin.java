package cn.jvmaster.security.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GrantedAuthority的Mixin
 * @author AI
 * @date 2025/4/22 17:42
 * @version 1.0
**/
public abstract class GrantedAuthorityMixin {

    @JsonCreator
    public GrantedAuthorityMixin(@JsonProperty("authority") String role) {}
}
