package com.ksource.hbStore.fragment;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.TimeUtils;
import com.google.gson.Gson;
import com.ksource.hbStore.R;
import com.ksource.hbStore.activity.JSDetailActivity;
import com.ksource.hbStore.adapter.DefaultBaseAdapter;
import com.ksource.hbStore.bean.CalResultBean;
import com.ksource.hbStore.bean.JSDListResultBean;
import com.ksource.hbStore.config.ConstantValues;
import com.ksource.hbStore.util.DataUtil;
import com.ksource.hbStore.util.TimeUtil;
import com.ksource.hbStore.widget.KCalendar;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.DialogUtil;
import com.yitao.util.NumberUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 结算单页面-日历 + 列表
 */
public class JSDBillFragment extends BaseFragment implements View.OnClickListener {
    public PullToRefreshListView lv;
    public ListView lv_order;
    public List<JSDListResultBean.HistoryBean> datas;
    public RelativeLayout rl_error, rl_order_null;
    public LinearLayout ll_cal, ll_list;
    public BaseAdapter adapter;
    private TextView tvData;
    //    private TextView tvMonth;
    private boolean isList;
    private LoadDialog mLoadDialog;
    private ImageView iv_last, iv_next;

    private int nowYear;
    private int nowMonth;
    private int nowDay;
    private List<CalResultBean.ToBeSettleBean> tobelist;
    private List<CalResultBean.ClsdSettleBean> clslist;
    public int currPage = 1;
    public boolean isUpdate;
    private String token;
    private KCalendar signView;
    private FloatingActionButton fab;
    private String statementTime;
    private List<String> calList;

    @Override
    public View initView() {
        return View.inflate(context, R.layout.fragment_jsd, null);
    }

    @Override
    public void initData() {
        datas = new ArrayList<>();
        token = spUtils.getString(ConstantValues.TOKEN, null);
        lv = (PullToRefreshListView) view.findViewById(R.id.lv_order);
        rl_error = (RelativeLayout) view.findViewById(R.id.rl_error);
        rl_order_null = (RelativeLayout) view.findViewById(R.id.rl_null);
        iv_last = (ImageView) view.findViewById(R.id.iv_last);
        iv_next = (ImageView) view.findViewById(R.id.iv_next);
        tvData = (TextView) view.findViewById(R.id.activity_main_tv_year);
        signView = (KCalendar) view.findViewById(R.id.activity_main_cv);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        ll_cal = (LinearLayout) view.findViewById(R.id.ll_cal);
        ll_list = (LinearLayout) view.findViewById(R.id.ll_list);
        lv.setPullRefreshEnabled(true);
        lv.setPullLoadEnabled(false);
        lv.setScrollLoadEnabled(true);
        lv_order = lv.getRefreshableView();
        lv_order.setDivider(null);
        rl_error.setOnClickListener(this);
        fab.setOnClickListener(this);
        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                isUpdate = true;
                currPage = 1;
                getData();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                isUpdate = false;
                ++currPage;
                getData();
            }
        });
        iv_last.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        signView.setOnCalendarDateChangedListener(new KCalendar.OnCalendarDateChangedListener() {
            @Override
            public void onCalendarDateChanged(int year, int month) {
                tvData.setText(year + "-" + month);
//                tvMonth.setText(month + "月");
                getCalData(year, month);
            }
        });
        signView.setOnCalendarClickListener(new KCalendar.OnCalendarClickListener() {
            @Override
            public void onCalendarClick(int row, int col, String dateFormat) {
                //点击查看本日结算单
                isList = true;
                statementTime = dateFormat.substring(0, 4) + dateFormat.substring(5, 7) + dateFormat.substring(8);
//				statementTime = dateFormat;
                showListOrCal();
            }
        });
        Calendar calendar = Calendar.getInstance();
        tvData.setText(String.valueOf(calendar.get(Calendar.YEAR)) + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1));
