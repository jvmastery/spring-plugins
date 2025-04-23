package cn.jvmaster.security.domain;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 定义用户信息接口
 * @author AI
 * @date 2025/4/22 15:38
 * @version 1.0
**/
public interface UserInfo<T> extends UserDetails {

    /**
     * 用户主键
     */
    T getId();

    /**
     * 获取用户密码
     */
    @Override
    String getPassword();

    /**
     * 权限信息
     */
    @Override
    default Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
}
