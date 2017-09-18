package com.larks.testapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.kernal.passport.sdk.utils.AppManager;
import com.kernal.passport.sdk.utils.CheckPermission;
import com.kernal.passport.sdk.utils.Devcode;
import com.kernal.passport.sdk.utils.PermissionActivity;
import com.kernal.passport.sdk.utils.SharedPreferencesHelper;
import com.kernal.passportreader.sdk.CameraActivity;
import com.kernal.passportreader.sdk.MainActivity;
import com.larks.testapp.R;


/**
 * Created by huangzhen on 2016/12/19.
 */

public class FirstActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main01);
//        Intent intent=new Intent(FirstActivity.this,IdCardMainActivity.class);
//        FirstActivity.this.finish();
//        startActivity(intent);
        /**
         * 由于在相机界面释放相机等资源会耗费很多时间， 为了优化用户体验，需要在调用相机的那个界面的oncreate()方法中
         * 调用AppManager.getAppManager().finishAllActivity();
         * 如果调用和识别的显示界面是同一个界面只需调用一次即可， 如果是不同界面，需要在显示界面的oncreate()方法中
         * 调用AppManager.getAppManager().finishAllActivity();即可，
         * 否则会造成相机资源的内存溢出。
         */
        AppManager.getAppManager().finishAllActivity();
        Intent intent = new Intent(FirstActivity.this, CameraActivity.class);
        if (Build.VERSION.SDK_INT >= 23) {
            CheckPermission checkPermission = new CheckPermission(this);
            if (checkPermission.permissionSet(MainActivity.PERMISSION)) {
                PermissionActivity.startActivityForResult(this, 0,
                        SharedPreferencesHelper.getInt(
                                getApplicationContext(), "nMainId", 2),
                        Devcode.devcode, 0,0, MainActivity.PERMISSION);
            } else {
                intent.putExtra("nMainId", SharedPreferencesHelper.getInt(
                        getApplicationContext(), "nMainId", 2));
                intent.putExtra("devcode", Devcode.devcode);
                intent.putExtra("flag", 0);
                FirstActivity.this.finish();
                startActivity(intent);
            }
        } else {
            intent.putExtra("nMainId", SharedPreferencesHelper.getInt(
                    getApplicationContext(), "nMainId", 2));
            intent.putExtra("devcode", Devcode.devcode);
            intent.putExtra("flag", 0);
            FirstActivity.this.finish();
            startActivity(intent);
        }
    }
}
