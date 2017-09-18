package com.yitao.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yitao.library_tao.R;
/**
 * 选项弹出框
 * @author Administrator
 *
 */
public class ProgressDialog extends Dialog {
	private Context mContext;
	private ListView lv;
	private TextView title;
	android.widget.AdapterView.OnItemClickListener mOnItemClick;
	public ProgressDialog(Context context) {
		super(context);
		this.mContext = context;
	}

	public ProgressDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_item_select);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		initView();
	}

	private void initView() {
		lv = (ListView) findViewById(R.id.dialog_lv);
		title = (TextView) findViewById(R.id.dialog_title);
	}
	/**
	 * listView item 点击事件
	 */
	public void setListOnItemClickListener(android.widget.AdapterView.OnItemClickListener l){
		this.mOnItemClick = l;
		lv.setOnItemClickListener(mOnItemClick);
	}
	/**
	 * 设置标题
	 * @param title_name
	 */
	public void setTitle(String title_name){
		title.setText(title_name);
	}
	/**
	 * 设置dialog数据并更新
	 * @param adapter
	 */
	public void setListAdapterData(BaseAdapter adapter){
		lv.setAdapter(adapter);
	}
	
}
