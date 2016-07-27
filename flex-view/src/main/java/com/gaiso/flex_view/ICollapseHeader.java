package com.gaiso.flex_view;

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
