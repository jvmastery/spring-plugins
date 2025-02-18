package cn.jvmaster.core.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 对象
 * @author AI
 * @date 2024/11/25 17:27
 * @version 1.0
**/
public class ObjectUtils {

    /**
     * 判断一个对象是否为null
     *
     * @param obj 待判断对象
     * @return    是否为null
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 判断一个对象是否不为null
     *
     * @param obj   待判断对象
     * @return      是否不为null
     */
    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * 判断一个对象是否为空。对象类型：支持字符串、数组、集合
     *
     * @param obj   待判断对象
     * @return      是否为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        // 根据不同的类型进行判断
        if (obj instanceof CharSequence charSequence) {
            return StringUtils.isEmpty(charSequence);
        } else if (obj instanceof Map<?, ?> map) {
            return map.isEmpty();
        } else if (obj instanceof Collection<?> collection) {
            return collection.isEmpty();
        } else if (ArrayUtils.isArray(obj)) {
            return Array.getLength(obj) == 0;
        }

        return false;
    }

    /**
     * 比较2个基础类型变量是否相等
     *
     * @param obj1  变量1
     * @param obj2  变量2
     * @return  如果相等，返回true。任意一个为null都返回false
     */
    public static boolean equals(Object obj1, Object obj2) {
        if (obj1 instanceof Number number1 && obj2 instanceof Number numbe2) {
            return NumberUtils.equals(number1, numbe2);
        }

        return Objects.equals(obj1, obj2);
    }

}
