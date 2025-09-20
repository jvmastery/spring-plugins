package cn.jvmaster.core.util;

import cn.jvmaster.core.constant.Constant;
import java.util.HashMap;
import java.util.Map;

/**
 * 常量工具类
 * @author AI
 * @date 2025/8/13 11:30
 * @version 1.0
**/
public class ConstantUtils {

    /**
     * 将枚举类转换为Map
     * @param clazz 枚举类
     */
    public static <T extends Constant<V>, V> Map<V, T> getConstantMap(Class<T> clazz) {
        Map<V, T> map = new HashMap<>();
        for (T constant : clazz.getEnumConstants()) {
            map.put(constant.getCode(), constant);
        }

        return map;
    }

}
