package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksource.hbpostal.R;
import com.yitao.util.ToastUtil;

/**
 * 缴费历史页面
 */
public class HistoryActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private TextView tv_water, tv_ele,tv_gas, tv_tv,tv_broad, tv_tle,tv_wei;
	private Intent intent;
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_right:
			finish();
			mApplication.finishActivity("SHJFActivity");
			break;
		case R.id.tv_water:
			ToastUtil.showTextToast(context, "暂不支持该历史查询！");
//			intent.putExtra("type", 1);
//			startActivity(intent);
			break;
		case R.id.tv_ele:
//			ToastUtil.showTextToast(context, "暂无电费缴费历史！");
			intent.putExtra("type", 2);
			startActivity(intent);
			break;
		case R.id.tv_gas:
//			ToastUtil.showTextToast(context, "暂无燃气费缴费历史！");
			intent.putExtra("type", 3);
			startActivity(intent);
			break;
		case R.id.tv_tv:
//			ToastUtil.showTextToast(context, "暂无有线电视缴费历史！");
			intent.putExtra("type", 4);
			startActivity(intent);
			break;
		case R.id.tv_broad:
//			ToastUtil.showTextToast(context, "暂无固话宽带缴费历史！");
//			intent.putExtra("type", 5);
//			startActivity(intent);
			Intent intentBroad = new Intent(context,HistoryListActivity.class);
			intentBroad.putExtra("type", 6);
			startActivity(intentBroad);
			break;
		case R.id.tv_tle:
//			ToastUtil.showTextToast(context, "暂无手机充值缴费历史！");
			
			Intent intentPhone = new Intent(context,HistoryListActivity.class);
			intentPhone.putExtra("type", 10);
			startActivity(intentPhone);
			break;
		case R.id.tv_wei:
			ToastUtil.showTextToast(context, "暂无违章查询缴费历史！");
//			Intent intentWZ = new Intent(context, TrafficRemindActivity.class);
//			startActivity(intentWZ);
			break;

		default:
			break;
		}
	}

	@Override
	public int getLayoutResId() {
		return R.layout.activity_history;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
//		iv_right = (ImageView) findViewById(R.id.iv_right);
//		iv_right.setImageResource(R.drawable.life_icon_home);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		
		tv_water = (TextView) findViewById(R.id.tv_water);
		tv_ele = (TextView) findViewById(R.id.tv_ele);
		tv_gas = (TextView) findViewById(R.id.tv_gas);
		tv_tv = (TextView) findViewById(R.id.tv_tv);
		tv_broad = (TextView) findViewById(R.id.tv_broad);
		tv_tle = (TextView) findViewById(R.id.tv_tle);
		tv_wei = (TextView) findViewById(R.id.tv_wei);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
//		iv_right.setOnClickListener(this);
		tv_water.setOnClickListener(this);
		tv_ele.setOnClickListener(this);
		tv_gas.setOnClickListener(this);
		tv_tv.setOnClickListener(this);
		tv_broad.setOnClickListener(this);
		tv_tle.setOnClickListener(this);
		tv_wei.setOnClickListener(this);

	}

	@Override
	public void initData() {
		intent = new Intent(context, HistoryListActivity.class);
		tv_title.setText("缴费历史");
//		iv_right.setVisibility(View.VISIBLE);

	}

}
