package com.ys.simple.testinvoker;

import android.util.Log;

import com.ys.annotation.ForInvoker;
import com.ys.annotation.InvokeBy;

/**
 * Created by yinshan on 2021/3/4.
 */
public class Invoker {

  @ForInvoker(methodId = "test")
  public void invokeMethod() {

  }
}
