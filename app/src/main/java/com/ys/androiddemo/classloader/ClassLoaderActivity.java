package com.ys.androiddemo.classloader;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;

/**
 * Created by yinshan on 2020/12/17.
 */
public class ClassLoaderActivity extends Activity {

  private static final String TAG = "testClassLoader";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    BClassLoader bClassLoader = new BClassLoader(getClassLoader());
    Log.d(TAG, "create b classloader: " + bClassLoader);

    final String textPath = "/data/data/com.ys.androiddemo/plugindemo-debug.apk";
    File optimizedPath = getDir("odex", Context.MODE_PRIVATE);
    AClassLoader aClassLoader = new AClassLoader(textPath, optimizedPath, "", bClassLoader);
    Log.d(TAG, "create a classloader: " + aClassLoader);

    bClassLoader.setPluginLoader(aClassLoader);

    try{
      Class internalClass = aClassLoader.loadClass("com.ys.androiddemo.classloader.TestClass");
      Log.d(TAG, "load by:" + internalClass.getClassLoader());

      Class pluginClass = aClassLoader.loadClass("com.ys.simple.plugindemo.MainActivity");
      Log.d(TAG, "load by:" + pluginClass.getClassLoader());
    }catch (Exception e){
      Log.e(TAG, e.getMessage());
      e.printStackTrace();
    }

  }
}
