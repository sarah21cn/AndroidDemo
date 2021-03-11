package com.ys.androiddemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.PermissionChecker;

import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import com.ys.androiddemo.background.BackgroundDemoActivity;
import com.ys.androiddemo.classloader.ClassLoaderActivity;
import com.ys.androiddemo.downloadview.DownloadViewActivity;
import com.ys.androiddemo.fullscreen.HorizontalActivity;
import com.ys.androiddemo.fullscreen.VerticalActivity;
import com.ys.androiddemo.invoker.Invokee;
import com.ys.androiddemo.invoker.Invoker;
import com.ys.androiddemo.memory.MemoryActivity;
import com.ys.androiddemo.orientation.OrientationActivity;
import com.ys.androiddemo.serializable.TestJsonObject;
import com.ys.androiddemo.shortcut.ShortCutUtils;
import com.ys.androiddemo.statemachine.StateMachineActivity;
import com.ys.androiddemo.viewmodel.ViewModelActivity;
import com.ys.androiddemo.vpn.VpnActivity;
import com.ys.androiddemo.x2c.X2cDemoActivity;

public class MainActivity extends Activity {

  private SplitInstallManager manager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    manager = SplitInstallManagerFactory.create(this);

    new Invoker().invokeMethod();
  }

  public void onClick(View view){
    switch (view.getId()){
      case R.id.x2c_btn:
        Intent intent = new Intent(this, X2cDemoActivity.class);
        startActivity(intent);
        break;
      case R.id.background_btn:
        intent = new Intent(this, BackgroundDemoActivity.class);
        intent.putExtra("data", "{\"runMode\":1,\"providerId\":1,\"authCode\":\"\",\"visualLogEnable\":true}");
        startActivity(intent);
        break;
      case R.id.vpn_btn:
        intent = new Intent(this, VpnActivity.class);
        startActivity(intent);
        break;
      case R.id.vm_btn:
        intent = new Intent(this, ViewModelActivity.class);
        startActivity(intent);
        break;
      case R.id.qigsaw_btn:
        loadPlugin();
        break;
      case R.id.cloud_btn:
//        intent = new Intent();
//        intent.setData(Uri.parse("kwai://cloudgame/play?packageName=com.happyelements.AndroidAnimal.kuaishou&appId=ks655273747375298423&scope=user_info&screenOrientation=0"));
//        intent.setAction(Intent.ACTION_VIEW);
////        startActivity(intent);
//
//        ShortcutInfoCompat shortcut = new ShortcutInfoCompat
//            .Builder(this, "ks655273747375298423")
//            .setShortLabel("测试快捷方式")
//            .setLongLabel("测试快捷方式")
//            .setIntent(intent)
//            .build();
//
//        Intent pinnedShortcutCallbackIntent = ShortcutManagerCompat.createShortcutResultIntent(this, shortcut);
//        PendingIntent successCallback = PendingIntent.getBroadcast(this, 0,
//            pinnedShortcutCallbackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // 发起添加快捷方式的请求
//        ShortcutManagerCompat.requestPinShortcut(this, shortcut, successCallback.getIntentSender());

        intent = new Intent();
        intent.setClassName("com.smile.gifmaker", "com.yxcorp.gifshow.gamecenter.sogame.playstation.sogame.playstation.PlayStationIpcService");
        startService(intent);
        if(!hasPermission(this, Manifest.permission.INSTALL_SHORTCUT)){
          Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
          return;
        }
        if(new ShortCutUtils(this).createPinnedShortCuts(this)){
          Toast.makeText(this, "创建成功", Toast.LENGTH_SHORT).show();
        }else{
          Toast.makeText(this, "创建失败，没权限", Toast.LENGTH_SHORT).show();
        }
        break;
      case R.id.vertical_btn:
        intent = new Intent(this, VerticalActivity.class);
        intent.putExtra("test", new TestJsonObject());
        startActivity(intent);
        break;
      case R.id.horizontal_btn:
        intent = new Intent(this, HorizontalActivity.class);
        startActivity(intent);
        break;
      case R.id.class_loader_btn:
        intent = new Intent(this, ClassLoaderActivity.class);
        startActivity(intent);
        break;
      case R.id.memory_btn:
        intent = new Intent(this, MemoryActivity.class);
        startActivity(intent);
        break;
      case R.id.state_machine_btn:
        intent = new Intent(this, StateMachineActivity.class);
        startActivity(intent);
        break;
      case R.id.download_btn:
        intent = new Intent(this, DownloadViewActivity.class);
        startActivity(intent);
        break;
      case R.id.orientation_btn:
        intent = new Intent(this, OrientationActivity.class);
        startActivity(intent);
        break;
    }
  }

  public static boolean hasPermission(Context context, String permission) {
    if (context == null) {
      return false;
    }

    return PermissionChecker.checkSelfPermission(context, permission) ==
        PermissionChecker.PERMISSION_GRANTED;
  }

  private void loadPlugin(){
    if(manager == null){
      manager = SplitInstallManagerFactory.create(this);
    }
    if(manager.getInstalledModules() != null && manager.getInstalledModules().contains("plugin")){
      Toast.makeText(this, "已加载插件", Toast.LENGTH_SHORT).show();
      onLoadSucceed();
      return;
    }

    SplitInstallRequest.Builder builder = SplitInstallRequest.newBuilder();
    builder.addModule("plugin");
    SplitInstallRequest splitInstallRequest = builder.build();

    manager.startInstall(splitInstallRequest);
  }

  @Override
  protected void onResume() {
    super.onResume();
    manager.registerListener(mListener);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    manager.unregisterListener(mListener);
  }

  SplitInstallStateUpdatedListener mListener = new SplitInstallStateUpdatedListener() {
    @Override
    public void onStateUpdate(SplitInstallSessionState splitInstallSessionState) {
      Log.d("plugin", "status: " + splitInstallSessionState.status());
      if(splitInstallSessionState.status() >= SplitInstallSessionStatus.INSTALLED){
        Toast.makeText(MainActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
        onLoadSucceed();
      }
    }
  };

  private void onLoadSucceed(){
    Intent intent = new Intent();
    intent.setClassName("com.ys.androiddemo", "com.ys.simple.plugin.PluginActivity");
    startActivity(intent);

    try{
      Class<?> clazz = Class.forName("com.ys.simple.plugin.PluginActivity");
    }catch (ClassNotFoundException e){
      e.printStackTrace();
    }
  }

}
