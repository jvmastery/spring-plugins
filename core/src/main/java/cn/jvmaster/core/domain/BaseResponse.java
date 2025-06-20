package cn.jvmaster.core.domain;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.constant.Constant;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 统一返回格式
 * @author AI
 * @date 2024/2/16 14:05
 */
public class BaseResponse<T> {

    /**
     * 当前时间戳
     */
    private Long timestamp;

    /**
     * 返回码
     */
    private Integer code;
    /**
     * 消息
     */
    private String msg;
    /**
     * 内容
     */
    private T data;

    /**
     * 是否加密
     */
    @JsonIgnore
    private Boolean secs;

    protected BaseResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    protected BaseResponse(Constant<Integer> data) {
        this(data.getCode(), data.getMessage());
    }

    protected BaseResponse(Constant<Integer> code, T data) {
        this(code.getCode(), code.getMessage(), data);
    }

    protected BaseResponse(Integer code, String msg) {
        this(code, msg, null);
    }

    public static <T> BaseResponse<T> success(T data) {
        return build(Code.OK, data);
    }

    public static <T> BaseResponse<T> error(Constant<Integer> code, String message) {
        return build(code.getCode(), message, null);
    }

    public static <T> BaseResponse<T> error(Constant<Integer> code, String message, T detailMessage) {
        return build(code.getCode(), message, detailMessage);
    }

    public static <T> BaseResponse<T> build(Constant<Integer> code, T data) {
        return build(code.getCode(), code.getMessage(), data);
    }

    public static <T> BaseResponse<T> build(Integer code, String msg, T data) {
        return new BaseResponse<>(code, msg, data);
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

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getSecs() {
        return secs;
    }

    public void setSecs(Boolean secs) {
        this.secs = secs;
    }
}
