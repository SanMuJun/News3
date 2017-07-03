package com.san.news3.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.san.news3.R;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewsDetailActivity extends Activity implements View.OnClickListener {


    private TextView tvTitle;
    private ImageButton ibMenu;
    private ImageButton ibBack;
    private ImageButton ibTextsize;
    private ImageButton ibShare;
    private WebView webview;
    private ProgressBar pbLoading;
    private WebSettings webSettings;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-12-12 20:45:24 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        tvTitle = (TextView)findViewById( R.id.tv_title );
        ibMenu = (ImageButton)findViewById( R.id.ib_menu );
        ibBack = (ImageButton)findViewById( R.id.ib_back );
        ibTextsize = (ImageButton)findViewById( R.id.ib_textsize );
        ibShare = (ImageButton)findViewById( R.id.ib_share );
        webview = (WebView)findViewById( R.id.webview );
        pbLoading = (ProgressBar)findViewById( R.id.pb_loading );

        tvTitle.setVisibility(View.GONE);
        ibMenu.setVisibility(View.GONE);
        ibBack.setVisibility(View.VISIBLE);
        ibTextsize.setVisibility(View.VISIBLE);
        ibShare.setVisibility(View.VISIBLE);


        ibBack.setOnClickListener( this );
        ibTextsize.setOnClickListener( this );
        ibShare.setOnClickListener( this );
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-12-12 20:45:24 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
      if ( v == ibBack ) {
            finish();
            // Handle clicks for ibBack
        } else if ( v == ibTextsize ) {
          Toast.makeText(NewsDetailActivity.this, "设置文字大小", Toast.LENGTH_SHORT).show();
           showChangeTextSizeDialog();
        } else if ( v == ibShare ) {
          Toast.makeText(NewsDetailActivity.this, "分享", Toast.LENGTH_SHORT).show();
          showShare();
            // Handle clicks for ibShare
        }
    }


    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("呵呵");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl("http://user.qzone.qq.com/1120567103/infocenter?ptsig=8MNihbBMvOnGkqR54FNCX*BOLiE1CBjInqP8j9jCnIs_");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("好无聊");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("谁评论谁是sb");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("Nothing");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://user.qzone.qq.com/1120567103/infocenter?ptsig=8MNihbBMvOnGkqR54FNCX*BOLiE1CBjInqP8j9jCnIs_");

// 启动分享GUI
        oks.show(this);
    }



    private int tempSize=2;
    private  int realSize=tempSize;

    private void showChangeTextSizeDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置字体");

        String[] item={"超大字体","大字体","正常","小字体","超小字体"};
        builder.setSingleChoiceItems(item, realSize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tempSize=i;

            }
        });
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                realSize=tempSize;
                changeTextSize(realSize);

            }
        });
        builder.show();
    }

    private void changeTextSize(int realSize) {
        switch(realSize){

            case 0://超大字体
                webSettings.setTextZoom(250);
                break;
            case 1://大字体
                webSettings.setTextZoom(200);
                break;
            case 2://正常
                webSettings.setTextZoom(150);
                break;
            case 3://小字体
                webSettings.setTextZoom(100);
                break;
            case 4://超小字体
                webSettings.setTextZoom(50);
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        findViews();

        getData();


    }

    private void getData() {
    String url = getIntent().getStringExtra("url");
    //   String url="https://www.baidu.com/index.php?tn=02049043_8_pg";

        webSettings = webview.getSettings();
        //支持JavaScript
        webSettings.setJavaScriptEnabled(true);
        //设置双击放大
        webSettings.setUseWideViewPort(true);
        //设置放大按钮
        webSettings.setBuiltInZoomControls(true);
        //不然当前页面跳转到系统浏览器
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbLoading.setVisibility(View.GONE);
            }
        });

        webview.loadUrl(url);
    }
}
