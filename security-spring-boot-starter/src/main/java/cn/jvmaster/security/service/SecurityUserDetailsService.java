package cn.jvmaster.security.service;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.exception.SystemException;
import cn.jvmaster.redis.constant.LuaFiles;
import cn.jvmaster.redis.service.RedisOperationService;
import cn.jvmaster.security.UserCustomizer;
import cn.jvmaster.security.constant.SecurityCacheName;
import cn.jvmaster.security.domain.AuthorizationUser;
import cn.jvmaster.security.domain.UserInfo;
import cn.jvmaster.security.util.UserUtils;
import java.time.Duration;
import java.util.ArrayList;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 定义用户信息获取服务
 * @author AI
 * @date 2025/4/22 15:41
 * @version 1.0
**/
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserCustomizer<?> userCustomizer;
    private final RedisOperationService<Object> redisOperationService;

    public SecurityUserDetailsService(UserCustomizer<?> userCustomizer, RedisOperationService<Object> redisOperationService) {
        this.userCustomizer = userCustomizer;
        this.redisOperationService = redisOperationService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从缓存中进行获取
        UserInfo<?> userInfo = redisOperationService
            .stringRedisOperationService()
            .executeLuaFromFile(UserInfo.class, LuaFiles.RELATION_QUERY, 2, SecurityCacheName.CACHE_USER_NAME, SecurityCacheName.CACHE_USER_INFO, username);
        if (userInfo == null) {
            userInfo = UserUtils.findUser(username);
            if (userInfo == null) {
                throw new UsernameNotFoundException("未找到当前用户信息，请确认用户名或者密码是否正确！");
            }
        }

        return new AuthorizationUser(userInfo.getId(), username, userInfo.getPassword(), new ArrayList<>());
    }
}
