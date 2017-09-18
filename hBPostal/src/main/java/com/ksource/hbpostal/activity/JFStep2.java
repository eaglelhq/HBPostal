package com.ksource.hbpostal.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.blankj.utilcode.utils.ConvertUtils;
import com.blankj.utilcode.utils.LogUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.AccountInfoResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.widgets.CustomPopWindow;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.ConvertUtil;
import com.yitao.util.DialogUtil;
import com.yitao.util.NumberUtil;
import com.yitao.util.ToastUtil;
import com.yitao.util.UtilTool;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 生活缴费-缴费单信息
 */
public class JFStep2 extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back, iv_right;

    private TextView tv_jf_name, tv_company, tv_user_number, tv_unit_name, tv_num_name;
    private ImageView iv_jf_icon;
    private TextView tv_user_name, tv_addr, tv_time, tv_bal,
            tv_now_usage, tv_usage, tv_money;

    private TextView tv_jifen, tv_diyong, tv_count;
    private CheckBox cb_diyong;
    private TextView btn_ok;
    private LinearLayout ll_time, ll_bal, ll_edit_pay;
    private EditText et_money;

    // 加减数量
    private Button btn_sub, btn_add;
    private EditText et_number;
    private int num;
    private double count = 0.0;
    private double money = 0.0;
    private boolean isCheck = true;
    private int accountType;
    private LoadDialog mLoadDialog;
    private String token;

    private String accountName = "";
    private String accountNumber = "";
    private String address = "";
    private String readTime = "";
    private String queryId = "";
    private String unitName = "";
    private String unitKey = "";
    // private double lastRecod = 0.0;
    // private double thisRecod = 0.0;
    // private double amount = 0.0;
    private int jifen;
    private String accountId = "";
    private CustomPopWindow.PopupWindowBuilder popupWindowBuilder;
    private CustomPopWindow mCustomPopWindow;
    private boolean isSd;
    private String url;
    private String papertype;
    private String paymentBal;
    private double oweMoney;//欠费额

    @Override
    public int getLayoutResId() {
        return R.layout.activity_jf_step2;
    }

    @Override
    public void initView() {
        token = sp.getString(ConstantValues.TOKEN, null);
        popupWindowBuilder = new CustomPopWindow.PopupWindowBuilder(context);
        // 头部
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("生活缴费");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        iv_right.setImageResource(R.drawable.life_button_edit);
        iv_right.setVisibility(View.VISIBLE);
        // 内容
        iv_jf_icon = (ImageView) findViewById(R.id.iv_jf_icon);
        tv_jf_name = (TextView) findViewById(R.id.tv_jf_name);
        tv_company = (TextView) findViewById(R.id.tv_company);
        tv_unit_name = (TextView) findViewById(R.id.tv_unit_name);
        tv_num_name = (TextView) findViewById(R.id.tv_num_name);
        tv_user_number = (TextView) findViewById(R.id.tv_user_number);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_bal = (TextView) findViewById(R.id.tv_bal);
        tv_now_usage = (TextView) findViewById(R.id.tv_now_usage);
        tv_usage = (TextView) findViewById(R.id.tv_usage);
        tv_money = (TextView) findViewById(R.id.tv_money);

        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_jifen = (TextView) findViewById(R.id.tv_jifen);
        tv_diyong = (TextView) findViewById(R.id.tv_diyong);
        cb_diyong = (CheckBox) findViewById(R.id.cb_diyong);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_sub = (Button) findViewById(R.id.btn_sub);
        et_number = (EditText) findViewById(R.id.et_number);
        et_money = (EditText) findViewById(R.id.et_money);
        ll_time = (LinearLayout) findViewById(R.id.ll_time);
        ll_bal = (LinearLayout) findViewById(R.id.ll_bal);
        ll_edit_pay = (LinearLayout) findViewById(R.id.ll_edit_pay);
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_sub.setOnClickListener(this);
        popupWindowBuilder.setOnDissmissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色恢复
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        cb_diyong.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                isCheck = isChecked;
                btn_add.setEnabled(isChecked);
                btn_sub.setEnabled(isChecked);
                if (isChecked) {
                    // ToastUtil.showTextToast(context, "选择");
                } else {
                    // ToastUtil.showTextToast(context, "取消");
                    num = 0;
                }
                et_number.setEnabled(true);
                et_number.setText("" + num);
                setMoney();
            }
        });
        et_number.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    int markVal = 0;
                    try {
                        markVal = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        markVal = 0;
                    }
                    if (markVal > jifen) {
                        ToastUtil.showTextToast(context, "使用积分不能超过抵扣上限");
                        et_number.setText(String.valueOf(jifen));
                        num = jifen;
                    } else if (markVal < 0) {
                        num = 0;
//                        ToastUtil.showTextToast(context, "使用积分不能小于0");
                        et_number.setText(String.valueOf(0));
                    } else {
                        num = markVal;
                    }
                    setMoney();
                }
            }
        });

        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    count = ConvertUtil.obj2Double(s);
                    if (count <= 0)
                        btn_ok.setEnabled(false);
                    else
                        btn_ok.setEnabled(true);

                    LogUtils.e("JFmoney", count + "");
                    setMoney();
                } else {
                    btn_ok.setEnabled(false);
                }
            }
        });

    }

    @Override
    public void initData() {
        btn_ok.setEnabled(false);
        Intent intent = getIntent();
        if (intent.hasExtra("accountId"))
            accountId = intent.getStringExtra("accountId");
        if (intent.hasExtra("accountType")) {
            accountType = intent.getIntExtra("accountType", 1);
            switch (accountType) {
                case 1:
                    url = ConstantValues.GET_JF_ACCOUNT_INFO_URL;
                    iv_jf_icon.setImageResource(R.drawable.life_icon_water_bg);
                    tv_jf_name.setText("水费");
                    getData();
                    break;
                case 2:
                    url = ConstantValues.GET_JF_ACCOUNT_INFO_URL;
                    iv_jf_icon.setImageResource(R.drawable.life_icon_ele_bg);
                    tv_jf_name.setText("电费");
                    getData();
                    break;
                case 3:
                    url = ConstantValues.GET_JF_ACCOUNT_INFO_URL;
                    iv_jf_icon.setImageResource(R.drawable.life_icon_gas_bg);
                    tv_jf_name.setText("燃气费");
                    getData();
                    break;
                case 4:
                    iv_jf_icon.setImageResource(R.drawable.life_icon_tv_bg);
                    tv_jf_name.setText("有线电视");
                    tv_unit_name.setText("证件类型");
                    url = ConstantValues.GET_JF_ACCOUNT_INFO_URL;
                    getData();
                    break;
                // case 5:
                // getBalance(4);
                // accountNumber = intent.getStringExtra("");
                // iv_jf_icon.setImageResource(R.drawable.life_icon_broad_bg);
                // tv_jf_name.setText("固话宽带");
                // break;
                // case 6:
                // getBalance(5);
                // accountNumber = intent.getStringExtra("");
                // iv_jf_icon.setImageResource(R.drawable.life_icon_broad_bg);
                // tv_jf_name.setText("固话宽带");
                // break;

                default:
                    break;
            }
        }
    }

    // 获取缴费数据
    private void getData() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("id", accountId);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                LogUtils.e(arg0);
