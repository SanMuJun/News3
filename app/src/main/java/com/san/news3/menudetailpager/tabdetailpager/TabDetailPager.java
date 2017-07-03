package com.san.news3.menudetailpager.tabdetailpager;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.san.news3.R;
import com.san.news3.activity.NewsDetailActivity;
import com.san.news3.base.MenuDetailBasePager;
import com.san.news3.domain.NewsCenterPagerBean;
import com.san.news3.domain.TabDetailPagerBean;
import com.san.news3.utils.CacheUtils;
import com.san.news3.utils.Constants;
import com.san.news3.utils.LogUtil;
import com.san.news3.view.RefreshListview;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;


/**
 * Created by San on 2016/12/7.
 */
public class TabDetailPager  extends MenuDetailBasePager {

    public static final String READ_ARRAY_ID = " read_array_id";
    private int prePosition;

    private  ImageOptions imageOptions;

    private ViewPager viewpager;
    private TextView tv_title;
    private LinearLayout ll_point_group;
    private RefreshListview listview;


    private final NewsCenterPagerBean.DataBean.ChildrenBean childrenData;
    private String url;
    private List<TabDetailPagerBean.DataBean.TopnewsBean> topnews;
    public List<TabDetailPagerBean.DataBean.NewsBean> news;
    private TabDetailPagerListener adapter;
    public String moreUrl;
    private boolean isLoadMore=false;
    private InternalHandler internalHandler;
    private boolean isDragging=false;


    public TabDetailPager(Context context, NewsCenterPagerBean.DataBean.ChildrenBean childrenData) {
        super(context);
        this.childrenData=childrenData;

        imageOptions = new ImageOptions.Builder()
                .setSize(org.xutils.common.util.DensityUtil.dip2px(120), org.xutils.common.util.DensityUtil.dip2px(120))
                .setRadius(org.xutils.common.util.DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.newslogo)
                .setFailureDrawableId(R.mipmap.newslogo)
                .build();

    }

    @Override
    public View initView() {

        View topNewView=View.inflate(context,R.layout.topnews,null);
        View view=View.inflate(context,R.layout.tabdetail_pager,null);

        viewpager= (ViewPager) topNewView.findViewById(R.id.viewpager);
        tv_title= (TextView) topNewView.findViewById(R.id.tv_title);
        ll_point_group= (LinearLayout) topNewView.findViewById(R.id.ll_point_group);
        listview= (RefreshListview) view.findViewById(R.id.listview);

        //把顶部轮播图部分视图，以头的方式添加到ListView中
       // listview.addHeaderView(topNewView);
        listview.addTopNewsView(topNewView);
        //设置监听下拉刷新
        listview.setOnRefreshListener(new MyOnRefreshListener());
        //设置ListView的item的点击监听
        listview.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    class  MyOnRefreshListener implements RefreshListview.OnRefreshListener{
        @Override
        public void onPullDownRefresh() {
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {

            if (TextUtils.isEmpty(moreUrl)){
                Toast.makeText(context, "没同意更多数据", Toast.LENGTH_SHORT).show();
            }

            getMoreDataFromNet();
        }
    }

    private void getMoreDataFromNet() {

        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LogUtil.e("加载更多联网成功"+result);
                isLoadMore=true;
                //解析数据
                processData(result);

                listview.onRefeshFinish(true);


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {


                LogUtil.e("加载更多联网成功"+ex.getMessage());

            }

            @Override
            public void onCancelled(CancelledException cex) {

                LogUtil.e("加载更多联网成功"+cex.getMessage());
            }

            @Override
            public void onFinished() {

                LogUtil.e("加载更多联网成功onFinished");
            }
        });


    }

    @Override
    public void initData() {
        super.initData();

        url = Constants.BASE_URL + childrenData.getUrl();
        LogUtil.e(childrenData.getTitle()+"的联网地址"+url);
        Log.e("Tag","UUUUUUUUUUUUUUUUUUUUUUUUU"+url.toString());

        // //把之前缓存的数据取出
        String saveJson = CacheUtils.getString(context, url);

        if (!TextUtils.isEmpty(saveJson)){
            //解析和处理显示数据
            processData(saveJson);
        }

        getDataFromNet();
    }

