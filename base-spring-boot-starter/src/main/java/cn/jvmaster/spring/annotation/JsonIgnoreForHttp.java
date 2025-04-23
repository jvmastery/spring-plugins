package cn.jvmaster.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义字段在http中不返回
 * @author AI
 * @date 2025/4/1 16:55
 * @version 1.0
**/
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonIgnoreForHttp {

}
