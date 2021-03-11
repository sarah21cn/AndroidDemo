package com.ys.simple.apt;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Created by yinshan on 2021/3/7.
 */
@SupportedAnnotationTypes("com.ys.annotation.Test")
public class MyAnnotationProcessor extends AbstractProcessor {

  private Filer mFiler;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    mFiler = processingEnvironment.getFiler();
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    MethodSpec methodMain = MethodSpec.methodBuilder("main")//创建main方法
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)//定义修饰符为 public static
        .addJavadoc("@  此类由apt自动生成")//在生成的代码前添加注释
        .returns(void.class)//定义返回类型
        .addParameter(String[].class, "args")//定义方法参数
        .addStatement("$T.out.println($S)", System.class, "helloWorld")//定义方法体
        .build();
    TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld") //创建HelloWorld类
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)//定义修饰符为 public final
        .addMethod(methodMain)//添加方法
        .addJavadoc("@  此方法由apt自动生成")//定义方法参数
        .build();
    JavaFile javaFile = JavaFile.builder("com.ys.simple.apt", helloWorld).build();
    try{
      javaFile.writeTo(mFiler);
    }catch (IOException e){
      e.printStackTrace();
    }
    return true;
  }
}
