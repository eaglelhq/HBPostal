package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.CartResultBean;
import com.ksource.hbpostal.bean.CartResultBean.GoodsBean;
import com.ksource.hbpostal.bean.CountResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.ConvertUtil;
import com.yitao.util.DialogUtil;
import com.yitao.util.NumberUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 购物车
 */
public class ActivityCart extends BaseActivity {

	private TextView tv_title, tv_right;
	private ImageView iv_back;
	private CheckBox cb_chioce;
	private TextView tv_gongji, tv_count;
	private Button btn_gosee;
	private PullToRefreshListView lv;
	private ListView lv_cart;
	private TextView btn_delete, btn_clearing;
	private RelativeLayout rl_error, rl_cart_null;

	private BaseAdapter adapter;
	private boolean isEdit = false;
	// private boolean chioceAll = true;
	private List<GoodsBean> datas = new ArrayList<GoodsBean>();
	private double jifen;
	private double count;
	private LoadDialog mLoadDialog;
	private String cartIds = "";

	private List<String> chioceIds = new ArrayList<String>();
	private List<GoodsBean> chioceList = new ArrayList<GoodsBean>();
	private int num;

	private void setText() {
		chioceIds.clear();
		chioceList.clear();
		count = 0;
		jifen = 0;
		num = 0;
		if (datas != null) {
			// DecimalFormat f = new DecimalFormat("0.00");
			// count = Float.parseFloat(f.format(count));
			for (GoodsBean bean : datas) {
				if (bean.isChioce) {
					chioceIds.add(bean.CART_ID);
					chioceList.add(bean);
					num++;
					count = NumberUtil.add(count, NumberUtil.mul(
							ConvertUtil.obj2Double(bean.PRICE),
							ConvertUtil.obj2Double(bean.NUMBER)));
					jifen = NumberUtil.add(jifen, NumberUtil.mul(
							ConvertUtil.obj2Double(bean.INTEGRAL),
							ConvertUtil.obj2Double(bean.NUMBER)));
				}
				// chioceAll = chioceAll && bean.isChioce;
			}
			// cb_chioce.setChecked(chioceAll);
		}
		System.out.println(count);
		BigDecimal jifenBig = NumberUtil.doubleToLong(jifen);
		String countBig = NumberUtil.toDecimal2(count);
		if (jifen == 0) {
			tv_count.setText("￥" + NumberUtil.toDecimal2(count));
		} else if (count == 0) {
			tv_count.setText(ConvertUtil.obj2Int(jifen) + "积分");
		} else {
			tv_count.setText("￥" + NumberUtil.toDecimal2(count) + "+" + ConvertUtil.obj2Int(jifen) + "积分");
		}
		btn_clearing.setText(" 结算(" + num + ") ");
	}

	@Override
	public void onResume() {
		super.onResume();
		cb_chioce.setChecked(false);
		datas = null;
		lv.doPullRefreshing(true, 100);
	}

	@Override
	public int getLayoutResId() {
		return R.layout.fragment_cart;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_right = (TextView) findViewById(R.id.btn_right);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		rl_error = (RelativeLayout) findViewById(R.id.rl_error);
		rl_cart_null = (RelativeLayout) findViewById(R.id.rl_cart_null);
		cb_chioce = (CheckBox) findViewById(R.id.cb_chioce);
		tv_count = (TextView) findViewById(R.id.tv_count);
		tv_gongji = (TextView) findViewById(R.id.tv_gongji);
		btn_clearing = (TextView) findViewById(R.id.btn_clearing);
		btn_delete = (TextView) findViewById(R.id.btn_delete);
		btn_gosee = (Button) findViewById(R.id.btn_gosee);
		lv = (PullToRefreshListView) findViewById(R.id.lv_cart);

	}

	@Override
	public void initListener() {
		rl_error.setOnClickListener(this);
	}

