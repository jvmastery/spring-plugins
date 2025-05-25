package cn.jvmaster.core.tree;

import cn.jvmaster.core.tree.BinarySearchTree.BinaryTreeNode;
import cn.jvmaster.core.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 二叉搜索树。一种特殊的二叉树，其中每个节点的左子树包含的值都小于节点的值，右子树包含的值都大于节点的值。
 * @author AI
 * @date 2024/12/26 8:59
 * @version 1.0
**/
public class BinarySearchTree<T> implements Tree<T, BinaryTreeNode<T>> {

    /**
     * 根节点
     */
    private BinaryTreeNode<T> root;

    /**
     * 数据比较器
     */
    private Comparator<T> comparator;

    public BinarySearchTree(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public BinarySearchTree() {
    }

    @Override
    public <V extends T> BinaryTreeNode<T> insert(V node) {
        if (node == null) {
            return root;
        }

        if (root == null) {
            root = new BinaryTreeNode<>(node);
            return root;
        }

        return insert(root, node);
    }

    /**
     * 插入数据到节点下
     * @param root  根节点
     * @param node  待插入节点
     */
    private BinaryTreeNode<T> insert(BinaryTreeNode<T> root, T node) {
        if (root == null) {
            return new BinaryTreeNode<>(node);
        }

        // 和根节点数据进行比较，如果大于，则插入到右子树中，小于则插入到左子树中
        int result = compare(root.data, node);
        if (result < 0) {
            root.right = insert(root.right, node);
        } else if (result > 0) {
            root.left = insert(root.left, node);
        }

        return root;
    }

    /**
     * 比较2个数据
     * @param data1 待比较数据
     * @param data2 待比较数据
     * @return  2个数据的大小
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private int compare(T data1, T data2) {
        if (comparator == null) {
            if (data1 instanceof Comparable t1 && data2 instanceof Comparable t2) {
                return t1.compareTo(t2);
            }

            return String.valueOf(data1).compareTo(String.valueOf(data2));
        }

        return comparator.compare(data1, data2);
    }

    @Override
    public <V extends T> Tree<T, BinaryTreeNode<T>> insertAll(Collection<V> nodes) {
        CollectionUtils.traverse(nodes, this::insert);
        return this;
    }

    @Override
    public List<T> delete(T data) {
        BinaryTreeNode<T> deleteNode = delete(root, item -> compare(item, data));
        if (deleteNode == null) {
            return Collections.emptyList();
        }

        return List.of(deleteNode.data);
    }

    /**
     * 删除指定节点
     * @param root  根节点
     * @param apply 数据比较器
     */
    public <V extends T> BinaryTreeNode<V> delete(BinaryTreeNode<V> root, Function<T, Integer> apply) {
        if (root == null) {
            return null;
        }

        int compare = apply.apply(root.data);
        if (compare < 0) {
            // 待查找数据在左子树
            root.left = delete(root.left, apply);
        } else if (compare > 0) {
            // 待查找数据在右子树
            root.right = delete(root.right, apply);
        } else {
            // 当前节点
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }

            // 当左右子树都不为空，则需要找到右子树的最小值，替换当前节点
            BinaryTreeNode<V> minNode = root.right;
            while (minNode.left != null) {
                minNode = minNode.left;
            }

            // 替换当前节点
            root.data = minNode.data;

            // 删除右子树最小值
            BinaryTreeNode<V> finalMinNode = minNode;
            delete(root.right, item -> compare(item, finalMinNode.data));
        }

        return root;
    }

    @Override
    public Optional<BinaryTreeNode<T>> search(T data) {
        if (data == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(search(root, item -> compare(item, data)));
    }

    @Override
    public Optional<BinaryTreeNode<T>> search(Predicate<BinaryTreeNode<T>> predicate) {
        return Optional.empty();
    }

    /**
     * 查询数据
     * @param root  根节点
     * @param apply 数据比较器
     */
    private <V extends T> BinaryTreeNode<V> search(BinaryTreeNode<V> root, Function<T, Integer> apply) {
        if (root == null) {
            return null;
        }

        int compare = apply.apply(root.data);
        if (compare < 0) {
            // 查左子树
            return search(root.left, apply);
        } else if (compare > 0) {
            // 右子树
            return search(root.right, apply);
        }

        // 当前节点
        return root;
    }

    @Override
    public boolean contains(T data) {
        return search(data).isPresent();
    }

    @Override
    public List<T> traverse() {
        return traverse(null);
    }

    @Override
    public List<T> traverse(Consumer<BinaryTreeNode<T>> consumer) {
        List<T> list = new ArrayList<>();
        traverse(root, item -> {
            list.add(item.getData());
            if (consumer != null) {
                consumer.accept(item);
            }
        });

        return list;
    }

    /**
     * 前序遍历， 根 - 左 - 右
     * @param node      根节点
     * @param consumer  回调函数
     */
    private void traverse(BinaryTreeNode<T> node, Consumer<BinaryTreeNode<T>> consumer) {
        if (node == null) {
            return;
        }

        consumer.accept(node);
        traverse(node.getLeft(), consumer);
        traverse(node.getRight(), consumer);
    }

    @Override
    public List<T> inOrderTraverse() {
        return inOrderTraverse(null);
    }

    @Override
    public List<T> inOrderTraverse(Consumer<BinaryTreeNode<T>> consumer) {
        List<T> list = new ArrayList<>();
        inOrderTraverse(root, item -> {
            list.add(item.getData());
            if (consumer != null) {
                consumer.accept(item);
            }
        });

        return list;
    }

    /**
     * 中序遍历， 左 - 根 - 右
     * @param node      根节点
     * @param consumer  回调函数
     */
    private void inOrderTraverse(BinaryTreeNode<T> node, Consumer<BinaryTreeNode<T>> consumer) {
        if (node == null) {
            return;
        }

        inOrderTraverse(node.getLeft(), consumer);
        consumer.accept(node);
        inOrderTraverse(node.getRight(), consumer);
    }

    @Override
    public List<T> postOrderTraverse() {
        return postOrderTraverse(null);
    }

    @Override
    public List<T> postOrderTraverse(Consumer<BinaryTreeNode<T>> consumer) {
        List<T> list = new ArrayList<>();
        postOrderTraverse(root, item -> {
            list.add(item.getData());
            if (consumer != null) {
                consumer.accept(item);
            }
        });

        return list;
    }

    /**
     * 后序遍历， 左 - 右 - 根
     * @param node      根节点
     * @param consumer  回调函数
     */
    private void postOrderTraverse(BinaryTreeNode<T> node, Consumer<BinaryTreeNode<T>> consumer) {
        if (node == null) {
            return;
        }

        postOrderTraverse(node.getLeft(), consumer);
        postOrderTraverse(node.getRight(), consumer);
        consumer.accept(node);
    }

    /**
     * 二叉树节点
     * @param <T>   节点数据类型
     */
    public static class BinaryTreeNode<T> implements TreeNode<T> {
        /**
         * 根节点
         */
        private T data;

        /**
         * 左子树
         */
        private BinaryTreeNode<T> left;

        /**
         * 右子树
         */
        private BinaryTreeNode<T> right;

        public BinaryTreeNode(T data) {
            this.data = data;
        }

        public BinaryTreeNode(T data, BinaryTreeNode<T> left, BinaryTreeNode<T> right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public BinaryTreeNode<T> getLeft() {
            return left;
        }

        public void setLeft(BinaryTreeNode<T> left) {
            this.left = left;
        }

        public BinaryTreeNode<T> getRight() {
            return right;
        }

        public void setRight(BinaryTreeNode<T> right) {
            this.right = right;
        }
    }
}