    private void getDataFromNet() {
        prePosition=0;

        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LogUtil.e(childrenData.getTitle()+"页面数据请求成功"+result);

                //存储数据
                CacheUtils.putString(context,url,result);

                //解析和处理显示数据
                processData(result);

                listview.onRefeshFinish(true);

            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                LogUtil.e(childrenData.getTitle()+"页面数据请求失败"+ex.getMessage());
                listview.onRefeshFinish(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {

                LogUtil.e(childrenData.getTitle()+"页面数据请求失败"+cex.getMessage());
            }
            @Override
            public void onFinished() {

                LogUtil.e(childrenData.getTitle()+"onFinished");
            }
        });
    }
    private void processData(String json) {

        prePosition=0;

        TabDetailPagerBean bean= ParsedJson(json);
        LogUtil.e(childrenData.getTitle()+"解析成功----------------"+bean.getData().getNews().get(0).getTitle());

        moreUrl="";
        if (TextUtils.isEmpty(bean.getData().getMore())) {
            moreUrl = "";
        }else {

            moreUrl=Constants.BASE_URL+bean.getData().getMore();
        }
        //默认加载更多
        if (!isLoadMore){
            //默认

            topnews=bean.getData().getTopnews();

            viewpager.setAdapter(new TabDetailPagerTopNewAsapter());

            ll_point_group.removeAllViews();
            for (int i=0;i<topnews.size();i++){

                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.point_select);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(org.xutils.common.util.DensityUtil.dip2px(8), org.xutils.common.util.DensityUtil.dip2px(8));
                if (i==0){
                    imageView.setEnabled(true);
                }else {
                    imageView.setEnabled(false);
                    params.leftMargin= DensityUtil.dip2px(8);
                }

                imageView.setLayoutParams(params);
                ll_point_group.addView(imageView);

            }

            viewpager.addOnPageChangeListener(new MyOnPageChangeListener());

            tv_title.setText(topnews.get(prePosition).getTitle());

            news = bean.getData().getNews();

            //设置适配器
            adapter = new TabDetailPagerListener();
            listview.setAdapter(adapter);

        }else{
            //加载在更多
            isLoadMore=true;
           List<TabDetailPagerBean.DataBean.NewsBean> morenews= bean.getData().getNews();
            //加载到原来的集合
            news.addAll(morenews);
        }


        //发消息每隔4000切换一次ViewPager页面

        if(internalHandler == null){
            internalHandler = new InternalHandler();
        }
        //是把消息队列所有的消息和回调移除
        internalHandler.removeCallbacksAndMessages(null);
        internalHandler.postDelayed(new MyRunnable(),4000);
    }
    class InternalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //切换ViewPager的下一个页面
            int item = (viewpager.getCurrentItem()+1)%topnews.size();
            viewpager.setCurrentItem(item);
            internalHandler.postDelayed(new MyRunnable(), 4000);
        }
    }
    class MyRunnable implements  Runnable{

        @Override
        public void run() {
            internalHandler.sendEmptyMessage(0);
        }
    }


    class  TabDetailPagerListener extends BaseAdapter {

        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView==null){
                viewHolder=new ViewHolder();
                convertView=View.inflate(context,R.layout.item_tabdetail_pager,null);

                viewHolder.iv_icon= (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title= (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tv_time= (TextView) convertView.findViewById(R.id.tv_time);

                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //根据位置得到数据
            TabDetailPagerBean.DataBean.NewsBean newsData = news.get(position);
            String imageUrl = Constants.BASE_URL + newsData.getListimage();
            LogUtil.e("==============================++++++++++++++++++++++++++++++++++++++++++++++++=====____________________"+imageUrl);
            //请求图片XUtils3
            x.image().bind(viewHolder.iv_icon,imageUrl,imageOptions);

            //设置标题
            viewHolder.tv_title.setText(newsData.getTitle());

            //设置更新时间
            viewHolder.tv_time.setText(newsData.getPubdate());

            String idArray = CacheUtils.getString(context,READ_ARRAY_ID);
            if(idArray.contains(newsData.getId()+"")){
                //设置灰色
                viewHolder.tv_title.setTextColor(Color.GRAY);
            }else{
                //设置黑色
                viewHolder.tv_title.setTextColor(Color.BLUE);
            }

            return convertView;
        }

        private class ViewHolder {
            ImageView iv_icon;
            TextView tv_title;
            TextView tv_time;

        }
    }

    private TabDetailPagerBean ParsedJson(String json) {

        return new Gson().fromJson(json,TabDetailPagerBean.class);
    }


    private class TabDetailPagerTopNewAsapter extends PagerAdapter {

        private TabDetailPagerBean.DataBean.TopnewsBean topnewsData;

        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = new ImageView(context);
            //设置图片默认北京
            imageView.setImageResource(R.drawable.news_pic_default);
            //x轴和Y轴拉伸
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //把图片添加到容器(ViewPager)中
            container.addView(imageView);
            //图片请求地址
            topnewsData = topnews.get(position);
            String imageUrl = Constants.BASE_URL + topnewsData.getTopimage();
            //联网请求图片
            x.image().bind(imageView,imageUrl);

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN://按下
                            LogUtil.e("按下");
                            //是把消息队列所有的消息和回调移除
                            internalHandler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP://离开
                            LogUtil.e("离开");
                            //是把消息队列所有的消息和回调移除
                            internalHandler.removeCallbacksAndMessages(null);
                            internalHandler.postDelayed(new MyRunnable(), 4000);
                            break;
//                        case MotionEvent.ACTION_CANCEL://取消
//                            LogUtil.e("取消");
//                            //是把消息队列所有的消息和回调移除
//                            internalHandler.removeCallbacksAndMessages(null);
//                            internalHandler.postDelayed(new MyRunnable(), 4000);
//                            break;
                    }
                    return true;
                }
            });


            return imageView;
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


    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            //1.设置文本
            tv_title.setText(topnews.get(position).getTitle());

            //2.对应页面的点高亮-红色
            //把之前的变成灰色
            ll_point_group.getChildAt(prePosition).setEnabled(false);

            //把当前设置红色
            ll_point_group.getChildAt(position).setEnabled(true);

            prePosition=position;
        }
        @Override
        public void onPageScrollStateChanged(int state) {


            if(state ==ViewPager.SCROLL_STATE_DRAGGING){//拖拽
                isDragging = true;
                LogUtil.e("拖拽");
                //拖拽要移除消息
                internalHandler.removeCallbacksAndMessages(null);
            }else if(state ==ViewPager.SCROLL_STATE_SETTLING&&isDragging){//惯性
                //发消息
                LogUtil.e("惯性");
                isDragging = false;
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(),4000);

            }else if(state ==ViewPager.SCROLL_STATE_IDLE&&isDragging){//静止状态
                //发消息
                LogUtil.e("静止状态");
                isDragging = false;
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(),4000);
            }

        }


    }

  class MyOnItemClickListener implements AdapterView.OnItemClickListener {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int pisiton, long id) {

          int realPosition=pisiton-1;
          TabDetailPagerBean.DataBean.NewsBean newsData = news.get(realPosition);

          String idArray = CacheUtils.getString(context, READ_ARRAY_ID);//"3511,"
          //2，判断是否存在，如果不存在，才保存，并且刷新适配器
          if(!idArray.contains(newsData.getId()+"")){//3512

              CacheUtils.putString(context, READ_ARRAY_ID,idArray+newsData.getId()+",");//"3511,3512,"

              //刷新适配器
              adapter.notifyDataSetChanged();//getCount-->getView

          }

          //跳转到新闻浏览页面
          Intent intent = new Intent(context,NewsDetailActivity.class);
          intent.putExtra("url",Constants.BASE_URL+newsData.getUrl());
          context.startActivity(intent);


      }
  }
}
