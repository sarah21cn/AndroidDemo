package com.ys.androiddemo.service;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by yinshan on 2021/1/14.
 */
public class NewProcessActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    Intent intent = new Intent(this, TestService.class);
//    startService(intent);
    this.finish();
  }
}
