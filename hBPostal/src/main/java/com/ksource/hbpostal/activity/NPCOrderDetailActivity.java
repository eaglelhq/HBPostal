package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.ZPKDetailResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.util.TimeUtil;
import com.ksource.hbpostal.widgets.MyListView;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.ConvertUtil;
import com.yitao.util.DialogUtil;
import com.yitao.util.NetStateUtils;
import com.yitao.util.ToastUtil;
import com.yitao.widget.BottomScrollView;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class NPCOrderDetailActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private ImageView iv_goods_icon;
    private LoadDialog mLoadDialog;
    private TextView tv_sh_name, tv_phone, tv_addr, tv_count, tv_jifen;
    private TextView tv_goods_name, tv_zhouqi, tv_renshu, tv_price, tv_pay;
    private TextView tv_order, tv_creat_time, tv_pay_time, tv_time1,
            tv_order_state, tv_send_price;
    private MyListView lv_order_goods;
    private RelativeLayout rl_null;

    private BottomScrollView mScrollView;

    private String userName = "", phone = "", addr = "";
    private String orderId = "", creatTime = "", fkTime = "", fhTime = "";
    private int count = 0;
    private double money;

    //	private String express = "";
    private List<ZPKDetailResultBean.ZpkDetailBean.PsListBean> datas;
    private BaseAdapter adapter;
    private String id;
    private String goodsName;
    private String image;
    private int payState;
    private TagFlowLayout fl_time;
    private TagAdapter<String> adapterTime;
    private boolean isOpen;

    private ImageView btn_go_top;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_npc_order_detail;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        btn_go_top = (ImageView) findViewById(R.id.btn_go_top);
        iv_goods_icon = (ImageView) findViewById(R.id.iv_goods_icon);
        tv_sh_name = (TextView) findViewById(R.id.tv_sh_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_jifen = (TextView) findViewById(R.id.tv_jifen);
        tv_send_price = (TextView) findViewById(R.id.tv_sendprice);
        tv_order = (TextView) findViewById(R.id.tv_order);
        tv_creat_time = (TextView) findViewById(R.id.tv_creat_time);
        tv_pay_time = (TextView) findViewById(R.id.tv_pay_time);
        tv_time1 = (TextView) findViewById(R.id.tv_time1);
        tv_order_state = (TextView) findViewById(R.id.tv_order_state);
        tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);
        tv_zhouqi = (TextView) findViewById(R.id.tv_zhouqi);
        tv_renshu = (TextView) findViewById(R.id.tv_renshu);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_pay = (TextView) findViewById(R.id.ll_pay);
