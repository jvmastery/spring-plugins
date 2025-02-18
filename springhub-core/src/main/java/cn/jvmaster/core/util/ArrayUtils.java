package cn.jvmaster.core.util;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 数组工具类
 * @author AI
 * @date 2024/12/5 16:36
 * @version 1.0
**/
public class ArrayUtils {

    /**
     * 判断对象是否是一个数组
     *
     * @param obj   待判断对象
     * @return  如果是数组，返回true
     */
    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    /**
     * 判断数组是否为空
     *
     * @param array 数组对象
     * @return      是否为空
     * @param <T>   数组类型
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否不为空
     *
     * @param array 数组对象
     * @return      是否不为空
     * @param <T>   数组类型
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    /**
     * 新建一个空数组
     *
     * @param clazz 数组对象
     * @param size  数组大小
     * @return      新数组
     * @param <T>   数组对象类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<?> clazz, int size) {
        return (T[]) Array.newInstance(clazz, size);
    }

    /**
     * 克隆一个新的数组对象
     *
     * @param array 待克隆数组
     * @return      新的数组
     * @param <T>   数组类型
     */
    public static <T> T[] clone(T[] array) {
        return array == null ? newArray(Object.class, 0) :  array.clone();
    }

    /**
     * 替换数组中的内容，如果为null，则删除元素。
     *
     * @param array     待处理数组
     * @param function  替换方式
     * @return          新数组
     * @param <T>       数组字段类型
     */
    public static <T> T[] replace(T[] array, Function<T, T> function) {
        if (isEmpty(array) || function == null) {
            return clone(array);
        }

        final List<T> newList = new ArrayList<T>();
        for (T item : array) {
            // 处理数据
            T newResult = function.apply(item);
            if (newResult != null) {
                // 如果为null不添加，表示删除元素
                newList.add(newResult);
            }
        }

        return newList.toArray(newArray(array.getClass().getComponentType(), newList.size()));
    }

    /**
     * 查找需要的数据
     *
     * @param array     待处理数组
     * @param predicate 查找规则
     * @return          查找结果数组
     * @param <T>       数组字段类型
     */
    public static <T> T[] filter(T[] array, Predicate<T> predicate) {
        return replace(array, p -> predicate.test(p) ? p : null);
    }

    /**
     * 删除null元素
     *
     * @param array     待处理数组
     * @return          查找结果数组
     * @param <T>       数组字段类型
     */
    public static <T> T[] removeNull(T[] array) {
        return replace(array, item -> item);
    }

    /**
     * 判断数据在数组中的位置。如果没有找到，返回-1
     *
     * @param array         数组
     * @param predicate     判断规则
     * @return      位置下标
     * @param <T>   数据类型
     */
    public static <T> int indexOf(T[] array, Predicate<T> predicate) {
        return indexOf(array, 0, predicate);
    }

    /**
     * 判断数据在数组中的位置。如果没有找到，返回-1
     *
     * @param array         数组
     * @param startPosition 开始判断位置
     * @param predicate     判断规则
     * @return      位置下标
     * @param <T>   数据类型
     */
    public static <T> int indexOf(T[] array, int startPosition, Predicate<T> predicate) {
        if (isEmpty(array) || predicate == null) {
            return -1;
        }

        // 查找满足条件的数据
        for (int i = startPosition; i < array.length; i++) {
            if (predicate.test(array[i])) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 判断数据在数组中的位置。如果没有找到，返回-1
     *
     * @param array         数组
     * @param element     比较数据
     * @return      位置下标
     * @param <T>   数据类型
     */
    public static <T> int indexOf(T[] array, Object element) {
        return indexOf(array, 0, element);
    }

    /**
     * 判断数据在数组中的位置。如果没有找到，返回-1
     *
     * @param array         数组
     * @param startPosition 开始判断位置
     * @param element     比较数据
     * @return      位置下标
     * @param <T>   数据类型
     */
    public static <T> int indexOf(T[] array, int startPosition, Object element) {
        if (ObjectUtils.isNull(element)) {
            return -1;
        }

        return indexOf(array, startPosition, item -> ObjectUtils.equals(item, element));
    }

    /**
     * 获取数组指定下标的数据。如果为负数，表示从结束开始查找。
     *
     * @param array     数组
     * @param index     下标
     * @return          对应下标数据
     * @param <T>       数据类型
     */
    public static <T> T get(T[] array, int index) {
        if (isEmpty(array)) {
            return null;
        }

        if (index < 0) {
            index += array.length;
        }

        return index >= array.length ? null : array[index];
    }

    /**
     * 移除指定下标元素
     * @param array 数组
     * @param index 移除元素下标
     * @return      移除元素后的新数组
     * @param <T>   数据类型
     */
    public static <T> T[] remove(T[] array, int index) {
        if (isEmpty(array)) {
            return newArray(Object.class, 0);
        }

        if (index < 0 || index >= array.length) {
            return clone(array);
        }

        final T[] newArray = newArray(array.getClass().getComponentType(), array.length - 1);
        System.arraycopy(array, 0, newArray, 0, index);

        // 后半部分
        if (index < array.length - 1) {
            System.arraycopy(array, index + 1, newArray, index, array.length - index - 1);
        }
        return newArray;
    }

    /**
     * 将新元素添加到数组中，返回一个新的数组
     *
     * @param array     数组
     * @param elements  待添加元素
     * @return          新数组
     * @param <T>       数据类型
     */
    @SafeVarargs
    public static <T> T[] append(T[] array, T... elements) {
        if (isEmpty(elements)) {
            return clone(array);
        }

        if (isEmpty(array)) {
            return clone(elements);
        }

        // 拼接数组
        final T[] newArray = newArray(array.getClass().getComponentType(), array.length + elements.length);
        System.arraycopy(array, 0, newArray, 0, array.length);
        System.arraycopy(elements, 0, newArray, array.length, elements.length);

        return newArray;
    }

    public static void main(String[] args) {
        Integer[] array = new Integer[]{1, 2, 3, 4, 5};
        Integer[] b = replace(array, x -> x * 2);

        System.out.println(StringUtils.toString(array));
        System.out.println(StringUtils.toString(append(null, array)));
        System.out.println(StringUtils.toString(append(b)));
        System.out.println(StringUtils.toString(append(array, b)));
    }
}
