package com.san.news3.pager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.san.news3.R;
import com.san.news3.adapter.SmartServicePageAdapter;
import com.san.news3.base.BasePager;
import com.san.news3.domain.SmartServicePagerBean;
import com.san.news3.utils.CacheUtils;
import com.san.news3.utils.Constants;
import com.san.news3.utils.LogUtil;
import com.san.news3.volley.VolleyManager;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by San on 2016/12/1.
 */
public class SmartServicePager extends BasePager {

    private MaterialRefreshLayout refreshLayout;
    private RecyclerView recyclerview;
    private ProgressBar pb_loading;
    public String url;
    public int pageSize=20;
    public int curPage=1;
    public int toalPage;
    public List<SmartServicePagerBean.ListBean> datas;
    private SmartServicePageAdapter adapter;

    private final static int STATE_NORMALL=1;
    private final static int STATE_REFRESH=2;
    private final static int STATE_LOADMORE=3;

    private int state=STATE_NORMALL;


    public SmartServicePager(Context context) {
        super(context);
    }


    @Override
    public void initData() {
        super.initData();

        LogUtil.e("购物商城被初始化了");
        tv_title.setText("购物商城");

//        TextView textView = new TextView(context);

//        textView.setTextColor(Color.GREEN);
//        textView.setGravity(Gravity.CENTER);
//        textView.setTextSize(25);
        View view=View.inflate(context, R.layout.smartservice_pager,null);
        refreshLayout= (MaterialRefreshLayout) view.findViewById(R.id.refreshLayout);
        recyclerview= (RecyclerView) view.findViewById(R.id.recyclerview);
        pb_loading= (ProgressBar) view.findViewById(R.id.pb_loading);

        if (fl_content!=null){
            fl_content.removeAllViews();
        }

        fl_content.addView(view);
//        textView.setText("");
        initRefresh();

        getRequestParams();

//        getDataFromNetByOkHttpUtils();
        getDataFromNetByVolley();



    }

    private void initRefresh() {
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                state=STATE_REFRESH;
                curPage=1;
                url= Constants.WARES_URL+pageSize+"&curPage="+curPage;
                getDataFromNetByVolley();

            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                if (curPage<toalPage){
                    state=STATE_LOADMORE;
                    curPage+=1;
                    url= Constants.WARES_URL+pageSize+"&curPage="+curPage;
                    getDataFromNetByVolley();
                }else {
                    Toast.makeText(context, "没有啦啦啦", Toast.LENGTH_SHORT).show();
                    refreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    public void getRequestParams() {
        state=STATE_NORMALL;
        curPage=1;
        url= Constants.WARES_URL+pageSize+"&curPage="+curPage;
    }



    public void getDataFromNetByVolley() {
        //请求队列
//        RequestQueue queue = Volley.newRequestQueue(context);
        //String请求
        StringRequest request = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {


                LogUtil.e("使用Volley联网请求成功==" + result);
                //缓存数据
                CacheUtils.putString(context, Constants.WARES_URL,result);

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

    private void processData(String json) {

        SmartServicePagerBean bean=params(json);
        datas = bean.getList();
        curPage = bean.getCurrentPage();
        toalPage = bean.getTotalPage();
        LogUtil.e("curpage=="+curPage+"toalpage=="+toalPage+"datas"+datas.get(1));

        showData();
    }

    private void showData() {
        switch (state){
            case STATE_NORMALL:
                adapter = new SmartServicePageAdapter(context, datas);
                recyclerview.setAdapter(adapter);
                recyclerview.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
                break;
            case STATE_REFRESH:
                adapter.clearData();
                adapter.addData(0,datas);
                refreshLayout.finishRefresh();
                break;
            case STATE_LOADMORE:
                adapter.addData(adapter.getCount(),datas);
                refreshLayout.finishRefreshLoadMore();
                break;
        }

        pb_loading.setVisibility(View.GONE);
    }

    private SmartServicePagerBean params(String json) {
        return new Gson().fromJson(json,SmartServicePagerBean.class);
    }


//    private void getDataFromNetByOkHttpUtils() {
//        String saveJson = CacheUtils.getString(context, Constants.WARES_URL);
//        if (!TextUtils.isEmpty(saveJson)) {
//            processData(saveJson);
//        }
//        OkHttpUtils
//                .get()
//                .url(url)
//                .id(100)
//                .build()
//                .execute(new MyStringCallback());
//    }
//
//    public class MyStringCallback extends StringCallback
//    {
////        @Override
////        public void onBefore(okhttp3.Request request, int id)
////        {
////        }
////
////        @Override
////        public void onAfter(int id)
////        {
////        }
//
//        @Override
//        public void onError(Call call, Exception e, int id)
//        {
//            e.printStackTrace();
//            LogUtil.e("使用okhttp联网请求失败==" + e.getMessage());
//        }
//
//        @Override
//        public void onResponse(String saveJson, int id)
//        {
//            LogUtil.e( "onResponse：complete");
//            LogUtil.e("使用okhttp联网请求成功==" + saveJson);
//            //缓存数据
//            CacheUtils.putString(context,  Constants.WARES_URL, saveJson);
//
//            processData(saveJson);
//            //设置适配器
//
//            switch (id)
//            {
//                case 100:
//                    Toast.makeText(context, "http", Toast.LENGTH_SHORT).show();
//                    break;
//                case 101:
//                    Toast.makeText(context, "https", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//
////        @Override
////        public void inProgress(float progress, long total, int id)
////        {
////            LogUtil.e( "inProgress:" + progress);
//////            mProgressBar.setProgress((int) (100 * progress));
////        }
//
//        @Override
//        public void onError(okhttp3.Call call, Exception e, int id) {
//
//        }
//  }
//
//    private void processData(String json) {
//    }
}
