package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.OrderDetailResultBean;
import com.ksource.hbpostal.bean.OrderDetailResultBean.OrderInfoBean.GoodsListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.widgets.MyListView;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.ConvertUtil;
import com.yitao.util.DialogUtil;
import com.yitao.util.NetStateUtils;
import com.yitao.util.NumberUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 订单详情页面
 */
public class OrderDetailActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private LoadDialog mLoadDialog;
	private TextView tv_sh_name, tv_phone, tv_addr, tv_count, tv_jifen,
			tv_wl_info;
	private TextView tv_order, tv_creat_time, tv_pay_time, tv_fh_time,
			tv_order_state,tv_send_price,tv_ztwd;
	private TextView tv_left, btn_right;
	private TextView tv_yf_l, tv_yf_r;
	private MyListView lv_order_goods;
	private LinearLayout ll_deliver,ll_ztwd;
	private RelativeLayout rl_shxx;

	private String userName = "", phone = "", addr = "";
	private String orderId = "", creatTime = "", fkTime = "", fhTime = "";
	private int count = 0;
	private int jifen;
	private double money ;
	private int state;
	private double sendPrice;
	private int sendType;

	private String express = "";
	private List<GoodsListBean> datas;
	private BaseAdapter adapter;
	private String id;
	private String ztwd;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_order_detail;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_ztwd = (TextView) findViewById(R.id.tv_ztwd);
		tv_wl_info = (TextView) findViewById(R.id.tv_wl_info);
		tv_sh_name = (TextView) findViewById(R.id.tv_sh_name);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_addr = (TextView) findViewById(R.id.tv_addr);
		tv_count = (TextView) findViewById(R.id.tv_count);
		tv_jifen = (TextView) findViewById(R.id.tv_jifen);
		tv_send_price = (TextView) findViewById(R.id.tv_sendprice);
		tv_order = (TextView) findViewById(R.id.tv_order);
		tv_creat_time = (TextView) findViewById(R.id.tv_creat_time);
		tv_pay_time = (TextView) findViewById(R.id.tv_pay_time);
		tv_fh_time = (TextView) findViewById(R.id.tv_fh_time);
		tv_order_state = (TextView) findViewById(R.id.tv_order_state);
		tv_left = (TextView) findViewById(R.id.btn_left);
		btn_right = (TextView) findViewById(R.id.tv_btn_right);
		tv_yf_l = (TextView) findViewById(R.id.tv_yf_l);
		tv_yf_r = (TextView) findViewById(R.id.tv_yf_r);
		lv_order_goods = (MyListView) findViewById(R.id.lv_order_goods);
		ll_deliver = (LinearLayout) findViewById(R.id.ll_deliver);
		ll_ztwd = (LinearLayout) findViewById(R.id.ll_ztwd);
		rl_shxx = (RelativeLayout) findViewById(R.id.rl_shxx);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		tv_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		ll_deliver.setOnClickListener(this);
		//TODO 点击打开商品详情
