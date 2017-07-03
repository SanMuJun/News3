package com.san.news3.fragment;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.san.news3.R;
import com.san.news3.activity.MainActivity;
import com.san.news3.adapter.CotentFragmentAdapter;
import com.san.news3.base.BaseFragment;
import com.san.news3.base.BasePager;
import com.san.news3.pager.GovaffairPager;
import com.san.news3.pager.HomePager;
import com.san.news3.pager.NewsCenterPager;
import com.san.news3.pager.SettingPager;
import com.san.news3.pager.SmartServicePager;
import com.san.news3.utils.LogUtil;
import com.san.news3.view.NoScrollViewPager;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by San on 2016/11/30.
 */
public class ContentFragment extends BaseFragment {


    @ViewInject(R.id.viewpager)
    private NoScrollViewPager viewpager;//设置成不可滑动的
    @ViewInject(R.id.rg_main)
    private RadioGroup rg_main;

    //装五个页面的集合
    private ArrayList<BasePager> basePagers;


    @Override
    public View initView() {

        LogUtil.e("正文fragment视图初始化");

        View view = View.inflate(context,R.layout.content_fragment, null);
        //把视图注入到框架中
        x.view().inject(this,view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("正文被初始化了");

        //初始化五个页面，并且放入集合中
        basePagers = new ArrayList<>();
        basePagers.add(new HomePager(context));
        basePagers.add(new NewsCenterPager(context));
        basePagers.add(new SmartServicePager(context));
        basePagers.add(new GovaffairPager(context));
        basePagers.add(new SettingPager(context));

        //设置默认选中首页
//        rg_main.check(R.id.rb_home);

        //设置viewpager的适配器
        viewpager.setAdapter(new CotentFragmentAdapter(basePagers));

        //设置radiogroup的选中状态
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        //监听某个页面被选中改变的监听
        viewpager.addOnPageChangeListener(new MyOnPageChangeListener());

        //监听某个页面被选中，初始对应的/页面的数据
        rg_main.check(R.id.rb_home);
        basePagers.get(0).initData();

        //设置模式SlidingMenu不可以滑动
        isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);

    }

    //得到新闻中心
    public NewsCenterPager getNewsCententerPager() {
        return (NewsCenterPager) basePagers.get(1);
    }

//     class CotentFragmentAdapter extends PagerAdapter {
//        @Override
//        public int getCount() {
//            return basePagers.size();
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//
//            BasePager basePager = basePagers.get(position);//各个页面的实例
//            View rootView = basePager.rootView;//各个子页面
//            //调用各个页面的initData()
////            basePager.initData();
//            container.addView(rootView);
//            return rootView;
//        }
//
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view==object;
//        }
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//
//            container.removeView((View) object);
//        }
//    }

    public class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch(checkedId){

                case R.id.rb_home://主页radioButton的id
                    viewpager.setCurrentItem(0);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                 break;
                case R.id.rb_newscenter://新闻中心radioButton的id
                    viewpager.setCurrentItem(1,false);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);
                 break;
                case R.id.rb_smartservice://智慧服务radioButton的id
                    viewpager.setCurrentItem(2);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                break;
                case R.id.rb_govaffair://政要指南的RadioButton的id
                    viewpager.setCurrentItem(3,false);//false有无动画
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                break;
                case R.id.rb_setting://设置中心RadioButton的id
                    viewpager.setCurrentItem(4);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                break;
            }
        }
    }

    //根据输入的参数设置是否让slidingmenu可以滑动
    private void isEnableSlidingMenu(int touchmodeFullscreen) {

        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        //当某个页面被选中的方法
        @Override
        public void onPageSelected(int position) {

            //调用被选中的initdata方法
            basePagers.get(position).initData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
