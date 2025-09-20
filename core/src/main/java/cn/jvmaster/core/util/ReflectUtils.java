package cn.jvmaster.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
     * 遍历对象字段
     * @param clazz     对象类型
     * @param consumer  回调
     */
    public static void traverseFields(Class<?> clazz, Consumer<Field> consumer) {
        if (clazz == null || consumer == null) {
            return;
        }

        Arrays.stream(clazz.getDeclaredFields()).forEach(consumer);

        // 父类中找相应字段
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null &&  superClass != Object.class) {
            traverseFields(superClass, consumer);
        }
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

        Field field = Arrays.stream(clazz.getDeclaredFields()).filter(predicate).findFirst().orElse(null);
        if (field != null) {
            return field;
        }

        // 从父类中找
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null &&  superClass != Object.class) {
            return getField(superClass, predicate);
        }

        return null;
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
            field.set(obj, convertValue(value, field.getType()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据集合填充对象数据
     * @param instance  对象
     * @param list      集合数据
     * @param nameApply 获取字段名称
     * @param valueApply    获取字段值
     */
    public static <T> void fillObject(Object instance, List<T> list, Function<T, String> nameApply, Function<T, Object> valueApply) {
        if (list == null || list.isEmpty()) {
            return;
        }

        Map<String, Field> fieldMap = Arrays.stream(instance.getClass().getDeclaredFields())
            .collect(Collectors.toMap(Field::getName, Function.identity()));

        // 遍历数据
        for (T item : list) {
            String fieldName = nameApply.apply(item);
            if (StringUtils.isEmpty(fieldName) || !fieldMap.containsKey(fieldName)) {
                continue;
            }

            Object value = valueApply.apply(item);
            if (value == null) {
                continue;
            }

            setFieldValue(instance, fieldMap.get(fieldName), value);
        }
    }

    /**
     * 基本类型转换方法
     */
    public static Object convertValue(Object valueObj, Class<?> targetType) {
        if (valueObj == null) {
            return null;
        }

        // 如果已经是目标类型或其子类，直接返回
        if (targetType.isInstance(valueObj)) {
            return valueObj;
        }

        // 转成字符串再解析
        String valueStr = String.valueOf(valueObj).trim();
        if (valueStr.isEmpty()) {
            return null;
        }

        if (targetType == String.class) {
            return valueStr;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(valueStr);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(valueStr);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(valueStr);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.valueOf(valueStr);
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.valueOf(valueStr);
        } else if (targetType == Short.class || targetType == short.class) {
            return Short.valueOf(valueStr);
        } else if (targetType == Byte.class || targetType == byte.class) {
            return Byte.valueOf(valueStr);
        }
        throw new IllegalArgumentException("Unsupported field type: " + targetType);
    }
}
