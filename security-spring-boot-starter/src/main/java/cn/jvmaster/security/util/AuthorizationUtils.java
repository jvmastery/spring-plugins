package cn.jvmaster.security.util;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.constant.DateField;
import cn.jvmaster.core.exception.SystemException;
import cn.jvmaster.core.util.CollectionUtils;
import cn.jvmaster.core.util.DateUtils;
import cn.jvmaster.redis.service.RedisOperationService;
import cn.jvmaster.security.constant.SecurityCacheName;
import cn.jvmaster.security.customizer.UserCustomizer;
import cn.jvmaster.security.domain.AuthorizationUser;
import cn.jvmaster.security.domain.OAuth2IntrospectionAuthenticatedPrincipal;
import cn.jvmaster.spring.domain.RequestAesKey;
import cn.jvmaster.security.domain.UserInfo;
import cn.jvmaster.spring.util.SpringUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * 授权认证工具类
 * @author AI
 * @date 2025/4/15 10:18
 * @version 1.0
**/
public class AuthorizationUtils {
    public static final Duration EXPIRE_TIME = Duration.ofDays(30);

    /**
     * 获取表单数据
     * @param request   request
     * @return MultiValueMap
     */
    public static MultiValueMap<String, String> getFormParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameterMap.forEach((key, values) -> {
            String queryString = StringUtils.hasText(request.getQueryString()) ? request.getQueryString() : "";
            if (!queryString.contains(key)) {
                for(String value : values) {
                    parameters.add(key, value);
                }
            }

        });
        return parameters;
    }

    /**
     * 抛出异常
     * @param code              代码
     * @param parameterName     错误字段名称
     * @param parameterSpecification    地址
     */
    public static void throwError(String code, String parameterName, String parameterSpecification) {
        OAuth2Error error = new OAuth2Error(code, "OAuth 2.0 Parameter: " + parameterName, parameterSpecification);
        throw new OAuth2AuthenticationException(error);
    }

    /**
     * 获取客户端token信息
     * @param authentication    authentication
     * @return  OAuth2ClientAuthenticationToken
     */
    public static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken)authentication.getPrincipal();
        }

        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        } else {
            throw new OAuth2AuthenticationException("invalid_client");
        }
    }

    /**
     * 创建accessToken
     * @param builder   builder
     * @param token token
     * @param accessTokenContext    accessTokenContext
     * @return  OAuth2AccessToken
     */
    public static <T extends OAuth2Token> OAuth2AccessToken accessToken(OAuth2Authorization.Builder builder, T token, OAuth2TokenContext accessTokenContext) {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(TokenType.BEARER, token.getTokenValue(), token.getIssuedAt(), token.getExpiresAt(), accessTokenContext.getAuthorizedScopes());
        OAuth2TokenFormat accessTokenFormat = accessTokenContext.getRegisteredClient().getTokenSettings().getAccessTokenFormat();
        builder.token(accessToken, (metadata) -> {
            if (token instanceof ClaimAccessor claimAccessor) {
                metadata.put(Token.CLAIMS_METADATA_NAME, claimAccessor.getClaims());
            }

            metadata.put(Token.INVALIDATED_METADATA_NAME, false);
            metadata.put(OAuth2TokenFormat.class.getName(), accessTokenFormat.getValue());
        });
        return accessToken;
    }

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
        return getLoginUser(true, true);
    }

    /**
     * 获取当前登录用户信息
     * @param throwEx 没有获取到用户信息时，是否抛出异常
     */
    public static AuthorizationUser getLoginUser(boolean throwEx, boolean checkPasswordExpired) {
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
            throwException(throwEx);
            return null;
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof OAuth2IntrospectionAuthenticatedPrincipal authenticatedPrincipal) {
            Object userPrincipal = authenticatedPrincipal.principal();
            if (userPrincipal instanceof Authentication authentication && authentication.getPrincipal() instanceof AuthorizationUser authorizationUser) {
                if (checkPasswordExpired) {
                    // 验证密码是否过期
                    if (authorizationUser.passwordExpireTime() != null
                        && authorizationUser.passwordExpireTime().before(DateUtils.now()
                        .setField(DateField.HOUR, 23)
                        .setField(DateField.MINUTE, 59)
                        .setField(DateField.SECOND, 59))) {
                        throw new SystemException(Code.PASSWORD_EXPIRED, "密码已过期，请修改密码");
                    }
                }
                return authorizationUser;
            }
        }

        throwException(throwEx);
        return null;
    }

    /**
     * 获取当前token对应的加密秘钥
     * @return RequestAesKey
     */
    public static RequestAesKey getRequestAesKey() {
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof OAuth2IntrospectionAuthenticatedPrincipal authenticatedPrincipal) {
            return authenticatedPrincipal.requestAesKey();
        }

        return null;
    }

    /**
     * 获取当前客户端信息
     */
    public static String getClientId() {
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }

        if (SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2ClientAuthenticationToken clientAuthenticationToken &&
            clientAuthenticationToken.getRegisteredClient() != null) {
            return clientAuthenticationToken.getRegisteredClient().getId();
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof OAuth2IntrospectionAuthenticatedPrincipal authenticatedPrincipal) {
            return authenticatedPrincipal.clientId();
        }

        return null;
    }

    /**
     * 获取登录用户信息
     */
    public static UserInfo<?> getUserInfo() {
        AuthorizationUser authorizationUser = getLoginUser(false, true);
        if (authorizationUser == null) {
            // 没有登录用户信息
            return null;
        }

        // 根据用户ID查询对应的缓存数据
        RedisOperationService<?> redisOperationService = SpringUtils.getBean(RedisOperationService.class);
        UserInfo<?> userInfo = (UserInfo<?>) redisOperationService.getStringRedisOperationService().get(SecurityCacheName.CACHE_USER_INFO + authorizationUser.id());
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
    public static <T> UserInfo<T> findUser(String username) {
        UserCustomizer<T> userCustomizer = SpringUtils.getBean(UserCustomizer.class);
        UserInfo<T> userInfo = userCustomizer.getUserInfo(username);
        if (userInfo == null) {
            return null;
        }

        // 这里对用户信息做一次缓存处理，将用户信息存储起来
        SpringUtils.getBean(RedisOperationService.class).getRedisTemplate().executePipelined(new SessionCallback<>() {
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
            throw new SystemException(Code.USER_NOT_LOGIN);
        }
    }

    /**
     * 判断当前用户是否拥有某个角色
     * @param roleName 判断的角色名称
     */
    public static boolean hasRole(String roleName) {
        if (cn.jvmaster.core.util.StringUtils.isEmpty(roleName)) {
            return false;
        }

        UserInfo<?> userInfo = getUserInfo();
        if (userInfo == null || CollectionUtils.isEmpty(userInfo.getRoleList())) {
            return false;
        }

        return userInfo.getRoleList().stream().anyMatch(item -> roleName.equals(item.getName()));
    }
}
