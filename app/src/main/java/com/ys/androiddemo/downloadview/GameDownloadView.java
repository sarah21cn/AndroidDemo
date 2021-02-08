package com.ys.androiddemo.downloadview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ys.androiddemo.R;

/**
 * 云游戏加载进度
 */
public class GameDownloadView extends View {

  private static final int DEFAULT_RADIUS = 20;
  private static final int DELAY_INVALIDATE_TIME = 100;
  private static final int PROGRESS_SUCCESS = 100;
  private static final int PROGRESS_BG_SHADER_TIME = 5;

  private int mProgress;

  Paint mPaint = new Paint();
  Paint mProgressShaderPaint = new Paint();
  Paint mClearPaint = new Paint();
  Paint mBackgroundPaint;
  Paint mBorderPaint;
  Paint mProgressPaint;
  Paint mTextPaint;
  RectF mBackgroundBounds;
  RectF mProgressBounds;
  int mRadius;
  int mBgIdleGreyColor;
  int mProgressStartColor;
  int mProgressEndColor;
  int mBorderStartColor;
  int mBorderEndColor;

  float mTextSize = getResources().getDimension(R.dimen.text_size_14);
  int mProgressBarHeight = (int) getResources().getDimension(R.dimen.progress_bar_height_11dp);

  float mMarginTop;
  float mMarginHorizontal;

  Bitmap mWhiteBitmap;
  Bitmap mProgressBitmap;
  Bitmap mProgressShaderBitmap;
  Bitmap mBorderBitmap;
  Bitmap mShaderBgBitmap;
  Bitmap mTextBgBitmap;
  Canvas mBitmapCanvas = new Canvas();
  Canvas mBitmapShaderCanvas = new Canvas();
  Canvas mBorderCanvas = new Canvas();
  Canvas mShaderBgCanvas = new Canvas();
  PorterDuffXfermode mCleanXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
  PorterDuffXfermode mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
  int mBackgroundOffsetX;

  Runnable mInvalidateRunnable = new Runnable() {
    @Override
    public void run() {
      invalidate();
    }
  };

  public void setProgress(int progress, String text) {
    this.mProgress = progress;
    invalidate();
  }

  public GameDownloadView(Context context) {
    super(context);
    init(context, null);
  }

