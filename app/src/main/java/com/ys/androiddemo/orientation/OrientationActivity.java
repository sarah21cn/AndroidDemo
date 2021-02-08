package com.ys.androiddemo.orientation;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ys.androiddemo.R;

/**
 * Created by yinshan on 2021/2/2.
 */
public class OrientationActivity extends Activity {

  private static final String TAG = "OrientationActivity";

  private TextView clickBtn;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    int orientation = getResources().getConfiguration().orientation;
    if(orientation == Configuration.ORIENTATION_PORTRAIT){
      setContentView(R.layout.activity_orientation_portrait);
    }else{
      setContentView(R.layout.activity_orientation_landscape);
    }
    initViews();
  }

  @Override
  public void onConfigurationChanged(@NonNull Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    Log.d(TAG, "onConfigurationChanged");
    int orientation = newConfig.orientation;
    if(orientation == Configuration.ORIENTATION_PORTRAIT){
      setContentView(R.layout.activity_orientation_portrait);
    }else{
      setContentView(R.layout.activity_orientation_portrait);
    }
    initViews();
  }

  private void initViews(){
    clickBtn = findViewById(R.id.click_btn);
    clickBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {//竖屏
          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
      }
    });
  }
}
