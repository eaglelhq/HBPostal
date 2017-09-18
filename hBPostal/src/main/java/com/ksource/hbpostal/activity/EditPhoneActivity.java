package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.RegexUtils;
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
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 修改手机号
 */
public class EditPhoneActivity extends BaseActivity {

	private TextView tv_title, tv_get_yzm;
	private ImageView iv_back;
	private EditText et_phone_number, et_yzm;
	private Button btn_ok;

	// 倒计时
	private int recTimer;
	TimerTask task;
	Timer timer;
	private LoadDialog mLoadDialog;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_edit_phone;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);

		tv_get_yzm = (TextView) findViewById(R.id.tv_get_yzm);
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		et_yzm = (EditText) findViewById(R.id.et_yzm);
		btn_ok = (Button) findViewById(R.id.btn_ok);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		tv_get_yzm.setOnClickListener(this);
	}

	@Override
	public void initData() {
		tv_title.setText("更改手机号");
		String phone = getIntent().getStringExtra("phone");
		if (!TextUtils.isEmpty(phone)){
			et_phone_number.setText(phone);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_ok:
			if (!RegexUtils.isMobileExact(et_phone_number.getText().toString()
					.trim())) {
				new SweetAlertDialog(context).setTitleText("请输入正确的手机号码！").show();
				return;
			}
			if (TextUtils.isEmpty(et_yzm.getText().toString().trim())) {
				new SweetAlertDialog(context).setTitleText("请输入验证码").show();
				return;
			}
			
			checkAuthCode();
			break;
		case R.id.tv_get_yzm:
			if (RegexUtils.isMobileExact(et_phone_number.getText().toString()
					.trim())) {
				getYZM();
				
			} else {
				new SweetAlertDialog(context).setTitleText("请输入正确的手机号码！").show();
			}
			break;
		default:
			break;
		}

	}

	/*
	 * 检查验证码是否匹配
	 */
	private void checkAuthCode() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
		Map<String, String> paramValues = new HashMap<>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		paramValues.put("token", token);
		paramValues.put("mobile", et_phone_number.getText().toString()
				.trim());
		paramValues.put("code", et_yzm.getText().toString().trim());
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
					return;
				}
				if (pwdResult.success) {
					new SweetAlertDialog(context,
							SweetAlertDialog.SUCCESS_TYPE)
							.setTitleText("验证成功！")
							.setContentText("原手机号验证成功！\n去绑定新手机号")
							.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sweetAlertDialog) {
									
									Intent intent = new Intent(context,
											ConfirmPhoneActivity.class);
									intent.putExtra("phone", et_phone_number.getText()
											.toString().trim());
									intent.putExtra("code", et_yzm.getText().toString()
											.trim());
									startActivity(intent);
									finish();
								}
							})
							.show();
				} else {
//					showDialog(pwdResult.msg);
					ToastUtil.showTextToast(context, pwdResult.msg);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "发送短信失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.CHECK_PHONE_URL, paramValues, callback);
	}

	// 获取验证码
	private void getYZM() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
		Map<String, String> paramValues = new HashMap<>();
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
					new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("ERROR")
                    .setContentText("发送短信失败！")
                    .show();
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
							.setContentText("请在10分钟内完成验证!")
							.show();
				} else {
					new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("ERROR")
                    .setContentText(pwdResult.msg)
                    .show();
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("ERROR")
                .setContentText("发送短信失败！")
                .show();
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_YZM_URL, paramValues, callback);
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
