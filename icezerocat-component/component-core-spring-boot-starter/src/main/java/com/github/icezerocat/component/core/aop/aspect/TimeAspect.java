package com.github.icezerocat.component.core.aop.aspect;

import com.github.icezerocat.component.core.aop.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;

/**
 * @author 0.0.0
 * ProjectName: [easyexcel]
 * Package: [com.excel.easyexcel.config.aop.TimeAspect]
 * Description 时间记录切面，收集接口的运行时间
 * date 2020/3/13 9:51
 */
@Slf4j
@Aspect
@Component
public class TimeAspect {
    /**
     * 切入点：修正Timer注解的全局唯一限定符
     */
    @Pointcut("@annotation(com.github.icezerocat.component.core.aop.Timer)")
    private void pointcut() {
    }

    @Around("pointcut() && @annotation(timer)")
    public Object around(ProceedingJoinPoint joinPoint, Timer timer) throws Throwable {

        //注解描述
        String description = timer.description();
        description = StringUtils.isEmpty(description) ? "" : ("-描述：" + description);

        //方法参数
        String args = Arrays.toString(joinPoint.getArgs());
        args = args.replaceFirst("\\[", "(").substring(0, args.length() - 1) + ")";

        // 获取目标类名称
        String clazzName = joinPoint.getTarget().getClass().getName();

        // 获取目标类方法名称
        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        log.debug("[{}]{} {}{}: start {}", clazzName, description, methodName, args, new Date());

        // 调用目标方法
        Object result = joinPoint.proceed();

        long time = System.currentTimeMillis() - start;
        log.debug("[{}]{} {}{}: end {} cost time: {} ms", clazzName, description, methodName, args, new Date(), time);

        return result;
    }
}
