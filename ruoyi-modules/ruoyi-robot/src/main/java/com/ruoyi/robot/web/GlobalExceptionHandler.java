package com.ruoyi.robot.web;

import com.ruoyi.common.core.web.domain.AjaxResult;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400 参数问题
    public AjaxResult handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors()
                .stream().findFirst()
                .map(e -> e.getField() + ":" + e.getDefaultMessage())
                .orElse("参数校验失败");
        return AjaxResult.error(400, msg).put("traceId", traceId());
    }

    @ExceptionHandler(org.springframework.web.client.HttpStatusCodeException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY) // 502 下游 HTTP 错
    public AjaxResult handleDownstream(org.springframework.web.client.HttpStatusCodeException ex) {
        return AjaxResult.error(502, "下游HTTP错误:" + ex.getStatusCode())
                .put("traceId", traceId());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500 兜底
    public AjaxResult handleAny(Exception ex) {
        return AjaxResult.error(500, "服务异常:" + ex.getClass().getSimpleName())
                .put("traceId", traceId());
    }

    private String traceId() { return MDC.get("traceId"); }
}
