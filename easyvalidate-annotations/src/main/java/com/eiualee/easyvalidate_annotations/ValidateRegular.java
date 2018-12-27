package com.eiualee.easyvalidate_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by liweihua on 2018/12/21.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface ValidateRegular {

    int id();//控件ID

    String toast();//不合法时提示的内容

    int[] plan() default {Plan.DEFAULT};//校验计划

    String regular();

}
