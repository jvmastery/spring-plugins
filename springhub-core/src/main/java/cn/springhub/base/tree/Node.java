package cn.springhub.base.tree;

import java.lang.constant.ConstantDesc;

/**
 * 节点接口，构成树的节点必须实现该接口
 * @author AI
 * @date 2024/12/24 14:17
 * @version 1.0
**/
public interface Node<T extends ConstantDesc> {

    /**
     * 获取主键标识
     * @return  标识
     */
    T getId();

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
