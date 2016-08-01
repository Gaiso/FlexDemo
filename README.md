# FlexDemo
由CustomCoordinatorLayout控制两个子view（CustomCollaspingToolbarLayout + other）的touchEvent，由CustomCollaspingToolbarLayout控制它的子view随顶部offset变化而变化。
# Demo
![](https://github.com/Gaiso/FlexDemo/blob/master/preview/recyclerview.gif?raw=true)
==
![](https://github.com/Gaiso/FlexDemo/blob/master/preview/viewpager.gif?raw=true)
#引用
    compile 'com.gaiso.flex_view:flex-view:1.0.0'
#用法
![](https://github.com/Gaiso/FlexDemo/blob/master/preview/layout.png?raw=true)
###CustomCoordinatorLayout
```java
    coordinatorLayout.addOnOffsetChangedListener(this);//顶部offset变化回调 CustomCoordinatorLayout.OnOffsetChangedListener
    //coordinatorLayout.setHorizontalMoveChildView(CustomCoordinatorLayout.CONTENT_VIEW_CAN_MOVE_HORIZONTALLY); 当子view存在viewpager
    //需要横向滑动时设置。HEADER_VIEW_CAN_MOVE_HORIZONTALLY，CONTENT_VIEW_CAN_MOVE_HORIZONTALLY，默认NONE_CAN_MOVE_HORIZONTALLY
    coordinatorLayout.setScrollHandler(new IScrollHandler() {//获取第二个子view的状态及对它的操作
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
```
###CustomCollapsingToolbarLayout
```xml
    <com.gaiso.flex_view.CustomCollapsingToolbarLayout
        android:id="@+id/layout_collapse"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        app:collapseHeight="49dp"
        app:layoutScrim="@color/colorPrimary">

        <ImageView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:scaleType="centerCrop"
            android:src="@mipmap/car"/>

        <com.flex.jie.flexdemo.widget.HorizontalFlexSearchView
            android:id="@+id/horizontal_search_view"
            android:layout_width="match_parent"
            android:layout_height="35dp"
            android:layout_alignParentBottom="true"
            android:layout_marginBottom="15dp"
            android:layout_marginLeft="15dp"
            android:layout_marginRight="15dp"
            app:background_color_after="@color/bg_orange"
            app:background_color_before="@color/white"
            app:collapseMode="parallax"
            app:corner_radiu="3dp"
            app:drawScrim="always"
            app:parallax_range="10dp"
            app:right_offset="30dp"
            app:search_icon_after="@mipmap/icon_search"
            app:search_icon_before="@mipmap/icon_gray_search"
            app:search_text="大家都在搜：汽车"
            app:text_color_after="@color/text_orange"
            app:text_color_before="@color/text_gray"
            app:text_size="15sp"/>

    </com.gaiso.flex_view.CustomCollapsingToolbarLayout>
```
    app:collapseHeight 设置收缩状态的高度
    app:layoutScrim 设置颜色
#####对子view的设置
    app:collapseMode none(默认）,pin(offset变化时该子view位置固定),parallax(与parallax_range一起设置)
    app:drawScrim="always" 设置scrim覆盖哪些子view（按照子view draw的顺序，一般看xml的顺序，
    例如子view3设置了always,scrim将覆盖子view1和2）
    app:parallax_range="10dp" 随着收缩百分比变化的范围

    
