package com.zyong.library.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.zyong.library.R;
import com.zyong.library.utils.MiscUtil;

/**
 * @Description: 自定义ItemView
 * Created by yong on 2019/3/8 18:10.
 */
public class ItemView extends RelativeLayout {
    private final String TAG = this.getClass().getCanonicalName();
    private static final int NORMAL = 0;
    private static final int SWITCH = 1;
    private int mDefaultSize;
    private ImageView mIvLeftIcon;
    private TextView mTvLeft;
    private ImageView mIvRightIcon;
    private TextView mTvRight;
    private Switch mSwitch;
    private View mDivide;
    private int styleType;
    private OnClickListener mClickListener;
    private View mItemView;

    public ItemView(Context context) {
        super(context);
        init(context, null);
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MiscUtil.measure(heightMeasureSpec, mDefaultSize));
    }

    private void init(Context context, AttributeSet attrs) {
        initView(context);
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemView);
            Drawable leftIcon = typedArray.getDrawable(R.styleable.ItemView_left_icon);
            if (leftIcon != null) {
                mIvLeftIcon.setBackground(leftIcon);
            }
            boolean leftIconVisible = typedArray.getBoolean(R.styleable.ItemView_left_icon_visible, true);
            mIvLeftIcon.setVisibility(leftIconVisible ? VISIBLE : GONE);
            String leftTxt = typedArray.getString(R.styleable.ItemView_left_txt);
            if (!TextUtils.isEmpty(leftTxt)) {
                mTvLeft.setText(leftTxt);
            }
            float leftTxtSize = typedArray.getDimensionPixelSize(R.styleable.ItemView_left_txt_size, -1);
            if (leftTxtSize != -1) {
                mTvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTxtSize);
            }
            ColorStateList leftTxtColor = typedArray.getColorStateList(R.styleable.ItemView_left_txt_color);
            if (leftTxtColor != null) {
                mTvLeft.setTextColor(leftTxtColor);
            }
            String rightTxt = typedArray.getString(R.styleable.ItemView_right_txt);
            if (!TextUtils.isEmpty(rightTxt)) {
                mTvRight.setText(rightTxt);
            }
            float rightTxtSize = typedArray.getDimensionPixelSize(R.styleable.ItemView_right_txt_size, -1);
            if (rightTxtSize != -1) {
                mTvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTxtSize);
            }
            ColorStateList rightTxtColor = typedArray.getColorStateList(R.styleable.ItemView_right_txt_color);
            if (rightTxtColor != null) {
                mTvRight.setTextColor(rightTxtColor);
            }
            Drawable rightIcon = typedArray.getDrawable(R.styleable.ItemView_right_icon);
            if (null != rightIcon) {
                mIvRightIcon.setBackground(rightIcon);
            }
            boolean rightIconVisible = typedArray.getBoolean(R.styleable.ItemView_right_icon_visible, true);
            mIvRightIcon.setVisibility(rightIconVisible ? VISIBLE : GONE);
            boolean divideVisible = typedArray.getBoolean(R.styleable.ItemView_divide_line, true);
            mDivide.setVisibility(divideVisible ? VISIBLE : GONE);
            boolean aBoolean = typedArray.getBoolean(R.styleable.ItemView_custom_switch, false);
            if (aBoolean) {
                setCustomSwitch();
            }
            styleType = typedArray.getInt(R.styleable.ItemView_right_style, 0);
            setItemType(styleType);
            typedArray.recycle();
        }
    }

    private void initView(Context context) {
        mDefaultSize = MiscUtil.dipToPx(context, 50);
        mItemView = inflate(context, R.layout.item_view, this);
        mIvLeftIcon = (ImageView) findViewById(R.id.iv_left_icon);
        mTvLeft = (TextView) findViewById(R.id.tv_left);
        mIvRightIcon = (ImageView) findViewById(R.id.iv_right_icon);
        mTvRight = (TextView) findViewById(R.id.tv_right);
        mSwitch = (Switch) findViewById(R.id.switch_view);
        mDivide = (View) findViewById(R.id.divide);
    }

    public void setCustomSwitch() {
        mSwitch.setThumbDrawable(getContext().getResources().getDrawable(R.drawable.item_switch_click));
        mSwitch.setTrackResource(android.R.color.transparent);
        mSwitch.setBackground(getContext().getResources().getDrawable(R.drawable.item_switch_bg));
    }

    public void setLeftIcon(Drawable leftIcon) {
        mIvLeftIcon.setBackground(leftIcon);
    }

    public void setLeftIconVisible(boolean visibility) {
        mIvLeftIcon.setVisibility(visibility ? VISIBLE : GONE);
    }

    public void setLeftTxt(String leftTxt) {
        mTvLeft.setText(leftTxt);
    }

    public void setLeftTxtSize(float txtSize) {
        mTvLeft.setTextSize(txtSize);
    }

    public void setLeftTxtColor(int txtColor) {
        mTvLeft.setTextColor(txtColor);
    }

    public void setRightTxt(String rightTxt) {
        mTvRight.setText(rightTxt);
    }

    public void setRightTxtSize(float txtSize) {
        mTvRight.setTextSize(txtSize);
    }

    public void setRightTxtColor(int txtColor) {
        mTvRight.setTextColor(txtColor);
    }

    public void setRightIcon(Drawable rightIcon) {
        mIvRightIcon.setBackground(rightIcon);
    }

    public void setRightIconVisible(boolean visible) {
        if (styleType == NORMAL) {
            mIvRightIcon.setVisibility(visible ? VISIBLE : GONE);
        }
    }

    public void setChecked(boolean checked) {
        mSwitch.setChecked(checked);
    }

    public boolean isChecked() {
        return mSwitch.isChecked();
    }

    public void setItemType(int styleType) {
        this.styleType = styleType;
        if (styleType == NORMAL) {//normal
            mTvRight.setVisibility(VISIBLE);
            mIvRightIcon.setVisibility(VISIBLE);
            mSwitch.setVisibility(GONE);
            mItemView.setClickable(true);
        } else if (styleType == SWITCH) {//switch
            mTvRight.setVisibility(GONE);
            mIvRightIcon.setVisibility(GONE);
            mSwitch.setVisibility(VISIBLE);
            mItemView.setClickable(false);
        }
    }

    public void setDivideVisible(boolean visible) {
        mDivide.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setEnable(boolean enable) {
        mSwitch.setEnabled(enable);
        mTvLeft.setEnabled(enable);
        mItemView.setEnabled(enable);
    }

    public interface OnClickListener {
        void onClick(View view);
    }

    public void setListener(OnClickListener changedListener) {
        if (styleType == NORMAL) {
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onClick(mItemView);
                    }
                }
            });
        } else {
            mSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onClick(mItemView);
                    }
                }
            });
        }
        this.mClickListener = changedListener;
    }
}
