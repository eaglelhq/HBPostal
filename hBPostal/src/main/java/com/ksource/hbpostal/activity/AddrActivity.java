package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.ksource.hbpostal.bean.BaseResultBean;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

public class AddrActivity extends BaseActivity {

	private TextView tv_title;
	private Button btn_gosee;
	private ImageView iv_back;
	private TextView tv_add_addr;
	private PullToRefreshListView lv;
	private ListView lv_addr;
	private List<AddressListBean> datas;
	private BaseAdapter adapter;
	private RelativeLayout rl_error, rl_addr_null;

	private LoadDialog mLoadDialog;
	private int type;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_addr;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_gosee = (Button) findViewById(R.id.btn_gosee);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_add_addr = (TextView) findViewById(R.id.tv_add_addr);
		rl_error = (RelativeLayout) findViewById(R.id.rl_error);
		rl_addr_null = (RelativeLayout) findViewById(R.id.rl_addr_null);
		lv = (PullToRefreshListView) findViewById(R.id.lv_addr);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		btn_gosee.setOnClickListener(this);
		tv_add_addr.setOnClickListener(this);
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
	}

	@Override
	public void initData() {
        type = getIntent().getIntExtra("type",1);
		if (type == 1){
			tv_title.setText("管理收货地址");
			btn_gosee.setText("添加收货地址");
			tv_add_addr.setText("添加收货地址");
		}else{
			tv_title.setText("管理寄件地址");
			btn_gosee.setText("添加寄件地址");
			tv_add_addr.setText("管理寄件地址");
		}
        datas = new ArrayList<>();
	}

	@Override
	protected void onStart() {
		super.onStart();
		adapter = null;
//		isUpdate = true;
//		currPage = 1;
		getData();
//		lv.doPullRefreshing(true, 500);
	}

	private void getData() {
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("type", type+"");
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				AddrResaultBean addrResult = null;
				boolean hasDefault;
				Gson gson = new Gson();
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
					if (addrResult.addressList != null) {
						datas = addrResult.addressList;
						adapter = new MyAdapter(datas);
						lv_addr.setAdapter(adapter);
						// adapter.notifyDataSetChanged();
						for (AddressListBean bean : datas) {
							hasDefault = bean.IS_DEFAULT_ADDRESS == 2;
							if (hasDefault) {
								break;
							}
						}
					}
					if (datas.size() == 0) {
//						editDefaultAddr(null, null, null, null,null);
						rl_addr_null.setVisibility(View.VISIBLE);
					} else {
						rl_addr_null.setVisibility(View.GONE);
					}
				} else {
					if (addrResult.flag == 10) {
						mApplication.login();
					}
					ToastUtil.showTextToast(context, addrResult.msg);
					rl_error.setVisibility(View.VISIBLE);
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
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_error:
			mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
					"数据加载中...");
			adapter = null;
//			isUpdate = true;
//			currPage = 1;
			getData();
