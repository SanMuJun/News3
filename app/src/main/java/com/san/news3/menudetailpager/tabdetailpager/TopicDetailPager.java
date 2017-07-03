package com.san.news3.menudetailpager.tabdetailpager;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.san.news3.R;
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
public class TopicDetailPager extends MenuDetailBasePager {

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


    public TopicDetailPager(Context context, NewsCenterPagerBean.DataBean.ChildrenBean childrenData) {
        super(context);
        this.childrenData=childrenData;

        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
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

        View view=View.inflate(context,R.layout.topicdetail_pager,null);
        listview= (RefreshListview) view.findViewById(R.id.listview);
        View topNewView=View.inflate(context,R.layout.topnews,null);

        viewpager= (ViewPager) topNewView.findViewById(R.id.viewpager);
        tv_title= (TextView) topNewView.findViewById(R.id.tv_title);
        ll_point_group= (LinearLayout) topNewView.findViewById(R.id.ll_point_group);

        //把顶部轮播图部分视图，以头的方式添加到ListView中
       // listview.addHeaderView(topNewView);
        listview.addTopNewsView(topNewView);

        listview.setOnRefreshListener(new MyOnRefreshListener());

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

        // //把之前缓存的数据取出
        String saveJson = CacheUtils.getString(context, url);

        if (!TextUtils.isEmpty(saveJson)){
            //解析和处理显示数据
            processData(saveJson);
        }

        getDataFromNet();
    }

    private void getDataFromNet() {

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
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(8), DensityUtil.dip2px(8));
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

        }
    }
}
