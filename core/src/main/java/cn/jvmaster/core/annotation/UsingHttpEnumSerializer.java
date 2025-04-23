package cn.jvmaster.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * http 枚举类型序列化
 * @author AI
 * @date 2025/4/9 16:30
 * @version 1.0
**/
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UsingHttpEnumSerializer {
}
