package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BrandResultBean;
import com.ksource.hbpostal.bean.SearchResultBean;
import com.ksource.hbpostal.bean.SearchResultBean.GoodsListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.widgets.MyGridView;
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

/**
 * 商品分类列表，第三级分类，商品搜索结果
 */
public class CateGoryListActivity extends BaseActivity implements
		OnClickListener {

	DrawerLayout dl;
	LinearLayout rlRight;
	private RelativeLayout rl_search_null;
	private ImageView iv_back, iv_right;
	private EditText et_search;
	private TextView tv_all, tv_sale, tv_price, tv_filter;
	private RadioButton tv_only_jifen, tv_only_money, tv_jifen_and_money;
	private EditText et_low_price, et_high_price, et_low_jifen, et_high_jifen;
	private Button btn_reset, btn_finish;
	private PullToRefreshListView lv;
	private ListView lv_goods_list;
	private BaseAdapter adapter;
	private List<GoodsListBean> datas;
	private LoadDialog mLoadDialog;
	private MyGridView gv_grade;
	private List<Map<String, String>> brandList;
	private BaseAdapter brandAdapter;
	private RelativeLayout rl_error;

	private boolean isUp = true;
	private String keyWorld;
	private String cateId;
	private String arrange;
	private String arrangeKey;
	private String brand;
	private String buyType;
	private int beginPrice;
	private int endPrice;
	private int beginScore;
	private int endScore;
	private int chioceItem = 1000;

	private int currPage = 1;
	private boolean isUpdate;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_category_list;
	}

	@Override
	public void initView() {
		datas = new ArrayList<>();
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		et_search = (EditText) findViewById(R.id.et_search);
		tv_all = (TextView) findViewById(R.id.tv_all);
		tv_sale = (TextView) findViewById(R.id.tv_sale);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_filter = (TextView) findViewById(R.id.tv_filter);
		rl_search_null = (RelativeLayout) findViewById(R.id.rl_search_null);
		lv = (PullToRefreshListView) findViewById(R.id.lv_goods_list);

		dl = (DrawerLayout) findViewById(R.id.drawerlayout);
		rlRight = (LinearLayout) findViewById(R.id.right);
		rl_error = (RelativeLayout) findViewById(R.id.rl_error);

		tv_only_jifen = (RadioButton) findViewById(R.id.tv_only_jifen);
		tv_only_money = (RadioButton) findViewById(R.id.tv_only_money);
		tv_jifen_and_money = (RadioButton) findViewById(R.id.tv_jifen_and_money);
		et_low_price = (EditText) findViewById(R.id.et_low_price);
		et_high_price = (EditText) findViewById(R.id.et_high_price);
		et_low_jifen = (EditText) findViewById(R.id.et_low_jifen);
		et_high_jifen = (EditText) findViewById(R.id.et_high_jifen);
		btn_finish = (Button) findViewById(R.id.btn_finish);
		btn_reset = (Button) findViewById(R.id.btn_reset);
		gv_grade = (MyGridView) findViewById(R.id.gv_grade);
		// 关闭手势滑动
		dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		dl.setDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {

			}

			@Override
			public void onDrawerOpened(View drawerView) {
				drawerView.setClickable(true);
				// 打开手势滑动
				dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				// 关闭手势滑动
				dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			}

			@Override
			public void onDrawerStateChanged(int newState) {

			}
		});

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		iv_right.setOnClickListener(this);
		tv_all.setOnClickListener(this);
		tv_sale.setOnClickListener(this);
		tv_price.setOnClickListener(this);
		tv_filter.setOnClickListener(this);
		tv_only_jifen.setOnClickListener(this);
		tv_only_money.setOnClickListener(this);
		tv_jifen_and_money.setOnClickListener(this);
		btn_reset.setOnClickListener(this);
		btn_finish.setOnClickListener(this);
		rl_error.setOnClickListener(this);
		lv.setPullRefreshEnabled(true);
		lv.setPullLoadEnabled(false);
		lv.setScrollLoadEnabled(true);
		lv_goods_list = lv.getRefreshableView();
		lv_goods_list.setDivider(null);
		lv_goods_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(context, GoodsDetailActivity.class);
				intent.putExtra("goodsId", datas.get(position).ID);
				startActivity(intent);
			}
		});
		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				isUpdate = true;
				currPage = 1;
				searchGoods();
				// searchGoods(currPage);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				isUpdate = false;
				currPage++;
				searchGoods();
				// searchGoods(currPage++);
			}
		});
		gv_grade.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gv_grade.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				chioceItem = position;
				brandAdapter.notifyDataSetChanged();
				brand = brandList.get(position).get("BRAND");
			}
		});
	}

	@Override
	public void initData() {

		cateId = getIntent().getStringExtra("cateId");
		keyWorld = getIntent().getStringExtra("keyWorld");
		et_search.setText(keyWorld);
		// searchGoods();
		currPage = 1;
		adapter = null;
		lv.doPullRefreshing(true, 500);

	}

	@Override
	public void onResume() {
		super.onResume();
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
			rl_error.setVisibility(View.GONE);
			currPage = 1;
			adapter = null;
			lv.doPullRefreshing(true, 500);
			break;
		case R.id.iv_right:
			// ToastUtil.showTextToast(context, "搜索");
			keyWorld = et_search.getText().toString().trim();
			resetData();
			// searchGoods();
			currPage = 1;
			adapter = null;
			lv.doPullRefreshing(true, 500);
			break;
		case R.id.tv_all:
			// ToastUtil.showTextToast(context, "综合排序");
			tv_all.setTextColor(Color.rgb(255, 84, 0));
			tv_sale.setTextColor(Color.GRAY);
			tv_price.setTextColor(Color.GRAY);
			tv_filter.setTextColor(Color.GRAY);
			arrangeKey = "";
			arrange = "";
			// resetData();
			// searchGoods();
			currPage = 1;
			adapter = null;
			lv.doPullRefreshing(true, 500);

			break;
		case R.id.tv_sale:
			// ToastUtil.showTextToast(context, "已按销量排序");
			tv_sale.setTextColor(Color.rgb(255, 84, 0));
			tv_all.setTextColor(Color.GRAY);
			tv_price.setTextColor(Color.GRAY);
			tv_filter.setTextColor(Color.GRAY);
			arrangeKey = "buyNum";
			arrange = "2";
			// resetData();
			// searchGoods();
			currPage = 1;
			adapter = null;
			lv.doPullRefreshing(true, 500);

			break;
		case R.id.tv_price:
			tv_price.setTextColor(Color.rgb(255, 84, 0));
			tv_sale.setTextColor(Color.GRAY);
			tv_all.setTextColor(Color.GRAY);
			tv_filter.setTextColor(Color.GRAY);
			arrangeKey = "price";
			isUp = !isUp;
			// resetData();
			if (isUp) {
				// ToastUtil.showTextToast(context, "价格升序");
				Drawable nav_up = getResources().getDrawable(
						R.drawable.goods_price_low);
				nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
						nav_up.getMinimumHeight());
				tv_price.setCompoundDrawables(null, null, nav_up, null);
				arrange = "1";
			} else {
				// ToastUtil.showTextToast(context, "价格降序");
				Drawable nav_up = getResources().getDrawable(
						R.drawable.goods_price_high);
				nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
						nav_up.getMinimumHeight());
				tv_price.setCompoundDrawables(null, null, nav_up, null);
				arrange = "2";
			}
			// searchGoods();
			currPage = 1;
			adapter = null;
			lv.doPullRefreshing(true, 500);

			break;
		case R.id.tv_filter:
			// ToastUtil.showTextToast(context, "筛选");
			resetData();
			tv_filter.setTextColor(Color.rgb(255, 84, 0));
			tv_sale.setTextColor(Color.GRAY);
			tv_price.setTextColor(Color.GRAY);
			tv_all.setTextColor(Color.GRAY);
			if (!dl.isDrawerOpen(rlRight)) {
				dl.openDrawer(rlRight);
			}
			getBrand(cateId);
			break;
		case R.id.tv_only_jifen:
			tv_only_jifen.setBackgroundResource(R.drawable.rect_green_bk);
			tv_only_money.setBackgroundResource(R.drawable.rect_gary_bk);
			tv_jifen_and_money.setBackgroundResource(R.drawable.rect_gary_bk);
			buyType = "3";
			break;
		case R.id.tv_only_money:
			tv_only_money.setBackgroundResource(R.drawable.rect_green_bk);
			tv_only_jifen.setBackgroundResource(R.drawable.rect_gary_bk);
			tv_jifen_and_money.setBackgroundResource(R.drawable.rect_gary_bk);
			buyType = "1";

			break;
		case R.id.tv_jifen_and_money:
			tv_jifen_and_money.setBackgroundResource(R.drawable.rect_green_bk);
			tv_only_money.setBackgroundResource(R.drawable.rect_gary_bk);
			tv_only_jifen.setBackgroundResource(R.drawable.rect_gary_bk);
			buyType = "2";

			break;
		case R.id.btn_reset:
			resetData();
			if (brandAdapter != null) {
				brandAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.btn_finish:
			// ToastUtil.showTextToast(context, "完成");
			String lowPrice = et_low_price.getText().toString().trim();
			String highPrice = et_high_price.getText().toString().trim();
			String lowScore = et_low_jifen.getText().toString().trim();
			String highScore = et_high_jifen.getText().toString().trim();
			if (!TextUtils.isEmpty(lowPrice)) {
				beginPrice = Integer.parseInt(lowPrice);
			}
			if (!TextUtils.isEmpty(highPrice)) {
				endPrice = Integer.parseInt(highPrice);
			}
			if (!TextUtils.isEmpty(lowScore)) {
				beginScore = Integer.parseInt(lowScore);
			}
			if (!TextUtils.isEmpty(highScore)) {
				endScore = Integer.parseInt(highScore);
			}
			if (beginPrice > endPrice) {
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("温馨提示!")
						.setContentText("价格区间最大值不能小于最小值！").show();
				return;
			}
			if (beginScore > endScore) {
				new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("温馨提示!")
				.setContentText("积分区间最大值不能小于最小值！").show();
				return;
			}
			// searchGoods();
			if (dl.isDrawerOpen(rlRight)) {
				dl.closeDrawer(rlRight);
			}
			currPage = 1;
			adapter = null;
			lv.doPullRefreshing(true, 500);
			break;

		default:
			break;
		}
	}

	// 重置筛选条件
	private void resetData() {
		tv_jifen_and_money.setBackgroundResource(R.drawable.rect_gary_bk);
		tv_only_money.setBackgroundResource(R.drawable.rect_gary_bk);
		tv_only_jifen.setBackgroundResource(R.drawable.rect_gary_bk);
		buyType = "";
		et_low_price.setText("");
		et_high_price.setText("");
		et_low_jifen.setText("");
		et_high_jifen.setText("");
		chioceItem = 1000;
		beginPrice = 0;
		endPrice = 0;
		beginScore = 0;
		endScore = 0;
		brand = "";
	}

	// 获取品牌列表
	private void getBrand(String id) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("cateId", cateId);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);

				Gson gson = new Gson();
				BrandResultBean result = null;
				try {
					result = gson.fromJson(arg0,
							BrandResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (result == null) {
					ToastUtil.showTextToast(context, "获取品牌失败！");
					return;
				}
				if (result.success) {
					if (result.brandList != null
							&& result.brandList.size() > 0) {

						brandList = result.brandList;
						brandAdapter = new BrandAdapter(brandList);
						gv_grade.setAdapter(brandAdapter);
					}
				} else {
					ToastUtil.showTextToast(context, result.msg);
				}

			
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "获取品牌失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GOODS_BRAND_URL, params, callback);
	}

	// 搜索商品
	private void searchGoods() {
		// mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
		// "数据加载中...");
		Map<String, String> params = new HashMap<String, String>();
		params.put("keyWorld", keyWorld);
		params.put("cateId", cateId);
		params.put("cateLvl", "3");
		params.put("currPage", "" + currPage);
		params.put("pageSize", "10");
		params.put("arrange", arrange);
		params.put("arrangeKey", arrangeKey);
		params.put("brand", brand);
		params.put("buyType", buyType);
		if (beginPrice != 0) {
			params.put("beginPrice", "" + beginPrice);
		}
		if (endPrice != 0) {
			params.put("endPrice", "" + endPrice);
		}
		if (beginScore != 0) {
			params.put("beginScore", "" + beginScore);
		}
		if (endScore != 0) {
			params.put("endScore", "" + endScore);
		}
		
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);

				Gson gson = new Gson();
				SearchResultBean result = null;
				try {
					result = gson.fromJson(arg0,
							SearchResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (result == null) {
					rl_error.setVisibility(View.VISIBLE);
					return;
				}
				if (result.success) {
					rl_error.setVisibility(View.GONE);
					if (result.goodsList != null) {

						if (isUpdate) {
							datas.clear();
						}
						datas.addAll(result.goodsList);
						if (adapter == null) {
							adapter = new MyAdapter(datas);
							lv_goods_list.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						if (isUpdate) {
							lv_goods_list.setSelection(0);
						}
						lv_goods_list.setVisibility(View.VISIBLE);
						lv.onPullDownRefreshComplete();
						lv.onPullUpRefreshComplete();
						lv.setHasMoreData(result.goodsList.size() < 10 ? false
								: true);
					}
					if (datas == null || datas.size() == 0) {
						rl_search_null.setVisibility(View.VISIBLE);
					} else {
						rl_search_null.setVisibility(View.GONE);
					}
				} else {
					rl_error.setVisibility(View.VISIBLE);
					ToastUtil.showTextToast(context, result.msg);
				}

			
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				rl_error.setVisibility(View.VISIBLE);
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GOODS_SEARCH_URL, params, callback);
	}

	class MyAdapter extends DefaultBaseAdapter<GoodsListBean> {

		public MyAdapter(List<GoodsListBean> datas) {
			super(datas);
		}

		@SuppressWarnings("finally")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item_goods_list,
						null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.iv_goods);
				holder.name = (TextView) convertView
						.findViewById(R.id.tv_goods_name);
				holder.tv_goods_jifen = (TextView) convertView
						.findViewById(R.id.tv_goods_jifen);
				holder.tv_add = (TextView) convertView
						.findViewById(R.id.tv_add);
				holder.tv_jifen_name = (TextView) convertView
						.findViewById(R.id.tv_jifen_name);
				holder.tv_goods_price = (TextView) convertView
						.findViewById(R.id.tv_goods_price);
				holder.count = (TextView) convertView
						.findViewById(R.id.tv_goods_count);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 填充数据
			int type = datas.get(position).BUY_TYPE;
			switch (type) {
			case 1:
				holder.tv_goods_price.setText("￥" + datas.get(position).PRICE);
				holder.tv_add.setVisibility(View.GONE);
				holder.tv_jifen_name.setVisibility(View.GONE);
				break;
			case 2:
				holder.tv_goods_price.setText("￥" + datas.get(position).PRICE);
				holder.tv_add.setVisibility(View.VISIBLE);
				holder.tv_goods_jifen
						.setText(datas.get(position).INTEGRAL + "");
				holder.tv_jifen_name.setVisibility(View.VISIBLE);
				break;
			case 3:
				holder.tv_add.setVisibility(View.GONE);
				holder.tv_goods_jifen
						.setText(datas.get(position).INTEGRAL + "");
				holder.tv_jifen_name.setVisibility(View.VISIBLE);

				break;

			default:
				break;
			}
			switch (type) {
			case 1:
				holder.tv_goods_price.setText("￥" + datas.get(position).PRICE);
				holder.tv_add.setText("");
				holder.tv_goods_jifen.setText("");

				break;
			case 2:
				holder.tv_goods_jifen
						.setText("" + datas.get(position).INTEGRAL);
				holder.tv_add.setText("+");
				holder.tv_goods_price.setText("￥" + datas.get(position).PRICE);

				break;
			case 3:
				holder.tv_goods_jifen
						.setText("" + datas.get(position).INTEGRAL);
				holder.tv_add.setText("积分");
				break;

			default:
				break;
			}
			holder.name.setText(datas.get(position).NAME);
			holder.count.setText("已兑换" + datas.get(position).BUY_NUM);
			try {
				ImageLoaderUtil.loadNetPic(
						ConstantValues.BASE_URL + datas.get(position).IMAGE,
						holder.icon);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				return convertView;
			}
		}

	}

	private class ViewHolder {
		private ImageView icon;
		private TextView name, count;
		private TextView tv_goods_price, tv_goods_jifen, tv_add, tv_jifen_name;
	}

	class BrandAdapter extends DefaultBaseAdapter<Map<String, String>> {
		BrandHolder holder = null;

		public BrandAdapter(List<Map<String, String>> datas) {
			super(datas);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item_textview,
						null);
				holder = new BrandHolder();
				holder.name = (TextView) convertView
						.findViewById(R.id.tv_brand);

				convertView.setTag(holder);
			} else {
				holder = (BrandHolder) convertView.getTag();
			}

			// 填充数据
			holder.name.setText(datas.get(position).get("BRAND"));
			if (chioceItem == position) {
				holder.name.setBackgroundResource(R.drawable.rect_green_bk);
			} else {
				holder.name.setBackgroundResource(R.drawable.rect_gary_bk);
			}
			return convertView;
		}

	}

	private class BrandHolder {
		private TextView name;
	}

	@Override
	public void onBackPressed() {
		if (dl.isDrawerOpen(rlRight)) {
			dl.closeDrawer(rlRight);
		} else {
			super.onBackPressed();
		}
	}

}
