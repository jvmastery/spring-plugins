package cn.springhub.base.tree;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * 二叉树，每个节点最多有两个子节点，通常被称为左子节点和右子节点。常见变种有：
 * <pre>
 *  - 满二叉树：每个非叶子节点都有两个子节点，所有叶子节点都在同一层。
 *  - 完全二叉树：除了最底层外，其他层的节点都被填满，且最底层的节点从左到右填充。
 *  - 平衡二叉树 (AVL Tree)：一种自平衡的二叉搜索树，保证任何节点的左右子树高度差不超过1。
 *  - 红黑树 (Red-Black Tree)：一种自平衡的二叉搜索树，具有颜色标记，每个节点要么是红色，要么是黑色，遵循一些约束以保持树的平衡。
 * </pre>
 *
 * @author AI
 * @date 2024/12/26 8:58
 * @version 1.0
**/
record BinaryTree<T>() implements Tree<T> {

    @Override
    public <V extends T> TreeNode<T> insert(V node) {
        return null;
    }

    @Override
    public <V extends T> Tree<T> insertAll(Collection<V> nodes) {
        return null;
    }

    @Override
    public List<T> delete(T id) {
        return List.of();
    }

    @Override
    public TreeNode<T> search(T id) {
        return null;
    }

    @Override
    public boolean contains(T id) {
        return false;
    }

    @Override
    public List<T> traverse() {
        return List.of();
    }

    @Override
    public List<T> traverse(Consumer<TreeNode<T>> consumer) {
        return List.of();
    }

    @Override
    public List<T> inOrderTraverse() {
        return List.of();
    }

    @Override
    public List<T> inOrderTraverse(Consumer<TreeNode<T>> consumer) {
        return List.of();
    }

    @Override
    public List<T> postOrderTraverse() {
        return List.of();
    }

    @Override
    public List<T> postOrderTraverse(Consumer<TreeNode<T>> consumer) {
        return List.of();
    }

}
