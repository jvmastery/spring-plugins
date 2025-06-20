package cn.jvmaster.spring.controller;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.domain.BaseResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 自定义error处理器，直接抛出异常，让全局处理器处理
 * @author AI
 * @date 2025/6/17 14:40
 * @version 1.0
**/
@RestController
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class CustomErrorController extends AbstractErrorController {

    public CustomErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping
    public ResponseEntity<?> handleError(HttpServletRequest request) throws Throwable {
        HttpStatus status = this.getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        } else {
            Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
            return new ResponseEntity<>(BaseResponse.error(Code.ERROR, "数据处理失败，请重新进行尝试！", throwable.getMessage()), status);
        }
    }

}
