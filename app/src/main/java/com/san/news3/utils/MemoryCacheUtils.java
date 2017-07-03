package com.san.news3.utils;

import android.graphics.Bitmap;
import android.view.View;

import org.xutils.cache.LruCache;

/**
 * Created by San on 2016/12/14.
 */
public class MemoryCacheUtils {

    //数据集合
    public LruCache<String,Bitmap> lruCache;

    public MemoryCacheUtils(){

        int maxSize = (int) Runtime.getRuntime().maxMemory()/8;
        lruCache=new LruCache<String,Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight();
            }
        };
    }

    public Bitmap getBitmapFromUrl(String imageUrl) {
        return lruCache.get(imageUrl);
    }

    public void putBitmap(String imageUrl, Bitmap bitmap) {
              lruCache.put(imageUrl,bitmap);
    }
}
