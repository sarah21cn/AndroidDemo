package com.ys.androiddemo.fullscreen;

import android.content.Context;
import android.os.Build;

/**
 * 第三方rom提供的方法.
 */
public class VendorUtils {

  // 一些暂时无法获取刘海屏api的机型，先用model来判断
  private static final String[] HOLES_MODEL_LIST = {"ONEPLUS A6000", "ONEPLUS A6003", "IN2010"};

  // VIVO Constants
  private static final int ROUND_CORNER_MASK = 0x8;
  private static final int HOLE_MASK = 0x20;
  private static final int HOLE_SMARTISAN_MASK = 0x00000001;

  private static final String HOLE_FEATURE_VIVO = "vivo.hardware.holescreen";

  private static Boolean HAS_HOLES;

  private VendorUtils() {}

  public static boolean hasHoles(Context context) {

    if (HAS_HOLES != null) {
      return HAS_HOLES;
    }

    if (RomUtils.isOppo()) {
      HAS_HOLES = readHolesForOppo(context);
    } else if (RomUtils.isVivo()) {
      HAS_HOLES = readHolesForVivo() || readHolesForVivoNew();
    } else if (RomUtils.isEmui()) {
      HAS_HOLES = readHolesForHuawei(context);
    } else if (RomUtils.isMiui()) {
      HAS_HOLES = readHolesForXiaomi(context);
    } else if (RomUtils.isSmartisan()) {
      HAS_HOLES = readHolesForSmartisan();
    } else {
      HAS_HOLES = readHolesForModelList();
    }
    return HAS_HOLES;
  }

  /**
   * https://resource.smartisan.com/resource/61263ed9599961d1191cc4381943b47a.pdf
   * 
   * @return hasHoles
   */
  private static Boolean readHolesForSmartisan() {
    Boolean hasHoles = null;
    try {
      hasHoles = JavaCalls.callStaticMethod("smartisanos.api.DisplayUtilsSmt",
          "isFeatureSupport", HOLE_SMARTISAN_MASK);
    } catch (Exception e) {}
    return hasHoles != null && hasHoles;
  }

  private static boolean readHolesForOppo(Context context) {
    try {
      return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    } catch (Exception ignored) {
      return false;
    }
  }

  // 按照Vivo的说法，这个是刘海屏
  private static boolean readHolesForVivo() {
    Boolean hasHoles = null;
    try {
      hasHoles = JavaCalls.callStaticMethod("android.util.FtFeature",
          "isFeatureSupport", HOLE_MASK);
    } catch (Exception e) {}
    return hasHoles != null && hasHoles;
  }

  // 按照Vivo的说法，这个是挖孔屏
  private static boolean readHolesForVivoNew() {
    Boolean hasHoles = null;
    try {
      hasHoles = JavaCalls.callStaticMethod("android.util.FtFeature",
          "isFeatureSupport", HOLE_FEATURE_VIVO);
    } catch (Exception e) {}
    return hasHoles != null && hasHoles;
  }

  private static boolean readHolesForHuawei(Context context) {
    Boolean hasHoles = null;
    try {
      hasHoles = JavaCalls.callStaticMethod("com.huawei.android.util.HwNotchSizeUtil",
          "hasNotchInScreen");
    } catch (Exception e) {}
    return hasHoles != null && hasHoles;
  }

  private static boolean readHolesForXiaomi(Context context) {
    Integer notch = null;
    try {
      notch =
          JavaCalls.callStaticMethod("android.os.SystemProperties", "getInt", "ro.miui.notch", 0);
    } catch (Exception e) {}
    return notch != null && notch == 1;
  }

  private static boolean readHolesForModelList() {
    for (String model : HOLES_MODEL_LIST) {
      if (model.equalsIgnoreCase(Build.MODEL)) {
        return true;
      }
    }
    return false;
  }
}
