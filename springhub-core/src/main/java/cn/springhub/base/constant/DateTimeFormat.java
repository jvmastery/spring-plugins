package cn.springhub.base.constant;

/**
 * 日期格式
 * @author AI
 * @date 2024/12/12 9:16
 * @version 1.0
**/
public interface DateTimeFormat {

    /**
     * 年
     */
    String NORMAL_YEAR = "yyyy";

    /**
     * 年-月
     */
    String NORMAL_YEAR_MONTH = "yyyy-MM";

    /**
     * 年-月-日
     */
    String NORMAL_DATE = "yyyy-MM-dd";

    /**
     * 标准时间格式：HH:mm:ss
     */
    String NORMAL_TIME = "HH:mm:ss";

    /**
     * 年-月-日 时
     */
    String NORMAL_DATETIME_HOUR = "yyyy-MM-dd HH";

    /**
     * 年-月-日 时:分
     */
    String NORMAL_DATETIME_MINUTE = "yyyy-MM-dd HH:mm";

    /**
     * 年-月-日 时:分:秒
     */
    String NORMAL_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 年-月-日 时:分:秒.毫秒
     */
    String NORMAL_DATETIME_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 中文日期
     */
    String CHINESE_DATE = "yyyy年MM月dd日";

    /**
     * 中文时间
     */
    String CHINESE_DATETIME = "yyyy年MM月dd日HH时mm分ss秒";

    /**
     * 简单日期
     */
    String SIMPLE_DATE = "yyyyMMdd";

    /**
     * 简单时间
     */
    String SIMPLE_DATETIME = "yyyyMMddHHmmss";

    /**
     * 简单毫秒数
     */
    String SIMPLE_DATETIME_MILLIS = "yyyyMMddHHmmssSSS";
}
