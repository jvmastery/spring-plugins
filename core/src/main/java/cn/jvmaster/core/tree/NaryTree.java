package cn.jvmaster.core.tree;

import cn.jvmaster.core.exception.SystemException;
import cn.jvmaster.core.serializer.TreeSerializer;
import cn.jvmaster.core.tree.NaryTree.NaryTreeNode;
import cn.jvmaster.core.util.CollectionUtils;
import cn.jvmaster.core.util.StringUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.constant.ConstantDesc;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 多叉树，一个根节点有多个叶子节点
 *
 * @author AI
 * @date 2025/4/23 16:39
 * @version 1.0
**/
@JsonSerialize(using = TreeSerializer.class)
public class NaryTree<T> implements Tree<T, NaryTreeNode<T>> {

    /**
     * 根节点
     */
    private NaryTreeNode<T> root;

    /**
     * 节点数据提取器，用于获取节点ID
     */
    private Function<T, ? extends ConstantDesc> keyExtractor;

    /**
     * 节点数据提取器，用于获取节点ID
     */
    private Function<T, String> nameExtractor;

    /**
     * 节点数据提取器，用于获取节点父ID
     */
    private Function<T, ? extends ConstantDesc> parentExtractor;

    /**
     * 节点数据提取器，用于获取节点的排序值
     */
    private Comparator<T> sortComparator;

    public NaryTree(NaryTreeNode<T> root,
                    Function<T, ? extends ConstantDesc> keyExtractor,
                    Function<T, ? extends ConstantDesc> parentExtractor,
                    Function<T, String> nameExtractor,
                    Comparator<T> sortComparator) {
        this.root = root;
        this.keyExtractor = keyExtractor;
        this.nameExtractor = nameExtractor;
        this.parentExtractor = parentExtractor;
        this.sortComparator = sortComparator;
    }

    public NaryTreeNode<T> getRoot() {
        return root;
    }

    public void setRoot(NaryTreeNode<T> root) {
        this.root = root;
    }

    public void setKeyExtractor(Function<T, ? extends ConstantDesc> keyExtractor) {
        this.keyExtractor = keyExtractor;
    }

    public void setParentExtractor(Function<T, ? extends ConstantDesc> parentExtractor) {
        this.parentExtractor = parentExtractor;
    }

    public void setSortComparator(Comparator<T> sortComparator) {
        this.sortComparator = sortComparator;
    }

    public void setNameExtractor(Function<T, String> nameExtractor) {
        this.nameExtractor = nameExtractor;
    }

    @Override
    public <V extends T> NaryTreeNode<T> insert(V node) {
        return insertTreeNode(node);
    }

    /**
     * 插入树节点
     * @param node  节点内容
     * @return  插入的节点
     */
    private NaryTreeNode<T> insertTreeNode(T node) {
        if (node == null) {
            return null;
        }

        ConstantDesc parentId = parentExtractor.apply(node);
        Optional<NaryTreeNode<T>> parentNode = parentId == null ? Optional.empty() : search(item -> {
            if (parentId.equals(item.getId())) {
                return true;
            }

            return item.getData() != null && parentId.equals(keyExtractor.apply(item.getData()));
        });
        if (parentNode.isEmpty()) {
            // 没有父节点数据
            return null;
        } else {
            // 找到了父节点，将当前节点插入到父节点子元素中
            NaryTreeNode<T> treeNode = NaryTreeNode.build(node, keyExtractor, nameExtractor);

            parentNode.get().getChildren().add(treeNode);
            sort(parentNode.get());

            return treeNode;
        }
    }

    @Override
    public <V extends T> Tree<T, NaryTreeNode<T>> insertAll(Collection<V> nodes) {
        return insertAll(nodes, null);
    }

