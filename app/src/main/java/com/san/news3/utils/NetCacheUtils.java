package com.san.news3.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by San on 2016/12/14.
 */
//网络缓存工具类
public class NetCacheUtils {


    //请求图片成功
    public static final int SUCESS=1;
    //请求图片失败
    public static final int FAIL=2;
    private final Handler handler;
    private final LoaclCacheUtils loaclCacheUtils;
    private final MemoryCacheUtils memoryCacheUils;
    private ExecutorService service;

    public NetCacheUtils(Handler handler, LoaclCacheUtils loaclCacheUtils, MemoryCacheUtils memoryCacheUils) {
        this.handler=handler;
        service = Executors.newFixedThreadPool(10);
        this.loaclCacheUtils=loaclCacheUtils;
        this.memoryCacheUils=memoryCacheUils;
    }

    public void getBitmapFromNet(String imageUrl, int position) {

          //new Thread(new MyRunable(imageUrl,position)).start();

           service.execute(new MyRunable(imageUrl,position));
    }

    class MyRunable implements Runnable {
        private final String imageUrl;
        private final int position;

        public MyRunable(String imageUrl, int position) {
            this.imageUrl=imageUrl;
            this.position=position;
        }

        @Override
        public void run() {

            //子线程网络请求图片
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(4000);
                connection.setReadTimeout(4000);
                connection.connect();
                int code = connection.getResponseCode();
                if (code==200){

                    InputStream is = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);


                    Message msg = Message.obtain();
                    msg.what= SUCESS;
                    msg.arg1=position;
                    msg.obj=bitmap;
                    handler.sendMessage(msg);

                    //保存一份在内存
                    memoryCacheUils.putBitmap(imageUrl,bitmap);

                    //保存一份在本地
                    loaclCacheUtils.putBitmap(imageUrl,bitmap);
                }

            } catch (IOException e) {
                e.printStackTrace();

                Message msg = Message.obtain();
                msg.what= FAIL;
                msg.arg1=position;

                handler.sendMessage(msg);
            }
        }
    }
}
