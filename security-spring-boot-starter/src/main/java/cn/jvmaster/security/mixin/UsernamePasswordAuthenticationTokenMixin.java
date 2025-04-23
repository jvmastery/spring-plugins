package cn.jvmaster.security.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

/**
 * UsernamePasswordAuthenticationToken的mixin
 * @author AI
 * @date 2025/4/22 17:39
 * @version 1.0
**/
public abstract class UsernamePasswordAuthenticationTokenMixin {

    @JsonCreator
    public UsernamePasswordAuthenticationTokenMixin(
        @JsonProperty("principal") Object principal,
        @JsonProperty("credentials") Object credentials,
        @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities
    ) {}

    @JsonProperty("authenticated")
    boolean authenticated;

    @JsonProperty("name")
    String name;

    @JsonProperty("details")
    Object details;
}
