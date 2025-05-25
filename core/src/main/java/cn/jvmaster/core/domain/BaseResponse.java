package cn.jvmaster.core.domain;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.constant.Constant;

/**
 * 统一返回格式
 * @author AI
 * @date 2024/2/16 14:05
 */
public class BaseResponse<T> {

    /**
     * 返回码
     */
    private final Integer code;
    /**
     * 消息
     */
    private final String msg;
    /**
     * 内容
     */
    private final T data;

    public BaseResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BaseResponse(Constant<Integer> data) {
        this(data.getCode(), data.getMessage());
    }

    public BaseResponse(Constant<Integer> code, T data) {
        this(code.getCode(), code.getMessage(), data);
    }

    public BaseResponse(Integer code, String msg) {
        this(code, msg, null);
    }

    public static <T> BaseResponse<T> success(T data) {
        return build(Code.OK, data);
    }

    public static <T> BaseResponse<T> error(Constant<Integer> code, String message) {
        return new BaseResponse<>(code.getCode(), message, null);
    }

    public static <T> BaseResponse<T> build(Constant<Integer> code, T data) {
        return new BaseResponse<>(code.getCode(), code.getMessage(), data);
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
