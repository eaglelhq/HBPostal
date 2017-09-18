package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.AddrResaultBean;
import com.ksource.hbpostal.bean.AddrResaultBean.AddressListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ChioceAddrActivity extends BaseActivity {

	private static int RESULT_CODE = 101;

	private TextView tv_title, tv_right;
	private ImageView iv_back;
	private PullToRefreshListView lv;
	private ListView lv_addr;
	private List<AddressListBean> datas;
	private RelativeLayout rl_error, rl_addr_null;
	private Button btn_gosee;

	private LoadDialog mLoadDialog;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_chioce_addr;
	}

	@Override
	public void initView() {
		rl_error = (RelativeLayout) findViewById(R.id.rl_error);
		rl_addr_null = (RelativeLayout) findViewById(R.id.rl_addr_null);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_right = (TextView) findViewById(R.id.btn_right);
		btn_gosee = (Button) findViewById(R.id.btn_gosee);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		lv = (PullToRefreshListView) findViewById(R.id.lv_addr);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		tv_right.setOnClickListener(this);
		btn_gosee.setOnClickListener(this);
		rl_error.setOnClickListener(this);
		lv.setPullRefreshEnabled(true);
		lv.setPullLoadEnabled(false);
		lv.setScrollLoadEnabled(true);
		lv.setHasMoreData(false);
		lv_addr = lv.getRefreshableView();
		lv_addr.setDivider(null);
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
		lv_addr.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent mIntent = new Intent();
				String addr = datas.get(position).PROVINCE_NAME
						+ datas.get(position).CITY_NAME
						+ datas.get(position).AREA_NAME;
				mIntent.putExtra("addr", addr + datas.get(position).ADDRESS);
				mIntent.putExtra("userName", datas.get(position).NAME);
				mIntent.putExtra("phone", datas.get(position).MOBILE);
				mIntent.putExtra("addrId", datas.get(position).ID);
				mIntent.putExtra("provinceId", datas.get(position).PROVINCE_ID);
				// 设置结果，并进行传送
				setResult(RESULT_CODE, mIntent);
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (datas != null) {
			datas.clear();
		}
		lv.doPullRefreshing(true, 500);
	}

	@Override
	public void initData() {

		tv_title.setText("选择收货地址");
		tv_right.setText("管理");
		tv_right.setVisibility(View.VISIBLE);
		String token = sp.getString(ConstantValues.TOKEN, null);
		if (TextUtils.isEmpty(token)) {
			// finish();
			startActivity(new Intent(context, LoginActivity.class));
		}
		// getData();
	}

	private void getData() {
		// mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
		// "数据加载中...");
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("type", "1");
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);

				Gson gson = new Gson();
				AddrResaultBean addrResult = null;
				try {
					addrResult = gson.fromJson(arg0,
							AddrResaultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (addrResult == null) {
					rl_error.setVisibility(View.VISIBLE);
					return;
				}
				if (addrResult.success) {
					rl_error.setVisibility(View.GONE);
					if (addrResult.addressList != null
							&& addrResult.addressList.size() > 0) {
						rl_addr_null.setVisibility(View.GONE);
						datas = addrResult.addressList;
						lv_addr.setAdapter(new MyAdapter(datas));
					} else {
						rl_addr_null.setVisibility(View.VISIBLE);
					}
				} else {
					if (addrResult.flag == 10) {
						mApplication.login();
						// getData();
						rl_error.setVisibility(View.VISIBLE);
						ToastUtil
								.showTextToast(context, addrResult.msg);
					}
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				rl_error.setVisibility(View.VISIBLE);
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_ADDR_URL, params, callback);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_right:
			Intent intent = new Intent(context, AddrActivity.class);
			startActivity(intent);
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_error:
			if (datas != null) {
				datas.clear();
			}
			lv.doPullRefreshing(true, 500);
			break;
		case R.id.btn_gosee:
			startActivity(new Intent(context, AddAddrActivity.class));
			break;

		default:
			break;
		}
	}

	class MyAdapter extends DefaultBaseAdapter<AddressListBean> {

		private ViewHolder holder;

		public MyAdapter(List<AddressListBean> datas) {
			super(datas);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			holder = null;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item_addr_list,
						null);
				holder = new ViewHolder();
				holder.tv_user_name = (TextView) convertView
						.findViewById(R.id.tv_user_name);
				holder.tv_user_addr = (TextView) convertView
						.findViewById(R.id.tv_user_addr);
				holder.tv_user_phone = (TextView) convertView
						.findViewById(R.id.tv_user_phone);
				holder.tv_default = (TextView) convertView
						.findViewById(R.id.tv_default);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 填充数据
			holder.tv_user_name.setText(datas.get(position).NAME);
			holder.tv_user_phone.setText(datas.get(position).MOBILE);
			String addr = datas.get(position).PROVINCE_NAME
					+ datas.get(position).CITY_NAME
					+ datas.get(position).AREA_NAME;
			holder.tv_user_addr.setText(addr + datas.get(position).ADDRESS);

			int isDefault = datas.get(position).IS_DEFAULT_ADDRESS;
			switch (isDefault) {
			case 1:
				holder.tv_default.setVisibility(View.GONE);

				break;
			case 2:
				holder.tv_default.setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}

			return convertView;
		}

	}

	class ViewHolder {
		private TextView tv_user_name;
		private TextView tv_user_addr;
		private TextView tv_user_phone;
		private TextView tv_default;
	}

}
