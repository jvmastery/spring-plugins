package cn.jvmaster.security.domain;

import java.util.Date;
import java.util.List;

/**
 * 定义用户信息接口
 * @author AI
 * @date 2025/4/22 15:38
 * @version 1.0
**/
public interface UserInfo<T> {

    /**
     * 用户主键
     */
    T getId();

    /**
     * 获取用户密码
     */
    String getPassword();

    /**
     * 获取密码过期时间
     */
    default Date getPasswordExpireTime() {
        return null;
    }

    /**
     * 获取用户角色
     */
    List<? extends RoleInfo> getRoleList();
}
