package cn.jvmaster.security;

import cn.jvmaster.redis.generator.CacheKeyGenerator;
import cn.jvmaster.redis.generator.CacheProcessorManager;
import cn.jvmaster.redis.util.CacheUtils;
import cn.jvmaster.security.annotation.UserInfoCacheUpdate;
import cn.jvmaster.security.constant.SecurityCacheName;
import cn.jvmaster.security.controller.LoginController;
import java.util.Arrays;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * security 控制层方法
 * @author AI
 * @date 2025/4/14 17:07
 * @version 1.0
**/
@Configuration
@Aspect
public class SecurityRequestConfiguration {
    private final CacheProcessorManager cacheProcessorManager;
    private final CacheKeyGenerator cacheKeyGenerator;

    public SecurityRequestConfiguration(CacheProcessorManager cacheProcessorManager, CacheKeyGenerator cacheKeyGenerator) {
        this.cacheProcessorManager = cacheProcessorManager;
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    /**
     * 登录接口
     * @return LoginController
     */
    @Bean
    public LoginController loginController() {
        return new LoginController();
    }

    /**
     * 对缓存注解进行拦截，如果已经存在缓存，则从缓存中获取结果
     * @param joinPoint 切面信息
     * @param cache     缓存信息
     * @return          方法执行结果
     */
    @Around("@annotation(cache)")
    public Object handleCacheRemove(ProceedingJoinPoint joinPoint, UserInfoCacheUpdate cache) throws Throwable {
        List<String> cacheNames  = Arrays.stream(cache.ids())
            .map(item -> SecurityCacheName.CACHE_USER_INFO + CacheUtils.getCacheName(item, joinPoint, cacheKeyGenerator))
            .toList();

        Object result = joinPoint.proceed();
        cacheProcessorManager.remove(cacheNames, null, joinPoint, false);

        return result;
    }
}
