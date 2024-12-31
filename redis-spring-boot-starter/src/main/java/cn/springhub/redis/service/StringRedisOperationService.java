package cn.springhub.redis.service;

import cn.springhub.redis.domain.SignEntity;
import java.time.Duration;
import java.util.List;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

/**
 * 字符串操作服务
 * @author AI
 * @date 2024/12/11 14:14
 * @version 1.0
**/
public class StringRedisOperationService extends AbstractRedisOperationService<Object> {




    public StringRedisOperationService(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    /**
     * 获取缓存
     * @param key   缓存key
     * @return  缓存内容
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置缓存
     * @param key   缓存key
     * @param value 缓存内容
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 有过期时间的缓存
     * @param key   缓存key
     * @param value 缓存内容
     * @param duration  缓存有效时间
     */
    public void setEx(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    /**
     * 分布式锁
     * @param key       锁的key值
     * @param value     锁值
     * @param expireTime    锁自动释放时间
     */
    public boolean lock(String key, Object value, Duration expireTime) {
        Boolean result = (Boolean) redisTemplate.execute((RedisCallback<Object>) connection -> connection.stringCommands().set(serializeKey(key),
            serializeValue(value),
            Expiration.seconds(expireTime.getSeconds()),
            SetOption.SET_IF_ABSENT));

        return result != null && result;
    }

    /**
     * 解锁
     * @param key   锁的key值
     * @param value 锁值
     */
    public void unlock(String key, Object value) {
        executeLuaFromFile(Boolean.class, UN_LOCK_SCRIPT, 1, key, value);
    }

    /**
     * 签到
     * @param key   缓存key
     * @param offset 偏移量，根据key来确定
     */
    @SuppressWarnings("unchecked")
    public SignEntity sign(String key, long offset) {
        List<Long> result = executeLuaFromFile(List.class, redisTemplate.getStringSerializer(), SIGN_SCRIPT, 1, key, String.valueOf(offset));

        if (result == null || result.isEmpty()) {
            return new SignEntity(false, 0L, 0L);
        }

        return new SignEntity(result.get(0) > 0, result.get(1), result.get(2));
    }

    /**
     * 取消签到
     * @param key    缓存key
     * @param offset 偏移量，根据key来确定
     */
    public void cancelSign(String key, long offset) {
        redisTemplate.opsForValue().setBit(key, offset, false);
    }

    /**
     * 判断是否已经签到
     * @param key   缓存key
     * @param offset  偏移量，根据key来确定
     * @return 是否已经签到
     */
    public boolean hasSign(String key, long offset) {
        Boolean result = redisTemplate.opsForValue().getBit(key, offset);
        return result != null && result;
    }

    /**
     * 统计全部签到天数
     * @param key   缓存key
     * @return  签到天数
     */
    public long getSignCount(String key) {
        Long result = redisTemplate.execute((RedisCallback<Long>) redis -> redis.stringCommands().bitCount(serializeKey(key)));
        return result == null ? 0 : result;
    }
}
