package cn.springhub.base.constant;

/**
 * 季度
 * @author AI
 * @date 2024/12/12 11:26
 * @version 1.0
**/
public enum Quarter {

    /** 第一季度 */
    Q1(1),
    /** 第二季度 */
    Q2(2),
    /** 第三季度 */
    Q3(3),
    /** 第四季度 */
    Q4(4);

    private final int value;

    Quarter(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
