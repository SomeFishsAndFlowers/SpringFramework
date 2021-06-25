package com.jwl.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * 连接点
 * @author wenlo
 */
public interface JoinPoint {
    /**
     * 业务方法本身（需要增强的方法）
     * @return 业务方法本身
     */
    Method getMethod();

    /**
     * 业务方法参数列表
     * @return 业务方法参数列表
     */
    Object[] getArguments();

    /**
     * 业务方法所属的实例对象
     * @return 业务方法所属的实例对象
     */
    Object getThis();

    /**
     * 在joinPoint对象中添加自定义属性
     * @param key 属性的key
     * @param value 属性的value
     */
    void setUserAttribute(String key, Object value);

    /**
     * 获取自定义属性value
     * @param key key
     * @return value
     */
    Object getUserAttribute(String key);
}
