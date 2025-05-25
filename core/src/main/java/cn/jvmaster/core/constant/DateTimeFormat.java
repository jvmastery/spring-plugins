package cn.jvmaster.core.constant;

/**
 * 日期格式
 * @author AI
 * @date 2024/12/12 9:16
 * @version 1.0
**/
public enum DateTimeFormat {
    /**
     * yyyy-MM-dd HH:mm:ss 格式
     */
    NORMAL_DATETIME(
        "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$",
        "yyyy-MM-dd HH:mm:ss",
        "年-月-日 时:分:秒（如 2024-05-21 15:30:00）"
    ),

    /**
     * yyyy/MM/dd HH:mm:ss 格式
     */
    NORMAL_DATETIME_SLASH(
        "^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$",
        "yyyy/MM/dd HH:mm:ss",
        "年/月/日 时:分:秒（如 2024/05/21 15:30:00）"
    ),

    /**
     * yyyy-MM-dd 格式
     */
    NORMAL_DATE(
        "^\\d{4}-\\d{1,2}-\\d{1,2}$",
        "yyyy-MM-dd",
        "年-月-日（如 2024-05-21）"
    ),

    /**
     * yyyy/MM/dd 格式
     */
    NORMAL_DATE_SLASH(
        "^\\d{4}/\\d{1,2}/\\d{1,2}$",
        "yyyy/MM/dd",
        "年/月/日（如 2024/05/21）"
    ),

    /**
     * xx年xx月xx日HH时mm分ss秒 格式
     */
    CHINESE_DATETIME(
        "^\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒$",
        "yyyy年MM月dd日HH时mm分ss秒",
        "中文日期（如2024年05月21日15时30分00秒）"
    ),

    /**
     * xx年xx月xx日 格式
     */
    CHINESE_DATE(
        "^\\d{4}年\\d{1,2}月\\d{1,2}日$",
        "yyyy年MM月dd日",
        "中文日期（如2024年05月21日）"
    ),

    /**
     * yyyyMMddHHmmss 紧凑格式
     */
    SIMPLE_DATETIME(
        "^\\d{14}$",
        "yyyyMMddHHmmss",
        "年月日时分秒（如 20240521153000）"
    ),

    /**
     * yyyyMMdd 紧凑日期格式
     */
    SIMPLE_DATE(
        "^\\d{8}$",
        "yyyyMMdd",
        "年月日（如 20240521）"
    );
    ;

    /**
     * 日期字符串判断正则
     */
    private final String regex;

    /**
     * 日期转换格式
     */
    private final String pattern;

    /**
     * 日期描述
     */
    private final String description;

    DateTimeFormat(String regex, String pattern, String description) {
        this.regex = regex;
        this.pattern = pattern;
        this.description = description;
    }

    public String getRegex() {
        return regex;
    }

    public String getPattern() {
        return pattern;
    }

    public String getDescription() {
        return description;
    }
}
