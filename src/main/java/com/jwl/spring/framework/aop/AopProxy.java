package com.jwl.spring.framework.aop;

/**
 * @author wenlo
 */
public interface AopProxy {
    /**
     * 获取代理对象
     * @return 代理对象
     */
    Object getProxy();

    /**
     * 获取代理对象
     * @param classLoader classLoader
     * @return 代理对象
     */
    Object getProxy(ClassLoader classLoader);

}
