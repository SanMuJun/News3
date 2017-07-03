package com.san.news3.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.san.news3.base.BasePager;
import com.san.news3.utils.LogUtil;

/**
 * Created by San on 2016/12/1.
 */
public class SettingPager extends BasePager {


    public SettingPager(Context context) {
        super(context);
    }


    @Override
    public void initData() {
        super.initData();

        LogUtil.e("设置页面被初始化了");
        tv_title.setText("设置标题");

        TextView textView = new TextView(context);

        textView.setTextColor(Color.GREEN);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(25);

        fl_content.addView(textView);
        textView.setText("设置");

    }
}
