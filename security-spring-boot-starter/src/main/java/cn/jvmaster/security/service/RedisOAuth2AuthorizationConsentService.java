package cn.jvmaster.security.service;

import cn.jvmaster.redis.constant.CacheConstant;
import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;

/**
 * 授权同意相关处理方法
 * @author AI
 * @date 2024/3/14 21:42
 */
public class RedisOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {
    public static final String KEY = "authorization-consent" + CacheConstant.SEPARATOR;
    private final RedisTemplate<String, OAuth2AuthorizationConsent> redisTemplate;

    public RedisOAuth2AuthorizationConsentService(RedisTemplate<String, OAuth2AuthorizationConsent> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        redisTemplate.opsForValue().set(KEY + authorizationConsent.getRegisteredClientId() + CacheConstant.SEPARATOR + authorizationConsent.getPrincipalName(),
                authorizationConsent,
                Duration.ofDays(1L)
        );
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        redisTemplate.delete(KEY + authorizationConsent.getRegisteredClientId() + CacheConstant.SEPARATOR + authorizationConsent.getPrincipalName());
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        return redisTemplate.opsForValue().get(KEY + registeredClientId + CacheConstant.SEPARATOR + principalName);
    }
}
