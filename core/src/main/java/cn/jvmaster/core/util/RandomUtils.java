package cn.jvmaster.core.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数工具类
 * @author AI
 * @date 2024/11/15 16:05
 * @version 1.0
**/
public class RandomUtils {
    /**
     * 用于随机选的数字
     */
    public static final String BASE_NUMBER = "0123456789";
    /**
     * 用于随机选的字符
     */
    public static final String BASE_CHAR = "abcdefghijklmnopqrstuvwxyz";
    /**
     * 用于随机选的字符和数字（小写）
     */
    public static final String BASE_CHAR_NUMBER_LOWER = BASE_CHAR + BASE_NUMBER;
    /**
     * 用于随机选的字符和数字（包括大写和小写字母）
     */
    public static final String BASE_CHAR_NUMBER = BASE_CHAR.toUpperCase() + BASE_CHAR_NUMBER_LOWER;

    private RandomUtils() {}

    /**
     * 获取随机数生成器对象
     * @return 随机数生成器对象
     */
    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    /**
     * 获取范围内的随机整数，范围：[0, bound)
     * @param bound 随机数的范围，不包含这个数
     * @return 随机整数
     */
    public static int random(final int bound) {
        if (bound <= 0) {
            return 0;
        }

        return getRandom().nextInt(bound);
    }

    /**
     * 获取范围内的随机整数，范围：[origin, bound)
     * @param origin    开始数
     * @param bound     结束数
     * @return  随机数
     */
    public static int random(final int origin, final int bound) {
        if (origin == bound) {
            return origin;
        } else if (origin > bound) {
            return random(bound, origin);
        } else {
            return getRandom().nextInt(origin, bound);
        }
    }

    /**
     * 获取范围内的随机数，范围：[0, bound)
     * @param bound 随机数的范围，不包含这个数
     * @return  随机数
     */
    public static long random(final long bound) {
        if (bound <= 0L) {
            return 0L;
        }

        return getRandom().nextLong(bound);
    }

    /**
     * 获取范围内的随机数，范围：[origin, bound)
     * @param origin    开始数据
     * @param bound     结束数据
     * @return  随机数
     */
    public static long random(final long origin, final long bound) {
        if (origin == bound) {
            return origin;
        } else if (origin > bound) {
            return random(bound, origin);
        } else {
            return getRandom().nextLong(origin, bound);
        }
    }

    /**
     * 获取范围内的随机数，范围：[0, bound)
     * @param bound 随机数的范围，不包含这个数
     * @return  随机数
     */
    public static float random(final float bound) {
        if (bound <= 0F) {
            return 0F;
        }

        return getRandom().nextFloat(bound);
    }

    /**
     * 获取范围内的随机数，范围：[origin, bound)
     * @param origin    开始数据
     * @param bound     结束数据
     * @return  随机数
     */
    public static float random(final float origin, final float bound) {
        if (origin == bound) {
            return origin;
        } else if (origin > bound) {
            return random(bound, origin);
        } else {
            return getRandom().nextFloat(origin, bound);
        }
    }

    /**
     * 获取范围内的随机数，范围：[0, bound)
     * @param bound 随机数的范围，不包含这个数
     * @return  随机数
     */
    public static double random(final double bound) {
        if (bound <= 0D) {
            return 0D;
        }

        return getRandom().nextDouble(bound);
    }

    /**
     * 获取范围内的随机数，范围：[origin, bound)
     * @param origin    开始数据
     * @param bound     结束数据
     * @return  随机数
     */
    public static double random(final double origin, final double bound) {
        if (origin == bound) {
            return origin;
        } else if (origin > bound) {
            return random(bound, origin);
        } else {
            return getRandom().nextDouble(origin, bound);
        }
    }

    /**
     * 获得一个随机的字符串（包含英文和数字）
     * @param length    数据长度
     * @return  随机字符串
     */
    public static String randomString(int length) {
        return random(BASE_CHAR_NUMBER, length);
    }

    /**
     * 获得一个随机的字符串（英文）
     * @param length    数据长度
     * @return  随机字符串
     */
    public static String randomChar(int length) {
        return random(BASE_CHAR, length);
    }

    /**
     * 获取随机字符串（数字）
     * @param length    数据长度
     * @return  随机字符串
     */
    public static String randomNumber(int length) {
        return random(BASE_NUMBER, length);
    }

    /**
     * 随机汉字（'\u4E00'-'\u9FFF'）
     *
     * @return 随机的汉字字符
     */
    public static String randomChinese(int length) {
        final StringBuilder sb = new StringBuilder(length);
        if (length < 1) {
            length = 1;
        }

        // 随机数
        for (int i = 0; i < length; i++) {
            sb.append(randomChinese());
        }

        return sb.toString();
    }

    /**
     * 随机汉字（'\u4E00'-'\u9FFF'）
     *
     * @return 随机的汉字字符
     */
    public static char randomChinese() {
        return (char) random('\u4E00', '\u9FFF');
    }

    /**
     * 获得一个随机的字符串
     *
     * @param baseString 随机字符选取的样本
     * @param length     字符串的长度
     * @return 随机字符串
     */
    public static String random(final String baseString, int length) {
        if (StringUtils.isEmpty(baseString)) {
            return "";
        }

        if (length < 1) {
            length = 1;
        }

        final StringBuilder sb = new StringBuilder(length);
        final int baseLength = baseString.length();

        // 随机生成
        for (int i = 0; i < length; i++) {
            final int number = random(baseLength);
            sb.append(baseString.charAt(number));
        }

        return sb.toString();
    }
}
