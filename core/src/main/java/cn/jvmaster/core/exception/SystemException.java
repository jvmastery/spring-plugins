package cn.jvmaster.core.exception;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.constant.Constant;

/**
 * 系统异常定义
 * @author AI
 * @date 2024/11/25 17:32
 * @version 1.0
**/
public class SystemException extends RuntimeException {

    private final int code;

    public SystemException(int code) {
        this.code = code;
    }

    public SystemException(String message) {
        this(Code.ERROR, message);
    }

    public SystemException(Constant<Integer> code) {
        this.code = code.getCode();
    }

    public SystemException(Constant<Integer> code, String message) {
        super(message);
        this.code = code.getCode();
    }

    public SystemException(Constant<Integer> code, String message, Throwable cause) {
        super(message, cause);
        this.code = code.getCode();
    }

    public SystemException(Constant<Integer> code, Throwable cause) {
        super(cause);
        this.code = code.getCode();
    }

    public SystemException(Constant<Integer> code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code.getCode();
    }

    public int getCode() {
        return code;
    }
}
