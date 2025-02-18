package cn.jvmaster.redis.serializer;

import cn.jvmaster.core.util.StringUtils;
import cn.jvmaster.redis.constant.Constant;
import java.nio.charset.StandardCharsets;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * redis key缓存处理
 * key按照string进行处理
 * @author AI
 * @date 2024/11/25 16:44
 * @version 1.0
**/
public class RedisKeySerializer implements RedisSerializer<Object> {

    /**
     * 缓存前缀
     */
    private String cacheKeyPrefix;

    public RedisKeySerializer(String cacheKeyPrefix) {
        this.cacheKeyPrefix = cacheKeyPrefix;
    }

    public RedisKeySerializer() {
    }

    @Override
    public byte[] serialize(Object key) throws SerializationException {
        if (key == null || key.equals("")) {
            return null;
        }

        // 转换成字符型
        String cacheKey = StringUtils.isEmpty(cacheKeyPrefix) ? key.toString() : cacheKeyPrefix + Constant.SEPARATOR + key;
        return cacheKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return (bytes == null ? null : new String(bytes, StandardCharsets.UTF_8));
    }
}
