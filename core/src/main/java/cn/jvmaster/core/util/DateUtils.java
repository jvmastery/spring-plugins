package cn.jvmaster.core.util;

import cn.jvmaster.core.constant.DateField;
import cn.jvmaster.core.constant.DateTimeFormat;
import cn.jvmaster.core.domain.DateTime;
import cn.jvmaster.core.exception.SystemException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期工具类
 * @author AI
 * @date 2024/11/15 16:04
 * @version 1.0
**/
public class DateUtils {
    private static final Map<String, SimpleDateFormat> FORMAT_MAP = new HashMap<>();

    /**
     * 获取时间
     * @return 当前时间
     */
    public static DateTime now() {
        return new DateTime();
    }

    /**
     * 获取日期
     * @param date  java时间
     * @return  DateTime对象时间
     */
    public static DateTime date(Date date) {
        if (date == null) {
            return null;
        } else if (date instanceof DateTime) {
            return (DateTime) date;
        } else {
            return new DateTime(date);
        }
    }

    /**
     * 获取当前时间字符串，格式：yyyy-MM-dd HH:mm:ss
     * @return  时间字符串
     */
    public static String getCurrentTime() {
        return covert(new Date());
    }

    /**
     * 将字符串日期转换为日期格式，字符串格式：yyyy-MM-dd HH:mm:ss
     * @param time  待转换时间字符串
     * @return DateTime对象
     */
    public static DateTime convert(String time) {
        return convert(time, DateTimeFormat.NORMAL_DATETIME);
    }

