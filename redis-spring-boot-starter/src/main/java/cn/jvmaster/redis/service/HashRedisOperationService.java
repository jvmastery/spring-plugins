package cn.jvmaster.redis.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * list操作
 * @author AI
 * @date 2024/12/19 15:52
 * @version 1.0
**/
public class HashRedisOperationService extends AbstractRedisOperationService<Object> {

    public HashRedisOperationService(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    /**
     * 设置缓存
     * @param key   缓存key
     * @param value 缓存对象
     */
    public void set(String key, Map<?, ?> value) {
        set(key, value, null);
    }

    /**
     * 设置缓存
     * @param key   缓存key
     * @param value 缓存对象
     * @param timeout   有效时间
     */
    public void set(String key, Map<?, ?> value, Duration timeout) {
        set(key, value, timeout, false);
    }

    /**
     * 设置缓存
     * @param key   缓存key
     * @param value 缓存对象
     * @param timeout   有效时间
     * @param remove 是否删除旧数据
     */
    public void set(String key, Map<?, ?> value, Duration timeout, boolean remove) {
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
     * 获取list中所有数据
     * @param key   缓存值
     * @return  list数据
     */
    public Map<?, ?> get(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
}
