package com.skies.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局捕获异常
 */
@ControllerAdvice(basePackages = "cn.skies.controller")
@Slf4j
public class ExceptionMsg {

    /**
     * 捕获运行时异常
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Object toString(HttpServletRequest httpServletRequest,Exception e) {
        log.error("url:{}---exception:{}",httpServletRequest.getRequestURL(),e.getMessage(),e);
        return e.toString();
    }
}
