package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.RegexUtils;
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
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 更换手机号，第二步，确认手机号
 */
public class ConfirmPhoneActivity extends BaseActivity {

	protected static final int RESULT_CODE = 208;
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
		tv_title.setText("更换手机号");
		btn_ok.setText("确认");
		et_phone_number.setHint("请输入新手机号码");
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
				checkPhone();
			} else {
				new SweetAlertDialog(context).setTitleText("请输入正确的手机号码！").show();
			}
			break;
		default:
			break;
		}

	}

	/*
	 * 检查手机号唯一性
	 */
	private void checkPhone() {
		Map<String, String> paramValues = new HashMap<String, String>();
		paramValues.put("mobile", et_phone_number.getText().toString()
				.trim());
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				BaseResultBean result = null;
				Gson gson = new Gson();
				try {
					result = gson.fromJson(arg0,
							BaseResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (result == null) {
					ToastUtil.showTextToast(context, "发送短信失败");
					return;
				}
				if (result.success) {
					getYZM();
				} else {
					 ToastUtil.showTextToast(context, result.msg);
				}
			
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "发送短信失败");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.CHECK_PHONE_ONLY_URL, paramValues, callback);
	}

	/*
	 * 检查验证码是否匹配
	 */
	private void checkAuthCode() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
		Map<String, String> paramValues = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		paramValues.put("token", token);
		paramValues.put("mobile", et_phone_number.getText().toString()
				.trim());
		paramValues.put("code", et_yzm.getText().toString().trim());
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				BaseResultBean result = null;
				Gson gson = new Gson();
				try {
					result = gson.fromJson(arg0,
							BaseResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (result == null) {
					return;
				}
				if (result.success) {
					new SweetAlertDialog(context,
							SweetAlertDialog.SUCCESS_TYPE)
							.setTitleText("绑定成功！")
							.setContentText("更换手机号成功！\n请重新登录")
							.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sweetAlertDialog) {
									new SPUtils(ConstantValues.SP_NAME).clear();
									finish();
									Intent intent = new Intent(context,
											LoginActivity.class);
									intent.putExtra("phone", et_phone_number.getText().toString()
											.trim());
									startActivity(intent);
								}
							})
							.show();
				} else {
					  new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("验证失败！")
                        .setContentText(result.msg)
                        .show();
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "校验验证码失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.UPDATE_PHONE_URL, paramValues, callback);
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
				timer = new Timer();
				task = new MyTask();
				recTimer = 60;
				BaseResultBean result = null;
				Gson gson = new Gson();
				try {
					result = gson.fromJson(arg0,
							BaseResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (result == null) {
					ToastUtil.showTextToast(context, "获取验证码失败！");
					return;
				}
				if (result.success) {
					timer.schedule(task, 1000, 1000);
					new SweetAlertDialog(context,
							SweetAlertDialog.SUCCESS_TYPE)
							.setTitleText("短信已发送！")
							.setContentText("请在10分钟内完成验证!")
							.show();
				} else {
					new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("发送短信失败！")
                    .setContentText(result.msg)
                    .show();
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				timer = new Timer();
				task = new MyTask();
				recTimer = 60;
				ToastUtil.showTextToast(context, "获取验证码失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_YZM_URL, paramValues, callback);
	}
	
	class MyTask extends TimerTask {  
        @Override  
        public void run() {  
  
            runOnUiThread(new Runnable() {      // 在主线程中执行  
                @Override  
                public void run() {  
                    recTimer--;  
                    tv_get_yzm.setText("验证码已发送（"+recTimer+"S）");  
                    tv_get_yzm.setClickable(false);
                    if(recTimer < 0){  
                        timer.cancel(); 
                        task.cancel();
                        tv_get_yzm.setText("重新发送");
                        tv_get_yzm.setClickable(true);
                    }  
                }  
            });  
        }  
    };  

    
}
