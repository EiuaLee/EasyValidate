package com.eiualee.easyvalidate_compiler.processor;

import com.eiualee.easyvalidate_annotations.ValidateCheck;
import com.eiualee.easyvalidate_annotations.ValidateNull;
import com.eiualee.easyvalidate_annotations.ValidateRegular;
import com.eiualee.easyvalidate_compiler.AnnotationProcessor;
import com.eiualee.easyvalidate_compiler.bean.BaseValidateBean;
import com.eiualee.easyvalidate_compiler.bean.ValidateCheckBean;
import com.eiualee.easyvalidate_compiler.bean.ValidateNullBean;
import com.eiualee.easyvalidate_compiler.bean.ValidateRegularBean;
import com.eiualee.easyvalidate_compiler.impl.IProcessor;
import com.eiualee.easyvalidate_compiler.utils.C;
import com.eiualee.easyvalidate_compiler.utils.MapUtils;
import com.eiualee.easyvalidate_compiler.utils.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.FilerException;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Created by liweihua on 2018/12/21.
 */

public class ValidateProcessor implements IProcessor {

    AnnotationProcessor annotationProcessor;


    @Override
    public void process(RoundEnvironment roundEnv, AnnotationProcessor annotationProcessor) {
        try {
            this.annotationProcessor = annotationProcessor;
            //用于缓存所有的Map 的 Key
            Map<String, Element> useAnnoClassElementMap = new LinkedHashMap<>();

            //根据注解获取所有的数据
            Map<String, List<ValidateNullBean>> validateNullBeanMap = MapUtils.getAnnotationDataGroupByClassName(roundEnv, ValidateNull.class, useAnnoClassElementMap);
            Map<String, List<ValidateCheckBean>> validateCheckBeanHashMap = MapUtils.getAnnotationDataGroupByClassName(roundEnv, ValidateCheck.class, useAnnoClassElementMap);
            Map<String, List<ValidateRegularBean>> validateRegularBeanHashMap = MapUtils.getAnnotationDataGroupByClassName(roundEnv, ValidateRegular.class, useAnnoClassElementMap);

            for (Element useAnnoClassElement : useAnnoClassElementMap.values()) {

                //com.xxx.xxx.xxx.XXActivity
                String useAnnoClassElementStr = useAnnoClassElement.toString();

                TypeName ac_fra_typeName = annotationProcessor.getTypeName(useAnnoClassElementStr);
                //创建类
                TypeSpec.Builder tBuilder = TypeSpec.classBuilder(useAnnoClassElement.getSimpleName().toString() + "_Validate")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addSuperinterface(annotationProcessor.TN_IVALIDATE)
                        .addJavadoc(" @ 由apt自动生成,请勿编辑\n");
                tBuilder.addField(ac_fra_typeName, "target", Modifier.PRIVATE);
                tBuilder.addField(annotationProcessor.TN_VIEW, "sourse", Modifier.PRIVATE);
                //创建构造体
                MethodSpec constructorBuilder = getConstrutorBuilder(useAnnoClassElement, ac_fra_typeName);
                tBuilder.addMethod(constructorBuilder);
                //创建isEmptyValidate方法
                MethodSpec isEmptyValidate = createValidateMethod(validateNullBeanMap.get(useAnnoClassElementStr), "isEmptyValidate");
                tBuilder.addMethod(isEmptyValidate);
                //创建isCheckValidate方法
                MethodSpec isCheckValidate = createValidateMethod(validateCheckBeanHashMap.get(useAnnoClassElementStr), "isCheckValidate");
                tBuilder.addMethod(isCheckValidate);
                //创建isRegularValidate方法
                MethodSpec isRegularValidate = createValidateMethod(validateRegularBeanHashMap.get(useAnnoClassElementStr), "isRegularValidate");
                tBuilder.addMethod(isRegularValidate);
                //创建unBind方法
                tBuilder.addMethod(createUnBindMethod());
                //创建isValidatePass方法
                tBuilder.addMethod(createIsValidatePassMethod());
                JavaFile javaFile = JavaFile.builder(annotationProcessor.mElements.getPackageOf(useAnnoClassElement).toString(), tBuilder.build()).build();
                javaFile.writeTo(annotationProcessor.mFiler);
            }


        } catch (FilerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建sValidatePassi方法
     *
     * @return
     */
    private MethodSpec createIsValidatePassMethod() {
        return MethodSpec.methodBuilder("isValidatePass")
                .addAnnotation(ClassName.get(annotationProcessor.mElements.getTypeElement(C.UITHREAD)))
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addParameter(TypeName.INT, "plan")
                .returns(TypeName.BOOLEAN)
                .addJavadoc("@ 是否验证通过\n")
                .addStatement("return isEmptyValidate(plan) && isCheckValidate(plan) && isRegularValidate(plan)")
                .build();
    }


    /**
     * 根据类的类型返回不同的构造体
     *
     * @param key
     * @return
     */
    private MethodSpec getConstrutorBuilder(Element key, TypeName act_fra_typeName) {
        MethodSpec.Builder constructorBuilder = null;
        constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
                .addParameter(act_fra_typeName, "target")
                .addParameter(annotationProcessor.TN_VIEW, "sourse")
                .addStatement("this.$N = $N", "target", "target")
                .addStatement("this.$N = $N", "sourse", Utils.isSubtypeOfType(key.asType(), C.ACTIVITY) ? "target.getWindow().getDecorView()" : "sourse");
        return constructorBuilder.build();
    }


    /**
     * 创建验证方法
     *
     * @param tList
     * @param methodName
     * @param <T>
     * @return
     */
    private <T extends BaseValidateBean> MethodSpec createValidateMethod(List<T> tList, String methodName) {


        MethodSpec.Builder mBuilder = MethodSpec.methodBuilder(methodName)
                .addJavadoc("@ 验证方法\n")
                .returns(TypeName.BOOLEAN)
                .addAnnotation(ClassName.get(annotationProcessor.mElements.getTypeElement(C.UITHREAD)))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addParameter(TypeName.INT, "plan");

        if (tList == null || tList.isEmpty()) {
            return mBuilder.addStatement("return true").build();
        }
        //获取该集合中所有要用到的验证计划
        Set<Integer> allPlan = getAllPlan(tList);

        CodeBlock.Builder blockBuilder = CodeBlock.builder();

        //根据计划写不同的代码
        for (Integer plan : allPlan) {
            blockBuilder.add("\nif(plan == $L){\n", plan);
            for (T t : tList) {
                //当被注解的元素没有使用该验证计划时，不进行代码注入
                if (!t.getPlans().contains(plan)) {
                    continue;
                }
                if (t instanceof ValidateCheckBean) {
                    validateCheckCode((ValidateCheckBean) t, blockBuilder);
                } else if (t instanceof ValidateNullBean) {
                    validateNullCode((ValidateNullBean) t, blockBuilder);
                } else if (t instanceof ValidateRegularBean) {
                    validateRegularCode((ValidateRegularBean) t, blockBuilder);
                }
            }
            blockBuilder.add("     return true;\n}");
        }
        blockBuilder.add("\nreturn true;\n");
        mBuilder.addCode(blockBuilder.build());
        return mBuilder.build();
    }

    /**
     * 创建UnBind方法
     *
     * @return
     */
    private MethodSpec createUnBindMethod() {
        return MethodSpec.methodBuilder("unBind")
                .addAnnotation(ClassName.get(annotationProcessor.mElements.getTypeElement(C.UITHREAD)))
                .addJavadoc("@ 解绑\n")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addStatement("this.sourse = null")
                .addStatement("this.target = null")
                .build();
    }

    /**
     *  String VALIDATE_REGULAR_CODE = "     if (!$T.getText(target.$N != null?target.$N:($T)sourse.findViewById($L)),matches($S)){" +
     "\n       $T.showShortToast($S);" +
     "\n       return false;" +
     "\n     }\n";
     */

    /**
     * 生成validateRegular代码
     *
     * @param validateRegularBean
     * @param blockBuilder
     */
    private void validateRegularCode(ValidateRegularBean validateRegularBean, CodeBlock.Builder blockBuilder) {
        blockBuilder.add(
                C.VALIDATE_REGULAR_CODE
                , annotationProcessor.TN_EASY_VALIDATE
                , validateRegularBean.getRegular()
                , annotationProcessor.TN_EASY_VALIDATE
                , validateRegularBean.getFieldName()
                , validateRegularBean.getFieldName()
                , annotationProcessor.getTypeName(validateRegularBean.getElementType())
                , validateRegularBean.getId()
                , annotationProcessor.TN_TOASTUTILS
                , validateRegularBean.getToast());
    }

    /**
     * 生成validateNull代码
     *
     * @param validateNullBean
     */
    private void validateNullCode(ValidateNullBean validateNullBean, CodeBlock.Builder blockBuilder) {
        blockBuilder.add(
                C.VALIDATE_NULL_CODE
                , annotationProcessor.TN_TEXTUTILS
                , annotationProcessor.TN_EASY_VALIDATE
                , validateNullBean.getFieldName()
                , validateNullBean.getFieldName()
                , annotationProcessor.getTypeName(validateNullBean.getElementType())
                , validateNullBean.getId()
                , annotationProcessor.TN_TOASTUTILS
                , validateNullBean.getToast());
    }

    /**
     * 生成validateCheck代码
     *
     * @param validateCheckBean
     */
    private void validateCheckCode(ValidateCheckBean validateCheckBean, CodeBlock.Builder blockBuilder) {
        blockBuilder.add(
                C.VALIDATE_CHECK_CODE
                , validateCheckBean.getFieldName()
                , validateCheckBean.isValidateState() ? "" : "!"
                , validateCheckBean.getFieldName()
                , validateCheckBean.isValidateState() ? "" : "!"
                , annotationProcessor.getTypeName(validateCheckBean.getElementType())
                , validateCheckBean.getId()
                , annotationProcessor.TN_TOASTUTILS
                , validateCheckBean.getToast());
    }

    /**
     * 获取所有对象的验证计划总集合
     *
     * @param baseValidateBeans
     * @return
     */
    private Set<Integer> getAllPlan(List<? extends BaseValidateBean> baseValidateBeans) {
        Set<Integer> integerSet = new LinkedHashSet<>();
        for (BaseValidateBean validateBean : baseValidateBeans) {
            integerSet.addAll(validateBean.getPlans());
        }
        return integerSet;
    }
}
