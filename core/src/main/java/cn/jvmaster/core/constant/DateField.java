package cn.jvmaster.core.constant;

import java.util.Calendar;

/**
 * 日期字段
 * @author AI
 * @date 2024/12/12 11:29
 * @version 1.0
**/
public enum DateField {
    /**
     * 世纪
     *
     * @see Calendar#ERA
     */
    ERA(Calendar.ERA),
    /**
     * 年
     *
     * @see Calendar#YEAR
     */
    YEAR(Calendar.YEAR),
    /**
     * 月
     *
     * @see Calendar#MONTH
     */
    MONTH(Calendar.MONTH),
    /**
     * 一年中第几周
     *
     * @see Calendar#WEEK_OF_YEAR
     */
    WEEK(Calendar.WEEK_OF_YEAR),
    /**
     * 一月中第几周
     *
     * @see Calendar#WEEK_OF_MONTH
     */
    WEEK_OF_MONTH(Calendar.WEEK_OF_MONTH),
    /**
     * 一月中的第几天
     *
     * @see Calendar#DAY_OF_MONTH
     */
    DAY_OF_MONTH(Calendar.DAY_OF_MONTH),
    /**
     * 一年中的第几天
     *
     * @see Calendar#DAY_OF_YEAR
     */
    DAY(Calendar.DAY_OF_YEAR)  {
        @Override
        public long getMilliSecond() {
            return HOUR.getMilliSecond() * 24;
        }
    },
    /**
     * 周几，1表示周日，2表示周一
     *
     * @see Calendar#DAY_OF_WEEK
     */
    DAY_OF_WEEK(Calendar.DAY_OF_WEEK),
    /**
     * 天所在的周是这个月的第几周
     *
     * @see Calendar#DAY_OF_WEEK_IN_MONTH
     */
    DAY_OF_WEEK_IN_MONTH(Calendar.DAY_OF_WEEK_IN_MONTH),
    /**
     * 上午或者下午
     *
     * @see Calendar#AM_PM
     */
    AM_PM(Calendar.AM_PM),
    /**
     * 小时，用于12小时制
     *
     * @see Calendar#HOUR
     */
    HOUR_12(Calendar.HOUR) {
        @Override
        public long getMilliSecond() {
            return MINUTE.getMilliSecond() * 60;
        }
    },
    /**
     * 小时，用于24小时制
     *
     * @see Calendar#HOUR
     */
    HOUR(Calendar.HOUR_OF_DAY) {
        @Override
        public long getMilliSecond() {
            return MINUTE.getMilliSecond() * 60;
        }
    },
    /**
     * 分钟
     *
     * @see Calendar#MINUTE
     */
    MINUTE(Calendar.MINUTE) {
        @Override
        public long getMilliSecond() {
            return SECOND.getMilliSecond() * 60;
        }
    },
    /**
     * 秒
     *
     * @see Calendar#SECOND
     */
    SECOND(Calendar.SECOND) {
        @Override
        public long getMilliSecond() {
            return 1000L;
        }
    },
    /**
     * 毫秒
     *
     * @see Calendar#MILLISECOND
     */
    MILLISECOND(Calendar.MILLISECOND) {
        @Override
        public long getMilliSecond() {
            return 1L;
        }
    };
    ;

    private int value;

    DateField(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * 获取时段对应的毫秒数
     * @return
     */
    public long getMilliSecond() {
        return 1;
    }
}
