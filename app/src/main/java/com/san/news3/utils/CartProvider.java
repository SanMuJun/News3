package com.san.news3.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.san.news3.domain.ShoppingCart;
import com.san.news3.domain.SmartServicePagerBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by San on 2016/12/27.
 */
public class CartProvider {

    public static final String JSON_CART = "json_cart";
    private final Context context;
    private final SparseArray<ShoppingCart> datas;

    private String saveJson;
//    private List<ShoppingCart> carts;


    public CartProvider(Context context) {
        this.context=context;
        datas = new SparseArray<>(10);
        listToSparse();
    }

    private void listToSparse() {
        List<ShoppingCart> carts = getData();
        if (carts !=null&& carts.size()>0){
            for (int i = 1; i< carts.size(); i++){
                ShoppingCart cart= carts.get(i);
                datas.put(cart.getId(),cart);
            }
        }
    }

    public List<ShoppingCart> getData() {

        return getDataFromLocal() ;
    }

    public List<ShoppingCart> getDataFromLocal() {

        List<ShoppingCart> carts = new ArrayList<>();
        saveJson = CacheUtils.getString(context, JSON_CART);
        if (!TextUtils.isEmpty(saveJson)){

        carts = new Gson().fromJson(saveJson, new TypeToken<List<ShoppingCart>>() {
        }.getType());
        }
        return carts;
    }

    public void addData(ShoppingCart cart){

        ShoppingCart shoppingCart = datas.get(cart.getId());
        if (shoppingCart!=null){
            shoppingCart.setCount(shoppingCart.getCount()+1);
        }else {
            shoppingCart=cart;
            shoppingCart.setCount(1);
        }
        datas.put(shoppingCart.getId(),shoppingCart);

        commit();

    }

    private void commit() {
        List<ShoppingCart> carts=parsedToList();

        String json = new Gson().toJson(carts);

        CacheUtils.putString(context,JSON_CART,json);

    }

    private List<ShoppingCart> parsedToList() {
        List<ShoppingCart> carts=new ArrayList<>();

        if (datas!=null&&datas.size()>0){

            for(int i=0;i<datas.size();i++){
                ShoppingCart cart = datas.valueAt(i);
                carts.add(cart);
            }

        }


        return carts;
    }

    public void deleteData(ShoppingCart cart){

        datas.delete(cart.getId());

        commit();

    }

    public void upData(ShoppingCart cart){

        datas.put(cart.getId(),cart);

        commit();

    }

    public ShoppingCart conversion(SmartServicePagerBean.ListBean wares) {

        ShoppingCart shoppingCart=new ShoppingCart();

        shoppingCart.setDescription(wares.getDescription());
        shoppingCart.setId(wares.getId());
        shoppingCart.setImgUrl(wares.getImgUrl());
        shoppingCart.setName(wares.getName());
        shoppingCart.setPrice(wares.getPrice());
        shoppingCart.setSale(wares.getSale());

        return shoppingCart;
    }
}
