package com.skies.utils.crudUtils;

import java.lang.annotation.*;

/**
 * //使用方式，在需要忽略的字段上 @IgnoreColumn("")
 * Created by Admin on 2018/5/5.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface IgnoreColumn {
    String value();
}