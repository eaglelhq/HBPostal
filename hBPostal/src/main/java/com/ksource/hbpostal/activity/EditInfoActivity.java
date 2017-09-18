package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.SimpleManagerReusltBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.receiver.PushDemoReceiver;
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
 * 修改个人信息页面
 * 	昵称，客户经理
 */
public class EditInfoActivity extends BaseActivity implements PushDemoReceiver.BRInteraction {

	private static final int RESULT_CODE = 201;

	private TextView tv_title, tv_tishi;
	private EditText et_name;
	private ImageView iv_back;
	private Button btn_submit;

	private String itemName;
	private String content;
	private String tishi;
	private String hint;
	private String managerName;
	private String managerId;
	private int type;

	private LoadDialog mLoadDialog;
	private String text;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_edit_info;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_tishi = (TextView) findViewById(R.id.tv_tishi);
		et_name = (EditText) findViewById(R.id.et_name);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		btn_submit = (Button) findViewById(R.id.btn_submit);
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
	}

	@Override
	public void initData() {
		itemName = getIntent().getStringExtra("itemName");
		tishi = getIntent().getStringExtra("tishi");
		text = getIntent().getStringExtra("text");
		hint = getIntent().getStringExtra("hint");
		type = getIntent().getIntExtra("type", 0);
		if (hint != null) {
			et_name.setHint(hint);
		}
		if (TextUtils.isEmpty(text)){
			et_name.setHint(hint);
		} else{
			et_name.setText(text);
		}
		tv_title.setText(itemName);
		tv_tishi.setText(tishi);
		// if (type == ) {
		//
		// } else {
		//
		// }
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
//		PushDemoReceiver dianLiangBR = new PushDemoReceiver();
//		registerReceiver(dianLiangBR, intentFilter);
//		dianLiangBR.setBRInteractionListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_submit:
			content = et_name.getText().toString().trim();
			if (TextUtils.isEmpty(content)) {
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
						.setTitleText(hint).show();
				et_name.requestFocus();
				return;
			}
			if (type == 3) {
				getManager();
			} else if (type == 2) {
				String nickNameStr = et_name.getText().toString().trim();
				if (!TextUtils.isEmpty(nickNameStr)
						&& nickNameStr.length() > 10) {
					SweetAlertDialog dialog = new SweetAlertDialog(this,
							SweetAlertDialog.WARNING_TYPE);
					dialog.setTitleText("温馨提示")
							.setContentText("您输入的昵称太长了\n请修改一下吧！").show();
				} else {
					submitInfo();
					// Intent mIntent = new Intent();
					// mIntent.putExtra("content", content);
					// mIntent.putExtra("type", type);
					// setResult(RESULT_CODE, mIntent);
					// finish();
				}
			} else {
				submitInfo();
				// Intent mIntent = new Intent();
				// mIntent.putExtra("content", content);
				// mIntent.putExtra("type", type);
				// setResult(RESULT_CODE, mIntent);
				// finish();
			}
			break;

		default:
			break;
		}
	}

	// 获取客户经理
	private void getManager() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"数据加载中...");
		Map<String, String> params = new HashMap<String, String>();
		params.put("sign", content);
		StringCallback callback = new StringCallback() {

			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				Gson gson = new Gson();
				SimpleManagerReusltBean managerBean = null;
				try {
					managerBean = gson.fromJson(arg0,
							SimpleManagerReusltBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (managerBean == null) {
					// new SweetAlertDialog(context,
					// SweetAlertDialog.ERROR_TYPE)
					// .setTitleText("Sorry！")
					// .setContentText("未查询到对应的客户经理！").show();
					// et_name.requestFocus();
					ToastUtil.showTextToast(context, "未查询到对应的客户经理！");
					return;
				}
				if (managerBean.success) {
					managerId = managerBean.cusManInfo.ID;
					managerName = managerBean.cusManInfo.ZSXM;
					submitInfo();
				} else {
					et_name.requestFocus();
					ToastUtil.showTextToast(context, managerBean.msg);
				}
			}

			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "未查询到对应的客户经理！");
			}
		};

		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_MANAGER_BYSIGN_URL, params,
				callback);
	}

	// 修改个人信息
	private void submitInfo() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"提交个人信息...");
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		switch (type) {
		case 1:
			params.put("name", content);

			break;
		case 2:
			params.put("nickName", content);

			break;
		case 3:
			params.put("cusManId", managerId);
			params.put("cusManName", managerName);

			break;

		default:
			break;
		}

		StringCallback callback = new StringCallback() {
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				BaseResultBean baseResult = null;
				Gson gson = new Gson();
				try {
					baseResult = gson.fromJson(arg0, BaseResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (baseResult == null) {
					ToastUtil.showTextToast(context, "修改个人信息失败！");
					return;
				}
				if (baseResult.success) {
					SweetAlertDialog dialog = new SweetAlertDialog(
							context, SweetAlertDialog.SUCCESS_TYPE);
					dialog.setTitleText("修改成功！")
							.setConfirmClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												SweetAlertDialog sweetAlertDialog) {
											sweetAlertDialog.dismiss();
											if (type == 3) {
												Intent mIntent = new Intent();
												mIntent.putExtra("content",
														managerName);
												mIntent.putExtra("type", type);
												mIntent.putExtra("managerId",
														managerId);
												setResult(RESULT_CODE, mIntent);
											} else {
												Intent mIntent = new Intent();
												mIntent.putExtra("content",
														content);
												mIntent.putExtra("type", type);
												setResult(RESULT_CODE, mIntent);
											}
											finish();
										}
									}).show();
				} else if (baseResult.flag == 10) {
					mApplication.login();
					// submitData();
				} else {
					ToastUtil.showTextToast(context, baseResult.msg);
				}
			}

			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "修改个人信息失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.UPDATE_USERINFO_URL, params,
				callback);

	}

	@Override
	public void setText(String content) {
		et_name.setText(content);
	}
}