//		lv_order_goods.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				Intent intent = new Intent(context, GoodsDetailActivity.class);
//				intent.putExtra("goodsId", datas.get(position).GOODS_ID);
//				startActivity(intent);
//			}
//		});
	}

	@Override
	public void initData() {
		tv_title.setText("订单详情");
		id = getIntent().getStringExtra("orderId");
		String token = sp.getString(ConstantValues.TOKEN, null);
		if (TextUtils.isEmpty(token)) {
			// finish();
			startActivity(new Intent(context, LoginActivity.class));
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		getData();
	}

	// 添加数据
	private void setData() {
		tv_sh_name.setText(userName);
		tv_phone.setText(phone);
		tv_addr.setText(addr);
		tv_count.setText("" + count);
//		if (sendPrice == 0) {
//			tv_send_price.setText("免邮费");
//		} else {
//		}
		if (sendPrice == 0){
			tv_yf_l.setVisibility(View.GONE);
			tv_yf_r.setVisibility(View.GONE);
			tv_send_price.setVisibility(View.GONE);
		}else{
			tv_yf_l.setVisibility(View.VISIBLE);
			tv_yf_r.setVisibility(View.VISIBLE);
			tv_send_price.setVisibility(View.VISIBLE);
			tv_send_price.setText("￥" + sendPrice);
		}
		if (money == 0) {
			tv_jifen.setText(jifen + "积分");
		} else if (jifen == 0) {
			tv_jifen.setText("￥" + NumberUtil.toDecimal2(money));
		} else {
			tv_jifen.setText("￥" + NumberUtil.toDecimal2(money) + "+" + jifen + "积分");
		}
		tv_order.setText(orderId);
		if (!TextUtils.isEmpty(express)) {
			tv_wl_info.setText(express);
		} else {
			tv_wl_info.setText("暂无物流信息");
		}
		if (!TextUtils.isEmpty(creatTime)) {
			tv_creat_time.setText(creatTime.substring(0, 4) + "-"
					+ creatTime.substring(4, 6) + "-"
					+ creatTime.substring(6, 8) + " "
					+ creatTime.substring(8, 10) + ":"
					+ creatTime.substring(10, 12));
		} else {
			tv_creat_time.setText("暂无信息");
		}
		if (!TextUtils.isEmpty(fkTime)) {
			tv_pay_time.setText(fkTime.substring(0, 4) + "-"
					+ fkTime.substring(4, 6) + "-" + fkTime.substring(6, 8)
					+ " " + fkTime.substring(8, 10) + ":"
					+ fkTime.substring(10, 12));
		} else {
			tv_pay_time.setText("暂无信息");
		}
		if (!TextUtils.isEmpty(fhTime)) {
			tv_fh_time.setText(fhTime.substring(0, 4) + "-"
					+ fhTime.substring(4, 6) + "-" + fhTime.substring(6, 8)
					+ " " + fhTime.substring(8, 10) + ":"
					+ fhTime.substring(10, 12));
		} else {
			tv_fh_time.setText("暂无信息");
		}

		if (sendType == 1){
			ll_deliver.setVisibility(View.VISIBLE);
			rl_shxx.setVisibility(View.VISIBLE);
			ll_ztwd.setVisibility(View.GONE);
		}else if (sendType == 2){
			ll_deliver.setVisibility(View.GONE);
			rl_shxx.setVisibility(View.GONE);
			ll_ztwd.setVisibility(View.VISIBLE);
			tv_ztwd.setText(ztwd);
		}

		switch (state) {
		case 0:
			tv_order_state.setText("买家未付款");
			tv_left.setVisibility(View.VISIBLE);
			btn_right.setVisibility(View.VISIBLE);
			tv_left.setText("取消订单");
			btn_right.setText("立即支付");
			break;
		case 1:
			if (sendType ==1){
				tv_order_state.setText("等待卖家发货");
			}else if(sendType == 2){
				tv_order_state.setText("等待买家自提");
			}
			tv_left.setVisibility(View.GONE);
			btn_right.setVisibility(View.GONE);
			btn_right.setText("提醒发货");
			break;
		case 2:
			if (sendType ==1){
				tv_order_state.setText("卖家已发货");
				tv_left.setVisibility(View.VISIBLE);
				tv_left.setText("查看物流");
			}else if(sendType == 2){
				tv_order_state.setText("等待买家自提");
				tv_left.setVisibility(View.GONE);
			}
			btn_right.setVisibility(View.VISIBLE);
			btn_right.setText("确认收货");
			break;
		case 3:
			if (sendType ==1){
				tv_left.setVisibility(View.VISIBLE);
				tv_left.setText("查看物流");
				tv_order_state.setText("买家已收货");
			}else if(sendType == 2){
				tv_left.setVisibility(View.GONE);
				tv_order_state.setText("买家已提货");
			}
			btn_right.setVisibility(View.GONE);
			btn_right.setText("去评价");
			break;
		case 4:
			tv_order_state.setText("订单已完成");
			tv_left.setVisibility(View.GONE);
			btn_right.setVisibility(View.VISIBLE);
			btn_right.setText("已完成");

			break;

		default:
			break;
		}

	}

	// 获取订单详情
	private void getData() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"数据加载中...");
		if (!NetStateUtils.isNetworkAvailable(context)) {
			ToastUtil.showTextToast(context, "当前网络不可用！");
			DialogUtil.getInstance().dialogDismiss(mLoadDialog);
			return;
		}
		Map<String, String> params = new HashMap<>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("orderId", id);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				Gson gson = new Gson();
				OrderDetailResultBean resultBean = null;
				try {
					resultBean = gson.fromJson(
							arg0, OrderDetailResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (resultBean == null) {
					ToastUtil.showTextToast(context, "获取订单详情失败！");
					return;
				}
				if (resultBean.success) {
					String provinceName = resultBean.orderInfo.PROVINCE_NAME;
					String cityName = resultBean.orderInfo.CITY_NAME;
					String areaName = resultBean.orderInfo.AREA_NAME;
					String address = resultBean.orderInfo.ADDRESS;
					provinceName = TextUtils.isEmpty(provinceName) ? ""
							: provinceName;
					cityName = TextUtils.isEmpty(cityName) ? ""
							: cityName;
					areaName = TextUtils.isEmpty(areaName) ? ""
							: areaName;
					address = TextUtils.isEmpty(address) ? ""
							: address;
					addr = provinceName + cityName + areaName + address;
					userName = resultBean.orderInfo.PERSON;
					phone = resultBean.orderInfo.MOBILE;
					count = resultBean.orderInfo.TOTAL_NUM;
					jifen = ConvertUtil.obj2Int(resultBean.orderInfo.TOTAL_JF);
					money = resultBean.orderInfo.TOTAL_PRICE;
					sendType = resultBean.orderInfo.SEND_TYPE;
					ztwd = resultBean.orderInfo.TAKE_PLACE;
					String sendPriceStr = resultBean.orderInfo.SEND_PRICE;
					if (TextUtils.isEmpty(sendPriceStr)) {
						sendPriceStr = "0";
					}
					sendPrice = ConvertUtil.obj2Double(sendPriceStr);
					money = NumberUtil.add(money, sendPrice);
					state = resultBean.orderInfo.STATE;
					creatTime = resultBean.orderInfo.CREATE_TIME;
					fkTime = resultBean.orderInfo.ZF_TIME;
					fhTime = resultBean.orderInfo.FH_TIME;
					orderId = resultBean.orderInfo.ORDER_ID;
					express = resultBean.orderInfo.EXPRESS_CODE;
					setData();
					datas = resultBean.orderInfo.goodsList;
					// for (int i = 0; i < datas.size(); i++) {
					// datas.get(i).STATE = state;
					// }
					adapter = new MyAdapter(datas);
					lv_order_goods.setAdapter(adapter);
				} else if (resultBean.flag == 10) {
					mApplication.login();
					// getData();
				} else {
					ToastUtil.showTextToast(context, resultBean.msg);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "获取订单详情失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.DETAIL_ORDER_URL, params, callback);
	}

	// 取消订单
	private void delOrder() {

		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"取消订单...");
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("orderId", id);
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
					new SweetAlertDialog(context,
							SweetAlertDialog.ERROR_TYPE).setTitleText(
							"删除订单失败！").show();
					return;
				}
				if (baseResult.success) {
					// ToastUtil.showTextToast(context, "确认收货成功！");
					tv_left.setText("订单已取消");
					tv_left.setEnabled(false);
					btn_right.setVisibility(View.GONE);
					new SweetAlertDialog(context,
							SweetAlertDialog.SUCCESS_TYPE)
							.setTitleText("成功！")
							.setContentText("订单已删除!").show();
				} else {
					if (baseResult.flag == 10) {
						mApplication.login();
						// confirmOrder();
					}
					new SweetAlertDialog(context,
							SweetAlertDialog.ERROR_TYPE)
							.setTitleText("删除订单失败！")
							.setContentText(baseResult.msg).show();
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				new SweetAlertDialog(context,
						SweetAlertDialog.ERROR_TYPE).setTitleText(
						"删除订单失败！").show();
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.DEL_ORDER_URL, params, callback);
	}

	// 订单确认收货
	private void confirmOrder() {

		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"确认收货...");
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("orderId", id);
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
					new SweetAlertDialog(context,
							SweetAlertDialog.ERROR_TYPE).setTitleText(
							"确认收货失败！").show();
					return;
				}
				if (baseResult.success) {
					// ToastUtil.showTextToast(context, "确认收货成功！");
					SweetAlertDialog dialog = new SweetAlertDialog(
							context, SweetAlertDialog.SUCCESS_TYPE);
					dialog.setTitleText("确认收货成功！")
							.setConfirmText("确定")
							.setConfirmClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												SweetAlertDialog sDialog) {
											sDialog.dismiss();
											getData();
										}
									}).show();
				} else {
					if (baseResult.flag == 10) {
						mApplication.login();
						// confirmOrder();
					}
					new SweetAlertDialog(context,
							SweetAlertDialog.ERROR_TYPE)
							.setTitleText("失败！")
							.setContentText(baseResult.msg).show();
				}

			
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.CONFIRM_ORDER_URL, params, callback);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.ll_deliver:
//			if (TextUtils.isEmpty(express)) {
//				ToastUtil.showTextToast(context, "暂无物流信息");
//				return;
//			}
//			Intent intentDeliver = new Intent(context, DeliverActivity.class);
//			intentDeliver.putExtra("count", ""+count);
//			intentDeliver.putExtra("imageUrl", ""+datas.get(0).IMAGE);
//			intentDeliver.putExtra("num", express);
//			startActivity(intentDeliver);
			break;
		case R.id.btn_left:
			switch (state) {
			case 0:
				SweetAlertDialog dialog = new SweetAlertDialog(context,
						SweetAlertDialog.WARNING_TYPE);
				dialog.setTitleText("确定要取消该订单？")
						.setContentText("该订单取消后不可恢复！")
						.setCancelText("先不了")
						.setConfirmText("是的，我要取消")
						.showCancelButton(true)
						.setConfirmClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.dismiss();
										delOrder();
									}
								}).show();
				break;
			case 1:
				break;
			case 2:
			case 3:
				// ToastUtil.showTextToast(context, "Sorry!暂无物流信息");
				if (TextUtils.isEmpty(express)) {
					ToastUtil.showTextToast(context, "暂无物流信息");
					return;
				}
				Intent intentDeliver = new Intent(context, DeliverActivity.class);
				intentDeliver.putExtra("count", ""+count);
				intentDeliver.putExtra("imageUrl", ""+datas.get(0).IMAGE);
				intentDeliver.putExtra("num", express);
				startActivity(intentDeliver);
				break;

			default:
				break;
			}
			break;
		case R.id.tv_btn_right:
			switch (state) {
			case 0:
				Intent intent = new Intent(context, PayActivity.class);
				intent.putExtra("orderId", id);
				intent.putExtra("money", "" + money);
				intent.putExtra("jifen", "" + jifen);
				startActivity(intent);
				break;
			case 1:
				// DialogUtil dialogUtil = DialogUtil.getInstance();
				// final TiShiOnlySureDialog dialog =
				// dialogUtil.showTiShiOnlySureDialog(context,
				// "温馨提示", "已提醒卖家发货！");
				// dialog.setOnClickTiShiOnlySureDialog(new
				// OnClickTiShiOnlySureDialog() {
				//
				// @Override
				// public void setOnclick() {
				// dialog.dismiss();
				//
				// }
				// });
				SystemClock.sleep(500);
				ToastUtil.showTextToast(context, "消息已发送！已提醒卖家发货");
				break;
			case 2:
				SweetAlertDialog dialog = new SweetAlertDialog(context,
						SweetAlertDialog.WARNING_TYPE);
				dialog.setTitleText("确定要确认收货？")
						.setContentText("确认收货收货后将向卖家付款")
						.setCancelText("取消")
						.setConfirmText("确认收货")
						.showCancelButton(true)
						.setConfirmClickListener(
								new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.dismiss();
										confirmOrder();
									}
								}).show();
				break;
			case 3:
				// if (datas.size() == 1){
				// Intent intent1 = new Intent(context,
				// OrderValueActivityForDetail.class);
				// intent1.putExtra("orderId", id);
				// intent1.putExtra("goodsBean", datas.get(0));
				// startActivity(intent1);
				// }else {
				// Intent intent1 = new Intent(context,
				// OrderListActivityForDetail.class);
				// intent1.putExtra("orderId", id);
				// Bundle bundle = new Bundle();
				// bundle.putSerializable("goodsList", (Serializable) datas);
				// intent1.putExtra("bundle", bundle);
				// startActivity(intent1);
				// }
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
	}

	class MyAdapter extends DefaultBaseAdapter<GoodsListBean> {

		private ViewHolder holder;

		public MyAdapter(List<GoodsListBean> datas) {
			super(datas);
		}

		@SuppressWarnings("finally")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			holder = null;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item_order_goods,
						null);
				holder = new ViewHolder();
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
				holder.tv_review_state = (TextView) convertView
						.findViewById(R.id.tv_review_state);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();

			}

			// 填数据
			holder.tv_name_list_item.setText(datas.get(position).GOODS_NAME);

			double useMoney = ConvertUtil.obj2Double(datas.get(position).ONE_USE_MONEY);
			int useScore = ConvertUtil.obj2Int(datas.get(position).ONE_USE_SCORE);
			int type = 0;
			if (useMoney == 0){
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

			final int reviewState = datas.get(position).REVIEW_STATE;
			if (state == 3) {
				holder.tv_review_state.setVisibility(View.VISIBLE);
				if (reviewState != 1) {
					holder.tv_review_state.setText("去评价");
				} else {
					holder.tv_review_state.setText("已评价");
				}
			} else {
				holder.tv_review_state.setVisibility(View.GONE);
			}
			holder.tv_review_state.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (reviewState != 1) {
						Intent intent = new Intent(context,
								OrderValueActivityForDetail.class);
						intent.putExtra("goodsBean", datas.get(position));
						startActivity(intent);
					} else {
						ToastUtil.showTextToast(context, "亲！该商品已评价过了哦");
					}
				}
			});

			holder.tv_goods_number.setText("×" + datas.get(position).BUY_NUM);
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
		private ImageView iv_goods_icon;
		private TextView tv_name_list_item;
		private TextView tv_goods_jifen;
		private TextView tv_cate_name;
		private TextView tv_add;
		private TextView tv_review_state;
		private TextView tv_jifen_name;
		private TextView tv_goods_price;
		private TextView tv_goods_number;
	}

}
