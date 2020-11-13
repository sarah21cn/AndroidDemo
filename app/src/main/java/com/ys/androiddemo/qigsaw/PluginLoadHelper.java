package com.ys.androiddemo.qigsaw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;

/**
 * Created by yinshan on 2020/11/11.
 */
public class PluginLoadHelper {

  private SplitInstallManager mSplitInstallManager;
  private Context mContext;

  public PluginLoadHelper(Context context) {
    mContext = context;
    mSplitInstallManager = SplitInstallManagerFactory.create(context);
  }

  private void loadPlugin(){
    if(mSplitInstallManager == null){
      mSplitInstallManager = SplitInstallManagerFactory.create(mContext);
    }
    if(mSplitInstallManager.getInstalledModules() != null && mSplitInstallManager.getInstalledModules().contains("plugin")){
      Toast.makeText(mContext, "已加载插件", Toast.LENGTH_SHORT).show();
      onLoadSucceed();
      return;
    }

    SplitInstallRequest.Builder builder = SplitInstallRequest.newBuilder();
    builder.addModule("plugin");
    SplitInstallRequest splitInstallRequest = builder.build();

    mSplitInstallManager.startInstall(splitInstallRequest);
  }

  private void onLoadSucceed(){
    Intent intent = new Intent();
    intent.setClassName("com.ys.androiddemo", "com.ys.simple.plugin.PluginActivity");
    if(!(mContext instanceof Activity)){
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    mContext.startActivity(intent);
  }
}
