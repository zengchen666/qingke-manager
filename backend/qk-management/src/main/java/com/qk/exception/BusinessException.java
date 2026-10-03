package com.qk.exception;

/**
 * 自定义业务异常
 * 用于抛出可预期的业务错误（如用户名不存在、密码错误等），
 * 由 GlobalExceptionHandler 捕获并转换为统一的 Result 响应
 */
public class BusinessException extends RuntimeException {

    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }
}
