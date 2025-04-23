package cn.jvmaster.security.util;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.exception.SystemException;
import cn.jvmaster.redis.service.RedisOperationService;
import cn.jvmaster.security.UserCustomizer;
import cn.jvmaster.security.constant.SecurityCacheName;
import cn.jvmaster.security.domain.AuthorizationUser;
import cn.jvmaster.security.domain.OAuth2IntrospectionAuthenticatedPrincipal;
import cn.jvmaster.security.domain.UserInfo;
import cn.jvmaster.spring.util.SpringUtils;
import java.time.Duration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 用户工作类
 * @author AI
 * @date 2025/4/23 10:04
 * @version 1.0
 **/
public class UserUtils {
    public static final Duration EXPIRE_TIME = Duration.ofDays(30);

    /**
     * 获取当前登录用户ID
     * @return  用户ID
     */
    public static Object getLoginUserId() {
        return getLoginUser().id();
    }

    /**
     * 当前登录用户信息
     * @return UserInfo
     */
    public static AuthorizationUser getLoginUser() {
        return getLoginUser(true);
    }

    /**
     * 获取当前登录用户信息
     * @param throwEx 没有获取到用户信息时，是否抛出异常
     */
    public static AuthorizationUser getLoginUser(boolean throwEx) {
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
            throwException(throwEx);
            return null;
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof OAuth2IntrospectionAuthenticatedPrincipal authenticatedPrincipal) {
            Object userPrincipal = authenticatedPrincipal.principal();
            if (userPrincipal instanceof Authentication authentication && authentication.getPrincipal() instanceof AuthorizationUser authorizationUser) {
                return authorizationUser;
            }
        }

        throwException(throwEx);
        return null;
    }

    public static UserInfo<?> getUserInfo() {
        AuthorizationUser authorizationUser = getLoginUser(false);
        if (authorizationUser == null) {
            // 没有登录用户信息
            return null;
        }

        // 根据用户ID查询对应的缓存数据
        RedisOperationService<?> redisOperationService = SpringUtils.getBean(RedisOperationService.class);
        UserInfo<?> userInfo = (UserInfo<?>) redisOperationService.stringRedisOperationService().get(SecurityCacheName.CACHE_USER_INFO + authorizationUser.id());
        if (userInfo != null) {
            return userInfo;
        }

        // 缓存已经失效，重新获取
        return findUser(authorizationUser.username());
    }

    /**
     * 从数据库中进行获取
     * @param username  用户名称
     * @return  UserInfo
     */
    @SuppressWarnings("unchecked")
    public static UserInfo<?> findUser(String username) {
        UserInfo<?> userInfo = SpringUtils.getBean(UserCustomizer.class).getUserInfo(username);
        if (userInfo == null) {
            return null;
        }

        // 这里对用户信息做一次缓存处理，将用户信息存储起来
        SpringUtils.getBean(RedisOperationService.class).redisTemplate().executePipelined(new SessionCallback<>() {
            @Override
            public Object execute(@NonNull RedisOperations operations) throws DataAccessException {
                operations.opsForValue().set(SecurityCacheName.CACHE_USER_NAME + username, userInfo.getId(), EXPIRE_TIME);
                operations.opsForValue().set(SecurityCacheName.CACHE_USER_INFO + userInfo.getId(), userInfo, EXPIRE_TIME);

                return null;
            }
        });

        return userInfo;
    }

    /**
     * 抛出用户异常信息
     * @param throwEx 是否抛出异常信息
     */
    private static void throwException(boolean throwEx) {
        if (throwEx) {
            throw new SystemException(Code.USER_NOT_LOGIN, "用户信息获取失败");
        }
    }
}
