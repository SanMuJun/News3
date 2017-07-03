package com.san.news3.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by San on 2016/12/14.
 */
public class LoaclCacheUtils {


    private final MemoryCacheUtils memoryCacheUils;

    public LoaclCacheUtils(MemoryCacheUtils memoryCacheUils) {
        this.memoryCacheUils=memoryCacheUils;
    }

    public Bitmap getBitmapFromUrl(String imageUrl) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //保存在/mmt/sdcard/news/http:/...
            try {
                String fileName = MD5Encoder.encode(imageUrl);
               // File file = new File(Environment.getExternalStorageDirectory() + "/news3",fileName);
                File file = new File( "mnt/sdcard/news3",fileName);
                LogUtil.e("本地存储路径"+file);

                if (file.exists()){

                    FileInputStream is = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    if (bitmap!=null){
                        memoryCacheUils.putBitmap(imageUrl,bitmap);
                        LogUtil.e("从本地保存到内存中");
                    }
                    return bitmap;
                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("本地存储获取失败"+e.toString());
            }
        }

        return null;

    }

    public void putBitmap(String imageUrl, Bitmap bitmap) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //保存在/mmt/sdcard/news/http:/...
            try {
                String fileName = MD5Encoder.encode(imageUrl);
                //本地地址注意“  + ”  “  ， ”
             // File file = new File(Environment.getExternalStorageDirectory() + "/news3",fileName);
                File file = new File( "mnt/sdcard/news3",fileName);
                LogUtil.e("本地存储路径"+file);

                File parentFile = file.getParentFile();
                if (!parentFile.exists()){
                    parentFile.mkdirs();
                }
                if (!file.exists()){
                    file.createNewFile();
                }
                //保存图片
                bitmap.compress(Bitmap.CompressFormat.PNG,100, new FileOutputStream(file));
            } catch (Exception e) {
                e.printStackTrace();

                LogUtil.e("本地存储失败"+e.toString());
            }
        }
    }
}