//			lv.doPullRefreshing(true, 500);
			break;
		case R.id.btn_gosee:
		case R.id.tv_add_addr:
			Intent intent = new Intent(context, AddAddrActivity.class);
			if (datas.size() <= 0) {
				intent.putExtra("null", true);
			}
			intent.putExtra("type", type);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	// 删除地址
	private void delAddr(final String addressId) {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"删除地址...");
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("addressId", addressId);
		params.put("type", type+"");
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				BaseResultBean baseResult = null;
				Gson gson = new Gson();
				try {
					baseResult = gson.fromJson(arg0,
							BaseResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (baseResult == null) {
					ToastUtil.showTextToast(context, "删除地址失败！");
					return;
				}
				if (baseResult.success) {
					// ToastUtil.showTextToast(context, baseResult.msg);
					getData();
				} else if (baseResult.flag == 10) {
					mApplication.login();
					// delAddr(addressId);
				} else {
					ToastUtil.showTextToast(context, baseResult.msg);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "删除地址失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.DEL_ADDR_URL, params, callback);
	}

	// 设置默认地址
	private void setDefault(final String addressId) {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"设置默认地址...");
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("addressId", addressId);
		params.put("type", type+"");
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				Gson gson = new Gson();
				BaseResultBean baseResult = null;
				try {
					baseResult = gson.fromJson(arg0,
							BaseResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (baseResult == null) {
					ToastUtil.showTextToast(context, "设置默认地址失败！");
					return;
				}
				if (baseResult.success) {
					// ToastUtil.showTextToast(context, baseResult.msg);
					getData();
				} else if (baseResult.flag == 10) {
					mApplication.login();
					// setDefault(addressId);
				} else {
					ToastUtil.showTextToast(context, baseResult.msg);
				}
				
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "设置默认地址失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.DEFAULT_ADDR_URL, params, callback);
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
        convertView = View.inflate(context, R.layout.item_addr, null);
				holder = new ViewHolder();
				holder.tv_user_name = (TextView) convertView
						.findViewById(R.id.tv_user_name);
				holder.tv_user_addr = (TextView) convertView
						.findViewById(R.id.tv_user_addr);
				holder.tv_user_phone = (TextView) convertView
						.findViewById(R.id.tv_user_phone);
				holder.tv_del = (TextView) convertView
						.findViewById(R.id.tv_del);
				holder.tv_edit = (TextView) convertView
						.findViewById(R.id.tv_edit);
				holder.cb_is_default = (TextView) convertView
						.findViewById(R.id.cb_is_default);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 填充数据
			final String addr = datas.get(position).PROVINCE_NAME
					+ datas.get(position).CITY_NAME
					+ datas.get(position).AREA_NAME;
			final int isDefault = datas.get(position).IS_DEFAULT_ADDRESS;
			holder.tv_user_name.setText(datas.get(position).NAME);
			holder.tv_user_phone.setText(datas.get(position).MOBILE);
			holder.tv_user_addr.setText(addr + datas.get(position).ADDRESS);
			switch (isDefault) {
			case 1:
				Drawable nav_up = getResources().getDrawable(
						R.drawable.good_icon_check_nor);
				nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
						nav_up.getMinimumHeight());
				holder.cb_is_default.setCompoundDrawables(nav_up, null, null,
						null);

				break;
			case 2:
				Drawable nav_down = getResources().getDrawable(
						R.drawable.good_icon_check_cur);
				nav_down.setBounds(0, 0, nav_down.getMinimumWidth(),
						nav_down.getMinimumHeight());
				holder.cb_is_default.setCompoundDrawables(nav_down, null, null,
						null);
//				editDefaultAddr(datas.get(position).NAME,
//						addr + datas.get(position).ADDRESS,
//						datas.get(position).ID, datas.get(position).MOBILE,
//						datas.get(position).PROVINCE_ID);
				break;

			default:
				break;
			}
			holder.cb_is_default.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setDefault(datas.get(position).ID);
				}
			});

			holder.tv_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SweetAlertDialog delDialog = new SweetAlertDialog(
							context, SweetAlertDialog.WARNING_TYPE);
					delDialog
							.setTitleText("确定要删除该地址？")
							.setContentText("地址删除后不可恢复！")
							.setCancelText("取消")
							.setConfirmText("删除")
							.showCancelButton(true)
							.setCancelClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												SweetAlertDialog sDialog) {
											sDialog.dismiss();
										}
									})
							.setConfirmClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												SweetAlertDialog sDialog) {
											delAddr(datas.get(position).ID);
											sDialog.dismiss();
										}
									}).show();
				}
			});
			holder.tv_edit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, AddAddrActivity.class);
					intent.putExtra("userAddrId", datas.get(position).ID);
					intent.putExtra("userName", datas.get(position).NAME);
					intent.putExtra("userPhone", datas.get(position).MOBILE);
					intent.putExtra("userArea", datas.get(position).AREA_NAME);
					intent.putExtra("userAreaId", datas.get(position).AREA_ID);
					intent.putExtra("userCity", datas.get(position).CITY_NAME);
					intent.putExtra("userCityId", datas.get(position).CITY_ID);
					intent.putExtra("userProvince",
							datas.get(position).PROVINCE_NAME);
					intent.putExtra("userProvinceId",
							datas.get(position).PROVINCE_ID);
					intent.putExtra("userAddr", datas.get(position).ADDRESS);
					intent.putExtra("type", type);
					intent.putExtra("isDefault", isDefault + "");
					startActivity(intent);

				}
			});
			return convertView;
		}

	}

	class ViewHolder {
		private TextView tv_user_name;
		private TextView tv_user_addr;
		private TextView tv_user_phone;
		private TextView tv_del;
		private TextView tv_edit;
		private TextView cb_is_default;
	}

}
