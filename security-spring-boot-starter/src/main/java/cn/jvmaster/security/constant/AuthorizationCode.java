package cn.jvmaster.security.constant;

import cn.jvmaster.core.constant.Constant;

/**
 * 权限常量
 * @author AI
 * @date 2024/3/23 21:12
 */
public enum AuthorizationCode implements Constant<Integer> {
    USER_LOCKED(2001, "当前账号已被锁定，请联系管理员进行解锁"),
    USER_UNAVAILABLE(2002, "当前账号不可用，请联系管理员进行解决"),
    USER_LOGIN_ERROR(2009, "用户登录错误，请检查用户账号密码是否正确"),
    INVALID_CSRF(2010, "请求无效"),
    CLIENT_ERROR(2020, "客户端信息错误")
    ;

    private final int code;

    /**
     * 描述
     */
    private final String message;

    AuthorizationCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
