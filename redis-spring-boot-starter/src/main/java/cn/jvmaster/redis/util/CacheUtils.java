package cn.jvmaster.redis.util;

import cn.jvmaster.core.util.StringUtils;
import cn.jvmaster.redis.generator.CacheKeyGenerator;
import cn.jvmaster.spring.util.ExpressionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存工具类
 * @author AI
 * @date 2025/5/23 10:04
 * @version 1.0
**/
public class CacheUtils {
    private static final Logger log = LoggerFactory.getLogger(CacheUtils.class);

    /**
     * 获取缓存名称
     * @param name      缓存注解定义的名称
     * @param joinPoint 切面信息
     * @return          缓存key
     */
    public static String getCacheName(String name, ProceedingJoinPoint joinPoint, CacheKeyGenerator cacheKeyGenerator) {
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
