package com.san.news3.fragment;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.san.news3.R;
import com.san.news3.activity.MainActivity;
import com.san.news3.base.BaseFragment;
import com.san.news3.domain.NewsCenterPagerBean;
import com.san.news3.pager.NewsCenterPager;
import com.san.news3.utils.DensityUtil;
import com.san.news3.utils.LogUtil;

import java.util.List;

/**
 * Created by San on 2016/11/30.
 */
public class LeftmenuFragment extends BaseFragment {

//    private TextView textView;
    private List<NewsCenterPagerBean.DataBean> data;
    private LeftmenuFragmentAdapter adapter;
    private ListView listView;

    //点击的位置
    private int prePosition;

    @Override
    public View initView() {

        LogUtil.e("左侧fragment视图初始化");

        listView = new ListView(context);
        listView.setPadding(0, DensityUtil.dip2px(context,40),0,0);
        //设置分割线高度为0
        listView.setDividerHeight(0);
        listView.setCacheColorHint(Color.TRANSPARENT);

        //设置按下listview的item 不变色
        listView.setSelector(android.R.color.transparent);

        //设置item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //记录点击的位置，变红
                prePosition=position;
                adapter.notifyDataSetChanged();//getCount

                //把左侧菜单关闭//开变关，关变开
                MainActivity mainActivity= (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();

                //切换到对应的详情页面：新闻详情页面，专题详情页面，图组详情页面，互动详情页面
                swichPager(prePosition);

            }
        });


        return listView;
    }

    //根据位置切换不同详情页面
    private void swichPager(int position) {

        MainActivity mainActivity= (MainActivity) context;
        ContentFragment contentFragment= mainActivity.getContentFragment();
        NewsCenterPager newsCenterPager=contentFragment.getNewsCententerPager();
        newsCenterPager.switchPager(position);
    }

    @Override
    public void initData() {
        super.initData();

        LogUtil.e("左侧被初始化了");



    }

    //接收新闻中心数据
    public void setData(List<NewsCenterPagerBean.DataBean> data) {
        this.data = data;
        for(int i=0;i<data.size();i++){

            LogUtil.e("title=="+data.get(i).getTitle());
        }

        //设置适配器
        adapter = new LeftmenuFragmentAdapter();
        listView.setAdapter(adapter);

        //设置默认的页面
        swichPager(prePosition);
    }

  class LeftmenuFragmentAdapter extends BaseAdapter {
      @Override
      public int getCount() {
          return data.size();
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
      public View getView(int posotion, View view, ViewGroup viewGroup) {

          TextView textView = (TextView) View.inflate(context, R.layout.item_leftmenu, null);
          textView.setText(data.get(posotion).getTitle());

          textView.setEnabled(posotion==prePosition);
          return textView;
      }
  }
}
