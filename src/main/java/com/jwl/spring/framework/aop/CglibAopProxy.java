package com.jwl.spring.framework.aop;

import com.jwl.spring.framework.aop.support.AdvisedSupport;

/**
 * @author wenlo
 */
public class CglibAopProxy implements AopProxy {


    private AdvisedSupport config;

    public CglibAopProxy(AdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
