package com.spring.aop.aspect.factoryAOP;

import org.springframework.stereotype.Component;

@Component
public class PhoneFactory implements Factory {
    @Override
    public void make() {
        System.out.println("making phone");
    }

    @Override
    public void delivery(String address) {
        System.out.println("selling phone to " + address);
    }

    @Override
    public void testArgs(String address, int name) {

    }
}
