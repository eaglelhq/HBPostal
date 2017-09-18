package com.ksource.hbpostal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.DYYHQResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 组合优惠券详情
 * Created by Administrator on 2017/3/8 0008.
 */

public class YHQDetailActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private PullToRefreshListView lv;
    private ListView lv_card;
    private List<DYYHQResultBean.CouponListBean> datas;
    private DYYHQResultBean.CouponListBean couponList;
    private BaseAdapter adapter;
    private LoadDialog mLoadDialog;
    private RelativeLayout rl_error, rl_null;
    private TextView btn_get, tv_coins;
    private String token;
    private String id;
    private int currPage;
    private boolean isUpdate;
    private boolean canChange = true;
    private int coins;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_yhq_detail;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_coins = (TextView) findViewById(R.id.tv_coins);
        btn_get = (TextView) findViewById(R.id.btn_get);
        lv = (PullToRefreshListView) findViewById(R.id.lv_yhq);

        rl_error = (RelativeLayout) findViewById(R.id.rl_error);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_get.setOnClickListener(this);
        rl_error.setOnClickListener(this);
        lv.setPullRefreshEnabled(true);
        lv.setPullLoadEnabled(false);
        lv.setScrollLoadEnabled(true);
        lv.setHasMoreData(false);
        lv_card = lv.getRefreshableView();
        lv_card.setDivider(null);
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
                currPage++;
                getData();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter = null;
//		isUpdate = true;
//		currPage = 1;
//        getData();
        lv.doPullRefreshing(true, 500);
    }

    @Override
    public void initData() {
        tv_title.setText("优惠券领取");
        token = sp.getString(ConstantValues.TOKEN, "");
        id = getIntent().getStringExtra("id");
        if (getIntent().hasExtra("bundle")) {
            Bundle bundle = getIntent().getBundleExtra("bundle");
            couponList = (DYYHQResultBean.CouponListBean) bundle.getSerializable("couponList");
        }
        datas = new ArrayList<>();
        if (TextUtils.isEmpty(token)) {
            // finish();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("isBack", true);
            startActivity(intent);
        }
//        datas.add(new CardResaultBean.BankCardListBean());
//        datas.add(new CardResaultBean.BankCardListBean());
//        adapter = new MyAdapter(datas);
//        lv_card.setAdapter(adapter);
    }

    /**
     * 获取优惠券信息
     */
    private void getData() {
        if (couponList != null ) {
            rl_error.setVisibility(View.GONE);
            rl_null.setVisibility(View.GONE);
            if (isUpdate) {
                datas.clear();
            }
            datas.add(couponList);
            for (DYYHQResultBean.CouponListBean bean : datas) {
                if (bean.COUNT == 0) {
                    canChange = false;
                    break;
                }
            }
            coins = couponList.USE_GOLD_COINS;
            tv_coins.setText( coins + "个");
            if (canChange) {
                btn_get.setText("立即兑换");
                btn_get.setEnabled(true);
            } else {
                btn_get.setText("已兑完");
                btn_get.setEnabled(false);
            }
            if (adapter == null) {
                adapter = new MyAdapter(datas);
                lv_card.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            lv.onPullDownRefreshComplete();
            lv.onPullUpRefreshComplete();
            lv.setHasMoreData(false);
            if (datas == null || datas.size() == 0) {
                rl_null.setVisibility(View.VISIBLE);
            }
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("token", token);
            params.put("PACKAGE_ID", id);
            params.put("pageSize", "10");
            params.put("currPage", "" + currPage);
            StringCallback callback = new StringCallback() {

                @Override
                public void onResponse(String arg0, int arg1) {
                    DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                    Gson gson = new Gson();
                    DYYHQResultBean resultBean = null;
                    try {
                        resultBean = gson.fromJson(
                                arg0, DYYHQResultBean.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (resultBean == null) {
                        ToastUtil.showTextToast(context, "获取优惠券失败！");
                        return;
                    }
                    if (resultBean.success) {
                        rl_error.setVisibility(View.GONE);
                        rl_null.setVisibility(View.GONE);
                        if (isUpdate) {
                            datas.clear();
                        }
                        datas.addAll(resultBean.couponList);
                        for (DYYHQResultBean.CouponListBean bean : datas) {
                            if (bean.COUNT == 0) {
                                canChange = false;
                                break;
                            }
                        }
                        coins = resultBean.COINS;
                        tv_coins.setText(coins + "个");
                        if (canChange) {
                            btn_get.setText("立即兑换");
                            btn_get.setEnabled(true);
                        } else {
                            btn_get.setText("已兑完");
                            btn_get.setEnabled(false);
                        }
                        if (adapter == null) {
                            adapter = new MyAdapter(datas);
                            lv_card.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        lv.onPullDownRefreshComplete();
                        lv.onPullUpRefreshComplete();
                        lv.setHasMoreData(resultBean.couponList.size() >= 10);
                        if (datas == null || datas.size() == 0) {
                            rl_null.setVisibility(View.VISIBLE);
                        }
                    } else if (resultBean.flag == 10) {
                        mApplication.login();
                        // getData();
                    } else {
                        ToastUtil
                                .showTextToast(context, resultBean.msg);
                    }

                }

                @Override
                public void onError(Call arg0, Exception arg1, int arg2) {
                    DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                    rl_error.setVisibility(View.VISIBLE);
                }
            };
            DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_ZH_YHQ_LIST_URL, params, callback);

        }
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
                isUpdate = true;
                currPage = 1;
                getData();
//			lv.doPullRefreshing(true, 500);
                break;
            case R.id.btn_get:
                final AlertDialog dialog = new AlertDialog.Builder(context).create();
                dialog.setTitle("兑换优惠券");
                dialog.setMessage("确定要使用"+coins+"金邮币兑换优惠券吗？");
                dialog.setButton(dialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getYhq();
                    }
                });
                dialog.setButton(dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gary));
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                break;

            default:
                break;
        }

    }

    // 领取优惠券
    private void getYhq() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "领取优惠券...");
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        if (couponList != null ) {
            params.put("type", "1");
            params.put("couponId", couponList.COUPON_ID);
        }else{
            params.put("type", "2");
            params.put("couponId", id);
        }
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
//						new SweetAlertDialog(context,
//								SweetAlertDialog.ERROR_TYPE)
//								.setTitleText("领取优惠券失败！").show();
                    ToastUtils.showShortToast("领取优惠券失败！");
                    return;
                }
                if (baseResult.success) {
                    // datas.remove(position);
                    // adapter.notifyDataSetChanged();
//                    ToastUtils.showShortToast("兑换优惠券成功！");
                    final AlertDialog dialog = new AlertDialog.Builder(context).create();
                    dialog.setTitle("兑换优惠券成功！");
                    dialog.setMessage("去查看我的优惠券");
                    dialog.setButton(dialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(context,YhqActivity.class));
                        }
                    });
                    dialog.setButton(dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gary));
                    dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            adapter = null;
                            isUpdate = true;
                            currPage = 1;
                            getData();
                        }
                    });

