package cn.springhub.base.tree;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * 树接口
 * @author AI
 * @date 2024/12/24 14:34
 * @version 1.0
**/
public interface Tree<T> {

    /**
     * 插入节点
     * @param node 节点数据
     * @return  如果插入成功了，返回当前节点；如果没有找到对应的父节点，返回null
     */
    <V extends T> TreeNode<T> insert(V node);

    /**
     * 批量插入节点。如果没有找到对应的节点，会将节点添加到根节点下
     * @param nodes 节点集合
     */
    <V extends T> Tree<T> insertAll(Collection<V> nodes);

    /**
     * 根据节点数据的id来删除节点
     *
     * @param id  节点数据的ID
     * @return  删除节点的子节点
     */
    List<T> delete(T id);

    /**
     * 根据节点数据的id找到当前节点数据
     *
     * @param id   节点数据的ID
     * @return  对应的节点
     */
    TreeNode<T> search(T id);

    /**
     * 判断是否存在对应的节点ID
     *
     * @param id   节点id
     * @return  如果包含，返回true
     */
    boolean contains(T id);

    /**
     * 前序遍历
     * @return  遍历结果
     */
    List<T> traverse();

    /**
     * 带有回调的前序遍历
     * @param consumer  回调函数，方便进行数据处理
     * @return  遍历结果
     */
    List<T> traverse(Consumer<TreeNode<T>> consumer);

    /**
     * 中序遍历
     * @return  遍历结果
     */
    List<T> inOrderTraverse();

    /**
     * 带有回调的中序遍历
     * @param consumer  回调函数，方便进行数据处理
     * @return  遍历结果
     */
    List<T> inOrderTraverse(Consumer<TreeNode<T>> consumer);

    /**
     * 后序遍历
     * @return  遍历结果
     */
    List<T> postOrderTraverse();

    /**
     * 带有回调的后序遍历
     * @param consumer  回调函数，方便进行数据处理
     * @return  遍历结果
     */
    List<T> postOrderTraverse(Consumer<TreeNode<T>> consumer);
}
