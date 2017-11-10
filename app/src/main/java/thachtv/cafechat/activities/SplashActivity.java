package thachtv.cafechat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import thachtv.cafechat.R;
import thachtv.cafechat.define.Constant;

public class SplashActivity extends AppCompatActivity {

    private ImageView ivSplash;
    private TextView tvSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initUI();
        showAnim();
        nextAction();
    }

    private void initUI() {
        ivSplash = (ImageView) findViewById(R.id.iv_splash);
        tvSplash = (TextView) findViewById(R.id.tv_splash);
    }

    private void showAnim() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_splash);
        tvSplash.startAnimation(animation);
        ivSplash.startAnimation(animation);
    }

    private void nextAction() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.SharePref.CAFE_CHAT_PREF, Context.MODE_PRIVATE);
                if (sharedPreferences.getBoolean(Constant.SharePref.IS_LOGGED_BEFORE, false)) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }else {
                    Intent intent = new Intent(SplashActivity.this, LogInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }
}
