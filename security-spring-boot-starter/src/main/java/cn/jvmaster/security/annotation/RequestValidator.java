package cn.jvmaster.security.annotation;

import cn.jvmaster.security.constant.Permission;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义接口对应的权限
 * @author AI
 * @date 2025/4/29 9:10
 * @version 1.0
**/
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestValidator {

    /**
     * 权限
     */
    Permission value();

    /**
     * 自定义额外参数
     */
    String[] data() default {};
}
