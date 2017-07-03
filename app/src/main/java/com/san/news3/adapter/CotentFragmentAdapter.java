package com.san.news3.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.san.news3.base.BasePager;

import java.util.ArrayList;

/**
 * Created by San on 2016/12/4.
 */
//
public class CotentFragmentAdapter extends PagerAdapter {

    private final ArrayList<BasePager> basePagers;

    public CotentFragmentAdapter(ArrayList<BasePager> basePagers){

        this.basePagers=basePagers;

    }

    @Override
    public int getCount() {
        return basePagers.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        BasePager basePager = basePagers.get(position);//各个页面的实例
        View rootView = basePager.rootView;//各个子页面
        //调用各个页面的initData()
//            basePager.initData();
        container.addView(rootView);
        return rootView;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View) object);
    }
}