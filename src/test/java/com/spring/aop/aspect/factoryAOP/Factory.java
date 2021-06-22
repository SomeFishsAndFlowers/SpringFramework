package com.spring.aop.aspect.factoryAOP;

public interface Factory {

    void make();

    void delivery(String address);

    void testArgs(String address, int name);
}
