package com.jwl.spring.framework.aop.aspect;

import com.jwl.spring.framework.aop.intercept.MethodInterceptor;
import com.jwl.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author wenlo
 */
public class MethodBeforeAdvice extends AbstractAspectjAdvice implements Advice, MethodInterceptor {

    private JoinPoint joinPoint;

    public MethodBeforeAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(joinPoint, null, null);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        joinPoint = methodInvocation;
        before(methodInvocation.getMethod(), methodInvocation.getArguments(), methodInvocation.getThis());
        return methodInvocation.proceed();
    }
}
