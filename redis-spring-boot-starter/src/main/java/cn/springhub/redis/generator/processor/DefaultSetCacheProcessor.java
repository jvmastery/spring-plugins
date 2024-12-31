package cn.springhub.redis.generator.processor;

import cn.springhub.base.util.RandomUtils;
import cn.springhub.redis.annotation.Cache;
import cn.springhub.redis.generator.CacheProcessor;
import cn.springhub.redis.service.SetRedisOperationService;
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
    public Object get(String cacheName, Cache cache) {
        return setRedisOperationService.get(cacheName);
    }

    @Override
    public void save(String cacheName, Object value, Cache cache) {
        Set<?> set = (Set<?>) value;
        setRedisOperationService.set(cacheName, set, Duration.ofSeconds(cache.expire() + RandomUtils.random(100L)), true);
    }
}
