package cn.jvmaster.security.constant;

import cn.jvmaster.redis.constant.CacheConstant;

/**
 * 安全缓存key
 * @author AI
 * @date 2025/4/22 16:30
 * @version 1.0
**/
public interface SecurityCacheName {

    String BASE_CACHE_USER_KEY = "authorization" + CacheConstant.SEPARATOR + "user" + CacheConstant.SEPARATOR;

    /**
     * 存储用户信息的key
     */
    String CACHE_USER_INFO = BASE_CACHE_USER_KEY + "info" + CacheConstant.SEPARATOR;

    /**
     * 存储用户名的key
     */
    String CACHE_USER_NAME = BASE_CACHE_USER_KEY + "name" + CacheConstant.SEPARATOR;

}
