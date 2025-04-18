package cn.jvmaster.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * 反射工具类
 * @author AI
 * @date 2025/4/18 9:48
 * @version 1.0
**/
public class ReflectUtils {

    /**
     * 查询带有指定注解的字段
     * @param clazz     对象类型
     * @param annotationClass   注解类型
     * @return  对应的字段集合
     */
    public static List<Field> findAnnotationFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        if (clazz == null || annotationClass == null) {
            return Collections.emptyList();
        }

        return findFields(clazz, field -> field.isAnnotationPresent(annotationClass));
    }

    /**
     * 根据条件找到满足条件的字段
     * @param clazz         对象类型
     * @param predicate     条件
     * @return  对应的字段集合
     */
    public static List<Field> findFields(Class<?> clazz, Predicate<Field> predicate) {
        if (clazz == null || predicate == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(clazz.getDeclaredFields()).filter(predicate).toList();
    }

    /**
     * 获取指定名称的字段
     * @param clazz     对象类型
     * @param fieldName 字段名称
     * @return  Field
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        return getField(clazz, field -> StringUtils.equals(field.getName(), fieldName));
    }

    /**
     * 获取指定字段
     * @param clazz         对象类型
     * @param predicate     字段条件
     * @return Field
     */
    public static Field getField(Class<?> clazz, Predicate<Field> predicate) {
        if (clazz == null || predicate == null) {
            return null;
        }

        return Arrays.stream(clazz.getDeclaredFields()).filter(predicate).findFirst().orElse(null);
    }

    /**
     * 获取字段对应的值
     * @param obj           对象
     * @param fieldName     字段名称
     * @return  字段的值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        if (obj == null || StringUtils.isEmpty(fieldName)) {
            return null;
        }

        return getFieldValue(obj, getField(obj.getClass(), fieldName));
    }

    /**
     * 获取字段对应的值
     * @param obj       对象
     * @param field     字段
     * @return          字段的值
     */
    public static Object getFieldValue(Object obj, Field field) {
        if (obj == null || field == null) {
            return null;
        }

        try {
            field.setAccessible(true);
            // 静态字段获取时对象为null
            return field.get(obj instanceof Class<?> ? null : obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置字段的 值
     * @param obj       对象
     * @param fieldName     字段名称
     * @param value     字段的值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        if (obj == null || StringUtils.isEmpty(fieldName)) {
            return;
        }

        setFieldValue(obj, getField(obj.getClass(), fieldName), value);
    }

    /**
     * 设置字段的 值
     * @param obj       对象
     * @param field     字段
     * @param value     字段的值
     */
    public static void setFieldValue(Object obj, Field field, Object value) {
        if (obj == null || field == null) {
            return;
        }

        try {
            field.setAccessible(true);
            field.set(obj instanceof Class ? null : obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
