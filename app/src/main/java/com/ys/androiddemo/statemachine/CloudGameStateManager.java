package com.ys.androiddemo.statemachine;

import java.util.Random;

import android.util.Log;

/**
 * Created by yinshan on 2021/1/19.
 */
public class CloudGameStateManager {

  private static final String TAG = "CloudGameStateManager";

  private CloudGameCallerContext mCallerContext;

  public CloudGameStateManager(CloudGameCallerContext callerContext){
    this.mCallerContext = callerContext;
  }

  public void retryConnect(){
    if(mCallerContext == null){
      mCallerContext = new CloudGameCallerContext();
      return;
    }
    Log.d(TAG, "retry connect");
    mCallerContext.mState = CloudGameState.INIT;
    CloudGameManager.nextStep(mCallerContext);
  }

  public void bindService(){
    if(mCallerContext == null){
      mCallerContext = new CloudGameCallerContext();
      return;
    }
    if(randomError()){
      Log.d(TAG, "bind service error");
      mCallerContext.mState = CloudGameState.ERROR;
    }else{
      Log.d(TAG, "bind service succeed");
      mCallerContext.mState = CloudGameState.BIND_SERVICE;
      CloudGameManager.nextStep(mCallerContext);
    }

  }

  public void loadPlugin(){
    if(mCallerContext == null){
      mCallerContext = new CloudGameCallerContext();
      return;
    }
    if(randomError()){
      Log.d(TAG, "load plugin error");
      mCallerContext.mState = CloudGameState.ERROR;
    }else{
      Log.d(TAG, "load plugin succeed");
      mCallerContext.mState = CloudGameState.LOAD_PLUGIN;
      CloudGameManager.nextStep(mCallerContext);
    }

  }

  public void loadGameConfig(){
    if(mCallerContext == null){
      mCallerContext = new CloudGameCallerContext();
      return;
    }
    if(randomError()){
      Log.d(TAG, "load game config error");
      mCallerContext.mState = CloudGameState.ERROR;
    }else{
      Log.d(TAG, "load game config succeed");
      mCallerContext.mState = CloudGameState.GAME_CONFIG;
    }
  }


  private boolean randomError(){
    Random random = new Random();
    return random.nextInt() % 2 == 0;
  }

}
