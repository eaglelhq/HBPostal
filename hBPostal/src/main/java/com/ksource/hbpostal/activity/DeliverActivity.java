package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.PhoneUtils;
import com.blankj.utilcode.utils.RegexUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.DeliverResaultBean;
import com.ksource.hbpostal.bean.DeliverResaultBean.ShowapiResBodyBean.DataBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.util.SpannableUtils;
import com.yitao.dialog.CallOrSendMessageDialog;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * 物流详情页面
 */
public class DeliverActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private ImageView iv_goods;

	private TextView tv_state, tv_com, tv_num, tv_tel, tv_count;
	private PullToRefreshListView lv;
	private ListView listView;
	private List<DataBean> datas;
	private BaseAdapter adapter;
	private LoadDialog mLoadDialog;
	private RelativeLayout rl_error, rl_null;
	private String num;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_deliver;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_goods = (ImageView) findViewById(R.id.iv_goods);

		tv_count = (TextView) findViewById(R.id.tv_count);
		tv_state = (TextView) findViewById(R.id.tv_state);
		tv_com = (TextView) findViewById(R.id.tv_com);
		tv_num = (TextView) findViewById(R.id.tv_num);
		tv_tel = (TextView) findViewById(R.id.tv_tel);
		lv = (PullToRefreshListView) findViewById(R.id.lv_deliver);
		rl_null = (RelativeLayout) findViewById(R.id.rl_null);
		rl_error = (RelativeLayout) findViewById(R.id.rl_error);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		rl_error.setOnClickListener(this);
		tv_tel.setOnClickListener(this);
		lv.setPullRefreshEnabled(false);
		lv.setPullLoadEnabled(false);
		lv.setScrollLoadEnabled(false);
		listView = lv.getRefreshableView();
		listView.setDivider(null);
		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
			}
		});
	}

	@Override
	public void initData() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
		tv_title.setText("物流详情");
		Intent intent = getIntent();
		String count = intent.getStringExtra("count");
		tv_count.setText(count + "件商品");
		String imageUrl = intent.getStringExtra("imageUrl");
		if (!TextUtils.isEmpty(imageUrl)) {
			try {
				ImageLoaderUtil.loadNetPic(ConstantValues.BASE_URL + imageUrl,
						iv_goods);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		num = intent.getStringExtra("num");
		// num = "3934681379348";
		// ToastUtil.showTextToast(context, "快递单号:::::" + num);
		getData();
	}

	private void getData() {
		StringCallback callback = new StringCallback() {

			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				System.out.println("Exception====\n" + arg1.toString());
				rl_error.setVisibility(View.VISIBLE);
				ToastUtil.showTextToast(context, "查询物流信息失败！");
			}

			@Override
			public void onResponse(String result, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				System.out.println("result====" + result);
				// ToastUtil.showTextToast(context, arg0);
				showInfo(result);
			}
		};
		// String result =
		// "{'showapi_res_code':0,'showapi_res_error':'','showapi_res_body':{'mailNo':'3934681379348','update':1483686184107,'updateStr':'2017-01-06 15:03:04','ret_code':0,'flag':true,'status':4,'tel':'400-821-6789','expSpellName':'yunda','data':[{'time':'2016-12-14 13:26:07','context':'[河南郑州公司高新技术开发区分部]快件已被 已签收 签收'},{'time':'2016-12-14 08:44:55','context':'[河南郑州公司高新技术开发区分部]进行派件扫描；派送业务员：薛玉锋；联系电话：15238967183'},{'time':'2016-12-14 08:16:05','context':'[河南郑州公司高新技术开发区分部]到达目的地网点，快件将很快进行派送'},{'time':'2016-12-13 14:11:09','context':'[河南郑州公司]进行快件扫描，将发往：河南郑州公司高新技术开发区分部'},{'time':'2016-12-13 14:10:14','context':'[河南郑州公司]到达目的地网点，快件将很快进行派送'},{'time':'2016-12-13 14:07:08','context':'[河南郑州分拨中心]从站点发出，本次转运目的地：河南郑州公司'},{'time':'2016-12-13 14:05:56','context':'[河南郑州分拨中心]在分拨中心进行卸车扫描'},{'time':'2016-12-12 18:16:54','context':'[上海分拨中心]进行装车扫描，即将发往：河南郑州分拨中心'},{'time':'2016-12-12 18:10:59','context':'[上海分拨中心]在分拨中心进行称重扫描'},{'time':'2016-12-12 14:12:45','context':'[通达公司菜鸟大包装上海仓服务部]进行揽件扫描'}],'expTextName':'韵达快运'}}";
		// showInfo(result);
		DataUtil.getDeliver(num, callback);
	}

	// 显示物流信息
	private void showInfo(String result) {
//		DialogUtil.getInstance().dialogDismiss(mLoadDialog);
		Gson gson = new Gson();
		DeliverResaultBean resaultBean = null;
		try {
			resaultBean = gson.fromJson(result, DeliverResaultBean.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (resaultBean == null) {
			rl_error.setVisibility(View.VISIBLE);
			return;
		}
		if (resaultBean.showapi_res_code == 0) {
			rl_error.setVisibility(View.GONE);
			int state = resaultBean.showapi_res_body.status;
			String stateStr = "";
			switch (state) {
			case -1:
				stateStr = "待查询";
				break;
			case 0:
				stateStr = "查询异常";
				break;
			case 1:
				stateStr = "暂无记录";
				break;
			case 2:
				stateStr = "在途中";
				break;
			case 3:
				stateStr = "派送中";
				break;
			case 4:
				stateStr = "已签收";
				break;
			case 5:
				stateStr = "用户拒签";
				break;
			case 6:
				stateStr = "疑难件";
				break;
			case 7:
				stateStr = "无效单";
				break;
			case 8:
				stateStr = "超时单";
				break;
			case 9:
				stateStr = "签收失败";
				break;
			case 10:
				stateStr = "退回";
				break;

			default:
				break;
			}
			tv_state.setText(stateStr);
			tv_num.setText(resaultBean.showapi_res_body.mailNo);
			tv_com.setText(resaultBean.showapi_res_body.expTextName);
			tv_tel.setText(resaultBean.showapi_res_body.tel);
			if (resaultBean.showapi_res_body.flag) {
				datas = resaultBean.showapi_res_body.data;
				adapter = new MyAdapter(datas);
				listView.setAdapter(adapter);
			} else {
				ToastUtil.showTextToast(context,
						resaultBean.showapi_res_body.msg);
				rl_null.setVisibility(View.VISIBLE);
			}
		} else {
			ToastUtil.showTextToast(context, resaultBean.showapi_res_error);
			rl_error.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_error:
			mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
			getData();
			break;
		case R.id.tv_tel:
			// 打电话
			String phoneNum = tv_tel.getText().toString().trim();
			if (!TextUtils.isEmpty(phoneNum)) {
				callorSms(phoneNum);
			}
			break;

		default:
			break;
		}
	}

	class MyAdapter extends DefaultBaseAdapter<DataBean> {

		private ViewHolder holder;

		public MyAdapter(List<DataBean> datas) {
			super(datas);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			holder = null;
			if (convertView == null) {
				convertView = View
						.inflate(context, R.layout.item_deliver, null);
				holder = new ViewHolder();
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				holder.tv_context = (TextView) convertView
						.findViewById(R.id.tv_context);
				holder.iv_dot = (ImageView) convertView
						.findViewById(R.id.iv_dot);
				holder.iv_top = convertView.findViewById(R.id.iv_top);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();

			}

			// 填数据
			String content = datas.get(position).context;
			try {
				if (content.contains("联系电话")) {
					// content = content.substring();
					int index = content.lastIndexOf("：") + 1;
					SpannableString spStr = new SpannableString(content);
					final String phoneNum = content
							.substring(index, index + 11);
					if (RegexUtils.isMobileSimple(phoneNum)) {
						ClickableSpan what = new ClickableSpan() {
							@Override
							public void updateDrawState(TextPaint ds) {
								super.updateDrawState(ds);
								ds.setColor(Color.rgb(1, 133, 62)); // 设置文件颜色
								ds.setUnderlineText(true); // 设置下划线
							}

							@Override
							public void onClick(View widget) {
								callorSms(phoneNum);
							}
						};
						SpannableUtils.setTextClickable(what,
								holder.tv_context, spStr, index, index + 11);
						holder.tv_context.setText(spStr);
					}
				} else {
					holder.tv_context.setText(content);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			holder.tv_time.setText(datas.get(position).time);
			if (position == 0) {
				holder.iv_dot.setImageResource(R.drawable.item_dot_green);
				holder.iv_top.setVisibility(View.INVISIBLE);
				holder.tv_context.setTextColor(Color.rgb(1, 133, 62));
				holder.tv_time.setTextColor(Color.rgb(1, 133, 62));
			} else {
				holder.iv_dot.setImageResource(R.drawable.item_dot_gray);
				holder.iv_top.setVisibility(View.VISIBLE);
				holder.tv_context.setTextColor(Color.rgb(178, 178, 178));
				holder.tv_time.setTextColor(Color.rgb(178, 178, 178));
			}
			return convertView;

		}

	}

	class ViewHolder {
		private ImageView iv_dot;
		private View iv_top;
		private TextView tv_context;
		private TextView tv_time;
	}

	// 打电话
	private void callorSms(final String phoneNum) {
		DialogUtil dialog = DialogUtil.getInstance();

		final CallOrSendMessageDialog callDialog = dialog
				.showCallOrSendMessageDialog(context, phoneNum);
		callDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.dialog_call:
					// ToastUtil.showTextToast(context, "打電話");
					PhoneUtils.dial(phoneNum);
					break;
				case R.id.dialog_message:
					// ToastUtil.showTextToast(context, "发短信");
					PhoneUtils.sendSms(phoneNum, "");

					break;
				case R.id.dialog_cancle:
					// ToastUtil.showTextToast(context, "取消");
					callDialog.dismiss();
					break;

				default:
					break;
				}
			}
		});
	}

}
