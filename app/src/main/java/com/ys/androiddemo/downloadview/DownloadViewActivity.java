package com.ys.androiddemo.downloadview;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ys.androiddemo.R;

/**
 * Created by yinshan on 2021/2/2.
 */
public class DownloadViewActivity extends Activity {

  private GameDownloadView downloadView;
  private GameDownloadView downloadView2;

  private int i = 0;

  private int progress = 0;

  private Handler mHandler = new Handler(Looper.getMainLooper()){
    @Override
    public void handleMessage(@NonNull Message msg) {
      Log.d("testtest", "handle message " + msg.what);
      if(msg.what == 0) {
        int random = new Random().nextInt(6);
        downloadView.setProgress(Math.min(99, i+=random), "");
        if(i <= 99){
          mHandler.sendEmptyMessageDelayed(0, 100);
        }
      }

      if(msg.what == 1){
        downloadView2.setProgress(Math.min(99, progress++), "");
        if(progress <= 99){
          mHandler.sendEmptyMessageDelayed(1, 1000);
        }
      }
    }
  };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_download_view);

    downloadView = findViewById(R.id.download_view);
    mHandler.sendEmptyMessage(0);

    downloadView2 = findViewById(R.id.download_view_2);
    mHandler.sendEmptyMessage(1);
  }
}
