package com.ys.androiddemo;

import org.json.JSONObject;
import org.junit.Test;

/**
 * Created by yinshan on 2021/1/26.
 */
public class TestJson {

  @Test
  public void testJson(){
    final String jsonStr = "{\"runMode\":1,\"providerId\":1,\"authCode\":\"\"," +
        "\"visualLogEnable\":true}";
    try{
      JSONObject obj = new JSONObject(jsonStr);
      int sMode = obj.optInt("runMode");
      int sProviderId = obj.optInt("providerId");
      String sAuthCode = obj.optString("authCode");
      boolean visualLogEnable = obj.optBoolean("visuablLogEnable", false);
    }catch (Exception e){
      e.printStackTrace();
    }

  }
}
