package cn.jvmaster.security.service;

import cn.jvmaster.redis.constant.CacheConstant;
import cn.jvmaster.security.constant.OAuth2AuthorizationTokenType;
import cn.jvmaster.security.constant.SecurityVariables;
import cn.jvmaster.security.domain.RedisOAuth2Authorization;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 于管理新的和现有的授权，将授权信息存储到redis中
 * 此处token类型：
 * - State：token.equals(authorization.getAttribute(OAuth2ParameterNames.STATE))
 * - AuthorizationCode：authorization.getToken(OAuth2AuthorizationCode.class)
 * - AccessToken： authorization.getToken(OAuth2AccessToken.class);
 * - RefreshToken： authorization.getToken(OAuth2RefreshToken.class)
 * - IdToken： authorization.getToken(OidcIdToken.class)
 * - DeviceCode：authorization.getToken(OAuth2DeviceCode.class)
 * - UserCode：authorization.getToken(OAuth2UserCode.class)
 *
 * @author AI
 * @date 2024/3/8 21:20
 */
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {
    public static final String KEY = "authorization" + CacheConstant.SEPARATOR;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RegisteredClientRepository registeredClientRepository;

    public RedisOAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate, RegisteredClientRepository registeredClientRepository) {
        this.redisTemplate = redisTemplate;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        /*
           通过id存储实际的内容，
           然后其他的token对应存储id，后续查找时通过二次查询获取
         */
        RedisOAuth2Authorization redisOAuth2Authorization = new RedisOAuth2Authorization(authorization);

        // 存储到redis中
        Duration cacheExpireTime = Duration.ofSeconds(redisOAuth2Authorization.getMaxExpiresAt() == null ? 60*30L :
                redisOAuth2Authorization.getMaxExpiresAt().getEpochSecond() - Instant.now().getEpochSecond());
        long current = Instant.now().getEpochSecond();
        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public Object execute(@NonNull RedisOperations operations) throws DataAccessException {
                /*
                  查询token时，会通过token的类型和token的值的方式来查询
                 */
                if (!redisOAuth2Authorization.getTokenValuesMap().isEmpty()) {
                    // token类型的对应
                    redisOAuth2Authorization.getTokenValuesMap().forEach((key, value) -> {
                        if (value.getExpiresAt().getEpochSecond() - current <= 0) {
                            return;
                        }

                        operations.opsForValue().set(KEY + key.name() + CacheConstant.SEPARATOR + value.getValue(),
                                redisOAuth2Authorization.getId(),
                                Duration.ofSeconds(value.getExpiresAt().getEpochSecond() - current));
                    });
                }

                /*
                  state类型也是TokenType的一种，也需要存储
                 */
                if (StringUtils.hasText(redisOAuth2Authorization.getState())) {
                    operations.opsForValue().set(KEY + OAuth2AuthorizationTokenType.STATE.name() + CacheConstant.SEPARATOR + redisOAuth2Authorization.getState(),
                            redisOAuth2Authorization.getId(),
                            cacheExpireTime);
                }

                // 具体信息
                operations.opsForValue().set(KEY + SecurityVariables.ID + CacheConstant.SEPARATOR + redisOAuth2Authorization.getId(),
                        redisOAuth2Authorization,
                        cacheExpireTime);
                return null;
            }
        });
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        List<String> tokens = new ArrayList<>();
        for (OAuth2AuthorizationTokenType tokenType : OAuth2AuthorizationTokenType.values()) {
            String token = tokenType.getTokenValue(authorization);
            if (!StringUtils.hasText(token)) {
                continue;
            }

            tokens.add(KEY + tokenType.name() + CacheConstant.SEPARATOR + token);
        }

        // id的映射
        tokens.add(KEY + SecurityVariables.ID + CacheConstant.SEPARATOR + authorization.getId());

        redisTemplate.delete(tokens);
    }

    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id 不能为空");

        return getOAuth2Authorization((RedisOAuth2Authorization) redisTemplate.opsForValue().get(KEY + SecurityVariables.ID + CacheConstant.SEPARATOR + id));
    }

    @Override
    public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        Assert.hasText(token, "token 不能为空");
        OAuth2AuthorizationTokenType oAuth2AuthorizationTokenType = OAuth2AuthorizationTokenType.getInstance(tokenType.getValue());
        Assert.isTrue(oAuth2AuthorizationTokenType != null, "token类型不支持");

        String oAuth2AuthorizationId = (String) redisTemplate.opsForValue().get(KEY + oAuth2AuthorizationTokenType.name() + CacheConstant.SEPARATOR + token);
        if (StringUtils.hasText(oAuth2AuthorizationId)) {
            return findById(oAuth2AuthorizationId);
        }

        return null;
    }

    /**
     * 转成OAuth2Authorization对象
     * @param authorization redis中存储的对象
     */
    private OAuth2Authorization getOAuth2Authorization(RedisOAuth2Authorization authorization) {
        if (authorization == null) {
            return null;
        }

        RegisteredClient registeredClient = this.registeredClientRepository.findById(authorization.getRegisteredClientId());
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + authorization.getRegisteredClientId() + "' was not found in the RegisteredClientRepository.");
        }
        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient);
        builder.id(authorization.getId())
                .principalName(authorization.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType(authorization.getAuthorizationGrantType()))
                .authorizedScopes(authorization.getAuthorizedScopes())
                .attributes((attrs) -> attrs.putAll(authorization.getAttributes()));

        if (StringUtils.hasText(authorization.getState())) {
            builder.attribute(OAuth2ParameterNames.STATE, authorization.getState());
        }

        // token
        authorization.getTokenValuesMap().forEach((key, value) -> {
            builder.token(key.getOAuth2Token(value), (metadata) -> metadata.putAll(value.getMetaData()));
        });

        return builder.build();
    }
}
