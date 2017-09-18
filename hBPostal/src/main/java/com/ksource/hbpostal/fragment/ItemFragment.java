package com.ksource.hbpostal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.activity.JFStep1;
import com.ksource.hbpostal.activity.JFStep2;
import com.ksource.hbpostal.activity.SJCZActivity;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.HomeListResultBean;
import com.ksource.hbpostal.bean.HomeListResultBean.PamentAccountListBean;
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

public class ItemFragment extends BaseFragment implements OnClickListener {

	private PullToRefreshListView lv;
	private ListView lv_shjf;
	private TextView tv_edit;
	private RelativeLayout rl_error;

	private boolean isEdit = false;
	private boolean isNotBind = false;
	private boolean isHasAccNum = false;
	private BaseAdapter adapter;
	// private Map<Integer, SHJFItem> map;
	// 获取信息列表
	// private List<PamentAccountListBean> datas;
	ViewHolder holder = null;
	private int type;
	// 展示列表
	private List<PamentAccountListBean> lists;
	private LoadDialog mLoadDialog;

	private String token;
	private String homeId;
	private String homeName;

	@Override
	public View initView() {
		return View.inflate(context, R.layout.fragment_item, null);
	}

	@Override
	public void onStart() {
		super.onStart();
//		tv_edit.setVisibility(View.GONE);
		if (isEdit) {
			tv_edit.setText("取消编辑");
		} else {
			tv_edit.setText("编辑");
		}
	}

