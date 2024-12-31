package cn.springhub.redis.service;

import cn.springhub.base.exception.SystemException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * redis操作父类
 * @author AI
 * @date 2024/12/17 17:21
 * @version 1.0
**/
public abstract class AbstractRedisOperationService<T> {
    protected static final String UN_LOCK_SCRIPT = "lua/un-lock.lua";
    protected static final String SIGN_SCRIPT = "lua/sign.lua";

    /**
     * lua脚本
     */
    private static final Map<String, byte[]> SCRIPT_SOURCE_MAP = new HashMap<>();

    protected final RedisTemplate<String, T> redisTemplate;

    public AbstractRedisOperationService(RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 序列化key
     * @param key   key字符串
     * @return      二进制数据
     */
    protected byte[] serializeKey(Object key) {
        return serialize(key, redisTemplate.getKeySerializer());
    }

    /**
     * 序列化值
     * @param value 值
     * @return  二进制数据
     */
    protected byte[] serializeValue(T value) {
        return serialize(value, redisTemplate.getValueSerializer());
    }

    /**
     * 序列化数据
     * @param key   待序列化数据
     * @param redisSerializer   序列化方式
     * @return  二进制数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected byte[] serialize(Object key, RedisSerializer redisSerializer) {
        return redisSerializer.serialize(key);
    }

    /**
     * 执行lua脚本文件
     * @param filepath  lua脚本路径，classpath下
     * @param keySize  指定参数中key的数量
     * @param args      脚本需要的参数
     * @return  脚本执行结果
     */
    public <S> S executeLuaFromFile(Class<S> tClass, String filepath, int keySize, Object... args) {
        return executeLuaFromFile(tClass, redisTemplate.getValueSerializer(), filepath, keySize, args);
    }

    /**
     * 执行lua脚本文件
     * @param filepath lua脚本路径，classpath下
     * @param argSerializer 参数序列化方式
     * @param keySize  指定参数中key的数量
     * @param args 脚本需要的参数
     * @return 脚本执行结果
     */
    @SuppressWarnings("unchecked")
    public <S> S executeLuaFromFile(Class<S> tClass, RedisSerializer<?> argSerializer, String filepath, int keySize, Object... args) {
        // 执行脚本获取结果
        Object result = redisTemplate.execute((RedisCallback<Object>) connection -> {
            final byte[][] keysAndArgs = keysAndArgs(keySize, args, argSerializer);

            return connection.scriptingCommands().eval(getScriptSourceFromFile(filepath), ReturnType.fromJavaType(tClass), keySize, keysAndArgs);
        });

        return (S) deserializeResult(result);
    }

    /**
     * 反序列化结果
     * @param result 对结果进行反序列化
     * @return 脚本执行结果
     */
    private Object deserializeResult(Object result) {
        switch (result) {
            case null -> {
                return null;
            }

            // 反序列化
            case byte[] resultByte -> {
                return redisTemplate.getValueSerializer().deserialize(resultByte);
            }

            // 数组，依次解析
            case List<?> resultList -> {
                List<Object> results = new ArrayList<>(resultList.size());

                for (Object obj : resultList) {
                    results.add(deserializeResult(obj));
                }

                return results;
            }

            default -> {
            }
        }

        return result;
    }

    /**
     * 构建参数
     * @param keySize   key的数量
     * @param args      参数列表
     * @param argSerializer 参数序列化方式
     * @return 二进制内容
     */
    private byte[][] keysAndArgs(int keySize, Object[] args, RedisSerializer<?> argSerializer) {
        final byte[][] keysAndArgs = new byte[args.length][];

        int i = 0;
        for (Object arg : args) {
            if(arg instanceof byte[]) {
                keysAndArgs[i++] = (byte[]) arg;
                continue;
            }

            if(i < keySize) {
                // 当前为key，以key的序列化方式
                keysAndArgs[i++] = serializeKey(arg);
            } else {
                // 参数，正常序列化
                keysAndArgs[i++] = serialize(arg, argSerializer);
            }
        }

        return keysAndArgs;
    }

    /**
     * 从文件中读取lua脚本
     * @param filepath  脚本地址路径
     * @return  lua脚本内容
     */
    private byte[] getScriptSourceFromFile(String filepath) {
        if(SCRIPT_SOURCE_MAP.containsKey(filepath)) {
            return SCRIPT_SOURCE_MAP.get(filepath);
        }

        // 从resource目录下读取
        try {
            ResourceScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource(filepath));
            byte[] result = redisTemplate.getStringSerializer().serialize(scriptSource.getScriptAsString());
            SCRIPT_SOURCE_MAP.put(filepath, result);

            return result;
        } catch (Exception e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    public RedisTemplate<String, T> getRedisTemplate() {
        return redisTemplate;
    }
}
