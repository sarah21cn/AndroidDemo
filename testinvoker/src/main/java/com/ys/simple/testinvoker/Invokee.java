package com.ys.simple.testinvoker;

import android.util.Log;

import com.ys.annotation.InvokeBy;

/**
 * Created by yinshan on 2021/3/4.
 */
public class Invokee {

  private String title = "Hello World";

  @InvokeBy(methodId = "test")
  public static void invokeeMethod(){
    Log.d("testtest", "invoked");
    System.out.println("invoked");
  }
}
