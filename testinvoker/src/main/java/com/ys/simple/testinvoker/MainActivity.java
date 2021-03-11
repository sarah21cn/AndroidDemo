package com.ys.simple.testinvoker;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.ys.annotation.Test;

@Test
public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    new Invoker().invokeMethod();
  }
}