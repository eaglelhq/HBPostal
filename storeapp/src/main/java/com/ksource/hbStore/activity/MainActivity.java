package com.ksource.hbStore.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.blankj.utilcode.utils.LogUtils;
import com.blankj.utilcode.utils.SPUtils;
import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.ksource.hbStore.MyApplication;
import com.ksource.hbStore.R;
import com.ksource.hbStore.bean.CheckResultBean;
import com.ksource.hbStore.config.ConstantValues;
import com.ksource.hbStore.fragment.FirstFragment;
import com.ksource.hbStore.fragment.FourthFragment;
import com.ksource.hbStore.fragment.SecondFragment;
import com.ksource.hbStore.fragment.ThreeFragment;
import com.ksource.hbStore.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends FragmentActivity implements
        android.view.View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private String token;
    private LoadDialog mLoadDialog;

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "该应用需要的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }

    private final class OnMyCheckedChangeListener implements
            OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
//			int index = 0;
            switch (checkedId) {
                case R.id.rb_1:
//				index = 0;
                    switchFragment(0);
                    break;
                case R.id.rb_2:
//					index = 1;
                    switchFragment(1);
                    break;
                case R.id.rb_3:
//					index = 2;
                    switchFragment(2);
                    break;
                case R.id.rb_4:
//					index = 3;
                    switchFragment(3);
                    break;

                default:
                    return;
            }
//			getSupportFragmentManager().beginTransaction()
//					.replace(R.id.fl_content, fragments.get(index)).commit();
        }
    }


    private List<Fragment> fragments;
    private Context context = null;
    private SPUtils sp;

    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        MyApplication.getInstance().addActivity(this);
        sp = new SPUtils(ConstantValues.SP_NAME);
        requestCodeQRCodePermissions();
        String userId = sp.getString(ConstantValues.SELLER_ID, "");
        // 个推推送服务器初始化
        Log.d("GetuiSdk", "initializing sdk...");
        PushManager.getInstance().initialize(this);
        boolean isLooper = false;
        // 回调得到 个推的 clientId
        String clientId = PushManager.getInstance().getClientid(this);
        while (!isLooper) {
            if (TextUtils.isEmpty(clientId)) {
                // 个推推送服务器初始化
                PushManager.getInstance().initialize(MainActivity.this);
                clientId = PushManager.getInstance().getClientid(context);
            }
            isLooper = PushManager.getInstance().bindAlias(context, userId);
        }
        RadioGroup rg_home = (RadioGroup) findViewById(R.id.rg_home);
        token = sp.getString(ConstantValues.TOKEN);
        fragments = new ArrayList<>();
        fragments.add(new FirstFragment());
        fragments.add(new SecondFragment());
        fragments.add(new ThreeFragment());
        fragments.add(new FourthFragment());
        // 添加默认选中
        rg_home.check(R.id.rb_1);
        switchFragment(0);
        rg_home.setOnCheckedChangeListener(new OnMyCheckedChangeListener());
        checkVision();
    }

    //检查版本
    private void checkVision() {
//		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "检查更新");
        Map<String, String> params = new HashMap<String, String>();
//        params.put("token", token);
        params.put("versionName", getVersionCode(context));
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                LogUtils.e("CheckVersion", arg0);
                CheckResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(arg0, CheckResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean != null) {
                    if (resultBean.isUpdate == 0) {
                        Intent intent = new Intent(context,
                                UpDateActivity.class);
                        intent.putExtra("url",
                                ConstantValues.BASE_URL
                                        + resultBean.URL);
                        intent.putExtra("describes",
                                resultBean.DESCRIBES);
                        intent.putExtra("version",
                                resultBean.VERSION_NAME);
                        startActivity(intent);
//                        UpdateManager updateManager = new UpdateManager(context, ConstantValues.BASE_URL + resultBean.URL, resultBean.DESCRIBES, resultBean.VERSION_NAME);
//                        updateManager.checkUpdateInfo();
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
//				ToastUtil.showTextToast(context, "检查版本失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.CKECK_UPDATA_URL, params, callback);
    }

    /**
     * 获取apk的版本号
     *
     * @param context
     * @return
     */
    public String getVersionCode(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            default:
                break;
        }

    }

    /**
     * 点击切换fragment
     *
     * @param position
     */

    public void switchFragment(int position) {
        //开启事务
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        //遍历集合
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            if (i == position) {
                //显示fragment
                if (fragment.isAdded()) {
                    //如果这个fragment已经被事务添加,显示
                    fragmentTransaction.show(fragment);
                } else {
                    //如果这个fragment没有被事务添加过,添加
                    fragmentTransaction.add(R.id.fl_content, fragment);
                }
            } else {
                //隐藏fragment
                if (fragment.isAdded()) {
                    //如果这个fragment已经被事务添加,隐藏
                    fragmentTransaction.hide(fragment);
                }
            }
        }
        //提交事务
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
