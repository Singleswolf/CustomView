package com.zyong.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
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
    private ImageView mIvRightArrow;
    private TextView mTvRight;
    private Switch mSwitch;
    private View mDivide;
    private int styleType;
    private OnItemViewClickListener mClickListener;
    private View mItemView;
    private OnCheckedChangedListener mChangedListener;

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
                mIvLeftIcon.setImageDrawable(leftIcon);
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
            int leftTxtColor = typedArray.getColor(R.styleable.ItemView_left_txt_color, 0);
            if (leftTxtColor != 0) {
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
            int rightTxtColor = typedArray.getColor(R.styleable.ItemView_right_txt_color, 0);
            if (rightTxtColor != 0) {
                mTvRight.setTextColor(rightTxtColor);
            }
            Drawable rightArrow = typedArray.getDrawable(R.styleable.ItemView_right_arrow);
            if (null != rightArrow) {
                mIvRightArrow.setImageDrawable(rightArrow);
            }
            boolean rightArrowVisible = typedArray.getBoolean(R.styleable.ItemView_right_arrow_visible, true);
            mIvRightArrow.setVisibility(rightArrowVisible ? VISIBLE : GONE);
            boolean divideVisible = typedArray.getBoolean(R.styleable.ItemView_divide_line, true);
            mDivide.setVisibility(divideVisible ? VISIBLE : GONE);
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
        mIvRightArrow = (ImageView) findViewById(R.id.iv_right_arrow);
        mTvRight = (TextView) findViewById(R.id.tv_right);
        mSwitch = (Switch) findViewById(R.id.switch_view);
        mDivide = (View) findViewById(R.id.divide);
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

    public void setRightArrow(Drawable rightArrow) {
        mIvRightArrow.setBackground(rightArrow);
    }

    public void setRightArrowVisible(boolean visible) {
        if (styleType == NORMAL) {
            mIvRightArrow.setVisibility(visible ? VISIBLE : GONE);
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
            mIvRightArrow.setVisibility(VISIBLE);
            mSwitch.setVisibility(GONE);
            mItemView.setClickable(true);
        } else if (styleType == SWITCH) {//switch
            mTvRight.setVisibility(GONE);
            mIvRightArrow.setVisibility(GONE);
            mSwitch.setVisibility(VISIBLE);
            mItemView.setClickable(false);
        }
    }

    public void setDivideVisible(boolean visible) {
        mDivide.setVisibility(visible ? VISIBLE : GONE);
    }

    public interface OnItemViewClickListener {
        void onClick(View v);
    }

    public void setOnItemViewClickListener(OnItemViewClickListener listener) {
        mItemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onClick(v);
                }
            }
        });
        this.mClickListener = listener;
    }

    public interface OnCheckedChangedListener {
        void onCheckedChanged(boolean checked);
    }

    public void setOnCheckedChangedListener(OnCheckedChangedListener changedListener) {
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mChangedListener != null) {
                    mChangedListener.onCheckedChanged(isChecked);
                }
            }
        });
        this.mChangedListener = changedListener;
    }
}
