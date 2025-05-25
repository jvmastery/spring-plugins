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

        T result = supplier.get();
        if (result != null) {
            cache.put(key, result);
        }

        return result;
    }
}
