package com.skies.utils;

import com.alibaba.fastjson.JSONObject;
import com.skies.bean.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局捕获异常
 */
@ControllerAdvice(basePackages = "cn.com.cennavi.controller")
@Slf4j
public class ExceptionMsg {

    /**
     * 捕获运行时异常
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Object toString(HttpServletRequest httpServletRequest, Exception e) {
        ResultBean resultBean = new ResultBean();
        resultBean.setStateCode(CommUtils.ERRCODE);
        log.error("url:{}---exception:{}",httpServletRequest.getRequestURL(),e.getMessage(),e);
        resultBean.setMessage(e.toString());
        return JSONObject.toJSON(resultBean).toString();
    }
}
