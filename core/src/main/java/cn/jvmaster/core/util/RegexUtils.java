package cn.jvmaster.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  正则工具类，正则表达式判断
 *
 * @author 艾虎
 * @since 2018年6月28日11:17:35
 */
public class RegexUtils {

    public static final Map<String, Pattern> PATTERNS = new HashMap<>();

    /**
     *  判断字符串是否匹配正则表达式
     * @param input 输入字符串
     * @param regex 正则表达式
     */
    public static boolean matches(String input, String regex) {
        if(StringUtils.isEmpty(input)) {
            return false;
        }

        Pattern p = getPattern(regex);
        Matcher m = p.matcher(input);

        return m.matches();
    }

    /**
     *  判断字符串是否包含符合正则表达式的字符
     * @param input 字符串
     * @param regex 正则表达式
     */
    public static boolean contains(String input, String regex) {
        if(StringUtils.isEmpty(input)) {
            return false;
        }

        Pattern p = getPattern(regex);
        Matcher m = p.matcher(input);

        return m.find();
    }

    /**
     * 获取表示式
     * @param regex 正则
     */
    public static Pattern getPattern(String regex) {
        if (PATTERNS.containsKey(regex)) {
            return PATTERNS.get(regex);
        }

        return Pattern.compile(regex);
    }
}
