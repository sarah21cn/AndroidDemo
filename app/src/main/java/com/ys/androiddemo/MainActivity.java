package com.ys.androiddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ys.androiddemo.background.BackgroundDemoActivity;
import com.ys.androiddemo.viewmodel.ViewModelActivity;
import com.ys.androiddemo.vpn.VpnActivity;
import com.ys.androiddemo.x2c.X2cDemoActivity;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void onClick(View view){
    switch (view.getId()){
      case R.id.x2c_btn:
        Intent intent = new Intent(this, X2cDemoActivity.class);
        startActivity(intent);
        break;
      case R.id.background_btn:
        intent = new Intent(this, BackgroundDemoActivity.class);
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
    }
  }
}
