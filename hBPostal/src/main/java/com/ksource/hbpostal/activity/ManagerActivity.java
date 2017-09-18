package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.PhoneUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.ManagerReusltBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.CallOrSendMessageDialog;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.NetStateUtils;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 客户经理页面
 */
public class ManagerActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private TextView tv_third_name, tv_third_sex, tv_third_phone;
	private ImageView iv_third_icon, iv_qq;
	private RelativeLayout rl_error, rl_no_manager,rl_manager;
	private Button btn_setting;

	boolean isMan = true;
	private LoadDialog mLoadDialog;

	private String qqNum;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_manager;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		rl_error = (RelativeLayout) findViewById(R.id.rl_error);
		rl_no_manager = (RelativeLayout) findViewById(R.id.rl_no_manager);
		rl_manager = (RelativeLayout) findViewById(R.id.rl_manager);

		btn_setting = (Button) findViewById(R.id.btn_setting);
		iv_third_icon = (ImageView) findViewById(R.id.iv_third_icon);
		tv_third_name = (TextView) findViewById(R.id.tv_third_name);
		tv_third_sex = (TextView) findViewById(R.id.tv_third_sex);
		tv_third_phone = (TextView) findViewById(R.id.tv_third_phone);
		iv_qq = (ImageView) findViewById(R.id.iv_qq);

	}

	@Override
	public void initListener() {
		rl_error.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		iv_qq.setOnClickListener(this);
		btn_setting.setOnClickListener(this);
		tv_third_phone.setOnClickListener(this);
		// tv_second_phone.setOnClickListener(this);

	}

	@Override
	public void initData() {
		tv_title.setText("客户经理");

		String token = sp.getString(ConstantValues.TOKEN, null);
		if (TextUtils.isEmpty(token)) {
			// finish();
			startActivity(new Intent(context, LoginActivity.class));
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		getData();
	}

	// 获取客户经理信息
	private void getData() {

		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"数据加载中...");
		if (!NetStateUtils.isNetworkAvailable(context)) {
			ToastUtil.showTextToast(context, "当前网络不可用！");
			DialogUtil.getInstance().dialogDismiss(mLoadDialog);
			return;
		}
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				Gson gson = new Gson();
				ManagerReusltBean resultBean = null;
				try {
					resultBean = gson.fromJson(arg0,
							ManagerReusltBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (resultBean == null) {
					rl_error.setVisibility(View.VISIBLE);
					return;
				}
				if (resultBean.success) {
					rl_error.setVisibility(View.GONE);
					rl_manager.setVisibility(View.VISIBLE);
					// managerList = resultBean.customManList;
					if (resultBean.customManList != null
							&& resultBean.customManList.size() > 0
							&& !TextUtils
									.isEmpty(resultBean.customManList
											.get(0).ZSXM)) {
						rl_no_manager.setVisibility(View.GONE);
						tv_third_sex.setText(resultBean.customManList
								.get(0).SEX);
						if ("男".equals(resultBean.customManList.get(0).SEX)) {
							iv_third_icon
									.setImageResource(R.drawable.mangrer_icon_man);
						} else {
							iv_third_icon
									.setImageResource(R.drawable.mangrer_icon_woman);
						}
						tv_third_name.setText(resultBean.customManList
								.get(0).ZSXM);
						tv_third_phone.setText(resultBean.customManList
								.get(0).MOBILE);
						qqNum = resultBean.customManList.get(0).QQ;
					} else {
						rl_no_manager.setVisibility(View.VISIBLE);
					}
				} else {
					if (resultBean.flag == 10) {
						mApplication.login();
						// getData();
					}
					ToastUtil.showTextToast(context, resultBean.msg);
					rl_error.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				rl_error.setVisibility(View.VISIBLE);
//				ToastUtil.showTextToast(context, "获取数据失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_MANAGER_URL, params, callback);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_error:
			getData();
			break;
		case R.id.btn_setting:
			Intent intent = new Intent(context, EditInfoActivity.class);
			intent.putExtra("itemName", "选择客户经理");
			intent.putExtra("type", 3);
			intent.putExtra("hint", "请输入客户经理编号");
			intent.putExtra("tishi", "*客户经理编号，可匹配到对应的客户经理！\n*客户经理确定后不可更改！");
			startActivity(intent);
			break;
		case R.id.iv_qq:
			// final String qq = tv_qq_num.getText().toString().trim();
//			 final String qqNum = "514822418";
			if (TextUtils.isEmpty(qqNum)) {
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("该经理暂无QQ号！").show();
				return;
			}
			SweetAlertDialog dialog = new SweetAlertDialog(this,
					SweetAlertDialog.WARNING_TYPE);
			dialog.setTitleText("跳转提醒")
					.setContentText("确定要和" + qqNum + "交谈吗？")
					.setConfirmText("是的")
					.setCancelText("取消")
					.setConfirmClickListener(
							new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									sDialog.dismiss();
									final String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin="
											+ qqNum + "&version=1";
									startActivity(new Intent(
											Intent.ACTION_VIEW, Uri
													.parse(qqUrl)));
								}
							}).show();
			break;
		case R.id.tv_third_phone:
			String phone = tv_third_phone.getText().toString().trim();
			if (!TextUtils.isEmpty(phone)) {
				callorSms(phone);
			}
			break;

		default:
			break;
		}

	}

	private void callorSms(final String phoneNum) {
		DialogUtil dialog = DialogUtil.getInstance();

		final CallOrSendMessageDialog callDialog = dialog
				.showCallOrSendMessageDialog(context, phoneNum);
		callDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.dialog_call:
					// ToastUtil.showTextToast(context, "打電話");
					PhoneUtils.dial(phoneNum);
					break;
				case R.id.dialog_message:
					// ToastUtil.showTextToast(context, "发短信");
					PhoneUtils.sendSms(phoneNum, "");

					break;
				case R.id.dialog_cancle:
					// ToastUtil.showTextToast(context, "取消");
					callDialog.dismiss();
					break;

				default:
					break;
				}
			}
		});
	}
}
