package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.DYYHQResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.widgets.MyListView;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.yitao.widget.BottomScrollView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 兑换优惠券
 * Created by Administrator on 2017/3/8 0008.
 */

public class DHYhqActivity extends BaseActivity {

    private PullToRefreshScrollView mPullRefreshScrollView;
    private BottomScrollView mScrollView;
    private TextView tv_title;
    private ImageView iv_back;
    private MyListView lv_yhq, lv_zhq;
    private BaseAdapter yhqAdapter, zhAdapter;
    private LoadDialog mLoadDialog;
    private RelativeLayout rl_error, rl_null;
    private Button btn_add;
    private String token;

    private int currPage = 1;
    public boolean isUpdate;
    private List<DYYHQResultBean.CouponListBean> couponList;
    private List<DYYHQResultBean.CouponPackageListBean> couponPackageList;
    private boolean isHasMore;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_yhq;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        btn_add = (Button) findViewById(R.id.btn_add);
        lv_yhq = (MyListView) findViewById(R.id.lv_yhq);
        lv_zhq = (MyListView) findViewById(R.id.lv_zhq);
        mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mScrollView = mPullRefreshScrollView.getRefreshableView();
        rl_error = (RelativeLayout) findViewById(R.id.rl_error);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        rl_error.setOnClickListener(this);
        lv_zhq.setDivider(null);
        lv_zhq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                getYhq(couponList.get(i).COUPON_ID);
                //调转到优惠券详情
                Intent intent = new Intent(context, YHQDetailActivity.class);
                intent.putExtra("id", couponPackageList.get(i).PACKAGE_ID);
                startActivity(intent);
            }
        });
        lv_yhq.setDivider(null);
        lv_yhq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                getYhq(couponList.get(i).COUPON_ID);
                Intent intent = new Intent(context, YHQDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("couponList", (Serializable) couponList.get(i));
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });
        mScrollView.setVerticalScrollBarEnabled(false);
        mScrollView.setOnScrollToBottomLintener(new BottomScrollView.OnScrollToBottomListener() {

            @Override
            public void onScrollBottomListener(boolean isBottom) {
                if (isBottom && isHasMore) {
                    // ToastUtil.showTextToast(context, "SCROLLVIEW"+isBottom +
                    // "");
                    // mLoadDialog =
                    // DialogUtil.getInstance().showLoadDialog(context,
                    // "数据加载中...");
                    isUpdate = false;
                    currPage++;
                    getData();
                }

            }
        });
        mPullRefreshScrollView.setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.PULL_FROM_START);
        mPullRefreshScrollView
                .setOnRefreshListener(new com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener<BottomScrollView>() {


                    @Override
                    public void onRefresh(
                            com.handmark.pulltorefresh.library.PullToRefreshBase<BottomScrollView> refreshView) {
                        currPage = 1;
                        isUpdate = true;
                        getData();
                    }

                });
    }


    @Override
    public void initData() {
        tv_title.setText("优惠券领取");
        token = sp.getString(ConstantValues.TOKEN, "");
        if (TextUtils.isEmpty(token)) {
            // finish();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("isBack", true);
            startActivity(intent);
        }
        couponList = new ArrayList<>();
        couponPackageList = new ArrayList<>();
//        datas.add(new CardResaultBean.BankCardListBean());
//        datas.add(new CardResaultBean.BankCardListBean());
//        datas.add(new CardResaultBean.BankCardListBean());
//        datas.add(new CardResaultBean.BankCardListBean());
//        datas.add(new CardResaultBean.BankCardListBean());
//        zhAdapter = new ZHAdapter(datas);
//        lv_zhq.setAdapter(zhAdapter);
        getData();
    }

    /**
     * 获取优惠券信息
     */
    private void getData() {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("pageSize", "10");
        params.put("currPage", "" + currPage);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                mPullRefreshScrollView.onRefreshComplete();
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
                    rl_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (resultBean.success) {
                    rl_error.setVisibility(View.GONE);
                    rl_null.setVisibility(View.GONE);
                    if (isUpdate) {
                        couponList.clear();
                        couponPackageList.clear();
                    }
                    couponList.addAll(resultBean.couponList);
                    couponPackageList.addAll(resultBean.couponPackageList);
                    isHasMore = resultBean.couponList.size() + resultBean.couponPackageList.size() >= 10;
                    if (yhqAdapter == null) {
                        yhqAdapter = new YHQAdapter(couponList);
                        lv_yhq.setAdapter(yhqAdapter);
                    } else {
                        yhqAdapter.notifyDataSetChanged();
                    }
                    if (zhAdapter == null) {
                        zhAdapter = new ZHAdapter(couponPackageList);
                        lv_zhq.setAdapter(zhAdapter);
                    } else {
                        zhAdapter.notifyDataSetChanged();
                    }
//                    if (isUpdate) {
//                        lv_zhq.setSelection(0);
//                    }
//                    lv_zhq.setVisibility(View.VISIBLE);
                    if ((couponPackageList == null || couponPackageList.size() == 0) && (couponList == null || couponList.size() == 0)) {
                        rl_null.setVisibility(View.VISIBLE);
                    }
                } else if (resultBean.flag == 10) {
                    mApplication.login();
                } else {
                    ToastUtil
                            .showTextToast(context, resultBean.msg);
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                mPullRefreshScrollView.onRefreshComplete();
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                rl_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_DH_YHQ_LIST_URL, params, callback);

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
                zhAdapter = null;
                yhqAdapter = null;
                isUpdate = true;
                currPage = 1;
                getData();
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

    // 领取优惠券请求
    private void getYhq(final String id) {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "领取优惠券...");
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("type", "1");
        params.put("couponId", id);
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
                    // zhAdapter.notifyDataSetChanged();
                    ToastUtils.showShortToast("领取优惠券成功！");
//                    zhAdapter = null;
//                    yhqAdapter = null;
                    isUpdate = true;
                    currPage = 1;
                    getData();
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

    //组合券
    class ZHAdapter extends DefaultBaseAdapter<DYYHQResultBean.CouponPackageListBean> {

        private ViewHolder holder;

        public ZHAdapter(List<DYYHQResultBean.CouponPackageListBean> datas) {
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

            if (position == 0) {
                holder.tv_yhq_type.setVisibility(View.VISIBLE);
                holder.tv_yhq_type.setText("组合券");
            } else {
                holder.tv_yhq_type.setVisibility(View.GONE);
            }
            holder.tv_yhq_type_name.setText(datas.get(position).PACKAGE_NAME);
            String couponName = datas.get(position).COUPON_NAME;
            couponName = couponName.replaceAll(",","+\n");
            holder.tv_yhq_name.setText(couponName);
            holder.tv_jyb.setText(datas.get(position).USE_GOLD_COINS + "金邮币");
            holder.tv_yhq_use.setVisibility(View.GONE);
            holder.tv_yhq_time.setVisibility(View.GONE);
            holder.iv_icon.setVisibility(View.VISIBLE);
            holder.ll_money.setVisibility(View.GONE);
            holder.left_view.setBackgroundResource(R.drawable.rect_bluez_left);

            return convertView;
        }
    }

    //优惠券
    class YHQAdapter extends DefaultBaseAdapter<DYYHQResultBean.CouponListBean> {

        private ViewHolder holder;

        public YHQAdapter(List<DYYHQResultBean.CouponListBean> datas) {
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
            if (datas.get(position).COUNT > 0){
                holder.btn_change.setBackgroundColor(getResources().getColor(R.color.lightgreen));
                holder.btn_change.setText("兑换");
            }else{
                holder.btn_change.setBackgroundColor(getResources().getColor(R.color.gary));
                holder.btn_change.setText("已兑完");
            }
            if (position == 0) {
                holder.tv_yhq_type.setVisibility(View.VISIBLE);
                holder.tv_yhq_type.setText("优惠券");
            } else {
                holder.tv_yhq_type.setVisibility(View.GONE);
            }
            holder.iv_icon.setVisibility(View.VISIBLE);
            holder.ll_money.setVisibility(View.GONE);
            holder.iv_icon.setVisibility(View.GONE);
            holder.ll_money.setVisibility(View.VISIBLE);

            holder.tv_yhq_money.setText(datas.get(position).DENOMINATION);
            holder.tv_yhq_use.setText("满" + datas.get(position).TAKE_EFFECT_PRICE + "元即可使用");
            holder.tv_yhq_time.setText("有效期" + datas.get(position).VALID_DAYS + "天");
            holder.tv_jyb.setText(datas.get(position).USE_GOLD_COINS + "金邮币");
//            holder.btn_change.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (datas.get(position).COUNT > 0) {
//                        getYhq(datas.get(position).COUPON_ID);
//                    }
//                }
//            });
            if (datas.get(position).COUPON_CATE == 1) {
                holder.left_view.setBackgroundResource(R.drawable.rect_greencao_left);
                holder.tv_yhq_name.setText("邮政快递优惠券");
                holder.tv_yhq_type_name.setText("快递券");
            } else {
                holder.left_view.setBackgroundResource(R.drawable.rect_green_left);
                String shopName = datas.get(position).SHOP_NAME == null? "":datas.get(position).SHOP_NAME;
                holder.tv_yhq_name.setText(shopName+"商家优惠券");
                holder.tv_yhq_type_name.setText("满减券");
            }
            return convertView;
        }
    }

    class ViewHolder {
        // private TextView tv_card_name;
        private TextView tv_yhq_type, tv_yhq_money, tv_yhq_type_name, tv_yhq_name, tv_yhq_use,
                tv_yhq_time, tv_jyb, btn_change, tv_state;
        private ImageView iv_icon, iv_timeout;
        private View left_view;
        private LinearLayout ll_money;
        private RelativeLayout rl_quan;
    }
}
