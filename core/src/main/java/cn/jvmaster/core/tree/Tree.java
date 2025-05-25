package cn.jvmaster.core.tree;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 树接口
 * @author AI
 * @date 2024/12/24 14:34
 * @version 1.0
**/
public interface Tree<T, N extends TreeNode<T>> {

    /**
     * 插入节点
     * @param node 节点数据
     * @return  如果插入成功了，返回当前节点；如果没有找到对应的父节点，返回null
     */
    <V extends T> N insert(V node);

    /**
     * 批量插入节点。如果没有找到对应的节点，会将节点添加到根节点下
     * @param nodes 节点集合
     */
    <V extends T> Tree<T, N> insertAll(Collection<V> nodes);

    /**
     * 批量插入节点。如果没有找到对应的节点，会将节点添加到根节点下
     * @param nodes 节点集合
     */
    default <V extends T> Tree<T, N> insertAll(Collection<V> nodes, Predicate<V> predicate) {
        return insertAll(nodes, node -> true);
    }

    /**
     * 根据节点数据来删除节点
     *
     * @param data  节点数据的
     * @return  删除节点的子节点
     */
    List<T> delete(T data);

    /**
     * 根据节点数据的找到当前节点数据
     *
     * @param data   节点数据
     * @return 对应的节点
     */
    Optional<N> search(T data);

    /**
     * 根据节点数据的找到当前节点数据
     *
     * @param predicate   节点数据的
     * @return 对应的节点
     */
    Optional<N> search(Predicate<N> predicate);

    /**
     * 判断是否存在对应的节点
     *
     * @param data   节点
     * @return  如果包含，返回true
     */
    boolean contains(T data);

    /**
     * 前序遍历         根  -->   左    -->   右
     * @return  遍历结果
     */
    List<T> traverse();

    /**
     * 带有回调的前序遍历
     * @param consumer  回调函数，方便进行数据处理
     * @return  遍历结果
     */
    List<T> traverse(Consumer<N> consumer);

    /**
     * 中序遍历    左   -->   根    -->    右
     * @return  遍历结果
     */
    List<T> inOrderTraverse();

    /**
     * 带有回调的中序遍历
     * @param consumer  回调函数，方便进行数据处理
     * @return  遍历结果
     */
    List<T> inOrderTraverse(Consumer<N> consumer);

    /**
     * 后序遍历   左 -->  右   -->    中
     * @return  遍历结果
     */
    List<T> postOrderTraverse();

    /**
     * 带有回调的后序遍历
     * @param consumer  回调函数，方便进行数据处理
     * @return  遍历结果
     */
    List<T> postOrderTraverse(Consumer<N> consumer);
}
