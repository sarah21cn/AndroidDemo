package com.ys.androiddemo.memory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.permissionx.guolindev.PermissionX;

/**
 * Created by yinshan on 2021/1/10.
 */
public class MemoryActivity extends AppCompatActivity {

  private List<Object> cacheList = new ArrayList<>();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    readFilesCheckPermissions();
  }

  // 循环调用，压入栈帧，会导致StackOverflowError
  private void stackOverflow(){
    stackOverflow();
  }

  // 疯狂创建线程，会引起频繁GC，导致系统卡顿
  // Throwing OutOfMemoryError: Failed to allocate a 32 byte
  // 不会崩溃，会导致anr
  private void createThread(){
    new Thread(){
      @Override
      public void run() {
        while(true){
          Thread thread = new Thread();
          cacheList.add(thread);
        }
      }
    }.start();
  }

  // 在堆上new对象，会导致oom
  private void heapOOM(){
    new Thread(){
      @Override
      public void run() {
        while (true){
          int[] list = new int[10000];
          cacheList.add(list);
        }
      }
    }.start();
  }

  int i;

  // 打开strictmode 可以看到提示 System: A resource failed to call close.
  // 不会崩溃，也不会报fd不够，也不会报file descriptor不够
  // adb shell ulimit -a 查看最大fd为32768
  private void readFiles(){
    new Thread(){
      @Override
      public void run() {
        while(true){
          readFile();
          Log.d("MemoryActivity", "read count: " + ++i);
        }
      }
    }.start();
  }

  private void readFile(){
    try{
      FileInputStream in = new FileInputStream("/sdcard/a.txt");
    }catch (IOException e){
      e.printStackTrace();
    }
  }

  private void readFilesCheckPermissions(){
    PermissionX.init(this)
        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .request((allGranted, grantedList, deniedList) -> {
          if(allGranted){
            readFiles();
          }
        });
  }

  private void readFileCheckPermissions(){
    PermissionX.init(this)
        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .request((allGranted, grantedList, deniedList) -> {
          if(allGranted){
            readFile();
          }
        });
  }


}
