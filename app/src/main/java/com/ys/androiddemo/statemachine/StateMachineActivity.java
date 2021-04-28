package com.ys.androiddemo.statemachine;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;

import com.ys.androiddemo.R;

/**
 * Created by yinshan on 2021/1/19.
 */
public class StateMachineActivity extends Activity {

  CloudGameCallerContext callerContext = new CloudGameCallerContext();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_state_machine);
    NewStateMachine stateMachine = new NewStateMachine("NewStateMachine");
    findViewById(R.id.a_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        stateMachine.sendMessage(NewStateMachine.MSG_ENTER_A);
      }
    });
    findViewById(R.id.b_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        stateMachine.sendMessage(NewStateMachine.MSG_ENTER_B);
      }
    });
    findViewById(R.id.c_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        stateMachine.sendMessage(NewStateMachine.MSG_ENTER_C);
      }
    });
    findViewById(R.id.exit_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        stateMachine.quitNow();
      }
    });
    findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        stateMachine.start();
      }
    });
  }
}
