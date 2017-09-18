package com.ksource.hbpostal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.widgets.PayPwdEditText;
import com.unionpay.UPPayAssistEx;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.ConvertUtil;
import com.yitao.util.DialogUtil;
import com.yitao.util.NumberUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 扫码支付-支付
 */
public class ScanPayActivity extends BaseActivity {

    private static final String LOG_TAG = "Pay";

    public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;

    private TextView tv_title;
    private ImageView iv_back;
    private ImageView iv_card, iv_union;
    private TextView tv_pay, tv_pay_money, tv_pay_user, tv_card, tv_set_pwd;
    private EditText et_pay_pwd;
    private LinearLayout ll_card, ll_union;
    private Button btn_pay;
    private PayPwdEditText payPwdEditText;

    private LoadDialog mLoadDialog;
    private String name;
    private String id;
    private String sellerId;
    private String seller_leave_msg;
    private String member_leave_msg;
    private String money_set;
    private String total_amount;
    private String pay_amount;
    private String score_num;
    private String yhq_money;
    private String moneyStr;
    private boolean canPay;
    private String orderId = "";
    private int pay_way;
    private boolean rePay;
    private String pwd;
    private String token;
    private String yhqId;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_scan_pay;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        iv_card = (ImageView) findViewById(R.id.iv_card);
        iv_union = (ImageView) findViewById(R.id.iv_union);
        tv_pay_user = (TextView) findViewById(R.id.tv_pay_user);
        tv_pay = (TextView) findViewById(R.id.tv_pay);
        tv_pay_money = (TextView) findViewById(R.id.tv_pay_money);
        tv_card = (TextView) findViewById(R.id.tv_card);
        tv_set_pwd = (TextView) findViewById(R.id.tv_set_pwd);
        et_pay_pwd = (EditText) findViewById(R.id.et_pay_pwd);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        ll_card = (LinearLayout) findViewById(R.id.ll_card);
        ll_union = (LinearLayout) findViewById(R.id.ll_union);
        payPwdEditText = (PayPwdEditText) findViewById(R.id.ppet);
        btn_pay.setEnabled(false);
    }

    @Override
    public void initListener() {
        btn_pay.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_card.setOnClickListener(this);
        ll_union.setOnClickListener(this);
        ll_card.setOnClickListener(this);
        tv_set_pwd.setOnClickListener(this);
        payPwdEditText.initStyle(R.drawable.edit_num_bg, 6, 0.33f, R.color.gary, R.color.lightblack, 20);
        payPwdEditText.setOnTextFinishListener(new PayPwdEditText.OnTextFinishListener() {
            @Override
            public void onFinish(String str) {//密码输入完后的回调
//                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                pwd = str;
                btn_pay.setEnabled(true);
            }
        });
    }

    @Override
    public void initData() {
        tv_title.setText("支付信息");
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("shop_id");
        sellerId = getIntent().getStringExtra("seller_id");
        orderId = getIntent().getStringExtra("id");
        yhqId = getIntent().getStringExtra("yhq_id");
        token = sp.getString(ConstantValues.TOKEN, "");
        if (!TextUtils.isEmpty(orderId)) {
            rePay = true;
        }
        total_amount = getIntent().getStringExtra("total_amount");
        pay_amount = getIntent().getStringExtra("pay_amount");
        score_num = getIntent().getStringExtra("score_num");
        money_set = getIntent().getStringExtra("money_set");
        yhq_money = getIntent().getStringExtra("yhq_money");
        if (TextUtils.isEmpty(yhq_money)){
            yhq_money = "0";
        }
        member_leave_msg = getIntent().getStringExtra("member_leave_msg");
        seller_leave_msg = getIntent().getStringExtra("seller_leave_msg");
//        String countBig = NumberUtil.toDecimal2(ConvertUtil.obj2Double(pay_amount));

        if (!"0.0".equals(pay_amount)) {
            moneyStr = "￥" + NumberUtil.toDecimal2(ConvertUtil.obj2Double(pay_amount));
            if (!"0".equals(score_num)) {
                moneyStr = "￥" + NumberUtil.toDecimal2(ConvertUtil.obj2Double(pay_amount)) + "+" + score_num + "积分";
                if (!"0".equals(yhq_money)) {
                    moneyStr = "￥" + NumberUtil.toDecimal2(ConvertUtil.obj2Double(pay_amount)) + "+" + score_num + "积分+" + yhq_money + "元优惠券";
                }
            }else{
                if (!"0".equals(yhq_money)) {
                    moneyStr = "￥" + NumberUtil.toDecimal2(ConvertUtil.obj2Double(pay_amount)) + "+" + yhq_money + "元优惠券";
                }
            }
        } else if (!"0".equals(score_num)) {
            moneyStr = score_num + "积分";
            if (!"0".equals(yhq_money)) {
                moneyStr = score_num + "积分+" + yhq_money + "元优惠券";
            }
        } else {
            moneyStr = yhq_money + "元优惠券";
        }
        tv_pay.setText(moneyStr);
        tv_pay_user.setText("向商户“" + name + "”转账");
        tv_pay_money.setText("￥" + NumberUtil.toDecimal2(ConvertUtil.obj2Double(total_amount)));
        if ("0.0".equals(pay_amount)) {
//            ll_card.setVisibility(View.GONE);
            ll_union.setVisibility(View.GONE);
            pay_way = 2;
        } else {
//            ll_card.setVisibility(View.VISIBLE);
            ll_union.setVisibility(View.VISIBLE);
        }
        pay_way = 2;
//        sp.getBoolean()

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPayPwd();
    }

    /**
     * 获取是否设置交易密码
     */
    private void getPayPwd() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
        Map<String, String> params = new HashMap<>();
        String token = sp.getString(ConstantValues.TOKEN, "");
        params.put("token", token);

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                BaseResultBean baseResult = null;
                Gson gson = new Gson();
                try {
                    baseResult = gson
                            .fromJson(arg0, BaseResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (baseResult == null) {
                    ToastUtil.showTextToast(context, "检查支付密码失败！");
                    return;
                }
                if (baseResult.success && baseResult.pwd == 1) {
                    tv_set_pwd.setVisibility(View.GONE);
                } else {
                    tv_set_pwd.setVisibility(View.VISIBLE);
                    if (baseResult.flag == 10) {
                        mApplication.login();
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "检查支付密码失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.DEAL_QUERY_PWD, params, callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_set_pwd:
                Intent intent2 = new Intent(context, EditPayPwdActivity.class);
                intent2.putExtra("has_pay_pwd", false);
                startActivity(intent2);
                break;
            case R.id.ll_card:
                iv_card.setImageResource(R.drawable.good_icon_check_cur);
                iv_union.setImageResource(R.drawable.good_icon_check_nor);
                pay_way = 1;
                break;
            case R.id.ll_union:
                iv_union.setImageResource(R.drawable.good_icon_check_cur);
                iv_card.setImageResource(R.drawable.good_icon_check_nor);
                pay_way = 2;
                break;
            case R.id.tv_card:
                iv_card.setImageResource(R.drawable.good_icon_check_cur);
                iv_union.setImageResource(R.drawable.good_icon_check_nor);
                pay_way = 1;
                chioceCard();
                break;
            case R.id.btn_pay:
//                pwd = et_pay_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showTextToast(context, "请输入支付密码！");
                    et_pay_pwd.requestFocus();
                    return;
                }

                if (pay_way == 1) {
                    ToastUtils.showShortToast("暂不支持该支付方式");
                } else if (pay_way == 2) {
                    checkPayPwd();
//                    unionPay();
                }
                break;

            default:
                break;
        }
    }

    //验证支付密码
    private void checkPayPwd() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "正在支付...");
        Map<String, String> params = new HashMap<>();
        String token = sp.getString(ConstantValues.TOKEN, "");
        params.put("token", token);
        params.put("pwd", pwd);

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                BaseResultBean baseResult = null;
                Gson gson = new Gson();
                try {
                    baseResult = gson
                            .fromJson(arg0, BaseResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (baseResult == null) {
                    ToastUtil.showTextToast(context, "支付失败！");
                    return;
                }
                if (baseResult.success && baseResult.flag == 0) {
                    if (pay_way == 1) {
                        et_pay_pwd.setText("");
                        et_pay_pwd.requestFocus();
                    } else if (pay_way == 2) {
                        unionPay();
                    }
                } else {
                    if (baseResult.flag == 10) {
                        mApplication.login();
                    }
                    ToastUtil.showTextToast(context, baseResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "支付失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.DEAL_CHECK_PWD, params, callback);
    }

    //选择银行卡
    private void chioceCard() {
//        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "选择银行卡...");
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
    }

    // 银联支付
    private void unionPay() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "正在支付...");
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("clerk_id", sellerId);
        params.put("coupon_id", yhqId);
        if (rePay) {
            params.put("orderId", orderId);
        } else {
            params.put("seller_id", id);
            params.put("total_amount", total_amount);
            params.put("pay_amount", pay_amount);
            params.put("score_num", score_num);
            params.put("money_set", money_set);
            params.put("member_leave_msg", member_leave_msg);
            params.put("seller_level_msg", seller_leave_msg);
        }
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                BaseResultBean baseResult = null;
                Gson gson = new Gson();
                try {
                    baseResult = gson
                            .fromJson(arg0, BaseResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (baseResult == null) {
                    ToastUtil.showTextToast(context, "支付失败！");
                    return;
                }
                if (baseResult.success && baseResult.flag == 0) {
                    String tn = baseResult.tn;
                    orderId = baseResult.orderId;
                    if ("jfzfcg".equals(tn)) {
                        btn_pay.setEnabled(false);
                        canPay = false;
//                        ToastUtil.showTextToast(context, baseResult.msg);
                        Intent intent = new Intent(context,
                                ScanPayResultActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("money", pay_amount);
                        intent.putExtra("total_amount", total_amount);
                        intent.putExtra("jifen", score_num);
                        intent.putExtra("id", orderId);
                        startActivity(intent);
                    } else if ("jfzfsb".equals(tn)) {
                        canPay = true;
                        ToastUtil.showTextToast(context, baseResult.msg);
                        btn_pay.setEnabled(true);
                    } else {
                        doStartUnionPayPlugin(tn);
                    }

                } else {
                    canPay = true;
                    if (baseResult.flag == 10) {
                        mApplication.login();
                    }
                    ToastUtil.showTextToast(context, baseResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "支付失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.F2F_UNION_PAY, params, callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }

//        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            // 支付成功后，extra中如果存在result_data，取出校验
            // result_data结构见c）result_data参数说明
            if (data.hasExtra("result_data")) {
                String result = data.getExtras().getString("result_data");
                try {
                    JSONObject resultJson = new JSONObject(result);
                    String sign = resultJson.getString("sign");
                    String dataOrg = resultJson.getString("data");
                    // 验签证书同后台验签证书
                    //TODO 此处的verify，商户需送去商户后台做验签
                    boolean ret = verify(dataOrg, sign, "00");
                    if (ret) {
                        // 验证通过后，显示支付结果
                        btn_pay.setEnabled(false);
                        canPay = false;
//                        ToastUtil.showTextToast(context, "支付成功！");
                        Intent intent = new Intent(
                                context,
                                ScanPayResultActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("money", pay_amount);
                        intent.putExtra("total_amount", total_amount);
                        intent.putExtra("jifen", score_num);
                        intent.putExtra("id", orderId);
                        startActivity(intent);
                    } else {
                        // 验证不通过后的处理
                        // 建议通过商户后台查询支付结果
                        ToastUtil.showTextToast(context, "支付失败！");
                        canPay = true;
                    }
                } catch (JSONException e) {
                }
            } else {
                // 未收到签名信息
                // 建议通过商户后台查询支付结果
                btn_pay.setEnabled(false);
                canPay = false;
//                ToastUtil.showTextToast(context, "支付成功！");
                Intent intent = new Intent(
                        context,
                        ScanPayResultActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("money", pay_amount);
                intent.putExtra("total_amount", total_amount);
                intent.putExtra("jifen", score_num);
                intent.putExtra("id", orderId);
                startActivity(intent);
            }
        } else if (str.equalsIgnoreCase("fail")) {
            ToastUtil.showTextToast(context, "支付失败！");
            canPay = true;
        } else if (str.equalsIgnoreCase("cancel")) {
//            msg = "用户取消了支付";
            ToastUtil.showTextToast(context, "用户取消了支付！");
            canPay = true;
        }
    }

    private boolean verify(String msg, String sign64, String mode) {
        // 此处的verify，商户需送去商户后台做验签
        return true;

    }

    // 吊起银联支付
    public void doStartUnionPayPlugin(String tn) {
        // mMode参数解释：
        // 0 - 启动银联正式环境C
        //TODO 1 - 连接银联测试环境
        int ret = UPPayAssistEx.startPay(this, null, null, tn, "00");
        if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
            // 需要重新安装控件
            Log.e(LOG_TAG, " plugin not found or need upgrade!!!");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("完成购买需要安装银联支付控件，是否安装？");

            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UPPayAssistEx.installUPPayPlugin(context);
                            dialog.dismiss();
                        }
                    });

            builder.setPositiveButton("取消",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();

        }
        Log.e(LOG_TAG, "" + ret);
    }

}
