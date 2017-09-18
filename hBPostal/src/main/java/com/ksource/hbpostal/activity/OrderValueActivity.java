package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.OrderResultBean.PageInfoBean.DatasBean.GoodsListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 订单评价页面
 */
public class OrderValueActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private Button btn_submit;
	private TextView tv_goods_name;
	private EditText et_value;
	private ImageView iv_goods_icon;
	private RatingBar ratingBar;

	// private String orderId;
	private GoodsListBean goodsBean;
	private String valueContent;
	private LoadDialog mLoadDialog;
	private int rating;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_order_value;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);

		iv_goods_icon = (ImageView) findViewById(R.id.iv_goods_icon);
		et_value = (EditText) findViewById(R.id.et_value);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);
		btn_submit = (Button) findViewById(R.id.btn_submit);
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				if (fromUser && rating <= 0) {
					OrderValueActivity.this.ratingBar.setRating(1);
				}
			}
		});
	}

	@Override
	public void initData() {
		tv_title.setText("发表评价");
		// orderId = getIntent().getStringExtra("orderId");
		goodsBean = (GoodsListBean) getIntent().getSerializableExtra(
				"goodsBean");
		tv_goods_name.setText(goodsBean.GOODS_NAME);
		try {
			ImageLoaderUtil.loadNetPic(ConstantValues.BASE_URL
					+ goodsBean.IMAGE, iv_goods_icon);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_submit:
			valueContent = "" + et_value.getText().toString().trim();
			rating = (int) ratingBar.getRating();
			String token = sp.getString(ConstantValues.TOKEN, null);
			if (TextUtils.isEmpty(token)) {
				// finish();
				startActivity(new Intent(context, LoginActivity.class));
				return;
			}
			if (TextUtils.isEmpty(valueContent)) {
				// finish();
				ToastUtil.showTextToast(context, "请输入品论内容！");
				et_value.requestFocus();
				return;
			}
			if (rating > 0) {
				submitValue();
			} else{
				ToastUtil.showTextToast(context, "请为该商品评价！");
			}
			break;
		case R.id.iv_back:
			finish();
			break;

		default:
			break;
		}

	}

	// 提交评价
	private void submitValue() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"提交评价...");
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("orderId", goodsBean.ORDER_ID);
		params.put("goods_id", goodsBean.GOODS_ID);
		params.put("goods_name", goodsBean.GOODS_NAME);
		params.put("appraise_content", valueContent);
		params.put("appraise_level", "" + rating);
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
					ToastUtil.showTextToast(context, "评价失败！");
					return;
				}
				if (baseResult.success) {
					SweetAlertDialog dialog = new SweetAlertDialog(context,
							SweetAlertDialog.SUCCESS_TYPE);
					dialog.setTitleText("评价成功！")
							.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
								
								@Override
								public void onClick(SweetAlertDialog sweetAlertDialog) {
									sweetAlertDialog.dismiss();
									finish();
								}
							})
							.show();
				} else {
					if (baseResult.flag == 10) {
						mApplication.login();
						// submitValue();
					}
					ToastUtil.showTextToast(context, baseResult.msg);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "评价失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.APPRAISE_ORDER_URL, params, callback);
	}

}
