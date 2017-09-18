package com.ksource.hbpostal;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.blankj.utilcode.utils.SPUtils;
import com.blankj.utilcode.utils.Utils;
import com.google.gson.Gson;
import com.ksource.hbpostal.activity.LoginActivity;
import com.ksource.hbpostal.bean.LoginResult;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ResolutionUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.yitao.util.LogUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Cookie;

public class MyApplication extends Application {
    public static Cookie cookie = null;
    public static final String USER_ACCOUNT = "user_account";// 账号
    public static final String USER_PASSWORD = "user_password";// 密码
    public static final String REMEMBER_PASSWORD = "remember_password";// 记住密码
    public static final String AUTOMATICALLY_LOGIN = "automatically_login";// 自动登录
    private static MyApplication instance;
    private static Context mContext;
    private List<WeakReference<Activity>> mActivityList;
    private SharedPreferences mSharedPreferences;

    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        initFilePath();
        mContext = this.getApplicationContext();
        instance = this;
        mActivityList = new ArrayList<WeakReference<Activity>>();
        // mSharedPreferences = PreferenceManager
        // .getDefaultSharedPreferences(this);
        mSharedPreferences = getSharedPreferences(ConstantValues.SP_NAME,
                MODE_PRIVATE);
        Utils.init(mContext);
        SDKInitializer.initialize(this);
        // TODO 程序异常捕捉
        CrashLog.getInstance().init(mContext);
        ResolutionUtil.getInstance().init(this);
        // 初始化图片加载
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                // 线程池内加载的数量
                .threadPoolSize(5)
                // 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .discCache(
                        new UnlimitedDiscCache(new File(
                                ConstantValues.CACHE_PATH)))
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }

    // 登录获取token存到本地
    public void login() {
        // 联网登录接口
        Map<String, String> userInfos = new HashMap<>();
        String userStr = mSharedPreferences.getString(
                ConstantValues.KEY_USERNAME, "");
        String pwdStr = mSharedPreferences.getString(
                ConstantValues.KEY_PASSWORD, "");
        userInfos.put("logSign", userStr);
        userInfos.put("passWorld", pwdStr);
        new SPUtils(ConstantValues.SP_NAME).clear();
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                Gson gson = new Gson();
                LoginResult loginResult = null;
                try {
                    loginResult = gson.fromJson(arg0, LoginResult.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (loginResult == null) {
                    mContext.startActivity(intent);
                    return;
                }
                LogUtils.i("LoginActivity", loginResult.toString());
                if (loginResult.success) {
                    // 存储token到本地
                    mSharedPreferences.edit()
                            .putString(ConstantValues.TOKEN, loginResult.token)
                            .apply();
                    mSharedPreferences
                            .edit()
                            .putString(ConstantValues.USER_ID,
                                    loginResult.userId).apply();
                } else {
                    // 密码错误
                    mContext.startActivity(intent);
                }
                // ToastUtil.showTextToast(mContext, loginResult.msg);

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {

            }
        };
        DataUtil.doPostAESData(null,mContext, ConstantValues.LOGIN_URL, userInfos, callback);
    }

    // 登录获取token存到本地
    public void login(final String userName, final String pwd) {
        // 联网登录接口
        Map<String, String> userInfos = new HashMap<>();
        userInfos.put("logSign", userName);
        userInfos.put("passWorld", pwd);
        new SPUtils(ConstantValues.SP_NAME).clear();
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                Gson gson = new Gson();
                LoginResult loginResult = null;
                try {
                    loginResult = gson.fromJson(arg0, LoginResult.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (loginResult == null) {
                    mContext.startActivity(intent);
                    return;
                }
                LogUtils.i("LoginActivity", loginResult.toString());
                if (loginResult.success) {
                    mSharedPreferences.edit()
                            .putString(ConstantValues.KEY_USERNAME, userName)
                            .apply();
                    mSharedPreferences.edit()
                            .putString(ConstantValues.KEY_PASSWORD, pwd)
                            .apply();
                    // 存储token到本地
                    mSharedPreferences.edit()
                            .putString(ConstantValues.TOKEN, loginResult.token)
                            .apply();
                    mSharedPreferences
                            .edit()
                            .putString(ConstantValues.USER_ID,
                                    loginResult.userId).apply();
                } else {
                    // 密码错误
                    mContext.startActivity(intent);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        };
        DataUtil.doPostAESData(null,mContext, ConstantValues.LOGIN_URL, userInfos, callback);
    }

    /**
     * 方法说明：获取请求ip<br>
     *
     * @return <br>
     */
    public String getURL() {
        String ip = getSharedPreferences("ip", "");
        String port = getSharedPreferences("port", "");
        String work = getSharedPreferences("work", "");
        String url = "";
        if (!"".equals(ip) && !"".equals(port)) {
            url = "http://" + ip + ":" + port;
            if (!"".equals(work)) {
                url += "/" + work;
            }
        }
        return url;
    }

    private void initFilePath() {
        File file_down = new File(ConstantValues.DOWNLOADFILEPATH);
        if (!file_down.exists()) {
            file_down.mkdirs();
        }
        File file_cache = new File(ConstantValues.CACHE_PATH);
        if (!file_cache.exists()) {
            file_cache.mkdirs();
        }
        File file_image = new File(ConstantValues.IMAGEFILEPATH);
        if (!file_image.exists()) {
            file_image.mkdirs();
        }
    }

    public void addActivity(Activity pActivity) {
        mActivityList.add(new WeakReference<Activity>(pActivity));
    }

    public void removeActivity(Activity pActivity) {
        mActivityList.remove(new WeakReference<Activity>(pActivity));
    }

    public void finishAllActivity() {
        for (WeakReference<Activity> _ActivityRef : mActivityList) {
            try {
                _ActivityRef.get().finish();
            } catch (NullPointerException e) {
                Log.e("ActivityDestroyed.", e.toString());
            }
        }
        mActivityList.clear();
    }

    public void finishActivity(String activityName) {
        for (WeakReference<Activity> _ActivityRef : mActivityList) {
            try {
                String allName = _ActivityRef.get().getClass().getName();
                String simpleName = allName
                        .substring(allName.lastIndexOf(".") + 1);
                if (simpleName.equals(activityName)) {
                    _ActivityRef.get().finish();
                }
                // if (_ActivityRef.get().getClass().getName()
                // .equals(activityName)) {
                // _ActivityRef.get().finish();
                // }
                ;
            } catch (NullPointerException e) {
                Log.e("ActivityDestroyed", e.toString());
            }
        }
    }

    /**
     * @param activityName 全名
     * @return
     */
    public boolean isActivityExists(String activityName) {
        for (WeakReference<Activity> _ActivityRef : mActivityList) {
            try {
                String allName = _ActivityRef.get().getClass().getName();
                String simpleName = allName.substring(allName.lastIndexOf(".") + 1);
                if (simpleName.equals(activityName)) {
                    return true;
                }
//				if (_ActivityRef.get().getClass().getName()
//						.equals(activityName)) {
//					return true;
//				}
            } catch (NullPointerException e) {
                Log.e("ActivityDestroyed", e.toString());
            }
        }
        return false;
    }

    public static Context Ct() {
        return mContext;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public boolean putSharedPreferences(String key, String value) {
        return mSharedPreferences.edit().putString(key, value).commit();
    }

    public String getSharedPreferences(String key, String value) {
        return mSharedPreferences.getString(key, value);
    }

    public boolean putSharedPreferencesForObject(String key, Object value) {
        return mSharedPreferences.edit()
                .putString(key, new Gson().toJson(value)).commit();
    }

    public <T> T getSharedPreferencesForObject(String key, Class<T> clazz) {
        return new Gson()
                .fromJson(mSharedPreferences.getString(key, ""), clazz);
    }

    public int getSharedPreferencesForInt(String key, int value) {
        return mSharedPreferences.getInt(key, value);
    }

    public boolean putSharedPreferencesForInt(String key, int value) {
        return mSharedPreferences.edit().putInt(key, value).commit();
    }

    public boolean getSharedPreferencesForBoolean(String key, boolean value) {
        return mSharedPreferences.getBoolean(key, value);
    }

    public boolean putSharedPreferencesForBoolean(String key, boolean value) {
        return mSharedPreferences.edit().putBoolean(key, value).commit();
    }
}
