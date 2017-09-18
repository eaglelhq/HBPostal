package com.ksource.hbpostal.activity;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.RemindCarBean;
import com.yitao.util.ToastUtil;

/**
 * 车辆信息
 * @author Administrator
 *
 */
public class TrafficRemindActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back,iv_right;
	private Button btn_query;
	private TextView tv_user_name,tv_id_card,tv_indate;
	private ListView lv_car_msg;
	private RelativeLayout rl_no_bind,rl_no_car;
	private TextView tv_bind_card;
	private BaseAdapter adapter;
	private List<RemindCarBean> datas;
	private boolean isOpen = false;
	private String userId = "";
	
	
	

	@Override
	public int getLayoutResId() {
		return R.layout.activity_wzremind;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("车辆信息");
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		iv_right.setVisibility(View.VISIBLE);
		btn_query = (Button) findViewById(R.id.btn_query);
		tv_user_name = (TextView) findViewById(R.id.tv_user_name);
		tv_id_card = (TextView) findViewById(R.id.tv_id_card);
		tv_indate = (TextView) findViewById(R.id.tv_indate);
		lv_car_msg = (ListView) findViewById(R.id.lv_car_msg);
		tv_bind_card = (TextView) findViewById(R.id.tv_bind_card);
		rl_no_bind = (RelativeLayout) findViewById(R.id.rl_no_bind);
		rl_no_car = (RelativeLayout) findViewById(R.id.rl_no_car);
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		iv_right.setOnClickListener(this);
		btn_query.setOnClickListener(this);
		tv_bind_card.setOnClickListener(this);
	}

	@Override
	public void initData() {
		tv_user_name.setText("*hehe");
		tv_id_card.setText("4***********5");
		tv_indate.setText("提醒到期时间：2017-10-18");
		datas = new ArrayList<RemindCarBean>();
		datas.add(new RemindCarBean("", "豫F45165", "2016-10-18 09:13:25"));
		datas.add(new RemindCarBean("", "豫F45165", "2016-10-18 09:13:25"));
		datas.add(new RemindCarBean("", "豫F45165", "2016-10-18 09:13:25"));
		adapter = new MyAdapter(datas);
		lv_car_msg.setAdapter(adapter);
		if (isOpen) {
			btn_query.setText("取消开通");
			
		} else {
			btn_query.setText("开通提醒");

		}
		if (userId == null) {
			rl_no_bind.setVisibility(View.VISIBLE);
		}else{
			rl_no_bind.setVisibility(View.GONE);
			
		}
		if (datas.size() == 0) {
			rl_no_car.setVisibility(View.VISIBLE);
		}else{
			rl_no_car.setVisibility(View.GONE);
			
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_right:
			finish();
			mApplication.finishActivity("JTWZActivity");
			break;
		case R.id.tv_bind_card:
			ToastUtil.showTextToast(context, "去绑定银行卡");
			break;
		case R.id.btn_query:
			isOpen = !isOpen;
			if (isOpen) {
				btn_query.setText("取消开通");
				ToastUtil.showTextToast(context, "开通");
			} else {
				btn_query.setText("开通提醒");
				ToastUtil.showTextToast(context, "取消");
			}
			//TODO 开通/取消
//			startActivity(new Intent(context,QueryResultActivity.class ));
			break;

		default:
			break;
		}

	}
	
	class MyAdapter extends DefaultBaseAdapter<RemindCarBean> {

		public MyAdapter(List<RemindCarBean> datas) {
			super(datas);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item_car,
						null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tv_number = (TextView) convertView
						.findViewById(R.id.tv_number);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 填充数据
			holder.iv_icon.setImageResource(R.drawable.traffic_icon_vehicle);
			holder.tv_number.setText(datas.get(position).carNumber);
			holder.tv_time.setText("车检有效期："+datas.get(position).time);
			
			return convertView;
		}

	}

	class ViewHolder {
		private TextView tv_number;
		private TextView tv_time;
		private ImageView iv_icon;
	}

	
}
