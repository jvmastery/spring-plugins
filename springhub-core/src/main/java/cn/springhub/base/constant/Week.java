package cn.springhub.base.constant;

import java.util.Calendar;

/**
 * 星期
 * @author AI
 * @date 2024/12/12 11:13
 * @version 1.0
**/
public enum Week {
    /**
     * 周日
     */
    SUNDAY(Calendar.SUNDAY, "星期天"),
    /**
     * 周一
     */
    MONDAY(Calendar.MONDAY, "星期一"),
    /**
     * 周二
     */
    TUESDAY(Calendar.TUESDAY, "星期二"),
    /**
     * 周三
     */
    WEDNESDAY(Calendar.WEDNESDAY, "星期三"),
    /**
     * 周四
     */
    THURSDAY(Calendar.THURSDAY, "星期四"),
    /**
     * 周五
     */
    FRIDAY(Calendar.FRIDAY, "星期五"),
    /**
     * 周六
     */
    SATURDAY(Calendar.SATURDAY, "星期六");
    ;

    private final int value;

    /**
     * 中文名
     */
    private final String chineseName;

    Week(int value, String chineseName) {
        this.value = value;
        this.chineseName = chineseName;
    }

    public int getValue() {
        return value;
    }

    public String getChineseName() {
        return chineseName;
    }
}
