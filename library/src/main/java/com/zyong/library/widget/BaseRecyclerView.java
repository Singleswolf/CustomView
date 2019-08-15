package com.zyong.library.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * @Description:
 * @Author: Yong
 * @Date: 2019/6/23 13:06
 */
public class BaseRecyclerView extends RecyclerView {
    private OnItemClickListener onItemClickListener;
    private GestureDetector gestureDetector;

    public BaseRecyclerView(Context context) {
        super(context);
        init();
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        //intercept touch event for reacting to click event on item of RecyclerView
        gestureDetector.onTouchEvent(e);
        return super.onTouchEvent(e);
    }

    private class GestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            try {
                View view = findChildViewUnder(e.getX(), e.getY());
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, getChildAdapterPosition(view), getAdapter());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View item, int adapterPosition, Adapter adapter);
    }
}