package cn.jvmaster.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注接口是开放接口，无需通过授权即可访问
 * @author AI
 * @date 2025/4/23 11:05
 * @version 1.0
**/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OpenApi {

    /**
     * 允许的ip地址
     */
    String[] ips() default {};

}
