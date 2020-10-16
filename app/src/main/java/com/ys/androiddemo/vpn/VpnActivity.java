package com.ys.androiddemo.vpn;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Created by yinshan on 2020/10/16.
 */
public class VpnActivity extends Activity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = VpnService.prepare(this);
    if(intent != null){
      startActivityForResult(intent, 0);
    }else{
      onActivityResult(0, RESULT_OK, null);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(resultCode == RESULT_OK){
      Intent intent = new Intent(this, MyVpnService.class);
      startService(intent);
    }
  }
}
