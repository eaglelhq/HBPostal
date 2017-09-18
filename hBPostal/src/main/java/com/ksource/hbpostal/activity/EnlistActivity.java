package com.ksource.hbpostal.activity;

import android.app.AlertDialog;
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
import com.ksource.hbpostal.util.TimeUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 八戒农场-活动报名页面
 */
public class EnlistActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private EditText et_name,et_tel, et_content;
	private TextView tv_act_name,tv_deadline;
	private Button btn_submit;

	private String title;//姓名
	private String content;//备注
	private String tel;//电话
	private LoadDialog mLoadDialog;
	private String actId;
	private String token;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_enlist;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);

		tv_act_name = (TextView) findViewById(R.id.tv_act_name);
		tv_deadline = (TextView) findViewById(R.id.tv_deadline);
		et_name = (EditText) findViewById(R.id.et_name);
		et_tel = (EditText) findViewById(R.id.et_tel);
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
		token = sp.getString(ConstantValues.TOKEN, "");
		tv_title.setText("活动报名");
		String title = getIntent().getStringExtra("title");
		String deadline = getIntent().getStringExtra("deadline");
		actId = getIntent().getStringExtra("actId");

		tv_act_name.setText(title);
		tv_deadline.setText("截止时间:" + TimeUtil.formatTimeMon(deadline));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_submit:
			title = et_name.getText().toString().trim();
			tel = et_tel.getText().toString().trim();
			content = et_content.getText().toString().trim();
			if (TextUtils.isEmpty(title)) {
				ToastUtil.showTextToast(context, "请输入联系人！");
				et_name.requestFocus();
				return;
			}if (TextUtils.isEmpty(tel)) {
				ToastUtil.showTextToast(context, "请输入联系电话！");
				et_tel.requestFocus();
				return;
			}
//			if (TextUtils.isEmpty(content)) {
//				ToastUtil.showTextToast(context, "请输入内容");
//				et_content.requestFocus();
//				return;
//			}
			submit();
			break;

		default:
			break;
		}
	}

	// 提交信息
	private void submit() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "报名中...");
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("activity_id", actId);
		params.put("contacts", title);
		params.put("mobile", tel);
		params.put("note", content);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				BaseResultBean msgResult = null;
				Gson gson = new Gson();
				try {
					msgResult = gson
							.fromJson(arg0, BaseResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (msgResult == null) {
					ToastUtil.showTextToast(context, "报名失败！");
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
				ToastUtil.showTextToast(context, "报名失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.BJ_ACT_JOIN_LIST, params, callback);
	}

	private void showDialog(String content, final boolean success) {
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
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