//        tvMonth.setText(String.valueOf(calendar.get(Calendar.MONTH) + 1) + "月");
        String nowTime = TimeUtils.getNowTimeString();
        nowYear = Integer.parseInt(nowTime.substring(0, 4));
        nowMonth = Integer.parseInt(nowTime.substring(5, 7));
        nowDay = Integer.parseInt(nowTime.substring(8, 10));

        showListOrCal();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_error:
                mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                        "数据加载中...");
                adapter = null;
                isUpdate = true;
                currPage = 1;
                getData();
                break;
            case R.id.iv_next:
                signView.nextMonth();
                break;
            case R.id.iv_last:
                signView.lastMonth();
                break;
            case R.id.fab:
                isList = !isList;
                showListOrCal();
                statementTime = "";
                break;
            default:
                break;
        }
    }

    private void showListOrCal() {
        if (isList) {
            fab.setImageResource(R.drawable.icon_calendarday);
            ll_cal.setVisibility(View.GONE);
            ll_list.setVisibility(View.VISIBLE);
            mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                    "数据加载中...");
            adapter = null;
            isUpdate = true;
            currPage = 1;
            getData();
        } else {
            fab.setImageResource(R.drawable.icon_liebiao);
            ll_cal.setVisibility(View.VISIBLE);
            ll_list.setVisibility(View.GONE);
            getCalData(nowYear, nowMonth);
        }
    }

    // 获取结算列表信息
    public void getData() {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("pageSize", "10");
        params.put("currPage", "" + currPage);
        if (!TextUtils.isEmpty(statementTime))
            params.put("statementTime", statementTime);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                JSDListResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(arg0, JSDListResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取结算单信息失败！");
                    rl_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (resultBean.success) {
                    rl_error.setVisibility(View.GONE);
                    rl_order_null.setVisibility(View.GONE);
                    if (isUpdate) {
                        datas.clear();
                    }
                    datas.addAll(resultBean.history);
                    if (adapter == null) {
                        adapter = new OrderAdapter(datas);
                        lv_order.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    if (isUpdate) {
                        lv_order.setSelection(0);
                    }
                    lv_order.setVisibility(View.VISIBLE);
                    lv.onPullDownRefreshComplete();
                    lv.onPullUpRefreshComplete();
                    lv.setHasMoreData(resultBean.history.size() >= 10);
                    if (datas == null || datas.size() == 0) {
                        rl_order_null.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (resultBean.flag == 10) {
                        mApplication.login();
                    }
                    ToastUtil.showTextToast(context, resultBean.msg);
                    rl_error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取结算单信息失败！");
                rl_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.JIESUAN_LIST, params, callback);
    }

    // 获取结算日历信息
    public void getCalData(int year, int month) {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("year", "" + year);
        params.put("month", "" + month);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                CalResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(arg0, CalResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取结算单信息失败！");
                    rl_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (resultBean.success) {

//                    calList = new ArrayList<>();

                    tobelist = resultBean.toBeSettle;
                    for (CalResultBean.ToBeSettleBean bean : tobelist) {
//                        calList.add(bean.YEAR + bean.MONTH + bean.DAY);
                        signView.addMark(bean.YEAR + "-" + bean.MONTH + "-" + bean.DAY, R.drawable.icon_pending);
                    }
                    clslist = resultBean.clsdSettle;
                    for (CalResultBean.ClsdSettleBean bean : clslist) {
//                        calList.add(bean.YEAR + bean.MONTH + bean.DAY);
                        signView.addMark(bean.YEAR + "-" + bean.MONTH + "-" + bean.DAY, R.drawable.icon_settled);
                    }
//					signView.addMarks(calList, R.drawable.icon_pending);

//					List<String> clsdSettleList = new ArrayList<>();
//					signView.addMarks(clsdSettleList, R.drawable.icon_settled);
                } else {
                    if (resultBean.flag == 10) {
                        mApplication.login();
                    }
                    ToastUtil.showTextToast(context, resultBean.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取结算单信息失败！");
                rl_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.JIESUAN_CAL, params, callback);
    }

    private class OrderAdapter extends DefaultBaseAdapter<JSDListResultBean.HistoryBean> {

        public OrderHolder holder;

        private OrderAdapter(List<JSDListResultBean.HistoryBean> datas) {
            super(datas);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View.inflate(context,
                        R.layout.item_jsd_history, null);
                holder = new OrderHolder();
                holder.tv_pay = (TextView) convertView
                        .findViewById(R.id.ll_pay);
                holder.tv_pay_state = (TextView) convertView
                        .findViewById(R.id.tv_pay_state);
                holder.tv_order_num = (TextView) convertView
                        .findViewById(R.id.tv_order_num);
                holder.tv_sell_name = (TextView) convertView
                        .findViewById(R.id.tv_sell_name);
                holder.tv_time = (TextView) convertView
                        .findViewById(R.id.tv_time);
                holder.tv_detail = (TextView) convertView
                        .findViewById(R.id.tv_detail);
                convertView.setTag(holder);

            } else {
                holder = (OrderHolder) convertView.getTag();

            }

            JSDListResultBean.HistoryBean data = datas.get(position);
            holder.tv_pay.setText("￥" + NumberUtil.toDecimal2(data.TOTALAMOUNT));
            final int settleState = data.SETTLE_STATE;
            String settleStr = "";
            switch (settleState) {
                case 0:
                    settleStr = "待结算";
                    if (TextUtils.isEmpty(data.OUT_SETTLE_BILL_TIME)) {
                        holder.tv_time.setVisibility(View.GONE);
                    } else {
                        holder.tv_time.setVisibility(View.VISIBLE);
                        holder.tv_time.setText("出单时间： " + TimeUtil.formatTimeSec(data.OUT_SETTLE_BILL_TIME));
                    }
                    break;
                case 1:
                    settleStr = "已结算";
                    if (TextUtils.isEmpty(data.SETTLE_TIME)) {
                        holder.tv_time.setVisibility(View.GONE);
                    } else {
                        holder.tv_time.setVisibility(View.VISIBLE);
                        holder.tv_time.setText("结算时间： " + TimeUtil.formatTimeSec(data.SETTLE_TIME));
                    }
                    break;
                default:
                    break;
            }
            holder.tv_pay_state.setText(settleStr);
            holder.tv_order_num.setText("结算单号： " + data.SETTLE_CODE);
            holder.tv_sell_name.setText("结算记录数： " + data.SETTLENUM + "条");

//			holder.tv_ss.setText("");

            holder.tv_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, JSDetailActivity.class);
                    intent.putExtra("id", datas.get(position).ID);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    private class OrderHolder {
        private TextView tv_pay, tv_pay_state, tv_order_num, tv_sell_name, tv_time, tv_detail;
    }

}
