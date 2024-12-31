package cn.springhub.base.tree;

import java.util.List;

/**
 * 树节点
 * @author AI
 * @date 2024/12/26 15:37
 * @version 1.0
**/
public interface TreeNode<T> {

    /**
     * 获取节点内容
     * @return  节点内容
     */
    T getData();

    /**
     * 获取节点所有子节点
     * @return  子节点
     */
    List<? extends TreeNode<T>> getChildren();
}
