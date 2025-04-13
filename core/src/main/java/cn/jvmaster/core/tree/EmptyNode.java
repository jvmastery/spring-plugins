package cn.jvmaster.core.tree;

import java.lang.constant.ConstantDesc;

/**
 * 空节点
 * @param id    节点ID
 * @param <V>   节点ID类型
 */
public record EmptyNode<V extends ConstantDesc>(V id) implements Node<V> {
    public static final EmptyNode<Long> DEFAULT_LONG_EMPTY_NODE = new EmptyNode<>(0L);
    public static final EmptyNode<Integer> DEFAULT_INT_EMPTY_NODE = new EmptyNode<>(0);

    public static <V extends ConstantDesc> EmptyNode<V> build(V id) {
        return new EmptyNode<>(id);
    }

    @Override
    public V getId() {
        return id;
    }

    @Override
    public V getParentId() {
        return null;
    }

    @Override
    public long getPx() {
        return 0;
    }
}
