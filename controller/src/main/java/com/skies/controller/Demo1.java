package com.skies.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by SKIES on 2019/8/14.
 */
public class Demo1 {


    public static void main(String[] args) {

//        Map<String,Object> map = Maps.newHashMap();
//        map.put("1",00);
//        Map<String,Object> map2 = ImmutableMap.of("1", "one", "2", "two");
//        System.out.println(map2.size());
//
//        Map<String,Object> map3 = new HashMap<String,Object>() {
//            {
//                put("11",0);
//                put("22","AA");
//            }
//        };
//        System.out.println(map3);


//        List<Integer> listInt = Lists.newArrayList(1,2,3,4,5,6,7,8,9);
//        long count = listInt.stream().filter(n -> n % 2 == 1).count();
//        System.out.println(count);
//        List<Integer> collect = listInt.stream().filter(n -> n % 2 == 1).collect(Collectors.toList());
//        collect.forEach(n-> System.out.println(n));
//        listInt.forEach(n-> System.out.println(n));

//        String[] demo = {"a","B","c","b","0"};
////        Arrays.sort(demo,(a,b)->a.compareTo(b));
//        Arrays.sort(demo,String::compareTo);
//        Arrays.stream(demo).forEach(s -> System.out.println(s));



    }


    interface demo{
        void syso(String demo);
    }




}
