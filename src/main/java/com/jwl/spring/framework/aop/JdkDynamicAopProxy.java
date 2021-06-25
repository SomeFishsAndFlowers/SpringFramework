package com.jwl.spring.framework.aop;

import com.jwl.spring.framework.aop.intercept.MethodInvocation;
import com.jwl.spring.framework.aop.support.AdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author wenlo
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private AdvisedSupport config;

    public JdkDynamicAopProxy(AdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(config.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, this.config.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        List<Object> interceptorsAndDynamicMethodMatchers
                = config.getInterceptorsAndDynamicInterceptionAdvice(method, config.getTargetClass());
        MethodInvocation invocation = new MethodInvocation(proxy,
                                                            config.getTarget(),
                                                            method,
                                                            args,
                                                            config.getTargetClass(),
                                                            interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
