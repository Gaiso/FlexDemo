package com.flex.jie.flexdemo;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.flex.jie.flexdemo.widget.HorizontalFlexSearchView;
import com.gaiso.flex_view.CustomCollapsingToolbarLayout;
import com.gaiso.flex_view.CustomCoordinatorLayout;
import com.gaiso.flex_view.IScrollHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomCoordinatorLayout.OnOffsetChangedListener {

    HorizontalFlexSearchView searchView;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    CustomCollapsingToolbarLayout collapsingToolbarLayout;
    CustomCoordinatorLayout coordinatorLayout;
    ListAdapter adapter;

    private String mItemData = "Lorem Ipsum is simply dummy text of the printing and " +
            "when an unknown printer took a galley of type and scrambled it to make a type specimen book It has!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = (HorizontalFlexSearchView) findViewById(R.id.horizontal_search_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        collapsingToolbarLayout = (CustomCollapsingToolbarLayout) findViewById(R.id.layout_collapse);
        coordinatorLayout = (CustomCoordinatorLayout) findViewById(R.id.layout_coordinator);

        String[] listItems = mItemData.split(" ");

        List<String> list = new ArrayList<String>();
        Collections.addAll(list, listItems);

        adapter = new ListAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ListAdapter.onItemClickListener() {
            @Override
            public void onItemClick() {
                startActivity(new Intent(MainActivity.this, ViewPagerActivity.class));
            }
        });

        coordinatorLayout.addOnOffsetChangedListener(this);
        coordinatorLayout.setScrollHandler(new IScrollHandler() {
            @Override
            public boolean isTop() {
                return linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0;
            }

            @Override
            public boolean isBottom() {
                return false;
            }

            @Override
            public void scrollBy(int distance) {
                recyclerView.scrollBy(0, distance);
            }

            @Override
            public void translate(int left, int top, int right, int bottom) {
                recyclerView.measure(View.MeasureSpec.makeMeasureSpec(right, View.MeasureSpec.EXACTLY), View.MeasureSpec
                        .makeMeasureSpec(bottom - top, View.MeasureSpec.EXACTLY));
                recyclerView.layout(left, top, right, bottom);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int oldHeight = collapsingToolbarLayout.getCollapseHeight();
            int deltaHeight = getStatusBarHeight();
            collapsingToolbarLayout.setCollapseHeight(oldHeight + deltaHeight);
        }

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
        searchView.flex(percentage);
    }
}
