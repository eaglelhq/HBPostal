package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.DefAddrResaultBean;
import com.ksource.hbpostal.bean.SubOrderResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.ConvertUtil;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 八戒农场-农产品购买-确认订单
 */
public class ConfirmOrderForBJ extends BaseActivity {

    private static final int REQUEST_CODE = 100;
    private TextView tv_title;
    private ImageView iv_back;
    private ImageView iv_goods;
    private TextView tv_sh_name, tv_phone, tv_addr, tv_money;
    private TextView tv_goods_name, tv_goods_desc, tv_price;
    private EditText et_message;
    private RelativeLayout iv_more;
    private Button btn_submit;
    private double money;

    private LoadDialog mLoadDialog;
    private String goodIds = "";
    private String addressId = "";
    private String provinceId = "";
    private String postalName = "";
    private String postalId = "";
    private String orderNote = "";
    private String deliveryMode = "";
    private String token;
    private String key_code;
    private String key_name;
    private String option_key_code;
    private String option_key_name;
    private String goodsName;
    private String imagePath;
    private String goods_desc;
    private String price;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_confirm_bj_order;
    }

    @Override
    public void initView() {
        // 头部
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("确认订单");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        // 内容
        iv_goods = (ImageView) findViewById(R.id.iv_goods);
        iv_more = (RelativeLayout) findViewById(R.id.ll_addr);
        tv_sh_name = (TextView) findViewById(R.id.tv_sh_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);
        tv_goods_desc = (TextView) findViewById(R.id.tv_goods_desc);
        tv_price = (TextView) findViewById(R.id.tv_price);
        et_message = (EditText) findViewById(R.id.et_message);

        // 底部
        tv_money = (TextView) findViewById(R.id.tv_money);
        btn_submit = (Button) findViewById(R.id.btn_submit);

    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        iv_more.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();

        goodIds = intent.getStringExtra("goods_id");
        goodsName = intent.getStringExtra("goods_name");
        imagePath = intent.getStringExtra("goods_image");
        goods_desc = intent.getStringExtra("goods_desc");
        price = intent.getStringExtra("total_price");
        goodIds = intent.getStringExtra("goods_id");
        key_code = intent.getStringExtra("key_code");
        key_name = intent.getStringExtra("key_name");
        option_key_code = intent.getStringExtra("option_key_code");
        option_key_name = intent.getStringExtra("option_key_name");
        token = sp.getString(ConstantValues.TOKEN, null);
        getDefaultAddr();
        money = ConvertUtil.obj2Double(price);
        tv_money.setText("￥" + money);
        tv_goods_name.setText(goodsName);
        tv_goods_desc.setText(goods_desc);
        tv_price.setText("￥" + money);
        try {
            ImageLoaderUtil
                    .loadNetPic(ConstantValues.BASE_URL+imagePath,iv_goods);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 回调方法，从第二个页面回来的时候会执行这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String addr = data.getStringExtra("addr");
        String userName = data.getStringExtra("userName");
        String phone = data.getStringExtra("phone");
        String addrId = data.getStringExtra("addrId");
        String provinceId = data.getStringExtra("provinceId");
        // 根据上面发送过去的请求吗来区别
        if (requestCode == this.REQUEST_CODE) {
            setAddr(userName, phone, addr, addrId, provinceId);
        }
    }

    private void setAddr(String userName, String phone, String addr,
                         String addrId, String provinceId) {
        tv_sh_name.setText(userName);
        tv_phone.setText(phone);
        tv_addr.setText(addr);
        this.addressId = addrId;
        this.provinceId = provinceId;
    }

    private void getText() {
        orderNote = et_message.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_addr:
                // ToastUtil.showTextToast(context, "去设置收货地址");
                Intent intent = new Intent(context, ChioceAddrActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.btn_submit:
                if (TextUtils.isEmpty(token)) {
                    // finish();
                    startActivity(new Intent(context, LoginActivity.class));
                }
                if (TextUtils.isEmpty(addressId)) {
                     ToastUtil.showTextToast(context, "请先选择收货地址！");
//                    new SweetAlertDialog(context).setTitleText("请先选择收货地址！").show();
                    return;
                }
                // ToastUtil.showTextToast(context, "提交订单");

                getText();
                submitOrder();
                break;
            default:
                break;
        }

    }

    // 提交订单
    private void submitOrder() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "提交订单中...");
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("goods_id", goodIds);
        params.put("total_price", money+"");
        params.put("addressId", addressId);
        params.put("order_note", orderNote);
        params.put("key_code", key_code);
        params.put("key_name" , key_name);
        params.put("option_key_code", option_key_code);
        params.put("option_key_name", option_key_name);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                SubOrderResultBean subResult = null;
                try {
                    subResult = gson.fromJson(arg0,
                            SubOrderResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (subResult == null) {
                    ToastUtil.showTextToast(context,"提交订单失败！");
                    return;
                }
                if (subResult.success) {
                    final String orderId = subResult.orderId;
                    ToastUtil.showTextToast(context,"提交订单成功！");
                    btn_submit
                            .setEnabled(false);
                    Intent intent = new Intent(
                            context,
                            PayActivity.class);
                    intent.putExtra("isBJ",true);
                    intent.putExtra("orderId",
                            subResult.orderId);
                    intent.putExtra("money",
                            money+ "");
                    intent.putExtra("jifen",
                            0 + "");
                    intent.putExtra(
                            "goodsName",goodsName);
                    intent.putExtra(
                            "image",imagePath);
                    startActivity(intent);
                } else if (subResult.flag == 10) {
                    mApplication.login();
                    // submitOrder();
                } else {
//                    new SweetAlertDialog(context,
//                            SweetAlertDialog.ERROR_TYPE).setTitleText(
//                            "提交订单失败！").show();
                    ToastUtil.showTextToast(context,"提交订单失败！");
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context,"提交订单失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.SUBMIT_ZHAI_ORDER, params, callback);

    }


    // 获取默认收货地址
    private void getDefaultAddr() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("type", "1");
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                DefAddrResaultBean addrResult = null;
                try {
                    Gson gson = new Gson();
                    addrResult = gson.fromJson(arg0,
                            DefAddrResaultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (addrResult == null) {
                    ToastUtil.showTextToast(context, "获取默认收货地址失败！");
                    return;
                }
                if (addrResult.success) {
                    if (!TextUtils.isEmpty(addrResult.address.NAME)) {
                        tv_sh_name.setText(addrResult.address.NAME);
                    }
                    if (!TextUtils.isEmpty(addrResult.address.MOBILE)) {
                        tv_phone.setText(addrResult.address.MOBILE);
                    }
                    if (!TextUtils
                            .isEmpty(addrResult.address.PROVINCE_NAME)
                            && !TextUtils
                            .isEmpty(addrResult.address.ADDRESS)) {
                        tv_addr.setText(addrResult.address.PROVINCE_NAME
                                + addrResult.address.CITY_NAME
                                + addrResult.address.AREA_NAME
                                + addrResult.address.ADDRESS);
                    }
                    if (!TextUtils.isEmpty(addrResult.address.ID)) {
                        addressId = addrResult.address.ID;
                    }
                    if (!TextUtils
                            .isEmpty(addrResult.address.PROVINCE_ID)) {
                        provinceId = addrResult.address.PROVINCE_ID;
                    }
                } else {
                    ToastUtil.showTextToast(context, addrResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取默认收货地址失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_DEFAULT_ADDR, params, callback);
    }

}