//		tv_left = (TextView) findViewById(R.id.btn_left);
//		btn_right = (TextView) findViewById(R.id.tv_btn_right);
        lv_order_goods = (MyListView) findViewById(R.id.lv_order_goods);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
        fl_time = (TagFlowLayout) findViewById(R.id.fl_time);
        mScrollView = (BottomScrollView) findViewById(R.id.mScrollView);

    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        tv_pay.setOnClickListener(this);
        btn_go_top.setOnClickListener(this);
        tv_order_state.setOnClickListener(this);
        tv_pay.setVisibility(View.GONE);
        mScrollView.setVerticalScrollBarEnabled(false);
        mScrollView.setOnScrollListener(new BottomScrollView.OnScrollListener() {

            @Override
            public void onScroll(int scrollY) {
                if (scrollY > 2000) {
                    btn_go_top.setVisibility(View.VISIBLE);

                } else {
                    btn_go_top.setVisibility(View.GONE);
                }
            }
        });
        mScrollView.setOnScrollToBottomLintener(new BottomScrollView.OnScrollToBottomListener() {

            @Override
            public void onScrollBottomListener(boolean isBottom) {
//                if (isBottom && isHasMore) {
//                    // ToastUtil.showTextToast(context, "SCROLLVIEW"+isBottom +
//                    // "");
//                    // mLoadDialog =
//                    // DialogUtil.getInstance().showLoadDialog(context,
//                    // "数据加载中...");
//                    currpage++;
//                    getData();
//                }
            }
        });
    }

    @Override
    public void initData() {
        tv_title.setText("宅配卡订单详情");
        id = getIntent().getStringExtra("orderId");
        String token = sp.getString(ConstantValues.TOKEN, null);
        if (TextUtils.isEmpty(token)) {
            // finish();
            startActivity(new Intent(context, LoginActivity.class));
        }
        getData();
    }

    // 获取订单详情
    private void getData() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "数据加载中...");
        if (!NetStateUtils.isNetworkAvailable(context)) {
            ToastUtil.showTextToast(context, "当前网络不可用！");
            DialogUtil.getInstance().dialogDismiss(mLoadDialog);
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        String token = sp.getString(ConstantValues.TOKEN, "");
        params.put("token", token);
        params.put("order_id", id);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                ZPKDetailResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(
                            arg0, ZPKDetailResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取订单详情失败！");
                    return;
                }
                if (resultBean.success) {
                    String[] week = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
                    String psTime = resultBean.zpkDetail.zpk.DISTRIBU_TIME;
                    if (!TextUtils.isEmpty(psTime)) {
                        fl_time.setVisibility(View.VISIBLE);
                        tv_time1.setVisibility(View.GONE);
                        final List<String> timeDatas = new ArrayList<>();
                        for (int i = 0; i < psTime.length(); i++) {
                            if ("1".equals(psTime.substring(i, i + 1))) {
                                timeDatas.add(week[i]);
                            }
                        }
                        final LayoutInflater mInflater = LayoutInflater.from(context);
                        adapterTime = new TagAdapter<String>(timeDatas) {
                            @Override
                            public View getView(FlowLayout parent, int position, String s) {
                                TextView tv = (TextView) mInflater.inflate(R.layout.item_tv,
                                        fl_time, false);
                                tv.setText(s);
                                return tv;
                            }

                        };
                        fl_time.setAdapter(adapterTime);
                    }else{
                        fl_time.setVisibility(View.GONE);
                        tv_time1.setVisibility(View.VISIBLE);
                    }
                    String provinceName = resultBean.zpkDetail.zpk.PROVINCE_NAME;
                    String cityName = resultBean.zpkDetail.zpk.CITY_NAME;
                    String areaName = resultBean.zpkDetail.zpk.AREA_NAME;
                    String address = resultBean.zpkDetail.zpk.ADDRESS;
                    provinceName = TextUtils.isEmpty(provinceName) ? ""
                            : provinceName;
                    cityName = TextUtils.isEmpty(cityName) ? ""
                            : cityName;
                    areaName = TextUtils.isEmpty(areaName) ? ""
                            : areaName;
                    address = TextUtils.isEmpty(address) ? ""
                            : address;
                    addr = provinceName + cityName + areaName + address;
                    tv_phone.setText("电话："+resultBean.zpkDetail.zpk.MOBILE);
                    tv_addr.setText(addr);
                    tv_sh_name.setText(resultBean.zpkDetail.zpk.PERSON);

                    tv_order.setText(resultBean.zpkDetail.zpk.ORDER_ID);
                    if (resultBean.zpkDetail.zpk.all_num == 0 || resultBean.zpkDetail.zpk.all_num > resultBean.zpkDetail.zpk.yps_num) {
                        tv_order_state.setText("未完成：" + resultBean.zpkDetail.zpk.yps_num + "/" + resultBean.zpkDetail.zpk.all_num);
                    } else {
                        tv_order_state.setText("已完成：" + resultBean.zpkDetail.zpk.yps_num + "/" + resultBean.zpkDetail.zpk.all_num);
                    }
                    tv_goods_name.setText(resultBean.zpkDetail.zpk.NAME);
                    tv_zhouqi.setText(resultBean.zpkDetail.zpk.OPTION_KEY_NAME);
                    tv_renshu.setText(resultBean.zpkDetail.zpk.KEY_NAME);
                    tv_price.setText("￥" + resultBean.zpkDetail.zpk.TOTAL_PRICE);
                    payState = resultBean.zpkDetail.zpk.PAY_STATE;
                    if (payState == 1)
                        tv_pay.setText("已支付");
                    else
                        tv_pay.setText("去支付");

                    id = resultBean.zpkDetail.zpk.ID;
                    money = ConvertUtil.obj2Double(resultBean.zpkDetail.zpk.TOTAL_PRICE);
                    goodsName = resultBean.zpkDetail.zpk.NAME;
                    image = ConstantValues.BASE_URL + resultBean.zpkDetail.zpk.IMAGE;
                    tv_pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (payState == 1) {
                                ToastUtil.showTextToast(context, "该订单已支付！");
                            } else {
                                Intent intent = new Intent(context, PayActivity.class);
                                intent.putExtra("orderId", id);
                                intent.putExtra("money", money + "");
                                intent.putExtra("jifen", "0");
                                intent.putExtra("goodsName", goodsName);
                                intent.putExtra("image", image);
                                startActivity(intent);
                            }
                        }
                    });
                    try {
                        ImageLoaderUtil.loadNetPic(image, iv_goods_icon);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    try {
                        tv_creat_time.setText(TimeUtil.formatTimeMin(resultBean.zpkDetail.zpk.CREATE_TIME));
                    } catch (Exception e) {
                        tv_creat_time.setText("时间不详");
                    }
                    try {
                        tv_pay_time.setText(TimeUtil.formatTimeMin(resultBean.zpkDetail.zpk.ZF_TIME));
                    } catch (Exception e) {
                        tv_pay_time.setText("时间不详");
                    }

                    datas = resultBean.zpkDetail.psList;
                    if (datas != null && datas.size() > 0) {
                        rl_null.setVisibility(View.GONE);
                        adapter = new MyAdapter(datas);
                        lv_order_goods.setAdapter(adapter);
                    } else {
                        rl_null.setVisibility(View.VISIBLE);
                    }
                } else if (resultBean.flag == 10) {
                    mApplication.login();
                } else {
                    ToastUtil.showTextToast(context, resultBean.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取订单详情失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.ZHAI_ORDER_DETAIL, params, callback);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

             case R.id.btn_go_top:
                 mScrollView.fullScroll(ScrollView.FOCUS_UP);
                 btn_go_top.setVisibility(View.GONE);
                break;

             case R.id.tv_order_state:
                 isOpen = !isOpen;
                if (isOpen){
                    Drawable nav_up = getResources().getDrawable(
                            R.drawable.city_small_icon);
                    nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
                            nav_up.getMinimumHeight());
                    tv_order_state.setCompoundDrawables(null, null, nav_up, null);
                    lv_order_goods.setVisibility(View.GONE);
                }else{
                    Drawable nav_up = getResources().getDrawable(
                            R.drawable.common_more);
                    nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
                            nav_up.getMinimumHeight());
                    tv_order_state.setCompoundDrawables(null, null, nav_up, null);
                    lv_order_goods.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.ll_pay:

                if (payState == 1) {
                    ToastUtil.showTextToast(context,"该订单已支付！");
                }else{
                    Intent intent = new Intent(context, PayActivity.class);
                    intent.putExtra("isBJ",true);
                    intent.putExtra("orderId", id);
                    intent.putExtra("money", money+"");
                    intent.putExtra("jifen", "0");
                    intent.putExtra("goodsName", goodsName);
                    intent.putExtra("image", image);
                    startActivity(intent);
                }
                break;

            default:
                break;
        }
    }

    class MyAdapter extends DefaultBaseAdapter<ZPKDetailResultBean.ZpkDetailBean.PsListBean> {

        private ViewHolder holder;

        public MyAdapter(List<ZPKDetailResultBean.ZpkDetailBean.PsListBean> datas) {
            super(datas);
        }

        @SuppressWarnings("finally")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_ps,
                        null);
                holder = new ViewHolder();
                holder.tv_state = (TextView) convertView
                        .findViewById(R.id.tv_state);
                holder.tv_ps_time = (TextView) convertView
                        .findViewById(R.id.tv_ps_time);
                holder.tv_true_time = (TextView) convertView
                        .findViewById(R.id.tv_true_time);
                holder.tv_note = (TextView) convertView
                        .findViewById(R.id.tv_note);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 填数据
            holder.tv_state.setText(datas.get(position).DISTRIBU_STATE == 1 ? "已配送" : "未配送");
            try {
                holder.tv_ps_time.setText(TimeUtil.formatTimeDay(datas.get(position).DISTRIBU_TIME));
            } catch (Exception e) {
                holder.tv_ps_time.setText("时间不详");
            }
            if (TextUtils.isEmpty(datas.get(position).TRUE_DISTRIBU_TIME)){
                holder.tv_true_time.setText("未配送");
            }else{
                try {
                    holder.tv_true_time.setText(TimeUtil.formatTimeDay(datas.get(position).TRUE_DISTRIBU_TIME));
                } catch (Exception e) {
                    holder.tv_true_time.setText("时间不详");
                }
            }
            String note = datas.get(position).NOTE;
            holder.tv_note.setText(TextUtils.isEmpty(note) ? "无": note);
            return convertView;
        }

    }

    class ViewHolder {
        private TextView tv_state;
        private TextView tv_ps_time;
        private TextView tv_true_time;
        private TextView tv_note;
    }

}
