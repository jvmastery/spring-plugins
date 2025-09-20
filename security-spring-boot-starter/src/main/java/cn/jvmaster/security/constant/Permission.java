package cn.jvmaster.security.constant;

/**
 * 定义权限注解
 * @author AI
 * @date 2025/4/29 9:19
 * @version 1.0
**/
public enum Permission {
    /**
     * 标注接口为开放接口，注：需认证ip
     */
    OPEN_API,
    /**
     * 标注接口为登录用户接口，只要登录了，就能访问
     */
    LOGIN_USER,
    /**
     * 标注接口为匿名用户接口，不需要登录
     */
    ANONYMOUS,
    /**
     * 其他情况
     */
    OTHER

}
