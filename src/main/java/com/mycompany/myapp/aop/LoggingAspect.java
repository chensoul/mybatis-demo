package com.mycompany.myapp.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.config.AppConstants;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Aspect
public class LoggingAspect {
    private final Environment env;
    private final ObjectMapper objectMapper;

    public LoggingAspect(Environment env, ObjectMapper objectMapper) {
        this.env = env;
        this.objectMapper = objectMapper;
    }

    @Pointcut("within(@org.springframework.stereotype.Repository *)"
            + " || within(@org.springframework.stereotype.Service *)"
            + " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the
        // advices.
    }

    @Pointcut("within(com.mycompany.myapp..*.*Repository)" + " || within(com.mycompany.myapp..*.*Service)"
            + " || within(com.mycompany.myapp..*.*Controller)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the
        // advices.
    }

    private Logger logger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        if (env.acceptsProfiles(Profiles.of(AppConstants.PROFILE_NOT_PROD))) {
            logger(joinPoint)
                    .error(
                            "Exception in {}() with cause = '{}' and exception = '{}'",
                            joinPoint.getSignature().getName(),
                            e.getCause() != null ? e.getCause() : "NULL",
                            e.getMessage(),
                            e);
        } else {
            logger(joinPoint)
                    .error(
                            "Exception in {}() with cause = {}",
                            joinPoint.getSignature().getName(),
                            e.getCause() != null ? String.valueOf(e.getCause()) : "NULL");
        }
    }

    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = logger(joinPoint);
        if (log.isDebugEnabled()) {
            log.debug(
                    "Enter {}() with arguments = {}",
                    joinPoint.getSignature().getName(),
                    objectMapper.writeValueAsString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.debug(
                        "Exit {}() with result = {}",
                        joinPoint.getSignature().getName(),
                        objectMapper.writeValueAsString(result));
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error(
                    "Illegal argument: {} in {}()",
                    objectMapper.writeValueAsString(joinPoint.getArgs()),
                    joinPoint.getSignature().getName());
            throw e;
        }
    }
}
