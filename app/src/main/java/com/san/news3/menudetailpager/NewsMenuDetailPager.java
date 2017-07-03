package com.san.news3.menudetailpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.san.news3.R;
import com.san.news3.activity.MainActivity;
import com.san.news3.base.MenuDetailBasePager;
import com.san.news3.domain.NewsCenterPagerBean;
import com.san.news3.menudetailpager.tabdetailpager.TabDetailPager;
import com.san.news3.utils.LogUtil;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by San on 2016/12/3.
 */
public class NewsMenuDetailPager extends MenuDetailBasePager{

    @ViewInject(R.id.ib_tab_next)
    private ImageButton ib_tab_next;
    @ViewInject(R.id.tabPageIndicator)
    private TabPageIndicator tabPageIndicator;

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;
    private final List<NewsCenterPagerBean.DataBean.ChildrenBean> children;
    private ArrayList<TabDetailPager> tabDetaiPagers;


    public NewsMenuDetailPager(Context context, NewsCenterPagerBean.DataBean detaiPagerData) {
        super(context);
        children = detaiPagerData.getChildren();
    }

    @Override
    public View initView() {

        View view=View.inflate(context,R.layout.newsmenu_detail_pager,null);
        x.view().inject(this,view);
        //设置点击事件
        ib_tab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
        });

        return view;
    }

    @Override
    public void initData() {
        super.initData();

        LogUtil.e("新闻详情页面内容被初始化了");

        //准备新闻详情页面的数据
        tabDetaiPagers = new ArrayList<>();
            for(int i=0;i<children.size();i++){

            //添加新闻详情页面
            tabDetaiPagers.add(new TabDetailPager(context,children.get(i)));
        }

        //设置ViewPager的适配器
        viewPager.setAdapter(new MyNewsMenuDetailPagerAdapter());

        //ViewPager和TabPageIndicator关联
        tabPageIndicator.setViewPager(viewPager);

        //注意以后监听页面的变化 ，TabPageIndicator监听页面的变化
        tabPageIndicator.setOnPageChangeListener(new MyOnPageChangeListener());



    }
    //根据输入的参数设置是否让slidingmenu可以滑动
    private void isEnableSlidingMenu(int touchmodeFullscreen) {

        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }

    class MyNewsMenuDetailPagerAdapter extends PagerAdapter {
         @Override
         public CharSequence getPageTitle(int position) {
             return children.get(position).getTitle();
         }

         @Override
         public int getCount() {
             return tabDetaiPagers.size();
         }

         @Override
         public Object instantiateItem(ViewGroup container, int position) {

             TabDetailPager tabDetailPager = tabDetaiPagers.get(position);
             View rootView = tabDetailPager.rootView;
             //初始化数据
             tabDetailPager.initData();
             container.addView(rootView);

             return rootView;
         }

         @Override
         public void destroyItem(ViewGroup container, int position, Object object) {
             container.removeView((View) object);
         }

         @Override
         public boolean isViewFromObject(View view, Object object) {
             return view==object;
         }
     }

     class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

         }

         @Override
         public void onPageSelected(int position) {

             if(position==0){

                 //SlidingMenu可以全屏滑动
                 isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);

             }else {

                 //SlidingMenu不可以滑动
                 isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
             }
         }
         @Override
         public void onPageScrollStateChanged(int state) {

         }
     }
}
