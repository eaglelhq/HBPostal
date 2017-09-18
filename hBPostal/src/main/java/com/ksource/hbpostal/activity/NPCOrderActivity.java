package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.ZPKRecordResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 宅配卡购买记录页面
 */
public class NPCOrderActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private PullToRefreshListView lv;
    private ListView listView;
    private List<ZPKRecordResultBean.ZpkRecordBean> datas;
    private BaseAdapter adapter;
    private LoadDialog mLoadDialog;
    private RelativeLayout rl_error, rl_null;

    private boolean isUpdate;
    private int currPage;
    private String token;
    private TagAdapter<String> adapterTime;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_active_msg;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv = (PullToRefreshListView) findViewById(R.id.lv_active_msg);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
        rl_error = (RelativeLayout) findViewById(R.id.rl_error);

    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        rl_error.setOnClickListener(this);
        lv.setPullRefreshEnabled(true);
        lv.setPullLoadEnabled(false);
        lv.setScrollLoadEnabled(true);
        listView = lv.getRefreshableView();
        listView.setDivider(null);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(context, NPCOrderDetailActivity.class);
                intent.putExtra("orderId", datas.get(position).ORDER_ID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        token = sp.getString(ConstantValues.TOKEN, null);
        datas = new ArrayList<>();
        tv_title.setText("宅配卡购买记录");
        adapter = null;
        currPage = 1;
        lv.doPullRefreshing(true, 500);

    }

    private void getData() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("pageSize", "10");
        params.put("currPage", "" + currPage);

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ZPKRecordResultBean msgResult = null;
                Gson gson = new Gson();
                try {
                    msgResult = gson.fromJson(arg0,
                            ZPKRecordResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (msgResult == null) {
                    rl_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (msgResult.success) {
                    rl_error.setVisibility(View.GONE);
                    if (isUpdate) {
                        datas.clear();
                    }
                    datas.addAll(msgResult.zpkRecord);
                    if (datas.size() == 0) {
                        rl_null
                                .setVisibility(View.VISIBLE);
                    } else {
                        rl_null.setVisibility(View.GONE);
                        if (adapter == null) {
                            adapter = new MyAdapter(datas);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    if (isUpdate) {
                        listView.setSelection(0);
                    }
                    listView.setVisibility(View.VISIBLE);
                    lv.onPullDownRefreshComplete();
                    lv.onPullUpRefreshComplete();
                    lv.setHasMoreData(msgResult.zpkRecord.size() < 10 ? false
                            : true);
                } else if (msgResult.flag == 10) {
                    mApplication.login();
                } else {
                    rl_error.setVisibility(View.VISIBLE);
                    ToastUtil.showTextToast(context, msgResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                rl_error.setVisibility(View.VISIBLE);
            }
        };

        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.ZHAI_ORDER_LIST, params, callback);

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
                currPage = 1;
                getData();
                break;

            default:
                break;
        }

    }

    class MyAdapter extends DefaultBaseAdapter<ZPKRecordResultBean.ZpkRecordBean> {

        private ViewHolder holder;

        public MyAdapter(List<ZPKRecordResultBean.ZpkRecordBean> datas) {
            super(datas);
        }

        @SuppressWarnings("finally")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_ncp_order,
                        null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView
                        .findViewById(R.id.iv_goods_icon);
                holder.tv_goods_name = (TextView) convertView
                        .findViewById(R.id.tv_goods_name);
                holder.tv_zhouqi = (TextView) convertView
                        .findViewById(R.id.tv_zhouqi);
                holder.tv_renshu = (TextView) convertView
                        .findViewById(R.id.tv_renshu);
                holder.tv_time1 = (TextView) convertView
                        .findViewById(R.id.tv_time1);
                holder.tv_time2 = (TextView) convertView
                        .findViewById(R.id.tv_time2);
                holder.tv_price = (TextView) convertView
                        .findViewById(R.id.tv_price);
                holder.tv_pay = (TextView) convertView
                        .findViewById(R.id.ll_pay);
                holder.tv_order_name = (TextView) convertView
                        .findViewById(R.id.tv_order_name);
                holder.tv_order_state = (TextView) convertView
                        .findViewById(R.id.tv_order_state);
                holder.fl_time = (TagFlowLayout) convertView
                        .findViewById(R.id.fl_time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_order_name.setText("订单号：" + datas.get(position).ORDER_ID);
            if (datas.get(position).all_num == 0 || datas.get(position).all_num > datas.get(position).yps_num) {
                holder.tv_order_state.setText("未完成：" + datas.get(position).yps_num + "/" + datas.get(position).all_num);

            } else {
                holder.tv_order_state.setText("已完成：" + datas.get(position).yps_num + "/" + datas.get(position).all_num);

            }
            holder.tv_goods_name.setText(datas.get(position).NAME);
            holder.tv_zhouqi.setText(datas.get(position).OPTION_KEY_NAME);
            holder.tv_renshu.setText(datas.get(position).KEY_NAME);
            holder.tv_time1.setText("周一");
            holder.tv_time2.setText("周五");
            holder.tv_price.setText("￥" + datas.get(position).TOTAL_PRICE);
            final int payState = datas.get(position).PAY_STATE;
            if (payState == 1) {
                holder.tv_pay.setText("已支付");
                holder.tv_pay.setBackgroundResource(R.drawable.rect_gary);
            } else {
                holder.tv_pay.setText("去支付");
                holder.tv_pay.setBackgroundResource(R.drawable.rect_orange_bk);
            }
            holder.tv_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (payState == 1) {
                        ToastUtil.showTextToast(context, "该订单已支付！");
                    } else {
                        Intent intent = new Intent(context, PayActivity.class);
                        intent.putExtra("isBJ", true);
                        intent.putExtra("orderId", datas.get(position).ID);
                        intent.putExtra("money", datas.get(position).TOTAL_PRICE);
                        intent.putExtra("jifen", "0");
                        intent.putExtra("goodsName", datas.get(position).NAME);
                        intent.putExtra("image", datas.get(position).IMAGE);
                        startActivity(intent);
                    }
                }
            });

            String[] week = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
            String psTime = datas.get(position).DISTRIBU_TIME;
            if (!TextUtils.isEmpty(psTime)) {
                holder.fl_time.setVisibility(View.VISIBLE);
                holder.tv_time1.setVisibility(View.GONE);
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
                                holder.fl_time, false);
                        tv.setText(s);
                        return tv;
                    }

                };
                holder.fl_time.setAdapter(adapterTime);
            }else{
                holder.fl_time.setVisibility(View.GONE);
                holder.tv_time1.setVisibility(View.VISIBLE);
            }
            try {
                ImageLoaderUtil.loadNetPic(
                        ConstantValues.BASE_URL + datas.get(position).IMAGE,
                        holder.iv_icon);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                return convertView;
            }
        }

    }

    class ViewHolder {
        // private TextView tv_card_name;
        private TextView tv_goods_name;
        private ImageView iv_icon;
        private TextView tv_zhouqi;
        private TextView tv_renshu;
        private TagFlowLayout fl_time;
        private TextView tv_time1;
        private TextView tv_time2;
        private TextView tv_price;
        private TextView tv_pay;
        private TextView tv_order_state;
        private TextView tv_order_name;
    }


}