//						lv.doPullRefreshing(true, 500);
                } else {
                    if (baseResult.flag == 10) {
                        mApplication.login();
                        // removeData(id, position);
                    }
//					new SweetAlertDialog(context,
//								SweetAlertDialog.ERROR_TYPE)
//								.setTitleText("领取优惠券失败！")
//								.setContentText(baseResult.msg).show();
                    ToastUtils.showShortToast("领取优惠券失败！" + baseResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtils.showShortToast("领取优惠券失败！");
//				new SweetAlertDialog(context,
//						SweetAlertDialog.ERROR_TYPE).setTitleText(
//						"领取优惠券失败！").show();

            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_YHQ_URL, params, callback);
    }

    class MyAdapter extends DefaultBaseAdapter<DYYHQResultBean.CouponListBean> {

        private ViewHolder holder;

        public MyAdapter(List<DYYHQResultBean.CouponListBean> datas) {
            super(datas);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View
                        .inflate(context, R.layout.item_dh_yhq, null);
                holder = new ViewHolder();

                holder.btn_change = (TextView) convertView.findViewById(R.id.btn_change);
                holder.tv_yhq_type = (TextView) convertView.findViewById(R.id.tv_yhq_type);
                holder.tv_yhq_money = (TextView) convertView.findViewById(R.id.tv_yhq_money);
                holder.tv_yhq_type_name = (TextView) convertView.findViewById(R.id.tv_yhq_type_name);
                holder.tv_yhq_name = (TextView) convertView.findViewById(R.id.tv_yhq_name);
                holder.tv_yhq_use = (TextView) convertView.findViewById(R.id.tv_yhq_use);
                holder.tv_yhq_time = (TextView) convertView.findViewById(R.id.tv_yhq_time);
                holder.tv_jyb = (TextView) convertView.findViewById(R.id.tv_jyb);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.left_view = convertView.findViewById(R.id.left_view);
                holder.ll_money = (LinearLayout) convertView.findViewById(R.id.ll_money);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

//            if (position == 0 || (position > 0 && datas.get(position).SIGN_STATE != datas.get(position - 1).SIGN_STATE)) {
            holder.tv_yhq_type.setVisibility(View.GONE);
            holder.iv_icon.setVisibility(View.GONE);
            holder.tv_yhq_money.setText(datas.get(position).DENOMINATION + "");
            holder.tv_yhq_use.setText("满" + datas.get(position).TAKE_EFFECT_PRICE + "元即可使用");
            holder.tv_yhq_time.setText("有效期" + datas.get(position).VALID_DAYS + "天");
            String shopName = datas.get(position).SHOP_NAME == null ? "" : datas.get(position).SHOP_NAME;
            holder.tv_yhq_name.setText(shopName + "商家优惠券");

            if (datas.get(position).COUPON_CATE == 1) {
                holder.left_view.setBackgroundResource(R.drawable.rect_greencao_left);
                holder.tv_yhq_type_name.setText("快递券");
            } else {
                holder.left_view.setBackgroundResource(R.drawable.rect_green_left);
                holder.tv_yhq_type_name.setText("满减券");
            }

            holder.tv_jyb.setVisibility(View.GONE);
            holder.btn_change.setVisibility(View.GONE);
            return convertView;
        }
    }

    class ViewHolder {
        // private TextView tv_card_name;
        private TextView tv_yhq_type, tv_yhq_money, tv_yhq_type_name, tv_yhq_name, tv_yhq_use, tv_yhq_time, tv_jyb, btn_change;
        private ImageView iv_icon;
        private View left_view;
        private LinearLayout ll_money;
    }
}
