package cn.jvmaster.spring.configuration;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.domain.BaseResponse;
import cn.jvmaster.core.exception.SystemException;
import cn.jvmaster.spring.util.ValidatorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理
 * @author AI
 * @date 2025/6/3 17:52
 * @version 1.0
**/
@RestControllerAdvice
@ConditionalOnMissingBean(name = "customExceptionHandlerConfiguration")
public class ExceptionHandlerConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerConfiguration.class);

    /**
     *  全局通用类异常
     * @param e 异常信息
     */
    @ExceptionHandler(value = Exception.class)
    public BaseResponse<?> defaultErrorHandler(Exception e)  {
        log.error(e.getMessage(), e);
        return BaseResponse.error(Code.ERROR, "数据处理失败，请重新进行尝试！", e.getMessage());
    }

    /**
     *  参数格式转换异常
     * @param e 异常信息
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public BaseResponse<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        return BaseResponse.error(Code.ERROR, "参数格式错误，请使用正确格式[" + e.getName() + "]", e.getCause() == null ? null : e.getCause().toString());
    }

    /**
     *  自定义系统类异常
     * @param e 异常信息
     */
    @ExceptionHandler(value = SystemException.class)
    public BaseResponse<?> systemErrorHandler(SystemException e) {
        log.error(e.getMessage(), e);
        return BaseResponse.build(e.getCode(), e.getMessage(), e.getCause() == null ? e.getMessage() : e.getCause().toString());
    }

    /**
     * 参数校验错误
     * @param ex 异常信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handleValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        return BaseResponse.error(Code.ERROR, ValidatorUtils.validResult(ex.getBindingResult(), false), ex.getMessage());
    }
}