    /**
     * 将字符串日期转换为日期格式
     * @param time  待转换时间字符串
     * @param format    字符串格式
     * @return  时间对象
     */
    public static DateTime convert(String time, String format) {
        if (StringUtils.isEmpty(time) || StringUtils.isEmpty(format)) {
            return null;
        }

        try {
            return date(getFormat(format).parse(time));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将日期格式转换为字符串，格式：yyyy-MM-dd HH:mm:ss
     * @param date  时间
     * @return  字符串时间
     */
    public static String covert(Date date) {
        return covert(date, DateTimeFormat.NORMAL_DATETIME);
    }

    /**
     * 将日期格式转换为字符串
     * @param date  时间
     * @param format    字符串格式
     * @return  字符串时间
     */
    public static String covert(Date date, String format) {
        if (date == null || StringUtils.isEmpty(format)) {
            return null;
        }

        try {
            return getFormat(format).format(date);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取格式化对象
     * @param format    字符串格式
     * @return  格式化对象
     */
    private static SimpleDateFormat getFormat(String format) {
        if (FORMAT_MAP.containsKey(format)) {
            return FORMAT_MAP.get(format);
        }

        // 创建对象
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        FORMAT_MAP.put(format, dateFormat);

        return dateFormat;
    }

    /**
     * 是否是闰年
     * @param year  年份
     * @return  是否是闰年
     */
    public static boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }

    /**
     * 比较2个日期相差年份（非严格模式），只比较到年份，不管后面的月份
     * e:
     *      getYearDiff(2023-12-01, 2024-01-01) = 1
     *      getYearDiff(2023-01-01, 2024-01-01) = 1
     *      getYearDiff(2024-01-01, 2024-02-01) = 0
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return  2个时间相差年份
     */
    public static long getYearDiff(DateTime startTime, DateTime endTime) {
        return getTimeDiff(startTime, endTime, DateField.YEAR, false);
    }

    /**
     * 比较2个日期相差月份（非严格模式），只比较到月份，不管后面的天数
     * e：
     *      getMonthDiff(2023-12-05, 2024-01-01) = 1
     *      getMonthDiff(2024-01-05, 2024-01-01) = 0
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return  2个时间相差月份
     */
    public static long getMonthDiff(DateTime startTime, DateTime endTime) {
        return getTimeDiff(startTime, endTime, DateField.MONTH, false);
    }

    /**
     * 比较2个日期相差天数（非严格模式），只比较到天数，不管后面的小时
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 相差天数
     */
    public static long getDayDiff(DateTime startTime, DateTime endTime) {
        return getTimeDiff(startTime, endTime, DateField.DAY, false);
    }

    /**
     * 比较2个日期的时间差
     * @param time1     第一个时间
     * @param time2     比较时间
     * @param dateField 需要计算的时间差
     * @param strict 是否是严格模式，严格模式下，年/月/日的返回会有偏差
     *               年：如果起始日期的天大于结束日期的天，年数要少算1
     *               月：如果起始日期的天大于结束日期的天，月数要少算1
     *               日：如果起始时间的小时数大于结束日期的小时数，则天要减去1
     */
    public static long getTimeDiff(DateTime time1, DateTime time2, DateField dateField, boolean strict) {
        if (time1 == null || time2 == null) {
            throw new NullPointerException("比较日期不能为空");
        }

        int compareResult = compare(time1, time2);
        if (compareResult == 0) {
            // 日期相同
            return 0L;
        } else if (compareResult > 0) {
            // time1 > time2，为了保证返回结果为正数，让time2总是>time1
            DateTime temp = time1;
            time1 = time2;
            time2 = temp;
        }

        long differ;
        switch (dateField) {
            case YEAR:
                differ = time2.getYear() - time1.getYear();
                if (strict) {
                    // 如果是严格模式，如果起始日期的天大于结束日期的天，年份要少算1（不足1年）
                    time2 = time2.newTime();
                    time2.setYear(time1.getYear());
                    if (time1.getTime() > time2.getTime()) {
                        differ = differ - 1;
                    }
                }
                break;
            case MONTH:
                int betweenYear = time2.getYear() - time1.getYear();
                int month = time2.getReallyMonth() - time1.getReallyMonth();
                // 相差月份
                differ = betweenYear * 12L + month;
                if (strict) {
                    // 如果是严格模式下，如果起始日期的天大于结束日期的天，月数要少算1（不足1个月）
                    time2 = time2.newTime();
                    time2.setYear(time1.getYear());
                    time2.setMonth(time1.getMonth());
                    if (time1.getTime() > time2.getTime()) {
                        differ = differ - 1;
                    }
                }
                break;
            case DAY:
                if (!strict) {
                    /*
                      由于按照时差进行统计，可能会存在前后2天时差小于1的情况
                      如：
                      time1：2024-12-12 20:00
                      time2：2024-12-13 02:00
                      此时，时间差为5小时(不足1天)，但按照日期来算（非严格模式）的话，应该是差1天

                      因此，为了排除时间差，将2个日期的时间设置为相同
                     */
                    time2 = time2.newTime();
                    time2.setHours(time1.getHours());
                    time2.setMinutes(time1.getMinutes());
                    time2.setSeconds(time1.getSeconds());
                    time2.setField(DateField.MILLISECOND, time1.getMinutes());
                }
            case HOUR:
            case MINUTE:
            case SECOND:
            case MILLISECOND:
                differ = (time2.getTime() - time1.getTime()) / dateField.getMilliSecond();
                break;
            default:
                throw new SystemException("无效的日期字段：" + dateField.name());
        }

        return differ;
    }

    /**
     * 比较2个日期的大小
     * @param dateTime1 开始时间
     * @param dateTime2 结束时间
     * @return  dateTime1 &gt; dateTime2， 返回1，dateTime1 = dateTime2，返回0，dateTime1 &lt; dateTime2，返回-1
     */
    public static int compare(DateTime dateTime1, DateTime dateTime2) {
        if (dateTime1 == null && dateTime2 == null) {
            return 0;
        } else if (dateTime1 == null) {
            return -1;
        } else if (dateTime2 == null) {
            return 1;
        } else {
            return dateTime1.compareTo(dateTime2);
        }
    }

    /**
     * 比较日期是否在2个日期之间
     * @param date          待比较日期
     * @param compareTime1  开始时间
     * @param compareTime2  结束时间
     * @return  如果日期在2个日期之间，返回true
     */
    public static boolean between(Date date, Date compareTime1, Date compareTime2) {
        long dateMillis = date.getTime();
        long startMillis = compareTime1.getTime();
        long endMillis = compareTime2.getTime();

        return dateMillis >= Math.min(startMillis, endMillis) && dateMillis <= Math.max(startMillis, endMillis);
    }
}
