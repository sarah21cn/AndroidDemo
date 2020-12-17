package com.ys.androiddemo.fullscreen;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ys.androiddemo.R;

/**
 * Created by yinshan on 2020/12/1.
 */
public class VerticalActivity extends AppCompatActivity {

  private MyPopupWindow popupWindow;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    StatusBarUtils.adjustStatusBar(this, true);
    StatusBarUtils.hideNavigationBar(this, true);

    setContentView(R.layout.activity_fullscreen_activity);

    popupWindow = new MyPopupWindow(VerticalActivity.this);

    findViewById(R.id.text_tv).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(!popupWindow.isShowing()) {
          popupWindow.showAtLocation(VerticalActivity.this.getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
        }else{
          popupWindow.dismiss();
        }
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    StatusBarUtils.hideNavigationBar(this, true);
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d("testtest", "onPause");
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d("testtest", "onStop");
  }
}
