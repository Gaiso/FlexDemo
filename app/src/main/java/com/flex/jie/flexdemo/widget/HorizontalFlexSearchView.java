package com.flex.jie.flexdemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.flex.jie.flexdemo.R;

/**
 * Created by Jie on 2016/7/15.
 */
public class HorizontalFlexSearchView extends View {

    private static final float BOUNDARY = 0.6f;
    private static final int DEFAULT_TEXT_SIZE = 15;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final float PADDING_LEFT_DP = 10;
    private static final float DRAWABLE_PADDING = 15;

    private int mCornerRadiu;
    private int[] mBackgroundColors = new int[2];
    private int[] mTextColors = new int[2];
    private Drawable[] mSearchIcons = new Drawable[2];
    private int[] mOffsets = new int[2];
    private String mContent;
    private int mTextSize;

    private int mPaddingLeft;
    private int mDrawablePadding;

    private Drawable mCurrentSearchIcon;

    private TextPaint mTextPaint;
    private Paint mBackgroundPaint;

    private float mRatio;
    private float mBoundary = BOUNDARY;

    public HorizontalFlexSearchView(Context context) {
        this(context, null, 0);
    }

    public HorizontalFlexSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalFlexSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalFlexSearchView);
        mCornerRadiu = a.getDimensionPixelSize(R.styleable.HorizontalFlexSearchView_corner_radiu, 0);
        mBackgroundColors[0] = a.getColor(R.styleable.HorizontalFlexSearchView_background_color_before, 0);
        mBackgroundColors[1] = a.getColor(R.styleable.HorizontalFlexSearchView_background_color_after, 0);
        mTextColors[0] = a.getColor(R.styleable.HorizontalFlexSearchView_text_color_before, DEFAULT_TEXT_COLOR);
        mTextColors[1] = a.getColor(R.styleable.HorizontalFlexSearchView_text_color_after, 0);
        mSearchIcons[0] = a.getDrawable(R.styleable.HorizontalFlexSearchView_search_icon_before);
        mSearchIcons[1] = a.getDrawable(R.styleable.HorizontalFlexSearchView_search_icon_after);
        mContent = a.getString(R.styleable.HorizontalFlexSearchView_search_text);
        mTextSize = a.getDimensionPixelSize(R.styleable.HorizontalFlexSearchView_text_size, DEFAULT_TEXT_SIZE);
        mOffsets[0] = a.getDimensionPixelSize(R.styleable.HorizontalFlexSearchView_left_offset, 0);
        mOffsets[1] = a.getDimensionPixelSize(R.styleable.HorizontalFlexSearchView_right_offset, 0);

        a.recycle();

        init();
    }

    private void init() {
        mCurrentSearchIcon = mSearchIcons[0];

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColors[0]);
        mTextPaint.setTextSize(mTextSize);


        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackgroundColors[0]);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mPaddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PADDING_LEFT_DP, getContext()
                .getResources().getDisplayMetrics());
        mDrawablePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DRAWABLE_PADDING, getContext()
                .getResources().getDisplayMetrics());

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mContent == null) {
            mContent = "";
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        //draw background
        RectF backgroundR = new RectF(0 + mOffsets[0] * mRatio, 0, width - mOffsets[1] * mRatio, height);
        canvas.drawRoundRect(backgroundR, mCornerRadiu, mCornerRadiu, mBackgroundPaint);

        //draw search icon
        int iconWidth = mCurrentSearchIcon.getMinimumWidth();
        int iconHeight = mCurrentSearchIcon.getMinimumHeight();
        int iconTop = (height - iconHeight) / 2;
        Rect searchIconR = new Rect((int) backgroundR.left + mPaddingLeft, iconTop, (int) backgroundR.left +
                mPaddingLeft + iconWidth, iconTop + iconHeight);
        mCurrentSearchIcon.setBounds(searchIconR);
        mCurrentSearchIcon.draw(canvas);

        //draw text
        int baseX = searchIconR.right + mDrawablePadding;
        int baseY = (int) ((height - (mTextPaint.descent() + mTextPaint.ascent())) / 2);
        canvas.drawText(mContent, baseX, baseY, mTextPaint);
        mTextPaint.clearShadowLayer();
    }

    public void setText(String content) {
        mContent = content;
        invalidate();
    }

    public void setBoundary(float boundary) {
        mBoundary = boundary;
    }

    public void flex(float ratio) {
        mRatio = ratio;
        if (mRatio > mBoundary) {
            mBackgroundPaint.setColor(mBackgroundColors[1]);
            mTextPaint.setColor(mTextColors[1]);
            mCurrentSearchIcon = mSearchIcons[1];
        } else {
            mBackgroundPaint.setColor(mBackgroundColors[0]);
            mTextPaint.setColor(mTextColors[0]);
            mCurrentSearchIcon = mSearchIcons[0];
        }
        invalidate();
    }
}
