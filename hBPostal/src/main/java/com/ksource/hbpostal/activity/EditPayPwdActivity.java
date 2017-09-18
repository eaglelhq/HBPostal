package com.ksource.hbpostal.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * 修改支付密码页面
 */
public class EditPayPwdActivity extends BaseActivity {


    private Button btn_cancel, btn_ok;
    private EditText et_old_pwd, et_new_pwd, et_qr_pwd;
    private TextView tv_title;
    private TextView tv_pwd;
    private LinearLayout ll_old_pwd;
    private ImageView iv_back;
    private View line;
    private String oldPwd;
    private String newPwd;
    private LoadDialog mLoadDialog;
    private boolean has_pay_pwd;
    private boolean newEnable;
    private boolean confirmEnable;
//	private String state;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_edit_pay_pwd;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_old_pwd = (EditText) findViewById(R.id.et_old_pwd);
        et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
        et_qr_pwd = (EditText) findViewById(R.id.et_qr_pwd);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        tv_pwd = (TextView) findViewById(R.id.tv_pwd);
        ll_old_pwd = (LinearLayout) findViewById(R.id.ll_old_pwd);
        line = findViewById(R.id.line);
        btn_ok.setEnabled(false);
    }

    @Override
    public void initListener() {
        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        et_qr_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                confirmEnable = editable != null && editable.length() == 6;
                btn_ok.setEnabled(confirmEnable && newEnable);
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
                newEnable = editable != null && editable.length() == 6;
                btn_ok.setEnabled(confirmEnable && newEnable);
            }
        });
    }

    @Override
    public void initData() {
        has_pay_pwd = getIntent().getBooleanExtra("has_pay_pwd", true);
//		state = getIntent().getStringExtra("state");
//		if (!"修改登录密码".equals(state)){
//		}
        tv_title.setText(has_pay_pwd ? "修改支付密码" : "设置支付密码");
        if (has_pay_pwd) {
            ll_old_pwd.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        } else {
            ll_old_pwd.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            tv_pwd.setText("设置密码");
            et_new_pwd.setHint("请输入密码");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                ToastUtil.showTextToast(context, "你已取消修改密码！");
                break;
            case R.id.btn_ok:
                oldPwd = et_old_pwd.getText().toString().trim();
                newPwd = et_new_pwd.getText().toString().trim();
                if (has_pay_pwd && TextUtils.isEmpty(oldPwd)) {
                    ToastUtil.showTextToast(context, "请输入旧密码！");
                    et_old_pwd.setText("");
                    et_old_pwd.setFocusable(true);
                    return;
                }
                // if (oldPwd.equals(curPwd)) {
                if (TextUtils.isEmpty(newPwd)) {
                    ToastUtil.showTextToast(context, "新密码不能为空！");
                    et_new_pwd.setText("");
                    et_new_pwd.setFocusable(true);
                    return;
                }
                String qrPwd = et_qr_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(qrPwd)) {
                    ToastUtil.showTextToast(context, "请确认新密码！");
                    et_qr_pwd.setText("");
                    et_qr_pwd.setFocusable(true);
                    return;
                }
                if (!newPwd.equals(qrPwd)) {
                    ToastUtil.showTextToast(context, "两次输入的密码不一致！");
                    et_new_pwd.setText("");
                    et_qr_pwd.setText("");
                    et_new_pwd.setFocusable(true);
                    return;
                }
                // 上传新密码
//			if("修改登录密码".equals(state)){
//				upLoadNewPwd(ConstantValues.EDIT_PWD_URL,oldPwd, newPwd);
//			}else{
                upLoadNewPwd(ConstantValues.CHANGE_PWD_URL, oldPwd, newPwd);
//			}
                // } else {
                // ToastUtil.showTextToast(context, "旧密码错误！");
                // et_old_pwd.setText("");
                // et_old_pwd.setFocusable(true);
                // return;
                // }
                break;
            case R.id.iv_back:
                finish();
                break;

            default:
                break;
        }
    }

    // 上传新密码
    private void upLoadNewPwd(String url, final String oldPwd, final String newPwd) {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "上传新密码...");
        Map<String, String> paramValues = new HashMap<String, String>();
        String token = sp.getString(ConstantValues.TOKEN, "");
        paramValues.put("token", token);
//		if("修改登录密码".equals(state)) {
//			paramValues.put("password", oldPwd);
//			paramValues.put("newPass", newPwd);
//		}else{
        paramValues.put("old_pwd", oldPwd);
        paramValues.put("new_pwd", newPwd);
//		}
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                BaseResultBean pwdResult = null;
                try {
                    pwdResult = gson.fromJson(arg0,
                            BaseResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (pwdResult == null) {
                    ToastUtil.showTextToast(context, "修改支付密码失败！");
                    return;
                }
                if (pwdResult.success && pwdResult.flag == 0) {
                    SweetAlertDialog dialog = new SweetAlertDialog(context,
                            SweetAlertDialog.SUCCESS_TYPE);
                    dialog.setTitleText("修改支付密码成功！").setContentText(pwdResult.msg)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(
                                        SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    finish();
                                }
                            }).show();
                } else {
                    ToastUtil.showTextToast(context, pwdResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "修改密码失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, url, paramValues, callback);
    }
}
