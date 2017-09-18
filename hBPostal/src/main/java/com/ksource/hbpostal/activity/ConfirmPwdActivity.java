package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
 * 更换密码，第二步，确认密码
 */
public class ConfirmPwdActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private EditText et_new_pwd, et_confirm_pwd;
	private Button btn_next;
	
	private String phone;
	private String code;
	private String newPwd;
	private String qrPwd;
	private LoadDialog mLoadDialog;
	private int type;
	private String resetUrl;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_next:
			newPwd = et_new_pwd.getText().toString().trim();
			if (TextUtils.isEmpty(newPwd)) {
				ToastUtil.showTextToast(context, "新密码不能为空！");
				et_new_pwd.setText("");
				et_new_pwd.setFocusable(true);
				return;
			}
			qrPwd = et_confirm_pwd.getText().toString().trim();
			if (TextUtils.isEmpty(qrPwd)) {
				ToastUtil.showTextToast(context, "请确认新密码！");
				et_confirm_pwd.setText("");
				et_confirm_pwd.setFocusable(true);
				return;
			}
			if (newPwd.equals(qrPwd)) {
				// 上传新密码
				upLoadNewPwd();
			} else {
				ToastUtil.showTextToast(context, "两次输入的密码不一致！");
				et_new_pwd.setText("");
				et_confirm_pwd.setText("");
				et_new_pwd.setFocusable(true);
				return;
			}
//			finish();
//			startActivity(new Intent(context, LoginActivity.class));
			break;

		default:
			break;
		}
	}

	@Override
	public int getLayoutResId() {
		return R.layout.activity_confirm_pwd;
	}

	@Override
	public void initView() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		type = getIntent().getIntExtra("type",1);
		if (type == 1){
			tv_title.setText("忘记密码");
			resetUrl = ConstantValues.RESET_PWD_URL;
		}else{
			tv_title.setText("重置支付密码");
			resetUrl = ConstantValues.RESET_PAY_PWD;
		}
		et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
		et_confirm_pwd = (EditText) findViewById(R.id.et_confirm_pwd);
		btn_next = (Button) findViewById(R.id.btn_next);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		btn_next.setOnClickListener(this);

	}

	@Override
	public void initData() {
		phone = getIntent().getStringExtra("phone");
		code = getIntent().getStringExtra("code");

	}

	// 上传新密码
	private void upLoadNewPwd() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
		Map<String, String> paramValues = new HashMap<>();
		if (type == 1){
			paramValues.put("password",newPwd);
		}else{
			paramValues.put("new_pwd",newPwd);
			String token = sp.getString(ConstantValues.TOKEN, null);
			paramValues.put("token", token);
		}
		paramValues.put("mobile", phone);
		paramValues.put("code", code);
		
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
				if (pwdResult.success && pwdResult.flag == 0) {
					SweetAlertDialog dialog = new SweetAlertDialog(context,
							SweetAlertDialog.SUCCESS_TYPE);
					if (type == 1) {
						dialog.setTitleText("重置密码成功！")
								.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sweetAlertDialog) {
										sweetAlertDialog.dismiss();
										finish();
										mApplication.finishActivity("MissPwdActivity");
										mApplication.finishActivity("LoginActivity");
										mApplication.login(phone, newPwd);
										startActivity(new Intent(context,
												MainActivity.class));
									}
								})
								.show();
					}else{
						dialog.setTitleText("重置支付密码成功！").setContentText(pwdResult.msg)
								.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(
											SweetAlertDialog sweetAlertDialog) {
										sweetAlertDialog.dismiss();
										mApplication.finishActivity("MissPwdActivity");
										finish();
									}
								}).show();
					}
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
		DataUtil.doPostAESData(mLoadDialog,context,resetUrl, paramValues, callback);
	}

//	private void showDialog(String content) {
//		AlertDialog.Builder adb = new Builder(context);
//		// adb.setTitle("提示信息");
//		adb.setMessage(content);
//		adb.setNegativeButton("确定", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//
//				// finish();
//			}
//		});
//		adb.show();
//	}

	
}
