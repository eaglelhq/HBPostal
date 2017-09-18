package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.HistoryResultBean;
import com.ksource.hbpostal.bean.HistoryResultBean.PaymentListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.TimeUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.DialogUtil;
import com.yitao.util.NetStateUtils;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 具体缴费条目历史列表
 */
public class HistoryListActivityForPhone extends BaseActivity {

	private TextView tv_title, tv_right;
	private ImageView iv_back;
	private PullToRefreshListView lv;
	private ListView lv_history;
	private RelativeLayout rl_error, rl_null;

	private BaseAdapter adapter;
	private List<PaymentListBean> datas;

	private int currPage = 1;
	private boolean isUpdate;
	private LoadDialog mLoadDialog;
	private int type;

	private String url;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_error:
			rl_error.setVisibility(View.GONE);
			mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
					"数据加载中...");
			currPage = 1;
			adapter = null;
			getData(currPage);
			break;
		case R.id.btn_right:
			Intent intent = new Intent(context, SHJFActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public int getLayoutResId() {
		return R.layout.activity_history_detail;
	}

	@Override
	public void initView() {
		datas = new ArrayList<>();
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_right = (TextView) findViewById(R.id.btn_right);
		lv = (PullToRefreshListView) findViewById(R.id.lv_history);
		rl_null = (RelativeLayout) findViewById(R.id.rl_null);
		rl_error = (RelativeLayout) findViewById(R.id.rl_error);
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		tv_right.setOnClickListener(this);
		rl_error.setOnClickListener(this);
		lv.setPullRefreshEnabled(true);
		lv.setPullLoadEnabled(false);
		lv.setScrollLoadEnabled(true);
		lv_history = lv.getRefreshableView();
		lv_history.setDivider(null);
		lv_history.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(context, BillDetailActivity.class);
				intent.putExtra("id", datas.get(position).ID);
				intent.putExtra("type", type);
				startActivity(intent);
			}
		});
		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				isUpdate = true;
				currPage = 1;
				getData(currPage);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				isUpdate = false;
				getData(++currPage);
			}
		});
	}

	@Override
	public void initData() {
		type = getIntent().getIntExtra("type", 1);
		switch (type) {
		case 1:
			tv_title.setText("水费缴费历史");
			url = ConstantValues.GET_WATER_PAY_HISTORY_URL;
			break;
		case 2:
			tv_title.setText("电费缴费历史");
			url = ConstantValues.GET_ELE_PAY_HISTORY_URL;
			break;
		case 3:
			tv_title.setText("燃气费缴费历史");
			url = ConstantValues.GET_GAS_PAY_HISTORY_URL;
			break;
		case 4:
			tv_title.setText("有线电视缴费历史");
			url = ConstantValues.GET_TV_PAY_HISTORY_URL;
			break;
		case 5:
			tv_title.setText("固话宽带缴费历史");
			url = ConstantValues.GET_BROAD_PAY_HISTORY_URL;
			break;
		case 6:
			tv_title.setText("手机缴费历史");
			url = ConstantValues.GET_MOBILE_PAY_HISTORY_URL;
			break;
		default:
			break;
		}
		tv_right.setText("缴费");
		tv_right.setVisibility(View.VISIBLE);
		
		currPage = 1;
		adapter = null;
		lv.doPullRefreshing(true, 100);
	}

	// 获取列表信息
	private void getData(final int currPage) {
		
		if (!NetStateUtils.isNetworkAvailable(context)) {
			ToastUtil.showTextToast(context, "当前网络不可用！");
			DialogUtil.getInstance().dialogDismiss(mLoadDialog);
			return;
		}
		Map<String, String> params = new HashMap<>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		if (type == 6) {
			params.put("type", "3");
		}
		if (type == 5) {
			params.put("type", "1");
		}
		params.put("pageSize", "10");
		params.put("currPage", "" + currPage);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				Gson gson = new Gson();
				HistoryResultBean resultBean = null;
				try {
					resultBean = gson.fromJson(
							arg0, HistoryResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (resultBean == null) {
					rl_error.setVisibility(View.VISIBLE);
					lv.setHasMoreData(false);
				} else {
					if (resultBean.success) {
						rl_error.setVisibility(View.GONE);
						rl_null.setVisibility(View.GONE);
						if (isUpdate) {
							datas.clear();
						}
						 datas.addAll(resultBean.paymentList);
						if (datas.size() == 0) {
							rl_null.setVisibility(View.VISIBLE);
							return;
						}
						if (adapter == null) {
							adapter = new MyAdapter(datas);
							lv_history.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						if (isUpdate) {
							lv_history.setSelection(0);
						}
						lv_history.setVisibility(View.VISIBLE);
						lv.onPullDownRefreshComplete();
						lv.onPullUpRefreshComplete();
						lv.setHasMoreData(resultBean.paymentList.size() >= 10 );

					} else {
						if (resultBean.flag == 10) {
							mApplication.login();
							// getData(currPage);
						}
						ToastUtil.showTextToast(context,
								resultBean.msg);
						rl_error.setVisibility(View.VISIBLE);
					}
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
//				ToastUtil.showTextToast(context, "获取数据失败！");
				rl_error.setVisibility(View.VISIBLE);
				lv.setHasMoreData(false);
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,url, params, callback);
	}

	class MyAdapter extends DefaultBaseAdapter<PaymentListBean> {

		private ViewHolder holder;

		public MyAdapter(List<PaymentListBean> datas) {
			super(datas);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			holder = null;
			if (convertView == null) {
				convertView = View
						.inflate(context, R.layout.item_history, null);
				holder = new ViewHolder();
				holder.tv_user_name = (TextView) convertView
						.findViewById(R.id.tv_user_name);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				holder.tv_jinbi = (TextView) convertView
						.findViewById(R.id.tv_jinbi);
				holder.tv_huhao = (TextView) convertView
						.findViewById(R.id.tv_huhao);
				holder.tv_huming = (TextView) convertView
						.findViewById(R.id.tv_huming);
				holder.tv_user_number = (TextView) convertView
						.findViewById(R.id.tv_user_number);
				holder.tv_money = (TextView) convertView
						.findViewById(R.id.tv_money);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 填充数据
			holder.tv_huming.setText("用户名:");
			holder.tv_huhao.setText("手机号:");
			int state = datas.get(position).PAY_STATE;//支付状态
			holder.tv_user_name.setText(datas.get(position).MEMBER_NAME);
			String payTime = datas.get(position).PAY_TIME;
			payTime = TimeUtil.formatTimeMin(payTime);
			holder.tv_time.setText(payTime);
//			holder.tv_jinbi.setText("+" + datas.get(position).jinbi);
			holder.tv_user_number.setText(datas.get(position).DEVICE_NUM);
			String money = datas.get(position).FACT_MONEY;
			String integral = datas.get(position).TOTAL_INTEGRAL;
			holder.tv_money.setText("￥" + money);
			holder.tv_jinbi.setText(integral);
//			if (TextUtils.isEmpty(integral) || "0".equals(integral)) {
//				holder.tv_money.setText("￥" + money);
//			} else {
//				holder.tv_money.setText("￥" + money+"\n"+integral+"积分");
//			}
			return convertView;
		}

	}

	class ViewHolder {
		private TextView tv_time;
		private TextView tv_jinbi;
		private TextView tv_huhao;
		private TextView tv_huming;
		private TextView tv_user_name;
		private TextView tv_user_number;
		private TextView tv_money;
	}

}
