package cn.jvmaster.core.constant;

/**
 * 常用正则表达式
 * @author AI
 * @date 2024/12/12 14:28
 * @version 1.0
**/
public interface RegexConstant {

    /**
     * 日期正则表达式
     * yyyy-MM-dd HH:mm:ss.SSSSSS
     * yyyy-MM-dd HH:mm:ss
     * yyyy-MM-dd HH:mm
     * yyyy-MM-dd HH
     * yyyy-MM-dd
     */
    String DATE_PATTERN = "\\d{4}-\\d{1,2}-\\d{1,2}(\\s+\\d{1,2}(:\\d{1,2})?(:\\d{1,2})?(.\\d{1,6})?)?";

}
