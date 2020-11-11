package com.ys.androiddemo.viewmodel;


import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by yinshan on 2020/11/7.
 */
public class TestViewModel extends ViewModel {

  private MutableLiveData<String> data;

  public TestViewModel(){
    data = new MutableLiveData<>();
    requestData();
  }

  public LiveData<String> getData(){
    return data;
  }

  public void requestData(){
    Thread thread = new Thread(){
      @Override
      public void run() {
        super.run();
        try{
          Thread.sleep(5000);
        }catch (Exception e){
         e.printStackTrace();
        }
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
          @Override
          public void run() {
            data.setValue("Hello World");
          }
        });
      }
    };
    thread.start();
  }
}
