package cn.jvmaster.redis.service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * list操作
 * @author AI
 * @date 2024/12/19 15:52
 * @version 1.0
**/
public class ListRedisOperationService extends AbstractRedisOperationService<Object> {

    public ListRedisOperationService(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    /**
     * 设置缓存
     * @param key   缓存key
     * @param value 缓存对象
     */
    public void set(String key, Collection<?> value) {
        set(key, value, null);
    }

    /**
     * 设置缓存
     * @param key   缓存key
     * @param value 缓存对象
     * @param timeout   有效时间
     */
    public void set(String key, Collection<?> value, Duration timeout) {
        set(key, value, timeout, false);
    }

    /**
     * 设置缓存
     * @param key   缓存key
     * @param value 缓存对象
     * @param timeout   有效时间
     * @param remove 是否删除旧数据
     */
    public void set(String key, Collection<?> value, Duration timeout, boolean remove) {
        if (value == null || value.isEmpty()) {
            // 没有数据
            return;
        }

        // 添加到缓存中
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            byte[] keyBytes = serializeKey(key);
            byte[][] values = value.stream().map(this::serializeValue).toArray(byte[][]::new);

            if (remove) {
                connection.keyCommands().del(keyBytes);
            }

            connection.listCommands().lPush(serializeKey(key), values);
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
    public List<?> get(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }
}
