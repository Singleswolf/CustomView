package com.zyong.library.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.zyong.library.R;
import com.zyong.library.utils.MiscUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yong on 2018/2/11.
 */
public class LoadingView extends View {

    private Paint mPaint;
    private int defalutSize = 45;//view默认宽高
    private int circleNum = 5;//圆数量
    private float interval = 10;//间隔
    private float radius = 5;//圆半径
    private float centerX;
    private int centerY;
    private int width;
    private Context mContext;
    private int animationDur;
    private float value;
    private List<ValueAnimator> animatorList;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MiscUtil.measure(widthMeasureSpec, defalutSize),
                MiscUtil.measure(heightMeasureSpec, defalutSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        centerY = h / 2;
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        animatorList = new ArrayList<>();
        initAttrs(attrs);
        initPaint();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        circleNum = typedArray.getInt(R.styleable.LoadingView_circleNum, 5);
        radius = typedArray.getDimension(R.styleable.LoadingView_radius, 20);
        interval = typedArray.getDimension(R.styleable.LoadingView_interval, 10);
        animationDur = typedArray.getInt(R.styleable.LoadingView_animationDur, 600);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        for (int i = 0; i < circleNum; i++) {
            ValueAnimator valueAnimator = null;
            if (animatorList.size() > i) {
                valueAnimator = animatorList.get(i);
            }
            if (valueAnimator == null) {//添加动画后才开始改变半径
                value = radius;
            } else {
                value = (float) valueAnimator.getAnimatedValue();
            }
            centerX = width / 2 - ((circleNum - 1) / 2f - i) * (radius * 2 + interval);//每个点圆心的x位置
            canvas.drawCircle(centerX, centerY, value, mPaint);
        }
    }

    private void initAnimate() {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(radius, 0);
        valueAnimator.setDuration(animationDur);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        animatorList.add(valueAnimator);
        valueAnimator.start();
    }

    public void startAnimator(){
        mRunnable.run();
    }

    public void stopAnimator(){
        Iterator<ValueAnimator> iterator = animatorList.iterator();
        while (iterator.hasNext()) {
            ValueAnimator valueAnimator = iterator.next();
            valueAnimator.end();
            iterator.remove();
        }
    }

    @Override
    public void onWindowSystemUiVisibilityChanged(int visible) {
        super.onWindowSystemUiVisibilityChanged(visible);
        if (visible == View.VISIBLE) {
            //可见
            mRunnable.run();
        } else if (visible == INVISIBLE || visible == GONE) {
            //不可见
            Iterator<ValueAnimator> iterator = animatorList.iterator();
            while (iterator.hasNext()) {
                ValueAnimator valueAnimator = iterator.next();
                valueAnimator.cancel();
                iterator.remove();
            }
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            initAnimate();
            if (animatorList.size() == circleNum) {
                removeCallbacks(mRunnable);
            } else {
                postDelayed(mRunnable, animationDur / circleNum);//按先后顺序添加动画
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (animatorList.isEmpty()){
                    startAnimator();
                } else {
                    stopAnimator();
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
