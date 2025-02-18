package cn.jvmaster.redis.generator;

import org.aspectj.lang.reflect.MethodSignature;

/**
 * 缓存key生成规则
 * 当未指定缓存key时，会根据构造规则进行生成
 * @author AI
 * @date 2024/12/13 16:04
 * @version 1.0
**/
public interface CacheKeyGenerator {

    /**
     * 返回缓存key
     * @param target 对象
     * @param method 对应的方法
     * @param args 参数值
     *
     * @return  缓存key
     */
    String generate(Object target, MethodSignature method, Object[] args);
}
