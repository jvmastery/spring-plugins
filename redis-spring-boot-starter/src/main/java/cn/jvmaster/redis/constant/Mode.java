package cn.jvmaster.redis.constant;

/**
 * 缓存模式
 * @author AI
 * @date 2024/12/19 14:10
 * @version 1.0
**/
public enum Mode {
    /**
     * 正常存储-读取模式
     */
    NORMAL,
    /**
     * 仅做修改使用
     */
    UPDATE_ONLY
}
