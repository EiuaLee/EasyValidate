package com.eiualee.easyvalidate_compiler.bean;

import java.util.List;

/**
 * Created by liweihua on 2018/12/24.
 */

public class BaseValidateBean {

    private int id;
    private String toast;
    private List<Integer> plans;
    private String elementType; //控件类型
    private String fieldName;//控件 tv_pass


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToast() {
        return toast;
    }

    public void setToast(String toast) {
        this.toast = toast;
    }

    public List<Integer> getPlans() {
        return plans;
    }

    public void setPlans(List<Integer> plans) {
        this.plans = plans;
    }
}
