package com.ownchan.slideswitchbutton.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ownchan.slideswitchbutton.R;

/**
 * Author: Owen Chan
 * DATE: 2017-03-02.
 */

public class SlideButton extends View {
    private int widthSize = 280;
    private int heightSize = 140;

    private int mInnerRadius;
    private int mOutRadius;

    private static final int PADDING = 0;
    private static final int DEFAULT_COLOR_THEME = Color.parseColor("#ff00ee00");
    private int color_theme;
    private boolean isOpen;

    private Paint mPaint;
    private int mAlpha;

    private RectF mOutRect = new RectF();

    private int changingValue;
    private int maxValue;
    private int minValue;
    private int mStartValue = PADDING;

    private int eventStartX;
    private int eventLastX;
    private int diffX = 0;

    private boolean slideEnable = true;
    private SlideSwitchButton.SlideListener listener;


    public SlideButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        listener = null;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.slideswitch);
        if (typedArray != null) {
            color_theme = typedArray.getColor(R.styleable.slideswitch_themeColor, DEFAULT_COLOR_THEME);
            isOpen = typedArray.getBoolean(R.styleable.slideswitch_isOpen, false);
            typedArray.recycle();
        }
    }

    /**
     *  测量控件的大小, 如果是Wrap content 取默认值
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        int withModel = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightModel = View.MeasureSpec.getMode(heightMeasureSpec);

        if (withModel == View.MeasureSpec.EXACTLY) {
            widthSize = width;
        }
        if (heightModel == View.MeasureSpec.EXACTLY) {
            heightSize = height;
        }
        setMeasuredDimension(widthSize, heightSize);
        initValue();
    }

    public void initValue() {
        mInnerRadius = (heightSize - 2 * PADDING) / 2;
        mOutRadius = heightSize / 2;

        minValue = PADDING;
        maxValue = widthSize - 2 * mInnerRadius - PADDING;
        if (isOpen) {
            changingValue = maxValue;
            mAlpha = 255;
        } else {
            changingValue = PADDING;
            mAlpha = 0;
        }
        mStartValue = changingValue;
    }

    /**
     * 绘制包括两部分 一部分是底部的背景
     * 第二部分是上层滑动的圆
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //绘制底层背景
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(10);
        canvas.drawLine(0, mOutRadius, widthSize, mOutRadius, mPaint);
        //绘制滑动的圆圈
        mPaint.setColor(Color.GRAY);
        canvas.drawCircle(changingValue + mInnerRadius, PADDING + mInnerRadius, mInnerRadius, mPaint);
        mPaint.setColor(color_theme);
        mPaint.setAlpha(mAlpha);
        canvas.drawCircle(changingValue + mInnerRadius, PADDING + mInnerRadius, mInnerRadius, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!slideEnable) {
            return super.onTouchEvent(event);
        }
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                eventStartX = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                eventLastX = (int) event.getRawX();
                diffX = eventLastX - eventStartX;
                int tempX = diffX + mStartValue;
                tempX = (tempX > maxValue ? maxValue : tempX);
                tempX = (tempX < minValue ? minValue : tempX);
                if (tempX >= minValue && tempX <= maxValue) {
                    changingValue = tempX;
                    mAlpha = (int) (255 * (float) tempX / (float) maxValue);
                    invalidateView();
                }
                break;
            case MotionEvent.ACTION_UP:
                int wholeX = (int) (event.getRawX() - eventStartX);
                mStartValue = changingValue;
                boolean toRight;
                toRight = (mStartValue > maxValue / 2);
                if (Math.abs(wholeX) < 3) {
                    toRight = !toRight;
                }
                startAnimator(toRight);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 自动向左或者向右滑动动画
     */
    public void startAnimator(final boolean toRight) {
        ValueAnimator toDestAnim = ValueAnimator.ofInt(changingValue, toRight ? maxValue : minValue);
        toDestAnim.setDuration(300);
        toDestAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        toDestAnim.start();
        toDestAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                changingValue = (Integer) animation.getAnimatedValue();
                mAlpha = (int) (255 * (float) changingValue / (float) maxValue);
                invalidateView();
            }
        });
        toDestAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (toRight) {
                    isOpen = true;
                    if (listener != null) {
                        listener.openState(true, SlideButton.this);
                    }
                    mStartValue = maxValue;
                } else {
                    isOpen = false;
                    if (listener != null) {
                        listener.openState(false, SlideButton.this);
                    }
                    mStartValue = minValue;
                }
            }
        });
    }

    /**
     * 改名开关状态
     */
    public void changeOpenState(boolean isOpen) {
        this.isOpen = isOpen;
        initValue();
        invalidateView();
        if (listener != null) {
            if (isOpen) {
                listener.openState(true, SlideButton.this);
            } else {
                listener.openState(false, SlideButton.this);
            }
        }
    }

    /**
     * 设置是否可以滑动改变开关状态
     */
    public void setSlideEnable(boolean slideEnable) {
        this.slideEnable = slideEnable;
    }

    /**
     * 重新绘制，如果是UI线程调用invalidate()
     * 否则调用postInvalidate();
     */
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void setSlideListener(SlideSwitchButton.SlideListener listener) {
        this.listener = listener;
    }

    public interface SlideListener {
        void openState(boolean isOpen, View view);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.isOpen = bundle.getBoolean("openState");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putBoolean("openState", this.isOpen);
        return bundle;
    }
}
