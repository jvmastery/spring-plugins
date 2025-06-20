package cn.jvmaster.spring.util;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.exception.SystemException;
import cn.jvmaster.core.util.StringUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Set;

/**
*  验证失败工具类
* @author         艾虎
* @date      2018/7/11 13:47
* @version       1.0
*/
public class ValidatorUtils {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     *  通过验证结果构建错误信息
     * @param result    为null时，表示无异常，否则返回异常数据
     * @param isShowAll 是否显示所有错误信息
     */
    public static String validResult(BindingResult result, boolean isShowAll) {
        if(result.hasErrors()) {
            // 获取所有验证失败的异常
            List<ObjectError> errors = result.getAllErrors();
            StringBuilder sb = new StringBuilder();
            for (ObjectError error : errors) {
                if (!isShowAll) {
                    return error.getDefaultMessage();
                }

                if(!sb.isEmpty()) {
                    sb.append(",");
                }
                sb.append(error.getDefaultMessage());
            }

            return sb.toString();
        }

        return null;
    }

    /**
     * 校验对象是否合法
     * @param obj obj
     * @param groups groups
     */
    public static void check(Object obj, Class<?>... groups) {
        check(obj, null, groups);
    }

    /**
     *  校验对象是否合法
     * @param obj obj
     * @param listIndex 集合的行号
     */
    public static void check(Object obj, Integer listIndex, Class<?>... groups) {
        if (obj == null) {
            return;
        }

        Set<ConstraintViolation<Object>> validResult =  VALIDATOR.validate(obj, groups);
        if (null != validResult && !validResult.isEmpty()) {
            for (ConstraintViolation<Object> constraintViolation : validResult) {
                String errorMsg = constraintViolation.getMessage();

                throw new SystemException(Code.ASSERT_ERROR, (listIndex == null ? "" : "第" + listIndex + "行") + (
                    StringUtils.isNotEmpty(errorMsg) ? errorMsg : constraintViolation.getPropertyPath().toString()));
            }
        }
    }
}
