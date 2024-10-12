package com.lms.lms.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Aspect
@EnableAspectJAutoProxy
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.lms.lms.modules.AppUserManagement.AppUserService.loadUserByUsername(..)) && args(username)")
    public void userLoadByUsername(String username) {}

    @Before("userLoadByUsername(username)")
    public void logBeforeUserLoad(JoinPoint joinPoint, String username) {
        logger.info("Attempting to load user by username: {}", username);
    }

    @AfterReturning(pointcut = "userLoadByUsername(username)", returning = "result")
    public void logAfterUserLoad(JoinPoint joinPoint, String username, Object result) {
        if (result != null) {
            logger.info("User loaded successfully with username: {}", username);
        } else {
            logger.warn("User not found with username: {}", username);
        }
    }

    @Pointcut("execution(* com.lms.lms.Controller.RegistrationController.login(..))")
    public void loginMethod() {}

    @Before("loginMethod()")
    public void logBeforeLogin(JoinPoint joinPoint) {
        logger.info("Attempting to login");
    }


    @AfterReturning(pointcut = "loginMethod()", returning = "result")
    public void logAfterLogin(JoinPoint joinPoint, Object result) {
        logger.info("Login successful, redirecting to: {}", result);
    }


    @AfterThrowing(pointcut = "loginMethod()", throwing = "exception")
    public void logAfterLoginException(JoinPoint joinPoint, Throwable exception) {
        logger.error("Login failed with exception: {}", exception.getMessage());
    }
}
