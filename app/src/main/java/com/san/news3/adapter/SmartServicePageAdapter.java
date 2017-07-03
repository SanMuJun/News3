package com.san.news3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.san.news3.R;
import com.san.news3.domain.ShoppingCart;
import com.san.news3.domain.SmartServicePagerBean;
import com.san.news3.utils.CartProvider;

import java.util.List;

/**
 * Created by San on 2016/12/26.
 */
public class SmartServicePageAdapter extends RecyclerView.Adapter<SmartServicePageAdapter.ViewHolder> {

    private final Context context;
    private final List<SmartServicePagerBean.ListBean> datas;
    private View view;
    private SmartServicePagerBean.ListBean wares;

    private CartProvider cartProvider;

    public SmartServicePageAdapter(Context context, List<SmartServicePagerBean.ListBean> datas) {
        this.context=context;
        this.datas=datas;
        cartProvider=new CartProvider(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = View.inflate(context, R.layout.item_wares_page,null);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        wares = datas.get(position);


        Glide.with(context)
                .load(wares.getImgUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.newslogo)//真正加载的默认图片
                .error(R.mipmap.newslogo)//失败的默认图片
                .into(holder.iv_icon);

        holder.tv_text.setText(wares.getName());
        holder.tv_price.setText("&"+wares.getPrice());

        holder.btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShoppingCart cart=cartProvider.conversion(wares);
                cartProvider.addData(cart);
                Toast.makeText(context, "买买买", Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void clearData() {
        datas.clear();
        notifyItemRangeChanged(0,datas.size());
    }

    public void addData(int position, List<SmartServicePagerBean.ListBean> data) {
        if (data != null && data.size() > 0) {
            datas.addAll(position, data);
            notifyItemRangeChanged(0, datas.size());
        }
    }

    public int getCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_icon;
        private TextView tv_text;
        private TextView tv_price;
        private Button btn_buy;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_icon= (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_text= (TextView) itemView.findViewById(R.id.tv_text);
            tv_price= (TextView) itemView.findViewById(R.id.tv_price);
            btn_buy= (Button) itemView.findViewById(R.id.btn_buy);



        }
    }


}
