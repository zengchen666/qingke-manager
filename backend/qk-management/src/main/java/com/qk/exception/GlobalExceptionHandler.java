package com.qk.exception;

import com.qk.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice  // 标识当前是一个异常处理的类
public class GlobalExceptionHandler {

    /**
     * 处理业务异常（已知的可预期错误，直接把异常信息响应给前端）
     */
    @ExceptionHandler
    public Result businessExceptionHandler(BusinessException e) {
        log.error("业务异常={}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理唯一键冲突（如部门名称重复、用户名重复）
     */
    @ExceptionHandler
    public Result duplicateKeyExceptionHandler(DuplicateKeyException e) {
        String message = e.getMessage();
        if (message != null && message.contains("Duplicate entry")) {
            String duplicateEntry = message.substring(message.lastIndexOf("Duplicate entry"));
            String[] split = duplicateEntry.split(" ");
            String str = split[2];
            return Result.error("抱歉:" + str + "字段重复,请修改后重试");
        } else {
            return Result.error("对不起, 服务器异常, 请稍后重试");
        }
    }

    /**
     * 兜底：处理其他所有未知异常
     */
    @ExceptionHandler  // 声明一个方法来处理特定类型的异常。当 Controller 层抛出异常时，Spring 会自动找到匹配的 @ExceptionHandler 方法来处理该异常。
    public Result exceptionHandler(Exception e) {
        log.error("程序报错={}", e.getMessage(), e);
        return Result.error("对不起, 服务器异常, 请稍后重试");
    }

}
