package com.ksource.hbpostal.activity;

import android.Manifest;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.ksource.hbpostal.MyApplication;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.CheckResultBean;
import com.ksource.hbpostal.bean.CountResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * 主页面
 */
public class MainActivity extends TabActivity implements
        View.OnClickListener,EasyPermissions.PermissionCallbacks{
private static final int REQUEST_CODE_QRCODE_PERMISSIONS=1;
private String token;

private final class OnMyCheckedChangeListener implements
        OnCheckedChangeListener {

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int index = 0;
        switch (checkedId) {
            case R.id.rb_home:
                index = 0;
                mTabHost.setCurrentTabByTag(TAB_INDEX);
                break;
            case R.id.rb_cart:
                if (TextUtils.isEmpty(token)) {
                    setCheck(index);
                    // finish();
                    startActivity(new Intent(context, LoginActivity.class));
                    return;
                }
                index = 1;
                mTabHost.setCurrentTabByTag(TAB_CART);
                break;
//            case R.id.rb_cate:
//                index = 2;
//                mTabHost.setCurrentTabByTag(TAB_CATE);
//                break;
            case R.id.rb_me:
                if (TextUtils.isEmpty(token)) {
                    setCheck(index);
                    // finish();
                    startActivity(new Intent(context, LoginActivity.class));
                    return;
                }
                index = 3;
                mTabHost.setCurrentTabByTag(TAB_ME);
                break;

            default:
                return;
        }
    }

}

//    protected static final int UPLOAD_LOCATIONG = 100;

    // private List<Fragment> fragments;
    private Context context = null;
    private SharedPreferences sp;
    private MyApplication mApplication;
    public static TabHost mTabHost;
    public static TextView tv_cart_num;
    // private RadioButton[] btns;

    public static RadioGroup rg_home;
    // Bundle savedInstanceState;

    public static final String TAB_INDEX = "TAB_INDEX";
    public static final String TAB_CART = "TAB_CART";
//    public static final String TAB_CATE = "TAB_CATE";
    public static final String TAB_ME = "TAB_ME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // this.savedInstanceState = savedInstanceState;
        context = this;
        mApplication = MyApplication.getInstance();
        mApplication.addActivity(this);
        sp = getSharedPreferences(ConstantValues.SP_NAME, MODE_PRIVATE);
        token = sp.getString(ConstantValues.TOKEN, null);
//		boolean isConnected = NetworkUtils.isConnected();
//		if (!isConnected) {
//			SweetAlertDialog dialog = new SweetAlertDialog(context,
//					SweetAlertDialog.WARNING_TYPE);
//			dialog.setTitleText("网络未连接！").setContentText("去设置网络")
//					.setCancelText("取消")
//					.setConfirmText("去设置")
//					.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//						@Override
//						public void onClick(SweetAlertDialog sweetAlertDialog) {
//							sweetAlertDialog.dismiss();
//							NetworkUtils.openWirelessSettings();
//						}
//					}).show();
//		} else {
        checkVersion();
        // boolean isUpdae = sp.getBoolean(ConstantValues.IS_UPDATE, false);
        // if (isUpdae) {
        // new UpdateManager(context,
        // ConstantValues.APP_DOWNLOAD_URL).checkUpdateInfo();
        // }
//		}
        // 透明状态栏
        // getWindow()
        // .addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // // 透明导航栏
        // getWindow().addFlags(
        // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        // new GetUserInfoList().execute();
        String userId = sp.getString(ConstantValues.USER_ID, "");
        // 个推推送服务器初始化
        Log.d("GetuiSdkDemo", "initializing sdk...");
        PushManager.getInstance().initialize(this);
        requestCodeQRCodePermissions();
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

        rg_home = (RadioGroup) findViewById(R.id.rg_home);
        tv_cart_num = (TextView) findViewById(R.id.tv_cart_num);
        rg_home.setOnCheckedChangeListener(new OnMyCheckedChangeListener());
        mTabHost = getTabHost();
        mTabHost.addTab(mTabHost.newTabSpec(TAB_INDEX).setIndicator(TAB_INDEX)
                .setContent(new Intent(this, ActivityIndex1.class)));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_CART).setIndicator(TAB_CART)
                .setContent(new Intent(this, ActivityCart.class)));
