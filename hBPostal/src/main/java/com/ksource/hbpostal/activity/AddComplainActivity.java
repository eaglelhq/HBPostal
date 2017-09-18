package com.ksource.hbpostal.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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

import okhttp3.Call;

public class AddComplainActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private EditText et_title, et_content;
	private Button btn_submit;

	private String title;
	private String content;
	private LoadDialog mLoadDialog;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_add_complain;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);

		et_title = (EditText) findViewById(R.id.et_title);
		et_content = (EditText) findViewById(R.id.et_content);
		btn_submit = (Button) findViewById(R.id.btn_submit);

	}

	@Override
	public void initListener() {
		btn_submit.setOnClickListener(this);
		iv_back.setOnClickListener(this);
	}

	@Override
	public void initData() {
		tv_title.setText("客户投诉");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_submit:
			title = et_title.getText().toString().trim();
			content = et_content.getText().toString().trim();
			if (TextUtils.isEmpty(title)) {
				ToastUtil.showTextToast(context, "请输入标题");
				et_title.requestFocus();
				return;
			}
			if (TextUtils.isEmpty(content)) {
				ToastUtil.showTextToast(context, "请输入内容");
				et_content.requestFocus();
				return;
			}
			submit();
			break;

		default:
			break;
		}
	}

	// 提交信息
	private void submit() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "提交...");
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("title", title);
		params.put("content", content);
		
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				Gson gson = new Gson();
				BaseResultBean msgResult = null;
				try {
					msgResult = gson
							.fromJson(arg0, BaseResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (msgResult == null) {
					ToastUtil.showTextToast(context, "提交信息失败！");
					return;
				}
				if (msgResult.success) {
					showDialog(msgResult.msg,true);
				} else {
					showDialog(msgResult.msg,false);
				}

			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "提交信息失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.ADDTS_URL, params, callback);
	}

	private void showDialog(String content, final boolean success) {
		Builder adb = new Builder(context);
		// adb.setTitle("提示信息");
		adb.setMessage(content);
		adb.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (success) {
					finish();
				}
			}
		});
		adb.show();
	}

}
