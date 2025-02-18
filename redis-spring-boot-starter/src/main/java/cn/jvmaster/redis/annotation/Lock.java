package cn.jvmaster.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 锁注解，将指定方法用分布式锁进行锁定
 * @author AI
 * @date 2024/12/12 9:05
 * @version 1.0
**/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {

    /**
     * 锁名称，可以使用springEl表达式
     * #root：对象属性
     * #method: 方法
     * #params：参数，内容是一个数组，根据#params[i]来获取第i个参数
     * #[paramName]：直接根据参数名称来，[paramName]为对应的参数名称
     */
    String name() default "";

    /**
     * 锁存在时长，单位：秒
     */
    long lockExistSeconds() default 30;

    /**
     * 当锁存在时，等待重试时间
     */
    int retryTimes() default 3;
}