	@Override
	public void initData() {
		token = sp.getString(ConstantValues.TOKEN, null);
		// 头部
		tv_title.setText("购物车");
		if (isEdit) {
			tv_right.setText("保存");
		} else {
			tv_right.setText("编辑");
		}
		tv_right.setTextColor(Color.GRAY);
		tv_right.setOnClickListener(this);
		tv_right.setVisibility(View.VISIBLE);
		iv_back.setVisibility(View.GONE);
		// 底部
		cb_chioce.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// for (GoodsBean bean : datas) {
				// bean. = isChecked;
				// }
				// chioceAll = isChecked;
				for (GoodsBean bean : datas) {
					bean.isChioce = isChecked;
				}
				adapter.notifyDataSetChanged();
				setText();
			}

		});
		btn_gosee.setOnClickListener(this);
		btn_clearing.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
		// 内容
		lv.setPullRefreshEnabled(true);
		lv.setPullLoadEnabled(false);
		lv.setScrollLoadEnabled(true);
		lv.setHasMoreData(false);
		lv_cart = lv.getRefreshableView();
		lv_cart.setDivider(null);
		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				lv.onPullDownRefreshComplete();
				lv.onPullUpRefreshComplete();
				lv.setHasMoreData(false);
				// clearData();
				cb_chioce.setChecked(false);
				getData();
				setText();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {

			}
		});
		lv_cart.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(context, GoodsDetailActivity.class);
				intent.putExtra("goodsId", datas.get(position).GOODS_ID);
				startActivity(intent);

			}
		});
		if (TextUtils.isEmpty(token)) {
			// getActivity().finish();
			startActivity(new Intent(context, LoginActivity.class));
			return;
		}

	}

	// 获取购物车数据
	private void getData() {
		Map<String, String> params = new HashMap<String, String>();
		if (!TextUtils.isEmpty(token)) {
			params.put("token", token);
		}
		
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				CartResultBean result = null;
				Gson gson = new Gson();
				try {
					result = gson.fromJson(arg0,
							CartResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (result == null) {
					tv_right.setVisibility(View.GONE);
					rl_error.setVisibility(View.VISIBLE);
					return;
				}
				if (result.success) {
					rl_error.setVisibility(View.GONE);
					datas = result.goods;
					if (datas != null && datas.size() > 0) {
						rl_cart_null.setVisibility(View.GONE);
						adapter = new MyAdapter(datas);
						lv_cart.setAdapter(adapter);
						tv_right.setVisibility(View.VISIBLE);
					} else {
						rl_cart_null.setVisibility(View.VISIBLE);
						tv_right.setVisibility(View.GONE);
					}
				} else if (result.flag == 10) {
					mApplication.login();
					// getData();
				} else {
					ToastUtil.showTextToast(context, result.msg);
					rl_error.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				tv_right.setVisibility(View.GONE);
				rl_error.setVisibility(View.VISIBLE);
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.MY_CART_URL, params, callback);
	}

	class MyAdapter extends DefaultBaseAdapter<GoodsBean> {

		private ViewHolder holder;
		private int[] kcNums;


		public MyAdapter(List<GoodsBean> datas) {
			super(datas);
			// nums = new int[datas.size()];
			kcNums = new int[datas.size()];
			for (int i = 0; i < datas.size(); i++) {
				// nums[i] = datas.get(i).NUMBER;
				kcNums[i] = 1000;// datas.get(i).KC;
			}
		}

		@SuppressWarnings("finally")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			holder = null;
			Map<Integer, View> mHashMap = new HashMap<Integer, View>();
			if (mHashMap.get(position) == null) {
				convertView = View.inflate(context, R.layout.item_cart_goods,
						null);
				holder = new ViewHolder();
				holder.cb_chioce = (CheckBox) convertView
						.findViewById(R.id.cb_chioce);
				holder.tv_name_list_item = (TextView) convertView
						.findViewById(R.id.tv_name_list_item);
				holder.iv_goods_icon = (ImageView) convertView
						.findViewById(R.id.iv_goods_icon);
				holder.tv_goods_jifen = (TextView) convertView
						.findViewById(R.id.tv_goods_jifen);
				holder.tv_jifen_name = (TextView) convertView
						.findViewById(R.id.tv_jifen_name);
				holder.tv_goods_price = (TextView) convertView
						.findViewById(R.id.tv_goods_price);
				holder.tv_add = (TextView) convertView
						.findViewById(R.id.tv_add);
				holder.tv_goods_number = (TextView) convertView
						.findViewById(R.id.tv_goods_number);
				holder.tv_cate_name = (TextView) convertView
						.findViewById(R.id.tv_cate_name);
				holder.ll_edit = (LinearLayout) convertView
						.findViewById(R.id.ll_edit);
				holder.et_number = (EditText) convertView
						.findViewById(R.id.et_number);
				holder.btn_sub = (Button) convertView
						.findViewById(R.id.btn_sub);
				holder.btn_add = (Button) convertView
						.findViewById(R.id.btn_add);
				mHashMap.put(position, convertView);
				convertView.setTag(holder);

			} else {
				convertView = mHashMap.get(position);
				holder = (ViewHolder) convertView.getTag();

			}

			// 填数据
			holder.cb_chioce.setChecked(datas.get(position).isChioce);
			holder.cb_chioce
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							datas.get(position).isChioce = isChecked;
							adapter.notifyDataSetChanged();
							setText();
						}
					});
			holder.tv_name_list_item.setText(datas.get(position).NAME);
			final boolean isNcp = datas.get(position).IS_NCP == 1 ;
			double useMoney = ConvertUtil.obj2Double(datas.get(position).PRICE);
			int useScore = ConvertUtil.obj2Int(datas.get(position).INTEGRAL);
			int type = 1;
			if (useMoney == 0.0){
				type = 3;
			}else if (useScore == 0){
				type = 1;
			}else{
				type = 2;
			}
			switch (type) {
				case 1:
					holder.tv_goods_price.setText("￥" +  NumberUtil.toDecimal2(useMoney));
					holder.tv_add.setVisibility(View.GONE);
					holder.tv_jifen_name.setVisibility(View.GONE);
					holder.tv_goods_jifen.setVisibility(View.GONE);
					break;
				case 2:
					holder.tv_goods_price.setText("￥" +  NumberUtil.toDecimal2(useMoney));
					holder.tv_add.setVisibility(View.VISIBLE);
					holder.tv_goods_jifen.setVisibility(View.VISIBLE);
					holder.tv_goods_jifen.setText(useScore + "");
					holder.tv_jifen_name.setVisibility(View.VISIBLE);
					break;
				case 3:
					holder.tv_goods_price.setVisibility(View.GONE);
					holder.tv_add.setVisibility(View.GONE);
					holder.tv_goods_jifen.setText(useScore + "");
					holder.tv_goods_jifen.setVisibility(View.VISIBLE);
					holder.tv_jifen_name.setVisibility(View.VISIBLE);

					break;

				default:
					break;
			}

			if (isEdit) {
				holder.ll_edit.setVisibility(View.VISIBLE);
				holder.et_number.setText("" + datas.get(position).NUMBER);
				if (kcNums[position] > 0) {
					holder.btn_add.setEnabled(true);
					holder.btn_sub.setEnabled(true);
				} else {
					holder.btn_add.setEnabled(false);
					holder.btn_sub.setEnabled(false);
				}

			} else {
				holder.ll_edit.setVisibility(View.GONE);
			}
			holder.et_number.setText("" + datas.get(position).NUMBER);
			holder.btn_add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isNcp) {
						datas.get(position).NUMBER = 1;
						ToastUtil.showTextToast(context, "该商品每人限购一件!");
					} else {
						if (datas.get(position).NUMBER >= kcNums[position]) {
							datas.get(position).NUMBER = kcNums[position];
							// holder.et_number.setText("" + kcNum);
						} else {
							datas.get(position).NUMBER++;
						}
					}
					holder.et_number.setText("" + datas.get(position).NUMBER);
					holder.tv_goods_number.setText("×"
							+ datas.get(position).NUMBER);
					// datas.get(position).NUMBER = nums[position];
					adapter.notifyDataSetChanged();
				}
			});
			holder.btn_sub.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (datas.get(position).NUMBER <= 1) {
						datas.get(position).NUMBER = 1;
						// holder.et_number.setText("" + 1);
					} else {
						datas.get(position).NUMBER--;
					}
					holder.et_number.setText("" + datas.get(position).NUMBER);
					holder.tv_goods_number.setText("×"
							+ datas.get(position).NUMBER);
					adapter.notifyDataSetChanged();
				}
			});
			holder.tv_goods_number.setText("×" + datas.get(position).NUMBER);
			holder.tv_cate_name.setText(datas.get(position).LV1_NAME + " "
					+ datas.get(position).LV2_NAME + " "
					+ datas.get(position).LV3_NAME);
			try {
				ImageLoaderUtil.loadNetPic(
						ConstantValues.BASE_URL + datas.get(position).IMAGE,
						holder.iv_goods_icon);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				return convertView;
			}
		}

	}

	class ViewHolder {
		private CheckBox cb_chioce;
		private ImageView iv_goods_icon;
		private TextView tv_name_list_item;
		private TextView tv_goods_price;
		private TextView tv_add;
		private TextView tv_goods_jifen;
		private TextView tv_jifen_name;
		private TextView tv_cate_name;
		private TextView tv_goods_number;
		private LinearLayout ll_edit;
		private Button btn_sub, btn_add;
		private EditText et_number;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_clearing:
			if (chioceIds.size() == 0) {
				ToastUtil.showTextToast(context, "你还没选择商品呢！");
				return;
			}
			// ToastUtil.showTextToast(context, "去结算");
			Intent intent = new Intent(context, ConfirmOrderForCart.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("orderList", (Serializable) chioceList);
			intent.putExtra("bundle", bundle);
			startActivity(intent);
			break;
		case R.id.btn_delete:
			if (chioceIds.size() == 0) {
				ToastUtil.showTextToast(context, "你还没选择商品呢！");
				return;
			}
			SweetAlertDialog delDialog = new SweetAlertDialog(context,
					SweetAlertDialog.WARNING_TYPE);
			delDialog
					.setTitleText("确定删除选中商品？")
					.setContentText("商品删除后不可恢复！")
					.setCancelText("取消")
					.setConfirmText("删除")
					.showCancelButton(true)
					.setCancelClickListener(
							new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									sDialog.dismiss();
								}
							})
					.setConfirmClickListener(
							new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									removeDatas();
									sDialog.dismiss();
								}
							}).show();
			break;
		case R.id.btn_right:
			setText();
			// clearData();
			isEdit = !isEdit;
			cb_chioce.setChecked(false);
			if (isEdit) {
				tv_right.setText("保存");
				tv_gongji.setVisibility(View.INVISIBLE);
				tv_count.setVisibility(View.INVISIBLE);
				btn_delete.setVisibility(View.VISIBLE);
				btn_clearing.setVisibility(View.GONE);
			} else {
				// chioceAll = false;
				tv_right.setText("编辑");
				tv_gongji.setVisibility(View.VISIBLE);
				tv_count.setVisibility(View.VISIBLE);
				btn_delete.setVisibility(View.GONE);
				btn_clearing.setVisibility(View.VISIBLE);
			}
			adapter.notifyDataSetChanged();
			break;

		case R.id.btn_gosee:
//			if (MainActivity.rg_home != null) {
//				// getActivity().getSupportFragmentManager().beginTransaction()
//				// .replace(R.id.fl_content, new HomeFragment()).commit();
//				MainActivity.rg_home.check(R.id.rb_cate);
//			}
			startActivity(new Intent(context,ActivityCate.class));
			// MainActivity.mTabHost.setCurrentTabByTag(MainActivity.TAB_INDEX);
			break;
		case R.id.rl_error:
			mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
					"数据加载中...");
			getData();
			break;
		default:
			break;
		}
	}

	// 删除商品
	private void removeDatas() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"删除商品...");
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < chioceIds.size(); i++) {
			cartIds += chioceIds.get(i) + ",";
		}
		cartIds.substring(0, cartIds.length() - 1);
		params.put("token", token);
		params.put("cartIds", cartIds);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				BaseResultBean result = null;
					Gson gson = new Gson();
					try {
						result = gson.fromJson(arg0,
								BaseResultBean.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (result == null) {
						ToastUtil.showTextToast(context, "商品删除失败！");
						return;
					}
					if (result.success) {
						ToastUtil.showTextToast(context, result.msg);
						chioceIds.clear();
						cartIds = "";
						getData();
						getCartNum();
					} else {
						if (result.flag == 10) {
							mApplication.login();
						}
						ToastUtil.showTextToast(context, result.msg);
					}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "商品删除失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.DEL_CART_URL, params, callback);
	}

	private long mExitTime = 0;
	private String token;

	// 获取购物车数量
	private void getCartNum() {
		Map<String, String> params = new HashMap<String, String>();
		token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				CountResultBean cartCountResult = null;
				Gson gson = new Gson();
				try {
					cartCountResult = gson.fromJson(arg0,
							CountResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (cartCountResult == null) {
					ToastUtil.showTextToast(context, "获取购物车数量失败！");
					return;
				}
				if (cartCountResult.success) {
					int cartCount = cartCountResult.cartCount;
					if (cartCount > 0) {
						MainActivity.tv_cart_num.setVisibility(View.VISIBLE);
						MainActivity.tv_cart_num.setText("" + cartCount);
					} else {
						MainActivity.tv_cart_num.setVisibility(View.GONE);
					}
				} else if (cartCountResult.flag == 10) {
					mApplication.login();
				} else {
					ToastUtil.showTextToast(context,
							cartCountResult.msg);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				ToastUtil.showTextToast(context, "获取购物车数量失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.CART_COUNT_URL, params, callback);
	}

	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - mExitTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			mExitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}
}