    @Override
    public <V extends T> Tree<T, NaryTreeNode<T>> insertAll(Collection<V> nodes, Predicate<V> predicate) {
        if (CollectionUtils.isEmpty(nodes)) {
            return this;
        }

        // 游离节点集合，针对暂时还没有插入到集合中的节点进行存储，其中key是节点的父ID
        Map<ConstantDesc, List<T>> freeNodeMap = new HashMap<>();

        for (V node : nodes) {
            if (node == null || (predicate != null && !predicate.test(node))) {
                continue;
            }

            NaryTreeNode<T> currentNode = insertTreeNode(node);
            if (currentNode != null) {
                // 判断是否存在相应的游离节点，如果存在，则添加到子节点中。且添加的节点也需要遍历判断
                addFreeNode(currentNode, freeNodeMap);
                continue;
            }

            // 不成功，添加到游离节点内
            ConstantDesc parentId = parentExtractor.apply(node);
            if (!freeNodeMap.containsKey(parentId)) {
                freeNodeMap.put(parentId, new ArrayList<>());
            }
            freeNodeMap.get(parentId).add(node);
        }

        // 将剩余的游离节点添加到根节点下
        if (!freeNodeMap.isEmpty()) {
            freeNodeMap.forEach((key, value) -> {
                root.getChildren().addAll(value.stream().map(item -> NaryTreeNode.build(item, keyExtractor, nameExtractor)).toList());
            });
            sort(root);
        }

        return this;
    }

    /**
     * 添加游离节点
     * @param currentNode  当前节点
     * @param freeNodeMap  游离节点集合
     */
    private void addFreeNode(NaryTreeNode<T> currentNode, Map<ConstantDesc, List<T>> freeNodeMap) {
        ConstantDesc id = keyExtractor.apply(currentNode.getData());
        if (!freeNodeMap.containsKey(id)) {
            return;
        }

        // 存在该节点的游离节点
        List<NaryTreeNode<T>> nodeList = freeNodeMap.get(id).stream().map(item -> NaryTreeNode.build(item, keyExtractor, nameExtractor)).toList();
        currentNode.getChildren().addAll(nodeList);
        sort(currentNode);
        freeNodeMap.remove(id);

        nodeList.forEach(node -> addFreeNode(node, freeNodeMap));
    }

    @Override
    public List<T> delete(T data) {
        if (data == null) {
            return null;
        }
        
        return delete(item -> equals(item, data));
    }

    /**
     * 根据条件进行删除
     *
     * @param predicate 条件
     * @return  删除节点的子节点
     */
    public List<T> delete(Predicate<NaryTreeNode<T>> predicate) {
        if (predicate.test(root)) {
            // 如果是根节点，则只将子节点数据置空
            List<T> children = root.getChildren().stream().map(NaryTreeNode::getData).toList();
            root.getChildren().clear();

            return children;
        }

        return delete(root, predicate).map(tvTreeNode -> tvTreeNode.getChildren().stream().map(NaryTreeNode::getData).toList()).orElse(null);
    }