	@Override
	public void initData() {
		// datas = new ArrayList<>();
		token = sp.getString(ConstantValues.TOKEN, "");
		// 获取Activity传递过来的参数
		Bundle mBundle = getArguments();
		type = mBundle.getInt("type");
		homeId = mBundle.getString("homeId");
		homeName = mBundle.getString("homeName");
		lv = (PullToRefreshListView) view.findViewById(R.id.lv_shjf);
		tv_edit = (TextView) view.findViewById(R.id.tv_edit);
		rl_error = (RelativeLayout) view.findViewById(R.id.rl_error);
		tv_edit.setOnClickListener(this);

		lv.setPullRefreshEnabled(true);
		lv.setPullLoadEnabled(false);
		lv.setScrollLoadEnabled(true);
		lv.setHasMoreData(false);
		lv_shjf = lv.getRefreshableView();
		lv_shjf.setDivider(null);
		// getData();
		lv_shjf.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int accountType = lists.get(position).accountType;
				if (accountType == 1 ) {
					ToastUtil.showTextToast(context, "暂不支持该缴费！敬请期待");
					return;
				}
				if (!TextUtils.isEmpty(lists.get(position).accountNumber)) {
					// 跳转到缴费/添加缴费账户界面
					if (accountType == 5) {
						Intent intent = new Intent(context, SJCZActivity.class);
						String key = lists.get(position).key;
						if (key.equals("ZGDX")) {
							intent.putExtra("operator", 5);
						} else if (key.equals("ZGLT")) {
							intent.putExtra("operator", 4);
						}
						intent.putExtra("accountNumber",
								lists.get(position).accountNumber);
						startActivity(intent);
					} else {
						Intent intent = new Intent(context, JFStep2.class);
						intent.putExtra("accountType", accountType);
						intent.putExtra("accountId",
								lists.get(position).accountId);
						startActivity(intent);
					}
				} else {
					Intent intent = new Intent(context, JFStep1.class);
					intent.putExtra("accountType", accountType);

					if (!TextUtils.isEmpty(homeId)) {
						intent.putExtra("homeId", homeId);
					}
					if (!TextUtils.isEmpty(homeName)) {
						intent.putExtra("homeName", homeName);
					}
					startActivityForResult(intent,1001);
				}

			}
		});
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
		lv.doPullRefreshing(true, 100);
	}

	// 获取家庭缴费信息
	private void getData() {

		if (TextUtils.isEmpty(homeId)){
			rl_error.setVisibility(View.VISIBLE);
			return;
		}
		rl_error.setVisibility(View.GONE);
		// mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("homeId", homeId);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				HomeListResultBean homeListResultBean = null;
				Gson gson = new Gson();
				try {
					homeListResultBean = gson.fromJson(arg0,
							HomeListResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (homeListResultBean == null) {
					ToastUtil.showTextToast(context, "获取信息失败！");
				} else {
					if (homeListResultBean.success) {
						lists = new ArrayList<>();
						for (int i = 0; i < homeListResultBean.pamentAccountList
								.size(); i++) {
							if (homeListResultBean.pamentAccountList.get(i).accountType != 1) {
								if (homeListResultBean.pamentAccountList
										.get(i).accountType == type) {
									lists.add(
											0,
											homeListResultBean.pamentAccountList
													.get(i));
								} else {
									lists.add(homeListResultBean.pamentAccountList
											.get(i));
								}
							}
							if (!TextUtils
									.isEmpty(homeListResultBean.pamentAccountList
											.get(i).accountNumber)) {
								isHasAccNum = true;
							}
						}
						if (isHasAccNum) {
							tv_edit.setVisibility(View.VISIBLE);
						} else {
							tv_edit.setVisibility(View.GONE);
						}
						adapter = new MyAdapter(lists);
						lv_shjf.setAdapter(adapter);
					} else {
						if (homeListResultBean.flag == 10) {
							mApplication.login();
						} else {
							ToastUtil.showTextToast(context,
									"获取家庭失败！");
						}
					}
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "获取信息失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_HOME_LIST_URL, params, callback);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_edit:
			isEdit = !isEdit;
			if (isEdit) {
				tv_edit.setText("取消编辑");
			} else {
				tv_edit.setText("编辑");
			}
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}

	}

	class MyAdapter extends DefaultBaseAdapter<PamentAccountListBean> {

		private ViewHolder holder;

		public MyAdapter(List<PamentAccountListBean> datas) {
			super(datas);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.item_shjf_listview, null);
				holder = new ViewHolder();
				holder.itemIcon = (ImageView) convertView
						.findViewById(R.id.iv_item_icon);
				holder.itemName = (TextView) convertView
						.findViewById(R.id.tv_item_name);
				holder.bindNum = (TextView) convertView
						.findViewById(R.id.tv_number);
				holder.ll_chioce = (LinearLayout) convertView
						.findViewById(R.id.ll_chioce);
				holder.ll_edit = (LinearLayout) convertView
						.findViewById(R.id.ll_edit);
				holder.tv_update = (TextView) convertView
						.findViewById(R.id.tv_update);
				holder.tv_delete = (TextView) convertView
						.findViewById(R.id.tv_delete);
				holder.tv_chioce = (TextView) convertView
						.findViewById(R.id.tv_chioce);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tv_update.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// ToastUtil.showTextToast(context, "去编辑");
					Intent intent = new Intent(context, JFStep1.class);
					intent.putExtra("bindNum",
							datas.get(position).accountNumber);
					intent.putExtra("homeId", homeId);
					intent.putExtra("homeName", homeName);
					intent.putExtra("accountType",
							datas.get(position).accountType);
					intent.putExtra("accountId", datas.get(position).accountId);
					intent.putExtra("payUnitKey", datas.get(position).key);
					intent.putExtra("payUnitName",
							datas.get(position).keyName);
					startActivityForResult(intent,1001);

				}
			});
			holder.tv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SweetAlertDialog dialog = new SweetAlertDialog(
							context, SweetAlertDialog.WARNING_TYPE);
					dialog.setTitleText("确定要删除该账号？")
							.showCancelButton(true)
							.setCancelText("取消")
							.setConfirmText("删除")
							.setConfirmClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												SweetAlertDialog sweetAlertDialog) {
											sweetAlertDialog.dismiss();
											isEdit = false;
											tv_edit.setText("编辑");
											delAccount(datas.get(position).accountId);
										}

									}).show();
				}
			});

			int accountType = datas.get(position).accountType;
			// 填充数据
			switch (accountType) {
			case 1:
//				holder.itemIcon
//						.setImageResource(R.drawable.hisytoy_button_water);
//				holder.itemName.setText("水费");
				break;
			case 2:
				holder.itemIcon.setImageResource(R.drawable.hisytoy_button_ele);
				holder.itemName.setText("电费");
				break;
			case 3:
				holder.itemIcon.setImageResource(R.drawable.hisytoy_button_gas);
				holder.itemName.setText("燃气费");
				break;
			case 4:
				holder.itemIcon.setImageResource(R.drawable.hisytoy_button_tv);
				holder.itemName.setText("有线电视");
				break;
			case 5:
				holder.itemIcon
						.setImageResource(R.drawable.hisytoy_button_broad);
				holder.itemName.setText("固话缴费");
				break;

			default:
				holder.itemIcon.setVisibility(View.GONE);
				holder.itemName.setVisibility(View.GONE);
				break;
			}
			// 户号,户名
			String bindNumStr = datas.get(position).accountNumber;
			String bindNameStr = datas.get(position).accountName;

			isNotBind = TextUtils.isEmpty(bindNumStr);
			if (isNotBind) {
				// tv_edit.setVisibility(View.GONE);
				holder.tv_chioce.setVisibility(View.VISIBLE);
				holder.bindNum.setVisibility(View.GONE);
			} else {
				// tv_edit.setVisibility(View.VISIBLE);
				holder.bindNum.setVisibility(View.VISIBLE);
				holder.tv_chioce.setVisibility(View.GONE);
				holder.bindNum.setText(bindNumStr );//+ " | " + bindNameStr
				if (!isEdit) {
					holder.ll_chioce.setVisibility(View.VISIBLE);
					holder.ll_edit.setVisibility(View.GONE);
				} else {
					holder.ll_chioce.setVisibility(View.GONE);
					holder.ll_edit.setVisibility(View.VISIBLE);
				}
			}

			return convertView;
		}

	}

	class ViewHolder {
		private ImageView itemIcon;
		private TextView itemName, bindNum;
		private LinearLayout ll_chioce, ll_edit;
		private TextView tv_update, tv_delete, tv_chioce;
	}

	// 解绑缴费号
	private void delAccount(final String accountId) {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("accountId", accountId);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				BaseResultBean baseBean = null;
				Gson gson = new Gson();
				try {
					baseBean = gson.fromJson(arg0,
							BaseResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (baseBean == null) {
					ToastUtil.showTextToast(context, "解绑账户失败失败！");
				} else {
					if (baseBean.success) {
						lv.doPullRefreshing(true, 100);
					} else {
						if (baseBean.flag == 10) {
							mApplication.login();
						}
					}
					ToastUtil.showTextToast(context, baseBean.msg);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "解绑账户失败失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.DEL_JF_ACCOUNT_URL, params, callback);
	}

}