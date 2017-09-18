package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.SPUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 银行卡签约-验证短信，第一次需设置支付密码，二次校驗
 */
public class CardSignActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private EditText et_yzm, et_pwd, et_confirm_pwd;
    private Button btn_bind;
    private String cardNum, userName, idNum, phone, yzm;
    private String pwd,confirm_pwd;

    private LoadDialog mLoadDialog;
    private boolean isFirst;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_card_sign;
    }

    @Override
    public void initView() {
        // 头部
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        // 内容
        et_yzm = (EditText) findViewById(R.id.et_yzm);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_confirm_pwd = (EditText) findViewById(R.id.et_confirm_pwd);
        btn_bind = (Button) findViewById(R.id.btn_bind);
        SPUtils spUtils = new SPUtils(ConstantValues.SP_NAME);
        isFirst = spUtils.getBoolean(ConstantValues.PAY_PWD);
//        isFirst = getIntent().getBooleanExtra("isFirst", false);
        cardNum = getIntent().getStringExtra("cardNum");
        userName = getIntent().getStringExtra("userName");
        idNum = getIntent().getStringExtra("idNum");
        phone = getIntent().getStringExtra("phone");
        if (!isFirst) {
            et_pwd.setHint("请设置支付密码");
            et_confirm_pwd.setVisibility(View.VISIBLE);
        } else {
            et_pwd.setHint("请输入支付密码");
            et_confirm_pwd.setVisibility(View.GONE);
        }
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_bind.setOnClickListener(this);
    }

    // 签约银行卡
    private void checkCard() {
        String token = sp.getString(ConstantValues.TOKEN,null);
        if (TextUtils.isEmpty(token)){
            startActivity(new Intent(context, LoginActivity.class));
            return;
        }
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "签约银行卡...");
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("bank_card", cardNum);
        params.put("mobile", phone);
        params.put("idCard", idNum);
        params.put("bank_card_name", userName);
        params.put("password", pwd);

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                Gson gson = new Gson();
                BaseResultBean baseResult = null;
                try {
                    baseResult = gson.fromJson(arg0,
                            BaseResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (baseResult == null) {
                    ToastUtil.showTextToast(context, "签约银行卡失败！");
//					new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//					.setTitleText("验证银行卡失败！").show();
                    return;
                }
                if (baseResult.success && baseResult.flag == 0) {
                    SweetAlertDialog dialog = new SweetAlertDialog(
                            context, SweetAlertDialog.SUCCESS_TYPE);
                    dialog.setTitleText("签约成功！")
                            .setContentText("可用于在邮支付中快捷支付！")
                            .setConfirmClickListener(
                                    new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(
                                                SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
                                            finish();
                                            mApplication.finishActivity("AddCardStep1Activity");
                                        }
                                    }).show();
                } else {
//					tv_hint.setText("银行卡不可用");
//					btn_bind.setEnabled(false);
                    SweetAlertDialog dialog = new SweetAlertDialog(
                            context, SweetAlertDialog.ERROR_TYPE);
                    dialog.setTitleText("签约银行卡失败!")
                            .setContentText(baseResult.msg)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    et_yzm.requestFocus();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.SIGN_BANKCARD_URL, params, callback);
    }

    @Override
    public void initData() {
        String title = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(title)) {
            tv_title.setText("银行卡绑定");
        } else {
            tv_title.setText(title);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_bind:
//			String token = sp.getString(ConstantValues.TOKEN, null);
                // 绑定银行卡
                yzm = et_yzm.getText().toString().trim();
                if (TextUtils.isEmpty(yzm)) {
                    ToastUtil.showTextToast(context, "请输入您收到的验证码！");
                    et_yzm.requestFocus();
                    return;
                }
                pwd = et_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showTextToast(context, "请输入您的密码！");
                    et_pwd.requestFocus();
                    return;
                }
                confirm_pwd = et_confirm_pwd.getText().toString().trim();
                if (isFirst && TextUtils.isEmpty(confirm_pwd)){
                    ToastUtil.showTextToast(context, "请再次输入您的密码！");
                    et_confirm_pwd.requestFocus();
                    return;
                }else if (!confirm_pwd.equals(pwd)){
                    ToastUtil.showTextToast(context, "两次输入的密码不一致！");
                    et_confirm_pwd.requestFocus();
                    return;
                }
                checkCard();
                break;

            default:
                break;
        }

    }

}
