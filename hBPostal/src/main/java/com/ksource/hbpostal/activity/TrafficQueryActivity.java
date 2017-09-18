package com.ksource.hbpostal.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.CarBean;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.yitao.util.ToastUtil;

/**
 * 交通违章查询
 * @author Administrator
 *
 */
public class TrafficQueryActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back, iv_right;
	private LinearLayout ll_add_car;
	private Button btn_query;
	private PullToRefreshListView lv;
	private ListView lv_car_msg;
	private BaseAdapter adapter;
	private List<CarBean> datas;
	private String curId = "";

	@Override
	public int getLayoutResId() {
		return R.layout.activity_wzquery;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("交通违章查询");
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		iv_right.setVisibility(View.VISIBLE);
		ll_add_car = (LinearLayout) findViewById(R.id.ll_add_car);
		btn_query = (Button) findViewById(R.id.btn_query);
		lv = (PullToRefreshListView) findViewById(R.id.lv_car_msg);
		
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		iv_right.setOnClickListener(this);
		ll_add_car.setOnClickListener(this);
		btn_query.setOnClickListener(this);
		lv.setPullRefreshEnabled(true);
		lv.setPullLoadEnabled(false);
		lv.setScrollLoadEnabled(true);
		lv.setHasMoreData(false);
		lv_car_msg = lv.getRefreshableView();
		lv_car_msg.setDivider(null);
		lv_car_msg.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				curId = datas.get(position).id;
				adapter.notifyDataSetChanged();
			}
		});
		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				lv.onPullDownRefreshComplete();
				lv.onPullUpRefreshComplete();
				lv.setHasMoreData(false);
				getData();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// isUpdate = false;
				// getData();
			}
		});
	}

	@Override
	public void initData() {
		getData();

	}

	private void getData() {
		datas = new ArrayList<CarBean>();
		datas.add(new CarBean("1234", "豫F32654", "1", "565412"));
		datas.add(new CarBean("1215", "豫F54654", "2", "565412"));
		datas.add(new CarBean("1254", "豫F64324", "1", "565412"));
		adapter = new MyAdapter(datas);
		lv_car_msg.setAdapter(adapter);
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
		case R.id.ll_add_car:
			ToastUtil.showTextToast(context, "添加车辆");
			startActivity(new Intent(context, AddCarActivity.class));
			break;
		case R.id.btn_query:
			ToastUtil.showTextToast(context, "查询"+curId);
			Intent intent = new Intent(context, QueryResultActivity.class);
			intent.putExtra("carId", curId);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	class MyAdapter extends DefaultBaseAdapter<CarBean> {

		public MyAdapter(List<CarBean> datas) {
			super(datas);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item_car_listview,
						null);
				holder = new ViewHolder();
				holder.iv_sec = (ImageView) convertView
						.findViewById(R.id.iv_sec);
				holder.tv_number = (TextView) convertView
						.findViewById(R.id.tv_number);
				holder.tv_type = (TextView) convertView
						.findViewById(R.id.tv_type);
				holder.tv_car_num = (TextView) convertView
						.findViewById(R.id.tv_car_num);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 填充数据
			String id = datas.get(position).id;
			if (curId != null && id != null && curId.equals(id)) {
				holder.iv_sec.setVisibility(View.VISIBLE);
			} else {
				holder.iv_sec.setVisibility(View.GONE);
			}
			holder.tv_number.setText(datas.get(position).number);
			String type = datas.get(position).type;

			if ("1".equals(type)) {
				holder.tv_type.setText("小型车");

			} else if ("2".equals(type)) {
				holder.tv_type.setText("大型车");

			} else {
				holder.tv_type.setText("车");

			}
			holder.tv_car_num.setText(datas.get(position).carNum);
			return convertView;
		}

	}

	class ViewHolder {
		private TextView tv_number;
		private TextView tv_type;
		private TextView tv_car_num;
		private ImageView iv_sec;
	}

}
