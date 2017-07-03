package com.san.news3.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.san.news3.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by San on 2016/12/9.
 */
public class RefreshListview extends ListView {

    private LinearLayout headerView;

    private View ll_pull_down_refresh;

    private ImageView iv_arrow;

    private ProgressBar pb_status;

    private static final int PULL_DOWN_FRESH=0;
    private static final int RELEASE_REFRESH=-1;
    private static final int REFRESHING=2;
    private int currentStatus=PULL_DOWN_FRESH;


    private TextView tv_status;
    private TextView tv_time;
    private int pullDownRefreshHeight;
    private float startY=-1;
    private Animation upAnimation;
    private Animation downAnimation;
    private View footerView;
    private int footerViewHeight;
    private boolean isLoadMore=false;
    private View topNewsView;
    private int listViewOnScreenY;

    public RefreshListview(Context context) {
        this(context,null);
    }

    public RefreshListview(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initHeaderView(context);
        initAnimation();
        initFootView(context);
    }

    private void initFootView(Context context) {

        footerView = View.inflate(context, R.layout.refesh_footer,null);
        footerView.measure(0,0);
        footerViewHeight = footerView.getMeasuredHeight();

        footerView.setPadding(0,-footerViewHeight,0,0);

        //添加footer
        addFooterView(footerView);

        setOnScrollListener(new MyOnScrolistener() );

    }

    private void initAnimation() {
        upAnimation = new RotateAnimation(0, -180,  RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF,0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);
        downAnimation = new RotateAnimation(-180, -360,  RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF,0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);
    }

    private void initHeaderView(Context context) {

        headerView = (LinearLayout) View.inflate(context, R.layout.refresh_listview, null);
        //下拉刷新控件
        ll_pull_down_refresh= headerView. findViewById(R.id.ll_pull_down_refresh);
        iv_arrow= (ImageView)headerView. findViewById(R.id.iv_arrow);
        pb_status= (ProgressBar)headerView. findViewById(R.id.pb_status);
        tv_status= (TextView) headerView.findViewById(R.id.tv_status);
        tv_time= (TextView) headerView.findViewById(R.id.tv_time);


        ll_pull_down_refresh.measure(0,0);

        pullDownRefreshHeight = ll_pull_down_refresh.getMeasuredHeight();
        //隐藏下拉刷新控件
        ll_pull_down_refresh.setPadding(0,pullDownRefreshHeight,0,0);

        addHeaderView(headerView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){

            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(startY==-1){
                    startY=ev.getY();
                }
                boolean isDisplayTopNews=isDisplayTopNews();
                if (!isDisplayTopNews){
                    //加载更多
                    break;
                }
                if(currentStatus==REFRESHING){
                    break;
                }
                //2.来到新的坐标
                float endY = ev.getY();
                //3.记录滑动的距离
                float distanceY = endY - startY;
                if (distanceY>0){
                    int padding = (int) (-pullDownRefreshHeight + distanceY);
                if(padding<0&&currentStatus!=PULL_DOWN_FRESH){
                    //下拉刷新状态
                    currentStatus=PULL_DOWN_FRESH;
                    //更新状态
                    refreshViewStatus();
                }else if(padding>0&&currentStatus!=RELEASE_REFRESH){
                    currentStatus=RELEASE_REFRESH;
                    refreshViewStatus();
                    }
                    ll_pull_down_refresh.setPadding(0, padding,0,0);
                }

                break;

            case MotionEvent.ACTION_UP:
                startY=-1;
                if(currentStatus==PULL_DOWN_FRESH){
                   ll_pull_down_refresh.setPadding(0,-pullDownRefreshHeight,0,0);
                }else if(currentStatus==RELEASE_REFRESH) {
                    currentStatus = REFRESHING;
                    refreshViewStatus();
                    ll_pull_down_refresh.setPadding(0,0,0,0);

                    if(mOnRefreshListenr!=null){
                        mOnRefreshListenr.onPullDownRefresh();
                    }
                }

                break;
        }


        return super.onTouchEvent(ev);
    }

    private boolean isDisplayTopNews() {

        //得到listview在屏幕上的坐标

         if (topNewsView!=null){
             int[] location=new int[2];
             if (listViewOnScreenY==-1){

                 getLocationOnScreen(location);
                 listViewOnScreenY = location[1];
             }

             //得到顶部轮播图在屏幕上的坐标
             topNewsView.getLocationOnScreen(location);
             int topNewsViewScreeny=location[1];
//        if (listViewOnScreenY<=topNewsViewScreeny){
//            return true;
//        }else {
//            return false;
//        }
        return listViewOnScreenY<=topNewsViewScreeny;
         }

        else {
             return true;
         }


    }

    private void refreshViewStatus() {
        switch (currentStatus) {
            case PULL_DOWN_FRESH:
                iv_arrow.startAnimation(downAnimation);
                tv_time.setText("下拉刷新");
                break;

            case RELEASE_REFRESH:
                iv_arrow.startAnimation(upAnimation);
                tv_time.setText("正在刷新");
                break;

            case REFRESHING:
                tv_time.setText("刷新正在");
                pb_status.setVisibility(VISIBLE);
                iv_arrow.clearAnimation();
                iv_arrow.setVisibility(GONE);


                break;

        }

    }

    public void onRefeshFinish(boolean success) {

        if (isLoadMore){
            //加载更多
            isLoadMore=false;
            //隐藏加载更多
            footerView.setPadding(0,-footerViewHeight,0,0);

        }else {

        tv_time.setText("下拉刷新成功");
        currentStatus=PULL_DOWN_FRESH;
        iv_arrow.clearAnimation();
        pb_status.setVisibility(GONE);
        iv_arrow.setVisibility(VISIBLE);
        ll_pull_down_refresh.setPadding(0,-pullDownRefreshHeight,0,0);
        if(success){
            tv_time.setText("上次下拉刷新时间"+getSystemTime());
        }

        }

    }

    public String getSystemTime() {

        SimpleDateFormat format=new SimpleDateFormat("yyy-MM-dd-HH:mm:ss");

        return format.format(new Date());
    }

    public void addTopNewsView(View topNewView) {

        if (topNewView != null) {
            this.topNewsView = topNewView;
            headerView.addView(topNewView);
        }
    }
    //下拉刷新的定义回调
    public interface OnRefreshListener{
        //下拉刷新时回调
         void onPullDownRefresh();

        void onLoadMore();
    }
    private OnRefreshListener mOnRefreshListenr;


    //由外界设置
    public void setOnRefreshListener(OnRefreshListener l){
        this.mOnRefreshListenr=l;
    }



     class MyOnScrolistener implements OnScrollListener {
         @Override
         public void onScrollStateChanged(AbsListView view, int scrollState) {

             //当静止或者惯性滚动时
             if(scrollState==OnScrollListener.SCROLL_STATE_IDLE||scrollState==OnScrollListener.SCROLL_STATE_FLING){
             //并且显示最后一条
                 if(getLastVisiblePosition()>=getCount()-2){

                     //显示加载更多布局
                     footerView.setPadding(8,8,8,8);
                     //状态改变
                     isLoadMore=true;
                     //回调接口
                     if(mOnRefreshListenr!=null){
                         mOnRefreshListenr.onLoadMore();
                     }
                 }
             }
         }
         @Override
         public void onScroll(AbsListView absListView, int i, int i1, int i2) {

         }
     }
}
