package cn.jvmaster.redis.generator.processor;

import cn.jvmaster.core.util.RandomUtils;
import cn.jvmaster.redis.CacheContext;
import cn.jvmaster.redis.generator.CacheProcessor;
import cn.jvmaster.redis.service.SetRedisOperationService;
import java.time.Duration;
import java.util.Set;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * hash类型缓存操作
 * @author AI
 * @date 2024/12/19 15:31
 * @version 1.0
**/
public class DefaultSetCacheProcessor implements CacheProcessor {

    private final SetRedisOperationService setRedisOperationService;

    public DefaultSetCacheProcessor(SetRedisOperationService setRedisOperationService) {
        this.setRedisOperationService = setRedisOperationService;
    }

    @Override
    public boolean support(Object target, MethodSignature method, Object[] args) {
        return Set.class.isAssignableFrom(method.getReturnType());
    }

    @Override
    public Object get(String cacheName, CacheContext cache) {
        return setRedisOperationService.get(cacheName);
    }

    @Override
    public void save(String cacheName, Object value, CacheContext cache) {
        Set<?> set = (Set<?>) value;
        setRedisOperationService.set(cacheName, set, Duration.ofSeconds(cache.cache().expire() + RandomUtils.random(100L)), true);
    }
}
