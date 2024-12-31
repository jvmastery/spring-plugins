package cn.springhub.base.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 集合工具类
 * @author AI
 * @date 2024/11/18 17:10
 * @version 1.0
**/
public class CollectionUtils {

    /**
     * 判断集合是否为空
     * @param collection    集合
     * @return  是否为空
     */
    public static boolean isEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 如果list为null。则返回一个不可变的默认空集合，否则返回原集合
     * @param list  list
     * @return 非空list
     * @param <T> 元素类型
     */
    public static <T> List<T> emptyIfNull(final List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }


    /**
     * 如果Set为null。则返回一个不可变的默认空集合，否则返回原集合
     * @param set  Set
     * @return 非空Set
     * @param <T> 元素类型
     */
    public static <T> Set<T> emptyIfNull(final Set<T> set) {
        return set == null ? Collections.emptySet() : set;
    }

    /**
     * 判断集合是否包含某个元素
     * @param collection    集合
     * @param predicate     判断函数
     * @return  如果包含，则返回true
     * @param <T>   元素类型
     */
    public static <T> boolean contains(final Collection<T> collection, Predicate<? super T> predicate) {
        if (isEmpty(collection)) {
            return false;
        }

        return collection.stream().anyMatch(predicate);
    }

    /**
     * 判断集合是否包含某个元素，如果存在子元素，则递归查询
     *
     * @param collection    集合
     * @param predicate     判断函数
     * @param function    获取子元素方法
     * @return  如果包含，则返回true
     * @param <T>   元素类型
     */
    public static <T> boolean contains(final Collection<T> collection, Predicate<? super T> predicate, Function<T, Collection<T>> function) {
        if (isEmpty(collection)) {
            return false;
        }

        for (final T element : collection) {
            if (predicate.test(element) || (function != null && contains(function.apply(element), predicate, function))) {
                // 当前条件满足，或者子元素中存在数据满足条件
                return true;
            }
        }

        return false;
    }

    /**
     * 判断集合是否包含某个元素
     *
     * @param collection    集合
     * @param predicate     判断函数
     * @return  找到的数据
     * @param <T>   元素类型
     */
    public static <T> Optional<T> search(final Collection<T> collection, Predicate<? super T> predicate) {
        if (isEmpty(collection)) {
            return Optional.empty();
        }

        return collection.stream().filter(predicate).findFirst();
    }

    /**
     * 判断集合是否包含某个元素
     *
     * @param collection    集合
     * @param predicate     判断函数
     * @param function 或者子元素方法
     * @return  找到的数据
     * @param <T>   元素类型
     */
    public static <T> Optional<T> search(final Collection<T> collection, Predicate<? super T> predicate, Function<T, Collection<T>> function) {
        return searchAndRemove(collection, predicate, function, false);
    }

    /**
     * 判断集合是否包含某个元素
     *
     * @param collection    集合
     * @param predicate     判断函数
     * @param function 或者子元素方法
     * @return  找到的数据
     * @param <T>   元素类型
     */
    private static <T> Optional<T> searchAndRemove(final Collection<T> collection, Predicate<? super T> predicate, Function<T, Collection<T>> function, boolean remove) {
        if (isEmpty(collection)) {
            return Optional.empty();
        }

        for (final T element : collection) {
            if (predicate.test(element)) {
                // 当前条件满足，或者子元素中存在数据满足条件
                if (remove) {
                    collection.remove(element);
                }
                return Optional.of(element);
            }

            // 查找子元素集合
            if (function != null) {
                Optional<T> result = searchAndRemove(function.apply(element), predicate, function, remove);
                if (result.isPresent()) {
                    return result;
                }
            }
        }

        return Optional.empty();
    }

    /**
     * 删除集合中指定的元素
     *
     * @param collection    集合
     * @param predicate     判断函数
     * @param function 或者子元素方法
     * @return  找到的数据
     * @param <T>   元素类型
     */
    public static <T> Optional<T> remove(final Collection<T> collection, Predicate<? super T> predicate, Function<T, Collection<T>> function) {
        return searchAndRemove(collection, predicate, function, true);
    }

    /**
     * 遍历集合，如果集合为空，则不处理
     * @param collection    集合数据
     * @param <T>   元素类型
     */
    public static <T> void traverse(final Collection<T> collection, Consumer<T> consumer) {
        if (isEmpty(collection)) {
            return;
        }

        collection.forEach(consumer);
    }

    /**
     * 拼接字符串
     * @param collection   集合
     * @return  拼接后的字符串
     */
    public static <T> String join(final Collection<T> collection) {
        return join(collection, ",");
    }

    /**
     * 拼接字符串
     * @param collection   集合
     * @param delimiter    分隔符
     * @return  拼接后的字符串
     */
    public static <T> String join(final Collection<T> collection, final String delimiter) {
        return join(collection, delimiter, String::valueOf);
    }

    /**
     * 拼接字符串
     * @param collection   集合
     * @param delimiter    分隔符
     * @param function 转换程序，将对象转换成字符串
     * @return  拼接后的字符串
     */
    public static <T> String join(final Collection<T> collection, final CharSequence delimiter, Function<T, String> function) {
        if (isEmpty(collection)) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        boolean first = true;

        // 遍历添加
        for (final T element : collection) {
            String result = function == null ? String.valueOf(element) : function.apply(element);
            if (!first) {
                builder.append(delimiter);
            } else {
                first = false;
            }

            builder.append(result);
        }

        return builder.toString();
    }
}
