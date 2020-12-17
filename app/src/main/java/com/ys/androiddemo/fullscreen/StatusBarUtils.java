package com.ys.androiddemo.fullscreen;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarUtils {

    private static final String TAG = "StatusBarUtils";

    public static void adjustStatusBar(final Activity activity, final boolean customStyle) {
        //全屏隐藏状态栏
        Window window = activity.getWindow();
        if (window == null) {
            return;
        }
        //去除标题栏
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //28以下刘海屏
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P && VendorUtils.hasHoles(activity) &&
                customStyle) {
            if (RomUtils.isEmui()) {
                setDisplayInNotchHw(activity);
            } else if (RomUtils.isMiui()) {
                setDisplayInNotchMI(activity);
            }
        }
        View decorView = window.getDecorView();
        if (decorView == null) {
            return;
        }
        decorView.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if (!((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)) {
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.
                            hideNavigationBar(activity, customStyle);
                        }
                    }
                });
    }

    /**
     * 华为28以下的刘海屏适配
     */
    public static void setDisplayInNotchHw(Activity activity) {
        try {
            Window window = activity.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            if (layoutParams == null) {
                return;
            }
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("addHwFlags", int.class);
            method.invoke(layoutParamsExObj, 0x00010000);
            window.getWindowManager()
                    .updateViewLayout(window.getDecorView(), window.getDecorView().getLayoutParams());
        } catch (Exception ignore) {
            Log.e("SoGameNotchScreenUtils", "setDisplayInNotchHw: ex " + ignore.getMessage());
        }
    }

    /**
     * 小米28以下的刘海屏适配
     */
    public static void setDisplayInNotchMI(Activity activity) {
        int flag = 0x00000100 | 0x00000200 | 0x00000400;
        try {
            Method method = Window.class.getMethod("addExtraFlags",
                    int.class);
            method.invoke(activity.getWindow(), flag);
        } catch (Exception ignore) {
            Log.e("SoGameNotchScreenUtils", "setDisplayInNotchMI: ex " + ignore.getMessage());
        }
    }

    public static void hideNavigationBar(Activity activity, boolean customStyle) {
        Window window = activity.getWindow();
        if (window == null) {
            return;
        }
        Log.d(TAG, "hideNavigationBar: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (window.getDecorView() == null || window.getAttributes() == null) {
                return;
            }
            //全屏显示
            window.getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            if (customStyle) {
                WindowManager.LayoutParams lp = window.getAttributes();
                if (lp != null) {
                    lp.layoutInDisplayCutoutMode =
                            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                    window.setAttributes(lp);
                }
            }
            View decorView = window.getDecorView();
            int systemUiVisibility = decorView.getSystemUiVisibility();
            systemUiVisibility |= getSystemUIFlag();

            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        } else {
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(getSystemUIFlag());
        }
    }

    private static int getSystemUIFlag() {
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        return flags;
    }
}
