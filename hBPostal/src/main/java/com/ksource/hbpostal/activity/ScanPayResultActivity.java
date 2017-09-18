package com.ksource.hbpostal.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.AwardResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.ConvertUtil;
import com.yitao.util.DialogUtil;
import com.yitao.util.NumberUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 扫码支付结果
 */
public class ScanPayResultActivity extends BaseActivity {

    private TextView tv_title, btn_right;
    private ImageView iv_back;
    private TextView tv_pay_user, tv_pay, tv_all_pay, tv_detatl;
    private TextView btn_pay;
    private ListView lv_yhq;

    private String pay_amount;
    private String jifenStr;
    private String moneyStr;
    private String yhq_money;
    private String name;
    private String id;
    private String total_amount;
    private LoadDialog mLoadDialog;
    private String token;
    private BaseAdapter adapter;
    private List<AwardResultBean.InfoBean> datas;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_scan_pay_result;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_right = (TextView) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.VISIBLE);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        tv_pay_user = (TextView) findViewById(R.id.tv_pay_user);
        tv_all_pay = (TextView) findViewById(R.id.tv_all_pay);
        tv_pay = (TextView) findViewById(R.id.tv_pay);
        tv_detatl = (TextView) findViewById(R.id.tv_detatl);
//        lv_yhq = (ListView) findViewById(R.id.lv_yhq);
        btn_pay = (TextView) findViewById(R.id.btn_pay);

    }

    @Override
    public void initListener() {
        btn_pay.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        tv_detatl.setOnClickListener(this);
    }

    @Override
    public void initData() {
        tv_title.setText("支付结果");
        btn_right.setText("交易历史");
        token = sp.getString(ConstantValues.TOKEN, "");
        name = getIntent().getStringExtra("name");
        total_amount = getIntent().getStringExtra("total_amount");
        pay_amount = getIntent().getStringExtra("money");
        jifenStr = getIntent().getStringExtra("jifen");
        id = getIntent().getStringExtra("id");
//        id = "3fce8392df3a44ba949898bb92a17173";
        yhq_money = getIntent().getStringExtra("yhq_money");
        if (TextUtils.isEmpty(yhq_money)) {
            yhq_money = "0";
        }
        tv_pay_user.setText("支付收款商户：" + name);
        tv_all_pay.setText("￥" + NumberUtil.toDecimal2(ConvertUtil.obj2Double(total_amount)));
        if (!"0.0".equals(pay_amount)) {
            moneyStr = "￥" + NumberUtil.toDecimal2(ConvertUtil.obj2Double(pay_amount));
            if (!"0".equals(jifenStr)) {
                moneyStr = "￥" + NumberUtil.toDecimal2(ConvertUtil.obj2Double(pay_amount)) + "+" + jifenStr + "积分";
                if (!"0".equals(yhq_money)) {
                    moneyStr = "￥" + NumberUtil.toDecimal2(ConvertUtil.obj2Double(pay_amount)) + "+" + jifenStr + "积分+" + yhq_money + "元优惠券";
                }
            } else {
                if (!"0".equals(yhq_money)) {
                    moneyStr = "￥" + NumberUtil.toDecimal2(ConvertUtil.obj2Double(pay_amount)) + "+" + yhq_money + "元优惠券";
                }
            }
        } else if (!"0".equals(jifenStr)) {
            moneyStr = jifenStr + "积分";
            if (!"0".equals(yhq_money)) {
                moneyStr = jifenStr + "积分+" + yhq_money + "元优惠券";
            }
        } else {
            moneyStr = yhq_money + "元优惠券";
        }
        tv_pay.setText(moneyStr);
        datas = new ArrayList<>();
        getYhq();
    }

    /**
     * 获取优惠券
     */
    private void getYhq() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("orderId", id);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                AwardResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(
                            arg0, AwardResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取优惠券失败！");
                    return;
                }
                if (resultBean.success) {
                    datas = resultBean.info;
                    if (datas != null && datas.size() > 0) {
                        adapter = new MyAdapter(datas);
                        showYhq();
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
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.AWARD_YHQ_URL, params, callback);

    }

    /**
     * 显示获得的优惠券
     */
    private void showYhq() {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_yhq, null);

        TextView btn_get = (TextView) view.findViewById(R.id.btn_get);
        ImageView iv_close = (ImageView) view.findViewById(R.id.iv_close);
        ListView lv_yhq = (ListView) view.findViewById(R.id.lv_yhq);
        lv_yhq.setAdapter(adapter);
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //领取优惠券
//                ToastUtils.showShortToast("优惠券已领取，可在我的优惠券中查看");
                dialog.dismiss();
                startActivity(new Intent(context,YhqActivity.class));
            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_right:
                // 跳转交易历史
                Intent intent1 = new Intent(context, PayHistoryActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_pay:
                finish();
                mApplication.finishActivity("ScanPayActivity");
                mApplication.finishActivity("ScanResultActivity");
                mApplication.finishActivity("ScanActivity");
                break;
            case R.id.tv_detatl:
                // 跳转交易详情
                Intent intent = new Intent(context, ScanPayDetailActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;

            default:
                break;
        }
    }


    class MyAdapter extends DefaultBaseAdapter<AwardResultBean.InfoBean> {



        public MyAdapter(List<AwardResultBean.InfoBean> datas) {
            super(datas);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View
                        .inflate(context, R.layout.item_yhq_award, null);
                holder = new ViewHolder();

                holder.tv_quan_name = (TextView) convertView.findViewById(R.id.tv_quan_name);
                holder.tv_quan_use = (TextView) convertView.findViewById(R.id.tv_quan_use);
                holder.tv_quan_time = (TextView) convertView.findViewById(R.id.tv_quan_time);
                holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (datas.get(position).COUPON_CATE == 1) {
                holder.tv_quan_name.setText("邮政快递优惠券");
            } else {
                holder.tv_quan_name.setText(datas.get(position).SHOP_NAME + "商家优惠券");
            }
            holder.tv_quan_use.setText("满" + datas.get(position).TAKE_EFFECT_PRICE + "元即可使用");
            holder.tv_quan_time.setText("· 有效期： " + datas.get(position).VALID_DAYS + "天以内有效 ·");
            holder.tv_money.setText(datas.get(position).DENOMINATION + "");

            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv_quan_name;
        TextView tv_quan_use;
        TextView tv_quan_time;
        TextView tv_money;
    }
}
