package com.zyong.library.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

/**
 * @Description: Created by yong on 2019/6/10 14:38.
 */
public class AutoPollRecyclerView extends RecyclerView {
    private static final long TIME_AUTO_POLL = 16;
    AutoPollTask autoPollTask;
    private boolean running; //标示是否正在自动轮询
    private boolean canRun;//标示是否可以自动轮询,可在不需要的是否置false

    public AutoPollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        autoPollTask = new AutoPollTask(this);
    }

    static class AutoPollTask implements Runnable {
        private final WeakReference<AutoPollRecyclerView> mReference;

        //使用弱引用持有外部类引用->防止内存泄漏
        public AutoPollTask(AutoPollRecyclerView reference) {
            this.mReference = new WeakReference<AutoPollRecyclerView>(reference);
        }

        @Override
        public void run() {
            AutoPollRecyclerView recyclerView = mReference.get();
            if (recyclerView != null && recyclerView.running && recyclerView.canRun) {
                recyclerView.scrollBy(2, 2);
                recyclerView.postDelayed(recyclerView.autoPollTask, recyclerView.TIME_AUTO_POLL);
            }
        }
    }

    //开启:如果正在运行,先停止->再开启
    public void start() {
        if (running)
            stop();
        canRun = true;
        running = true;
        postDelayed(autoPollTask, TIME_AUTO_POLL);
    }

    public void stop() {
        running = false;
        removeCallbacks(autoPollTask);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (running)
                    stop();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (canRun)
                    start();
                break;
        }
        return super.onTouchEvent(e);
    }

    public static class LayoutManager extends LinearLayoutManager {
        private static final String TAG = "LayoutManager";
        private boolean pollEnable = true;

        public LayoutManager(Context context) {
            super(context);
            init();
        }

        private void init() {
            setOrientation(HORIZONTAL);
        }

        public void setPollEnable(boolean looperEnable) {
            this.pollEnable = looperEnable;
        }

        @Override
        public RecyclerView.LayoutParams generateDefaultLayoutParams() {
            return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        @Override
        public boolean canScrollHorizontally() {
            return true;
        }

        @Override
        public boolean canScrollVertically() {
            return false;
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            super.onLayoutChildren(recycler, state);
//        if (getItemCount() <= 0) {
//            return;
//        }
//        //preLayout主要支持动画，直接跳过
//        if (state.isPreLayout()) {
//            return;
//        }
//        //将视图分离放入scrap缓存中，以准备重新对view进行排版
//        detachAndScrapAttachedViews(recycler);
//
//        int autualWidth = 0;
//        for (int i = 0; i < getItemCount(); i++) {
//            //初始化，将在屏幕内的view填充
//            View itemView = recycler.getViewForPosition(i);
//            addView(itemView);
//            //测量itemView的宽高
//            measureChildWithMargins(itemView, 0, 0);
//            int width = getDecoratedMeasuredWidth(itemView);
//            int height = getDecoratedMeasuredHeight(itemView);
//            //根据itemView的宽高进行布局
//            layoutDecorated(itemView, autualWidth, 0, autualWidth + width, height);
//
//            autualWidth += width;
//            //如果当前布局过的itemView的宽度总和大于RecyclerView的宽，则不再进行布局
//            if (autualWidth > getWidth()) {
//                break;
//            }
//        }
        }

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
            //1.左右滑动的时候，填充子view
            int travl = fill(dx, recycler, state);
            if (travl == 0) {
                return 0;
            }

            //2.滚动
            offsetChildrenHorizontal(travl * -1);

            //3.回收已经离开界面的
            recyclerHideView(dx, recycler, state);
            return travl;
        }

        /**
         * 左右滑动的时候，填充
         */
        private int fill(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
            if (dx > 0) {
                //标注1.向左滚动
                View lastView = getChildAt(getChildCount() - 1);
                if (lastView == null) {
                    return 0;
                }
                int lastPos = getPosition(lastView);
                //标注2.可见的最后一个itemView完全滑进来了，需要补充新的
                if (lastView.getRight() < getWidth()) {
                    View scrap = null;
                    //标注3.判断可见的最后一个itemView的索引，
                    // 如果是最后一个，则将下一个itemView设置为第一个，否则设置为当前索引的下一个
                    if (lastPos == getItemCount() - 1) {
                        if (pollEnable) {
                            scrap = recycler.getViewForPosition(0);
                        } else {
                            dx = 0;
                        }
                    } else {
                        scrap = recycler.getViewForPosition(lastPos + 1);
                    }
                    if (scrap == null) {
                        return dx;
                    }
                    //标注4.将新的itemViewadd进来并对其测量和布局
                    addView(scrap);
                    measureChildWithMargins(scrap, 0, 0);
                    int width = getDecoratedMeasuredWidth(scrap);
                    int height = getDecoratedMeasuredHeight(scrap);
                    layoutDecorated(scrap, lastView.getRight(), 0,
                            lastView.getRight() + width, height);
                    return dx;
                }
            } else {
                //向右滚动
                View firstView = getChildAt(0);
                if (firstView == null) {
                    return 0;
                }
                int firstPos = getPosition(firstView);

                if (firstView.getLeft() >= 0) {
                    View scrap = null;
                    if (firstPos == 0) {
                        if (pollEnable) {
                            scrap = recycler.getViewForPosition(getItemCount() - 1);
                        } else {
                            dx = 0;
                        }
                    } else {
                        scrap = recycler.getViewForPosition(firstPos - 1);
                    }
                    if (scrap == null) {
                        return 0;
                    }
                    addView(scrap, 0);
                    measureChildWithMargins(scrap, 0, 0);
                    int width = getDecoratedMeasuredWidth(scrap);
                    int height = getDecoratedMeasuredHeight(scrap);
                    layoutDecorated(scrap, firstView.getLeft() - width, 0,
                            firstView.getLeft(), height);
                }
            }
            return dx;
        }

        /**
         * 回收界面不可见的view
         */
        private void recyclerHideView(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view == null) {
                    continue;
                }
                if (dx > 0) {
                    //向左滚动，移除一个左边不在内容里的view
                    if (view.getRight() < 0) {
                        removeAndRecycleView(view, recycler);
                        Log.d(TAG, "循环: 移除 一个view  childCount=" + getChildCount());
                    }
                } else {
                    //向右滚动，移除一个右边不在内容里的view
                    if (view.getLeft() > getWidth()) {
                        removeAndRecycleView(view, recycler);
                        Log.d(TAG, "循环: 移除 一个view  childCount=" + getChildCount());
                    }
                }
            }
        }
    }
}
