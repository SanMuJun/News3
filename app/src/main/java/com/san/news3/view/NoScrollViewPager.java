package com.san.news3.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by San on 2016/12/1.
 */
public class NoScrollViewPager extends ViewPager {

    //在代码中实例化的时候用该方法
    public NoScrollViewPager(Context context) {
        super(context);
    }

    //在布局文件中使用该类，实例化该类用该方法，这个方法不能少，否则会奔溃
    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //重写触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
