package cn.jvmaster.core.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 本地缓存
 * @author AI
 * @date 2025/4/25 16:40
 * @version 1.0
**/
public class LocalCache<T> {
    private final Map<String, T> cache = new ConcurrentHashMap<>();

    /**
     * 获取数据
     * @param key   对应的key值
     * @return T
     */
    public T get(String key, Supplier<T> supplier) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        return put(key, supplier.get());
    }

    /**
     * 获取数据
     * @param key  对应的key值
     * @return T
     */
    public T get(String key) {
        return cache.get(key);
    }

    /**
     * 添加缓存数据
     * @param key   key
     * @param value 数据
     */
    public T put(String key, T value) {
        if (value == null) {
            return null;
        }

        cache.put(key, value);
        return value;
    }
}
