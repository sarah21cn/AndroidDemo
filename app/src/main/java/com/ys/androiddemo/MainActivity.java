package com.ys.androiddemo;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ys.androiddemo.background.BackgroundDemoActivity;
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
    }
  }
}
