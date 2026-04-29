package com.example.user_service.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 29.04.2026
 * Description: this class describes an aspect that intercepts the methods of other classes for logging
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger("");

    private final String packagesToLog = "within(com.example.user_service.controller.*) || " +
            "within(com.example.user_service.kafka.*) ||" +
            "within(com.example.user_service.service.*)";

    /**
     * @ Method Name: logMethodEntry
     * @ Description: intercepts the method before it was executed,
     * and logs the class name, the method name and arguments of the intercepted method
     * @ param      : [org.aspectj.lang.JoinPoint]
     * @ return     : void
     */
    @Before(packagesToLog)
    public void logMethodEntry(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("Class: {}, starts method:{}, arguments: {}", className, methodName, args);
    }

    /**
     * @ Method Name: logMethodExit
     * @ Description: intercepts the method after it was executed successfully,
     * and logs the class name, the method name and results of the intercepted method
     * @ param      : [org.aspectj.lang.JoinPoint, java.lang.Object]
     * @ return     : void
     */
    @AfterReturning(pointcut = packagesToLog, returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("Class: {}, finish method: {}, result: {}", className, methodName, result);
    }

    /**
     * @ Method Name: logException
     * @ Description: intercepts the method after it was failed,
     * and logs the class name, the method name of the intercepted method and the exception message
     * @ param      : [org.aspectj.lang.JoinPoint, java.lang.Exception]
     * @ return     : void
     */
    @AfterThrowing(pointcut = packagesToLog, throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.warn("Class: {}, method: {}, exception: {}", className, methodName, exception.getMessage());
    }

    /**
     * @ Method Name: logDebud
     * @ Description: intercepts the method with logging level DEBUG and logs it proceeding
     * @ param      : [org.aspectj.lang.ProceedingJoinPoint]
     * @ return     : java.lang.Object
     */
    @Around("within(com.example.user_service.assembler.UserControllerAssembler) || " +
            "within(com.example.user_service.repository.UserRepository)")
    public Object logDebud(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("Class: {}, starts method:{}, arguments: {}", className, methodName, joinPoint.getArgs());

        Object result = joinPoint.proceed();

        log.debug("Class: {}, finish method: {}, result: {}", className, methodName, result);
        return result;
    }
}
