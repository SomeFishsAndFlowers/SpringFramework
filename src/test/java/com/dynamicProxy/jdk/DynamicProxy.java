package com.dynamicProxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class DynamicProxy {

    public static void main(String[] args) {
        Dog dog = new Dog();

        Animal proxyInstance = (Animal) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Animal.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(method);
//                System.out.println(proxy); stackOverflow 实际调用的是toString，toString方法也是被代理了
//                System.out.println(proxy.getClass().getName()); 正常
//                proxy.toString(); stackOverflow
                System.out.println(Arrays.toString(args));
                Object result = null;
                try {
                    result = method.invoke(dog, args);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                System.out.println("proxy");
                return result;
            }
        });

        String eat = proxyInstance.eat("aaa");
        System.out.println(eat);

        String s = proxyInstance.toString();
        System.out.println(s);
    }

}
