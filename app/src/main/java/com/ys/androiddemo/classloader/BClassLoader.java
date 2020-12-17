package com.ys.androiddemo.classloader;

import android.util.Log;

import dalvik.system.PathClassLoader;

/**
 * Created by yinshan on 2020/12/17.
 */
public class BClassLoader extends PathClassLoader {

  private ClassLoader testParent;
  private ClassLoader pluginLoader;

  public BClassLoader(ClassLoader parent) {
    super("", parent);
  }

  public void setPluginLoader(ClassLoader pluginLoader){
    this.pluginLoader = pluginLoader;
  }
}
