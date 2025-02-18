package cn.jvmaster.redis.generator;

import cn.jvmaster.redis.constant.Constant;
import java.util.Arrays;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * 默认缓存key构造器
 * @author AI
 * @date 2024/12/13 16:08
 * @version 1.0
**/
public class DefaultCacheKeyGenerator implements CacheKeyGenerator {

    @Override
    public String generate(Object target, MethodSignature method, Object[] args) {
        Object argsKey = generateKey(args);

        return target.getClass().getName()
            + Constant.SEPARATOR
            + method.getName()
            + (argsKey == null ? "" : Constant.SEPARATOR + argsKey);
    }

    /**
     * 构建参数唯一数据
     * @param params    方法参数
     * @return          生成参数的hash
     */
    public static Object generateKey(Object[] params) {
        if (params == null || params.length == 0) {
            return null;
        }

        if (params.length == 1) {
            Object param = params[0];
            if (param != null && !param.getClass().isArray()) {
                return param;
            }
        }

        return Arrays.deepHashCode(params);
    }
}
