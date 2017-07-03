package com.san.news3.pager;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.san.news3.activity.MainActivity;
import com.san.news3.base.BasePager;
import com.san.news3.base.MenuDetailBasePager;
import com.san.news3.domain.NewsCenterPagerBean;
import com.san.news3.fragment.LeftmenuFragment;
import com.san.news3.menudetailpager.InteracMenuDetailPager;
import com.san.news3.menudetailpager.NewsMenuDetailPager;
import com.san.news3.menudetailpager.PhotosMenuDetailPager;
import com.san.news3.menudetailpager.TopicMenuDetailPager;
import com.san.news3.utils.CacheUtils;
import com.san.news3.utils.Constants;
import com.san.news3.utils.LogUtil;
import com.san.news3.volley.VolleyManager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by San on 2016/12/1.
 * 新闻中心
 */
public class NewsCenterPager extends BasePager {

    //左侧菜单对应的数据集合
    private List<NewsCenterPagerBean.DataBean> data;
    //起始时间
    private long startTime;

    public NewsCenterPager(Context context) {
        super(context);
    }

    //详情页面的集合
    private ArrayList<MenuDetailBasePager>detailBasePagers;


    @Override
    public void initData() {
        super.initData();

        LogUtil.e("新闻页面被初始化了");
        ib_menu.setVisibility(View.VISIBLE);
        //设置标题
        tv_title.setText("新闻中心标题");

        //联网请求
        TextView textView = new TextView(context);

        textView.setTextColor(Color.GREEN);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(25);

        //把子视图添加到basepager的fragmentlayout中
        fl_content.addView(textView);
        //绑定数据
        textView.setText("新闻中心");

        //获取缓存数据
        String saveJson=CacheUtils.getString(context,Constants.NEWSCENTER_PAGER_URL);

        if (!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }

        //联网请求
       //getDataFormNet();
      getDataFromNetByVolley();

    }


    /**
     * 使用Volley联网请求数据
     */
    public void getDataFromNetByVolley() {
        //请求队列
//        RequestQueue queue = Volley.newRequestQueue(context);
        //String请求
        StringRequest request = new StringRequest(Request.Method.GET, Constants.NEWSCENTER_PAGER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                long endTime = SystemClock.uptimeMillis();

                long passTime = endTime - startTime;

                LogUtil.e("Volley--passTime==" + passTime);
                LogUtil.e("使用Volley联网请求成功==" + result);
                //缓存数据
                CacheUtils.putString(context,Constants.NEWSCENTER_PAGER_URL,result);

                processData(result);
                //设置适配器
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("使用Volley联网请求失败==" + volleyError.getMessage());
            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String  parsed = new String(response.data, "UTF-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };

        //添加到队列
        VolleyManager.getRequestQueue().add(request);


    }

    //使用xutils3联网请求
    public void getDataFormNet() {

        RequestParams params = new RequestParams(Constants.NEWSCENTER_PAGER_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("使用xutils3联网其请求成功=="+result);
                //缓存数据
                CacheUtils.putString(context,Constants.NEWSCENTER_PAGER_URL,result);
                processData(result);
                //设置适配器
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("使用xutils3联网其请求失败=="+ex.toString());
            }
            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("使用xutils3 onCancelled=="+cex.getMessage());
            }
            @Override
            public void onFinished() {

            }
        });
    }
    private void processData(String json) {

        NewsCenterPagerBean bean=parsedJson(json);
        String title = bean.getData().get(0).getChildren().get(1).getTitle();
        LogUtil.e("使用Gson解析json成功title"+title);

        //给左侧菜单传递数据
        data = bean.getData();

        MainActivity mainActivity= (MainActivity) context;
        LeftmenuFragment leftmenuFragment=mainActivity.getLeftmenuFrament();

        //添加详情页面
        detailBasePagers= new ArrayList<>();
        detailBasePagers.add(new NewsMenuDetailPager(context,data.get(0)));
        detailBasePagers.add(new TopicMenuDetailPager(context,data.get(0)));
        detailBasePagers.add(new PhotosMenuDetailPager(context,data.get(2)));
        detailBasePagers.add(new InteracMenuDetailPager(context,data.get(2)));

        //把数据传递给左侧菜单
        leftmenuFragment.setData(data);

    }
    //解析json数据
    //1使用系统的API
    //2第三方框架解析Gson
    private NewsCenterPagerBean parsedJson(String json) {

        Gson gson = new Gson();

        NewsCenterPagerBean bean = gson.fromJson(json, NewsCenterPagerBean.class);

        return bean;
    }

    //根据位置切换详情页面
    public void switchPager(int position) {
        //设置标题
        tv_title.setText(data.get(position).getTitle());
        //移除之前的视图
        fl_content.removeAllViews();
        //添加新内容
        MenuDetailBasePager detailBasePager = detailBasePagers.get(position);
        View rootView = detailBasePager.rootView;
        detailBasePager.initData();
        fl_content.addView(rootView);

        if (position==2){

            ib_swich_list_grid.setVisibility(View.VISIBLE);


            ib_swich_list_grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.得到图组详情页面对象
                    PhotosMenuDetailPager detailPager = (PhotosMenuDetailPager) detailBasePagers.get(2);
                    //2.调用图组对象的切换ListView和GridView的方法
                    detailPager.swichListAndGrid(ib_swich_list_grid);
                }
            });

        }else {
            ib_swich_list_grid.setVisibility(View.GONE);
        }

    }
}