//        mTabHost.addTab(mTabHost.newTabSpec(TAB_CATE).setIndicator(TAB_CATE)
//                .setContent(new Intent(this, ActivityCate.class)));// ActivityLinkManFirstAndSecond
        mTabHost.addTab(mTabHost.newTabSpec(TAB_ME).setIndicator(TAB_ME)
                .setContent(new Intent(this, ActivityMe.class)));
        mTabHost.setCurrentTabByTag(TAB_INDEX);

        if (getIntent().getBooleanExtra("bindCard", false)) {
            SweetAlertDialog dialog = new SweetAlertDialog(context,
                    SweetAlertDialog.WARNING_TYPE);
            dialog.setTitleText("您现在是未认证会员").setCancelText("先不了")
                    .setConfirmText("实名认证")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            Intent intent = new Intent(context,
                                    AddCardStep1Activity.class);
                            intent.putExtra("title", "实名认证");
                            startActivity(intent);
                        }
                    }).show();
        }
        if (getIntent().hasExtra("item")) {

            int currItem = getIntent().getIntExtra("item", 0);
            switch (currItem) {
                // 添加默认选中
                case 0:
                    rg_home.check(R.id.rb_home);
                    break;
                case 1:
                    rg_home.check(R.id.rb_cart);
                    break;
//                case 2:
//                    rg_home.check(R.id.rb_cate);
//                    break;
                case 3:
                    rg_home.check(R.id.rb_me);
                    break;

                default:
                    break;
            }
        } else {
            rg_home.check(R.id.rb_home);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartNum();
    }

    // 获取购物车数量
    private void getCartNum() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                Gson gson = new Gson();
                CountResultBean cartCountResult = null;
                try {
                    cartCountResult = gson.fromJson(arg0,
                            CountResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cartCountResult == null) {
                    ToastUtil.showTextToast(context, "获取购物车数量失败！");
                    return;
                }
                if (cartCountResult.success) {
                    int cartCount = cartCountResult.cartCount;
                    if (cartCount > 0) {
                        tv_cart_num.setVisibility(View.VISIBLE);
                        tv_cart_num.setText("" + cartCount);
                    } else {
                        tv_cart_num.setVisibility(View.GONE);
                    }
                } else if (cartCountResult.flag == 10) {

                } else {
                    ToastUtil.showTextToast(context,
                            cartCountResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
//				ToastUtil.showTextToast(context, "获取购物车数量失败！");
            }
        };
        DataUtil.doPostAESData(null, context, ConstantValues.CART_COUNT_URL, params, callback);
    }

    public void setCheck(int index) {
        switch (index) {
            case 0:
                rg_home.check(R.id.rb_home);
                break;
            case 1:
                rg_home.check(R.id.rb_cart);
                break;
            case 2:
                rg_home.check(R.id.rb_cate);
                break;
            case 3:
                rg_home.check(R.id.rb_me);
                break;

            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {

    }

    private long mExitTime = 0;

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

    /**
     * 检查版本
     */
    private void checkVersion() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("version_name", "" + getVersionCode(context));
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                Gson gson = new Gson();
                CheckResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(arg0,
                            CheckResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean != null) {
                    if (resultBean.flag == 1) {
                        if (mTabHost != null && mTabHost.getCurrentTabTag() != null
                                && TAB_INDEX.equals(mTabHost
                                .getCurrentTabTag())) {
                            Intent intent = new Intent(context,
                                    UpDateActivity.class);
                            intent.putExtra("url",
                                    ConstantValues.BASE_URL
                                            + resultBean.url);
                            intent.putExtra("describes",
                                    resultBean.describes);
                            intent.putExtra("version",
                                    resultBean.version_name);
                            intent.putExtra("isUpdate",
                                    resultBean.FORCE_UPDATE == 1);
                            startActivity(intent);
//                            UpdateManager updateManager = new UpdateManager(context,ConstantValues.BASE_URL+resultBean.url,resultBean.describes,resultBean.version_name);
//                            updateManager.checkUpdateInfo();
                        }
                    } else {
                        // new SweetAlertDialog(context,
                        // SweetAlertDialog.WARNING_TYPE)
                        // .setTitleText(resultBean.msg).show();
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
//				ToastUtil.showTextToast(context, arg1.getMessage());
            }
        };
        DataUtil.doPostAESData(null, context, ConstantValues.CKECK_UPDATA_URL, params, callback);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        ToastUtils.showShortToast("---"+perms);
        if (!EasyPermissions.hasPermissions(this, perms)) {
//            ToastUtils.showShortToast("hasPermissions"+perms);
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和闪光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }
}
