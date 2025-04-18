package cn.jvmaster.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 启用安全功能
 * @author AI
 * @date 2025/4/14 10:43
 * @version 1.0
**/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ SecurityAutoConfiguration.class, SecurityConfig.class, SecurityRequestConfiguration.class })
public @interface EnableSecurity {

}
