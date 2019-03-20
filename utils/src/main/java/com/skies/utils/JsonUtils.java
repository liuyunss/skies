package com.skies.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * Created by Admin on 2017-4-24.
 */
public class JsonUtils {

    /**
     * 序列化对象
     *
     * @param obj  对象
     * @param args 排除字段
     * @return String
     */
    public static String objectToJson(Object obj, String[] args) {
        //属性过滤器对象
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();

        //属性排斥集合,强调某些属性不需要或者一定不能被序列化
        Set<String> excludes = filter.getExcludes();

        //属性包含集合,强调仅需要序列化某些属性.具体用哪一个,看实际情况.此处我用的前者
        //Set<String> includes = filter.getIncludes();

        //排除不需序列化的属性
        for (String string : args) {
            excludes.add(string);
        }

        //调用fastJson的方法,对象转json,
        //参数一:需要被序列化的对象
        //参数二:用于过滤属性的过滤器
        //参数三:关闭循环引用,若不加这个,页面无法展示重复的属性值
        String string = JSONObject.toJSONString(obj, filter, SerializerFeature.WriteMapNullValue, SerializerFeature.DisableCircularReferenceDetect);
        string = jsonFormat(string);//updated sunpengyan
        return string;
    }

    public static String jsonFormat(String datas) {
        return datas.replace("\\\"","\"").replace("\"{","{").replace("}\"","}").replace("\"[","[").replace("]\"","]");
    }

    /**
     * 序列化对象
     *
     * @param obj  对象
     * @param args 保留字段
     * @return String
     */
    public static String inObjectToJson(Object obj, String[] args) {
        //属性过滤器对象
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        //属性包含集合,强调仅需要序列化某些属性.具体用哪一个,看实际情况.此处我用的前者
        Set<String> includes = filter.getIncludes();

        //排除不需序列化的属性
        for (String string : args) {
            includes.add(string);
        }

        //调用fastJson的方法,对象转json,
        //参数一:需要被序列化的对象
        //参数二:用于过滤属性的过滤器
        //参数三:关闭循环引用,若不加这个,页面无法展示重复的属性值
        String string = JSONObject.toJSONString(obj, filter, SerializerFeature.WriteMapNullValue);
        return string;
    }

    public static String objectToJson(Object obj, String[] args, String callback) {
        //属性过滤器对象
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();

        //属性排斥集合,强调某些属性不需要或者一定不能被序列化
        Set<String> excludes = filter.getExcludes();

        //属性包含集合,强调仅需要序列化某些属性.具体用哪一个,看实际情况.此处我用的前者
        //Set<String> includes = filter.getIncludes();

        //排除不需序列化的属性
        for (String string : args) {
            excludes.add(string);
        }

        //调用fastJson的方法,对象转json,
        //参数一:需要被序列化的对象
        //参数二:用于过滤属性的过滤器
        //参数三:关闭循环引用,若不加这个,页面无法展示重复的属性值
        String string = JSONObject.toJSONString(obj, filter, SerializerFeature.WriteMapNullValue, SerializerFeature.DisableCircularReferenceDetect);
        if (!StringUtils.isEmpty(callback)) {
            string = callback + "(" + string + ")";
        }
        return string;
    }
}
