package com.ksource.hbpostal.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.IllegalBean;
import com.yitao.util.ToastUtil;
/**
 * 违章查询
 * @author Administrator
 *
 */
public class QueryResultActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back,iv_right;
	private TextView tv_fakuan,tv_weizhang,tv_fen;
	private ListView lv_weifa_msg;
	private BaseAdapter adapter;
	private List<IllegalBean> datas;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_right:
			ToastUtil.showTextToast(context, "hehehe");
			break;

		default:
			break;
		}

	}

	@Override
	public int getLayoutResId() {
		return R.layout.activity_query_result;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("违法信息");
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		iv_right.setVisibility(View.VISIBLE);
		
		tv_fakuan = (TextView) findViewById(R.id.tv_fakuan);
		tv_weizhang = (TextView) findViewById(R.id.tv_weizhang);
		tv_fen = (TextView) findViewById(R.id.tv_fen);
		
		lv_weifa_msg = (ListView) findViewById(R.id.lv_weifa_msg);
		datas = new ArrayList<IllegalBean>();
		datas.add(new IllegalBean("2016-10-17 15:32:07", true, "河南省郑州市高新区枫杨街", "违章停车", 300, 2));
		datas.add(new IllegalBean("2016-10-17 15:32:07", false, "河南省郑州市高新区枫杨街", "违章停车", 500, 3));
		datas.add(new IllegalBean("2016-10-17 15:32:07", true, "河南省郑州市高新区枫杨街", "违章停车", 300, 2));
		adapter = new MyAdapter(datas);
		lv_weifa_msg.setAdapter(adapter);
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		iv_right.setOnClickListener(this);
		lv_weifa_msg.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
			}
		});
	}

	@Override
	public void initData() {
		tv_fakuan.setText("800");
		tv_weizhang.setText("3");
		tv_fen.setText("5");

	}
	
	class MyAdapter extends DefaultBaseAdapter<IllegalBean>{

		public MyAdapter(List<IllegalBean> datas) {
			super(datas);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.item_illegal, null);
				holder = new ViewHolder();
				holder.tv_number = (TextView) convertView
						.findViewById(R.id.tv_number);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				holder.tv_is_deal = (TextView) convertView
						.findViewById(R.id.tv_is_deal);
				holder.tv_addr = (TextView) convertView
						.findViewById(R.id.tv_addr);
				holder.tv_detail = (TextView) convertView
						.findViewById(R.id.tv_detail);
				holder.tv_money = (TextView) convertView
						.findViewById(R.id.tv_money);
				holder.tv_fen = (TextView) convertView
						.findViewById(R.id.tv_fen);
				holder.iv_deal = (ImageView) convertView
						.findViewById(R.id.iv_deal);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 填充数据
			holder.tv_number.setText(position+1+"");
			holder.tv_time.setText(datas.get(position).time);
			if (datas.get(position).isDeal) {
				holder.tv_is_deal.setText("已处理");
				holder.tv_is_deal.setTextColor(Color.GRAY);
				holder.iv_deal.setVisibility(View.GONE);
				
			} else {
				holder.tv_is_deal.setText("未处理");
				holder.tv_is_deal.setTextColor(Color.rgb(255, 191, 0));
				holder.iv_deal.setVisibility(View.VISIBLE);
			}
			holder.tv_addr.setText(datas.get(position).addr);
			holder.tv_detail.setText(datas.get(position).detail);
			holder.tv_money.setText(datas.get(position).money+"元");
			holder.tv_fen.setText(""+datas.get(position).fen);
			return convertView;
		}
		
	}
	
	class ViewHolder{
		private TextView tv_number;
		private TextView tv_time;
		private TextView tv_is_deal;
		private TextView tv_addr;
		private TextView tv_detail;
		private TextView tv_money;
		private TextView tv_fen;
		private ImageView iv_deal;
		
	}
	 

}
