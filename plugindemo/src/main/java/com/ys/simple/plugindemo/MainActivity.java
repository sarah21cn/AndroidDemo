package com.ys.simple.plugindemo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//        Intent intent = new Intent();
//        ComponentName componentName = new ComponentName("com.ys.androiddemo", "com.ys.androiddemo.service.TestService");
//        intent.setComponent(componentName);
//        startForegroundService(intent);

        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.ys.androiddemo", "com.ys.androiddemo.service.NewProcessActivity"));
        startActivity(intent);
      }
    });
  }
}