package com.eiualee.easyvalidate_compiler;

import com.eiualee.easyvalidate_compiler.processor.ValidateProcessor;
import com.eiualee.easyvalidate_compiler.utils.C;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by liweihua on 2018/12/26.
 */

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)//java版本支持
@SupportedAnnotationTypes({
        "com.eiualee.easyvalidate_annotations.ValidateCheck",
        "com.eiualee.easyvalidate_annotations.ValidateNull",
        "com.eiualee.easyvalidate_annotations.ValidateRegular"
})//标注注解处理器支持的注解类型
public class AnnotationProcessor extends AbstractProcessor {
    public Filer mFiler; //文件相关的辅助类
    public Elements mElements; //元素相关的辅助类
    public Messager mMessager; //日志相关的辅助类
    public String moduleName;

    public TypeName TN_VIEW,TN_TOASTUTILS,TN_TEXTUTILS,TN_IVALIDATE,TN_EASY_VALIDATE = null;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();

        TN_VIEW = getTypeName(C.VIEW);
        TN_TOASTUTILS = getTypeName(C.TOASTUTILS);
        TN_TEXTUTILS = getTypeName(C.TEXTUTILS);
        TN_IVALIDATE = getTypeName(C.IVALIDATE);
        TN_EASY_VALIDATE = getTypeName(C.EASY_VALIDATE);
        new ValidateProcessor().process(roundEnv,this);
        return true;
    }

    public TypeName getTypeName(String elementName) {
        return ClassName.get(mElements.getTypeElement(elementName).asType());
    }

    public void log(String string){
        mMessager.printMessage(Diagnostic.Kind.NOTE, string);
    }
}
