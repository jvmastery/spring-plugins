package cn.jvmaster.redis.starter;

import cn.jvmaster.core.util.StringUtils;
import cn.jvmaster.redis.annotation.Cache;
import cn.jvmaster.redis.annotation.CacheRemove;
import cn.jvmaster.redis.annotation.Caches;
import cn.jvmaster.redis.annotation.Lock;
import cn.jvmaster.redis.generator.CacheKeyGenerator;
import cn.jvmaster.redis.generator.CacheProcessorManager;
import cn.jvmaster.redis.service.RedisOperationService;
import cn.jvmaster.spring.util.ExpressionUtils;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * redis aop注解
 * @author AI
 * @date 2024/12/13 14:51
 * @version 1.0
**/
@Configuration
@Aspect
public class RedisAopConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RedisAopConfiguration.class);
    private final RedisOperationService redisOperationService;

    private final CacheKeyGenerator cacheKeyGenerator;

    private final CacheProcessorManager cacheProcessorManager;

    public RedisAopConfiguration(RedisOperationService redisOperationService, CacheKeyGenerator cacheKeyGenerator, CacheProcessorManager cacheProcessorManager) {
        this.redisOperationService = redisOperationService;
        this.cacheKeyGenerator = cacheKeyGenerator;
        this.cacheProcessorManager = cacheProcessorManager;
    }

    /**
     * 对分布式锁拦截处理
     * @param joinPoint 切面信息
     * @param lock      注解信息
     * @return  方法执行结果
     */
    @Around("@annotation(lock)")
    public Object handleLock(ProceedingJoinPoint joinPoint, Lock lock) {
        return redisOperationService.lock(getCacheName(lock.name(), joinPoint), () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }, lock.retryTimes(), Duration.ofSeconds(lock.lockExistSeconds()), true);
    }

    /**
     * 对缓存注解进行拦截，如果已经存在缓存，则从缓存中获取结果
     * @param joinPoint 切面信息
     * @param cache     缓存信息
     * @return          方法执行结果
     */
    @Around("@annotation(cache)")
    public Object handleCache(ProceedingJoinPoint joinPoint, Cache cache) {
        return cacheProcessorManager.resolve(getCacheName(cache.name(), joinPoint), cache, joinPoint, () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 对缓存注解进行拦截，一个方法上存在多个缓存注解
     * @param joinPoint 切面信息
     * @param cache     缓存信息
     * @return  方法执行结果
     */
    @Around("@annotation(cache)")
    public Object handleCache(ProceedingJoinPoint joinPoint, Caches cache) {
        AtomicReference<Object> methodExecuteResult = new AtomicReference<>();
        Object firstCacheResult = null;

        // 依次执行缓存
        for (Cache item : cache.value()) {
            Object result = cacheProcessorManager.resolve(getCacheName(item.name(), joinPoint), item, joinPoint, () -> {
                try {
                    // 保证实际方法只执行一次
                    if (methodExecuteResult.get() == null) {
                        methodExecuteResult.set(joinPoint.proceed());
                    }

                    return methodExecuteResult.get();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });

            if (firstCacheResult == null && result != null) {
                firstCacheResult = result;
            }
        }

        return firstCacheResult;
    }

    /**
     * 对缓存注解进行拦截，如果已经存在缓存，则从缓存中获取结果
     * @param joinPoint 切面信息
     * @param cache     缓存信息
     * @return          方法执行结果
     */
    @Around("@annotation(cache)")
    public Object handleCacheRemove(ProceedingJoinPoint joinPoint, CacheRemove cache) throws Throwable {
        // 如果没有指定待删除的缓存名称，则表示删除当前类下所有缓存数据
        boolean removeAll = cache.allEntries();
        List<String> cacheNames;

        if (cache.name() == null || cache.name().length == 0) {
            removeAll = true;
            cacheNames = Collections.singletonList(joinPoint.getTarget().getClass().getName());
        } else {
            cacheNames = Arrays.stream(cache.name()).map(item -> getCacheName(item, joinPoint)).toList();
        }
        Object result = joinPoint.proceed();
        cacheProcessorManager.remove(cacheNames, cache, joinPoint, removeAll);

        return result;
    }

    /**
     * 获取缓存名称
     * @param name      缓存注解定义的名称
     * @param joinPoint 切面信息
     * @return          缓存key
     */
    private String getCacheName(String name, ProceedingJoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
        Object[] args = joinPoint.getArgs();

        if (StringUtils.isEmpty(name)) {
            // 根据默认配置生成缓存key
            return cacheKeyGenerator.generate(target, signature, args);
        }

        // 计算表达式
        if (!name.contains("#")) {
            return name;
        }

        // 这里约定缓存key中不能添加#号，如果添加了#号，则表示为springel表达式
        String cacheName = ExpressionUtils.calculate(name, String.class, context -> {
            context.setVariable("target", target.getClass());
            context.setVariable("method", signature.getMethod());
            context.setVariable("params", args);

            // 根据字段名称来
            for (int i = 0; i < signature.getParameterNames().length; i++) {
                context.setVariable(signature.getParameterNames()[i], args[i]);
            }
        });
        log.debug("表达式{}解析结果: {}", name, cacheName);
        return cacheName;
    }
}
