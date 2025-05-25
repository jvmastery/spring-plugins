package cn.jvmaster.core.exception;

import cn.jvmaster.core.constant.Code;

/**
 * 数据断言错误
 * @author AI
 * @date 2025/5/21 9:51
 * @version 1.0
**/
public class AssertException extends SystemException {

    public AssertException(String message) {
        super(Code.ASSERT_ERROR, message);
    }
}
