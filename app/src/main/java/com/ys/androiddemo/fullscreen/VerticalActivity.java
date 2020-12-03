package com.ys.androiddemo.fullscreen;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ys.androiddemo.R;

/**
 * Created by yinshan on 2020/12/1.
 */
public class VerticalActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fullscreen_activity);
    FullScreenUtils.hideNavigationBar(getWindow());
  }
}
