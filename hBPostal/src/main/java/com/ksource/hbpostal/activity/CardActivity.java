package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.CardResaultBean;
import com.ksource.hbpostal.bean.CardResaultBean.BankCardListBean;
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

/**
 * 我的银行卡页面
 */
public class CardActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back, iv_right;
	private PullToRefreshListView lv;
	private ListView lv_card;
	private List<BankCardListBean> datas;
	private BaseAdapter adapter;
	private LoadDialog mLoadDialog;
	private RelativeLayout rl_error, rl_card_null;
	private Button btn_add;
	private String token;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_card;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		btn_add = (Button) findViewById(R.id.btn_add);
		lv = (PullToRefreshListView) findViewById(R.id.lv_card);

		rl_error = (RelativeLayout) findViewById(R.id.rl_error);
		rl_card_null = (RelativeLayout) findViewById(R.id.rl_card_null);
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		iv_right.setOnClickListener(this);
		btn_add.setOnClickListener(this);
		rl_error.setOnClickListener(this);
		lv.setPullRefreshEnabled(true);
		lv.setPullLoadEnabled(false);
		lv.setScrollLoadEnabled(true);
		lv.setHasMoreData(false);
		lv_card = lv.getRefreshableView();
		lv_card.setDivider(null);
		lv_card.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if(datas.get(i).SIGN_STATE == 0){
					String bankCard = datas.get(i).BANK_CARD;
					String name = datas.get(i).BANK_CARD_NAME;
					String idCard = datas.get(i).ID_CARD;
					String mobile = datas.get(i).MOBILE;
					authBankCard(bankCard,name,idCard,mobile);
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
	}

	//银行卡鉴权
    private void authBankCard(final String bank_card, final String bank_card_name, final String id_card, final String mobile) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("bank_card", bank_card);
        params.put("mobile", mobile);
        params.put("idCard", id_card);
        params.put("bank_card_name", bank_card_name);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                CardResaultBean cardResult = null;
                try {
                    cardResult = gson.fromJson(
                            arg0, CardResaultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cardResult == null) {
                    ToastUtil.showTextToast(context, "银行卡签约失败！");
                    return;
                }
                if (cardResult.success) {
                    Intent intent = new Intent(context,CardSignActivity.class);
                    intent.putExtra("cardNum",bank_card);
                    intent.putExtra("userName",bank_card_name);
                    intent.putExtra("idNum",id_card);
                    intent.putExtra("phone",mobile);
                    startActivity(intent);
                } else if (cardResult.flag == 10) {
                    mApplication.login();
                    // getData();
                } else {
                    ToastUtil.showTextToast(context, cardResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.AUTH_BANKCARD_URL, params, callback);

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

	@Override
	public void initData() {
		tv_title.setText("我的银行卡");
		iv_right.setVisibility(View.VISIBLE);
		token = sp.getString(ConstantValues.TOKEN, "");
		iv_right.setImageResource(R.drawable.mine_card_add);
		datas = new ArrayList<>();
		if (TextUtils.isEmpty(token)) {
			// finish();
			startActivity(new Intent(context, LoginActivity.class));
		}
	}

	private void getData() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				Gson gson = new Gson();
					CardResaultBean cardResult = null;
					try {
						cardResult = gson.fromJson(
								arg0, CardResaultBean.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (cardResult == null) {
						ToastUtil.showTextToast(context, "获取银行卡失败！");
						return;
					}
					if (cardResult.success) {
						datas = cardResult.bankCardList;
						if (datas == null || datas.size() == 0) {
							rl_card_null.setVisibility(View.VISIBLE);
						} else {
							rl_card_null.setVisibility(View.GONE);
							adapter = new MyAdapter(datas);
							lv_card.setAdapter(adapter);
						}
					} else if (cardResult.flag == 10) {
						mApplication.login();
						// getData();
					} else {
						ToastUtil
								.showTextToast(context, cardResult.msg);
					}
			
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				rl_error.setVisibility(View.VISIBLE);
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_BANKCARD_URL, params, callback);

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
		case R.id.btn_add:
		case R.id.iv_right:
			Intent intent = new Intent(context, AddCardStep1Activity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	// 删除银行卡请求
	private void removeData(final String id, final int position) {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"删除银行卡...");
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("bankCardId", id);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);

				Gson gson = new Gson();
					BaseResultBean baseResult = null;
					try {
						baseResult = gson.fromJson(
								arg0, BaseResultBean.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (baseResult == null) {
						new SweetAlertDialog(context,
								SweetAlertDialog.ERROR_TYPE)
								.setTitleText("删除银行卡失败！").show();
						return;
					}
					if (baseResult.success) {
						// datas.remove(position);
						// adapter.notifyDataSetChanged();
						getData();
//						lv.doPullRefreshing(true, 500);
					} else {
						if (baseResult.flag == 10) {
							mApplication.login();
							// removeData(id, position);
						}
						new SweetAlertDialog(context,
								SweetAlertDialog.ERROR_TYPE)
								.setTitleText("删除银行卡失败！")
								.setContentText(baseResult.msg).show();
					}
										
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				new SweetAlertDialog(context,
						SweetAlertDialog.ERROR_TYPE).setTitleText(
						"删除银行卡失败！").show();

			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.DEL_BANKCARD_URL, params, callback);

	}

	class MyAdapter extends DefaultBaseAdapter<BankCardListBean> {

		private ViewHolder holder;

		public MyAdapter(List<BankCardListBean> datas) {
			super(datas);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			holder = null;
			if (convertView == null) {
				convertView = View
						.inflate(context, R.layout.item_my_card, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tv_card_number = (TextView) convertView
						.findViewById(R.id.tv_card_number);
				// holder.tv_card_name = (TextView) convertView
				// .findViewById(R.id.tv_card_name);
				holder.iv_chioce = (ImageView) convertView
						.findViewById(R.id.iv_chioce);
				holder.iv_sign = (ImageView) convertView
						.findViewById(R.id.iv_sign);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 填充数据
			holder.iv_icon.setImageResource(R.drawable.maincard_logo);
			String bankCard = datas.get(position).BANK_CARD;
			if (bankCard.length() > 4) {
				bankCard = bankCard.substring(bankCard.length() - 4);
			} 
			holder.tv_card_number.setText("***************" + bankCard);
			holder.iv_chioce.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					SweetAlertDialog delDialog = new SweetAlertDialog(
							context, SweetAlertDialog.WARNING_TYPE);
					delDialog
							.setTitleText("确定要删除该银行卡？")
							.setContentText("银行卡删除后不可恢复！")
							.setCancelText("取消")
							.setConfirmText("删除")
							.showCancelButton(true)
							.setConfirmClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												SweetAlertDialog sDialog) {
											removeData(datas.get(position).ID,
													position);
											sDialog.dismiss();
										}
									}).show();
					//
				}
			});
			if(datas.get(position).SIGN_STATE == 1){
				holder.iv_sign.setImageResource(R.drawable.signed_no);
			}else{
				holder.iv_sign.setImageResource(R.drawable.signed);
			}
			return convertView;
		}
	}

	class ViewHolder {
		// private TextView tv_card_name;
		private TextView tv_card_number;
		private ImageView iv_icon;
		private ImageView iv_chioce;
		private ImageView iv_sign;
	}

}
