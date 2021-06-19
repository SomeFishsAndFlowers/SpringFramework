package com.jwl.spring.framework.beans;


/**
 * 保存创建后的对象实例，包括代理对象和原生对象
 * @author wenlo
 */
public class BeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public BeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    /**
     *
     * @return $Proxy0 if wrappedInstance was proxied.
     */
    public Class<?> getWrappedClass() {
        return wrappedInstance.getClass();
    }
}
