package com.zyong.library.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.zyong.library.R;
import com.zyong.library.utils.MiscUtil;

/**
 * Created by yong on 2018/2/9.
 */
public class CircleImageView extends ImageView {

    private final String TAG = getClass().getSimpleName();
    private static final int CLIP_SAVE_FLAG = 0x02;

    private Paint mArcPaint;
    private Paint mArcPaint2;
    private RectF mRectF;
    private Point mCenterPoint;
    private float arcWidth = 20;
    private int mRadius;
    private int mRadiusCircle;
    private int mRadiusBitmap;
    private int mDefaultSize = 200;
    private Matrix matrixArc;
    private float rotate;
    private Context mContext;
    private float startAngle;
    private float sweepAngle;
    private int arcColor;
    private int circleColor;
    private long animationDur;
    private ValueAnimator mAnimator;
    private Paint mCirclePaint;
    private float circleWidth;
    private Path mCirclePath;
    private Paint mLayerPaint;
    private RectF mLayerRect;
    private Paint mCircleBitmapPaint;
    private boolean drawableSettled;
    private Matrix mBitmapMatrix;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs) {
        //显示图片需要设置ScaleType.MATRIX
        setScaleType(ScaleType.MATRIX);
        mContext = context;
        initAttrs(attrs);
        initPaint();
        initAnimation();
        startAnimation();

        initCircleImage();
    }

    private void initCircleImage() {
        mCirclePath = new Path();
        mLayerPaint = new Paint();
        mLayerPaint.setXfermode(null);
        mLayerRect = new RectF();
        mCircleBitmapPaint = new Paint();
        mCircleBitmapPaint.setAntiAlias(true);
        mCircleBitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mBitmapMatrix = new Matrix();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray type = mContext.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        animationDur = type.getInt(R.styleable.CircleImageView_animDur, 4000);
        arcWidth = type.getDimension(R.styleable.CircleImageView_arcWidth, 10);
        circleWidth = type.getDimension(R.styleable.CircleImageView_circleWidth, 10);
        startAngle = type.getFloat(R.styleable.CircleImageView_startAngle, 0);
        sweepAngle = type.getFloat(R.styleable.CircleImageView_sweepAngle, 180);
        arcColor = type.getColor(R.styleable.CircleImageView_arcColor, Color.BLUE);
        circleColor = type.getColor(R.styleable.CircleImageView_arcColor, Color.BLUE);
    }

    private void initPaint() {
        mRectF = new RectF();
        mCenterPoint = new Point();
        matrixArc = new Matrix();
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeWidth(arcWidth);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeCap(Paint.Cap.BUTT);

        mArcPaint2 = new Paint();
        mArcPaint2.setAntiAlias(true);
        mArcPaint2.setStrokeWidth(arcWidth);
        mArcPaint2.setStyle(Paint.Style.STROKE);
        mArcPaint2.setStrokeCap(Paint.Cap.BUTT);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(circleWidth);
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeCap(Paint.Cap.BUTT);

    }

    private void updatePaint() {
        // 设置渐变
        //初始角度颜色
        //结束角度颜色
        SweepGradient sweepGradient = new SweepGradient(mCenterPoint.x,
                mCenterPoint.y,
                new int[]{0x0000ff,//初始角度颜色
                        arcColor},//结束角度颜色
                new float[]{0.0f, 0.25f});//渐变范围
        SweepGradient sweepGradient2 = new SweepGradient(mCenterPoint.x,
                mCenterPoint.y,
                new int[]{0x0000ff,
                        arcColor},
                new float[]{0.5f, 0.75f});
        mArcPaint.setShader(sweepGradient);
        mArcPaint2.setShader(sweepGradient2);
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

        mRadiusCircle = mRadius - 10;
        mRadiusBitmap = mRadiusCircle - 10;
        updatePaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        //对图片缩放平移，调整图片显示位置
        syncDrawableIf();
        if (drawableSettled) {
            //截取圆形显示区域
            int layerID = canvas.saveLayer(mLayerRect, mLayerPaint, CLIP_SAVE_FLAG);//clear background
            mCirclePath.reset();
            mCirclePath.addCircle(mLayerRect.centerX(), mLayerRect.centerY(), mRadiusBitmap, Path.Direction.CCW);
            canvas.clipPath(mCirclePath);

            super.onDraw(canvas);//draw drawable

            mCirclePath.reset();
            mCirclePath.addCircle(mLayerRect.centerX(), mLayerRect.centerY(), mRadiusBitmap - 1f, Path.Direction.CCW);//Antialiasing
            canvas.drawPath(mCirclePath, mCircleBitmapPaint);
            canvas.restoreToCount(layerID);
        } else {
            super.onDraw(canvas);
        }
        canvas.save();
        drawArc(canvas);
        drawCircle(canvas);
        canvas.restore();
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
        matrixArc.setRotate(rotate, mCenterPoint.x, mCenterPoint.y);
        canvas.concat(matrixArc);
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, mArcPaint);
        canvas.drawArc(mRectF, startAngle + 180, sweepAngle, false, mArcPaint2);
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mRadiusCircle, mCirclePaint);
    }

    /********************************** set ImageView ***********************************/

    private boolean syncDrawable() {

        Drawable drawable = getDrawable();
        if (drawable != null) {
            mLayerRect.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            Rect bounds = drawable.getBounds();

            float scale;
            int width = bounds.width();
            int height = bounds.height();

            mBitmapMatrix.set(null);

            if (width > height) {
                scale = ((float) mRadiusBitmap * 2) / height;
            } else {
                scale = ((float) mRadiusBitmap * 2) / width;
            }

            float dx = mCenterPoint.x - width * scale * 0.5f;
            float dy = mCenterPoint.y - height * scale * 0.5f;

            mBitmapMatrix.setScale(scale, scale);
            mBitmapMatrix.postTranslate(dx, dy);
            setImageMatrix(mBitmapMatrix);

            return true;
        }
        return false;
    }

    private void syncDrawableIf() {
        drawableSettled = syncDrawable();
    }
}
