package com.ys.androiddemo.statemachine;

import android.os.Message;
import android.util.Log;

/**
 * Created by yinshan on 2021/4/1.
 */
public class NewStateMachine extends StateMachine {

  private static final String TAG = "NewStateMachine";

  public static final int MSG_ENTER_B = 0;
  public static final int MSG_ENTER_A = 1;
  public static final int MSG_ENTER_C = 2;

  private BaseState mBaseState = new BaseState();
  private AState mAState = new AState();
  private BState mBState = new BState();
  private CState mCState = new CState();

  public NewStateMachine(String name) {
    super(name);
    addState(mAState, mBaseState);
    addState(mBState, mAState);
    addState(mCState, mBaseState);
    setInitialState(mBaseState);
    start();
  }

  class BaseState extends State{
    @Override
    public boolean processMessage(Message msg) {
      switch (msg.what){
        case MSG_ENTER_A:
          transitionTo(mAState);
          return HANDLED;
        case MSG_ENTER_B:
          transitionTo(mBState);
          return HANDLED;
        case MSG_ENTER_C:
          transitionTo(mCState);
          return HANDLED;
        default:
          return NOT_HANDLED;
      }
    }
  }

  class AState extends State {}

  class BState extends State{}

  class CState extends State{}
}
