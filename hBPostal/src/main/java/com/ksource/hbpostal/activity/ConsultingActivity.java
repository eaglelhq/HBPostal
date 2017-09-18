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
import com.ksource.hbpostal.bean.ConsultResaultBean;
import com.ksource.hbpostal.bean.ConsultResaultBean.KhzxListBean;
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

public class ConsultingActivity extends BaseActivity {

	private TextView tv_title, btn_right;
	private ImageView iv_back;
	private PullToRefreshListView lv;
	private ListView listView;
	private List<KhzxListBean> datas;
	private BaseAdapter adapter;
	private LoadDialog mLoadDialog;
	private RelativeLayout rl_error, rl_consulting_null;

	private boolean isUpdate;
	private int currPage;
	private String token;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_consulting;
	}

	@Override
	public void initView() {
		token = sp.getString(ConstantValues.TOKEN, "");
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_right = (TextView) findViewById(R.id.btn_right);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		rl_error = (RelativeLayout) findViewById(R.id.rl_error);
		rl_consulting_null = (RelativeLayout) findViewById(R.id.rl_consulting_null);
		lv = (PullToRefreshListView) findViewById(R.id.lv_consulting);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		btn_right.setOnClickListener(this);
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
				lv.onPullDownRefreshComplete();
				lv.onPullUpRefreshComplete();
				lv.setHasMoreData(false);
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
				Intent intent = new Intent(context, BaseHtmlActivity.class);
				intent.putExtra("title", "客户咨询");
				intent.putExtra("url",
						ConstantValues.KHZX_HTML_URL + datas.get(position).ID+ "&token="+token);
				//活动信息详情
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		adapter = null;
		currPage = 1;
		lv.doPullRefreshing(true, 500);
	}

	@Override
	public void initData() {
		datas = new ArrayList<>();
		tv_title.setText("客户咨询");
		btn_right.setText("咨询");
		btn_right.setVisibility(View.VISIBLE);
		adapter = null;
		currPage = 1;
		lv.doPullRefreshing(true, 500);

	}

	private void getData() {
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("pageSize", "10");
		params.put("currPage", "" + currPage);
		
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ConsultResaultBean msgResult = null ;
				Gson gson = new Gson();
				try {
					msgResult = gson.fromJson(arg0,
							ConsultResaultBean.class);
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
					datas.addAll(msgResult.khzxList);
					if (datas.size() == 0) {
						rl_consulting_null.setVisibility(View.VISIBLE);
					} else {
						rl_consulting_null.setVisibility(View.GONE);
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
					lv.setHasMoreData(msgResult.khzxList.size() > 10 ? false
							: true);
				} else {
					if (msgResult.flag == 10) {
						mApplication.login();
						// getData();
					}
					ToastUtil.showTextToast(context, msgResult.msg);
					rl_error.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				rl_error.setVisibility(View.VISIBLE);
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.KHZX_URL, params, callback);

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
		case R.id.btn_right:
			Intent intent = new Intent(context, AddConsultActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	class MyAdapter extends DefaultBaseAdapter<KhzxListBean> {

		private ViewHolder holder;

		public MyAdapter(List<KhzxListBean> datas) {
			super(datas);
		}

		@SuppressWarnings("finally")
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
		private TextView tv_title;
	}

}
