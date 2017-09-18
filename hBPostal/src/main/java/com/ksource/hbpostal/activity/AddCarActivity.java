package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksource.hbpostal.R;
import com.yitao.util.ToastUtil;

public class AddCarActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back,iv_right;
	private Button btn_query;
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_right:
			ToastUtil.showTextToast(context, "hehehe");
			break;
		case R.id.ll_add_car:
			ToastUtil.showTextToast(context, "添加车辆");
			startActivity(new Intent(context,AddCarActivity.class ));
			break;
		case R.id.btn_query:
			ToastUtil.showTextToast(context, "查询");
			startActivity(new Intent(context,QueryResultActivity.class ));
			break;

		default:
			break;
		}

	}

	@Override
	public int getLayoutResId() {
		return R.layout.activity_add_car;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		iv_right.setVisibility(View.VISIBLE);
		btn_query = (Button) findViewById(R.id.btn_query);
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		iv_right.setOnClickListener(this);
		btn_query.setOnClickListener(this);
	}

	@Override
	public void initData() {
		tv_title.setText("新增车辆信息");
		
	}

}
