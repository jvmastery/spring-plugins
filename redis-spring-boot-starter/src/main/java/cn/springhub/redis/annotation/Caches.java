package cn.springhub.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 重复缓存注解，让@Cache可以重复使用
 * @author AI
 * @date 2024/12/16 14:53
 * @version 1.0
**/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Caches {
    Cache[] value();
}
