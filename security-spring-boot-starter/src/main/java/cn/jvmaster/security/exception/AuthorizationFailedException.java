package cn.jvmaster.security.exception;

import cn.jvmaster.core.constant.Constant;
import org.springframework.security.access.AccessDeniedException;

/**
 * 授权失败异常
 * @author AI
 * @date 2025/6/17 11:26
 * @version 1.0
**/
public class AuthorizationFailedException extends AccessDeniedException {
    private final int code;

    public AuthorizationFailedException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public AuthorizationFailedException(Constant<Integer> code, String message) {
        super(message);
        this.code = code.getCode();
    }

    public static AuthorizationFailedException of(Constant<Integer> code) {
        return new AuthorizationFailedException(code, code.getMessage());
    }

    public static AuthorizationFailedException of(Constant<Integer> code, String message) {
        return new AuthorizationFailedException(code, message == null ? code.getMessage() : message);
    }

    public int getCode() {
        return code;
    }
}
