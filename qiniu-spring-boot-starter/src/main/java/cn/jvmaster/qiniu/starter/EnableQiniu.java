package cn.jvmaster.qiniu.starter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 启用starter注解
 * @author AI
 * @date 2025/3/18 16:53
 * @version 1.0
**/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({QiniuAutoConfiguration.class})
public @interface EnableQiniu {

}
