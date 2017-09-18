package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.LoginResult;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.LogUtils;
import com.yitao.util.NetStateUtils;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 登陆页面
 */
public class LoginActivity extends BaseActivity {

    private EditText et_user, et_pwd;
    private Button btn_login;
    private LoadDialog mLoadDialog;
    private TextView tv_miss_pwd, tv_register;
    private ImageView iv_show_pwd;
    private boolean isShowPwd = false;
    private boolean isBack;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    public void initListener() {
        iv_show_pwd.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_miss_pwd.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        et_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(et_pwd.getText()) || TextUtils.isEmpty(et_user.getText())) {
                    btn_login.setEnabled(false);
                } else {
                    btn_login.setEnabled(true);
                }
            }
        });
        et_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(et_pwd.getText()) || TextUtils.isEmpty(et_user.getText())) {
                    btn_login.setEnabled(false);
                } else {
                    btn_login.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void initData() {
        if (getIntent().hasExtra("phone")) {
            String phone = getIntent().getStringExtra("phone");
            et_user.setText(phone);
        }
        isBack = getIntent().getBooleanExtra("isBack", false);
        if (isShowPwd) {
            et_pwd.setTransformationMethod(HideReturnsTransformationMethod
                    .getInstance());
            iv_show_pwd.setImageResource(R.drawable.open_eye);
        } else {
            et_pwd.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
            iv_show_pwd.setImageResource(R.drawable.close_eye);
        }

    }

    @Override
    public void initView() {
        et_user = (EditText) findViewById(R.id.et_user);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_user.setText(sp.getString(ConstantValues.KEY_USERNAME, ""));
        et_pwd.setText(sp.getString(ConstantValues.KEY_PASSWORD, ""));

        tv_miss_pwd = (TextView) findViewById(R.id.tv_miss_pwd);
        tv_register = (TextView) findViewById(R.id.tv_register);
        iv_show_pwd = (ImageView) findViewById(R.id.iv_show_pwd);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setEnabled(false);
        // boolean isAutoLogin = sp.getBoolean(ConstantValues.KEY_ISAUTOLOGIN,
        // true);
        // if (isAutoLogin) {
        // login();
        // }
    }

    private void login() {
        final String userStr = et_user.getText().toString().trim();
        final String pwdStr = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(userStr) || TextUtils.isEmpty(pwdStr)) {
            ToastUtil.showTextToast(context, "请输入用户名或密码！");
        } else if (!NetStateUtils.isNetworkAvailable(context)) {
            ToastUtil.showTextToast(context, "当前网络不可用！");
            boolean userIsTrue = userStr.equals(sp.getString(
                    ConstantValues.KEY_USERNAME, ""));
            boolean pwdIsTrue = pwdStr.equals(sp.getString(
                    ConstantValues.KEY_PASSWORD, ""));
            if (userIsTrue && pwdIsTrue) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        } else {
            sp.edit().putString(ConstantValues.KEY_USERNAME, userStr).apply();
            sp.edit().putString(ConstantValues.KEY_PASSWORD, pwdStr).apply();
            mLoadDialog = DialogUtil.getInstance().showLoadDialog(
                    LoginActivity.this, "登录中...");
            //联网登录接口
            Map<String, String> userInfos = new HashMap<String, String>();
            userInfos.put("logSign", userStr);
            userInfos.put("passWorld", pwdStr);
            StringCallback callback = new StringCallback() {

                @Override
                public void onResponse(String arg0, int arg1) {
                    DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                    LoginResult loginResult = null;
                    Gson gson = new Gson();
                    try {
                        loginResult = gson.fromJson(arg0, LoginResult.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (loginResult == null) {
                        ToastUtil.showTextToast(context, "获取信息失败！");
                        boolean userIsTrue = userStr.equals(sp
                                .getString(ConstantValues.KEY_USERNAME,
                                        ""));
                        boolean pwdIsTrue = pwdStr.equals(sp.getString(
                                ConstantValues.KEY_PASSWORD, ""));
                        if (userIsTrue && pwdIsTrue) {
                            startActivity(new Intent(context,
                                    MainActivity.class));
                            finish();
                        }
                        return;
                    }
                    LogUtils.i("LoginActivity", loginResult.toString());
                    if (loginResult.success) {
                        // 存储token到本地
                        sp.edit()
                                .putString(ConstantValues.TOKEN,
                                        loginResult.token).apply();
                        sp.edit()
                                .putString(ConstantValues.USER_ID,
                                        loginResult.userId).apply();
                        if (isBack) {
                            finish();
                        } else {
                            startActivity(new Intent(context,
                                    MainActivity.class));
                            mApplication.finishAllActivity();
                        }
                    } else {
                        if (loginResult.flag == 2) {
                            // 用户名不存在
                            // et_user.setText("");
                            et_user.requestFocus();
                        } else if (loginResult.flag == 3) {
                            // 密码错误
                            // et_pwd.setText("");
                            et_pwd.requestFocus();
                        }
                        ToastUtil.showTextToast(context,
                                loginResult.msg);
                    }

                }

                @Override
                public void onError(Call arg0, Exception arg1, int arg2) {
                    DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                    ToastUtil.showTextToast(context, "获取信息失败！");
                    boolean userIsTrue = userStr.equals(sp
                            .getString(ConstantValues.KEY_USERNAME,
                                    ""));
                    boolean pwdIsTrue = pwdStr.equals(sp.getString(
                            ConstantValues.KEY_PASSWORD, ""));
                    if (userIsTrue && pwdIsTrue) {
                        startActivity(new Intent(context,
                                MainActivity.class));
                        finish();
                    }
                }
            };
            DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.LOGIN_URL, userInfos, callback);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_miss_pwd:
                // finish();
                Intent intent = new Intent(context, MissPwdActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
                break;
            case R.id.tv_register:
                // finish();
                startActivity(new Intent(context, RegisterActivity.class));
                break;
            case R.id.iv_show_pwd:
                isShowPwd = !isShowPwd;
                if (isShowPwd) {
                    et_pwd.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                    iv_show_pwd.setImageResource(R.drawable.open_eye);
                } else {
                    et_pwd.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                    iv_show_pwd.setImageResource(R.drawable.close_eye);
                }
                break;
            case R.id.btn_login:
                // finish();
                // startActivity(new Intent(context, MainActivity.class));
                // sp.edit().putBoolean(ConstantValues.KEY_ISAUTOLOGIN,
                // true).apply();
                login();
                break;

            default:
                break;
        }

    }

}
