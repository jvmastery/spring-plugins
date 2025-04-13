# 项目介绍
springhub-core是一个常用工具类合集，包括字符串、集合、线程、加密等相关方法。

# 工具类相关
## CollectionUtils
提供了和集合相关的工具方法。

# 树结构
## 树的定义
### 节点定义
叶子节点必须实现Node接口。Node内部定义如下：
```
public interface Node<T extends ConstantDesc> {
    /**
     * 获取主键标识
     * @return  标识
     */
    T getId();

    /**
     * 获取节点名称
     * @return  名称
     */
    String getName();

    /**
     * 获取父节点ID
     * @return  父节点ID
     */
    T getParentId();

    /**
     * 获取节点顺序
     * @return  顺序
     */
    long getPx();
}
```

### 树定义
所有树都必须实现Tree接口。Tree内部定义如下：
```
public interface Tree<T extends Node<V>, V extends ConstantDesc> {

    /**
     * 插入节点
     * @param node 节点数据
     * @return  如果插入成功了，返回当前节点；如果没有找到对应的父节点，返回null
     */
    TreeNode<T, V> insert(T node);

    /**
     * 批量插入节点。如果没有找到对应的节点，会将节点添加到根节点下
     * @param nodes 节点集合
     */
    NaryTree<T, V> insertAll(Collection<T> nodes);

    /**
     * 删除节点
     * @param key  根据节点标识删除节点
     * @return  删除节点的子节点
     */
    List<T> delete(V key);

    /**
     * 通过节点标识找到当前节点数据
     * @param key   节点标识
     * @return  对应的节点
     */
    TreeNode<T, V> search(V key);

    /**
     * 判断是否存在对应的节点
     * @param key   节点标识
     * @return  如果包含，返回true
     */
    boolean contains(V key);

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
    List<T> traverse(Consumer<TreeNode<T, V>> consumer);

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
    List<T> inOrderTraverse(Consumer<TreeNode<T, V>> consumer);

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
    List<T> postOrderTraverse(Consumer<TreeNode<T, V>> consumer);

    /**
     * 判断2个key是否相等
     * @param nodeKey       节点key
     * @param compareKey    比较key
     * @return  如果相等，返回true
     */
    default boolean equals(V nodeKey,V compareKey) {
        if (nodeKey == null || compareKey == null) {
            return false;
        }

        return nodeKey.equals(compareKey);
    }
}
```

## NaryTree 多叉树
多叉树允许每个节点可以有多个子节点。
### 定义节点
为了构建一颗树，需要先定义树的叶子节点数据。定义叶子节点需实现 `Node` 接口，然后实现其对应方法。
```
public class TestNode implements Node<Long> {

    private final Long id;
    private final Long parentId;

    public TestNode(Long id, Long parentId) {
        this.id = id;
        this.parentId = parentId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    @Override
    public long getPx() {
        return id;
    }
}
```
### 构建树
使用 `insertAll` 将元素插入到树中即可使用数据进行构建。
```
// 模拟一些数据
List<TestNode> list = new ArrayList<>();
for (int i = 0; i < 100; i++) {
    list.add(new TestNode((long) (i + 1), (long) i));
}

for (int i = 100; i < 300; i++) {
    Long parant = RandomUtils.random(100L);
    while (parant == (long) i) {
        parant = RandomUtils.random(100L);
    }
    list.add(new TestNode((long) i, parant));
}

// 构建树
Tree<TestNode, Long> naryTree = NaryTree.build();
naryTree.insertAll(list);
```

### 常用方法