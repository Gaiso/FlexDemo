package com.gaiso.flex_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jie on 2016/7/11.
 */
public class CustomCoordinatorLayout extends LinearLayout {

    private static final String TAG = CustomCoordinatorLayout.class.getName();
    public static final int NONE_CAN_MOVE_HORIZONTALLY = 0;
    public static final int HEADER_VIEW_CAN_MOVE_HORIZONTALLY = 1;
    public static final int CONTENT_VIEW_CAN_MOVE_HORIZONTALLY = 2;
    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private int mVerticalTouchSlop;
    private int mHorizontalTouchSlop;
    private Scroller mScroller;

    private MotionEvent mLastMoveEvent;

    private ICollapseHeader mCollapseHeader;
    private IScrollHandler mScrollHandler;
    private float mFirstPositionY;
    private float mLastPositionX;
    private float mLastPositionY;
    private boolean mIsSendCancelEvent;
    private boolean mIsHorizontalDragging;


    private int mScrollY;
    private final List<OnOffsetChangedListener> mListeners;

    private int mTempTopOffset;
    private int mWhichCanMoveHorizontally = NONE_CAN_MOVE_HORIZONTALLY;

    public CustomCoordinatorLayout(Context context) {
        this(context, null, 0);
    }

    public CustomCoordinatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mListeners = new ArrayList<>();
        init();
    }

    public interface OnOffsetChangedListener {

        void onOffsetChanged(CustomCoordinatorLayout coordinatorLayout, int verticalOffset, float percentage);
    }

    public void addOnOffsetChangedListener(OnOffsetChangedListener listener) {
        if (listener != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void removeOnOffsetChangedListener(OnOffsetChangedListener listener) {
        if (listener != null) {
            mListeners.remove(listener);
        }
    }

    private void init() {
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mVerticalTouchSlop = configuration.getScaledTouchSlop();
        mHorizontalTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mScroller = new Scroller(getContext());
    }

    @Override
    protected void onFinishInflate() {
        final int childCount = getChildCount();

        if (childCount == 2) {
            View child = getChildAt(0);
            if (!(child instanceof ICollapseHeader)) {
                throw new IllegalStateException("header view must implement ICollapseHeader");
            }
            mCollapseHeader = (ICollapseHeader) child;
        } else {
            throw new IllegalStateException("CustomCoordinatorLayout2 only can host 2 elements");
        }

        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View header = getChildAt(0);
        View content = getChildAt(1);

        if (header != null) {
            measureChildWithMargins(header, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }

        if (content != null) {
            measureChildWithMargins(content, widthMeasureSpec, 0, heightMeasureSpec, mCollapseHeader
                    .getCollapseHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        View header = getChildAt(0);
        View content = getChildAt(1);

        int left = 0;
        int top = 0 + mTempTopOffset;
        int right = left + header.getMeasuredWidth();
        int bottom = header.getMeasuredHeight() + top;

        header.layout(left, top, right, bottom);
        mScrollHandler.translate(0, bottom, content.getMeasuredWidth(), bottom + content.getMeasuredHeight());

        if (header instanceof CustomCollapsingToolbarLayout) {
            ((CustomCollapsingToolbarLayout) header).restoreChildViews();
        }
    }

    /**
     * 必须调用该方法
     *
     * @param scrollHandler
     */
    public void setScrollHandler(IScrollHandler scrollHandler) {
        mScrollHandler = scrollHandler;
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        obtainVelocityTracker(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScrollY = 0;

                mFirstPositionY = ev.getY();
                mLastPositionX = ev.getX();
                mLastPositionY = ev.getY();

                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                mIsHorizontalDragging = false;

                dispatchTouchEventSupper(ev);
                return true;
            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = ev;
                float currentPositionX = ev.getX();
                float currentPositionY = ev.getY();
                float distanceY = currentPositionY - mLastPositionY;
                float distanceX = currentPositionX - mLastPositionX;

                if (canHorizontalMove() && !mIsHorizontalDragging && Math.abs(distanceX) > mHorizontalTouchSlop &&
                        Math.abs(distanceX * 0.5) > Math.abs(distanceY)) {
                    mIsHorizontalDragging = true;
                }

                if (mIsHorizontalDragging && detectTouchRegion(currentPositionY) == mWhichCanMoveHorizontally) {
                    return dispatchTouchEventSupper(ev);
                }

                mTempTopOffset = getChildAt(0).getTop();
                boolean moveDown = distanceY > 0;
                mLastPositionX = currentPositionX;
                mLastPositionY = currentPositionY;
                if (moveDown) {
                    if (mCollapseHeader.isTotalCollapsed() && !mScrollHandler.isTop()) {
                        return dispatchTouchEventSupper(ev);
                    } else {
                        move(distanceY);
                        return true;
                    }
                } else {
                    if (!mCollapseHeader.isTotalCollapsed()) {
                        move(distanceY);
                        return true;
                    } else {
                        if (mIsSendCancelEvent) {
                            sendDownEvent();
                            mIsSendCancelEvent = false;
                        }
                        return dispatchTouchEventSupper(ev);
                    }
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity && !mIsHorizontalDragging) {
                    fling(velocityY);
                }
                recycleVelocityTracker();
                return dispatchTouchEventSupper(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    private int move(float deltaY) {
        if (Math.abs(mFirstPositionY - mLastPositionY) > mVerticalTouchSlop) {
            sendCancelEvent();
            mIsSendCancelEvent = true;
        }
        View child = getChildAt(0);
        int height = child.getHeight();
        int distanceY = calculateDistance(deltaY);
        int bottom = child.getBottom() + distanceY;
        int top = 0 - height + bottom;
        child.layout(0, top, child.getWidth(), bottom);
        mScrollHandler.translate(0, bottom, getWidth(), getHeight());

        int offset = child.getTop();
        float percent = offset / (float) (mCollapseHeader.getTotalCollapseRange());

        if (mListeners != null) {
            for (OnOffsetChangedListener listener : mListeners) {
                listener.onOffsetChanged(this, offset, Math.abs(percent));
            }
        }
        return distanceY;
    }

    private int detectTouchRegion(float positionY) {
        View header = getChildAt(0);
        View content = getChildAt(1);
        if (positionY < header.getBottom())
            return HEADER_VIEW_CAN_MOVE_HORIZONTALLY;
        else if (positionY > content.getTop())
            return CONTENT_VIEW_CAN_MOVE_HORIZONTALLY;
        else
            return NONE_CAN_MOVE_HORIZONTALLY;

    }

    public final void smoothScroll2Top() {
        mScrollY = 0;
        mScroller.startScroll(0, 0, 0, Integer.MIN_VALUE);
        postInvalidate();
    }

    private boolean canHorizontalMove() {
        return mWhichCanMoveHorizontally != NONE_CAN_MOVE_HORIZONTALLY;
    }

    private void fling(int velocityY) {
        mScroller.fling(0, 0, 0, -velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE,
                Integer.MIN_VALUE, Integer.MAX_VALUE);
        postInvalidate();
    }

    private void sendCancelEvent() {
        if (mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration
                .getLongPressTimeout(), MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    private void sendDownEvent() {
        if (mLastMoveEvent == null) {
            return;
        }
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last
                .getX(), last.getY() - mVerticalTouchSlop * 2, last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    public void setHorizontalMoveChildView(int which) {
        mWhichCanMoveHorizontally = which;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int distance = mScroller.getCurrY() - mScrollY;

            if (distance > 0) {//手势往上滑
                if (!mCollapseHeader.isTotalCollapsed()) {
                    move(-distance);
                } else {
                    if (mScrollHandler.isBottom())
                        mScroller.abortAnimation();
                    mScrollHandler.scrollBy(distance);
                }
            } else {//手势往下滑
                if (!mCollapseHeader.isTotalExpand() && mScrollHandler.isTop()) {
                    move(-distance);
                }
            }
            mTempTopOffset = getChildAt(0).getTop();
            mScrollY = mScroller.getCurrY();
            postInvalidate();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private int calculateDistance(float delta) {
        View child = getChildAt(0);
        int bottom = child.getBottom();
        int height = child.getHeight();
        int distance = 0;
        int collapseHeight = mCollapseHeader.getCollapseHeight();

        if (delta < 0) {//手势向上滑
            if (bottom == collapseHeight) {
                distance = 0;
            } else if (bottom + delta > collapseHeight) {
                distance = (int) delta;
            } else if (bottom + delta <= collapseHeight) {
                distance = collapseHeight - bottom;
            }

        } else {
            if (bottom == height) {
                distance = 0;
            } else if (bottom + delta < height) {
                distance = (int) delta;
            } else if (bottom + delta >= height) {
                distance = height - bottom;
            }
        }

        return distance;
    }
}
