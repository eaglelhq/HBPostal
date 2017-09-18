package com.yitao.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yitao.library_tao.R;

/**
 * 底部弹出dialog
 */
public class CallOrSendMessageDialog extends Dialog {
	private android.view.View.OnClickListener mOnClick;
	private Context mContext;
	private LinearLayout tv_weixin, callBtn, cancel, messageBtn; // 删除按钮, 取消按钮
	private TextView tv_call, tv_message;

	public CallOrSendMessageDialog(Context context) {
		super(context);
		this.mContext = context;
	}

	public CallOrSendMessageDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_callphone_dialog);
		// 使dialog全屏显示
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		initViews();
	}

	private void initViews() {
		callBtn = (LinearLayout) findViewById(R.id.dialog_call);
		tv_call = (TextView) findViewById(R.id.tv_call);
		messageBtn = (LinearLayout) findViewById(R.id.dialog_message);
		cancel = (LinearLayout) findViewById(R.id.dialog_cancle);
		tv_message = (TextView) findViewById(R.id.tv_message);
	}

	/**
	 * 设置监听
	 * 监听
	 */
	public void setOnClickListener(android.view.View.OnClickListener l) {
		mOnClick = l;
		callBtn.setOnClickListener(mOnClick);
		cancel.setOnClickListener(mOnClick);
		messageBtn.setOnClickListener(mOnClick);
	}

	/**
	 * 设置item text
	 * 
	 * @param phoneNum
	 */
	public void setItemTextForCall(String phoneNum) {
		tv_call.setText("打电话(" + phoneNum + ")");
		tv_message.setText("发短信(" + phoneNum + ")");
	}

	/**
	 * 设置item text
	 * 
	 */
	public void setItemTextForPic() {
		tv_call.setText("拍照");
		tv_message.setText("相册");
	}
}
