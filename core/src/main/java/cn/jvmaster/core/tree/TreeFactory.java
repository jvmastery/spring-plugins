package cn.jvmaster.core.tree;

import cn.jvmaster.core.tree.NaryTree.NaryTreeNode;
import cn.jvmaster.core.util.AssertUtils;
import java.lang.constant.ConstantDesc;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;

/**
 * 树构造工厂
 * @author AI
 * @date 2024/12/26 14:26
 * @version 1.0
**/
public class TreeFactory {

    /**
     * 构建默认多叉树。
     * <pre>
     * 节点对象：
     *   - 继承Node接口
     *   - 主键标识为Long
     * </pre>
     *
     * @return              多叉树
     */
    public static <T extends Node<Long>> NaryTree<T> build() {
        return buildBaseTree(0L, Node::getId, Node::getParentId, Node::getDisplayName, Comparator.comparingLong(Node::getPx));
    }

    /**
     * 构建多叉树
     * <pre>
     * 节点对象：
     *   - 继承Node接口
     *   - 主键标识为Long
     * </pre>
     *
     * @return              多叉树
     */
    public static <T extends Node<Long>> NaryTree<T> build(Collection<T> dataList) {
        NaryTree<T> tree = build();
        tree.insertAll(dataList);

        return tree;
    }

    /**
     * 自定义构建多叉树
     *
     * @param root          根节点
     * @param keyExtractor  id获取方式
     * @param parentExtractor   parentId获取方式
     * @param nameExtractor 节点名称获取方式
     * @param sortComparator    数据排序方式
     * @return              多叉树
     * @param <T>           数据类型
     */
    public static <T> NaryTree<T> build(T root,
                                        Function<T, ? extends ConstantDesc> keyExtractor,
                                        Function<T, ? extends ConstantDesc> parentExtractor,
                                        Function<T, String> nameExtractor,
                                        Comparator<T> sortComparator) {
        AssertUtils.notNull(root, "树根节点不能为空");
        AssertUtils.notNull(keyExtractor, "节点标识函数不能为空");
        AssertUtils.notNull(parentExtractor, "父节点标识函数不能为空");
        AssertUtils.notNull(sortComparator, "排序比较器不能为空");

        return new NaryTree<>(NaryTreeNode.build(root, keyExtractor, nameExtractor), keyExtractor, parentExtractor, nameExtractor, sortComparator);
    }

    /**
     * 自定义构建多叉树
     *
     * @param key          根节点
     * @param keyExtractor  id获取方式
     * @param parentExtractor   parentId获取方式
     * @param nameExtractor 节点名称获取方式
     * @param sortComparator    数据排序方式
     * @return              多叉树
     * @param <T>           数据类型
     */
    public static <T> NaryTree<T> buildBaseTree(ConstantDesc key,
                                Function<T, ? extends ConstantDesc> keyExtractor,
                                Function<T, ? extends ConstantDesc> parentExtractor,
                                Function<T, String> nameExtractor,
                                Comparator<T> sortComparator) {
        AssertUtils.notNull(key, "树根节点不能为空");
        AssertUtils.notNull(keyExtractor, "节点标识函数不能为空");
        AssertUtils.notNull(parentExtractor, "父节点标识函数不能为空");
        AssertUtils.notNull(sortComparator, "排序比较器不能为空");

        NaryTreeNode<T> root = new NaryTreeNode<>();
        root.setId(key);

        return new NaryTree<>(root, keyExtractor, parentExtractor, nameExtractor, sortComparator);
    }

    /**
     * 自定义构建多叉树
     *
     * @param root          根节点
     * @param keyExtractor  id获取方式
     * @param parentExtractor   parentId获取方式
     * @param sortComparator    数据排序方式
     * @param nameExtractor 节点名称获取方式
     * @param dataList      构建树的数据集合
     * @return              多叉树
     * @param <T>           数据类型
     */
    public static <T> NaryTree<T> build(T root,
                                        Function<T, ? extends ConstantDesc> keyExtractor,
                                        Function<T, ? extends ConstantDesc> parentExtractor,
                                        Function<T, String> nameExtractor,
                                        Comparator<T> sortComparator,
                                        Collection<T> dataList) {
        NaryTree<T> naryTree = build(root, keyExtractor, parentExtractor, nameExtractor, sortComparator);
        naryTree.insertAll(dataList);

        return naryTree;
    }
}
