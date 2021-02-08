package com.ys.androiddemo.statemachine;

import android.util.Log;

/**
 * Created by yinshan on 2021/1/19.
 */
public class CloudGameManager {

  public static void moveToState(CloudGameCallerContext callerContext, int newState){
    if(callerContext == null){
      return;
    }
    CloudGameStateManager stateManager = new CloudGameStateManager(callerContext);
    if(callerContext.mState <= newState){
      try{
        switch (callerContext.mState){
          case CloudGameState.INIT:
            if(newState > CloudGameState.INIT){
              stateManager.bindService();
            }
          case CloudGameState.BIND_SERVICE:
            if(newState > CloudGameState.BIND_SERVICE){
              stateManager.loadPlugin();
            }
          case CloudGameState.LOAD_PLUGIN:
            if(newState > CloudGameState.LOAD_PLUGIN){
              stateManager.loadGameConfig();
            }
          case CloudGameState.GAME_CONFIG:
            // do nothing
        }

        if(callerContext.mState != newState){
          callerContext.mState = newState;
        }
      }catch (Exception e){
        // 所有异常情况在catch中兜底
        Log.e("CloudGameStateManager", e.getMessage());
      }
    }else{
      // do nothing, 只可以正向状态转移，反向状态转移无效
    }
  }

  public static void nextStep(CloudGameCallerContext callerContext){
    if(callerContext == null){
      return;
    }
    CloudGameStateManager stateManager = new CloudGameStateManager(callerContext);
    switch (callerContext.mState){
      case CloudGameState.INIT:
        stateManager.bindService();
        break;
      case CloudGameState.BIND_SERVICE:
        stateManager.loadPlugin();
        break;
      case CloudGameState.LOAD_PLUGIN:
        stateManager.loadGameConfig();
        break;
      case CloudGameState.ERROR:
        stateManager.retryConnect();
        break;
    }
  }

}
