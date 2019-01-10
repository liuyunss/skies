package com.skies.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 捕获web请求日志
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {

    @Pointcut("execution(public * com.skies.controller.*.*(..))")
    public void weblog(){
    }

    @Before("weblog()")
    public void before() throws Throwable{
        //接收到请求，记录请求内容
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        log.info("url:{}---method:{}---ip:{}",request.getRequestURI(),request.getMethod(),request.getRemoteAddr());
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()){
            String name = parameterNames.nextElement();
            log.info("name:{},value:{}",name,request.getParameter(name));
        }
    }

    @AfterReturning(returning = "obj",pointcut = "weblog()")
    public void after(Object obj){
        //处理完，返回内容
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        log.info("url:{}---value:{}",request.getRequestURI(),obj);
    }
}
