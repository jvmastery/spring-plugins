package cn.jvmaster.core.constant;

/**
 * 系统统一返回码
 * @author AI
 * @date 2025/4/14 14:28
 * @version 1.0
**/
public enum Code implements Constant<Integer> {
    ERROR(-1, "系统错误"),
    OK(0, "操作成功"),

    LOGIN_ERROR_CAPTCHA(1001, "验证码错误"),
    ASSERT_ERROR(1002, "数据断言异常"),

    // Token已过期或者无效
    TOKEN_FAILED_ERROR(4002, "登录已过期，请重新登录"),

    // 用户异常信息
    USER_NOT_LOGIN(10001, "用户信息获取失败，请重新登录"),
    PASSWORD_EXPIRED(10002, "用户密码已经过期"),

    // 未授权访问请求
    NOT_AUTHORIZATION_REQUEST(20000, "未授权访问请求")
    ;

    private final int code;

    private final String description;

    Code(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return description;
    }
}
