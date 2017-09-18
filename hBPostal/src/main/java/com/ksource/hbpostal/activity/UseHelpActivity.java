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
import com.ksource.hbpostal.bean.PushMsgResaultBean;
import com.ksource.hbpostal.bean.PushMsgResaultBean.HelpInfoListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 *	生活缴费-使用帮助列表
 */
public class UseHelpActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private PullToRefreshListView lv;
	private ListView listView;
	private List<HelpInfoListBean> datas;
	private BaseAdapter adapter;
	private RelativeLayout rl_error, rl_null;

	private LoadDialog mLoadDialog;

	private boolean isUpdate;
	private int currPage;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_push_msg;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		rl_error = (RelativeLayout) findViewById(R.id.rl_error);
		rl_null = (RelativeLayout) findViewById(R.id.rl_null);
		lv = (PullToRefreshListView) findViewById(R.id.lv_push_msg);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		rl_error.setOnClickListener(this);
		lv.setPullRefreshEnabled(true);
		lv.setPullLoadEnabled(false);
		lv.setScrollLoadEnabled(true);
		listView = lv.getRefreshableView();
		listView.setDivider(null);
		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				isUpdate = true;
				currPage = 1;
				getData();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				isUpdate = false;
				currPage++;
				getData();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 使用帮助详情
				Intent intent = new Intent(context, HelpHtmlActivity.class);
				intent.putExtra("title", "使用帮助");
				intent.putExtra("newsTitle", datas.get(position).TITLE);
				intent.putExtra("url",
						ConstantValues.HELP_HTML_URL
								+ datas.get(position).ID );
				startActivity(intent);
			}
		});
	}

	@Override
	public void initData() {
		datas = new ArrayList<>();
		tv_title.setText("使用帮助");
		adapter = null;
		currPage = 1;
		lv.doPullRefreshing(true, 500);

	}

	private void getData() {
		Map<String, String> params = new HashMap<>();
		params.put("pageSize", "10");
		params.put("currPage", "" + currPage);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				Gson gson = new Gson();
				PushMsgResaultBean msgResult = null;
				try {
					msgResult = gson.fromJson(arg0,
							PushMsgResaultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (msgResult == null) {
					rl_error.setVisibility(View.VISIBLE);
					return;
				}
				if (msgResult.success) {
					rl_error.setVisibility(View.GONE);
					if (isUpdate) {
						datas.clear();
					}
					datas.addAll(msgResult.helpInfoList);
					if (datas.size() == 0) {
						rl_null.setVisibility(View.VISIBLE);
					} else {
						rl_null.setVisibility(View.GONE);
						if (adapter == null) {
							adapter = new MyAdapter(datas);
							listView.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
					}
					if (isUpdate) {
						listView.setSelection(0);
					}
					listView.setVisibility(View.VISIBLE);
					lv.onPullDownRefreshComplete();
					lv.onPullUpRefreshComplete();
					lv.setHasMoreData(msgResult.helpInfoList.size() >= 10 );
				} else if (msgResult.flag == 10) {
					mApplication.login();
				} else {
					ToastUtil.showTextToast(context, msgResult.msg);
					rl_error.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
//				ToastUtil.showTextToast(context, "获取数据失败！");
				rl_error.setVisibility(View.VISIBLE);
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_HELP_LIST_URL, params, callback);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_error:
			mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
					"数据加载中...");
			adapter = null;
			currPage = 1;
			getData();
			break;

		default:
			break;
		}

	}

	class MyAdapter extends DefaultBaseAdapter<HelpInfoListBean> {

		private ViewHolder holder;

		public MyAdapter(List<HelpInfoListBean> datas) {
			super(datas);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			holder = null;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item_title, null);
				holder = new ViewHolder();
				holder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tv_title.setText(datas.get(position).TITLE);

			return convertView;
		}

	}

	class ViewHolder {
		// private TextView tv_card_name;
		private TextView tv_title;
	}

}
