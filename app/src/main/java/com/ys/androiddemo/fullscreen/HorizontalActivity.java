package com.ys.androiddemo.fullscreen;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ys.androiddemo.R;

/**
 * Created by yinshan on 2020/12/1.
 */
public class HorizontalActivity extends AppCompatActivity {

  public static int IMMERSIVE_UI_FLAGS;
  static {
    if (Build.VERSION.SDK_INT >= 19) {
      IMMERSIVE_UI_FLAGS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_FULLSCREEN
          | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    } else if (Build.VERSION.SDK_INT >= 16) {
      IMMERSIVE_UI_FLAGS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_FULLSCREEN;
    } else {
      IMMERSIVE_UI_FLAGS = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    StatusBarUtils.adjustStatusBar(this, true);
    StatusBarUtils.hideNavigationBar(this, true);
    super.onCreate(savedInstanceState);


    setContentView(R.layout.activity_fullscreen_activity);
//    FullScreenUtils.hideNavigationBar(getWindow());
//    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//    enterImmersive();
  }

  public void enterImmersive() {
    getWindow().getDecorView().setSystemUiVisibility(IMMERSIVE_UI_FLAGS);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
  }
}
