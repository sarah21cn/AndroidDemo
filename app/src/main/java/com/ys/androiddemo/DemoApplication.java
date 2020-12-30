package com.ys.androiddemo;

import static android.os.StrictMode.*;

import java.util.List;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import com.iqiyi.android.qigsaw.core.Qigsaw;
import com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback;
import com.iqiyi.android.qigsaw.core.splitdownload.DownloadRequest;
import com.iqiyi.android.qigsaw.core.splitdownload.Downloader;

/**
 * Created by yinshan on 2020/11/7.
 */
public class DemoApplication extends Application {

  @Override
  public void onCreate() {
    //openStrictMode();
    super.onCreate();

    Qigsaw.install(this, new Downloader() {
      @Override
      public void startDownload(int sessionId, List<DownloadRequest> requests,
          DownloadCallback callback) {
        Log.d("plugin", "start Download " + sessionId);
      }

      @Override
      public void deferredDownload(int sessionId, List<DownloadRequest> requests,
          DownloadCallback callback, boolean usingMobileDataPermitted) {
        Log.d("plugin", "deferred Download " + sessionId);
      }

      @Override
      public boolean cancelDownloadSync(int sessionId) {
        return false;
      }

      @Override
      public long getDownloadSizeThresholdWhenUsingMobileData() {
        return 0;
      }

      @Override
      public boolean isDeferredDownloadOnlyWhenUsingWifiData() {
        return false;
      }
    });
    Qigsaw.onApplicationCreated();
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
