package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksource.hbpostal.R;

/**
 * 交通违章页面
 */
public class JTWZActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private TextView tv_traffic_query,tv_traffic_remind;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_traffic_query:
//			ToastUtil.showTextToast(context, "违章查询");
			startActivity(new Intent(context,TrafficQueryActivity.class ));
			break;
		case R.id.tv_traffic_remind:
//			ToastUtil.showTextToast(context, "违章提醒");
			startActivity(new Intent(context,TrafficRemindActivity.class ));
			break;

		default:
			break;
		}
	}

	@Override
	public int getLayoutResId() {
		return R.layout.activity_jtwz;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		
		tv_traffic_query = (TextView) findViewById(R.id.tv_traffic_query);
		tv_traffic_remind = (TextView) findViewById(R.id.tv_traffic_remind);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		tv_traffic_query.setOnClickListener(this);
		tv_traffic_remind.setOnClickListener(this);

	}

	@Override
	public void initData() {
		tv_title.setText("交通违章");
	}

}
