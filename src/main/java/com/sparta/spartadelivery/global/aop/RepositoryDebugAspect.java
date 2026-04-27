package com.sparta.spartadelivery.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class RepositoryDebugAspect {

    @Before("execution(* com.sparta.spartadelivery..*Repository.*(..))")
    public void logRepoCall(JoinPoint jp) {

        log.info("🔥 Repo 호출: {}", jp.getSignature());

        Arrays.stream(Thread.currentThread().getStackTrace())
                .forEach(s -> log.info("   at {}", s));
    }
}