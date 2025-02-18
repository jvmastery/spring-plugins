package cn.jvmaster.core.util;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 数字相关工具类
 *
 * @author AI
 * @date 2024/12/27 11:52
 * @version 1.0
**/
public class NumberUtils {

    /**
     * 比较2个数是否相等
     * @param a 数1
     * @param b 树2
     * @return  是否相等
     */
    public static boolean equals(Double a, Double b) {
        if (a == null || b == null) {
            return false;
        }

        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }

    /**
     * 比较2个数是否相等
     * @param a 数1
     * @param b 树2
     * @return  是否相等
     */
    public static boolean equals(Float a, Float b) {
        if (a == null || b == null) {
            return false;
        }

        return Float.floatToIntBits(a) == Float.floatToIntBits(b);
    }

    /**
     * 比较2个数是否相等
     * @param a 数1
     * @param b 树2
     * @return  是否相等
     */
    public static boolean equals(Long a, Long b) {
        if (a == null || b == null) {
            return false;
        }

        return a.longValue() == b.longValue();
    }

    /**
     * 比较2个数是否相等
     * @param a 数1
     * @param b 树2
     * @return  是否相等
     */
    public static boolean equals(Integer a, Integer b) {
        if (a == null || b == null) {
            return false;
        }

        return a.intValue() == b.intValue();
    }

    /**
     * 比较2个数是否相等
     * @param a 数1
     * @param b 树2
     * @return  是否相等
     */
    public static boolean equals(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            return false;
        }

        return a.compareTo(b) == 0;
    }

    /**
     * 比较2个数是否相等
     * @param a 数1
     * @param b 树2
     * @return  是否相等
     */
    public static boolean equals(Number a, Number b) {
        if (a instanceof BigDecimal decimal1 && b instanceof BigDecimal decimal2) {
            // BigDecimal使用compareTo方式判断，因为使用equals方法也判断小数位数，如2.0和2.00就不相等
            return equals(decimal1, decimal2);
        }

        return Objects.equals(a, b);
    }


}
