package cn.jvmaster.redis.generator.processor;

import cn.jvmaster.core.util.RandomUtils;
import cn.jvmaster.redis.annotation.Cache;
import cn.jvmaster.redis.generator.CacheProcessor;
import cn.jvmaster.redis.service.HashRedisOperationService;
import java.time.Duration;
import java.util.Map;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * hash类型缓存操作
 * @author AI
 * @date 2024/12/19 15:31
 * @version 1.0
**/
public class DefaultHashCacheProcessor implements CacheProcessor {

    private final HashRedisOperationService hashRedisOperationService;

    public DefaultHashCacheProcessor(HashRedisOperationService hashRedisOperationService) {
        this.hashRedisOperationService = hashRedisOperationService;
    }

    @Override
    public boolean support(Object target, MethodSignature method, Object[] args) {
        return Map.class.isAssignableFrom(method.getReturnType());
    }

    @Override
    public Object get(String cacheName, Cache cache) {
        return hashRedisOperationService.get(cacheName);
    }

    @Override
    public void save(String cacheName, Object value, Cache cache) {
        hashRedisOperationService.set(cacheName, (Map<?, ?>) value, Duration.ofSeconds(cache.expire() + RandomUtils.random(100L)), true);
    }
}
