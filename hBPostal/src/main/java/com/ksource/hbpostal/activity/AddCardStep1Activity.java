package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
 * 绑定银行卡第一步
 */
public class AddCardStep1Activity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private EditText et_card_number;
	private Button btn_bind;
	private String cardNum;

	private LoadDialog mLoadDialog;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_add_card_step1;
	}

	@Override
	public void initView() {
		// 头部
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		// 内容
		et_card_number = (EditText) findViewById(R.id.et_card_number);
		btn_bind = (Button) findViewById(R.id.btn_bind);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		btn_bind.setOnClickListener(this);
		et_card_number.addTextChangedListener(new TextWatcher() {


			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				btn_bind.setEnabled(true);
			}
		});
	}

	// 检查银行卡是否合法
	private void checkCard(final String cardNum) {
		 mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
		 "验证银行卡...");
		 Map<String, String> params = new HashMap<String, String>();
		 params.put("bankCardCode", cardNum);
		 String token = sp.getString(ConstantValues.TOKEN, "");
		 params.put("token", token);
		 
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
					ToastUtil.showTextToast(context, "验证银行卡失败！");
//					new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//					.setTitleText("验证银行卡失败！").show();
					return;
				}
				if (baseResult.success) {
//					tv_hint.setText("银行卡可用");
					btn_bind.setEnabled(true);
//					Intent intent = new Intent(context, AddCardStep2Activity.class);
					Intent intent = new Intent(context, AddCardActivity.class);
					intent.putExtra("cardNum",cardNum);
					startActivity(intent);
				} else {
//					tv_hint.setText("银行卡不可用");
					btn_bind.setEnabled(false);
					SweetAlertDialog dialog = new SweetAlertDialog(
							context, SweetAlertDialog.ERROR_TYPE);
					dialog.setTitleText("银行卡不可用!")
							.setContentText(baseResult.msg)
							.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sweetAlertDialog) {
									sweetAlertDialog.dismiss();
									et_card_number.requestFocus();
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
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.CHECK_BANKCARD_URL, params, callback);
	}

	@Override
	public void initData() {
		String title = getIntent().getStringExtra("title");
		if (TextUtils.isEmpty(title)) {
			tv_title.setText("银行卡绑定");
		}else{
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
			// 校验银行卡
			cardNum = et_card_number.getText().toString().trim();
			if (!TextUtils.isEmpty(cardNum)) {
				checkCard(cardNum);
			}else{
//				ToastUtil.showTextToast(context, "请输入您的邮政储蓄卡号！");
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("请输入您的邮政储蓄卡号！").show();
				et_card_number.requestFocus();
			}
			break;

		default:
			break;
		}

	}

}
