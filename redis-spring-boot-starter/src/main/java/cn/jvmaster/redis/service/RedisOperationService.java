package cn.jvmaster.redis.service;

import cn.jvmaster.core.exception.SystemException;
import cn.jvmaster.core.function.Callback;
import cn.jvmaster.redis.domain.SignEntity;
import java.time.Duration;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis操作
 *
 * @author AI
 * @version 1.0
 * @date 2024/12/11 17:20
 **/
public record RedisOperationService<T>(RedisTemplate<String, Object> redisTemplate,
                                       StringRedisOperationService<T> stringRedisOperationService,
                                       SetRedisOperationService<T> setRedisOperationService,
                                       ListRedisOperationService<T> listRedisOperationService,
                                       HashRedisOperationService<T> hashRedisOperationService) {

    /**
     * 签到前缀标识
     */
    public static final String SIGN_KEY_PREFIX = "info::sign::";
    /**
     * 锁后缀标识
     */
    public static final String LOCK_KEY_SUFFIX = "~lock";
    /**
     * 锁存在时间
     */
    public static final Duration LOCK_EXIST_SECOND = Duration.ofSeconds(30L);

    /**
     * 加锁操作
     *
     * @param key      锁标识
     * @param callable 回调
     * @return 回函函数返回数据
     */
    public Object lock(String key, Supplier<Object> callable) {
        return lock(key, callable, 0);
    }

    /**
     * 加锁操作
     *
     * @param key        锁标识
     * @param callable   回调
     * @param retryTimes 重试次数
     * @return 回函函数返回数据
     */
    public Object lock(String key, Supplier<Object> callable, int retryTimes) {
        return lock(key, callable, retryTimes, LOCK_EXIST_SECOND);
    }

    /**
     * 加锁操作
     *
     * @param key        锁标识
     * @param callable   回调
     * @param retryTimes 重试次数
     * @param lockTime   加锁时间
     * @return 回函函数返回数据
     */
    public Object lock(String key, Supplier<Object> callable, int retryTimes, Duration lockTime) {
        return lock(key, callable, retryTimes, lockTime, false);
    }

    /**
     * 加锁操作
     *
     * @param key        锁标识
     * @param callable   回调
     * @param retryTimes 重试次数
     * @param lockTime   加锁时间
     * @return 回函函数返回数据
     */
    public Object lock(String key, Supplier<Object> callable, int retryTimes, Duration lockTime, boolean throwEx) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String lockKey = key + LOCK_KEY_SUFFIX;
        boolean lockSuccess = false;

        try {
            if (!stringRedisOperationService.lock(lockKey, uuid, lockTime)) {
                // 没有成功，表示当前已经有相关的操作正在进行
                if (retryTimes > 0) {
                    // 等待1s后进行重试
                    Thread.sleep(1000);
                    return lock(key, callable, retryTimes - 1, lockTime, throwEx);
                }

                if (throwEx) {
                    throw new SystemException("已经存在正在运行的进程，请稍后重试");
                }
                return null;
            }

            lockSuccess = true;
            return callable.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (lockSuccess) {
                stringRedisOperationService.unlock(lockKey, uuid);
            }
        }
    }

    /**
     * 用户签到 - 按照月份来
     *
     * @param userKey  用户标识
     * @param consumer 签到成功后的回调
     * @return 签到相关信息
     */
    public SignEntity sign(Object userKey, Consumer<SignEntity> consumer) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);

        // 签到
        String key = SIGN_KEY_PREFIX + calendar.get(Calendar.YEAR) + (month < 10 ? "0" + month : month) + userKey;
        long offset = calendar.get(Calendar.DAY_OF_MONTH);
        SignEntity sign = stringRedisOperationService.sign(key, offset);

        // 回调处理
        if (consumer != null && sign.success()) {
            try {
                consumer.accept(sign);
            } catch (Exception e) {
                // 回调处理失败，取消签到
                stringRedisOperationService.cancelSign(key, offset);
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return sign;
    }

    /**
     * 判断是否存在缓存
     * @param key   缓存key
     * @return boolean
     */
    public boolean exist(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 从hash中获取数据，如果数据不存在，则执行回调函数，并返回回调函数返回的数据
     * @param key   缓存key
     * @param hashKey   hash的key
     * @param callback  通知函数
     * @return  对应的数据
     */
    public T get(String key, Object hashKey, Callback callback) {
        return hashRedisOperationService.get(key, hashKey).or(() -> {
            if (exist(key)) {
                return Optional.empty();
            }

            // 处理加载逻辑
            callback.execute();
            return hashRedisOperationService.get(key, hashKey);
        }).orElse(null);
    }

    /**
     * 从hash中获取数据，如果数据不存在，则执行回调函数，并返回回调函数返回的数据
     * @param key   缓存key
     * @param predicate   查询条件
     * @param callback  通知函数
     * @return  对应的数据
     */
    public T get(String key, Predicate<T> predicate, Callback callback) {
        T result = hashRedisOperationService.get(key, predicate);
        if (result != null) {
            return result;
        }

        if (exist(key)) {
            return null;
        }

        // 处理加载逻辑
        callback.execute();
        return hashRedisOperationService.get(key, predicate);
    }
}
