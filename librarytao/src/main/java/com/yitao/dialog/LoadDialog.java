package com.yitao.dialog;


import com.yitao.library_tao.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

/**
 * load dialog
 * @author Liang
 *
 */
public class LoadDialog extends Dialog
{
	Context mContext;
	private TextView loadTv;

	public LoadDialog(Context context)
  {
	  super(context);
	  this.mContext = context;
  }
	public LoadDialog(Context context, int theme)
  {
	  super(context, theme);
	  this.mContext = context;
  }

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.dialog_load);
	  // 使dialog全局
	  getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	  initViews();
	}
	//控件初始化
	private void initViews(){
		loadTv = (TextView) findViewById(R.id.widget_dialog_loading_tv);
	}
	
	/**
	 * 设置dialog text
	 * @param text
	 */
	public void setLoadText(String text){
		loadTv.setText(text);
	}
	
}
