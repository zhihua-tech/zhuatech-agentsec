/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agentsec.common;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @ExceptionHandler(BusinessException.class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiResponse<Void> business(BusinessException ex) { return ApiResponse.error(ex.getMessage()); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiResponse<Void> validation(MethodArgumentNotValidException ex) {
        return ApiResponse.error(ex.getBindingResult().getFieldErrors().stream().findFirst().map(e -> e.getDefaultMessage()).orElse("请求参数不正确"));
    }
}
