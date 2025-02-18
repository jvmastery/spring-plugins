package cn.jvmaster.redis.starter;

import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

/**
 * 事件通知注入
 * @author AI
 * @date 2024/11/26 9:43
 * @version 1.0
**/
@Configuration
public class RedisEventAutoConfiguration implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            // 根据类来分类其中的缓存注解

        }

        return bean;
    }
}
