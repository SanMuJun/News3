package com.san.news3.base;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.san.news3.R;
import com.san.news3.activity.MainActivity;

/**
 * Created by San on 2016/12/1.
 */
/**
 * 作用：基类或者说公共类
 * HomePager,NewsCenterPager,
 * SmartServicePager,GovaffairPager
 * SettingPager都继承BasePager
 */
public class BasePager {


    //上下文
    public final   Context context;//mainactivity

    //代表个个不同页面
    public View rootView;

    //显示标题
    public TextView tv_title;

    //点击侧滑
    public ImageButton ib_menu;

    //加载各个页面
    public FrameLayout fl_content;
    public ImageButton ib_swich_list_grid;

    public Button btn_cart;



    public BasePager(Context context) {

        this.context=context;
        //构造方法一执行，视图就被初始化了
        rootView=initView();

    }

    //用于初始化公共部分视图，并且初始化加载子视图的fragment
    public View initView() {

        //基类的页面
        View view=View.inflate(context, R.layout.base_pager,null);

        tv_title=(TextView)view.findViewById(R.id.tv_title);
        ib_menu=(ImageButton)view.findViewById(R.id.ib_menu);
        fl_content=(FrameLayout)view.findViewById(R.id.fl_content);
        ib_swich_list_grid= (ImageButton) view.findViewById(R.id.ib_swich_list_grid);
        btn_cart= (Button) view.findViewById(R.id.btn_cart);

        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity= (MainActivity) context;
                //开变关，关变开
                mainActivity.getSlidingMenu().toggle();
            }
        });

        return view;
    }


    /**
     * 初始化数据;当孩子需要初始化数据;或者绑定数据;联网请求数据并且绑定的时候，重写该方法
     */
    public void initData() {

    }


}
