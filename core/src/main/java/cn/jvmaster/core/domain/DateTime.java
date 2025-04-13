package cn.jvmaster.core.domain;

import cn.jvmaster.core.constant.DateField;
import cn.jvmaster.core.constant.Month;
import cn.jvmaster.core.constant.Week;
import cn.jvmaster.core.util.DateUtils;
import java.io.Serial;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期类型
 * @author AI
 * @date 2024/12/12 10:03
 * @version 1.0
**/
public class DateTime extends Date {
    @Serial
    private static final long serialVersionUID = 6789036098405441557L;

    /**
     * 一周开始的一天
     */
    private Week firstDayOfWeek = Week.MONDAY;

    /**
     * 对应的日历对象
     */
    private Calendar calendar;

    public DateTime() {
    }

    public DateTime(long date) {
        super(date);
    }

    public DateTime(Date date) {
        this(date.getTime());
    }

    public DateTime(Calendar calendar) {
        this(calendar.getTime());
    }

    public DateTime(Instant instant) {
        this(instant.toEpochMilli());
    }

    /**
     * 复制一份
     * @return 新的时间对象
     */
    public DateTime newTime() {
        return new DateTime(this.getTime());
    }

    /**
     * 转换为日历格式
     * @return  日历对象
     */
    public Calendar toCalendar() {
        if (calendar != null) {
            return calendar;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this);
        calendar.setFirstDayOfWeek(firstDayOfWeek.getValue());
        this.calendar = calendar;
        return calendar;
    }

    /**
     * 转换为java日期格式
     * @return java日期对象
     */
    public Date toDate() {
        return this;
    }

    /**
     * 调整日期
     * @param field     需要调整的字段
     * @param offset    调整的数量
     */
    public DateTime add(DateField field, int offset) {
        final Calendar calendar = toCalendar();
        calendar.add(field.getValue(), offset);

        setTime(calendar.getTimeInMillis());
        return this;
    }

    /**
     * 获取日期的某部分
     * @param field 获取字段
     * @return  对应字段数值
     */
    public int get(DateField field) {
        return toCalendar().get(field.getValue());
    }

    /**
     * 设置指定部分的值
     * @param field 需要调整的字段
     * @param value 需要调整成的值
     */
    public DateTime setField(DateField field, int value) {
        final Calendar calendar = toCalendar();
        calendar.set(field.getValue(), value);

        setTime(calendar.getTimeInMillis());
        return this;
    }

    /**
     * 获取所属季度
     * @return 季度
     */
    public int getQuarter() {
        return getMonth() / 3 + 1;
    }

    /**
     * 获得指定日期是所在年份的第几周
     * 此方法返回值与一周的第一天有关.
     * 2016年1月3日为周日，如果一周的第一天为周日，那这天是第二周（返回2）
     * 如果一周的第一天为周一，那这天是第一周（返回1）
     * @return 所在年份的第几周
     */
    public int getWeek() {
        return get(DateField.WEEK);
    }

    /**
     * 返回日期所在月份的第几周
     * 此方法返回值与一周的第一天有关
     * @return 所在月份的第几周
     */
    public int getWeekOfMonth() {
        return get(DateField.WEEK_OF_MONTH);
    }

    /**
     * 返回所属年份
     * @return  所属年份
     */
    @Override
    public int getYear() {
        return get(DateField.YEAR);
    }

    /**
     * 返回所属月份
     * @return  所属月份
     */
    @Override
    public int getMonth() {
        return get(DateField.MONTH);
    }

    /**
     * 默认情况下，月份从0开始
     * 实际情况下，月份应该从1开始
     * @return 真实月份
     */
    public int getReallyMonth() {
        return getMonth() + 1;
    }

    /**
     * 所属天数
     * @return 一月中的第几天
     */
    @Override
    public int getDate() {
        return get(DateField.DAY_OF_MONTH);
    }

    /**
     * 所属天数
     * @return 一年中的第几天
     */
    @Override
    public int getDay() {
        return get(DateField.DAY);
    }

    /**
     * 获取所在月份的第几天，从1开始
     * @return 一月中的第几天
     */
    public int getDayOfMonth() {
        return get(DateField.DAY_OF_MONTH);
    }

    /**
     * 是否是上午
     * @return 如果是上午，返回true
     */
    public boolean isAm() {
        return Calendar.AM == get(DateField.AM_PM);
    }

    /**
     * 是否是下午
     * @return 如果是下午，返回true
     */
    public boolean isPm() {
        return Calendar.PM == get(DateField.AM_PM);
    }

    /**
     * 是否是周末
     * @return 如果是周末，返回true
     */
    public boolean isWeekend() {
        final int day = getWeek();
        return day == Week.SATURDAY.getValue() || day == Week.SUNDAY.getValue();
    }

    /**
     * 是否是闰年
     * @return 如果是闰年，返回true
     */
    public boolean isLeapYear() {
        return DateUtils.isLeapYear(getYear());
    }

    /**
     * 小时，用于24小时制
     * @return 小时
     */
    @Override
    public int getHours() {
        return get(DateField.HOUR);
    }

    /**
     * 获取分钟
     * @return  分钟
     */
    @Override
    public int getMinutes() {
        return get(DateField.MINUTE);
    }

    /**
     * 获取秒
     * @return 秒
     */
    @Override
    public int getSeconds() {
        return get(DateField.SECOND);
    }

    /**
     * 设置年
     * @param year    the year value.
     */
    @Override
    public void setYear(int year) {
        setField(DateField.YEAR, year);
    }

    /**
     * 设置月
     * @param month   the month value between 0-11.
     */
    @Override
    public void setMonth(int month) {
        setField(DateField.MONTH, month);
    }

    /**
     * 设置天
     * @param date   the day of the month value between 1-31.
     */
    @Override
    public void setDate(int date) {
        setField(DateField.DAY_OF_MONTH, date);
    }

    /**
     * 设置天
     * @param date 一年中的天数
     */
    public void setDay(int date) {
        setField(DateField.DAY, date);
    }

    /**
     * 设置小时
     * @param hours  24小时数
     */
    @Override
    public void setHours(int hours) {
        setField(DateField.HOUR, hours);
    }

    /**
     * 设置分钟
     * @param minutes   the value of the minutes.
     */
    @Override
    public void setMinutes(int minutes) {
        setField(DateField.MINUTE, minutes);
    }

    /**
     * 设置秒
     * @param seconds   the seconds value.
     */
    @Override
    public void setSeconds(int seconds) {
        setField(DateField.SECOND, seconds);
    }

    /**
     * 设置时间戳
     * @param time   the number of milliseconds.
     */
    @Override
    public void setTime(long time) {
        super.setTime(time);
        this.calendar = null;
    }

    /**
     * 获取本月最后一天
     * @return 本月最后一天
     */
    public int getLastDayOfMonth() {
        final int month = getMonth();
        return Month.getMonth(month).getLastDay(isLeapYear());
    }

    public Week getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setFirstDayOfWeek(Week firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    @Override
    public String toString() {
        return DateUtils.covert(this);
    }
}
