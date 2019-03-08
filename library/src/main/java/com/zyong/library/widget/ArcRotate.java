package com.zyong.library.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.zyong.library.R;
import com.zyong.library.utils.MiscUtil;

/**
 * Created by yong on 2018/2/9.
 */
public class ArcRotate extends View {

    private Paint mArcPaint;
    private RectF mRectF;
    private Point mCenterPoint;
    private float arcWidth = 20;
    private int mRadius;
    private int mDefaultSize = 100;
    private SweepGradient mSweepGradient;
    private Matrix matrix;
    private float rotate;
    private Context mContext;
    private float startAngle;
    private float sweepAngle;
    private int arcColor;
    private long animationDur;
    private ValueAnimator mAnimator;

    public ArcRotate(Context context) {
        super(context);
    }

    public ArcRotate(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ArcRotate(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        initAttrs(attrs);
        initPaint();
        initAnimation();
        startAnimation();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray type = mContext.obtainStyledAttributes(attrs, R.styleable.ArcRotate);
        animationDur = type.getInt(R.styleable.ArcRotate_animationDur, 4000);
        arcWidth = type.getDimension(R.styleable.ArcRotate_arcWidth, 10);
        startAngle = type.getFloat(R.styleable.ArcRotate_startAngle, 0);
        sweepAngle = type.getFloat(R.styleable.ArcRotate_sweepAngle, 180);
        arcColor = type.getColor(R.styleable.ArcRotate_arcColor, Color.BLUE);
    }

    private void initPaint() {
        mRectF = new RectF();
        mCenterPoint = new Point();
        matrix = new Matrix();
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeWidth(arcWidth);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeCap(Paint.Cap.BUTT);

    }

    private void updatePaint() {
        // 设置渐变
        mSweepGradient = new SweepGradient(mCenterPoint.x,
                mCenterPoint.y,
                new int[]{0x0000ff,//初始角度颜色
                        arcColor}, new float[]{0.0f, 0.5f});//结束角度颜色
//        mSweepGradient = new SweepGradient(mCenterPoint.x,
//                mCenterPoint.y,
//                0x000000ff,//初始角度颜色
//                arcColor);//结束角度颜色
        mArcPaint.setShader(mSweepGradient);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MiscUtil.measure(widthMeasureSpec, mDefaultSize),
                MiscUtil.measure(heightMeasureSpec, mDefaultSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int minSize = Math.min(w - getPaddingLeft() - getPaddingRight() - 2 * (int) arcWidth,
                h - getPaddingTop() - getPaddingBottom() - 2 * (int) arcWidth);
        //减去圆弧的宽度，否则会造成部分圆弧绘制在外围
        mRadius = minSize / 2;
        mCenterPoint.x = w / 2;
        mCenterPoint.y = h / 2;
        //绘制圆弧的边界
        mRectF.left = mCenterPoint.x - mRadius - arcWidth / 2;
        mRectF.top = mCenterPoint.y - mRadius - arcWidth / 2;
        mRectF.right = mCenterPoint.x + mRadius + arcWidth / 2;
        mRectF.bottom = mCenterPoint.y + mRadius + arcWidth / 2;
        updatePaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
    }

    private void initAnimation() {
        mAnimator = ValueAnimator.ofFloat(0, 360f);
        mAnimator.setDuration(animationDur);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rotate = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    public void startAnimation() {
        mAnimator.start();
    }

    public void stopAnimation() {
        mAnimator.end();
    }

    public void setArcAngle(float startAngle, float sweepAngle) {
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
    }

    public void setAnimationDur(long during) {
        animationDur = during;
    }

    private void drawArc(Canvas canvas) {
        matrix.setRotate(rotate, mCenterPoint.x, mCenterPoint.y);
        canvas.concat(matrix);
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, mArcPaint);
    }
}