  public GameDownloadView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public GameDownloadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GameDownloadView);
    if (attrs != null && ta != null) {
      mRadius = ta.getDimensionPixelOffset(R.styleable.GameDownloadView_gameRadius, DEFAULT_RADIUS);
      mBgIdleGreyColor = ta.getColor(R.styleable.GameDownloadView_gameIdleGreyColor, getResources().getColor(R.color.gray_color));
      mProgressStartColor = ta.getColor(R.styleable.GameDownloadView_gameProgressStartColor, getResources().getColor(R.color.progress_start_color));
      mProgressEndColor = ta.getColor(R.styleable.GameDownloadView_gameProgressEndColor, getResources().getColor(R.color.progress_end_color));
      mBorderStartColor = ta.getColor(R.styleable.GameDownloadView_gameBorderStartColor, getResources().getColor(R.color.border_start_color));
      mBorderEndColor = ta.getColor(R.styleable.GameDownloadView_gameBorderEndColor, getResources().getColor(R.color.border_end_color));
      ta.recycle();
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      // 解决文字有时候画不出问题
      setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    if(mTextBgBitmap == null){
      Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.progress_bg);
      mTextBgBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
    }
    mMarginTop = mTextBgBitmap.getHeight();
    mMarginHorizontal = mTextBgBitmap.getWidth() / 2;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    drawBackground(canvas);
    drawProgress(canvas);
    drawText(canvas);
  }

  // 不同的状态对应的背景不同
  private void drawBackground(Canvas canvas) {
    if (mBackgroundPaint == null) {
      mBackgroundPaint = new Paint();
      mBackgroundPaint.setAntiAlias(true);
      mBackgroundPaint.setStyle(Paint.Style.FILL);
    }
    if (mBackgroundBounds == null) {
      mBackgroundBounds = new RectF();
    }
    mBackgroundBounds.left = mMarginHorizontal;
    mBackgroundBounds.top = mMarginTop;
    mBackgroundBounds.right = getWidth() - mMarginHorizontal;
    mBackgroundBounds.bottom = mMarginTop + mProgressBarHeight;
    mBackgroundPaint.setStyle(Paint.Style.FILL);
    mBackgroundPaint.setColor(mBgIdleGreyColor);
    canvas.drawRoundRect(mBackgroundBounds, mRadius, mRadius, mBackgroundPaint);
  }

  private void drawProgress(Canvas canvas) {
    if (mProgressPaint == null) {
      mProgressPaint = new Paint();
      mProgressPaint.setAntiAlias(true);
      mProgressPaint.setStyle(Paint.Style.FILL);
    }
    if (mProgressBounds == null) {
      mProgressBounds = new RectF();
      mProgressBounds.left = mMarginHorizontal;
      mProgressBounds.top = mMarginTop;
      mProgressBounds.bottom = mMarginTop + mProgressBarHeight;
    }

    int sc = canvas.saveLayer(0, mBackgroundBounds.top, mBackgroundBounds.right ,
        mBackgroundBounds.bottom, null, Canvas.ALL_SAVE_FLAG);
    canvas.drawBitmap(makeDst(), 0, 0, mPaint);
    mPaint.setXfermode(mPorterDuffXfermode);
    canvas.drawBitmap(makeSrc(), 0, 0, mPaint);
    // 边框暂时先不画了
//    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
//    canvas.drawBitmap(makeBorder(), 0, 0, mPaint);
    mPaint.setXfermode(null);
    canvas.restoreToCount(sc);

    int sc2 = canvas.saveLayer(0, mBackgroundBounds.top, mBackgroundBounds.right,
        mBackgroundBounds.bottom, null, Canvas.ALL_SAVE_FLAG);
    canvas.drawBitmap(makeShaderDst(), 0, 0, mPaint);
    mPaint.setXfermode(mPorterDuffXfermode);
    canvas.translate(mBackgroundOffsetX, 0);
    Bitmap backgroudShader = makeShaderSrc();
    if (backgroudShader.getWidth() < mBackgroundBounds.right) {
      if (mBackgroundOffsetX >= 2 * backgroudShader.getWidth() - mBackgroundBounds.right) {
        mBackgroundOffsetX = 0;
      }
      mBackgroundOffsetX += PROGRESS_BG_SHADER_TIME;
      canvas.drawBitmap(backgroudShader, mBackgroundBounds.right - 2 * backgroudShader.getWidth(), mBackgroundBounds.top, mPaint);
      canvas.drawBitmap(backgroudShader, mBackgroundBounds.right - backgroudShader.getWidth(), mBackgroundBounds.top, mPaint);
    } else {
      if (mBackgroundOffsetX >= backgroudShader.getWidth()) {
        mBackgroundOffsetX = 0;
      }
      mBackgroundOffsetX += PROGRESS_BG_SHADER_TIME;
      canvas.drawBitmap(backgroudShader, -1 * backgroudShader.getWidth(), mBackgroundBounds.top, mPaint);
      canvas.drawBitmap(backgroudShader, 0, mBackgroundBounds.top, mPaint);
    }
    mPaint.setXfermode(null);
    canvas.restoreToCount(sc2);
    removeCallbacks(mInvalidateRunnable);
    postDelayed(mInvalidateRunnable, DELAY_INVALIDATE_TIME);
  }

  Bitmap makeDst() {
    if (mWhiteBitmap != null && mWhiteBitmap.getWidth() == mBackgroundBounds.right) {
      return mWhiteBitmap;
    }
    Bitmap bm = Bitmap.createBitmap((int) mBackgroundBounds.right, (int) mBackgroundBounds.bottom,
        Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bm);
    mProgressPaint.setColor(Color.WHITE);
    canvas.drawRoundRect(mBackgroundBounds, mRadius, mRadius, mProgressPaint);
    mWhiteBitmap = bm;
    return bm;
  }

  Bitmap makeSrc() {
    if (mProgressBitmap == null || mProgressBitmap.getWidth() != mBackgroundBounds.right) {
      mProgressBitmap = Bitmap.createBitmap((int) mBackgroundBounds.right, (int) mBackgroundBounds.bottom, Bitmap.Config.ARGB_8888);
    }
    mProgressBounds.right = mMarginHorizontal + (getWidth() - 2 * mMarginHorizontal) * mProgress / PROGRESS_SUCCESS;
    mClearPaint.setXfermode(mCleanXfermode);
    mBitmapCanvas.drawPaint(mClearPaint);
    mBitmapCanvas.setBitmap(mProgressBitmap);
    LinearGradient lg = new LinearGradient(0, 0, mProgressBounds.right, mProgressBarHeight,
        mProgressStartColor, mProgressEndColor, Shader.TileMode.MIRROR);
    mProgressPaint.setShader(lg);
    mBitmapCanvas.drawRoundRect(mProgressBounds, mRadius, mRadius, mProgressPaint);
    return mProgressBitmap;
  }

  Bitmap makeBorder(){
    if (mBorderPaint == null) {
      mBorderPaint = new Paint();
      mBorderPaint.setAntiAlias(true);
      mBorderPaint.setStyle(Paint.Style.STROKE);
    }
    if (mBorderBitmap == null || mBorderBitmap.getWidth() != mBackgroundBounds.right) {
      mBorderBitmap = Bitmap.createBitmap((int) mBackgroundBounds.right, (int) mBackgroundBounds.bottom, Bitmap.Config.ARGB_8888);
    }
    mClearPaint.setXfermode(mCleanXfermode);
    mBorderCanvas.drawPaint(mClearPaint);
    mBorderCanvas.setBitmap(mBorderBitmap);
    LinearGradient borderLg = new LinearGradient(0, 0, mProgressBounds.right, mProgressBarHeight,
        mBorderStartColor, mBorderEndColor, Shader.TileMode.MIRROR);
    mBorderPaint.setShader(borderLg);
    mBorderPaint.setStyle(Paint.Style.STROKE);
//    mBorderPaint.setStrokeWidth(mBgBorderWidth);
    mBorderCanvas.drawRoundRect(mProgressBounds, mRadius, mRadius, mBorderPaint);
    return mBorderBitmap;
  }

  private void drawText(Canvas canvas){
    if(mTextPaint == null){
      mTextPaint = new Paint();
      mTextPaint.setAntiAlias(true);
      mTextPaint.setShader(null);
      mTextPaint.setColor(Color.WHITE);
      mTextPaint.setTextSize(mTextSize);
    }
    float bgWidth = mTextBgBitmap.getWidth();
//    float left = Math.max(0, mProgressBounds.right - bgWidth / 2);
//    left = Math.min(left, mBackgroundBounds.right - bgWidth);
    float left = mProgressBounds.right - bgWidth / 2;
    canvas.drawBitmap(mTextBgBitmap, left, 0, mTextPaint);
    float tvWidth = mTextPaint.measureText(mProgress + "%");
    canvas.drawText(mProgress + "%", left + (bgWidth - tvWidth) / 2, 35, mTextPaint);
  }

  Bitmap makeShaderDst() {
    if (mShaderBgBitmap == null || mShaderBgBitmap.getWidth() != mBackgroundBounds.right) {
      mShaderBgBitmap = Bitmap.createBitmap((int) mBackgroundBounds.right, (int) mBackgroundBounds.bottom, Bitmap.Config.ARGB_8888);
    }
    mClearPaint.setXfermode(mCleanXfermode);
    mShaderBgCanvas.drawPaint(mClearPaint);
    mShaderBgCanvas.setBitmap(mShaderBgBitmap);
    mShaderBgCanvas.clipRect(0, 0, mProgressBounds.right, mProgressBounds.bottom);
    mProgressPaint.setColor(Color.WHITE);
    mShaderBgCanvas.drawRoundRect(mProgressBounds, mRadius, mRadius, mProgressPaint);
    return mShaderBgBitmap;
  }

  Bitmap makeShaderSrc() {
    if (mProgressShaderBitmap == null) {
      Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loading_view);
      mProgressShaderBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
    }
    mClearPaint.setXfermode(mCleanXfermode);
    mBitmapShaderCanvas.drawPaint(mClearPaint);
    mBitmapShaderCanvas.drawBitmap(mProgressShaderBitmap,  0, 0, mProgressShaderPaint);
    return mProgressShaderBitmap;
  }
}

