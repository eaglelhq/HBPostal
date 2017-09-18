package com.ksource.hbpostal.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.activity.LoginActivity;
import com.ksource.hbpostal.activity.ScanPayActivity;
import com.ksource.hbpostal.activity.ScanPayDetailActivity;
import com.ksource.hbpostal.activity.ScanPayShenSuActivity;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.ScanHistoryResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.TimeUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.DialogUtil;
import com.yitao.util.NumberUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public abstract class BaseHistoryFragment extends BaseFragment implements
        OnClickListener {

    public PullToRefreshListView lv;
    public ListView lv_order;
    public List<ScanHistoryResultBean.HistoryBean> datas;
    public RelativeLayout rl_error, rl_order_null;
    public Button btn_gosee;
    public TextView tv_tishi;
    public BaseAdapter adapter;
    public LoadDialog mLoadDialog;

    public int currPage = 1;
    public boolean isUpdate;
    private String token;

    @Override
    public View initView() {
        return View.inflate(context, R.layout.fragment_order, null);
    }

    @Override
    public void initData() {
        datas = new ArrayList<>();
        lv = (PullToRefreshListView) view.findViewById(R.id.lv_order);
        rl_error = (RelativeLayout) view.findViewById(R.id.rl_error);
        rl_order_null = (RelativeLayout) view.findViewById(R.id.rl_order_null);
        tv_tishi = (TextView) view.findViewById(R.id.tv_tishi);
        tv_tishi.setText("\n暂无相关信息\n");
        btn_gosee = (Button) view.findViewById(R.id.btn_gosee);
        btn_gosee.setVisibility(View.GONE);

        lv.setPullRefreshEnabled(true);
        lv.setPullLoadEnabled(false);
        lv.setScrollLoadEnabled(true);
        lv_order = lv.getRefreshableView();
        lv_order.setDivider(null);
        rl_error.setOnClickListener(this);
        lv.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                isUpdate = true;
                currPage = 1;
                getData(setState());
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                isUpdate = false;
                ++currPage;
                getData(setState());
            }
        });
        token = sp.getString(ConstantValues.TOKEN, null);
        if (TextUtils.isEmpty(token)) {
            // getActivity().finish();
            startActivity(new Intent(context, LoginActivity.class));
        }

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
                getData(setState());
                break;

            default:
                break;
        }
    }

    /**
     * 设置状态
     *
     * @return
     */
    public abstract int setState();

    @Override
    public void onResume() {
        super.onResume();
        adapter = null;
        lv.doPullRefreshing(true, 100);
    }

    // 获取信息
    public void getData(final int state) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("pageSize", "10");
        params.put("currPage", "" + currPage);
        if (state != 10) {
            params.put("pay_type", state + "");
        }else{
            params.put("pay_type", "");
        }
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                ScanHistoryResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(arg0, ScanHistoryResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取交易历史失败！");
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
                ToastUtil.showTextToast(context, "获取交易历史失败！");
                rl_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.DEAL_HISTORY, params, callback);
    }


    private class OrderAdapter extends DefaultBaseAdapter<ScanHistoryResultBean.HistoryBean> {

        public OrderHolder holder;

        private OrderAdapter(List<ScanHistoryResultBean.HistoryBean> datas) {
            super(datas);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View.inflate(context,
                        R.layout.item_scan_history, null);
                holder = new OrderHolder();
                holder.tv_pay = (TextView) convertView
                        .findViewById(R.id.ll_pay);
                holder.tv_fact_pay = (TextView) convertView
                        .findViewById(R.id.tv_fact_pay);
                holder.tv_pay_state = (TextView) convertView
                        .findViewById(R.id.tv_pay_state);
                holder.tv_order_num = (TextView) convertView
                        .findViewById(R.id.tv_order_num);
                holder.tv_sell_name = (TextView) convertView
                        .findViewById(R.id.tv_sell_name);
                holder.tv_time = (TextView) convertView
                        .findViewById(R.id.tv_time);
                holder.tv_ss = (TextView) convertView
                        .findViewById(R.id.tv_ss);
                holder.tv_detail = (TextView) convertView
                        .findViewById(R.id.tv_detail);
                holder.line = convertView
                        .findViewById(R.id.line);
                convertView.setTag(holder);

            } else {
                holder = (OrderHolder) convertView.getTag();

            }

            // 填充数据
            ScanHistoryResultBean.HistoryBean data = datas.get(position);
            String factPay = "";
            if (data.PAY_AMOUNT == 0){
                factPay = "实际支付："+data.SCORE_NUM +"积分";
            }else if(data.SCORE_NUM == 0){
                factPay = "实际支付：￥"+ NumberUtil.toDecimal2(data.PAY_AMOUNT);
            }else{
                factPay = "实际支付：￥" + NumberUtil.toDecimal2(data.PAY_AMOUNT) + "+" + data.SCORE_NUM + "积分";
            }
            // 优惠券价值
//            if (data.SCORE_NUM > 0){
//                factPay = factPay + "+"+ data.SCORE_NUM +"元优惠券";
//            }.
            holder.tv_fact_pay.setText(factPay);
            holder.tv_pay.setText("￥" +  NumberUtil.toDecimal2(data.TOTAL_AMOUNT));
            final int state = data.PAY_TYPE;
            String stateStr = "";
            switch (state) {
                case 0:
                    stateStr = "未支付";
                    holder.tv_ss.setVisibility(View.GONE);
                    holder.line.setVisibility(View.GONE);
//                    holder.tv_ss.setText("去支付");
                    break;
                case 1:
                    stateStr = "交易成功";
                    holder.tv_ss.setVisibility(View.GONE);
                    holder.line.setVisibility(View.GONE);
                    break;
                case 2:
                    stateStr = "交易失败";
                    holder.tv_ss.setVisibility(View.VISIBLE);
                    holder.line.setVisibility(View.VISIBLE);
                    if(data.APPEAL_STATE==0){
                        holder.tv_ss.setTextColor(getResources().getColor(R.color.orange));
                        holder.tv_ss.setText("申诉");
                    }else{
                        holder.tv_ss.setTextColor(getResources().getColor(R.color.gary));
                        holder.tv_ss.setText("已申诉");
                    }
                    break;
                case 3:
                    stateStr = "交易已关闭";
                    holder.tv_ss.setVisibility(View.GONE);
                    holder.line.setVisibility(View.GONE);
                    break;
                default:
                    stateStr = "支付异常";
                    break;
            }
            holder.tv_pay_state.setText(stateStr);
			holder.tv_order_num.setText("订单编号："+data.ORDER_ID);
            String shopName = TextUtils.isEmpty(data.SHOP_NAME)?"":data.SHOP_NAME;
			holder.tv_sell_name.setText("收款商户："+shopName);
			holder.tv_time.setText("交易时间："+ TimeUtil.formatTimeSec(data.PAY_TIME));
//			holder.tv_ss.setText("");
            holder.tv_ss.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (state == 2){

                        if(datas.get(position).APPEAL_STATE==0) {
                            Intent intent = new Intent(context, ScanPayShenSuActivity.class);
                            intent.putExtra("id", datas.get(position).ID);
                            startActivity(intent);
                        }
                    }else if (state == 0){
                        Intent intent = new Intent(context, ScanPayActivity.class);
                        intent.putExtra("name", datas.get(position).SHOP_NAME);
                        intent.putExtra("id", datas.get(position).ID);
                        intent.putExtra("seller_id", datas.get(position).SELLER_ID);
                        intent.putExtra("total_amount", datas.get(position).TOTAL_AMOUNT+"");
                        intent.putExtra("pay_amount", datas.get(position).PAY_AMOUNT+"");
                        intent.putExtra("score_num", datas.get(position).SCORE_NUM+"");
                        intent.putExtra("money_set", datas.get(position).MONEY_SET+"");
                        intent.putExtra("member_leave_msg", datas.get(position).MEMBER_LEAVE_MSG);
                        intent.putExtra("seller_leave_msg", datas.get(position).SELLER_LEVEL_MSG);
                        startActivity(intent);
                    }
                }
            });
            holder.tv_detail.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ScanPayDetailActivity.class);
                    intent.putExtra("id",datas.get(position).ID);
                    startActivity(intent);
                }
            });
            return convertView;
        }

    }

    private class OrderHolder {
        private TextView tv_pay, tv_pay_state,tv_fact_pay, tv_order_num, tv_sell_name, tv_time, tv_ss, tv_detail;
        private View line;
    }

}
