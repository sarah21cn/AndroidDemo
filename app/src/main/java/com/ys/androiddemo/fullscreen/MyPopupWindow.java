package com.ys.androiddemo.fullscreen;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.ys.androiddemo.R;

/**
 * Created by yinshan on 2020/12/12.
 */
public class MyPopupWindow extends PopupWindow {

  public MyPopupWindow(Context context) {
    super(context);
    setClippingEnabled(false);
    View rootView = LayoutInflater.from(context).inflate(R.layout.layout_popup_window, null);
    setContentView(rootView);
    setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
    setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    setOutsideTouchable(false);
    setFocusable(true);

    rootView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
  }
}
