package cn.jvmaster.redis.generator;

import cn.jvmaster.redis.CacheContext;
import cn.jvmaster.redis.annotation.Cache;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 缓存解析器
 * 构建缓存时，对缓存进行处理，将结果保存到缓存中去
 * @author AI
 * @date 2024/12/16 15:03
 * @version 1.0
**/
public interface CacheProcessor {

    /**
     * 判断是否支持缓存操作
     * @param target    对象属性
     * @param method    方法属性
     * @param args      方法参数值
     * @return  是否是当前的缓存操作
     */
    default boolean support(Object target, MethodSignature method, Object[] args) {
        return false;
    }

    /**
     * 从缓存中加载数据
     * @param cacheName 缓存名称
     * @param cache 缓存配置
     * @return  获取缓存结果
     */
    Object get(String cacheName, CacheContext cache);

    /**
     * 保存数据到缓存中
     * @param cacheName 缓存名称
     * @param value     保存的数据
     * @param cache     缓存配置
     */
    void save(String cacheName, Object value, CacheContext cache);
}
