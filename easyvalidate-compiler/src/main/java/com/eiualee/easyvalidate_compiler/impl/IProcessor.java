package com.eiualee.easyvalidate_compiler.impl;


import com.eiualee.easyvalidate_compiler.AnnotationProcessor;

import javax.annotation.processing.RoundEnvironment;

/**
 * Created by liweihua on 2018/12/18.
 */

public interface IProcessor {
    void process(RoundEnvironment roundEnv, AnnotationProcessor annotationProcessor);
}
