package com.jwl.spring.framework.aop.intercept;

import com.jwl.spring.framework.aop.aspect.JoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wenlo
 */
public class MethodInvocation implements JoinPoint {

    private Object proxy;
    private Method method;
    private Object target;
    private Class<?> targetClass;
    private Object[] arguments;
    private List<Object> interceptorsAndDynamicMethodMatchers;

    private Map<String, Object> userAttributes;
    private int currentInterceptorIndex = -1;

    public MethodInvocation(Object proxy,
                            Object target,
                            Method method,
                            Object[] args,
                            Class<?> targetClass,
                            List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.method = method;
        this.target = target;
        this.targetClass = targetClass;
        this.arguments = args;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Object proceed() throws Throwable {
        if (currentInterceptorIndex == interceptorsAndDynamicMethodMatchers.size() - 1) {
            return method.invoke(target, arguments);
        }
        Object interceptorOrInterceptionAdvice = interceptorsAndDynamicMethodMatchers.get(++currentInterceptorIndex);
        if (interceptorOrInterceptionAdvice instanceof MethodInterceptor) {
            return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
        }
        else {
            return proceed();
        }
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (value != null) {
            if (userAttributes == null) {
                userAttributes = new HashMap<>(8);
            }
            userAttributes.put(key, value);
        }
        else {
            if (userAttributes != null) {
                userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return (userAttributes != null) ? userAttributes.get(key) : null;
    }
}
