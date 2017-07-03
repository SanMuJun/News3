package com.san.news3.utils;

import android.graphics.Bitmap;
import android.os.Handler;

/**
 * Created by San on 2016/12/13.
 */
public class BitmapUtils {




    public BitmapUtils(Handler handler) {
        //内存
        memoryCacheUils=new MemoryCacheUtils();
        //本地
        loaclCacheUtils = new LoaclCacheUtils(memoryCacheUils);
        //网络
        netCacheUtils=new NetCacheUtils(handler,loaclCacheUtils,memoryCacheUils);

    }

    //网络缓存工具类
    private NetCacheUtils netCacheUtils;
    //本地缓存工具类
    private LoaclCacheUtils loaclCacheUtils;
    //内存缓存工具类
    private MemoryCacheUtils memoryCacheUils;

    public Bitmap getBitmap(String imageUrl, int position) {

        //一级缓存,内存缓存
        if (memoryCacheUils!=null){
            Bitmap bitmap=memoryCacheUils.getBitmapFromUrl(imageUrl);
            if (bitmap!=null){
                LogUtil.e("从内存获取图片成功"+position);
                LogUtil.e("从内存获取图片成功"+bitmap);
                return bitmap;
            }
        }

        //二级缓存,本地缓存
        if (loaclCacheUtils!=null){
            Bitmap bitmap=loaclCacheUtils.getBitmapFromUrl(imageUrl);
            if (bitmap!=null){
                LogUtil.e("从本地获取图片成功"+position);
                LogUtil.e("从本地获取图片成功"+bitmap);

                return bitmap;
            }
        }

        //三级缓存,网络缓存
        netCacheUtils.getBitmapFromNet(imageUrl,position);
        LogUtil.e("从网络获取图片成功"+position);

        return null;
    }
}
