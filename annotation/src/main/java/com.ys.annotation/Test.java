package com.ys.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yinshan on 2021/3/8.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Test {
}