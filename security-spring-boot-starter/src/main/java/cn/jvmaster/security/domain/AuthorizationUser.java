package cn.jvmaster.security.domain;

import cn.jvmaster.core.exception.SystemException;
import cn.jvmaster.core.util.DateUtils;
import java.util.Collection;
import java.util.Date;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户信息
 * @author AI
 * @date 2024/3/28 14:37
 * @version 1.0
**/
public record AuthorizationUser(Object id,
                                String username,
                                String password,
                                Date passwordExpireTime,
                                Collection<? extends GrantedAuthority> authorities) implements UserDetails {

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
