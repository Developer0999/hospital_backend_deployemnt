package com.hospital.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.hospital.controller..*(..)) || execution(* com.hospital.service..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("Entering {}.{} with args={}", className, methodName, Arrays.toString(args));
        try {
            Object result = joinPoint.proceed();
            log.info("Exiting {}.{} with result={}", className, methodName, result);
            return result;
        } catch (Throwable ex) {
            log.error("Exception in {}.{} with message={}", className, methodName, ex.getMessage(), ex);
            throw ex;
        }
    }
}
