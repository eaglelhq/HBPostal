package com.ksource.hbpostal.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.PayDetailResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.TimeUtil;
import com.ksource.hbpostal.util.UtilTool;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.ConvertUtil;
import com.yitao.util.DialogUtil;
import com.yitao.util.NumberUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 扫码支付详情
 */
public class ScanPayDetailActivity extends BaseActivity {

    private TextView tv_title, btn_right;
    private ImageView iv_back;
    private LinearLayout ll_ss, ll_card, ll_close, ll_money, ll_jifen, ll_yhq;
    private TextView tv_all_pay, tv_pay_money, tv_pay_jifen, tv_sell_name, tv_curr_state,
            tv_pay_time, tv_pay_card, tv_pay_order, tv_ly_msg, tv_ss, tv_ss_hf, tv_ss_re,
            tv_pay_yhq, tv_close_time, tv_seller_name;

    private String id;
    private String token;
    private LoadDialog mLoadDialog;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_scan_pay_detail;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_right = (TextView) findViewById(R.id.btn_right);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        tv_all_pay = (TextView) findViewById(R.id.tv_all_pay);
        tv_pay_money = (TextView) findViewById(R.id.tv_pay_money);
        tv_pay_jifen = (TextView) findViewById(R.id.tv_pay_jifen);
        tv_pay_yhq = (TextView) findViewById(R.id.tv_pay_yhq);
        tv_sell_name = (TextView) findViewById(R.id.tv_sell_name);
        tv_curr_state = (TextView) findViewById(R.id.tv_curr_state);
        tv_pay_time = (TextView) findViewById(R.id.tv_pay_time);
        tv_pay_card = (TextView) findViewById(R.id.tv_pay_card);
        tv_ly_msg = (TextView) findViewById(R.id.tv_ly_msg);
        tv_pay_order = (TextView) findViewById(R.id.tv_pay_order);
        tv_ss = (TextView) findViewById(R.id.tv_ss);
        tv_ss_hf = (TextView) findViewById(R.id.tv_ss_hf);
        tv_ss_re = (TextView) findViewById(R.id.tv_ss_re);
        tv_seller_name = (TextView) findViewById(R.id.tv_seller_name);
        tv_close_time = (TextView) findViewById(R.id.tv_close_time);
        ll_ss = (LinearLayout) findViewById(R.id.ll_ss);
        ll_card = (LinearLayout) findViewById(R.id.ll_card);
        ll_close = (LinearLayout) findViewById(R.id.ll_close);
        ll_money = (LinearLayout) findViewById(R.id.ll_money);
        ll_jifen = (LinearLayout) findViewById(R.id.ll_jifen);
        ll_yhq = (LinearLayout) findViewById(R.id.ll_yhq);
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_right.setOnClickListener(this);
    }

    @Override
    public void initData() {
        tv_title.setText("支付结果");
        token = sp.getString(ConstantValues.TOKEN, null);
        id = getIntent().getStringExtra("id");
        getData();
    }

    // 获取订单数据
    private void getData() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("dealId", id);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                PayDetailResultBean resultBean = null;
                Gson gson = new Gson();
                try {
                    resultBean = gson.fromJson(arg0,
                            PayDetailResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取交易信息失败！");
                } else {
                    if (resultBean.success) {
                        PayDetailResultBean.InfoBean data = resultBean.info;
                        tv_all_pay.setText("￥" + NumberUtil.toDecimal2(ConvertUtil.obj2Double(data.TOTAL_AMOUNT)));
                        String shopName = TextUtils.isEmpty(data.SHOP_NAME)?"":data.SHOP_NAME;
                        String cName = TextUtils.isEmpty(data.CNAME)?"":data.CNAME;
                        tv_sell_name.setText(shopName + cName);
                        int state = data.PAY_TYPE;
                        String stateStr = "";
                        switch (state) {
                            case 0:
                                stateStr = "未支付";
                                break;
                            case 1:
                                stateStr = "交易成功";
                                break;
                            case 2:
                                stateStr = "交易失败";
                                break;
                            case 3:
                                stateStr = "交易已关闭";
                                break;
                            default:
                                stateStr = "支付异常";

                                break;
                        }
                        tv_curr_state.setText(stateStr);
                        tv_pay_time.setText(TimeUtil.formatTimeSec(data.PAY_TIME));
                        tv_pay_order.setText(data.ORDER_ID);
                        tv_seller_name.setText(data.DZSXM);
                        tv_ly_msg.setText(TextUtils.isEmpty(data.MEMBER_LEAVE_MSG) ? "无" : data.MEMBER_LEAVE_MSG);
                        if (TextUtils.isEmpty(data.PAY_CARD)) {
                            ll_card.setVisibility(View.GONE);
                        } else {
                            ll_card.setVisibility(View.VISIBLE);
                            tv_pay_card.setText(UtilTool.showBankCard(data.PAY_CARD));
                        }
                        if (data.SCORE_NUM == 0) {
                            ll_jifen.setVisibility(View.GONE);
                        } else {
                            ll_jifen.setVisibility(View.VISIBLE);
                            tv_pay_jifen.setText(data.SCORE_NUM + "积分");
                        }
                        if (ConvertUtil.obj2Double(data.PAY_AMOUNT) == 0) {
                            ll_money.setVisibility(View.GONE);
                        } else {
                            ll_money.setVisibility(View.VISIBLE);
                            tv_pay_money.setText("￥" + NumberUtil.toDecimal2(ConvertUtil.obj2Double(data.PAY_AMOUNT)));
                        }
                        //优惠券金额
                        if (resultBean.isExist != 1) {
//                            ll_yhq.setVisibility(View.GONE);
                            tv_pay_yhq.setText("无");
                        } else {
//                            ll_yhq.setVisibility(View.VISIBLE);
                            tv_pay_yhq.setText(resultBean.couponList.datas.get(0).DENOMINATION + "元优惠券");
                        }
                        if (TextUtils.isEmpty(data.CLOSE_TIME)) {
                            ll_close.setVisibility(View.GONE);
                        } else {
                            ll_close.setVisibility(View.VISIBLE);
                            tv_close_time.setText(TimeUtil.formatTimeSec(data.CLOSE_TIME));
                        }
                        if (data.APPEAL_STATE == 1) {
                            ll_ss.setVisibility(View.VISIBLE);
                            tv_ss.setText(data.APPEAL_CONTENT);
                            if (data.REPLY_STATE == 1) {
                                tv_ss_hf.setVisibility(View.VISIBLE);
                                tv_ss_re.setVisibility(View.VISIBLE);
                                tv_ss_re.setText(data.REPLY_CONTENT);
                            } else {
                                tv_ss_hf.setVisibility(View.GONE);
                                tv_ss_re.setVisibility(View.GONE);
                            }
                        } else {
                            ll_ss.setVisibility(View.GONE);
                        }
                    } else {
                        if (resultBean.flag == 10) {
                            mApplication.login();
                        } else {
                            ToastUtil.showTextToast(context, resultBean.msg);
                        }
                    }
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取交易信息失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.DEAL_INFO, params, callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

            default:
                break;
        }
    }

}
