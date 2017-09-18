package com.ksource.hbpostal.fragment;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.RecordResultBean;
import com.ksource.hbpostal.bean.RecordResultBean.GoodsByRecordListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.util.TimeUtil;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.NetStateUtils;
import com.yitao.util.ToastUtil;
import com.yitao.widget.RoundImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class RecordsFragment extends BaseFragment {

	private PullToRefreshListView lv;
	private ListView lv_records;
	private BaseAdapter adapter;
	private List<GoodsByRecordListBean> datas;
	private RelativeLayout rl_record_null;

	private int currPage;

	@Override
	public View initView() {
		return View.inflate(context, R.layout.fragment_records, null);
	}

	@Override
	public void initData() {

		rl_record_null = (RelativeLayout) view
				.findViewById(R.id.rl_record_null);
		lv = (PullToRefreshListView) view.findViewById(R.id.lv_records);
		datas = new ArrayList<GoodsByRecordListBean>();
		lv.setPullRefreshEnabled(false);
		lv.setPullLoadEnabled(true);
		lv.setScrollLoadEnabled(true);
		lv_records = lv.getRefreshableView();
		lv_records.setDivider(null);
		lv_records.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				currPage = 1;
				getData(currPage);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
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
//				params.put("goodsId", GoodsDetailActivity.goodsId);
		params.put("pageSize", "10");
		params.put("currPage", "" + currPage);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {

				RecordResultBean resultBean = null;
				Gson gson = new Gson();
				try {
					resultBean = gson.fromJson(arg0,
							RecordResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (resultBean == null) {
					ToastUtil.showTextToast(context, "获取数据失败！");
					return;
				}
				if (resultBean.success) {
					rl_record_null.setVisibility(View.GONE);
					// if (isUpdate) {
					// datas.clear();
					// }
					// if (resultBean.goodsByRecordList == null ||
					// resultBean.goodsByRecordList.size() == 0) {
					// // rl_order_null.setVisibility(View.VISIBLE);
					// lv.setHasMoreData(false);
					// return;
					// }
					datas.addAll(resultBean.goodsByRecordList);
					if (adapter == null) {
						adapter = new MyAdapter(datas);
						lv_records.setAdapter(adapter);
					} else {
						adapter.notifyDataSetChanged();
					}
					lv_records.setVisibility(View.VISIBLE);
					lv.onPullDownRefreshComplete();
					lv.onPullUpRefreshComplete();
					lv.setHasMoreData(resultBean.goodsByRecordList
							.size() < 10 ? false : true);
					if (datas.size() == 0) {
						rl_record_null.setVisibility(View.VISIBLE);
						lv.setHasMoreData(false);
						return;
					}
				} else if (resultBean.flag == 10) {
					mApplication.login();
					// getData(currPage);
				} else {
					rl_record_null.setVisibility(View.VISIBLE);
					lv.setHasMoreData(false);
					ToastUtil.showTextToast(context, resultBean.msg);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				ToastUtil.showTextToast(context, "获取数据失败！");
				rl_record_null.setVisibility(View.VISIBLE);
			}
		};
		DataUtil.doPostAESData(null,context,ConstantValues.GOODS_RECOED_URL, params, callback);
	}

	class MyAdapter extends DefaultBaseAdapter<GoodsByRecordListBean> {

		public MyAdapter(List<GoodsByRecordListBean> datas) {
			super(datas);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.item_record_listview, null);
				holder = new ViewHolder();
				holder.iv_user_avatar = (RoundImageView) convertView
						.findViewById(R.id.iv_user_avatar);
				holder.tv_user_name = (TextView) convertView
						.findViewById(R.id.tv_user_name);
				holder.tv_price = (TextView) convertView
						.findViewById(R.id.tv_price);
				holder.tv_record_time = (TextView) convertView
						.findViewById(R.id.tv_record_time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 填充数据
			String headImage = datas.get(position).HEAD_IMAGE;
			String nickName = datas.get(position).NICK_NAME;
			if (!TextUtils.isEmpty(nickName)) {
				if (nickName.length() > 2) {
					nickName = nickName.substring(0, 1) + "**"
							+ nickName.substring(nickName.length() - 1);
				} else {
					nickName = nickName.substring(0, 1) + "**";
				}
			} else {
				nickName = "匿名";
			}
			holder.tv_user_name.setText(nickName);
			String price = datas.get(position).TOTAL_MONEY;
			String jifen = datas.get(position).TOTAL_SCORE;
			String feiyong = getFei(price, jifen);
			holder.tv_price.setText(feiyong);
			String creatTime = TimeUtil
					.formatTimeMin(datas.get(position).CREATE_TIME);
			holder.tv_record_time.setText(creatTime);
			if (headImage == null || TextUtils.isEmpty(headImage)) {
				holder.iv_user_avatar
						.setImageResource(R.drawable.head_fail_img);
			} else {
				try {
					ImageLoaderUtil.loadNetPic(
							ConstantValues.BASE_URL
									+ datas.get(position).HEAD_IMAGE,
							holder.iv_user_avatar);

				} catch (Exception e) {
					System.out.println(e.getMessage());
					holder.iv_user_avatar
							.setImageResource(R.drawable.head_fail_img);
				}
			}
			return convertView;
		}

	}

	private String getFei(String price, String jifen) {
		if (TextUtils.isEmpty(price) || "0".equals(price)) {
			return jifen + "积分";
		} else if (TextUtils.isEmpty(jifen) || "0".equals(jifen)) {
			return "￥" + price;
		} else {
			return "￥" + price + "+" + jifen + "积分";
		}
	}

	class ViewHolder {
		private RoundImageView iv_user_avatar;
		private TextView tv_user_name;
		private TextView tv_price;
		private TextView tv_record_time;
	}

	
}