    /**
     * 遍历查询节点并删除
     * @param root  根节点
     * @return  被删除的节点
     */
    private Optional<NaryTreeNode<T>> delete(NaryTreeNode<T> root, Predicate<NaryTreeNode<T>> predicate) {
        for (final NaryTreeNode<T> currentNode : root.getChildren()) {
            if (predicate.test(currentNode)) {
                // 找到目标节点，移除节点
                root.getChildren().remove(currentNode);

                // 将移除节点的子节点添加到父节点中
                if (!currentNode.getChildren().isEmpty()) {
                    root.getChildren().addAll(currentNode.getChildren());
                    sort(currentNode);
                }

                return Optional.of(currentNode);
            }

            // 在子节点中查找删除
            Optional<NaryTreeNode<T>> result = delete(currentNode, predicate);
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<NaryTreeNode<T>> search(T node) {
        if (node == null) {
            return Optional.empty();
        }

        return search(item -> equals(item, node));
    }

    /**
     * 搜索指定数据
     * @param predicate 匹配函数
     * @return  找到的节点
     */
    @Override
    public Optional<NaryTreeNode<T>> search(Predicate<NaryTreeNode<T>> predicate) {
        if (predicate.test(root)) {
            return Optional.of(root);
        }

        return CollectionUtils.search(root.getChildren(), predicate, NaryTreeNode::getChildren);
    }

    @Override
    public boolean contains(T node) {
        if (root == null || node == null) {
            return false;
        }

        // 节点标识相等
        if (equals(root, node)) {
            return true;
        }

        // 判断子节点是否存在相等的数据
        return CollectionUtils.contains(root.getChildren(), item -> equals(item, node), NaryTreeNode::getChildren);
    }

    /**
     * 前序遍历：访问根节点 -> 遍历所有子节点（从左到右）
     * @return 遍历结果
     */
    @Override
    public List<T> traverse() {
        return traverse(null);
    }

    @Override
    public List<T> traverse(Consumer<NaryTreeNode<T>> consumer) {
        return traversal(root, consumer, true);
    }

    /**
     * 前序遍历：访问根节点 -> 遍历所有子节点（从左到右）
     * @param root  根节点
     * @return  子节点
     */
    private List<T> traversal(NaryTreeNode<T> root, Consumer<NaryTreeNode<T>> consumer, boolean isPreOrder) {
        List<T> list = new ArrayList<>();
        if (root == null) {
            return list;
        }

        if (!isPreOrder) {
            // 后序遍历，先访问子节点
            CollectionUtils.traverse(root.getChildren(), item -> list.addAll(traversal(item, consumer, isPreOrder)));
        }

        if (consumer != null) {
            consumer.accept(root);
        }

        if (isPreOrder) {
            // 前序遍历，先访问根节点
            CollectionUtils.traverse(root.getChildren(), item -> list.addAll(traversal(item, consumer, isPreOrder)));
        }
        return list;
    }

    @Override
    public List<T> inOrderTraverse() {
        throw new SystemException("多叉树不支持中序遍历");
    }

    @Override
    public List<T> inOrderTraverse(Consumer<NaryTreeNode<T>> consumer) {
        throw new SystemException("多叉树不支持中序遍历");
    }

    /**
     * 后续遍历：先遍历所有子节点，然后访问根节点
     * @return 遍历结果
     */
    @Override
    public List<T> postOrderTraverse() {
        return postOrderTraverse(null);
    }

    @Override
    public List<T> postOrderTraverse(Consumer<NaryTreeNode<T>> consumer) {
        return traversal(root, consumer, false);
    }

    /**
     * 比较2个节点是否相等
     *
     * @param node1  节点
     * @param node2    节点
     * @return  如果一致，则返回true
     */
    private boolean equals(NaryTreeNode<T> node1, T node2) {
        ConstantDesc node2Id = keyExtractor.apply(node2);
        if (node1.getId() != null) {
            if (node1.getId().equals(node2Id)) {
                return true;
            }
        }

        if (node1.getData() == null) {
            return false;
        }

        return keyExtractor.apply(node1.getData()).equals(keyExtractor.apply(node2));
    }

    /**
     * 对子节点进行排序
     *
     * @param node 节点
     */
    private void sort(NaryTreeNode<T> node) {
        node.getChildren().sort((t1, t2) -> sortComparator.compare(t1.getData(), t2.getData()));
    }

    @Override
    public String toString() {
        // 如果根节点是空节点，则表示无实际意义
        return StringUtils.toString(root.data instanceof EmptyNode ? root.children : root);
    }

    /**
     * 多叉树节点
     * @param <T>   节点数据类型
     */
    public static class NaryTreeNode<T> implements TreeNode<T> {

        /**
         * 对应节点的ID
         */
        private ConstantDesc id;

        /**
         * 节点名称
         */
        private String name;

        /**
         * 节点内容
         */
        private T data;

        /**
         * 子节点
         */
        private List<NaryTreeNode<T>> children = new ArrayList<>();

        public NaryTreeNode() {
        }

        public NaryTreeNode(ConstantDesc id, String name, T data, List<NaryTreeNode<T>> children) {
            this.id = id;
            this.name = name;
            this.data = data;
            this.children = children;
        }

        public ConstantDesc getId() {
            return id;
        }

        public void setId(ConstantDesc id) {
            this.id = id;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public List<NaryTreeNode<T>> getChildren() {
            return children;
        }

        public void setChildren(List<NaryTreeNode<T>> children) {
            this.children = children;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        /**
         * 构建节点
         * @param data  节点数据
         * @return      节点对象
         * @param <T>   数据类型
         */
        public static <T> NaryTreeNode<T> build(T data, Function<T, ? extends ConstantDesc> keyExtractor, Function<T, String> nameExtractor) {
            return new NaryTreeNode<>(keyExtractor.apply(data), nameExtractor != null ? nameExtractor.apply(data) : null, data, new ArrayList<>());
        }
    }
}
