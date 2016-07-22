package com.flex.jie.flexdemo.widget;

/**
 * Created by Jie on 2016/7/14.
 */
public interface ICollapseHeader {
    boolean isCollapsing();
    boolean isTotalCollapsed();
    boolean isTotalExpand();
    int getTotalCollapseRange();
    int getCollapseHeight();
    void setCollapseHeight(int height);
}
