package com.yitao.dialog;

import com.yitao.library_tao.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class UpOrDownLoadDialog extends Dialog {
	Context mContext;
	private TextView loadTv;
	private String tishi;

	public UpOrDownLoadDialog(Context context, String tishi) {
		super(context);
		this.tishi = tishi;
		this.mContext = context;
	}

	public UpOrDownLoadDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_upordown_load);
		// 使dialog全局
		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		initViews();
	}

	// 控件初始化
	private void initViews() {
		loadTv = (TextView) findViewById(R.id.pop_fileupload_text);
		loadTv.setText(tishi);
	}

	public void setLoadText(String text) {
		loadTv.setText(text);
	}
}
