package com.ksource.hbpostal.fragment;

import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.activity.ActivityCate;
import com.ksource.hbpostal.activity.DeliverActivity;
import com.ksource.hbpostal.activity.LoginActivity;
import com.ksource.hbpostal.activity.OrderDetailActivity;
import com.ksource.hbpostal.activity.OrderValueActivity;
import com.ksource.hbpostal.activity.PayActivity;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.OrderResultBean;
import com.ksource.hbpostal.bean.OrderResultBean.PageInfoBean.DatasBean;
import com.ksource.hbpostal.bean.OrderResultBean.PageInfoBean.DatasBean.GoodsListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.widgets.MyListView;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.ConvertUtil;
import com.yitao.util.DialogUtil;
import com.yitao.util.NumberUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

public abstract class BaseOrderFragment extends BaseFragment implements
        OnClickListener {

    public PullToRefreshListView lv;
    public ListView lv_order;
    public List<DatasBean> datas;
    public RelativeLayout rl_error, rl_order_null;
    public Button btn_gosee;
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
        btn_gosee = (Button) view.findViewById(R.id.btn_gosee);
        btn_gosee.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
                startActivity(new Intent(context, ActivityCate.class));
            }
        });

        lv.setPullRefreshEnabled(true);
        lv.setPullLoadEnabled(false);
        lv.setScrollLoadEnabled(true);
        lv_order = lv.getRefreshableView();
        lv_order.setDivider(null);
        rl_error.setOnClickListener(this);
        lv_order.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("orderId", datas.get(position).ORDER_ID);
                startActivity(intent);
            }
        });
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
                currPage = 1;
                adapter = null;
                lv.doPullRefreshing(true, 100);
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
        currPage = 1;
        adapter = null;
        lv.doPullRefreshing(true, 100);
    }

    // 获取信息
    public void getData(final int state) {

        // mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
        // "数据加载中...");
//		if (!NetStateUtils.isNetworkAvailable(context)) {
//			ToastUtil.showTextToast(context, "当前网络不可用！");
//			DialogUtil.getInstance().dialogDismiss(mLoadDialog);
//			return;
//		}
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("pageSize", "10");
        params.put("currPage", "" + currPage);
        if (state != 10) {
            params.put("state", state + "");
        }
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                OrderResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(arg0, OrderResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取订单失败！");
                    rl_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (resultBean.success) {
                    rl_error.setVisibility(View.GONE);
                    rl_order_null.setVisibility(View.GONE);
                    if (isUpdate) {
                        datas.clear();
                    }
                    datas.addAll(resultBean.pageInfo.datas);
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
                    lv.setHasMoreData(resultBean.pageInfo.datas.size() >= 10);
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
                ToastUtil.showTextToast(context, "获取订单失败！");
                rl_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_ORDER_URL, params, callback);
    }

    // 取消订单
    public void delOrder(final String id) {

        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "取消订单...");
        Map<String, String> params = new HashMap<String, String>();
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
                    ToastUtil.showTextToast(context, "取消订单失败！");
                    return;
                }
                if (baseResult.success) {
                    ToastUtil.showTextToast(context, "订单已取消！");
                    lv.doPullRefreshing(true, 100);
                } else {
                    if (baseResult.flag == 10) {
                        mApplication.login();
                        // confirmOrder();
                    }
                    ToastUtil.showTextToast(context, baseResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "取消订单失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.DEL_ORDER_URL, params, callback);
    }

    // 订单确认收货
    public void confirmOrder(final String id) {

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
//					ToastUtils.showShortToast("确认收货失败！");
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
                                            lv.doPullRefreshing(true,
                                                    100);
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
                ToastUtils.showShortToast("确认收货失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.CONFIRM_ORDER_URL, params, callback);
    }

    class OrderAdapter extends DefaultBaseAdapter<DatasBean> {

        public OrderHolder holder;

        public OrderAdapter(List<DatasBean> datas) {
            super(datas);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View.inflate(context,
                        R.layout.item_order_listview, null);
                holder = new OrderHolder();
                holder.tv_order_id = (TextView) convertView
                        .findViewById(R.id.tv_order_id);
                holder.tv_order_zt = (TextView) convertView
                        .findViewById(R.id.tv_order_zt);
                holder.lv_order_goods = (MyListView) convertView
                        .findViewById(R.id.lv_order_goods);
                holder.tv_count = (TextView) convertView
                        .findViewById(R.id.tv_count);
                holder.tv_jifen = (TextView) convertView
                        .findViewById(R.id.tv_jifen);
                holder.tv_left = (TextView) convertView
                        .findViewById(R.id.btn_left);
                holder.tv_right = (TextView) convertView
                        .findViewById(R.id.btn_right);
                holder.tv_yf_l = (TextView) convertView
                        .findViewById(R.id.tv_yf_l);
                holder.tv_yf_r = (TextView) convertView
                        .findViewById(R.id.tv_yf_r);
                holder.tv_send_price = (TextView) convertView
                        .findViewById(R.id.tv_send_price);
                convertView.setTag(holder);

            } else {
                holder = (OrderHolder) convertView.getTag();

            }

            // 填数据
            final String orderId = datas.get(position).ID;
            String sendPriceStr = datas.get(position).SEND_PRICE;
            sendPriceStr = (TextUtils.isEmpty(sendPriceStr)) ? "0"
                    : sendPriceStr;
            double sendPrice = ConvertUtil.obj2Double(sendPriceStr);
            if (sendPrice == 0) {
                holder.tv_yf_l.setVisibility(View.GONE);
                holder.tv_yf_r.setVisibility(View.GONE);
                holder.tv_send_price.setVisibility(View.GONE);
            } else {
                holder.tv_yf_l.setVisibility(View.VISIBLE);
                holder.tv_yf_r.setVisibility(View.VISIBLE);
                holder.tv_send_price.setVisibility(View.VISIBLE);
                holder.tv_send_price.setText("￥" + sendPrice);
            }
            if (datas.get(position).SEND_TYPE == 2) {
                holder.tv_order_zt.setVisibility(View.VISIBLE);
                holder.tv_order_zt.setText("自提");
                if (datas.get(position).BUY_WAY == 2) {
                    holder.tv_order_zt.setText("兑换");
                }
            } else {
                holder.tv_order_zt.setVisibility(View.GONE);
            }

            holder.tv_order_id.setText(datas.get(position).ORDER_ID);
            holder.tv_count.setText("" + datas.get(position).TOTAL_NUM);
            // holder.tv_jifen.setText("￥" + datas.get(position).TOTAL_PRICE +
            // "+"
            // + datas.get(position).TOTAL_JF + "积分");
            final double totalPrice = NumberUtil.add(
                    ConvertUtil.obj2Double(datas.get(position).TOTAL_PRICE),
                    sendPrice);
            final int totalJF = ConvertUtil
                    .obj2Int(datas.get(position).TOTAL_JF);
            if (totalPrice != 0 && totalJF == 0) {
                holder.tv_jifen.setText("￥" + NumberUtil.toDecimal2(totalPrice));
            } else if (totalJF != 0 && totalPrice == 0) {
                holder.tv_jifen.setText(totalJF + "积分");
            } else {
                holder.tv_jifen
                        .setText("￥" + totalPrice + "+" + totalJF + "积分");
            }
            final int payState = datas.get(position).PAY_STATE;
            final int state = datas.get(position).STATE;
            final int sendType = datas.get(position).SEND_TYPE;
            if (payState == 1) {
                switch (state) {
                    case 4:
                        holder.tv_left.setVisibility(View.GONE);
                        holder.tv_right.setVisibility(View.VISIBLE);
                        holder.tv_right.setText("已完成");
                        break;
                    case 1:
                        holder.tv_left.setVisibility(View.GONE);
                        holder.tv_right.setVisibility(View.GONE);
                        holder.tv_right.setText("提醒发货");
                        break;
                    case 2:
                        if (sendType == 1) {
                            holder.tv_left.setVisibility(View.VISIBLE);
                            holder.tv_left.setText("查看物流");
                        } else {
                            holder.tv_left.setVisibility(View.GONE);
                        }
                        holder.tv_right.setVisibility(View.VISIBLE);
                        holder.tv_right.setText("确认收货");
                        break;
                    case 3:
                        if (sendType == 1) {
                            holder.tv_left.setVisibility(View.VISIBLE);
                            holder.tv_left.setText("查看物流");
                        } else {
                            holder.tv_left.setVisibility(View.GONE);
                        }
                        holder.tv_right.setVisibility(View.GONE);
                        // holder.tv_right.setText("去评价");
                        break;

                    default:
                        break;
                }
            } else {
                holder.tv_left.setText("取消订单");
                holder.tv_left.setVisibility(View.VISIBLE);
                holder.tv_right.setVisibility(View.VISIBLE);
                holder.tv_right.setText("立即支付");
            }
            for (int i = 0; i < datas.get(position).goodsList.size(); i++) {
                datas.get(position).goodsList.get(i).STATE = datas
                        .get(position).STATE;
            }
            // datas.get(position).goodsList.get(position).STATE =
            // datas.get(position).STATE;
            holder.lv_order_goods.setAdapter(new MyAdapter(
                    datas.get(position).goodsList));
            holder.tv_left.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (payState == 1) {
                        switch (state) {
                            case 4:
                                break;
                            case 1:
                                break;
                            case 2:
                            case 3:
                                // ToastUtil.showTextToast(context, "Sorry!暂无物流信息");
                                Intent intent = new Intent(context,
                                        DeliverActivity.class);
                                intent.putExtra("num",
                                        datas.get(position).EXPRESS_CODE);
                                intent.putExtra("count", ""
                                        + datas.get(position).TOTAL_NUM);
                                intent.putExtra(
                                        "imageUrl",
                                        ""
                                                + datas.get(position).goodsList
                                                .get(0).IMAGE);
                                startActivity(intent);
                                break;

                            default:
                                break;
                        }
                    } else {
                        SweetAlertDialog dialog = new SweetAlertDialog(
                                context, SweetAlertDialog.WARNING_TYPE);
                        dialog.setTitleText("确定要取消该订单？")
                                .setContentText("该订单取消后不可恢复！")
                                .setCancelText("先不了")
                                .setConfirmText("是的，我要取消")
                                .showCancelButton(true)
                                .setConfirmClickListener(
                                        new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(
                                                    SweetAlertDialog sDialog) {
                                                sDialog.dismiss();
                                                delOrder(datas.get(position).ID);
                                            }
                                        }).show();
                    }
                }
            });
            holder.tv_right.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (payState == 1) {
                        switch (state) {
                            case 4:
                                ToastUtil.showTextToast(context, "订单已完成！");
                                break;
                            case 1:
                                SystemClock.sleep(500);
                                ToastUtil.showTextToast(context, "消息已发送！已提醒卖家发货");
                                break;
                            case 2:
                                SweetAlertDialog dialog = new SweetAlertDialog(
                                        context, SweetAlertDialog.WARNING_TYPE);
                                dialog.setTitleText("确定要确认收货？")
                                        .setContentText("确认收货收货后将向卖家付款")
                                        .setCancelText("取消")
                                        .setConfirmText("确认收货")
                                        .showCancelButton(true)
                                        .setConfirmClickListener(
                                                new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(
                                                            SweetAlertDialog sDialog) {
                                                        sDialog.dismiss();
                                                        confirmOrder(datas
                                                                .get(position).ID);
                                                    }
                                                }).show();
                                break;
                            case 3:
                                // if (datas.get(position).goodsList.size() == 1) {
                                // Intent intent = new Intent(context,
                                // OrderValueActivity.class);
                                // intent.putExtra("orderId",
                                // datas.get(position).ID);
                                // intent.putExtra("goodsBean",
                                // datas.get(position).goodsList.get(0));
                                // startActivity(intent);
                                // } else {
                                // Intent intent = new Intent(context,
                                // OrderListActivity.class);
                                // intent.putExtra("orderId",
                                // datas.get(position).ID);
                                // Bundle bundle = new Bundle();
                                // bundle.putSerializable(
                                // "goodsList",
                                // (Serializable) datas.get(position).goodsList);
                                // intent.putExtra("bundle", bundle);
                                // startActivity(intent);
                                // }
                                break;

                            default:
                                break;
                        }
                    } else {
                        // 支付
                        Intent intent = new Intent(context, PayActivity.class);
                        intent.putExtra("orderId", datas.get(position).ID);
                        intent.putExtra("money", "" + totalPrice);
                        intent.putExtra("jifen", "" + totalJF);
                        String goodsName = "";
                        for (GoodsListBean goodsBean : datas.get(position).goodsList) {
                            goodsName += goodsBean.GOODS_NAME + ",";
                        }
                        goodsName = goodsName.substring(0,
                                goodsName.length() - 1);
                        intent.putExtra("goodsName", goodsName);
                        intent.putExtra("image", datas.get(0).goodsList.get(0).IMAGE);
                        startActivity(intent);
                    }
                }
            });
            holder.lv_order_goods
                    .setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            Intent intent = new Intent(context,
                                    OrderDetailActivity.class);
                            intent.putExtra("orderId", orderId);
                            startActivity(intent);

                        }
                    });
            return convertView;
        }

    }

    class OrderHolder {
        public TextView tv_order_id;
        public TextView tv_count;
        public TextView tv_order_zt;
        public TextView tv_jifen;
        public TextView tv_left;
        public TextView tv_send_price;
        public TextView tv_yf_l;
        public TextView tv_yf_r;
        public TextView tv_right;
        public MyListView lv_order_goods;
    }

    class MyAdapter extends DefaultBaseAdapter<GoodsListBean> {

        public ViewHolder holder;

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
            if (useMoney == 0.0) {
                type = 3;
            } else if (useScore == 0) {
                type = 1;
            } else {
                type = 2;
            }
            switch (type) {
                case 1:
                    holder.tv_goods_price.setVisibility(View.VISIBLE);
                    holder.tv_goods_price.setText("￥" + NumberUtil.toDecimal2(useMoney));
                    holder.tv_add.setVisibility(View.GONE);
                    holder.tv_jifen_name.setVisibility(View.GONE);
                    holder.tv_goods_jifen.setVisibility(View.GONE);
                    break;
                case 2:
                    holder.tv_goods_price.setVisibility(View.VISIBLE);
                    holder.tv_goods_price.setText("￥" + NumberUtil.toDecimal2(useMoney));
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
            final int state = datas.get(position).STATE;
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
                                OrderValueActivity.class);
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
        public ImageView iv_goods_icon;
        public TextView tv_name_list_item;
        public TextView tv_goods_jifen;
        public TextView tv_cate_name;
        public TextView tv_add;
        public TextView tv_review_state;
        public TextView tv_jifen_name;
        public TextView tv_goods_price;
        public TextView tv_goods_number;
    }

}
