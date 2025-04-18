package cn.jvmaster.security.service;

import cn.jvmaster.redis.constant.CacheConstant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * 记住我功能
 * @author AI
 * @date 2024/9/7 21:29
 */
public class RedisRememberMePersistentTokenRepository implements PersistentTokenRepository {
    public static final String REMEMBER_ME_TOKEN_KEY = "remember-me" + CacheConstant.SEPARATOR;
    public static final String REMEMBER_ME_NAME_KEY = "remember-me" + CacheConstant.SEPARATOR + "name" + CacheConstant.SEPARATOR;

    private final RedisTemplate<String, PersistentRememberMeToken> redisTemplate;

    public RedisRememberMePersistentTokenRepository(RedisTemplate<String, PersistentRememberMeToken> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.opsForValue().set(REMEMBER_ME_TOKEN_KEY + token.getSeries(), token, Duration.ofDays(30L));
                operations.opsForValue().set(REMEMBER_ME_NAME_KEY + token.getUsername(), token.getSeries(), Duration.ofDays(30L));

                return null;
            }
        });
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        PersistentRememberMeToken rememberMeToken = getTokenForSeries(series);
        createNewToken(new PersistentRememberMeToken(rememberMeToken.getUsername(), series, tokenValue, lastUsed));
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        return redisTemplate.opsForValue().get(REMEMBER_ME_TOKEN_KEY + seriesId);
    }

    @Override
    public void removeUserTokens(String username) {
        Object seriesId = redisTemplate.opsForValue().get(REMEMBER_ME_NAME_KEY + username);
        if (seriesId == null) {
            return;
        }

        redisTemplate.delete(new ArrayList<>() {{
            add(REMEMBER_ME_NAME_KEY + username);
            add(REMEMBER_ME_TOKEN_KEY + seriesId);
        }});
    }
}
