package cn.springhub.base.constant;

import java.util.Calendar;

/**
 * 月份
 * @author AI
 * @date 2024/12/12 11:16
 * @version 1.0
**/
public enum Month {

    /**
     * 一月
     */
    JANUARY(Calendar.JANUARY),
    /**
     * 二月
     */
    FEBRUARY(Calendar.FEBRUARY),
    /**
     * 三月
     */
    MARCH(Calendar.MARCH),
    /**
     * 四月
     */
    APRIL(Calendar.APRIL) {
        @Override
        protected int getLastDay() {
            return 30;
        }
    },
    /**
     * 五月
     */
    MAY(Calendar.MAY),
    /**
     * 六月
     */
    JUNE(Calendar.JUNE) {
        @Override
        protected int getLastDay() {
            return 30;
        }
    },
    /**
     * 七月
     */
    JULY(Calendar.JULY),
    /**
     * 八月
     */
    AUGUST(Calendar.AUGUST),
    /**
     * 九月
     */
    SEPTEMBER(Calendar.SEPTEMBER) {
        @Override
        protected int getLastDay() {
            return 30;
        }
    },
    /**
     * 十月
     */
    OCTOBER(Calendar.OCTOBER),
    /**
     * 十一月
     */
    NOVEMBER(Calendar.NOVEMBER) {
        @Override
        protected int getLastDay() {
            return 30;
        }
    },
    /**
     * 十二月
     */
    DECEMBER(Calendar.DECEMBER),
    /**
     * 十三月，仅用于农历
     */
    UNDECIMBER(Calendar.UNDECIMBER);

    private final int value;

    Month(int value) {
        this.value = value;
    }

    /**
     * 获取月份最后一天
     * @return 当前月份最后一天
     */
    protected int getLastDay() {
        return 31;
    }

    /**
     * 判断当前年份最大最大值
     * 主要是二月份
     * @param isLeapYear    是否是闰年
     * @return 当前月份最后一天
     */
    public int getLastDay(boolean isLeapYear) {
        if (this.value != FEBRUARY.value) {
            return getLastDay();
        }

        return isLeapYear ? 29 : 28;
    }

    private static final Month[] MONTHS = Month.values();

    public static Month getMonth(int value) {
        return value >= MONTHS.length ? null : MONTHS[value];
    }
}
