package cn.springhub.base.factory;

import cn.springhub.base.exception.SystemException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件注解工厂
 * @author AI
 * @date 2024/11/26 9:32
 * @version 1.0
**/
public class EventFactory {

    private static final Map<Class<?>, List<EventEntity>> callMethods = new HashMap<>();

    /**
     * 遍历注册对象中的方法
     * @param bean  对象
     * @param annotationClass   事件注解
     */
    public static void register(Object bean, Class<? extends Annotation> annotationClass) {
        Method[] methods = bean.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(annotationClass)) {
                register(annotationClass, bean, method);
            }
        }
    }

    /**
     * 注册事件
     * @param clazz 对应的事件注解
     * @param bean      对象
     * @param method    方法
     */
    public static void register(Class<?> clazz, Object bean, Method method) {
        if (!callMethods.containsKey(clazz)) {
            callMethods.put(clazz, new ArrayList<>());
        }

        callMethods.get(clazz).add(new EventEntity(bean, method));
    }

    /**
     * 执行方法
     * @param clazz 对应的事件
     * @param args  方法的参数
     */
    public static void invoke(Class<?> clazz, Object... args) {
        List<EventEntity> methodList = callMethods.get(clazz);
        if (methodList == null || methodList.isEmpty()) {
            return;
        }

        // 循环调用所有的方法
        try {
            for (EventEntity item : methodList) {
                Class<?>[] parameterTypes = item.method.getParameterTypes();
                Object[] params = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    // 判断给的参数中有没有对应的类型，如果存在，则设置对应参数，否则，设置为null
                    params[i] = null;

                    // 遍历传入的参数，找到匹配类型的参数
                    for (Object arg : args) {
                        if (arg != null && parameterTypes[i].isAssignableFrom(arg.getClass())) {
                            params[i] = arg;
                            break;  // 找到匹配参数后退出循环
                        }
                    }
                }

                item.method.invoke(item.bean, params);
            }
        } catch (Exception e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /**
     * 事件实体类
     */
    static class EventEntity {
        Object bean;

        Method method;

        public EventEntity(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }
    }
}
