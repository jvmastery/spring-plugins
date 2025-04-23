package cn.jvmaster.redis.service;

import cn.jvmaster.core.util.StringUtils;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;

/**
 * list操作
 * @author AI
 * @date 2024/12/19 15:52
 * @version 1.0
**/
public class HashRedisOperationService<T> extends AbstractRedisOperationService<T> {

    public HashRedisOperationService(RedisTemplate<String, T> redisTemplate) {
        super(redisTemplate);
    }

    /**
     * 设置缓存
     * @param key   缓存key
     * @param value 缓存对象
     */
    public void set(String key, Map<?, ? extends T> value) {
        set(key, value, null);
    }

    /**
     * 设置缓存
     * @param key   缓存key
     * @param value 缓存对象
     * @param timeout   有效时间
     */
    public void set(String key, Map<?, ? extends T> value, Duration timeout) {
        set(key, value, timeout, false);
    }

    /**
     * 设置缓存
     * @param key   缓存key
     * @param value 缓存对象
     * @param timeout   有效时间
     * @param remove 是否删除旧数据
     */
    public void set(String key, Map<?, ? extends T> value, Duration timeout, boolean remove) {
        if (value == null || value.isEmpty()) {
            // 没有数据
            return;
        }

        // 添加到缓存中
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            byte[] keyBytes = serializeKey(key);

            if (remove) {
                connection.keyCommands().del(keyBytes);
            }

            // 序列化
            Map<byte[], byte[]> byteMap = new HashMap<>(value.size());
            value.forEach((k, v) -> {
               byteMap.put(serialize(k, redisTemplate.getHashKeySerializer()), serializeValue(v));
            });

            // 存入缓存
            connection.hashCommands().hMSet(keyBytes, byteMap);
            if (timeout != null && timeout.toSeconds() > 0) {
                // 设置有效期
                connection.keyCommands().expire(keyBytes, timeout.getSeconds());
            }

            return null;
        });
    }

    /**
     * 获取hash中所有数据
     * @param key   缓存值
     * @return  list数据
     */
    public Map<?, ?> get(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取hash中指定key的值
     * @param key       缓存值
     * @param hashKey   对应的key值
     * @return Optional
     */
    public Optional<T> get(String key, Object hashKey) {
        return Optional.ofNullable(getHashOperations().get(key, hashKey));
    }

    /**
     * 遍历hash中满足条件的值
     * @param key   缓存的值
     * @param predicate 条件
     * @return T
     */
    public T get(String key, Predicate<T> predicate) {
        return get(key, null, null, predicate);
    }

    /**
     * 遍历hash中满足条件的值
     * @param key   缓存的值
     * @param count 每次遍历返回的键值对数量
     * @param pattern 匹配规则
     * @param predicate 条件
     * @return T
     */
    public T get(String key, Long count, String pattern, Predicate<T> predicate) {
        ScanOptionsBuilder builder = ScanOptions.scanOptions();
        if (count != null) {
            builder.count(count);
        }
        if (StringUtils.isNotEmpty(pattern)) {
            builder.match(pattern);
        }

        // 迭代遍历
        try(Cursor<Map.Entry<Object, T>> cursor = getHashOperations().scan(key, builder.build())) {
            while (cursor.hasNext()) {
                Map.Entry<Object, T> entry = cursor.next();
                if (predicate.test(entry.getValue())) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 获取hash操作对象
     */
    public HashOperations<String, Object, T> getHashOperations() {
        return redisTemplate.opsForHash();
    }
}
