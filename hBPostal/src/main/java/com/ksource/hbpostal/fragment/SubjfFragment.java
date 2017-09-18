package com.ksource.hbpostal.fragment;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.ScoreDetailResultBean;
import com.ksource.hbpostal.bean.ScoreDetailResultBean.ScoreListBean;
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

public class SubjfFragment extends BaseFragment {

	private PullToRefreshListView lv;
	private ListView listview;
	private List<ScoreListBean> datas;
	// private RelativeLayout rl_order_null;
	// private Button btn_gosee;
	private BaseAdapter adapter;
	private LoadDialog mLoadDialog;
	private RelativeLayout rl_error, rl_null;

	private int currPage = 1;
	private boolean isUpdate;

	@Override
	public View initView() {
		return View.inflate(context, R.layout.fragment_add_jf, null);
	}

	@Override
	public void initData() {
		datas = new ArrayList<ScoreListBean>();
		lv = (PullToRefreshListView) view.findViewById(R.id.lv_add_jf);
		rl_error = (RelativeLayout) view.findViewById(R.id.rl_error);
		rl_null = (RelativeLayout) view.findViewById(R.id.rl_null);
		rl_error.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rl_error.setVisibility(View.GONE);
				mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
						"数据加载中...");
				currPage = 1;
				adapter = null;
				getData(currPage);
			}
		});
		lv.setPullRefreshEnabled(true);
		lv.setPullLoadEnabled(false);
		lv.setScrollLoadEnabled(true);
		listview = lv.getRefreshableView();
		listview.setDivider(null);
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
		currPage = 1;
		adapter = null;
		lv.doPullRefreshing(true, 100);
	}

	// 获取列表信息
	private void getData(final int currPage) {

		if (!NetStateUtils.isNetworkAvailable(context)) {
			ToastUtil.showTextToast(context, "当前网络不可用！");
			return;
		}
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("pageSize", "16");
		params.put("currPage", "" + currPage);
		params.put("type", "1");
		params.put("isAdd", "2");
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ScoreDetailResultBean resultBean = null;
				Gson gson = new Gson();
				try {
					resultBean = gson.fromJson(arg0,
							ScoreDetailResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (resultBean == null) {
					rl_error.setVisibility(View.VISIBLE);
					return;
				}
				if (resultBean.success) {
					rl_error.setVisibility(View.GONE);
					rl_null.setVisibility(View.GONE);
					if (isUpdate) {
						datas.clear();
					}
					datas.addAll(resultBean.scoreList);
					if (datas.size() == 0) {
						rl_null.setVisibility(View.VISIBLE);
					} else {
						if (adapter == null) {
							adapter = new OrderAdapter(datas);
							listview.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						if (isUpdate) {
							listview.setSelection(0);
						}
						listview.setVisibility(View.VISIBLE);
						lv.onPullDownRefreshComplete();
						lv.onPullUpRefreshComplete();
						lv.setHasMoreData(resultBean.scoreList.size() >= 16);
					}
				} else {
					if (resultBean.flag == 10) {
						mApplication.login();
					}
					ToastUtil.showTextToast(context, resultBean.msg);
					rl_error.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				rl_error.setVisibility(View.VISIBLE);
//				ToastUtil.showTextToast(context, "获取数据失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.SCORE_URL, params, callback);
	}

	class OrderAdapter extends DefaultBaseAdapter<ScoreListBean> {

		private ViewHolder holder;

		public OrderAdapter(List<ScoreListBean> datas) {
			super(datas);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			holder = null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.item_jifen_listview, null);
				holder = new ViewHolder();
				holder.ll_head = (LinearLayout) convertView
						.findViewById(R.id.ll_head);
				holder.tv_start_time = (TextView) convertView
						.findViewById(R.id.tv_start_time);
				holder.tv_end_time = (TextView) convertView
						.findViewById(R.id.tv_end_time);
				holder.tv_user_name = (TextView) convertView
						.findViewById(R.id.tv_user_name);
				holder.tv_price = (TextView) convertView
						.findViewById(R.id.tv_price);
				holder.tv_record_time = (TextView) convertView
						.findViewById(R.id.tv_record_time);
				holder.view_end = convertView.findViewById(R.id.view_end);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();

			}

			// 填数据
			// String lastBeginTime = "";
			// if (position >0) {
			// lastBeginTime =
			// TimeUtil.formatTimeMon(datas.get(position-1).BEGIN_TIME);
			// }
			// String beginTime =
			// TimeUtil.formatTimeMon(datas.get(position).BEGIN_TIME);
			// String endTime =
			// TimeUtil.formatTimeDayHZ(datas.get(position).OVER_TIME);
			String creatTime = TimeUtil
					.formatTimeSec(datas.get(position).CREATE_TIME);
			// if (lastBeginTime.equals(beginTime)) {
			holder.ll_head.setVisibility(View.GONE);
			holder.view_end.setVisibility(View.GONE);
			// } else {
			// holder.view_end.setVisibility(View.VISIBLE);
			// holder.ll_head.setVisibility(View.VISIBLE);
			// }
			// holder.tv_start_time.setText(beginTime);
			// holder.tv_end_time.setText("失效日期:" + endTime);
			holder.tv_price.setTextColor(Color.RED);
			holder.tv_user_name.setText(datas.get(position).SCORE_SOURCE);
			holder.tv_price.setText("-" + datas.get(position).SCORE);
			holder.tv_record_time.setText(creatTime);
			return convertView;
		}

	}

	class ViewHolder {
		private TextView tv_start_time;
		private TextView tv_end_time;
		private TextView tv_user_name;
		private TextView tv_price;
		private TextView tv_record_time;
		private View view_end;
		private LinearLayout ll_head;
	}

}
