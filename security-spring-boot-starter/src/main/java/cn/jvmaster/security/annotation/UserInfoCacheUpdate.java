package cn.jvmaster.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注用户缓存更新
 * @author AI
 * @date 2025/5/23 9:55
 * @version 1.0
**/
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserInfoCacheUpdate {

    String[] ids();
}
