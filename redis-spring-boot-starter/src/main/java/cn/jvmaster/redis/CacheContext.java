package cn.jvmaster.redis;

import cn.jvmaster.redis.annotation.Cache;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 缓存上下文环境
 * @author AI
 * @date 2025/4/22 14:20
 * @version 1.0
**/
public record CacheContext(Cache cache,
                           Object target,
                           MethodSignature signature,
                           Object[] args) {

}
