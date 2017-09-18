package com.yitao.dialog;

import com.yitao.library_tao.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
  * 类说明： 提示dialog<br>
  * 类名：com.ksource.oa.dialog.TiShiDialog <br>
  * 作者： 易涛 <br>
  * 时间：2015-10-14 下午1:39:18 <br>
  * 修改者：<br>
  * 修改日期：<br>
  * 修改内容：<br>
  */
public class UpdateValueDialog extends Dialog {
	Context mContext;
	private EditText loadTv;
	private String title;
	private TextView titleView;
	private String value;
	private Button mSure;
	private Button mCancel;
	private OnClickUpdateValueDialog mListener = null;
	
	public void setOnClickUpdateValueDialog(OnClickUpdateValueDialog p){
		this.mListener = p;
	}

	public UpdateValueDialog(Context context, String title, String value) {
		super(context);
		this.title = title;
		this.value = value;
		this.mContext = context;
	}

	public UpdateValueDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_config_update);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		initViews();
	}

	// 控件初始化
	private void initViews() {
		loadTv = (EditText) findViewById(R.id.dialog_tishi_content);
		loadTv.setHint(value==null?"":value);
		titleView = (TextView) findViewById(R.id.dialog_tishi_title);
		titleView.setText(title==null?"":title);
		mSure = (Button) findViewById(R.id.dialog_tishi_sure);
		mCancel = (Button) findViewById(R.id.dialog_tishi_cancel);
		
		mSure.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mListener != null){
					mListener.setOnClick(loadTv.getText().toString());
				}
			}
		});
		
		mCancel.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	public void setLoadText(String title,String text){
		loadTv.setHint(text);
		titleView.setText(title);
	}
	
	public interface OnClickUpdateValueDialog{
		public void setOnClick(String value);
	}
}
