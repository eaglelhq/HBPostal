package com.yitao.dialog;

import com.yitao.library_tao.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
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
public class TiShiOnlySureDialog extends Dialog {
	Context mContext;
	private TextView tvTitle;
	private TextView loadTv;
	private ImageView loadIv;
	private int resId;
	private String tishi;
	private String title;
	private Button mSure;
//	private View mLine;
	private OnClickTiShiOnlySureDialog mListener = null;
	
	public void setOnClickTiShiOnlySureDialog(OnClickTiShiOnlySureDialog p){
		this.mListener = p;
	}

	public TiShiOnlySureDialog(Context context, int resId,String title, String tishi) {
		super(context);
		this.resId = resId;
		this.title = title;
		this.tishi = tishi;
		this.mContext = context;
	}

	public TiShiOnlySureDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_config_tishi_only_sure);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		initViews();
	}

	// 控件初始化
	private void initViews() {
		tvTitle = (TextView) findViewById(R.id.dialog_tishi_title);
		tvTitle.setText(title);
		loadTv = (TextView) findViewById(R.id.dialog_tishi_content);
		loadTv.setText(tishi);
		loadIv = (ImageView) findViewById(R.id.dialog_tishi_img);
		mSure = (Button) findViewById(R.id.dialog_tishi_sure);
//		mCancel = (Button) findViewById(R.id.dialog_tishi_cancel);
//		mLine = findViewById(R.id.dialog_tishi_line);
		mSure.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mListener != null){
					mListener.setOnclick();
				}
			}
		});
		
//		mCancel.setOnClickListener(new android.view.View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				dismiss();
//			}
//		});
	}
	
	public void setLoadText(String titleText,String text){
		tvTitle.setText(titleText);
		loadTv.setText(text);
	}
	
	public void setLoadImage(int resId) {
		loadIv.setVisibility(View.VISIBLE);
		loadIv.setImageResource(resId);
	}
	
//	public void setCancelShow(boolean isNew){
//		if(!isNew){
//			mLine.setVisibility(View.VISIBLE);
//			mCancel.setVisibility(View.VISIBLE);
//		}else{
//			mLine.setVisibility(View.GONE);
//			mCancel.setVisibility(View.GONE);
//		}
//	}
	
	public interface OnClickTiShiOnlySureDialog{
		public void setOnclick();
	}
}
