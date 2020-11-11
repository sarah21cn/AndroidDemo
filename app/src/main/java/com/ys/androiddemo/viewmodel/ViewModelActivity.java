package com.ys.androiddemo.viewmodel;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.ys.androiddemo.R;

/**
 * Created by yinshan on 2020/11/7.
 */
public class ViewModelActivity extends AppCompatActivity {

  private TestViewModel mViewModel;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mViewModel = new ViewModelProvider(this).get(TestViewModel.class);
    setContentView(R.layout.activity_view_model);
    final TextView textView = findViewById(R.id.test_tv);
    mViewModel.getData().observe(this, text -> {
      textView.setText(text);
    });
  }
}
