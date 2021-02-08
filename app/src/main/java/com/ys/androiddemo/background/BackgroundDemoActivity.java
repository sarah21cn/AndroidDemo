package com.ys.androiddemo.background;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;

import com.ys.androiddemo.R;

/**
 * Created by yinshan on 2020/9/27.
 */
public class BackgroundDemoActivity extends Activity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_background_demo);
    if(getIntent() != null){
      String jsonStr = getIntent().getStringExtra("data");
      testJson(jsonStr);
    }
  }


  public void testJson(final String jsonStr){
    Log.d("testtest", "testJson " + jsonStr);
    try{
      JSONObject obj = new JSONObject(jsonStr);
      int sMode = obj.optInt("runMode");
      int sProviderId = obj.optInt("providerId");
      String sAuthCode = obj.optString("authCode");
      boolean visualLogEnable = obj.optBoolean("visuablLogEnable", false);
      Log.d("testtest", sMode + " " + sProviderId + " " + sAuthCode + " " + visualLogEnable);
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
