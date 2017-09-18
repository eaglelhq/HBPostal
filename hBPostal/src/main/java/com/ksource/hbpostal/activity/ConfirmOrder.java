package com.ksource.hbpostal.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.DefAddrResaultBean;
import com.ksource.hbpostal.bean.GoodsResultBean.GoodsInfoBean;
import com.ksource.hbpostal.bean.PostalResultBean;
import com.ksource.hbpostal.bean.PostalResultBean.PostalBean;
import com.ksource.hbpostal.bean.SubOrderResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.widgets.MyListView;
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
 * 单件商品直接购买-确认订单
 */
public class ConfirmOrder extends BaseActivity {

    private static final int REQUEST_CODE = 100;
    private TextView tv_title;
    private ImageView iv_back;
    private TextView tv_sh_name, tv_phone, tv_addr, tv_delivery_mode,
            tv_postal, tv_count, tv_money, tv_jifen, tv_postal_money;
    private EditText et_message;
    private RelativeLayout iv_more;
    private MyListView lv_goods_list;
    private Button btn_submit;
    private LinearLayout ll_chioce_postal;
    private BaseAdapter adapter;
    private List<GoodsInfoBean> datas;
    private double money;
    private double sendPrice;
    private int jifen;
    private GoodsInfoBean goodsBean;
    private int number;

