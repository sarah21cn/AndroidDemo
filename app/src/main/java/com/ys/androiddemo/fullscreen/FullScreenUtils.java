package com.ys.androiddemo.fullscreen;

import android.view.View;
import android.view.Window;

/**
 * Created by yinshan on 2020/12/1.
 */
public class FullScreenUtils {

  public static void hideNavigationBar(Window window) {
    int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_FULLSCREEN;

    if (android.os.Build.VERSION.SDK_INT >= 19) {
      uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE;
    } else {
      uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
    }

    try {
      window.getDecorView().setSystemUiVisibility(uiFlags);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
