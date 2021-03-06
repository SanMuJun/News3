package com.san.news3.menudetailpager;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.san.news3.R;
import com.san.news3.activity.PicassoSampleActivity;
import com.san.news3.base.MenuDetailBasePager;
import com.san.news3.domain.NewsCenterPagerBean;
import com.san.news3.domain.PhotoMenuDetailPagerBean;
import com.san.news3.utils.CacheUtils;
import com.san.news3.utils.Constants;
import com.san.news3.utils.LogUtil;
import com.san.news3.volley.VolleyManager;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by San on 2016/12/3.
 */
public class PhotosMenuDetailPager extends MenuDetailBasePager{




    private final NewsCenterPagerBean.DataBean detaipagerdata;
    private String url;
    private long startTime;
    private List<PhotoMenuDetailPagerBean.DataBean.NewsBean> news;
    private PhotosMenuDetailPagerAdapter adapter;
    private PhotoMenuDetailPagerBean.DataBean.NewsBean newsEmpty;

    public PhotosMenuDetailPager(Context context, NewsCenterPagerBean.DataBean detaipagerdata) {
        super(context);
        this.detaipagerdata=detaipagerdata;
    }
    @ViewInject(R.id.gridview)
    private GridView gridview;
    @ViewInject(R.id.listview)
    private ListView listview;

    @Override
    public View initView() {

        View view = View.inflate(context, R.layout.photos_menudetail_pager, null);
        x.view().inject(this, view);

        listview.setOnItemClickListener(new MyOnItemClickListener());
        gridview.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    @Override
    public void initData() {
        super.initData();

        LogUtil.e("图组详情页面内容被初始化了");
        url = Constants.BASE_URL + detaipagerdata.getUrl();
        LogUtil.e("______________________图组URL+++++++++++++++"+url);
        String saveJson = CacheUtils.getString(context, url);

        if(!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }

        getDataFromNetByVolley();
    }


    /**
     * 使用Volley联网请求数据
     */
    public void getDataFromNetByVolley() {
        //请求队列
//        RequestQueue queue = Volley.newRequestQueue(context);
        //String请求
        StringRequest request = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                long endTime = SystemClock.uptimeMillis();

                long passTime = endTime - startTime;

                LogUtil.e("Volley--passTime==" + passTime);
                LogUtil.e("使用Volley联网请求成功==" + result);
                //缓存数据
                CacheUtils.putString(context,url,result);

               // processData(result);
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

    private void processData(String saveJson) {
        PhotoMenuDetailPagerBean bean=parsedJson(saveJson);

        LogUtil.e("T图组解析成功11111111"+bean.getData().getNews().get(0).getTitle());

        news = bean.getData().getNews();

        adapter = new PhotosMenuDetailPagerAdapter();

        listview.setAdapter(adapter);
    }

    private PhotoMenuDetailPagerBean parsedJson(String saveJson) {
        return new Gson().fromJson(saveJson,PhotoMenuDetailPagerBean.class);
    }


     class PhotosMenuDetailPagerAdapter extends BaseAdapter {
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
                 convertView=View.inflate(context,R.layout.item_photo_menudetai_pager,null);
                 viewHolder.iv_icon= (ImageView) convertView.findViewById(R.id.iv_icon);
                 viewHolder.tv_title= (TextView) convertView.findViewById(R.id.tv_title);
                 convertView.setTag(viewHolder);
             }else{
                viewHolder= (ViewHolder) convertView.getTag();
             }
             newsEmpty = news.get(position);
             viewHolder.tv_title.setText(newsEmpty.getTitle());
             String imageUrl = Constants.BASE_URL + newsEmpty.getSmallimage();
             loaderImager(viewHolder,imageUrl);


             return convertView;
         }
     }

     class ViewHolder {
         private ImageView iv_icon;
         private TextView tv_title;
    }


    /**
     *
     * @param viewHolder
     * @param imageurl
     */
    private void loaderImager(final ViewHolder viewHolder, String imageurl) {

        //设置tag
        viewHolder.iv_icon.setTag(imageurl);
        //直接在这里请求会乱位置
        ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer != null) {

                    if (viewHolder.iv_icon != null) {
                        if (imageContainer.getBitmap() != null) {
                            //设置图片
                            viewHolder.iv_icon.setImageBitmap(imageContainer.getBitmap());
                        } else {
                            //设置默认图片
                            viewHolder.iv_icon.setImageResource(R.mipmap.newslogo);
                        }
                    }
                }
            }
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //如果出错，则说明都不显示（简单处理），最好准备一张出错图片
                viewHolder.iv_icon.setImageResource(R.mipmap.newslogo);
            }
        };
        VolleyManager.getImageLoader().get(imageurl, listener);
    }

 class MyOnItemClickListener implements AdapterView.OnItemClickListener {

     private PhotoMenuDetailPagerBean.DataBean.NewsBean newsEntity;

     @Override
     public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
         newsEntity = news.get(position);
         String imageUrl = Constants.BASE_URL+ newsEntity.getLargeimage();
         Intent intent = new Intent(context, PicassoSampleActivity.class);
         intent.putExtra("url",imageUrl);
         context.startActivity(intent);
     }
 }

    private boolean isShowListView = true;

    public void swichListAndGrid(ImageButton ib_swich_list_grid) {
        if(isShowListView){
            isShowListView = false;
            //显示GridView,隐藏ListView
            gridview.setVisibility(View.VISIBLE);
            adapter = new PhotosMenuDetailPagerAdapter();
            gridview.setAdapter(adapter);
            listview.setVisibility(View.GONE);
            //按钮显示--ListView
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_grid_type);


        }else{
            isShowListView = true;
            //显示ListView，隐藏GridView
            listview.setVisibility(View.VISIBLE);
            adapter = new PhotosMenuDetailPagerAdapter();
            listview.setAdapter(adapter);
            gridview.setVisibility(View.GONE);
            //按钮显示--GridView
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_grid_type);
        }
    }
}
