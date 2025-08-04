package cn.cug.sxy.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @version 1.0
 * @Date 2025/7/30 10:37
 * @Description
 * @Author jerryhotton
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获验证失败的异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        // 获取验证失败的结果
        BindingResult bindingResult = ex.getBindingResult();

        // 将错误信息封装为简单的字符串
        String errorMessage = bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", ")); // 可以将多个错误信息合并成一个字符串
        // 返回统一的错误响应格式
        ErrorResponse errorResponse = new ErrorResponse("error", errorMessage);
        // 返回 400 状态码和错误信息
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 错误响应结构
    public static class ErrorResponse {

        private String status;
        private String message;

        public ErrorResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

}
