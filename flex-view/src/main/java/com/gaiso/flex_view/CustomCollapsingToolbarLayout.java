package com.gaiso.flex_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Jie on 2016/7/11.
 */
public class CustomCollapsingToolbarLayout extends RelativeLayout implements ICollapseHeader {

    private static final String TAG = CustomCollapsingToolbarLayout.class.getName();
    private Drawable mContentScrim;
    private int mScrimAlpha;
    private int mTempOffset;
    private float mTempPercentage;
    private int mCollapseHeight;

    private CustomCoordinatorLayout.OnOffsetChangedListener mOnOffsetChangedListener;

    public CustomCollapsingToolbarLayout(Context context) {
        this(context, null, 0);
    }

    public CustomCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomCollapsingToolbarLayout);
        mCollapseHeight = a.getDimensionPixelSize(R.styleable.CustomCollapsingToolbarLayout_collapseHeight, 0);
        setContentScrim(a.getDrawable(R.styleable.CustomCollapsingToolbarLayout_layoutScrim));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mCollapseHeight > height) {
            mCollapseHeight = height;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewParent parent = getParent();
        if (mOnOffsetChangedListener == null)
            mOnOffsetChangedListener = new OffsetUpdateListener();
        ((CustomCoordinatorLayout) parent).addOnOffsetChangedListener(mOnOffsetChangedListener);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mContentScrim != null) {
            mContentScrim.setBounds(0, 0, w, h);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        LayoutParams params = (LayoutParams) child.getLayoutParams();
        if (params.mDrawScrim == LayoutParams.DRAW_SCRIM_ALWAYS && mContentScrim != null && mScrimAlpha > 0) {
            mContentScrim.setAlpha(mScrimAlpha);
            mContentScrim.draw(canvas);
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    protected void onDetachedFromWindow() {
        final ViewParent parent = getParent();
        if (mOnOffsetChangedListener != null && parent instanceof CustomCoordinatorLayout) {
            ((CustomCoordinatorLayout) parent).removeOnOffsetChangedListener(mOnOffsetChangedListener);
        }

        super.onDetachedFromWindow();
    }

    public void setContentScrim(/*@Nullable*/ Drawable drawable) {
        if (mContentScrim != drawable) {
            if (mContentScrim != null) {
                mContentScrim.setCallback(null);
            }
            mContentScrim = drawable != null ? drawable.mutate() : null;
            if (mContentScrim != null) {
                mContentScrim.setBounds(0, 0, getWidth(), getHeight());
                mContentScrim.setCallback(this);
                mContentScrim.setAlpha(mScrimAlpha);
            }
            postInvalidate();
        }
    }

    public void restoreChildViews() {
        for (int i = 0, z = getChildCount(); i < z; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            switch (lp.mCollapseMode) {
                case LayoutParams.COLLAPSE_MODE_PIN:
                    child.offsetTopAndBottom(-mTempOffset);
                    break;
                case LayoutParams.COLLAPSE_MODE_PARALLAX:
                    child.offsetTopAndBottom((int) (mTempPercentage * lp.mParallaxRange));
                    break;
            }
        }
    }

    private class OffsetUpdateListener implements CustomCoordinatorLayout.OnOffsetChangedListener {

        @Override
        public void onOffsetChanged(CustomCoordinatorLayout coordinatorLayout, int verticalOffset, float percentage) {
            for (int i = 0, z = getChildCount(); i < z; i++) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                switch (lp.mCollapseMode) {
                    case LayoutParams.COLLAPSE_MODE_PIN:
                        if (verticalOffset != mTempOffset) {
                            child.offsetTopAndBottom(-verticalOffset);
                            mTempOffset = verticalOffset;
                        }
                        break;
                    case LayoutParams.COLLAPSE_MODE_PARALLAX:
                        if (percentage != mTempPercentage) {
                            child.offsetTopAndBottom((int) (percentage * lp.mParallaxRange));
                            mTempPercentage = percentage;
                        }
                        break;
                }
                mScrimAlpha = (int) (percentage * 255);
                postInvalidateOnAnimation();
            }
        }
    }

    @Override
    public boolean isCollapsing() {
        return getBottom() > mCollapseHeight && getBottom() < getHeight();
    }

    @Override
    public boolean isTotalCollapsed() {
        return getBottom() == mCollapseHeight;
    }

    @Override
    public boolean isTotalExpand() {
        return getBottom() == getHeight();
    }

    @Override
    public int getTotalCollapseRange() {
        return getHeight() - mCollapseHeight;
    }

    @Override
    public int getCollapseHeight() {
        return mCollapseHeight;
    }

    @Override
    public void setCollapseHeight(int height) {
        mCollapseHeight = height;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(super.generateDefaultLayoutParams());
    }

    @Override
    public RelativeLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected RelativeLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {

        public static final int COLLAPSE_MODE_NONE = 0;

        public static final int COLLAPSE_MODE_PIN = 1;

        public static final int COLLAPSE_MODE_PARALLAX = 2;

        public static final int DRAW_SCRIM_NEVER = 0;

        public static final int DRAW_SCRIM_ALWAYS = 1;

        private static final int DEFAULT_PARALLAX_MULTIPLIER = 0;

        int mCollapseMode;
        int mDrawScrim;
        int mParallaxRange;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.CustomCollapsingToolbarLayout_LayoutParams);
            mCollapseMode = a.getInt(R.styleable.CustomCollapsingToolbarLayout_LayoutParams_collapseMode,
                    COLLAPSE_MODE_NONE);
            mDrawScrim = a.getInt(R.styleable.CustomCollapsingToolbarLayout_LayoutParams_drawScrim, DRAW_SCRIM_NEVER);
            mParallaxRange = a.getDimensionPixelSize(R.styleable
                    .CustomCollapsingToolbarLayout_LayoutParams_parallax_range, DEFAULT_PARALLAX_MULTIPLIER);
            a.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(RelativeLayout.LayoutParams source) {
            super(source);
        }


        @IntDef({
                COLLAPSE_MODE_NONE,
                COLLAPSE_MODE_PIN,
                COLLAPSE_MODE_PARALLAX
        })
        @Retention(RetentionPolicy.SOURCE)
        @interface CollapseMode {
        }

        public void setCollapseMode(/*@CollapseMode*/ int collapseMode) {
            mCollapseMode = collapseMode;
        }

        @CollapseMode
        public int getCollapseMode() {
            return mCollapseMode;
        }

        public void setParallaxMultiplier(int multiplier) {
            mParallaxRange = multiplier;
        }

        public float getParallaxMultiplier() {
            return mParallaxRange;
        }
    }
}
