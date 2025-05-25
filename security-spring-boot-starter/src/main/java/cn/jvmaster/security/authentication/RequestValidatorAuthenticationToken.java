package cn.jvmaster.security.authentication;

import cn.jvmaster.security.annotation.RequestValidator;
import cn.jvmaster.security.constant.Permission;
import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * 标注这是一个自定义权限的token
 * @author AI
 * @date 2025/4/24 17:36
 * @version 1.0
**/
public class RequestValidatorAuthenticationToken extends UsernamePasswordAuthenticationToken {

    /**
     * 当前授权IP
     */
    private String ip;

    /**
     * 当前验证权限注解
     */
    private final RequestValidator requestValidator;

    public RequestValidatorAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, RequestValidator requestValidator) {
        super(principal, credentials, authorities);
        this.requestValidator = requestValidator;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public RequestValidator getRequestValidator() {
        return requestValidator;
    }

    /**
     * 登录用户或者开放接口不需要认证权限
     */
    public boolean isAccessAuthority() {
        return requestValidator.value().equals(Permission.OPEN_API) || requestValidator.value().equals(Permission.LOGIN_USER);
    }
}
