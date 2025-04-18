package cn.jvmaster.core.constant;

/**
 * code常量
 * @author AI
 * @date 2024/3/23 21:17
 */
public interface Constant<T> {

    /**
     * 返回code编码
     */
    T getCode();

    /**
     * 获取描述
     */
    default String getMessage() {
        return null;
    };
}
