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
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 添加银行卡页面 -第二步
 *
 */
public class AddCardActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private EditText et_name, et_id_number, et_phone, et_yzm,et_id_num;
	private TextView tv_get_yzm;
	private Button btn_bind;
	private String cardNum, userName, idNum, phone, yzm;//,confirmIdNum

	// 倒计时
	private int recTimer;
	TimerTask task;
	Timer timer;
	private LoadDialog mLoadDialog;
    private boolean isFirst;

    @Override
	public int getLayoutResId() {
		return R.layout.activity_add_card;
	}

	@Override
	public void initView() {
		// 头部
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		// 内容
//		et_card_number = (EditText) findViewById(R.id.et_card_number);
		et_name = (EditText) findViewById(R.id.et_name);
		et_id_number = (EditText) findViewById(R.id.et_id_number);
		et_id_num = (EditText) findViewById(R.id.et_id_num);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_yzm = (EditText) findViewById(R.id.et_yzm);
		btn_bind = (Button) findViewById(R.id.btn_bind);
		tv_get_yzm = (TextView) findViewById(R.id.tv_get_yzm);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		tv_get_yzm.setOnClickListener(this);
		btn_bind.setOnClickListener(this);
	}


	@Override
	public void initData() {
		tv_title.setText("银行卡绑定");
		cardNum = getIntent().getStringExtra("cardNum");
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
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_get_yzm:
//			if (TextUtils.isEmpty(et_card_number.getText().toString().trim())) {
//				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//				.setTitleText("请输入银行卡号！").show();
//				return;
//			}
			if (RegexUtils.isMobileExact(et_phone.getText().toString().trim())) {
				getYZM();
			} else {
				// ToastUtils.showShortToast(context, "请输入正确的手机号码！");
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("请输入正确的手机号码！").show();
			}
			break;
		case R.id.btn_bind:
			String token = sp.getString(ConstantValues.TOKEN, null);
			if (TextUtils.isEmpty(token)) {
				// finish();
				startActivity(new Intent(context, LoginActivity.class));
			}
//			cardNum = et_card_number.getText().toString().trim();
			userName = et_name.getText().toString().trim();
			idNum = et_id_number.getText().toString().trim();
//			confirmIdNum = et_id_num.getText().toString().trim();
			phone = et_phone.getText().toString().trim();
			yzm = et_yzm.getText().toString().trim();
//			if (TextUtils.isEmpty(cardNum)) {
//				// ToastUtil.showTextToast(context, "请输入您的邮政储蓄卡号");
//				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//						.setTitleText("请输入您的邮政储蓄卡号！").show();
//				et_card_number.requestFocus();
//				return;
//			}
			if (TextUtils.isEmpty(userName)) {
				// ToastUtil.showTextToast(context, "请输入您的开户人姓名");
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("请输入您的开户人姓名！")
						.setContentText("要与开户人身份证姓名一致！").show();
				et_name.requestFocus();
				return;
			}
			if (!RegexUtils.isIDCard15(idNum)&&!RegexUtils.isIDCard18(idNum)) {
				// ToastUtil.showTextToast(context, "请输入您的开户人身份证号码");
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("请输入正确的身份证号码！").show();
				et_id_number.requestFocus();
				return;
			}
//			if (!RegexUtils.isIDCard15(confirmIdNum)&&!RegexUtils.isIDCard18(confirmIdNum)) {
//				// ToastUtil.showTextToast(context, "请输入您的开户人身份证号码");
//				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//						.setTitleText("请输入正确的身份证号码！").show();
//				et_id_num.requestFocus();
//				return;
//			}
//			if (!idNum.equals(confirmIdNum)){
//				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//						.setTitleText("两次输入的身份证号码不一致")
//						.setContentText("请确认两次输入的身份证号码一致！").show();
//				et_id_num.requestFocus();
//				return;
//			}
			if (!RegexUtils.isMobileExact(phone)) {
				// ToastUtil.showTextToast(context, "请输入您的手机号码");
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("请输入正确的手机号码！").show();
				et_phone.requestFocus();
				return;
			}
			if (TextUtils.isEmpty(yzm)) {
				// ToastUtil.showTextToast(context, "请输入您的验证码");
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("请输入您的验证码！").show();
				et_yzm.requestFocus();
				return;
			}
			submit();
//			 ToastUtil.showTextToast(context, "绑定成功");
//			finish();
//			mApplication.finishActivity("AddCardStep1Activity");
			break;

		default:
			break;
		}

	}

	// 获取验证码 SuperscriptSpan
	private void getYZM() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
		Map<String, String> paramValues = new HashMap<String, String>();
		paramValues.put("mobile", et_phone.getText().toString().trim());
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				timer = new Timer();
				task = new MyTask();
				recTimer = 60;
				Gson gson = new Gson();
				BaseResultBean pwdResult = null;
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
				timer = new Timer();
				task = new MyTask();
				recTimer = 60;
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_YZM_URL, paramValues, callback);
	}

	// 提交到服务器
	private void submit() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"添加银行卡...");
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("idCard", idNum);
		params.put("bank_card", cardNum);
		params.put("mobile", phone);
		params.put("code", yzm);
		params.put("bank_card_name", userName);
		
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
					new SweetAlertDialog(context,
							SweetAlertDialog.ERROR_TYPE)
							.setTitleText("失败！")
							.setContentText("绑定银行卡失败！").show();
					return;
				}
				if (baseResult.success) {
					SweetAlertDialog dialog = new SweetAlertDialog(
							context, SweetAlertDialog.SUCCESS_TYPE);
					dialog.setTitleText("成功！")
							.setContentText("绑定银行卡成功！")
							.setConfirmClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												SweetAlertDialog sDialog) {
											sDialog.dismiss();
											finish();
											//TODO 跳转到签约
//											Intent intent = new Intent(context,CardSignActivity.class);
//											intent.putExtra("cardNum",cardNum);
//											intent.putExtra("idNum",idNum);
//											intent.putExtra("phone",phone);
//											intent.putExtra("userName",userName);
//											intent.putExtra("isFirst",isFirst);
//											startActivity(intent);
											mApplication.finishActivity("AddCardStep1Activity");
										}
									}).show();

				} else {
					if (baseResult.flag == 10) {
						mApplication.login();
						// submitData();
					}else{
						new SweetAlertDialog(context,
								SweetAlertDialog.ERROR_TYPE)
								.setTitleText("绑定银行卡失败！")
								.setContentText(baseResult.msg).show();
					}
				}

			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				new SweetAlertDialog(context,
						SweetAlertDialog.ERROR_TYPE)
						.setTitleText("绑定银行卡失败！")
						.show();
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.ADD_BANKCARD_URL, params, callback);
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
	}

}
