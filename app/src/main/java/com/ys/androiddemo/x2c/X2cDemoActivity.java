package com.ys.androiddemo.x2c;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.ys.androiddemo.R;
import com.zhangyue.we.x2c.X2C;
import com.zhangyue.we.x2c.ano.Xml;

/**
 * Created by yinshan on 2020/9/25.
 *
 * x2c的几个问题：
 * 1. 什么时候将xml替换成java文件
 * 答：annotationProcessor，在APT的时候，对注解中的layout进行解析。通过文件名去对应路径下找到xml文件。
 * 2. 如果将xml生成对应的java文件
 * 答：代码在com.zhangyue.we.view.View#translate，字符串匹配生成对应的属性，使用javapoet生成Java源代码
 * 3. 怎样替换inflate过程，将从xml解析变成从java文件获取
 * 答：调用X2C.setContentView，会优先从生成的Java文件中创建View。使用生成的Creator来createView
 */

@Xml(layouts = "activity_x2c_demo")
public class X2cDemoActivity extends Activity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    X2C.setContentView(this, R.layout.activity_x2c_demo);
  }
}