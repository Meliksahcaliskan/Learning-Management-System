/*
package com.lsm.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.lsm.controller.*.*(..))")
    public void controllerMethods() {
    }

    @Pointcut("execution(* com.lsm.service.*.*(..))")
    public void serviceMethods() {
    }

    @Pointcut("execution(* com.lsm.repository.*.*(..))")
    public void repositoryMethods() {
    }

    @Pointcut("execution(* com.lsm.security.*.*(..))")
    public void securityMethods() {
    }

    @Pointcut("execution(* com.lsm.model.validation.*.*(..))")
    public void validationMethods() {
    }

    @Before("controllerMethods()")
    public void logBeforeControllerMethods(JoinPoint joinPoint) {
        logger.info("Entering method: {} with arguments: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @Around("serviceMethods() || repositoryMethods() || securityMethods() || validationMethods()")
    public Object logAroundMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            logger.info("Executed method: {} in {} ms", joinPoint.getSignature().getName(), endTime - startTime);
            return result;
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();
            logger.error("Exception in method: {} after {} ms with error: {}", joinPoint.getSignature().getName(), endTime - startTime, ex.getMessage(), ex);
            throw ex;  // Ensure the exception is propagated
        }
    }

}
*/