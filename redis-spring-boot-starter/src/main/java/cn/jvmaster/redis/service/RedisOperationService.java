package cn.jvmaster.redis.service;

import cn.jvmaster.core.exception.SystemException;
import cn.jvmaster.redis.domain.SignEntity;
import java.time.Duration;
import java.util.Calendar;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * redis操作
 * @author AI
 * @date 2024/12/11 17:20
 * @version 1.0
**/
public class RedisOperationService {

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
    private final StringRedisOperationService stringRedisOperationService;
    private final SetRedisOperationService setRedisOperationService;
    private final ListRedisOperationService listRedisOperationService;
    private final HashRedisOperationService hashRedisOperationService;

    public RedisOperationService(StringRedisOperationService stringRedisOperationService, SetRedisOperationService setRedisOperationService,
        ListRedisOperationService listRedisOperationService, HashRedisOperationService hashRedisOperationService) {
        this.stringRedisOperationService = stringRedisOperationService;
        this.setRedisOperationService = setRedisOperationService;
        this.listRedisOperationService = listRedisOperationService;
        this.hashRedisOperationService = hashRedisOperationService;
    }

    /**
     * 加锁操作
     * @param key 锁标识
     * @param callable 回调
     * @return 回函函数返回数据
     * @param <T> 回调函数参数类型
     */
    public <T> T lock(String key, Supplier<T> callable) {
        return lock(key, callable, 0);
    }

    /**
     * 加锁操作
     * @param key 锁标识
     * @param callable  回调
     * @param retryTimes 重试次数
     * @return 回函函数返回数据
     * @param <T> 回调函数参数类型
     */
    public <T> T lock(String key, Supplier<T> callable, int retryTimes) {
        return lock(key, callable, retryTimes, LOCK_EXIST_SECOND);
    }

    /**
     * 加锁操作
     * @param key 锁标识
     * @param callable  回调
     * @param retryTimes 重试次数
     * @param lockTime 加锁时间
     * @return  回函函数返回数据
     * @param <T> 回调函数参数类型
     */
    public <T> T lock(String key, Supplier<T> callable, int retryTimes, Duration lockTime) {
        return lock(key, callable, retryTimes, lockTime, false);
    }

    /**
     * 加锁操作
     * @param key 锁标识
     * @param callable  回调
     * @param retryTimes 重试次数
     * @param lockTime 加锁时间
     * @return  回函函数返回数据
     * @param <T> 回调函数参数类型
     */
    public <T> T lock(String key, Supplier<T> callable, int retryTimes, Duration lockTime, boolean throwEx) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String lockKey = key + LOCK_KEY_SUFFIX;
        boolean lockSuccess = false;

        try {
            if (!stringRedisOperationService.lock(lockKey, uuid, lockTime)) {
                // 没有成功，表示当前已经有相关的操作正在进行
                if(retryTimes > 0) {
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
     * @param userKey   用户标识
     * @param consumer  签到成功后的回调
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

    public StringRedisOperationService getStringRedisOperationService() {
        return stringRedisOperationService;
    }

    public SetRedisOperationService getSetRedisOperationService() {
        return setRedisOperationService;
    }

    public ListRedisOperationService getListRedisOperationService() {
        return listRedisOperationService;
    }

    public HashRedisOperationService getHashRedisOperationService() {
        return hashRedisOperationService;
    }
}
