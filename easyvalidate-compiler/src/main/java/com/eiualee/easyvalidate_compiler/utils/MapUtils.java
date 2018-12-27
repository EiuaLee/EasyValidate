package com.eiualee.easyvalidate_compiler.utils;

import com.eiualee.easyvalidate_annotations.ValidateCheck;
import com.eiualee.easyvalidate_annotations.ValidateNull;
import com.eiualee.easyvalidate_annotations.ValidateRegular;
import com.eiualee.easyvalidate_compiler.bean.BaseValidateBean;
import com.eiualee.easyvalidate_compiler.bean.ValidateCheckBean;
import com.eiualee.easyvalidate_compiler.bean.ValidateNullBean;
import com.eiualee.easyvalidate_compiler.bean.ValidateRegularBean;
import com.google.common.primitives.Ints;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

/**
 * Created by liweihua on 2018/12/24.
 */

public class MapUtils {


    /**
     * 获取注解数据
     *
     * @param roundEnv
     * @param clazz
     * @param groupByClassMap 该集合主要储存哪个Activity或Fragment使用到注解
     * @param <T>
     * @return
     */
    public static <T extends BaseValidateBean> Map<String, List<T>> getAnnotationDataGroupByClassName(RoundEnvironment roundEnv, Class<? extends Annotation> clazz, Map<String, Element> groupByClassMap) {
        Map<String, List<T>> listMap = new LinkedHashMap<>();
        iteratorAnnotation(roundEnv, clazz, new IteratorAnnotation() {
            @Override
            public <E extends Annotation> void iterator(E e, Element element) {
                //获取注解最里面的元素信息 即 "com.xxx.xx.xx.MianActivity" element.getEnclosingElement(): com.xxx.xx.xx.MianActivity 元素
                String enclosingElementStr = element.getEnclosingElement().toString();

                listMap.computeIfAbsent(enclosingElementStr, k -> new ArrayList<>());
                groupByClassMap.computeIfAbsent(enclosingElementStr, k -> element.getEnclosingElement());

                List<T> list = listMap.get(enclosingElementStr);
                if (e instanceof ValidateNull) {
                    list.add((T) validateNullHandle((ValidateNull) e, element));
                } else if (e instanceof ValidateCheck) {
                    list.add((T) validateCheckHandle((ValidateCheck) e, element));
                } else if (e instanceof ValidateRegular) {
                    list.add((T) validateRegularHandle((ValidateRegular) e, element));
                }
                listMap.put(enclosingElementStr, list);


            }
        });
        return listMap;
    }

    /**
     * 提取ValidateRegular注解的数据并返回实体类
     *
     * @param validateRegular
     * @param element
     * @return
     */
    private static ValidateRegularBean validateRegularHandle(ValidateRegular validateRegular, Element element) {
        ValidateRegularBean validateRegularBean = new ValidateRegularBean();
        validateRegularBean.setId(validateRegular.id());
        validateRegularBean.setPlans(Ints.asList(validateRegular.plan()));
        validateRegularBean.setToast(validateRegular.toast());
        validateRegularBean.setRegular(validateRegular.regular());
        validateRegularBean.setElementType(element.asType().toString());
        validateRegularBean.setFieldName(element.getSimpleName().toString());
        return validateRegularBean;
    }

    /**
     * 提取ValidateCheck注解的数据并返回实体类
     *
     * @param validateCheck
     * @param element
     * @return
     */
    private static ValidateCheckBean validateCheckHandle(ValidateCheck validateCheck, Element element) {
        ValidateCheckBean validateCheckBean = new ValidateCheckBean();
        validateCheckBean.setId(validateCheck.id());
        validateCheckBean.setPlans(Ints.asList(validateCheck.plan()));
        validateCheckBean.setToast(validateCheck.toast());
        validateCheckBean.setValidateState(validateCheck.validateState());
        validateCheckBean.setElementType(element.asType().toString());
        validateCheckBean.setFieldName(element.getSimpleName().toString());
        return validateCheckBean;
    }

    /**
     * 提取ValidateNull注解的数据并返回实体类
     *
     * @param validateNull
     * @param element
     * @return
     */
    private static ValidateNullBean validateNullHandle(ValidateNull validateNull, Element element) {
        ValidateNullBean validateNullBean = new ValidateNullBean();
        validateNullBean.setId(validateNull.id());
        validateNullBean.setPlans(Ints.asList(validateNull.plan()));
        validateNullBean.setToast(validateNull.toast());
        validateNullBean.setElementType(element.asType().toString());
        validateNullBean.setFieldName(element.getSimpleName().toString());
        return validateNullBean;
    }

    /**
     * 获取注解
     *
     * @param roundEnv
     * @param clazz
     * @param iteratorAnnotation
     * @param <T>
     */
    public static <T extends Annotation> void iteratorAnnotation(RoundEnvironment roundEnv, Class<? extends Annotation> clazz, IteratorAnnotation iteratorAnnotation) {
        for (Element element : roundEnv.getElementsAnnotatedWith(clazz)) {
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }
            iteratorAnnotation.iterator(element.getAnnotation(clazz), element);
        }
    }


    /**
     * 输出集合数据
     */
    interface IteratorAnnotation {
        <T extends Annotation> void iterator(T t, Element element);
    }


}
