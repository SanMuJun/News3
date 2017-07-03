package com.san.news3.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.san.news3.R;
import com.san.news3.SplashActivity;
import com.san.news3.utils.CacheUtils;
import com.san.news3.utils.DensityUtil;

import java.util.ArrayList;

public class GuideActivity extends Activity {

    private static final String TAG = GuideActivity.class.getSimpleName();
    private Button btn_start_main;
    private ViewPager viewpager;
    private LinearLayout ll_point_group;
    private ArrayList<ImageView> imageViews;
    private ImageView iv_red_point;
    private int leftMax;
    private int widthdpi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        viewpager= (ViewPager) findViewById(R.id.viewpager);
        btn_start_main= (Button) findViewById(R.id.btn_start_main);
        ll_point_group= (LinearLayout) findViewById(R.id.ll_point_group);
        iv_red_point= (ImageView) findViewById(R.id.iv_red_point);

        int[] ids=new int[]{
         R.drawable.guide_1,
         R.drawable.guide_2,
         R.drawable.guide_3
        };
        widthdpi= DensityUtil.dip2px(this,10);
        imageViews = new ArrayList<>();
        for(int i=0;i<ids.length;i++){
            ImageView imageView = new ImageView(this);
            //设置背景
            imageView.setBackgroundResource(ids[i]);
            //添加到集合中
            imageViews.add(imageView);
            //创建点
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_normal);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthdpi, widthdpi);
            if(i!=0){
                //点距离左边10dp
                params.leftMargin=widthdpi;
            }
            Log.e(TAG,widthdpi+"-----++++++++++++++++++++++++---------");
            point.setLayoutParams(params);
            //添加到线性布局里
            ll_point_group.addView(point);
        }
        //设置viewpager的适配器
        viewpager.setAdapter(new MyPagerAdapter());
        //根据view生命周期，当视图执行到layout或者onDraw的时候，视图的高和宽宾居都有了
        iv_red_point.getViewTreeObserver().addOnGlobalLayoutListener(new MyOnGlobalLayoutListener());
        //得到屏幕的百分比
        viewpager.addOnPageChangeListener(new MyOnPageChangeListener());
        //按钮的监听
        btn_start_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //保持曾经进入过主页面
                CacheUtils.putBoolean(GuideActivity.this, SplashActivity.START_MAIN,true);
                //跳转到主页面
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
                //关闭页面
                finish();
            }
        });
    }

     class MyPagerAdapter extends PagerAdapter {
        //返回数据总个数
        @Override
        public int getCount() {
            return imageViews.size();
        }
        //类似于getview
        //container viewpager
        //position  要创建页面的位置
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imageViews.get(position);
            //添加到容器中
            container.addView(imageView);
            return imageView;
        }
        //object上面instantiateItem返回的结果值
        //view当前创建的视图
        @Override
        public boolean isViewFromObject(View view, Object object) {

           return view==object;
        }
        //销毁页面
        //container viewpager
        //position  要销毁页面的位置
        //object    要销毁的页面
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


      class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {

            //执行不止一次
            iv_red_point.getViewTreeObserver().removeGlobalOnLayoutListener(MyOnGlobalLayoutListener.this);

            leftMax = ll_point_group.getChildAt(1).getLeft() - ll_point_group.getChildAt(0).getLeft();
        }
    }
     class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        //当页面回调了会回调这个方法
        //position当前滑到页面的位置
        //positionOffset页面滑到的位置
        //positionOffsetPixels页面的像素
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            int leftMargin = (int) (position * leftMax + (positionOffset * leftMax));

            RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) iv_red_point.getLayoutParams();
            params.leftMargin=leftMargin;
            iv_red_point.setLayoutParams(params);
            
          //  Log.e(TAG,"position=="+position+",positionOffset=="+positionOffset+",positionOffsetPixels"+positionOffsetPixels);

        }
        //当页面被选中会回调这个方法
        //position被选中页面对应的位置
        @Override
        public void onPageSelected(int position) {
            if(position==imageViews.size()-1){
                //最后一个页面
                btn_start_main.setVisibility(View.VISIBLE);
            }else {
                //其他页面
                btn_start_main.setVisibility(View.GONE);
            }
        }
        //页面状态发生改变时
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
