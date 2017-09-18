package com.ksource.hbpostal.activity;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.TimeUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.SignCountResultBean;
import com.ksource.hbpostal.bean.SignResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.yitao.widget.KCalendar;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 签到日历页面
 */
public class Sign1Activity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private TextView tvData;
//    private TextView tvMonth;
    private KCalendar signView;
    private boolean isSign;
    private LoadDialog mLoadDialog;
    private ImageView iv_last, iv_next;

    private int nowYear;
    private int nowMonth;
    private int nowDay;
    private String token;
    private List<SignCountResultBean.CalendarListBean> list;


    @Override
    public int getLayoutResId() {
        return R.layout.activity_sign1;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("签到");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_last = (ImageView) findViewById(R.id.iv_last);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        tvData = (TextView) findViewById(R.id.activity_main_tv_year);
//        tvMonth = (TextView) findViewById(R.id.activity_main_tv_month);
        signView = (KCalendar) findViewById(R.id.activity_main_cv);

    }


    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        iv_last.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        signView.setOnCalendarDateChangedListener(new KCalendar.OnCalendarDateChangedListener() {
            @Override
            public void onCalendarDateChanged(int year, int month) {
                tvData.setText(year + "-"+month );
//                tvMonth.setText(month + "月");
                getData(year, month);
            }
        });
        signView.setOnCalendarClickListener(new KCalendar.OnCalendarClickListener() {
            @Override
            public void onCalendarClick(int row, int col, String dateFormat) {
                String today = format(signView.getThisday());
//				ToastUtil.showTextToast(context, dateFormat+format(signView.getThisday()));
                if (!TextUtils.isEmpty(today) && today.equals(dateFormat)) {
                    if (!isSign)
                        doSign();
                    else
                        ToastUtil.showTextToast(context, "今日已签到！");
                } else {
                    boolean isSigned = false;
                    for (SignCountResultBean.CalendarListBean bean : list) {
                        String dateStr = bean.YEAR + "-" + bean.MONTH + "-" + bean.DAY;
                        if (dateStr.equals(dateFormat)) {
                            isSigned = true;
                        }
                    }
//                    if (isSigned)
//                        ToastUtil.showTextToast(context, "该日已签到！");
//                    else
//                        ToastUtil.showTextToast(context, "该日未签到！");
                }
            }
        });
    }

    @Override
    public void initData() {
        token = sp.getString(ConstantValues.TOKEN, "");
        Calendar calendar = Calendar.getInstance();
        tvData.setText(String.valueOf(calendar.get(Calendar.YEAR)) + "-"+String.valueOf(calendar.get(Calendar.MONTH) + 1));
//        tvMonth.setText(String.valueOf(calendar.get(Calendar.MONTH) + 1) + "月");
        String nowTime = TimeUtils.getNowTimeString();
        nowYear = Integer.parseInt(nowTime.substring(0, 4));
        nowMonth = Integer.parseInt(nowTime.substring(5, 7));
        nowDay = Integer.parseInt(nowTime.substring(8, 10));
        getData(nowYear, nowMonth);
//		signView.addMark("2017-03-02",R.drawable.calendar_bg_tag);
    }

    // 获取签到积分
    private void getData(int year, int month) {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "查询签到信息...");
        Map<String, String> params = new HashMap<String, String>();

        params.put("token", token);
        params.put("year", year + "");
        params.put("month", month + "");
        // params.put("year", "2016");
        // params.put("month", "12");
        StringCallback callback = new StringCallback() {
            @SuppressWarnings("deprecation")
            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                //
//				arg0 = "{'flag':0,'calendarList':[{'YEAR':'2017','MONTH':'02','DAY':'13'},{'YEAR':'2017','MONTH':'02','DAY':'08'},{'YEAR':'2017','MONTH':'03','DAY':'01'},{'YEAR':'2017','MONTH':'03','DAY':'03'}],'msg':'获取签到日历成功','success':true}";
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
                    return;
                }
                if (signCountResult.success) {
                    list = signCountResult.calendarList;
                    List<String> calList = new ArrayList<>();
                    for (SignCountResultBean.CalendarListBean bean : list
                            ) {
                        calList.add(bean.YEAR + "-" + bean.MONTH + "-" + bean.DAY);
                        if (nowYear == Integer.parseInt(bean.YEAR)
                                && nowMonth == Integer.parseInt(bean.MONTH)
                                && nowDay == Integer.parseInt(bean.DAY)) {
                            isSign = true;
                        }
                    }
                    signView.addMarks(calList, R.drawable.edit_icon);
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
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context, ConstantValues.GET_SIGN_DATA_URL, params,
                callback);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_next:
                signView.nextMonth();
                break;
            case R.id.iv_last:
                signView.lastMonth();
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
//											getData();
                                        }
                                    }).show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
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
        DataUtil.doPostAESData(mLoadDialog,context, ConstantValues.DO_SIGN_URL, params, callback);
    }

    /**
     * 将Date转化成字符串->2013-3-3
     */
    public String format(Date d) {
        return addZero(d.getYear() + 1900, 4) + "-" + addZero(d.getMonth() + 1, 2) + "-" + addZero(d.getDate(), 2);
    }

    // 2或4
    private static String addZero(int i, int count) {
        if (count == 2) {
            if (i < 10) {
                return "0" + i;
            }
        } else if (count == 4) {
            if (i < 10) {
                return "000" + i;
            } else if (i < 100 && i > 10) {
                return "00" + i;
            } else if (i < 1000 && i > 100) {
                return "0" + i;
            }
        }
        return "" + i;
    }
}
