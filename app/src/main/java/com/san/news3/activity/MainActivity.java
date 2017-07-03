package com.san.news3.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.san.news3.R;
import com.san.news3.fragment.ContentFragment;
import com.san.news3.fragment.LeftmenuFragment;
import com.san.news3.utils.DensityUtil;

public class MainActivity extends SlidingFragmentActivity {

    public static final String MAIN_CONTENT_TAG = "main_content_tag";
    public static final String LEFTMENU_TAG = "leftmenu_tag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //设置主页面
        setContentView(R.layout.activity_main);

        //设置左侧菜单
        setBehindContentView(R.layout.activity_leftmenu);

        //设右侧置菜单
        SlidingMenu slidingMenu = getSlidingMenu();
      //  slidingMenu.setSecondaryMenu(R.layout.activity_rightmenu);

        //设置显示模式
        slidingMenu.setMode(SlidingMenu.LEFT);

        //设置滑动模式
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        //侧滑宽度
         slidingMenu.setBehindOffset(DensityUtil.dip2px(MainActivity.this,250));


        //初始化Fragment
        initFragment();

    }

    private void initFragment() {
        //得到Fragment
        FragmentManager fm = getSupportFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //替换
        ft.replace(R.id.fl_main_content,new  ContentFragment(), MAIN_CONTENT_TAG);
        ft.replace(R.id.fl_leftmenu,new LeftmenuFragment(), LEFTMENU_TAG);
        //提交
        ft.commit();
        //或者
       // getSupportFragmentManager().beginTransaction().replace(R.id.fl_main_content,new  ContentFragment(), MAIN_CONTENT_TAG).replace(R.id.fl_leftmenu,new LeftmenuFragment(), LEFTMENU_TAG).commit();

    }

    //得到左侧菜单的fragment
    public LeftmenuFragment getLeftmenuFrament() {

        FragmentManager fm = getSupportFragmentManager();

        return (LeftmenuFragment) fm.findFragmentByTag(LEFTMENU_TAG);
    }

    public ContentFragment getContentFragment() {
        return (ContentFragment)getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT_TAG);
    }
}
