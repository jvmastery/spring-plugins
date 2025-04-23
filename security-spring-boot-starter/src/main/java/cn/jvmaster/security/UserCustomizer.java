package cn.jvmaster.security;

import cn.jvmaster.security.domain.UserInfo;

/**
 * 定义获取信息获取服务
 * @author AI
 * @date 2025/4/22 15:44
 * @version 1.0
**/
public interface UserCustomizer<T> {

    /**
     * 获取用户信息
     * @param username 用户名称
     * @return UserInfo
     */
    UserInfo<T> getUserInfo(String username);
}
