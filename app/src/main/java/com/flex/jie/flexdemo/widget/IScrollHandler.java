package com.flex.jie.flexdemo.widget;

/**
 * Created by Jie on 2016/7/13.
 */
public interface IScrollHandler {
    boolean isTop();

    boolean isBottom();

    void scrollBy(int distance);

    void translate(int left, int top, int right, int bottom);
}
