package com.san.news3.domain;

import java.io.Serializable;

/**
 * Created by San on 2016/12/27.
 */
public class ShoppingCart extends SmartServicePagerBean.ListBean implements Serializable{

    public int count;
    public Boolean isCheck=true;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Boolean isCheck() {
        return isCheck;
    }

    public void setCheck(Boolean check) {
        isCheck = check;
    }

    @Override
    public String toString() {
        return "ShoppingCart{" +
                "count=" + count +
                ", isCheck=" + isCheck +
                '}';
    }


}
