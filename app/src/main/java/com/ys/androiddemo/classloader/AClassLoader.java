package com.ys.androiddemo.classloader;

import java.io.File;

import dalvik.system.BaseDexClassLoader;

/**
 * Created by yinshan on 2020/12/17.
 */
public class AClassLoader extends BaseDexClassLoader {

  public AClassLoader(String dexPath, File optimizedDirectory, String librarySearchPath, ClassLoader parent) {
    super(dexPath, optimizedDirectory, librarySearchPath, parent);
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    // 在白名单里，调用super.loadClass(name) 即可
//    if (mHostInterfaces != null && mHostInterfaces.contains(name)) {
//      return super.loadClass(name, resolve);
//    }
//
//    if (mHostPackages != null) {
//      for (String prefix: mHostPackages) {
//        if (name.startsWith(prefix + ".")) {
//          return super.loadClass(name, resolve);
//        }
//      }
//    }

    Class<?> clazz = findLoadedClass(name);

    if(clazz == null){
      try{
        findClass(name);
      }catch (ClassNotFoundException e){
        // 插件的classLoader中未找到
      }
    }

    if(clazz == null){
      return super.loadClass(name);
    }

    return clazz;
  }
}
