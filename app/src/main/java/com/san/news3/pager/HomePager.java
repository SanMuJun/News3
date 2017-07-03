package com.san.news3.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.san.news3.R;
import com.san.news3.base.BasePager;
import com.san.news3.utils.LogUtil;

/**
 * Created by San on 2016/12/1.
 */
public class HomePager extends BasePager {


    public HomePager(Context context) {
        super(context);
    }


    @Override
    public void initData() {
        super.initData();

        LogUtil.e("主页面被初始化了");
        tv_title.setText("主页面");

        View view=View.inflate(context, R.layout.home_pager,null);

        if (fl_content!=null){
            fl_content.removeAllViews();
        }


        fl_content.addView(view);
    }
}
