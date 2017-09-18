package com.ksource.hbStore.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 修改密码页面
 */
public class EditPwdActivity extends BaseActivity {


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

    @Override
    public int getLayoutResId() {
        return R.layout.activity_edit_pwd;
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
    }

    @Override
    public void initListener() {
        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        iv_back.setOnClickListener(this);

    }

    @Override
    public void initData() {
        tv_title.setText("修改密码");
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
                if (TextUtils.isEmpty(oldPwd)) {
                    ToastUtil.showTextToast(context, "请输入旧密码！");
                    et_old_pwd.setText("");
                    et_old_pwd.setFocusable(true);
                    return;
                }
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
                upLoadNewPwd(ConstantValues.EDIT_PWD_URL, oldPwd, newPwd);
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
                "修改密码...");
        Map<String, String> paramValues = new HashMap<>();
        String token = spUtils.getString(ConstantValues.TOKEN, "");
        paramValues.put("token", token);
        paramValues.put("oldPassWord", oldPwd);
        paramValues.put("newPassWord", newPwd);
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
                    ToastUtil.showTextToast(context, "修改登陆密码失败！");
                    return;
                }
                if (pwdResult.success && pwdResult.flag == 0) {
                    SweetAlertDialog dialog = new SweetAlertDialog(context,
                            SweetAlertDialog.SUCCESS_TYPE);
                    dialog.setTitleText("修改登录密码成功！").setContentText("请重新登录！")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(
                                        SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    mApplication.finishAllActivity();
                                    startActivity(new Intent(context,
                                            LoginActivity.class));
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
