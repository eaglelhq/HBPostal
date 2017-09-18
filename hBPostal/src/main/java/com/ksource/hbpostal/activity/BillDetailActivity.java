package com.ksource.hbpostal.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BillDetailResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.TimeUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.yitao.util.UtilTool;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 缴费单详情
 */
public class BillDetailActivity extends BaseActivity {

    private TextView tv_title, btn_right;
    private ImageView iv_back;
    private ImageView iv_jf_icon;
    private TextView tv_company_name, tv_money, tv_bill_state, tv_company,
            tv_user_number, tv_user_name, tv_time, tv_tujing, tv_last_usage, tv_now_usage;
    private TextView tv_huhao, tv_huming;
    private TextView tv_last_use, tv_now_use;

    private String id;
    private String token;
    private LoadDialog mLoadDialog;
    private int type;
    private String accountId;
    private String accountName;
    private String address;
    private String name;
    private String endTime;
    private String startTime;
    private String payTime;
    private String lastNumber;
    private String nowNumber;
    private String money;
    private int state;
    private String integral;
    private String payment;

    private String url;
    private int payWay;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_bill_detail;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_right = (TextView) findViewById(R.id.btn_right);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        iv_jf_icon = (ImageView) findViewById(R.id.iv_jf_icon);
        tv_company_name = (TextView) findViewById(R.id.tv_company_name);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_bill_state = (TextView) findViewById(R.id.tv_bill_state);
        tv_company = (TextView) findViewById(R.id.tv_company);
        tv_user_number = (TextView) findViewById(R.id.tv_user_number);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_tujing = (TextView) findViewById(R.id.tv_tujing);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_last_usage = (TextView) findViewById(R.id.tv_last_usage);
        tv_now_usage = (TextView) findViewById(R.id.tv_now_usage);
        tv_huhao = (TextView) findViewById(R.id.tv_huhao);
        tv_huming = (TextView) findViewById(R.id.tv_huming);
        tv_last_use = (TextView) findViewById(R.id.tv_last_use);
        tv_now_use = (TextView) findViewById(R.id.tv_now_use);
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_right.setOnClickListener(this);
    }

    @Override
    public void initData() {
        tv_title.setText("账单详情");
        token = sp.getString(ConstantValues.TOKEN, null);
        id = getIntent().getStringExtra("id");
        type = getIntent().getIntExtra("type", 1);
        switch (type) {
            case 1:
                iv_jf_icon.setImageResource(R.drawable.life_icon_water_bg);
                tv_company.setText("水费");
                url = ConstantValues.GET_WATER_PAY_DETAIL_URL;
                break;
            case 2:
                iv_jf_icon.setImageResource(R.drawable.life_icon_ele_bg);
                tv_company.setText("电费");
                url = ConstantValues.GET_ELE_PAY_DETAIL_URL;
                break;
            case 3:
                iv_jf_icon.setImageResource(R.drawable.life_icon_gas_bg);
                tv_company.setText("燃气费");
                url = ConstantValues.GET_GAS_PAY_DETAIL_URL;
                break;
            case 4:
                iv_jf_icon.setImageResource(R.drawable.life_icon_tv_bg);
                tv_company.setText("有线电视费");
                url = ConstantValues.GET_TV_PAY_DETAIL_URL;
                break;
            case 5:
            case 6:
                tv_huhao.setText("固话号");
                tv_huming.setText("用户名");
                iv_jf_icon.setImageResource(R.drawable.life_icon_broad_bg);
                tv_company.setText("固话宽带费");
                url = ConstantValues.GET_MOBILE_PAY_DETAIL_URL;
                break;
            case 10:
                tv_huhao.setText("手机号");
                tv_huming.setText("用户名");
                iv_jf_icon.setImageResource(R.drawable.home_icon_telephone);
                tv_company.setText("手机充值");
                url = ConstantValues.GET_MOBILE_PAY_DETAIL_URL;
                break;

            default:
                break;
        }
        getData();
    }

    //设置信息
    private void setText() {
        name = TextUtils.isEmpty(name) ? "邮支付平台" : name;
        tv_company_name.setText(name);
        payment = TextUtils.isEmpty(integral) || "0".equals(integral) ? "-￥" + money : "-￥" + money + "\n-" + integral + "积分";
        tv_money.setText(payment);
        tv_bill_state.setText(state == 1 ? "交易成功" : "交易失败");
//		tv_company.setText(name);
        tv_user_number.setText(accountId);
        tv_user_name.setText(UtilTool.showStarName(accountName));
        tv_time.setText(TimeUtil.formatTimeMin(payTime));
        if (!TextUtils.isEmpty(lastNumber)) {
            tv_last_use.setVisibility(View.VISIBLE);
            tv_last_usage.setVisibility(View.VISIBLE);
            tv_last_usage.setText(lastNumber);
        } else {
            tv_last_use.setVisibility(View.GONE);
            tv_last_usage.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(nowNumber)) {
            tv_now_use.setVisibility(View.VISIBLE);
            tv_now_usage.setVisibility(View.VISIBLE);
            tv_now_usage.setText(nowNumber);
        } else {
            tv_now_use.setVisibility(View.GONE);
            tv_now_usage.setVisibility(View.GONE);
        }
        switch (payWay) {
            case 1:
                tv_tujing.setText("客户端");
                break;
            case 2:
                tv_tujing.setText("微信");
                break;
            case 3:
                tv_tujing.setText("POS");
                break;

            default:
                break;
        }

    }


    // 获取订单数据
    private void getData() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("id", id);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                BillDetailResultBean resultBean = null;
                Gson gson = new Gson();
                try {
                    resultBean = gson.fromJson(arg0,
                            BillDetailResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取订单信息失败！");
                } else {
                    if (resultBean.success) {
                        switch (type) {
                            case 2:
                                accountId = resultBean.paymentInfo.ACCOUNT_ID;
                                accountName = resultBean.paymentInfo.ACCOUNT_NAME;
                                address = resultBean.paymentInfo.ADDRESS;
                                name = resultBean.paymentInfo.NAME;
                                endTime = resultBean.paymentInfo.END_TIME;
                                startTime = resultBean.paymentInfo.START_TIME;
                                payTime = resultBean.paymentInfo.PAY_TIME;
                                lastNumber = resultBean.paymentInfo.LAST_NUMBER;
                                nowNumber = resultBean.paymentInfo.NOW_NUMBER;
                                money = resultBean.paymentInfo.FACT_MONEY;
                                state = resultBean.paymentInfo.PAY_STATE;
                                payWay = resultBean.paymentInfo.PAY_WAY;
                                integral = resultBean.paymentInfo.TOTAL_INTEGRAL;
                                break;
                            case 3:
                                accountId = resultBean.paymentInfo.ACCOUNT_ID;
                                accountName = resultBean.paymentInfo.ACCOUNT_NAME;
                                address = resultBean.paymentInfo.ADDRESS;
                                name = resultBean.paymentInfo.NAME;
                                endTime = resultBean.paymentInfo.END_TIME;
                                startTime = resultBean.paymentInfo.START_TIME;
                                payTime = resultBean.paymentInfo.PAY_TIME;
                                lastNumber = resultBean.paymentInfo.LAST_NUMBER;
                                nowNumber = resultBean.paymentInfo.NOW_NUMBER;
                                money = resultBean.paymentInfo.FACT_MONEY;
                                state = resultBean.paymentInfo.PAY_STATE;
                                payWay = resultBean.paymentInfo.PAY_WAY;
                                integral = resultBean.paymentInfo.TOTAL_INTEGRAL;
                                break;
                            case 4:
                                accountId = resultBean.paymentInfo.GD_PAPERNO;
                                accountName = resultBean.paymentInfo.ACCOUNT_NAME;
                                address = resultBean.paymentInfo.ADDRESS;
                                name = resultBean.paymentInfo.NAME;
                                endTime = resultBean.paymentInfo.END_TIME;
                                startTime = resultBean.paymentInfo.START_TIME;
                                payTime = resultBean.paymentInfo.PAY_TIME;
                                lastNumber = resultBean.paymentInfo.LAST_NUMBER;
                                nowNumber = resultBean.paymentInfo.NOW_NUMBER;
                                money = resultBean.paymentInfo.FACT_MONEY;
                                state = resultBean.paymentInfo.PAY_STATE;
                                payWay = resultBean.paymentInfo.PAY_WAY;
                                integral = resultBean.paymentInfo.TOTAL_INTEGRAL;
                                String paperType = resultBean.paymentInfo.GD_PAPERTYPE;
                                switch (paperType){
                                    case "21A":
                                        tv_huhao.setText("身份证号");
                                        break;
                                    case "21C":
                                        tv_huhao.setText("军官证号");
                                        break;
                                    case "21D":
                                        tv_huhao.setText("企业执照号");
                                        break;
                                    case "21E":
                                        tv_huhao.setText("护照号");
                                        break;
                                    case "21F":
                                        tv_huhao.setText("组织机构代码证号");
                                        break;
                                }
                                break;
                            case 5:
                            case 6:
                            case 10:
                                accountId = resultBean.mapInfo.ACCOUNT_ID;
                                accountName = resultBean.mapInfo.ACCOUNT_NAME;
                                address = resultBean.mapInfo.ADDRESS;
                                name = resultBean.mapInfo.NAME;
                                endTime = resultBean.mapInfo.END_TIME;
                                startTime = resultBean.mapInfo.START_TIME;
                                payTime = resultBean.mapInfo.PAY_TIME;
                                lastNumber = resultBean.mapInfo.LAST_NUMBER;
                                nowNumber = resultBean.mapInfo.NOW_NUMBER;
                                money = resultBean.mapInfo.FACT_MONEY;
                                state = resultBean.mapInfo.PAY_STATE;
                                payWay = resultBean.mapInfo.PAY_WAY;
                                integral = resultBean.mapInfo.TOTAL_INTEGRAL;
                                break;

                            default:
                                break;
                        }
                        setText();
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
                ToastUtil.showTextToast(context, "获取订单信息失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,url, params, callback);
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
