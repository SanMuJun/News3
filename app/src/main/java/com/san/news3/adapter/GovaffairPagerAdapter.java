package com.san.news3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.san.news3.R;
import com.san.news3.domain.ShoppingCart;
import com.san.news3.utils.CartProvider;
import com.san.news3.view.NumberAddSubView;

import java.util.List;

/**
 * Created by San on 2016/12/27.
 */
public class GovaffairPagerAdapter extends RecyclerView.Adapter<GovaffairPagerAdapter.ViewHolder> {

    public final Context context;
    public final List<ShoppingCart> data;
    public final CheckBox ck_chechall;
    public final TextView tv_price_total;
    public final CartProvider cartProvider;

    public GovaffairPagerAdapter(Context context, final List<ShoppingCart> data, CheckBox ck_chechall, TextView tv_price_total) {
        this.context=context;
        this.data=data;
        this.ck_chechall=ck_chechall;
        this.tv_price_total=tv_price_total;
        cartProvider = new CartProvider(context);

        showTotalPrice();
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClickr(View view, int position) {
                ShoppingCart shoppingCart = data.get(position);
                shoppingCart.setCheck(!shoppingCart.isCheck);
                notifyItemChanged(position);
                checkAll();
                showTotalPrice();

            }
        });
    }

    public void checkAll() {
        if (data!=null&&data.size()>0){
                    int number=0;
            for (int i=0;i<data.size();i++){

                ShoppingCart shoppingCart = data.get(i);
                if (!shoppingCart.isCheck){

                    ck_chechall.setChecked(false);
                }else {
                    number++;
                }

            }
            if (number==data.size()){
                ck_chechall.setChecked(true);
            }


        }else {
            ck_chechall.setChecked(false);
        }


        ck_chechall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ischeck=ck_chechall.isChecked();
                chechAll_None(ischeck);
                showTotalPrice();
            }
        });
    }

    public void chechAll_None(boolean isCHECK) {


        if (data!=null&&data.size()>0){
            int number=0;
            for (int i=0;i<data.size();i++){

                ShoppingCart shoppingCart = data.get(i);
              shoppingCart.setCheck(isCHECK);
                notifyItemChanged(i);
            }


        }

    }

    public void showTotalPrice() {

        tv_price_total.setText("￥￥￥"+ getTotalPrice());

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(context, R.layout.waresall,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ShoppingCart shoppingCart = data.get(position);

        Glide.with(context)
                .load(shoppingCart.getImgUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.newslogo)//真正加载的默认图片
                .error(R.mipmap.newslogo)//失败的默认图片
                .into(holder.iv_icon);

        holder.tv_name.setText(shoppingCart.getName());
        holder.tv_price.setText("￥￥"+shoppingCart.getPrice());
        holder.number_addsub_view.setValues(shoppingCart.getCount());
        holder.checkbox.setChecked(shoppingCart.isCheck());




        holder.number_addsub_view.seOnNumberClickListener(new NumberAddSubView.OnNumberClickListener() {
            @Override
            public void onNumberAdd(View view, int values) {
                shoppingCart.setCount(values);
                cartProvider.upData(shoppingCart);
                showTotalPrice();
            }

            @Override
            public void onNumberSub(View view, int values) {
                shoppingCart.setCount(values);
                cartProvider.deleteData(shoppingCart);
                showTotalPrice();

            }
        });

    }

    public double getTotalPrice() {
        double totalPrice=0;
        if (data!=null&&data.size()>0) {
            for (int i = 0; i < data.size();i++) {
                ShoppingCart shoppingCart = data.get(i);
                if (shoppingCart.isCheck){
                    totalPrice +=shoppingCart.getCount()*shoppingCart.getPrice();
                }
            }
        }
        return totalPrice;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void deleteData() {
        if (data!=null&&data.size()>0) {
            for (int i = 0; i < data.size();i++) {
                ShoppingCart shoppingCart = data.get(i);
                if (shoppingCart.isCheck()){
                    cartProvider.deleteData(shoppingCart);
                    data.remove(i);
                    notifyItemRemoved(i);
//                    notifyItemChanged(i);
                    i--;
                }

            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public CheckBox checkbox;
        public ImageView iv_icon;
        public TextView tv_name;
        public TextView tv_price;
        public NumberAddSubView number_addsub_view;

           public ViewHolder(View itemView) {
               super(itemView);

               checkbox= (CheckBox) itemView.findViewById(R.id.checkbox);
               iv_icon= (ImageView) itemView.findViewById(R.id.iv_icon);
               tv_name= (TextView) itemView.findViewById(R.id.tv_name);
               tv_price= (TextView) itemView.findViewById(R.id.tv_price);
               number_addsub_view= (NumberAddSubView) itemView.findViewById(R.id.number_addsub_view);
               itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       if (l!=null){
                           l.OnItemClickr(view,getLayoutPosition());
                       }
                   }
               });
           }
       }

    public interface OnItemClickListener{

        public void  OnItemClickr(View view,int position);

    }
    public  OnItemClickListener l;

    public void setOnItemClickListener(OnItemClickListener l) {
        this.l = l;

    }

}
