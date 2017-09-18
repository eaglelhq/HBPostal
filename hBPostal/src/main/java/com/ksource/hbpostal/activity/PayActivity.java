package com.ksource.hbpostal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.CardResaultBean;
import com.ksource.hbpostal.bean.CardResaultBean.BankCardListBean;
import com.ksource.hbpostal.bean.ScoreResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.unionpay.UPPayAssistEx;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.ConvertUtil;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 订单支付页面
 */
public class PayActivity extends BaseActivity {

    private static final String LOG_TAG = "Pay";

    public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;

    private TextView tv_title;
    private ImageView iv_back;
    private TextView tv_money, tv_jifen, tv_cur_jifen, tv_card;
    private Button btn_pay;
    private ListView lv_card;
    private LinearLayout ll_money, ll_jifen;
    private List<BankCardListBean> datas;
    private int position = 0;
    private BaseAdapter adapter;
    private LoadDialog mLoadDialog;
    private String orderId = "";
    private double money;
    private int jifen;
    private int currJifen;
    private String goodsName;
    private String image;
    private boolean isBJ;
    private boolean canPay;
    private boolean noCard;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_pay;
    }

    @Override
    public void initView() {
        // 头部
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("订单支付");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        // 内容
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_jifen = (TextView) findViewById(R.id.tv_jifen);
        tv_card = (TextView) findViewById(R.id.tv_card);
        tv_cur_jifen = (TextView) findViewById(R.id.tv_cur_jifen);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        lv_card = (ListView) findViewById(R.id.lv_card);
        ll_money = (LinearLayout) findViewById(R.id.ll_money);
        ll_jifen = (LinearLayout) findViewById(R.id.ll_jifen);

    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
        lv_card.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                PayActivity.this.position = position;
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        isBJ = getIntent().getBooleanExtra("isBJ", false);
        orderId = getIntent().getStringExtra("orderId");
        money = ConvertUtil.obj2Double(getIntent().getStringExtra("money"));
        jifen = ConvertUtil.obj2Int(getIntent().getStringExtra("jifen"));
        goodsName = getIntent().getStringExtra("goodsName");
        image = getIntent().getStringExtra("image");
        tv_money.setText(money + "元");
        tv_jifen.setText(jifen + "积分");
        String token = sp.getString(ConstantValues.TOKEN, null);
        if (TextUtils.isEmpty(token)) {
            // finish();
            startActivity(new Intent(context, LoginActivity.class));
        }

        // 判断支付0的时候
        if (money == 0) {
            tv_card.setVisibility(View.GONE);
            lv_card.setVisibility(View.GONE);
            ll_money.setVisibility(View.GONE);
        } else {
            tv_card.setVisibility(View.VISIBLE);
            lv_card.setVisibility(View.VISIBLE);
            ll_money.setVisibility(View.VISIBLE);
        }
        if (jifen == 0) {
            ll_jifen.setVisibility(View.GONE);
            tv_cur_jifen.setVisibility(View.GONE);
        } else {
            ll_jifen.setVisibility(View.VISIBLE);
            tv_cur_jifen.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        getdata();
    }

    private void getdata() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
        final Gson gson = new Gson();
        Map<String, String> params = new HashMap<String, String>();
        String token = sp.getString(ConstantValues.TOKEN, "");
        params.put("token", token);
        StringCallback cardCallback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                CardResaultBean cardResult = null;
                try {
                    cardResult = gson.fromJson(arg0,
                            CardResaultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cardResult == null) {
                    ToastUtil.showTextToast(context, "获取银行卡失败！");
                    return;
                }
                if (cardResult.success) {
                    datas = cardResult.bankCardList;
                    if (datas == null || datas.size() == 0) {

                        noCard = true;
                        if (money > 0) {
//                            btn_pay.setEnabled(false);
                            canPay = false;
                        } else {
                            if (canPay) {
                                btn_pay.setEnabled(true);
                            }
                        }
                    } else {
                        noCard = false;
                        if (money != 0) {
                            adapter = new MyAdapter(datas);
                            lv_card.setAdapter(adapter);
                        }
                    }
                } else {
                    ToastUtil.showTextToast(context, cardResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取银行卡失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_BANKCARD_URL, params, cardCallback);
        StringCallback scoreCallback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                final ScoreResultBean resultBean = gson.fromJson(arg0,
                        ScoreResultBean.class);
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取会员积分失败！");
                    return;
                }
                if (resultBean.success) {
//                    currJifen = "" + resultBean.memberScore.memberScore;
                    tv_cur_jifen.setText("(当前积分:" + resultBean.memberScore.memberScore + "积分)");
                } else if (resultBean.flag == 10) {
                    mApplication.login();
                    // getdata();
                } else {
                    ToastUtil.showTextToast(context, resultBean.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取会员积分失败！");
            }
        };

        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_SCORE_URL, params, scoreCallback);
    }

    // 获取数据
    private void pay() {
        //判断是否绑卡
        if (noCard) {
            SweetAlertDialog delDialog = new SweetAlertDialog(
                    context, SweetAlertDialog.WARNING_TYPE);
            delDialog
                    .setTitleText("您还没有绑定银行卡")
                    .setContentText("请先去绑定银行卡!")
                    .setCancelText("先不了")
                    .setConfirmText("去绑定")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            btn_pay.setEnabled(false);
                        }
                    })
                    .setConfirmClickListener(
                            new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(
                                        SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    startActivity(new Intent(
                                            context,
                                            AddCardStep1Activity.class));
                                }
                            }).show();
            return;
        }
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
        Map<String, String> params = new HashMap<String, String>();
        String token = sp.getString(ConstantValues.TOKEN, "");
        params.put("token", token);
        params.put("orderId", orderId);
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
                    ToastUtil.showTextToast(context, "支付失败！");
                    canPay = true;
                    return;
                }
                if (baseResult.success && baseResult.flag == 0) {
                    String tn = baseResult.tn;
                    if ("jfzfcg".equals(tn)) {
                        btn_pay.setEnabled(false);
                        canPay = false;
                        ToastUtil.showTextToast(context, baseResult.msg);
                        Intent intent = new Intent(context,
                                OrderPayResultActivity.class);
                        intent.putExtra("isBJ", isBJ);
                        intent.putExtra("image", image);
                        intent.putExtra("goodsName", goodsName);
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
                canPay = true;
            }
        };
        if (isBJ)
            DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.PAY_ZHAI_ORDER, params, callback);
        else
            DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.PAY_ORDER_URL, params, callback);
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
                        ToastUtil.showTextToast(context, "支付成功！");
                        Intent intent = new Intent(
                                context,
                                OrderPayResultActivity.class);
                        intent.putExtra("isBJ", isBJ);
                        intent.putExtra("image", image);
                        intent.putExtra("goodsName",
                                goodsName);
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
                ToastUtil.showTextToast(context, "支付成功！");
                Intent intent = new Intent(
                        context,
                        OrderPayResultActivity.class);
                intent.putExtra("isBJ", isBJ);
                intent.putExtra("image", image);
                intent.putExtra("goodsName",
                        goodsName);
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
        // 0 - 启动银联正式环境
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_pay:
                pay();
                break;

            default:
                break;
        }
    }

    class MyAdapter extends DefaultBaseAdapter<BankCardListBean> {

        private ViewHolder holder;

        public MyAdapter(List<BankCardListBean> datas) {
            super(datas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_card_pay,
                        null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView
                        .findViewById(R.id.iv_icon);
                holder.tv_card_number = (TextView) convertView
                        .findViewById(R.id.tv_card_number);
                holder.iv_chioce = (ImageView) convertView
                        .findViewById(R.id.iv_chioce);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 填充数据
            holder.iv_icon.setImageResource(R.drawable.maincard_logo);
            String bankCard = datas.get(position).BANK_CARD;
            holder.tv_card_number.setText("("
                    + bankCard.substring(bankCard.length() - 4) + ")");

            if (PayActivity.this.position == position) {
                holder.iv_chioce.setVisibility(View.VISIBLE);
            } else {
                holder.iv_chioce.setVisibility(View.GONE);
            }
            return convertView;
        }

    }

    class ViewHolder {
        private TextView tv_card_number;
        private ImageView iv_icon;
        private ImageView iv_chioce;
    }
}
