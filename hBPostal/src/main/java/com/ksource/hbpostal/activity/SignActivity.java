package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.TimeUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.SignCountResultBean;
import com.ksource.hbpostal.bean.SignCountResultBean.CalendarListBean;
import com.ksource.hbpostal.bean.SignResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.wecoder.signcalendar.library.MonthSignData;
import cn.wecoder.signcalendar.library.SignCalendar;
import okhttp3.Call;

/**
 * 老版签到
 * 已弃用
 */
public class SignActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back, iv_right;
	private TextView tv_sign;
	private ImageView iv_sign;
	private SignCalendar signCalendar;
	private boolean isSign;
	private LoadDialog mLoadDialog;

	private int nowYear;
	private int nowMonth;
	private int nowDay;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_sign;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("签到");
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		// iv_right.setVisibility(View.VISIBLE);

		tv_sign = (TextView) findViewById(R.id.tv_sign);
		iv_sign = (ImageView) findViewById(R.id.iv_sign);

		signCalendar = (SignCalendar) findViewById(R.id.my_sign_calendar);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		iv_right.setOnClickListener(this);
		iv_sign.setOnClickListener(this);
		signCalendar.setOnTodayClickListener(new SignCalendar.OnTodayClickListener() {
			@Override
			public void onTodayClick() {
//				doSign();
				ToastUtil.showTextToast(context,"hehheehhe");
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	public void initData() {
		String token = sp.getString(ConstantValues.TOKEN, null);
		if (TextUtils.isEmpty(token)) {
			// finish();
			startActivity(new Intent(context, LoginActivity.class));
		}
		isSign = getIntent().getBooleanExtra("isSign", false);
		if (isSign) {
			iv_sign.setVisibility(View.GONE);
			iv_sign.setEnabled(false);
		} else {
			iv_sign.setEnabled(true);
			iv_sign.setVisibility(View.VISIBLE);
		}
		MonthSignData monthData = new MonthSignData();
		ArrayList<MonthSignData> monthDatas = new ArrayList<>();
		monthData.setYear(nowYear);
		monthData.setMonth(nowMonth - 1);
		ArrayList<Date> signDates2 = new ArrayList<>();
		monthData.setSignDates(signDates2);
		monthDatas.add(monthData);

		Date today = new Date(nowYear - 1900, nowMonth - 1, nowDay);
		signCalendar.setToday(today);

		signCalendar.setSignDatas(monthDatas);
		getData();
	}

	// 获取签到积分
	private void getData() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"数据加载中...");
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		String nowTime = TimeUtils.getNowTimeString();
		nowYear = Integer.parseInt(nowTime.substring(0, 4));
		nowMonth = Integer.parseInt(nowTime.substring(5, 7));
		nowDay = Integer.parseInt(nowTime.substring(8, 10));
		params.put("token", token);
		params.put("year", nowYear + "");
		params.put("month", nowMonth + "");
		// params.put("year", "2016");
		// params.put("month", "12");
		StringCallback callback = new StringCallback() {

			@SuppressWarnings("deprecation")
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				//
//				arg0 = "{'flag':0,'calendarList':[{'YEAR':'2017','MONTH':'02','DAY':'13'},{'YEAR':'2017','MONTH':'02','DAY':'08'},{'YEAR':'2017','MONTH':'02','DAY':'01'},{'YEAR':'2017','MONTH':'02','DAY':'03'}],'msg':'获取签到日历成功','success':true}";
				SignCountResultBean signCountResult = null;
				Gson gson = new Gson();
				try {
					signCountResult = gson.fromJson(arg0,
							SignCountResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (signCountResult == null) {
					ToastUtil.showTextToast(context, "获取签到详情失败！");
					signCalendar.setVisibility(View.GONE);
					return;
				}
				if (signCountResult.success) {
					signCalendar.setVisibility(View.VISIBLE);
					List<CalendarListBean> list = signCountResult.calendarList;
					// String sumCount =
					// signCountResult.memberSignCount.SUM_COUNT;
					// String sumScore =
					// signCountResult.memberSignCount.SUM_SCORE;
					// if (TextUtils.isEmpty(sumCount) ||
					// "null".equals(sumCount)) {
					// sumCount = "0";
					// }
					// if (TextUtils.isEmpty(sumScore) ||
					// "null".equals(sumScore)) {
					// sumScore = "0";
					// }
					// tv_sign.setText("您已累计签到"+Integer.parseInt(sumCount)+"次，共获得"+Integer.parseInt(sumScore)+"金邮币");
//					tv_sign.setText("您已累计签到" + list.size() + "次，共获得"
//							+ list.size() + "金邮币");
					// isSign = signCountResult.isSign == 1;
					// if (isSign) {
					// iv_sign.setImageResource(R.drawable.button_sign_cur);
					// iv_sign.setEnabled(false);
					// } else {
					// iv_sign.setImageResource(R.drawable.button_sign_nor);
					// }
					ArrayList<MonthSignData> monthDatas = new ArrayList<>();
					MonthSignData monthData = new MonthSignData();
					if(list != null && list.size() > 0){
						monthData.setYear(Integer.parseInt(list.get(0).YEAR));
						monthData.setMonth(Integer.parseInt(list.get(0).MONTH) - 1);
						ArrayList<Date> signDates = new ArrayList<>();
						for (int i = 0; i < list.size(); i++) {
							signDates.add(new Date(Integer.parseInt(list.get(i).YEAR) - 1900, Integer.parseInt(list
									.get(i).MONTH) - 1, Integer.parseInt(list.get(i).DAY)));
							monthData.setSignDates(signDates);
							if (nowYear == Integer.parseInt(list.get(i).YEAR)
									&& nowMonth == Integer.parseInt(list.get(i).MONTH)
									&& nowDay == Integer.parseInt(list.get(i).DAY)) {
								iv_sign.setVisibility(View.GONE);
							}
						}
					}else {
						monthData.setYear(nowYear);
						monthData.setMonth(nowMonth - 1);
						monthData.setSignDates(new ArrayList<Date>());
					}
					monthDatas.add(monthData);

					Date today = new Date(nowYear - 1900, nowMonth - 1, nowDay);
					signCalendar.setToday(today);

					signCalendar.setSignDatas(monthDatas);
				} else if (signCountResult.flag == 10) {
					mApplication.login();
				} else {
					ToastUtil.showTextToast(context, signCountResult.msg);
				}
			}

			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "获取签到详情失败！");
				signCalendar.setVisibility(View.GONE);
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_SIGN_DATA_URL, params,
				callback);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_sign:
			// isSign = true;
			if (isSign) {
				iv_sign.setImageResource(R.drawable.button_sign_cur);
				iv_sign.setEnabled(false);
			} else {
				iv_sign.setImageResource(R.drawable.button_sign_nor);
				doSign();
			}
			break;
		case R.id.iv_right:
//			ToastUtil.showTextToast(context, "回到首页");
			mApplication.finishAllActivity();
			startActivity(new Intent(context, MainActivity.class));
			break;

		default:
			break;
		}
	}

	// 签到
	private void doSign() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"数据加载中...");
		Map<String, String> params = new HashMap<String, String>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		StringCallback callback = new StringCallback() {

			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);

				Gson gson = new Gson();
				SignResultBean signResult = null;
				try {
					signResult = gson.fromJson(arg0, SignResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (signResult == null) {
					new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
							.setTitleText("签到失败！").show();
					return;
				}
				if (signResult.success) {
					String score = signResult.score;
					SweetAlertDialog dialog = new SweetAlertDialog(
							context, SweetAlertDialog.SUCCESS_TYPE);
					dialog.setTitleText("签到成功！")
							.setContentText("获得" + score + "金邮币!")
							.setConfirmClickListener(
									new SweetAlertDialog.OnSweetClickListener() {

										@Override
										public void onClick(
												SweetAlertDialog sweetAlertDialog) {
											// 日历
											sweetAlertDialog.dismiss();
											finish();
//											getData();
										}
									}).show();
					// ToastUtil.showTextToast(context, "签到成功，获得"+score+"金邮币");
					// tv_sign.setText("您已累计签到"+sumCount+"次，共获得"+sumScore+"金邮币");
					// getData();
				} else if (signResult.flag == 10) {
					mApplication.login();
				} else {
					ToastUtil.showTextToast(context, signResult.msg);
				}
			}

			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);

			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.DO_SIGN_URL, params, callback);
	}
}
