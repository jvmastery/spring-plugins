package cn.jvmaster.spring.configuration;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.domain.BaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局统一格式兴业结果处理
 * @author AI
 * @date 2024/7/30 21:30
 */
@RestControllerAdvice
@ConditionalOnMissingBean(name = "customResponseResultConfiguration")
public class ResponseResultConfiguration implements ResponseBodyAdvice<Object> {
    private static final Logger log = LoggerFactory.getLogger(ResponseResultConfiguration.class);
    private final ObjectMapper objectMapper;

    public ResponseResultConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * exception异常处理
     * @param e 异常信息
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse<?> exception(Exception e) {
        log.error(e.getMessage(), e);
        return new BaseResponse<>(Code.ERROR, e.getMessage());
    }

    /**
     * 确定是否自动格式化返回数据
     * @param methodParameter the return type
     * @param converterType the selected converter type
     */
    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Class converterType) {
        return true;
    }

    /**
     * 返回数据格式化处理
     * @param body the body to be written
     * @param returnType the return type of the controller method
     * @param selectedContentType the content type selected through content negotiation
     * @param selectedConverterType the converter type selected to write to the response
     * @param request the current request
     * @param response the current response
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                @NonNull MethodParameter returnType,
                                @NonNull MediaType selectedContentType,
                                @NonNull Class selectedConverterType,
                                @NonNull ServerHttpRequest request,
                                @NonNull ServerHttpResponse response) {
        if (body instanceof BaseResponse) {
            return body;
        }

        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(BaseResponse.success(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return BaseResponse.success(body);
    }
}
