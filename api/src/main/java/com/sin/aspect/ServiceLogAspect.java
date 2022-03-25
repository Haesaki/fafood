package com.sin.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {
    /**
     * Aop:
     * 1. 前置通知，方法调用之前执行
     * 2. 后置通知，方法正常调用之后执行
     * 3. 环绕通知，方法调用之前和之后都执行
     * 4. 异常通知，发生异常，执行
     * 5. 最终通知，方法调用完成之后执行
     */

    public static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * 切面表达式
     * 第一处 代表方法放回类型 * 代表任何类型
     * 第二处 包名，AOP监控所在的包名子
     * 第三处 ..达标该包以及其子包下面的所有类方法
     * 第四处 * 代表类名,*代表所有类
     * 第五初 *(..) *代表类中的所有方法名, (..) 代表方法中所有的参数
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.sin.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("开始执行: {} {}",
                joinPoint.getTarget().getClass(),
                joinPoint.getSignature().getName());
        long begin = System.currentTimeMillis();
        // 执行目标的方法
        Object ob = joinPoint.proceed();

        long end = System.currentTimeMillis();
        if((end - begin) > 3000)
            log.error("执行结束: 耗时 {} 毫秒,", end - begin);
        return ob;
    }
}