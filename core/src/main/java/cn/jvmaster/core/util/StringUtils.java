package cn.jvmaster.core.util;

import cn.jvmaster.core.constant.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.NullNode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具类
 * @author AI
 * @date 2024/11/15 16:03
 * @version 1.0
**/
public class StringUtils {
    private StringUtils() {}

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    static {
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat(DateTimeFormat.NORMAL_DATETIME));
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 反序列化配置
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS);

        // 序列化配置
        OBJECT_MAPPER.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        /*
          JsonTypeInfo.As.PROPERTY（类型信息的嵌入方式） 作用： 指定类型信息在 JSON 中的表示形式。
            - PROPERTY：类型信息作为属性嵌入到 JSON 中（如 @class）。
            - WRAPPER_OBJECT：类型信息作为外层包装对象。
            - WRAPPER_ARRAY：类型信息作为包装数组。
            - EXTERNAL_PROPERTY：类型信息作为外部属性（适用于属性嵌套）。
            - EXISTING_PROPERTY：类型信息嵌入到已有属性中。
          OBJECT_MAPPER.DefaultTyping.NON_FINAL（类型适用范围）  指定哪些类型需要写入类型信息。
            - NON_CONCRETE_AND_ARRAYS：非具体类型（如接口、抽象类）以及数组类型需要写入类型信息。
            - OBJECT_AND_NON_CONCRETE：Object 类型和非具体类型需要写入类型信息。
            - NON_FINAL（此示例使用）：非最终类（final）需要写入类型信息。
            - EVERYTHING：所有类型都会写入类型信息。
         */
//        OBJECT_MAPPER.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, OBJECT_MAPPER.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    /**
     * 判断字符串是否为空
     * @param str   被检测字符串
     * @return 为空返回true
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否不为空
     * @param str   被检测字符串
     * @return 不为空返回true
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否以指定字符串开始
     * @param str       被检测字符串
     * @param prefix    开头字符串
     * @param ignoreCase 忽略大小写
     * @return 如果是以指定字符串开始，则返回true
     */
    public static boolean startWith(CharSequence str, CharSequence prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            // 任意一个为null，都返回false
            return false;
        }

        return str.toString().regionMatches(ignoreCase, 0, prefix.toString(), 0, prefix.length());
    }

    /**
     * 判断字符串是否以指定字符串开始
     * @param str 被检测字符串
     * @param prefix 开头字符串
     * @return 如果是以指定字符串开始，则返回true
     */
    public static boolean startWith(CharSequence str, CharSequence prefix) {
        return startWith(str, prefix, false);
    }

    /**
     * 判断字符串是否以指定字符串结束
     * @param str       被检测字符串
     * @param suffix    结束字符串
     * @param ignoreCase 忽略大小写
     * @return 如果是以指定字符串开始，则返回true
     */
    public static boolean endWith(CharSequence str, CharSequence suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            // 任意一个为null，都返回false
            return false;
        }

        final int strOffset = str.length() - suffix.length();
        return str.toString().regionMatches(ignoreCase, strOffset, suffix.toString(), 0, suffix.length());
    }

    /**
     * 判断字符串是否以指定字符串结束
     * @param str 被检测字符串
     * @param suffix 结束字符串
     * @return 如果是以指定字符串结束，则返回true
     */
    public static boolean endWith(CharSequence str, CharSequence suffix) {
        return endWith(str, suffix, false);
    }

    /**
     * 判断字符串是否包含指定字符串
     * @param str       被检测字符串
     * @param searchStr 被查找的字符串
     * @param ignoreCase 忽略大小写
     * @return 如果是以指定字符串结束，则返回true
     */
    public static boolean contains(CharSequence str, CharSequence searchStr, boolean ignoreCase) {
        if (str == null || searchStr == null) {
            // 任意一个为null，都返回false
            return false;
        }

        return ignoreCase ? str.toString().toLowerCase().contains(searchStr.toString().toLowerCase()) : str.toString().contains(searchStr);
    }

    /**
     * 判断字符串是否包含指定字符串
     * @param str 被检测字符串
     * @param searchStr 被查找的字符串
     * @return 如果是以指定字符串结束，则返回true
     */
    public static boolean contains(CharSequence str, CharSequence searchStr) {
        return startWith(str, searchStr, false);
    }

    /**
     * 字符串替换
     * @param str       处理字符串
     * @param searchStr 待替换字符串
     * @param replacement   替换后的字符
     * @return  新的字符串
     */
    public static String replace(String str, String searchStr, String replacement) {
        if (isEmpty(str) || isEmpty(searchStr)) {
            return str;
        }

        final StringBuilder sb = new StringBuilder(str.length());
        int pos = 0;
        int index = str.indexOf(searchStr);

        for(int patLen = searchStr.length(); index >= 0; index = str.indexOf(searchStr, pos)) {
            sb.append(str, pos, index);
            sb.append(replacement);
            pos = index + patLen;
        }

        sb.append(str, pos, str.length());
        return sb.toString();
    }

    /**
     * 判断2个字符串是否相等
     * @param str1  字符串1
     * @param str2  字符串2
     * @return  boolean
     */
    public static boolean equals(String str1, String str2) {
        return str1 != null && str1.equals(str2);
    }

    /**
     * 将第一个字符大小写转换
     * @param str   字符
     * @param capitalize    是否转换为大小
     * @return String
     */
    public static String changeFirstCase(String str, boolean capitalize) {
        if (isEmpty(str)) {
            return str;
        }

        char baseChar = str.charAt(0);
        char updatedChar;
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        } else {
            updatedChar = Character.toLowerCase(baseChar);
        }

        if (baseChar == updatedChar) {
            return str;
        } else {
            char[] chars = str.toCharArray();
            chars[0] = updatedChar;
            return new String(chars);
        }
    }

    /**
     * 字符串转对象
     * @param str 待转换json字符串
     * @param clazz 转换后的对象
     * @return 指定对象
     * @param <T>   对象类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseStrToObj(String str, Class<T> clazz) {
        if(StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }

        try {
            return clazz.equals(String.class) ? (T) str : OBJECT_MAPPER.readValue(str, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 反序列化，指定内部泛型类型
     * @param str   待转换json字符串
     * @param clazz 转换后的对象
     * @param innerJavaType 内部变量类型
     * @return  指定对象
     * @param <T> 对象类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseStrToObj(String str, Class<T> clazz, Class<?> innerJavaType) {
        if(StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }

        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(clazz, innerJavaType);
            return clazz.equals(String.class) ? (T) str : OBJECT_MAPPER.readValue(str, javaType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串转对象
     * @param str   待转换json字符串
     * @param clazz 转换后的对象
     * @return 指定对象
     * @param <T> 对象类型
     */
    public static <T> List<T> parseStrToList(String str, Class<T> clazz) {
        if(StringUtils.isEmpty(str)) {
            return null;
        }

        try {

            return OBJECT_MAPPER.readValue(str, OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转成jsonNode
     * @param str 待转换json字符串
     * @return 对应的jsonNode
     */
    public static JsonNode parseStrToObject(String str) {
        if(StringUtils.isEmpty(str)) {
            return NullNode.getInstance();
        }

        try {
            return OBJECT_MAPPER.readTree(str);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将对象装换为字符串
     * @param entity 待转换对象
     * @return  json字符串
     * @param <T>   对象类型
     */
    public static <T> String toString(T entity) {
        try {
            return OBJECT_MAPPER.writeValueAsString(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回OBJECT_MAPPER对象
     * @return  OBJECT_MAPPER对象
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

}
