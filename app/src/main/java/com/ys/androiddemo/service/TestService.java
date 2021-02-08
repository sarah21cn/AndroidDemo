package com.ys.androiddemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 * Created by yinshan on 2021/1/14.
 */
public class TestService extends Service {

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
