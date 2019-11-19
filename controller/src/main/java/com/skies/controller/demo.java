package com.skies.controller;

/**
 * Created by SKIES on 2019/8/19.
 */
public interface demo {
    default void print(){
        System.out.println("接口方法");
    }

    public String demo1();
}
