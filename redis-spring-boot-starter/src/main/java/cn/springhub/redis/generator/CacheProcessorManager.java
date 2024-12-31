package cn.springhub.redis.generator;

import cn.springhub.base.util.StringUtils;
import cn.springhub.redis.annotation.Cache;
import cn.springhub.redis.annotation.CacheRemove;
import cn.springhub.redis.constant.Mode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

/**
 * 解析器管理器
 * @author AI
 * @date 2024/12/16 15:20
 * @version 1.0
**/
public class CacheProcessorManager {
    private static final Log logger = LogFactory.getLog(CacheProcessorManager.class);

    /**
     * 所有的解析器
     */
    private final List<CacheProcessorEntity> cacheProcessorList;

    private final RedisTemplate<String, ?> redisTemplate;

    public CacheProcessorManager(List<CacheProcessorEntity> cacheProcessorList, RedisTemplate<String, ?> redisTemplate) {
        this.cacheProcessorList = cacheProcessorList;
        this.redisTemplate = redisTemplate;
    }

    /**
     *
     * @param cacheName             缓存名称
     * @param cache                 缓存属性
     * @param joinPoint             aop属性
     * @param resultSupplier        获取接口数据使用
     * @return  缓存逻辑执行结果
     */
    public Object resolve(String cacheName,
                            Cache cache,
                            ProceedingJoinPoint joinPoint,
                            Supplier<Object> resultSupplier
    ) {
        Object target = joinPoint.getTarget();
        MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
        Object[] args = joinPoint.getArgs();

        CacheProcessor cacheProcessor = getCacheResolver(cache.resolver(), target, signature, args);
        if (cacheProcessor == null) {
            // 没有对应的解析器
            logger.debug("未找到对应的缓存解析器");
            return resultSupplier.get();
        }

        // 判断是否存在对应的缓存
        boolean cacheExist = cache.mode().equals(Mode.NORMAL) ? redisTemplate.hasKey(cacheName) : false;
        if (!cacheExist) {
            // 不存在
            Object result = resultSupplier.get();
            if (result == null) {
                return null;
            }

            cacheProcessor.save(cacheName, result, cache);
        }

        // 存在，返回对应的缓存结果
        return cacheProcessor.get(cacheName, cache);
    }

    /**
     *
     * @param cacheNames             缓存名称
     * @param cacheRemove           缓存属性
     * @param joinPoint             aop属性
     * @param removeAll 是否删除所有缓存
     */
    public void remove(List<String> cacheNames, CacheRemove cacheRemove, ProceedingJoinPoint joinPoint, boolean removeAll) {
        Object target = joinPoint.getTarget();
        MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
        Object[] args = joinPoint.getArgs();

        // 获取解析器
        CacheProcessor cacheProcessor = getCacheResolver(cacheRemove.resolver(), target, signature, args);
        if (cacheProcessor == null) {
            return;
        }

        // 根据缓存的key值，删除对应的缓存
        if (removeAll) {
            List<byte[]> deleteCacheKeys = new ArrayList<>();

            // 遍历所有的key
            for (String currentName : cacheNames) {
                try(Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions().match("*" + currentName + "*").build())) {
                    while (cursor.hasNext()) {
                        String cacheKey = cursor.next();
                        deleteCacheKeys.add(cacheKey.getBytes(StandardCharsets.UTF_8));
                    }
                }
            }

            // 删除
            redisTemplate.execute((RedisCallback<Object>) connection -> connection.keyCommands().del(deleteCacheKeys.toArray(new byte[deleteCacheKeys.size()][])));
        } else {
            redisTemplate.delete(cacheNames);
        }
    }

    private CacheProcessor getCacheResolver(String resolverName, ProceedingJoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
        Object[] args = joinPoint.getArgs();

        return getCacheResolver(resolverName, target, signature, args);
    }

    /**
     * 获取解析器
     * @param resolverName 自定义解析器名称
     * @param target 对象信息
     * @param method 对应方法信息
     * @param args 方法参数
     * @return 获取缓存解析器
     */
    private CacheProcessor getCacheResolver(String resolverName, Object target, MethodSignature method, Object[] args) {
        boolean useOptionsCacheResolver = StringUtils.isNotEmpty(resolverName);
        for (CacheProcessorEntity resolver : cacheProcessorList) {
            // 如果指定了解析器，则以指定的解析器去寻找
            if (useOptionsCacheResolver) {
                if (!resolverName.equals(resolver.beanName)) {
                    continue;
                }
            } else {
                if (!resolver.cacheProcessor.support(target, method, args)) {
                    // 非当前解析器
                    continue;
                }
            }
            return resolver.cacheProcessor;
        }

        return null;
    }

    /**
     * 构建处理器实体
     * @param beanName          容器内beanName
     * @param cacheProcessor    对应的处理器
     * @return  处理器实体
     */
    public static CacheProcessorEntity build(String beanName, CacheProcessor cacheProcessor) {
        return new CacheProcessorEntity(beanName, cacheProcessor);
    }

    /**
     * 缓存处理器实体
     *
     * @param beanName       容器内beanName
     * @param cacheProcessor 对应的处理器
     */
    public record CacheProcessorEntity(String beanName, CacheProcessor cacheProcessor) {}
}
