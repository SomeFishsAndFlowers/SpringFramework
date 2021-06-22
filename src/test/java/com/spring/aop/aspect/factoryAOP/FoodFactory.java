package com.spring.aop.aspect.factoryAOP;

import org.springframework.stereotype.Component;

@Listen
@Component
public class FoodFactory implements Factory {


    @Override
    public void make() {
        System.out.println("making food");
    }

    @Override
    public void delivery(String address) {
        System.out.println("selling food to " + address);
    }

    @Override
    public void testArgs(String address, int name) {
        System.out.println("testArgs: " + address);
    }

    public void testArgsAnnotation(FoodFactory freshFactory) {

    }
}
