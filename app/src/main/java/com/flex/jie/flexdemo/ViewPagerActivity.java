package com.flex.jie.flexdemo;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flex.jie.flexdemo.widget.CustomCollapsingToolbarLayout;
import com.flex.jie.flexdemo.widget.CustomCoordinatorLayout;
import com.flex.jie.flexdemo.widget.IScrollHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jie on 2016/7/21.
 */
public class ViewPagerActivity extends AppCompatActivity implements CustomCoordinatorLayout.OnOffsetChangedListener,
        ViewPager.OnPageChangeListener {

    CustomCoordinatorLayout coordinatorLayout;
    CustomCollapsingToolbarLayout collapsingToolbarLayout;
    ViewPager viewPager;
    TabLayout tabLayout;
    RelativeLayout toolbar;
    TextView title;
    ViewPagerAdapter pagerAdapter;
    TabFragment mCurrentFragment;
    private float mTempPercent;
    private boolean mIsNeed2Top;
    private int mCurrentPosition;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        coordinatorLayout = (CustomCoordinatorLayout) findViewById(R.id.layout_coordinator);
        collapsingToolbarLayout = (CustomCollapsingToolbarLayout) findViewById(R.id.layout_collapse);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        title = (TextView) findViewById(R.id.tv_title);
        title.setText("ViewPagerActivity");
        handleTextAlpha(title, 0);
        toolbar = (RelativeLayout) findViewById(R.id.rl_toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int oldHeight = collapsingToolbarLayout.getCollapseHeight();
            int deltaHeight = getStatusBarHeight();
            collapsingToolbarLayout.setCollapseHeight(oldHeight + deltaHeight);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            params.topMargin = deltaHeight;
            toolbar.setLayoutParams(params);
        }

        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        mCurrentFragment = (TabFragment) pagerAdapter.getItem(0);

        coordinatorLayout.addOnOffsetChangedListener(this);
        coordinatorLayout.setHorizontalMoveChildView(CustomCoordinatorLayout.CONTENT_VIEW_CAN_MOVE_HORIZONTALLY);
        coordinatorLayout.setScrollHandler(new IScrollHandler() {
            @Override
            public boolean isTop() {
                return mCurrentFragment.isTop();
            }

            @Override
            public boolean isBottom() {
                return false;
            }

            @Override
            public void scrollBy(int distance) {
                mCurrentFragment.scrollBy(distance);
            }

            @Override
            public void translate(int left, int top, int right, int bottom) {
                viewPager.measure(View.MeasureSpec.makeMeasureSpec(right, View.MeasureSpec.EXACTLY), View
                        .MeasureSpec.makeMeasureSpec(bottom - top, View.MeasureSpec.EXACTLY));
                viewPager.layout(left, top, right, bottom);
            }
        });
    }

    private void setupViewPager() {
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), initFragments());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);
    }

    private List<Fragment> initFragments() {
        List<Fragment> list = new ArrayList<>();
        list.add(new TabFragment());
        list.add(new TabFragment());
        list.add(new TabFragment());
        return list;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelOffset(resourceId);
        }
        return result != 0 ? result : dp2px(25);
    }

    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    @Override
    public void onOffsetChanged(CustomCoordinatorLayout coordinatorLayout, int verticalOffset, float percentage) {
        handleTextAlpha(title, percentage);
        //offset发生变化时，viewpager每个item的recyclerview回到顶部
        if (percentage != mTempPercent) {
            mIsNeed2Top = true;
            mTempPercent = percentage;
        }
    }

    private void handleTextAlpha(TextView textView, float alpha) {
        textView.setAlpha(alpha);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mIsNeed2Top)
            scrollOtherChildView2Top();
    }

    private void scrollOtherChildView2Top() {
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            if (i == mCurrentPosition)
                continue;
            TabFragment fragment = (TabFragment) pagerAdapter.getItem(i);
            if (!fragment.isTop())
                fragment.scroll2Top();
        }
        mIsNeed2Top = false;
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentFragment = (TabFragment) pagerAdapter.getItem(position);
        mCurrentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void onBackClick(View view) {
        finish();
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private String[] title = {"TAB1", "TAB2", "TAB3"};
        private List<Fragment> fragments = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public ViewPagerAdapter(FragmentManager manager, List<Fragment> fragments) {
            this(manager);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return title.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }

    }
}
