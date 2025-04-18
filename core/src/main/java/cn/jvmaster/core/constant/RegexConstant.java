package cn.jvmaster.core.constant;

/**
 * 常用正则表达式
 * @author AI
 * @date 2024/12/12 14:28
 * @version 1.0
**/
public interface RegexConstant {
    /**
     * 电子邮箱
     * 示例：example@domain.com
     */
    String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    /**
     * 中国大陆手机号
     * 支持 13、14、15、16、17、18、19 开头的
     */
    String MOBILE = "^1[3-9]\\d{9}$";

    /**
     * 固话（含区号、可选分机号）
     * 示例：010-12345678 或 010-12345678-1234
     */
    String TELEPHONE = "^(0\\d{2,3}-)?\\d{7,8}(-\\d{1,4})?$";

    /**
     * URL（http, https）
     */
    String URL = "^(https?://)?([\\w-]+\\.)+[\\w-]+(:\\d+)?(/\\S*)?$";

    /**
     * IPv4 地址
     */
    String IPV4 = "^((25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}"
        + "(25[0-5]|2[0-4]\\d|[01]?\\d?\\d)$";

    /**
     * IPv6 地址（简化模式，不支持所有压缩写法）
     */
    String IPV6 = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";

    /**
     * 身份证号码（15 或 18 位）
     */
    String ID_CARD = "^(\\d{15}|\\d{17}[0-9Xx])$";

    /**
     * 邮政编码（中国）
     */
    String ZIP_CODE = "^[1-9]\\d{5}$";

    /**
     * 日期（yyyy-MM-dd）
     */
    String DATE = "^(1|2)\\d{3}-(0[1-9]|1[0-2])-"
        + "(0[1-9]|[12]\\d|3[01])$";

    /**
     * 时间（HH:mm:ss）
     */
    String TIME = "^(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$";

    /**
     * 日期时间（yyyy-MM-dd HH:mm:ss）
     */
    String DATE_TIME = DATE + "\\s" + TIME;

    /**
     * 日期时间（yyyy-MM-dd HH）
     */
    String DATETIME_HOUR = "^(1|2)\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])\\s(?:[01]\\d|2[0-3])$";

    /**
     * 日期时间（yyyy-MM-dd HH:mm）
     */
    String DATETIME_MINUTE = "^(1|2)\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])\\s(?:[01]\\d|2[0-3]):[0-5]\\d$";

    /**
     * 日期时间（yyyy-MM-dd HH:mm:ss.SSS）
     */
    String DATETIME_MILLISECOND = "^(1|2)\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])\\s(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d\\.\\d+$";

    /**
     * 整数（正负数）
     */
    String INTEGER = "^-?\\d+$";

    /**
     * 正整数
     */
    String POSITIVE_INTEGER = "^[1-9]\\d*$";

    /**
     * 负整数
     */
    String NEGATIVE_INTEGER = "^-[1-9]\\d*$";

    /**
     * 小数（包含正负，可含小数点）
     */
    String DECIMAL = "^-?\\d+(\\.\\d+)?$";

    /**
     * 中文字符
     */
    String CHINESE = "^[\\u4e00-\\u9fa5]+$";

    /**
     * 英文和数字
     */
    String ALPHANUMERIC = "^[A-Za-z0-9]+$";

    /**
     * 强密码（6-20 位，必须包含字母和数字）
     */
    String STRONG_PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$";

    /**
     * QQ 号（5-12 位，首位非 0）
     */
    String QQ = "^[1-9][0-9]{4,13}$";

    /**
     * HTML 标签
     */
    String HTML_TAG = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";

    /**
     * 匹配空白行
     */
    String BLANK_LINE = "^\\s*$";
}