//                arg0 = "{'GD_PAPERNO': '','GD_PAPERTYPE': '','accountNumber': '0013669721','memberScore': 10,'msg': '获取成功','pamentMap': {'accountName': 'qwed','address': 'asd','late_month': '1','money': 12,'queryId': '201703231334281167555532','success': true},'pay_unit_name': '鹤壁燃气公司','success': true,'sumScore': '10'}";
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                AccountInfoResultBean homeResultBean = null;
                Gson gson = new Gson();
                try {
                    homeResultBean = gson.fromJson(arg0,
                            AccountInfoResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (homeResultBean == null) {
                    ToastUtil.showTextToast(context, "获取信息失败！");
                } else {
                    if (homeResultBean.success) {
                        ll_edit_pay.setVisibility(View.VISIBLE);
                        btn_ok.setEnabled(true);
                        if (!TextUtils
                                .isEmpty(homeResultBean.pamentMap.accountName)) {
                            accountName = homeResultBean.pamentMap.accountName;
                        }
                        if (!TextUtils.isEmpty(homeResultBean.accountNumber)) {
                            accountNumber = homeResultBean.accountNumber;
                        }
                        if (accountType == 4) {
                            if (!TextUtils
                                    .isEmpty(homeResultBean.pamentMap.area_name)) {
                                address = homeResultBean.pamentMap.area_name;
                            }
                        } else {
                            if (!TextUtils
                                    .isEmpty(homeResultBean.pamentMap.address)) {
                                address = homeResultBean.pamentMap.address;
                            }
                        }
                        if (!TextUtils
                                .isEmpty(homeResultBean.pamentMap.late_month)) {
                            readTime = homeResultBean.pamentMap.late_month
                                    + "月";
                        }
                        if (!TextUtils
                                .isEmpty(homeResultBean.pamentMap.queryId)) {
                            queryId = homeResultBean.pamentMap.queryId;
                        }
                        if (!TextUtils.isEmpty(homeResultBean.pay_unit_name)) {
                            unitName = homeResultBean.pay_unit_name;
                        }
                        if (!TextUtils.isEmpty(homeResultBean.PAY_UNIT_KEY)) {
                            unitKey = homeResultBean.PAY_UNIT_KEY;
                        }
                        if (!TextUtils.isEmpty(homeResultBean.GD_PAPERTYPE)) {
                            papertype = homeResultBean.GD_PAPERTYPE;
                        }
                        if (!TextUtils.isEmpty(homeResultBean.pamentMap.paymentBal)) {
                            paymentBal = homeResultBean.pamentMap.paymentBal;
                        }
//						if (accountType == 4 && !TextUtils.isEmpty(homeResultBean.pamentMap.money+"")) {
//							paymentBal = homeResultBean.pamentMap.money+"";
//						}

                        if (accountType == 3) {
                            ll_edit_pay.setVisibility(View.GONE);
                        }
                        int isSd = homeResultBean.pamentMap.isSd;
                        if (isSd == 1) {
                            JFStep2.this.isSd = true;
                        } else if (isSd == 2) {
                            JFStep2.this.isSd = false;
                        }
                        // lastRecod =
                        // homeResultBean.pamentMap.lastRecod;
                        // thisRecod =
                        // homeResultBean.pamentMap.thisRecod;
                        // amount = homeResultBean.pamentMap.amount;
                        count = homeResultBean.pamentMap.money;//欠费额
                        oweMoney = homeResultBean.pamentMap.money;//欠费额

                        String sumScore = homeResultBean.sumScore;
                        sumScore = sumScore == null ? "0" : sumScore;
                        jifen = ConvertUtil.obj2Int(sumScore);
                        setText();
                    } else {
                        ll_edit_pay.setVisibility(View.GONE);
                        if (homeResultBean.flag == 10) {
                            mApplication.login();
                        } else {
                            SweetAlertDialog dialog = new SweetAlertDialog(context,
                                    SweetAlertDialog.WARNING_TYPE);
                            dialog.setTitleText(homeResultBean.msg)
                                    .setContentText(TextUtils.isEmpty(homeResultBean.error) ? "" : homeResultBean.error)
                                    .setConfirmText("确定")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    }).show();
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            });
//                            ToastUtil
//                                    .showTextToast(context, homeResultBean.error);
                        }
                    }
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                ll_edit_pay.setVisibility(View.GONE);
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取信息失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,url, params, callback);
    }

    private void setText() {
        tv_company.setText(unitName);
        if (accountType == 4) {
            tv_num_name.setText(unitName + "号");
        } else {
            tv_num_name.setText("户号");
        }

        tv_user_number.setText(accountNumber);
        tv_user_name.setText(UtilTool.showStarName(accountName));
        tv_addr.setText(UtilTool.showStarAddr(address,address.length()-7,address.length()-3));
        if (TextUtils.isEmpty(readTime)) {
            ll_time.setVisibility(View.GONE);
        } else {
            ll_time.setVisibility(View.VISIBLE);
            tv_time.setText(readTime);
        }
        if (TextUtils.isEmpty(paymentBal)) {
            ll_bal.setVisibility(View.GONE);
        } else {
            ll_bal.setVisibility(View.VISIBLE);
            tv_bal.setText("￥" + paymentBal);
        }

        // tv_last_usage.setText(lastRecod + "");
        // tv_now_usage.setText(thisRecod + "");
        // tv_usage.setText(amount + "");
        tv_money.setText("￥" + count);
        //不欠费是不去缴费
        if (count <= 0) {
            btn_ok.setEnabled(false);
        } else {
            btn_ok.setEnabled(true);
            et_money.setText(count + "");
        }
        et_number.setText("" + num);
        tv_jifen.setText(jifen + "分");
        setMoney();
        cb_diyong.setChecked(isCheck);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
                // ToastUtil.showTextToast(context, "充值记录");
                showPopMenu();
                break;
            case R.id.btn_ok:
                String payMoney = et_money.getText().toString().trim();
                if (ConvertUtil.obj2Double(payMoney) < oweMoney) {
                    ToastUtil.showTextToast(context, "充值金额需要大于应缴金额！");
                    et_money.requestFocus();
                    return;
                }
                if (ConvertUtil.obj2Double(payMoney) <= 0) {
                    ToastUtil.showTextToast(context, "请输入充值金额！");
                    et_money.requestFocus();
                    return;
                }
                btn_ok.setEnabled(false);
                // ToastUtil.showTextToast(context, "立即缴费");
                Intent intent = new Intent(context, PayActivityForJF.class);
                intent.putExtra("accountType", accountType + "");
                intent.putExtra("accountId", accountId + "");
                intent.putExtra("totleMoney", count + "");
                intent.putExtra("totalScore", num + "");
                intent.putExtra("unitName", unitName);
                intent.putExtra("unitKey", unitKey);
                intent.putExtra("address", address);
                intent.putExtra("accountNumber", accountNumber);
                intent.putExtra("accountName", accountName);
                intent.putExtra("queryId", queryId);
                intent.putExtra("factMoney", money + "");
                // intent.putExtra("lastNumber", lastRecod + "");
                // intent.putExtra("nowNumber", thisRecod + "");
                // intent.putExtra("payNumber", amount + "");
                // intent.putExtra("bankName", "邮政储蓄银行");
                // intent.putExtra("bankNumber", "51121546465");
                // intent.putExtra("startTime", "20161206");
                intent.putExtra("papertype", papertype);
                if (accountType == 2) {
                    if (isSd) {
                        intent.putExtra("isSd", "1");
                    } else {
                        intent.putExtra("isSd", "2");
                    }
                }
                startActivity(intent);
                // pay();
                break;

            case R.id.btn_add:
                if (num >= jifen) {
                    et_number.setText("" + jifen);
                } else {
                    num++;
                    et_number.setText("" + num);
                }
                setMoney();
                break;
            case R.id.btn_sub:
                if (num <= 0) {
                    et_number.setText("" + 0);
                } else {
                    num--;
                    et_number.setText("" + num);
                }
                setMoney();
                break;

            default:
                break;
        }

    }

    private void setMoney() {
        tv_diyong.setText("积分抵用" + num + "元");
        money = NumberUtil.sub(count, ConvertUtil.obj2Double(num)) < 0 ? 0
                : NumberUtil.sub(count, ConvertUtil.obj2Double(num));
        tv_count.setText("￥" + money);
    }

    private void showPopMenu() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu,
                null);
        // 处理popWindow 显示内容
        // handleLogic(contentView);
        contentView.findViewById(R.id.menu1).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                            // 设置背景颜色变暗
                            WindowManager.LayoutParams lp = getWindow()
                                    .getAttributes();
                            lp.alpha = 1.0f;
                            getWindow().setAttributes(lp);
                        }
                        Intent intent = new Intent(context,
                                HistoryListActivity.class);
                        intent.putExtra("type", accountType);
                        startActivity(intent);
                    }
                });
        contentView.findViewById(R.id.menu2).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                            // 设置背景颜色变暗
                            WindowManager.LayoutParams lp = getWindow()
                                    .getAttributes();
                            lp.alpha = 1.0f;
                            getWindow().setAttributes(lp);
                        }
                        //  使用帮助页面
                        Intent intent = new Intent(context,
                                UseHelpActivity.class);
                        startActivity(intent);
                    }
                });
        mCustomPopWindow = popupWindowBuilder.setView(contentView).create()
                .showAsDropDown(iv_right, ConvertUtils.dp2px(-80), 0);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);

    }

}
