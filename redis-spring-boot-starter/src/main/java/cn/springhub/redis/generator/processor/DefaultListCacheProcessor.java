package cn.springhub.redis.generator.processor;

import cn.springhub.base.util.RandomUtils;
import cn.springhub.redis.annotation.Cache;
import cn.springhub.redis.generator.CacheProcessor;
import cn.springhub.redis.service.ListRedisOperationService;
import java.time.Duration;
import java.util.List;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * hash类型缓存操作
 * @author AI
 * @date 2024/12/19 15:31
 * @version 1.0
**/
public class DefaultListCacheProcessor implements CacheProcessor {

    private final ListRedisOperationService listRedisOperationService;

    public DefaultListCacheProcessor(ListRedisOperationService listRedisOperationService) {
        this.listRedisOperationService = listRedisOperationService;
    }

    @Override
    public boolean support(Object target, MethodSignature method, Object[] args) {
        return List.class.isAssignableFrom(method.getReturnType());
    }

    @Override
    public Object get(String cacheName, Cache cache) {
        return listRedisOperationService.get(cacheName);
    }

    @Override
    public void save(String cacheName, Object value, Cache cache) {
        List<?> list = (List<?>) value;
        listRedisOperationService.set(cacheName, list, Duration.ofSeconds(cache.expire() + RandomUtils.random(100L)), true);
    }
}
