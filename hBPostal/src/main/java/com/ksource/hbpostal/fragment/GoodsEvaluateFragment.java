package com.ksource.hbpostal.fragment;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.EvaluateResultBean;
import com.ksource.hbpostal.bean.EvaluateResultBean.GoodsRatedListBean;
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

public class GoodsEvaluateFragment extends BaseFragment {

	private PullToRefreshListView lv;
	private ListView lv_evaluate;
	private BaseAdapter adapter;
	private List<GoodsRatedListBean> datas;
	private RelativeLayout rl_evaluate_null;

	private int currPage;

	@Override
	public View initView() {
		return View.inflate(context, R.layout.fragment_evaluate, null);
	}

	@Override
	public void initData() {
		rl_evaluate_null = (RelativeLayout) view
				.findViewById(R.id.rl_evaluate_null);
		lv = (PullToRefreshListView) view.findViewById(R.id.lv_evaluate);
		datas = new ArrayList<GoodsRatedListBean>();
		lv.setPullRefreshEnabled(false);
		lv.setPullLoadEnabled(true);
		lv.setScrollLoadEnabled(false);
		lv_evaluate = lv.getRefreshableView();
		lv_evaluate.setDivider(null);
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
		lv.doPullRefreshing(true, 500);

	}

	// 获取列表信息
	private void getData(final int currPage) {
		if (!NetStateUtils.isNetworkAvailable(context)) {
			ToastUtil.showTextToast(context, "当前网络不可用！");
			return;
		}
		Map<String, String> params = new HashMap<String, String>();
//				params.put("goodsId", GoodsDetailActivity.goodsId);
		params.put("pageSize", "10");
		params.put("currPage", "" + currPage);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				EvaluateResultBean resultBean = null;
				Gson gson = new Gson();
				try {
					resultBean = gson.fromJson(arg0,
							EvaluateResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (resultBean == null) {
//					 ToastUtil.showTextToast(context, "获取订单失败！");
					return;
				}
				if (resultBean.success) {
					// if (isUpdate) {
					// datas.clear();
					// }
					// if (resultBean.goodsRatedList == null ||
					// resultBean.goodsRatedList.size() == 0) {
					// // rl_order_null.setVisibility(View.VISIBLE);
					// lv.setHasMoreData(false);
					// return;
					// }
					datas.addAll(resultBean.goodsRatedList);
					if (datas.size() == 0) {
						rl_evaluate_null.setVisibility(View.VISIBLE);
						lv.setHasMoreData(false);
						return;
					} else {
						if (adapter == null) {
							adapter = new MyAdapter(datas);
							lv_evaluate.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						lv_evaluate.setVisibility(View.VISIBLE);
						lv.onPullDownRefreshComplete();
						lv.onPullUpRefreshComplete();
						lv.setHasMoreData(resultBean.goodsRatedList
								.size() < 10 ? false : true);
					}
				} else {
					if (resultBean.flag == 10) {
						mApplication.login();
						// getData(currPage);
					}
					lv.setHasMoreData(false);
					ToastUtil.showTextToast(context, resultBean.msg);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
//				ToastUtil.showTextToast(context, "获取订单失败！");
			}
		};
		DataUtil.doPostAESData(null,context,ConstantValues.GOODS_RATED_URL, params, callback);
	}

	class MyAdapter extends DefaultBaseAdapter<GoodsRatedListBean> {

		public MyAdapter(List<GoodsRatedListBean> datas) {
			super(datas);
		}

		@SuppressWarnings("finally")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.item_evalute_listview, null);
				holder = new ViewHolder();
				holder.iv_user_avatar = (RoundImageView) convertView
						.findViewById(R.id.iv_user_avatar);
				holder.tv_user_name = (TextView) convertView
						.findViewById(R.id.tv_user_name);
				holder.rb_eva = (RatingBar) convertView
						.findViewById(R.id.rb_eva);
				holder.tv_content = (TextView) convertView
						.findViewById(R.id.tv_content);
				holder.tv_record_time = (TextView) convertView
						.findViewById(R.id.tv_record_time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 填充数据
			String nickName = datas.get(position).NICK_NAME;
			
			if (!TextUtils.isEmpty(nickName)) {
				if (nickName.length() > 2) {
					nickName = nickName.substring(0, 1) + "**"
							+ nickName.substring(nickName.length() - 1);
				} else {
					nickName = nickName.substring(0, 1) + "**";
				}
			} else{
				nickName = "匿名";
			}
			holder.tv_user_name.setText(nickName);
			holder.rb_eva.setRating(datas.get(position).APPRAISE_LEVEL);
			holder.rb_eva.setEnabled(false);
			holder.tv_content.setText(datas.get(position).APPRAISE_CONTENT);
			String creatTime = TimeUtil
					.formatTimeMin(datas.get(position).APPRAISE_TIME);
			holder.tv_record_time.setText(creatTime);

			try {
				ImageLoaderUtil.loadNetPic(
						ConstantValues.BASE_URL
								+ datas.get(position).HEAD_IMAGE,
						holder.iv_user_avatar);

			} catch (Exception e) {
				System.out.println(e.getMessage());
				holder.iv_user_avatar
						.setImageResource(R.drawable.head_fail_img);

			} finally {

				return convertView;
			}
		}

	}

	class ViewHolder {
		private RoundImageView iv_user_avatar;
		private TextView tv_user_name;
		private RatingBar rb_eva;
		private TextView tv_content;
		private TextView tv_record_time;
	}

}
