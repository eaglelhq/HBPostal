package com.ksource.hbpostal.activity;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.InviteReusltBean;
import com.ksource.hbpostal.bean.InviteReusltBean.InvitedHistoryListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.DialogUtil;
import com.yitao.util.NetStateUtils;
import com.yitao.util.ToastUtil;
import com.yitao.widget.RoundImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 邀请好友历史记录页面
 */
public class InviteHistoryActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private PullToRefreshListView lv;
	private ListView lv_history;
	private LoadDialog mLoadDialog;
	private List<InvitedHistoryListBean> datas;
	private BaseAdapter adapter;
	private RelativeLayout rl_error, rl_invite_null;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_invite_history;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		rl_error = (RelativeLayout) findViewById(R.id.rl_error);
		rl_invite_null = (RelativeLayout) findViewById(R.id.rl_invite_null);
		lv = (PullToRefreshListView) findViewById(R.id.lv_history);
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		rl_error.setOnClickListener(this);
		lv.setPullRefreshEnabled(true);
		lv.setPullLoadEnabled(false);
		lv.setScrollLoadEnabled(true);
		lv.setHasMoreData(false);
		lv_history = lv.getRefreshableView();
		lv_history.setDivider(null);
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
		tv_title.setText("邀请好友历史");
		datas = new ArrayList<InvitedHistoryListBean>();
		adapter = null;
		lv.doPullRefreshing(true, 500);
	}

	private void getData() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"数据加载中...");
		if (!NetStateUtils.isNetworkAvailable(context)) {
			ToastUtil.showTextToast(context, "当前网络不可用！");
			DialogUtil.getInstance().dialogDismiss(mLoadDialog);
			return;
		}
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				Gson gson = new Gson();
				InviteReusltBean resultBean = null;
				try {
					resultBean = gson.fromJson(
							arg0, InviteReusltBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (resultBean == null) {
					rl_error.setVisibility(View.VISIBLE);
					return;
				}
				if (resultBean.success) {
					rl_error.setVisibility(View.GONE);
					datas = resultBean.invitedHistoryList;
					if (datas == null || datas.size() == 0) {
						rl_invite_null.setVisibility(View.VISIBLE);
					} else {
						rl_invite_null.setVisibility(View.GONE);
						adapter = new MyAdapter(datas);
						lv_history.setAdapter(adapter);
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
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_INVITE_URL, params, callback);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_error:
			getData();
			break;

		default:
			break;
		}
	}

	class MyAdapter extends DefaultBaseAdapter<InvitedHistoryListBean> {

		private ViewHolder holder;

		public MyAdapter(List<InvitedHistoryListBean> datas) {
			super(datas);
		}

		@SuppressWarnings("finally")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			holder = null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.item_invite_listview, null);
				holder = new ViewHolder();
				holder.tv_user_name = (TextView) convertView
						.findViewById(R.id.tv_user_name);
				holder.iv_user_avatar = (RoundImageView) convertView
						.findViewById(R.id.iv_user_avatar);
				holder.tv_price = (TextView) convertView
						.findViewById(R.id.tv_price);
				holder.tv_record_time = (TextView) convertView
						.findViewById(R.id.tv_record_time);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();

			}

			// 填数据
			holder.tv_price.setText("金邮币+" + datas.get(position).SCORE);
			String nickName = datas.get(position).INVI_MEMBER_NAME;
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
			String creatTime = "";
			try {
				creatTime = datas.get(position).CREATE_TIME
						.substring(0, 4)
						+ "-"
						+ datas.get(position).CREATE_TIME.substring(4, 6)
						+ "-"
						+ datas.get(position).CREATE_TIME.substring(6, 8)
						+ " "
						+ datas.get(position).CREATE_TIME.substring(8, 10)
						+ ":"
						+ datas.get(position).CREATE_TIME.substring(10, 12);
			} catch (Exception e1) {
				e1.printStackTrace();
				creatTime = "时间不详";
			}
			holder.tv_record_time.setText(creatTime);
			try {
				ImageLoaderUtil.loadNetPic(
						ConstantValues.BASE_URL
								+ datas.get(position).HEAD_IMAGE,
						holder.iv_user_avatar);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				return convertView;
			}
		}

	}

	class ViewHolder {
		private RoundImageView iv_user_avatar;
		private TextView tv_user_name;
		private TextView tv_price;
		private TextView tv_record_time;
	}

}
