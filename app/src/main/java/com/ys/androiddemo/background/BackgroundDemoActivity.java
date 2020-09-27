package com.ys.androiddemo.background;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.ys.androiddemo.R;

/**
 * Created by yinshan on 2020/9/27.
 */
public class BackgroundDemoActivity extends Activity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_background_demo);
  }
}