    private LoadDialog mLoadDialog;
    private String[] postals;
    private List<PostalBean> postaslList;
    private String sendType = "";
    private String goodIds = "";
    private String buyNums = "";
    private String addressId = "";
    private String provinceId = "";
    private String postalName = "";
    private String postalId = "";
    private String orderNote = "";
    private String deliveryMode = "";
    private String token;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_confirm_order;
    }

    @Override
    public void initView() {
        // 头部
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("确认订单");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        // 内容
        iv_more = (RelativeLayout) findViewById(R.id.ll_addr);
        tv_sh_name = (TextView) findViewById(R.id.tv_sh_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        tv_delivery_mode = (TextView) findViewById(R.id.tv_delivery_mode);
        tv_postal = (TextView) findViewById(R.id.tv_postal);
        tv_postal_money = (TextView) findViewById(R.id.tv_postal_money);
        et_message = (EditText) findViewById(R.id.et_message);
        lv_goods_list = (MyListView) findViewById(R.id.lv_goods_list);
        ll_chioce_postal = (LinearLayout) findViewById(R.id.ll_chioce_postal);

        // 底部
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_money = (TextView) findViewById(R.id.tv_money);
//        tv_jifen = (TextView) findViewById(R.id.tv_jifen);
        btn_submit = (Button) findViewById(R.id.btn_submit);

    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        iv_more.setOnClickListener(this);
        tv_delivery_mode.setOnClickListener(this);
        tv_postal.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void initData() {
        token = sp.getString(ConstantValues.TOKEN, null);
        goodsBean = (GoodsInfoBean) getIntent().getSerializableExtra(
                "goodsBean");
        number = getIntent().getIntExtra("number", 1);
        getDefaultAddr();
        //默认配送方式
        tv_delivery_mode.setText("网点自提");
        ll_chioce_postal.setVisibility(View.VISIBLE);
        tv_postal_money.setText("免邮费");
        getPostal();
//        tv_postal.setText("");
        datas = new ArrayList<>();
        datas.add(goodsBean);
        adapter = new MyAdapter(datas);
        lv_goods_list.setAdapter(adapter);
        tv_count.setText(number + "件");
//        tv_postal_money.setText(sendPrice + "元");
        money = ConvertUtil.obj2Double(goodsBean.PRICE);
        money = NumberUtil.mul(money, ConvertUtil.obj2Double(number));
        // DecimalFormat f = new DecimalFormat("0.00");
        // money = Float.parseFloat(f.format(money));

        jifen = ConvertUtil.obj2Int(goodsBean.INTEGRAL) * number;
//        tv_jifen.setText("" + jifen);
        setMoney();
        deliveryMode = tv_delivery_mode.getText().toString().trim();
    }

    //设置支付共计
    private void setMoney() {
        if (money == 0) {
            tv_money.setText(jifen + "积分");
        } else if (jifen == 0) {
            tv_money.setText("￥" + NumberUtil.toDecimal2(money));
        } else {
            tv_money.setText("￥" + NumberUtil.toDecimal2(money) + "+" + jifen + "积分");
        }
    }

    // private void setDefaultAddr() {
    // // tv_sh_name.setText(sp.getString(ConstantValues.DEFAULT_NAME, ""));
    // // tv_phone.setText(sp.getString(ConstantValues.DEFAULT_PHONE, ""));
    // // tv_addr.setText(sp.getString(ConstantValues.DEFAULT_ADDR, ""));
    // // addressId = sp.getString(ConstantValues.DEFAULT_ADDR_ID, "");
    // // provinceId = sp.getString(ConstantValues.PROVINCE_ID, "");
    // if (!TextUtils.isEmpty(provinceId)) {
    // getPostMoney();
    // }
    // }

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
        if ("邮政配送".equals(deliveryMode)) {
            ll_chioce_postal.setVisibility(View.GONE);
            getPostMoney();
        } else if ("网点自提".equals(deliveryMode)) {
            ll_chioce_postal.setVisibility(View.VISIBLE);
            tv_postal_money.setText("免邮费");
            getPostal();
        }
    }

    private void getText() {
        sendType = "网点自提".equals(deliveryMode) ? "2" : "1";
        goodIds = goodsBean.ID;
        buyNums = number + "";
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
//				new SweetAlertDialog(context).setTitleText("请先选择收货地址！").show();
                    ToastUtil.showTextToast(context, "请先选择收货地址！");
                    return;
                }

                if ("网点自提".equals(deliveryMode)
                        && TextUtils.isEmpty(tv_postal.getText().toString().trim())) {
//				new SweetAlertDialog(context).setTitleText("请选择自提网点！").show();
                    ToastUtil.showTextToast(context, "请选择自提网点！");
                    return;
                }
                getText();
                submitOrder();
                break;
            case R.id.tv_delivery_mode:
                // ToastUtil.showTextToast(context, "选择配送方式");
                chioceMode();
                break;
            case R.id.tv_postal:
                // ToastUtil.showTextToast(context, "选择邮局");
                chiocePostal(postals);
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
        params.put("send_type", sendType);
        params.put("goodIds", goodIds);
        params.put("buyNums", buyNums);
        params.put("addressId", addressId);
        if ("2".equals(sendType)) {
            params.put("take_place", postalName);
            params.put("take_place_id", postalId);
        }else{
            params.put("take_place", "");
            params.put("take_place_id", "");
        }
        params.put("order_note", orderNote);
        params.put("send_price", sendPrice + "");
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
                    ToastUtil.showTextToast(context, "提交订单失败！");
                    return;
                }

                if (subResult.success) {
                    final String orderId = subResult.orderId;
                    ToastUtil.showTextToast(context, "提交订单成功！");
                    btn_submit
                            .setEnabled(false);
                    Intent intent = new Intent(
                            context,
                            PayActivity.class);
                    intent.putExtra("orderId",
                            orderId);
                    intent.putExtra("money",
                            money + sendPrice
                                    + "");
                    intent.putExtra("jifen",
                            jifen + "");
                    intent.putExtra(
                            "goodsName",
                            datas.get(0).NAME);
                    intent.putExtra(
                            "image",
                            datas.get(0).goodsImgList
                                    .get(0).FILE_PATH);
                    startActivity(intent);
                } else if (subResult.flag == 10) {
                    mApplication.login();
                    // submitOrder();
                } else {
                    ToastUtil.showTextToast(context, "提交订单失败！");
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "提交订单失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.SUBMIT_ORDER_URL, params, callback);

    }

    class MyAdapter extends DefaultBaseAdapter<GoodsInfoBean> {

        private ViewHolder holder;

        public MyAdapter(List<GoodsInfoBean> datas) {
            super(datas);
        }

        @SuppressWarnings("finally")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_cart_goods,
                        null);
                holder = new ViewHolder();
                holder.cb_chioce = (CheckBox) convertView
                        .findViewById(R.id.cb_chioce);
                holder.tv_name_list_item = (TextView) convertView
                        .findViewById(R.id.tv_name_list_item);
                holder.iv_goods_icon = (ImageView) convertView
                        .findViewById(R.id.iv_goods_icon);
                holder.tv_goods_jifen = (TextView) convertView
                        .findViewById(R.id.tv_goods_jifen);
                holder.tv_jifen_name = (TextView) convertView
                        .findViewById(R.id.tv_jifen_name);
                holder.tv_goods_price = (TextView) convertView
                        .findViewById(R.id.tv_goods_price);
                holder.tv_add = (TextView) convertView
                        .findViewById(R.id.tv_add);
                holder.tv_goods_number = (TextView) convertView
                        .findViewById(R.id.tv_goods_number);
                holder.tv_cate_name = (TextView) convertView
                        .findViewById(R.id.tv_cate_name);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();

            }

            // 填数据
            holder.cb_chioce.setVisibility(View.GONE);
            holder.tv_name_list_item.setText(datas.get(position).NAME);
            holder.tv_cate_name.setText(datas.get(position).LV1_NAME + " "
                    + datas.get(position).LV2_NAME + " "
                    + datas.get(position).LV3_NAME);
            double useMoney = ConvertUtil.obj2Double(datas.get(position).PRICE);
            int useScore = ConvertUtil.obj2Int(datas.get(position).INTEGRAL);
            int type = 0;
            if (useMoney == 0.0){
                type = 3;
            }else if (useScore == 0){
                type = 1;
            }else{
                type = 2;
            }
            switch (type) {
                case 1:
                    holder.tv_goods_price.setText("￥" +  NumberUtil.toDecimal2(useMoney));
                    holder.tv_add.setVisibility(View.GONE);
                    holder.tv_jifen_name.setVisibility(View.GONE);
                    holder.tv_goods_jifen.setVisibility(View.GONE);
                    break;
                case 2:
                    holder.tv_goods_price.setText("￥" +  NumberUtil.toDecimal2(useMoney));
                    holder.tv_add.setVisibility(View.VISIBLE);
                    holder.tv_goods_jifen.setVisibility(View.VISIBLE);
                    holder.tv_goods_jifen.setText(useScore + "");
                    holder.tv_jifen_name.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    holder.tv_goods_price.setVisibility(View.GONE);
                    holder.tv_add.setVisibility(View.GONE);
                    holder.tv_goods_jifen.setText(useScore + "");
                    holder.tv_goods_jifen.setVisibility(View.VISIBLE);
                    holder.tv_jifen_name.setVisibility(View.VISIBLE);

                    break;

                default:
                    break;
            }
            holder.tv_goods_number.setText("×" + number);
            try {
                ImageLoaderUtil
                        .loadNetPic(
                                ConstantValues.BASE_URL
                                        + datas.get(position).goodsImgList
                                        .get(0).FILE_PATH,
                                holder.iv_goods_icon);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {

                return convertView;

            }
        }

    }

    class ViewHolder {
        private CheckBox cb_chioce;
        private ImageView iv_goods_icon;
        private TextView tv_name_list_item;
        private TextView tv_goods_price;
        private TextView tv_add;
        private TextView tv_goods_jifen;
        private TextView tv_jifen_name;
        private TextView tv_cate_name;
        private TextView tv_goods_number;
    }

    public void chioceMode() {

        Builder builder = new Builder(this);
        builder.setTitle("请选择配送方式");
        final String[] cities = new String[]{"网点自提", "邮政配送"};
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            /*
             * 第一个参数代表对话框对象 第二个参数是点击对象的索引
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_delivery_mode.setText(cities[which]);
                switch (which) {
                    case 0:
                        deliveryMode = cities[0];
                        ll_chioce_postal.setVisibility(View.VISIBLE);
                        tv_postal_money.setText("免邮费");
                        sendPrice = 0;
                        tv_money.setText("￥" + money);
                        getPostal();
                        break;
                    case 1:
                        deliveryMode = cities[1];
                        ll_chioce_postal.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(provinceId)) {
                            getPostMoney();
                        } else {
                            ToastUtil.showTextToast(context, "请选择收货地址！");
                        }
                        break;

                    default:
                        break;
                }
            }

        });
        builder.show();
    }

    // 获取邮费
    private void getPostMoney() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", provinceId);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                PostalResultBean postalResult = null;
                try {
                    Gson gson = new Gson();
                    postalResult = gson.fromJson(arg0,
                            PostalResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (postalResult == null) {
                    ToastUtil.showTextToast(context, "获取邮费失败！");
                    return;
                }
                if (postalResult.success) {
                    tv_postal_money.setText(NumberUtil.toDecimal2(postalResult.money) + "元");
                    sendPrice = postalResult.money;
                    tv_money.setText("￥" + NumberUtil.add(money, sendPrice));
                } else {
                    ToastUtil.showTextToast(context, postalResult.msg);
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取邮费失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_POST_MONEY_URL, params, callback);

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
                    if (!TextUtils.isEmpty(provinceId) && "1".equals(sendType)) {
                        getPostMoney();
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
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_DEFAULT_ADDR, params, callback);
    }

    // 获取邮局网点列表
    private void getPostal() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "获取自提网点...");
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                PostalResultBean postalResult = null;
                try {
                    postalResult = gson.fromJson(
                            arg0, PostalResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (postalResult == null) {
                    ToastUtil.showTextToast(context, " 获取邮局网点失败！");
                    return;
                }
                if (postalResult.success) {
                    if (postalResult.branchList != null
                            && postalResult.branchList.size() > 0) {
                        postaslList = postalResult.branchList;
                        postalId = postaslList.get(0).ID;
                        postalName = postaslList.get(0).NAME;
                        tv_postal.setText(postalName);
                        postals = new String[postaslList.size()];
                        for (int i = 0; i < postaslList.size(); i++) {
                            postals[i] = postaslList.get(i).NAME;
                        }
                    }
                } else {
                    ToastUtil.showTextToast(context, postalResult.msg);
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

            }
        };
        DataUtil.doGetData(mLoadDialog, context, ConstantValues.GET_STATION_URL, callback);
    }

    public void chiocePostal(final String[] postals) {

        Builder builder = new Builder(this);
        builder.setTitle("请选择自提网点");
        builder.setItems(postals, new DialogInterface.OnClickListener() {
            /*
             * 第一个参数代表对话框对象 第二个参数是点击对象的索引
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postalId = postaslList.get(which).ID;
                postalName = postaslList.get(which).NAME;
                tv_postal.setText(postalName);
            }
        });
        builder.show();
    }

}
