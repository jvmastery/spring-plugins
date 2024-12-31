package cn.springhub.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 删除缓存
 * @author AI
 * @date 2024/12/19 14:16
 * @version 1.0
**/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheRemove {

    /**
     * 缓存名称，可以同时删除多个缓存
     * @return  缓存key名称
     */
    String[] name() default {};

    /**
     * 自定义的缓存解析器
     * @return  对应解析器bean名称
     */
    String resolver() default "";

    /**
     * 是否是批量删除模式
     * @return  是否批量删除
     */
    boolean allEntries() default false;
}
