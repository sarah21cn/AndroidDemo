package com.ys.simple.testinvoker;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.Nullable;

/**
 * Created by yinshan on 2021/3/23.
 */
public class CloudGameCircleView extends View {

  private Paint mPaint;
  private float mSize = 10;

  public CloudGameCircleView(Context context) {
    this(context, null);
  }

  public CloudGameCircleView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CloudGameCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mPaint = new Paint();
    mPaint.setColor(Color.GREEN);
    mPaint.setAntiAlias(true);
    setOnMeasureCallback();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawCircle(mSize, mSize, mSize, mPaint);
  }

  public void setColor(int color){
    mPaint.reset();
    mPaint.setColor(color);
    mPaint.setAntiAlias(true);
    invalidate();
  }

  private void setOnMeasureCallback() {
    ViewTreeObserver vto = getViewTreeObserver();
    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        removeOnGlobalLayoutListener(this);
        mSize = getMeasuredWidth() / 2;
      }
    });
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  private void removeOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener listener) {
    if (Build.VERSION.SDK_INT < 16) {
      getViewTreeObserver().removeGlobalOnLayoutListener(listener);
    } else {
      getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }
  }
}
