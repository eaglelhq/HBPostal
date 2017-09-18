package com.ksource.hbStore.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.blankj.utilcode.utils.NetworkUtils;
import com.ksource.hbStore.R;
import com.ksource.hbStore.config.ConstantValues;
import com.yitao.util.ToastUtil;

/**
 * 闪屏界面
 */
public class SplashActivity extends BaseActivity {
    private String TAG = "TAG";

    private ImageView splash_view;
    // private Banner banner;

    // "file://" +ConstantValues.DOWNLOADFILEPATH
    // private String[] imagesUrl = { "" };
    // private int[] imagesUrl = { R.drawable.loading_pic };

    private Animation animation;
    private String token;
    private String sellerId,sellerName;

    // private boolean timeOut;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        if (!isTaskRoot()){
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)){
                finish();
                return;
            }
        }
        splash_view = (ImageView) findViewById(R.id.icon_splash);
    }

    @Override
    public void initListener() {
    }

    @Override
    public void initData() {
        // final boolean isShowBanner =
        // sp.getBoolean(ConstantValues.IS_SHOW_BANNER,
        // true);
        // handler.sendEmptyMessageDelayed(OPEN_MAIN, 4000);
        isConnected = NetworkUtils.isConnected();
        if (!isConnected) {
            ToastUtil.showTextToast(context, "网络未连接！");
        } else {
//            checkPwd();
        }

        token = spUtils.getString(ConstantValues.TOKEN);
        sellerId = spUtils.getString(ConstantValues.USER_ID);
        sellerName = spUtils.getString(ConstantValues.USER_NAME);
        initAnim();
        splash_view.setAnimation(animation);
    }


    private void initAnim() {
        animation = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(2000);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // timeOut = true;
                // splash_view.setVisibility(View.GONE);
                if (TextUtils.isEmpty(token) || TextUtils.isEmpty(sellerId) || TextUtils.isEmpty(sellerName)){
                    startActivity(new Intent(context, LoginActivity.class));
                }else{
                    startActivity(new Intent(context, MainActivity.class));
                }
                finish();
            }
        });
    }


    @Override
    public void onClick(View v) {

    }

    private boolean isConnected;

    @Override
    public void onBackPressed() {
//		super.onBackPressed();
    }

}
