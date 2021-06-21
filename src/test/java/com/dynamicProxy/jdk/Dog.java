package com.dynamicProxy.jdk;

public class Dog implements Animal {


    @Override
    public String eat(String food) {
        System.out.println("I am dog, I am eating " + food);
//        int a = 1/ 0;
        return "dog";
    }
}
