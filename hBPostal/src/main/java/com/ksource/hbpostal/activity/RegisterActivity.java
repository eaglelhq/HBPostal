package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.PhoneUtils;
import com.blankj.utilcode.utils.RegexUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.observer.SmsObserver;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 用户注册页面
 */
public class RegisterActivity extends BaseActivity {


    private TextView tv_title, tv_get_yzm;
    private TextView tv_xieyi;
    private ImageView iv_back;
    private EditText et_phone_number, et_yzm;
    private EditText et_new_pwd, et_confirm_pwd;
    private Button btn_reset, btn_register;
    private CheckBox cb_agree;

    // 倒计时
    private int recTimer;
    TimerTask task;
    Timer timer;
    //	private boolean ischeck;
    private LoadDialog mLoadDialog;

    SmsObserver smsObserver = new SmsObserver(this, new Handler(),
            new SmsObserver.SmsListener() {
                @Override
                public void onResult(String smsContent) {
                    //TODO 读取验证码，自动填写
//					et_yzm.setText(smsContent);
                }
            });

    @Override
    public int getLayoutResId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_get_yzm = (TextView) findViewById(R.id.tv_get_yzm);
        tv_xieyi = (TextView) findViewById(R.id.tv_xieyi);
        tv_title.setText("会员注册");
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        et_yzm = (EditText) findViewById(R.id.et_yzm);
        et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
        et_confirm_pwd = (EditText) findViewById(R.id.et_confirm_pwd);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setEnabled(false);
        btn_reset.setEnabled(false);
        cb_agree = (CheckBox) findViewById(R.id.cb_agree);
//		getContentResolver().registerContentObserver(
//				Uri.parse("content://sms/"), true, smsObserver);

    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        tv_get_yzm.setOnClickListener(this);
        tv_xieyi.setOnClickListener(this);
        cb_agree.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                btn_register.setEnabled(isChecked && !TextUtils.isEmpty(et_phone_number.getText()) && !TextUtils.isEmpty(et_yzm.getText()) &&
                        !TextUtils.isEmpty(et_confirm_pwd.getText()) && !TextUtils.isEmpty(et_new_pwd.getText()));

            }
        });
        et_phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((!cb_agree.isChecked()) || TextUtils.isEmpty(et_phone_number.getText()) || TextUtils.isEmpty(et_yzm.getText()) ||
                        TextUtils.isEmpty(et_confirm_pwd.getText()) || TextUtils.isEmpty(et_new_pwd.getText())) {
                    btn_register.setEnabled(false);
                } else {
                    btn_register.setEnabled(true);
                }
                if (TextUtils.isEmpty(et_phone_number.getText()) && TextUtils.isEmpty(et_yzm.getText()) &&
                        TextUtils.isEmpty(et_confirm_pwd.getText()) && TextUtils.isEmpty(et_new_pwd.getText())) {
                    btn_reset.setEnabled(false);
                } else {
                    btn_reset.setEnabled(true);
                }
            }
        });
        et_confirm_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((!cb_agree.isChecked()) || TextUtils.isEmpty(et_phone_number.getText()) || TextUtils.isEmpty(et_yzm.getText()) ||
                        TextUtils.isEmpty(et_confirm_pwd.getText()) || TextUtils.isEmpty(et_new_pwd.getText())) {
                    btn_register.setEnabled(false);
                } else {
                    btn_register.setEnabled(true);
                }
                if (TextUtils.isEmpty(et_phone_number.getText()) && TextUtils.isEmpty(et_yzm.getText()) &&
                        TextUtils.isEmpty(et_confirm_pwd.getText()) && TextUtils.isEmpty(et_new_pwd.getText())) {
                    btn_reset.setEnabled(false);
                } else {
                    btn_reset.setEnabled(true);
                }
            }
        });
        et_yzm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((!cb_agree.isChecked()) || TextUtils.isEmpty(et_phone_number.getText()) || TextUtils.isEmpty(et_yzm.getText()) ||
                        TextUtils.isEmpty(et_confirm_pwd.getText()) || TextUtils.isEmpty(et_new_pwd.getText())) {
                    btn_register.setEnabled(false);
                } else {
                    btn_register.setEnabled(true);
                }
                if (TextUtils.isEmpty(et_phone_number.getText()) && TextUtils.isEmpty(et_yzm.getText()) &&
                        TextUtils.isEmpty(et_confirm_pwd.getText()) && TextUtils.isEmpty(et_new_pwd.getText())) {
                    btn_reset.setEnabled(false);
                } else {
                    btn_reset.setEnabled(true);
                }
            }
        });
        et_new_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((!cb_agree.isChecked()) || TextUtils.isEmpty(et_phone_number.getText()) || TextUtils.isEmpty(et_yzm.getText()) ||
                        TextUtils.isEmpty(et_confirm_pwd.getText()) || TextUtils.isEmpty(et_new_pwd.getText())) {
                    btn_register.setEnabled(false);
                } else {
                    btn_register.setEnabled(true);
                }
                if (TextUtils.isEmpty(et_phone_number.getText()) && TextUtils.isEmpty(et_yzm.getText()) &&
                        TextUtils.isEmpty(et_confirm_pwd.getText()) && TextUtils.isEmpty(et_new_pwd.getText())) {
                    btn_reset.setEnabled(false);
                } else {
                    btn_reset.setEnabled(true);
                }
            }
        });

    }

    @Override
    public void initData() {
        et_phone_number.setText(PhoneUtils.getPhoneNumber(context));
        cb_agree.setChecked(true);
        CountDownTimer countDownTimer = new CountDownTimer(1000,1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_xieyi:
                Intent intent = new Intent(context, BaseHtmlActivity.class);
                intent.putExtra("title", "会员注册协议");
                intent.putExtra("url", ConstantValues.USER_XIEYI_HTML_URL);
                startActivity(intent);
                break;
            case R.id.btn_reset:
                et_phone_number.setText("");
                et_yzm.setText("");
                et_new_pwd.setText("");
                et_confirm_pwd.setText("");
                break;
            case R.id.tv_get_yzm:
                if (RegexUtils.isMobileExact(et_phone_number.getText().toString()
                        .trim())) {
                    getYZM();
                    // ToastUtils.showShortToast(context, "验证码已发送！");

                } else {
                    ToastUtils.showLongToast("请输入正确的手机号码！");
                }
                break;
            case R.id.btn_register:
                String newPwd = et_new_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(newPwd)) {
                    ToastUtil.showTextToast(context, "密码不能为空！");
                    // et_new_pwd.setText("");
                    et_new_pwd.setFocusable(true);
                    return;
                }
                String qrPwd = et_confirm_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(qrPwd)) {
                    ToastUtil.showTextToast(context, "请确认密码！");
                    // et_confirm_pwd.setText("");
                    et_confirm_pwd.setFocusable(true);
                    return;
                }
                if (newPwd.equals(qrPwd)) {
                    register(newPwd);
                } else {
                    ToastUtil.showTextToast(context, "两次输入的密码不一致！");
                    // et_new_pwd.setText("");
                    // et_confirm_pwd.setText("");
                    et_new_pwd.setFocusable(true);
                    return;
                }
                // finish();
                // startActivity(new Intent(context, LoginActivity.class));
                break;

            default:
                break;
        }
    }

    class MyTask extends TimerTask {
        @Override
        public void run() {

            runOnUiThread(new Runnable() { // 在主线程中执行
                @Override
                public void run() {
                    recTimer--;
                    tv_get_yzm.setText("验证码已发送（" + recTimer + "S）");
                    tv_get_yzm.setClickable(false);
                    if (recTimer < 0) {
                        timer.cancel();
                        task.cancel();
                        tv_get_yzm.setText("重新发送");
                        tv_get_yzm.setClickable(true);
                    }
                }
            });
        }
    }

    ;

    // 注册
    private void register(final String newPwd) {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "数据加载中...");
        Map<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("mobile", et_phone_number.getText().toString()
                .trim());
        paramValues.put("code", et_yzm.getText().toString().trim());
        paramValues.put("password", newPwd);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                BaseResultBean pwdResult = null;
                Gson gson = new Gson();
                try {
                    pwdResult = gson.fromJson(arg0,
                            BaseResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (pwdResult == null) {
                    ToastUtil.showTextToast(context, "注册失败！");
                    return;
                }
                if (pwdResult.success) {
                    SweetAlertDialog dialog = new SweetAlertDialog(context,
                            SweetAlertDialog.SUCCESS_TYPE);
                    dialog.setTitleText("注册成功！")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    finish();
                                    String userName = et_phone_number.getText()
                                            .toString().trim();
                                    String pwd = et_new_pwd.getText().toString()
                                            .trim();
                                    mApplication.finishActivity("LoginActivity");
                                    mApplication.login(userName, pwd);
                                    Intent intent = new Intent(context,
                                            MainActivity.class);
                                    intent.putExtra("bindCard", true);
                                    startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    ToastUtil.showTextToast(context, pwdResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "注册失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.REGISTER_URL, paramValues, callback);
    }

    // 获取验证码
    private void getYZM() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
        Map<String, String> paramValues = new HashMap<String, String>();
        paramValues.put("mobile", et_phone_number.getText().toString()
                .trim());
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                BaseResultBean pwdResult = null;
                Gson gson = new Gson();
                try {
                    pwdResult = gson.fromJson(arg0,
                            BaseResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (pwdResult == null) {
                    ToastUtil.showTextToast(context, "发送短信失败!");
                    return;
                }
                if (pwdResult.success) {
                    timer = new Timer();
                    task = new MyTask();
                    recTimer = 60;
                    timer.schedule(task, 1000, 1000);
                    new SweetAlertDialog(context,
                            SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("短信已发送！")
                            .setContentText("请在10分钟内完成操作!").show();
                } else {
                    ToastUtil.showTextToast(context, pwdResult.msg);
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "发送短信失败!");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_REGISTER_YZM_URL, paramValues, callback);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }
//		getContentResolver().unregisterContentObserver(smsObserver);
    }
}
