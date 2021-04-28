package com.ys.simple.testinvoker;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.ys.annotation.Test;

@Test
public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    new Invoker().invokeMethod();

    TextView textView = findViewById(R.id.text_view);
    textView.setText(Html.fromHtml(getResources().getString(R.string.zt_game_in_queue_tips, 1)));
  }
}