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
public class TiShiDialog extends Dialog {
	Context mContext;
	private TextView loadTv;
	private ImageView loadIv;
	private int resId;
	private String tishi;
	private Button mSure;
	private Button mCancel;
	private OnClickTiShiDialog mListener = null;

	public void setOnClickTiShiDialog(OnClickTiShiDialog p) {
		this.mListener = p;
	}

	public TiShiDialog(Context context, int resId, String tishi) {
		super(context);
		this.tishi = tishi;
		this.resId = resId;
		this.mContext = context;
	}
	public TiShiDialog(Context context, String tishi) {
		super(context);
		this.tishi = tishi;
		this.mContext = context;
	}

	public TiShiDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_config_tishi);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		initViews();
	}

	// 控件初始化
	private void initViews() {
		loadTv = (TextView) findViewById(R.id.dialog_tishi_content);
		loadTv.setText(tishi);
		loadIv = (ImageView) findViewById(R.id.dialog_tishi_img);
		mSure = (Button) findViewById(R.id.dialog_tishi_sure);
		mCancel = (Button) findViewById(R.id.dialog_tishi_cancel);

		mSure.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.setOnclick();
				}
			}
		});

		mCancel.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.setCancelClick();
				}
			}
		});
	}

	public void setLoadText(String text) {
		loadTv.setText(text);
	}
	public void setLoadImage(int resId) {
		loadIv.setVisibility(View.VISIBLE);
		loadIv.setImageResource(resId);
	}

	public interface OnClickTiShiDialog {
		public void setOnclick();

		public void setCancelClick();
	}
}
