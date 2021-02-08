package com.ys.androiddemo.statemachine;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * Created by yinshan on 2021/1/19.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({
    CloudGameState.INIT,
    CloudGameState.BIND_SERVICE,
    CloudGameState.LOAD_PLUGIN,
    CloudGameState.GAME_CONFIG,
    CloudGameState.ERROR
})
public @interface CloudGameState {
  int INIT = 0;
  int BIND_SERVICE = 1;
  int LOAD_PLUGIN = 2;
  int GAME_CONFIG = 3;

  int ERROR = -1;
  int ERROR_AUTH = -2;
}
