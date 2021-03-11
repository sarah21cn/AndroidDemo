package com.ys.androiddemo.shortcut;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.ys.androiddemo.MainActivity;
import com.ys.androiddemo.R;

/**
 * Created by yinshan on 2021/1/14.
 */
public class ShortCutUtils {


  private Context mContext;

  public ShortCutUtils(Context context){
    this.mContext = context;
  }

//  public void addShortCutCompact(Bitmap bitmap){
//    if(ShortcutManagerCompat.isRequestPinShortcutSupported(mContext)){
//      Intent shortcutIntent = new Intent();
//      shortcutIntent.setData(Uri.parse("kwai://cloudgame/play?packageName=com.happyelements.AndroidAnimal.kuaishou&appId=ks655273747375298423&scope=user_info&screenOrientation=0"));
//
//      Intent resultIntent = new Intent();
//      resultIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, bitmap);
//      resultIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//      resultIntent.putExtra(Intent.EXTRA_COMPONENT_NAME, "开心消消乐");
//      resultIntent.setAction(Intent.ACTION_CREATE_SHORTCUT);
//      mContext.sendBroadcast(resultIntent);
//
//      ShortcutManager shortcutManager = new ShortcutManager();
//
//    }else{
//
//    }
//  }

  public boolean createPinnedShortCuts(Context context){
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      return createPinnedShortCutsO(context);
    }else{
      return createPinnedShortCutsOthers();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  private boolean createPinnedShortCutsO(Context context) {
    ShortcutManager shortcutManager = mContext.getSystemService(ShortcutManager.class);
    if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()) {
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("kwai://cloudgame/play?packageName=com.happyelements.AndroidAnimal.kuaishou&appId=ks655273747375298423&scope=user_info&screenOrientation=0"));
//      intent.setData(Uri.parse(
//          "kwai://cloudgame/play?packageName=com.happyelements.AndroidAnimal.kuaishou&appId=ks655273747375298423&scope=user_info&screenOrientation=0"));
      intent.setAction(Intent.ACTION_VIEW);
      ShortcutInfoCompat pinShortcutInfo = new ShortcutInfoCompat.Builder(mContext, "my-shortcut")
          .setLongLabel("开心消消乐")
          .setShortLabel("开心消消乐")
          .setIcon(IconCompat.createWithResource(mContext, R.drawable.icon))
          .setIntent(intent)
          .build();
      Intent pinnedShortcutCallbackIntent =
          ShortcutManagerCompat.createShortcutResultIntent(mContext, pinShortcutInfo);
      PendingIntent successCallback = PendingIntent.getBroadcast(mContext, 0,
          pinnedShortcutCallbackIntent, 0);
      return ShortcutManagerCompat.requestPinShortcut(mContext, pinShortcutInfo, successCallback.getIntentSender());
    }
    return false;
  }

  private boolean createPinnedShortCutsOthers(){
    Intent addShortIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
    addShortIntent.putExtra("duplicate", false); //禁止重复添加。 小米系统无效果
    addShortIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"开心消消乐");//快捷方式的名字
    Intent.ShortcutIconResource shortcutIconResource = Intent.ShortcutIconResource.fromContext(mContext, R.drawable.icon);
    addShortIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,shortcutIconResource); //快捷方式的图标//点击快捷方式打开的页面
    Intent actionIntent = new Intent(Intent.ACTION_MAIN);
    actionIntent.setData(Uri.parse(
        "kwai://cloudgame/play?packageName=com.happyelements.AndroidAnimal.kuaishou&appId=ks655273747375298423&scope=user_info&screenOrientation=0"));
    actionIntent.addCategory(Intent.CATEGORY_LAUNCHER);//添加categoryCATEGORY_LAUNCHER 应用被卸载时快捷方式也随之删除
    addShortIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,actionIntent);
    mContext.sendBroadcast(addShortIntent); //设置完毕后发送广播给系统。
    return true;
  }

}
