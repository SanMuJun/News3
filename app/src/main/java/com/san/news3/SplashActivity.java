package com.san.news3;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.san.news3.activity.GuideActivity;
import com.san.news3.activity.MainActivity;
import com.san.news3.utils.CacheUtils;

public class SplashActivity extends Activity {
    public static final String START_MAIN = "start_main";
    private RelativeLayout rl_splash_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        rl_splash_root= (RelativeLayout) findViewById(R.id.rl_splash_root);
        //渐变
        AlphaAnimation aa=new AlphaAnimation(0,1);
        aa.setFillAfter(true);
        //缩放
        ScaleAnimation sa=new ScaleAnimation(0,1,0,1, ScaleAnimation.RELATIVE_TO_SELF,0.5F,ScaleAnimation.RELATIVE_TO_SELF,0.5F);
        sa.setFillAfter(true);
        //旋转
        RotateAnimation ra=new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        ra.setFillAfter(true);

        AnimationSet set=new AnimationSet(false);
        //没有先后顺序之分
        set.addAnimation(aa);
        set.addAnimation(sa);
        set.addAnimation(ra);

        set.setDuration(2000);
        rl_splash_root.startAnimation(set);
        set.setAnimationListener(new MyAnimationListener());
    }

    private class MyAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }
        @Override
        public void onAnimationEnd(Animation animation) {
            //判断有没有进入过主页面
            boolean isStartMain= CacheUtils.getBoolean(SplashActivity.this, START_MAIN);
            if(isStartMain){
                //进入过主页面
                //跳转到主页面
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }else{
                //没有进入过主页面
                Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                startActivity(intent);
            }
            //关闭Splash
            finish();
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
