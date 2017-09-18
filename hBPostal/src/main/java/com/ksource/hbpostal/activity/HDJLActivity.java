package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.MsgNumResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 互动交流页面
 */
public class HDJLActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back, iv_right;
	private RelativeLayout rl_active_msg, rl_notice, rl_faq, rl_consulting,
			rl_complain, rl_push_msg;
	private ImageView iv_consulting_msg, iv_complain_msg, iv_push_msg;
	private TextView tv_zx_num, tv_ts_num, tv_push_num;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_hdjl;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("互动交流");
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		iv_push_msg = (ImageView) findViewById(R.id.iv_push_msg);
		iv_consulting_msg = (ImageView) findViewById(R.id.iv_consulting_msg);
		iv_complain_msg = (ImageView) findViewById(R.id.iv_complain_msg);
		// iv_right.setVisibility(View.VISIBLE);
		rl_active_msg = (RelativeLayout) findViewById(R.id.rl_active_msg);
		rl_notice = (RelativeLayout) findViewById(R.id.rl_notice);
		rl_faq = (RelativeLayout) findViewById(R.id.rl_faq);
		rl_consulting = (RelativeLayout) findViewById(R.id.rl_consulting);
		rl_complain = (RelativeLayout) findViewById(R.id.rl_complain);
		rl_push_msg = (RelativeLayout) findViewById(R.id.rl_push_msg);
		tv_zx_num = (TextView) findViewById(R.id.tv_zx_num);
		tv_ts_num = (TextView) findViewById(R.id.tv_ts_num);
		tv_push_num = (TextView) findViewById(R.id.tv_push_num);
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		iv_right.setOnClickListener(this);
		rl_active_msg.setOnClickListener(this);
		rl_notice.setOnClickListener(this);
		rl_faq.setOnClickListener(this);
		rl_consulting.setOnClickListener(this);
		rl_complain.setOnClickListener(this);
		rl_push_msg.setOnClickListener(this);
	}

	@Override
	public void initData() {

	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	// 获取消息数
	private void getMsg() {
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		StringCallback callback = new StringCallback() {

			@Override
			public void onResponse(String arg0, int arg1) {

				MsgNumResultBean msgNUmBean = null;
				Gson gson = new Gson();
				try {
					// BaseBean baseBean =
					// gson.fromJson(jpResault,BaseBean.class);
					// jpResault = baseBean.params;
					// jpResault = AESOperator.getInstance().decrypt(jpResault);
					msgNUmBean = gson.fromJson(arg0, MsgNumResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (msgNUmBean == null) {
					iv_push_msg.setVisibility(View.GONE);
					iv_consulting_msg.setVisibility(View.GONE);
					iv_complain_msg.setVisibility(View.GONE);
					return;
				}
				if (msgNUmBean.success) {
					if (msgNUmBean.messageNum == 0) {
						iv_push_msg.setVisibility(View.GONE);
					} else {
						iv_push_msg.setVisibility(View.VISIBLE);
						tv_push_num.setText(msgNUmBean.messageNum + "条未读消息");
					}
					if (msgNUmBean.tsNum == 0) {
						iv_complain_msg.setVisibility(View.GONE);
					} else {
						iv_complain_msg.setVisibility(View.VISIBLE);
						tv_ts_num.setText(msgNUmBean.tsNum + "条回复消息");
					}
					if (msgNUmBean.zxNum == 0) {
						iv_consulting_msg.setVisibility(View.GONE);
					} else {
						iv_consulting_msg.setVisibility(View.VISIBLE);
						tv_zx_num.setText(msgNUmBean.zxNum + "条回复消息");
					}
				} else {
					iv_push_msg.setVisibility(View.GONE);
					iv_consulting_msg.setVisibility(View.GONE);
					iv_complain_msg.setVisibility(View.GONE);
				}
			}

			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				iv_push_msg.setVisibility(View.GONE);
				iv_consulting_msg.setVisibility(View.GONE);
				iv_complain_msg.setVisibility(View.GONE);
//				ToastUtil.showTextToast(context, "获取数据失败!");
			}
		};
		DataUtil.doPostAESData(null,context,ConstantValues.GET_MSG_NUM_URL, params, callback);
	}

	private String token;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_right:
			ToastUtil.showTextToast(context, "回到首页");
			mApplication.finishAllActivity();
			startActivity(new Intent(context, MainActivity.class));
			break;
		case R.id.rl_active_msg:
			Intent intentActive = new Intent(context, ActiveMsgActivity.class);
			startActivity(intentActive);
			break;
		case R.id.rl_notice:
			Intent noticeActive = new Intent(context, NoticeActivity.class);
			startActivity(noticeActive);
			break;
		case R.id.rl_faq:
			Intent faqActive = new Intent(context, FAQActivity.class);
			startActivity(faqActive);
			break;
		case R.id.rl_consulting:
			token = sp.getString(ConstantValues.TOKEN, "");
			if (TextUtils.isEmpty(token)) {
				startActivity(new Intent(context, LoginActivity.class));
				return;
			}
			Intent consultingActive = new Intent(context,
					ConsultingActivity.class);
			startActivity(consultingActive);
			break;
		case R.id.rl_complain:
			token = sp.getString(ConstantValues.TOKEN, "");
			if (TextUtils.isEmpty(token)) {
				startActivity(new Intent(context, LoginActivity.class));
				return;
			}
			Intent complainActive = new Intent(context, ComplainActivity.class);
			startActivity(complainActive);
			break;
		case R.id.rl_push_msg:
			token = sp.getString(ConstantValues.TOKEN, "");
			if (TextUtils.isEmpty(token)) {
				startActivity(new Intent(context, LoginActivity.class));
				return;
			}
			Intent pushMsgActive = new Intent(context, PushMsgActivity.class);
			startActivity(pushMsgActive);
			break;

		default:
			break;
		}

	}

}
