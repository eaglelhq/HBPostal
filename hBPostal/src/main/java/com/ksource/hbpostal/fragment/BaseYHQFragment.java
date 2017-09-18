package com.ksource.hbpostal.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.activity.LoginActivity;
import com.ksource.hbpostal.activity.ScanActivity;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.MyYHQResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.TimeUtil;
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

import okhttp3.Call;

public abstract class BaseYHQFragment extends BaseFragment implements
        OnClickListener {

    public PullToRefreshListView lv;
    public ListView lv_order;
    public List<MyYHQResultBean.CouponListBean> datas;
    public RelativeLayout rl_error, rl_order_null;
    public Button btn_gosee;
    public BaseAdapter adapter;
    public LoadDialog mLoadDialog;

    public int currPage = 1;
    public boolean isUpdate;
    private String token;
    private TextView tv_tishi;

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
//        datas.add(new CardResaultBean.BankCardListBean());
//        datas.add(new CardResaultBean.BankCardListBean());
//        datas.add(new CardResaultBean.BankCardListBean());
//        datas.add(new CardResaultBean.BankCardListBean());
//        datas.add(new CardResaultBean.BankCardListBean());
//        adapter = new ZHAdapter(datas);
//        lv_order.setAdapter(adapter);
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

    /**
     * 获取优惠券信息
     */
    private void getData(int state) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("pageSize", "10");
        params.put("currPage", "" + currPage);
        if (state != 10) {
            params.put("state", state + "");
        } else {
            params.put("state", "");
        }
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                MyYHQResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(
                            arg0, MyYHQResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取优惠券失败！");
                    rl_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (resultBean.success) {
                    rl_error.setVisibility(View.GONE);
                    rl_order_null.setVisibility(View.GONE);
                    if (isUpdate) {
                        datas.clear();
                    }
                    datas.addAll(resultBean.couponList);
                    if (adapter == null) {
                        adapter = new MyAdapter(datas);
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
                    lv.setHasMoreData(resultBean.couponList.size() >= 10);
                    if (datas == null || datas.size() == 0) {
                        rl_order_null.setVisibility(View.VISIBLE);
                    }
                } else if (resultBean.flag == 10) {
                    mApplication.login();
                    // getData();
                } else {
                    ToastUtil
                            .showTextToast(context, resultBean.msg);
                    rl_error.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                rl_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_YHQ_LIST_URL, params, callback);

    }


    class MyAdapter extends DefaultBaseAdapter<MyYHQResultBean.CouponListBean> {

        public MyAdapter(List<MyYHQResultBean.CouponListBean> datas) {
            super(datas);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View
                        .inflate(context, R.layout.item_dh_yhq, null);
                holder = new ViewHolder();

                holder.btn_change = (TextView) convertView.findViewById(R.id.btn_change);
                holder.tv_yhq_type = (TextView) convertView.findViewById(R.id.tv_yhq_type);
                holder.tv_use_num = (TextView) convertView.findViewById(R.id.tv_use_num);
                holder.tv_money_fh = (TextView) convertView.findViewById(R.id.tv_money_fh);
                holder.tv_yhq_money = (TextView) convertView.findViewById(R.id.tv_yhq_money);
                holder.tv_yhq_type_name = (TextView) convertView.findViewById(R.id.tv_yhq_type_name);
                holder.tv_yhq_name = (TextView) convertView.findViewById(R.id.tv_yhq_name);
                holder.tv_yhq_use = (TextView) convertView.findViewById(R.id.tv_yhq_use);
                holder.tv_yhq_time = (TextView) convertView.findViewById(R.id.tv_yhq_time);
                holder.tv_jyb = (TextView) convertView.findViewById(R.id.tv_jyb);
                holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.iv_timeout = (ImageView) convertView.findViewById(R.id.iv_timeout);
                holder.left_view = convertView.findViewById(R.id.left_view);
                holder.ll_money = (LinearLayout) convertView.findViewById(R.id.ll_money);
                holder.rl_quan = (RelativeLayout) convertView.findViewById(R.id.rl_quan);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

//            if (position == 0 || (position > 0 && datas.get(position).SIGN_STATE != datas.get(position - 1).SIGN_STATE)) {
            holder.tv_yhq_type.setVisibility(View.GONE);
            holder.iv_icon.setVisibility(View.GONE);
            holder.tv_jyb.setVisibility(View.GONE);
            holder.btn_change.setVisibility(View.GONE);
            holder.iv_icon.setVisibility(View.GONE);
            holder.rl_quan.setVisibility(View.VISIBLE);
            holder.ll_money.setVisibility(View.VISIBLE);
            holder.tv_yhq_money.setText(datas.get(position).DENOMINATION);
            int limit = datas.get(position).DAILY_LIMIT;
            if (limit >0) {
                holder.tv_use_num.setVisibility(View.VISIBLE);
                holder.tv_use_num.setText("限* " + limit);//+"/天"
            }else{
                holder.tv_use_num.setVisibility(View.GONE);
            }
            holder.tv_yhq_use.setText("满" + datas.get(position).TAKE_EFFECT_PRICE + "元即可使用");
            holder.tv_yhq_time.setText("有效期至：" + TimeUtil.formatTimeDay(datas.get(position).OUT_TIME));
            if (1 == datas.get(position).COUPON_CATE) {
                holder.left_view.setBackgroundResource(R.drawable.rect_greencao_left);
                holder.tv_yhq_type_name.setText("快递券");
                holder.tv_yhq_name.setText("邮政快递优惠券");
            } else if (2 == datas.get(position).COUPON_CATE) {
                holder.left_view.setBackgroundResource(R.drawable.rect_green_left);
                holder.tv_yhq_type_name.setText("满减券");
                String shopName = datas.get(position).SHOP_NAME == null ? "" : datas.get(position).SHOP_NAME;
                holder.tv_yhq_name.setText(shopName + "商家优惠券");
            }
            int state = datas.get(position).COUPON_STATE;
            if (3 == state) {
                holder.tv_state.setTextColor(getResources().getColor(R.color.lightgreen));
                holder.tv_state.setBackgroundResource(R.drawable.rect_lightgreen_bk);
                holder.tv_state.setText("立即使用");
                holder.iv_timeout.setVisibility(View.GONE);
                holder.tv_state.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 跳转到扫码付
//                        ToastUtils.showShortToast("立即使用");
                        startActivity(new Intent(context, ScanActivity.class));
                    }
                });
            } else if (4 == state) {
                holder.tv_state.setTextColor(getResources().getColor(R.color.gray));
                holder.tv_state.setBackgroundResource(R.drawable.rect_gary_bk);
                holder.tv_state.setText("已使用");
                holder.iv_timeout.setVisibility(View.GONE);
            } else if (5 == state) {
                holder.tv_state.setText("已过期");
                holder.iv_timeout.setVisibility(View.VISIBLE);
                holder.tv_state.setTextColor(getResources().getColor(R.color.gray));
                holder.tv_state.setBackgroundResource(R.drawable.rect_gary_bk);
                holder.tv_money_fh.setTextColor(getResources().getColor(R.color.gray));
                holder.tv_yhq_money.setTextColor(getResources().getColor(R.color.gray));
                holder.tv_yhq_type_name.setTextColor(getResources().getColor(R.color.gray));
                holder.tv_yhq_name.setTextColor(getResources().getColor(R.color.gray));
                holder.left_view.setBackgroundResource(R.drawable.rect_gray_left);
            }
            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv_yhq_type, tv_yhq_money, tv_yhq_type_name, tv_yhq_name, tv_yhq_use,
                tv_yhq_time, tv_jyb, btn_change, tv_state, tv_money_fh,tv_use_num;
        ImageView iv_icon, iv_timeout;
        View left_view;
        LinearLayout ll_money;
        RelativeLayout rl_quan;
    }

}
