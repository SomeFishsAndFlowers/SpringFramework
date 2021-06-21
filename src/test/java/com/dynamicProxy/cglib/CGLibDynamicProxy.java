package com.dynamicProxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class CGLibDynamicProxy {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Cat.class);
        enhancer.setCallback((MethodInterceptor) (proxy, method, objects, methodProxy) -> {
            System.out.println("----before----");
            Object result = methodProxy.invokeSuper(proxy, objects);
//                Object result = method.invoke(proxy, objects);
            System.out.println("----after----");
            return result;
        });

        Cat cat = (Cat) enhancer.create();
        cat.cry();
    }
}
