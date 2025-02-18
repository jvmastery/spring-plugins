package cn.jvmaster.redis.annotation;

import cn.jvmaster.redis.constant.Mode;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存注解，标注的方法，在调用后自动添加到redis缓存中
 * @author AI
 * @date 2024/12/16 14:51
 * @version 1.0
**/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Caches.class)
public @interface Cache {

    /**
     * 缓存名称
     * @return  缓存key名称
     */
    String name() default "";

    /**
     * 缓存存在的时间，单位：秒
     * 默认为30天有效期
     * @return  有效时间
     */
    long expire() default 60*60*24*30L;

    /**
     * 自定义的缓存解析器
     * @return  对应解析器bean名称
     */
    String resolver() default "";

    /**
     * 缓存模式
     * 当为NORMAL时，如果存在缓存，会通过缓存进行获取
     *    当为UPDATE_ONLY时，则不管缓存存不存在，则都会更新缓存
     * @return  对应的模式
     */
    Mode mode() default Mode.NORMAL;
}
