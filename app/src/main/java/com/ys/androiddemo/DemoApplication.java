package com.ys.androiddemo;

import static android.os.StrictMode.*;

import android.app.Application;
import android.os.StrictMode;

/**
 * Created by yinshan on 2020/11/7.
 */
public class DemoApplication extends Application {

  @Override
  public void onCreate() {
    openStrictMode();
    super.onCreate();
  }

  private void openStrictMode(){
    if (BuildConfig.DEBUG) {
      setThreadPolicy(new ThreadPolicy.Builder()
          .detectDiskReads()
          .detectDiskWrites()
          .detectNetwork()   // or .detectAll() for all detectable problems
          .penaltyLog()
          .build());
      setVmPolicy(new VmPolicy.Builder()
          .detectLeakedSqlLiteObjects()
          .detectLeakedClosableObjects()
          .detectActivityLeaks()
          .penaltyLog()
          .penaltyDeath()
          .build());
    }
  }
}
