package cn.springhub.redis.generator.processor;

import cn.springhub.base.util.RandomUtils;
import cn.springhub.redis.annotation.Cache;
import cn.springhub.redis.generator.CacheProcessor;
import cn.springhub.redis.service.StringRedisOperationService;
import java.time.Duration;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 默认缓存处理器
 * 以key-value的形式记录缓存
 *
 * @author AI
 * @date 2024/12/16 16:04
 * @version 1.0
**/
public class DefaultCacheProcessor implements CacheProcessor {

    private final StringRedisOperationService stringRedisOperationService;

    public DefaultCacheProcessor(StringRedisOperationService stringRedisOperationService) {
        this.stringRedisOperationService = stringRedisOperationService;
    }

    @Override
    public boolean support(Object target, MethodSignature method, Object[] args) {
        return true;
    }

    @Override
    public Object get(String cacheName, Cache cache) {
        return stringRedisOperationService.get(cacheName);
    }

    @Override
    public void save(String cacheName, Object value, Cache cache) {
        // 加个时间，防止缓存雪崩
        stringRedisOperationService.setEx(cacheName, value, Duration.ofSeconds(cache.expire() + RandomUtils.random(100L)));
    }
}
