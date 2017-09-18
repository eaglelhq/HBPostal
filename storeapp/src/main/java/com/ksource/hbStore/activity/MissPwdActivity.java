package com.ksource.hbStore.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.RegexUtils;
import com.google.gson.Gson;
import com.ksource.hbStore.R;
import com.ksource.hbStore.bean.BaseResultBean;
import com.ksource.hbStore.config.ConstantValues;
import com.ksource.hbStore.util.DataUtil;
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
 * 忘记密码页面
 */
public class MissPwdActivity extends BaseActivity {

    private TextView tv_title, tv_get_yzm;
    private ImageView iv_back;
    private EditText et_phone_number, et_yzm;
    private Button btn_next;

    // 倒计时
    private int recTimer;
    TimerTask task;
    Timer timer;
    private LoadDialog mLoadDialog;
    private String codeUrl;
    private int type;
    //GET_RESET_PWD_CODE

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_get_yzm:
                if (RegexUtils.isMobileExact(et_phone_number.getText().toString()
                        .trim())) {
                    getYZM();
                    // ToastUtils.showShortToast(context, "验证码已发送！");
                } else {
                    new SweetAlertDialog(context,
                            SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("请输入正确的手机号码!")
                            .show();
                    // ToastUtils.showShortToast(context, "请输入正确的手机号码！");
                }
                break;
            case R.id.btn_next:
                if (TextUtils.isEmpty(et_phone_number.getText().toString().trim())
                        || TextUtils.isEmpty(et_yzm.getText().toString().trim())) {
                    ToastUtil.showTextToast(context, "请输入手机号或验证码");
                    return;
                }
                Intent intent = new Intent(context,
                        ConfirmPwdActivity.class);
                intent.putExtra("phone", et_phone_number.getText().toString().trim());
                intent.putExtra("code", et_yzm.getText().toString().trim());
                startActivity(intent);

                break;

            default:
                break;
        }
    }


    @Override
    public int getLayoutResId() {
        return R.layout.activity_miss_pwd;
    }

    @Override
    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_get_yzm = (TextView) findViewById(R.id.tv_get_yzm);
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        et_yzm = (EditText) findViewById(R.id.et_yzm);
        btn_next = (Button) findViewById(R.id.btn_next);

    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        tv_get_yzm.setOnClickListener(this);

    }

    @Override
    public void initData() {
//		et_phone_number.setText(PhoneUtils.getPhoneNumber(context));
        tv_title.setText("忘记密码");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }
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
                    new SweetAlertDialog(context,
                            SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("发送短信失败!")
                            .setContentText("请稍候重试!").show();
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
                    new SweetAlertDialog(context,
                            SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("发送短信失败!")
                            .setContentText(pwdResult.msg).show();
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                new SweetAlertDialog(context,
                        SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("发送短信失败!")
                        .setContentText("请稍候重试!").show();
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_YZM_URL, paramValues, callback);
    }

}
