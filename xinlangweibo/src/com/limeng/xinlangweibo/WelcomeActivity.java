package com.limeng.xinlangweibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limeng.xinlangweibo.util.AccessTokenKeeper;
import com.limeng.xinlangweibo.util.NowUserKeeper;
import com.limeng.xinlangweibo.util.Tools;
import com.weibo.sdk.android.Oauth2AccessToken;

/**
 * 欢迎界面
 * 
 * @author limeng
 */
public class WelcomeActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.tv_center);
        RelativeLayout r = (RelativeLayout) findViewById(R.id.welcome);
        // 动画 渐变
        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
        animation.setDuration(5000);
        // 动画事件监听
        animation.setAnimationListener(new AnimationListener() {
            
            // 动画启动时执行
            @Override
            public void onAnimationStart(Animation animation) {
                
            }
            
            // 动画重复时执行
            @Override
            public void onAnimationRepeat(Animation animation) {
                
            }
            
            // 动画结束时执行
            @Override
            public void onAnimationEnd(Animation animation) {
                init();
            }
        });
        // 关联动画
        r.setAnimation(animation);
        // if (Tools.isNetworkAvailable(WelcomeActivity.this)) {
        // // 时间跳转
        // Timer timer = new Timer();
        // timer.schedule(new TimerTask() {
        //
        // @Override
        // public void run()
        //
        // {
        //
        // Intent intent = new Intent(WelcomeActivity.this, FragmentTabs.class);
        // startActivity(intent);
        // WelcomeActivity.this.finish();
        // }
        // }, 8000);
        //
        // }
    }
    
    private void init() {
        Tools.checkNetwork(WelcomeActivity.this);
        // UserDao dao = new UserDao(this);
        
        //
        // List<User> userList = dao.findAllUsers();
        //
        // if (userList == null || userList.isEmpty()) {
        // if (userList == null || userList.isEmpty()) {
        // Intent intent = new Intent(this, OAuthActivity.class);
        // startActivity(intent);
        // finish();
        // }
        // } else {
        // Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        // startActivity(intent);
        // finish();
        // }
        Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        if (Tools.isNetworkAvailable(WelcomeActivity.this)) {
            if (!token.isSessionValid() && NowUserKeeper.readUserInfo(getApplicationContext()) != null) {
                Intent intent = new Intent(this, OAuthActivity.class);
                startActivity(intent);
                finish();
            } else if (NowUserKeeper.readUserInfo(getApplicationContext()).getId().equals("limeng")) {
                Intent intent = new Intent(this, OAuthActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, FragmentTabs.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
