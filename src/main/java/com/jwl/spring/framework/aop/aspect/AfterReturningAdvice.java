package com.jwl.spring.framework.aop.aspect;

import com.jwl.spring.framework.aop.intercept.MethodInterceptor;
import com.jwl.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author wenlo
 */
public class AfterReturningAdvice extends AbstractAspectjAdvice implements Advice, MethodInterceptor {

    private JoinPoint joinPoint;

    public AfterReturningAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(joinPoint, returnValue, null);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object retVal = methodInvocation.proceed();
        joinPoint = methodInvocation;
        afterReturning(retVal, methodInvocation.getMethod(), methodInvocation.getArguments(), methodInvocation.getThis());
        return retVal;
    }
}
