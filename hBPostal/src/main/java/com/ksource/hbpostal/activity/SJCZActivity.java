package com.ksource.hbpostal.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.PhoneUtils;
import com.blankj.utilcode.utils.RegexUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BalanceResultBean;
import com.ksource.hbpostal.bean.YYSResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.widgets.ClearEditText;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.ConvertUtil;
import com.yitao.util.DialogUtil;
import com.yitao.util.NumberUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class SJCZActivity extends BaseActivity {
    private TextView tv_title, tv_right;
    private ImageView iv_back;
    private ClearEditText cet_phone_num;
    private TextView tv_yys, tv_jifen, tv_diyong, tv_count, tv_balance, tv_user_name, tv_check;
    private ImageView iv_contacts;
    private CheckBox cb_diyong;
    private TextView btn_ok, tv_tishi;
    private LinearLayout ll_user_msg;
    private LinearLayout ll_10, ll_20, ll_30, ll_50, ll_100, ll_200, ll_300,
            ll_500;
    private TextView tv_10, tv_20, tv_30, tv_50, tv_100, tv_200, tv_300,
            tv_500;
    private TextView tv_101, tv_201, tv_301, tv_501, tv_1001, tv_2001, tv_3001,
            tv_5001;
    private List<LinearLayout> llList;
    private List<TextView> tvList;
    private List<TextView> tvList1;
    // 加减数量
    private Button btn_sub, btn_add;
    private EditText et_number;
    private int num;
    private int jifen;
    private double count;
    private double money;
    private boolean isCheck = true;
    private LoadDialog mLoadDialog;
    private String phoneNum;
    private String userName;
    //运营商编号
    private int operator;
    private String queryId;

    private double fee;
    private String accountType;
    private String token;
    private TextView tv_balts;


    @Override
    public int getLayoutResId() {
        return R.layout.activity_sjcz;
    }

    @Override
    public void initView() {
        // 头部
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_right = (TextView) findViewById(R.id.btn_right);
        tv_right.setVisibility(View.VISIBLE);
        // 脚部
        tv_count = (TextView) findViewById(R.id.tv_count);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        // 内容
        cet_phone_num = (ClearEditText) findViewById(R.id.cet_phone_num);
        tv_yys = (TextView) findViewById(R.id.tv_yys);
        tv_check = (TextView) findViewById(R.id.tv_check);
        tv_jifen = (TextView) findViewById(R.id.tv_jifen);
        tv_diyong = (TextView) findViewById(R.id.tv_diyong);
        ll_user_msg = (LinearLayout) findViewById(R.id.ll_user_msg);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        iv_contacts = (ImageView) findViewById(R.id.iv_contacts);
        cb_diyong = (CheckBox) findViewById(R.id.cb_diyong);
        tv_tishi = (TextView) findViewById(R.id.tv_tishi);
        tv_balts = (TextView) findViewById(R.id.tv_balts);

        btn_add = (Button) findViewById(R.id.btn_add);
        btn_sub = (Button) findViewById(R.id.btn_sub);
        et_number = (EditText) findViewById(R.id.et_number);

        ll_10 = (LinearLayout) findViewById(R.id.ll_10);
        ll_20 = (LinearLayout) findViewById(R.id.ll_20);
        ll_30 = (LinearLayout) findViewById(R.id.ll_30);
        ll_50 = (LinearLayout) findViewById(R.id.ll_50);
        ll_100 = (LinearLayout) findViewById(R.id.ll_100);
        ll_200 = (LinearLayout) findViewById(R.id.ll_200);
        ll_300 = (LinearLayout) findViewById(R.id.ll_300);
        ll_500 = (LinearLayout) findViewById(R.id.ll_500);
        tv_10 = (TextView) findViewById(R.id.tv_10);
        tv_20 = (TextView) findViewById(R.id.tv_20);
        tv_30 = (TextView) findViewById(R.id.tv_30);
        tv_50 = (TextView) findViewById(R.id.tv_50);
        tv_100 = (TextView) findViewById(R.id.tv_100);
        tv_200 = (TextView) findViewById(R.id.tv_200);
        tv_300 = (TextView) findViewById(R.id.tv_300);
        tv_500 = (TextView) findViewById(R.id.tv_500);
        tv_101 = (TextView) findViewById(R.id.tv_101);
        tv_201 = (TextView) findViewById(R.id.tv_201);
        tv_301 = (TextView) findViewById(R.id.tv_301);
        tv_501 = (TextView) findViewById(R.id.tv_501);
        tv_1001 = (TextView) findViewById(R.id.tv_1001);
        tv_2001 = (TextView) findViewById(R.id.tv_2001);
        tv_3001 = (TextView) findViewById(R.id.tv_3001);
        tv_5001 = (TextView) findViewById(R.id.tv_5001);

        addList();
    }

    private void addList() {
        llList = new ArrayList<>();
        tvList = new ArrayList<>();
        tvList1 = new ArrayList<>();
        llList.add(ll_10);
        llList.add(ll_20);
        llList.add(ll_30);
        llList.add(ll_50);
        llList.add(ll_100);
        llList.add(ll_200);
        llList.add(ll_300);
        llList.add(ll_500);
        tvList.add(tv_10);
        tvList.add(tv_20);
        tvList.add(tv_30);
        tvList.add(tv_50);
        tvList.add(tv_100);
        tvList.add(tv_200);
        tvList.add(tv_300);
        tvList.add(tv_500);
        tvList1.add(tv_101);
        tvList1.add(tv_201);
        tvList1.add(tv_301);
        tvList1.add(tv_501);
        tvList1.add(tv_1001);
        tvList1.add(tv_2001);
        tvList1.add(tv_3001);
        tvList1.add(tv_5001);
    }

    @Override
    public void initListener() {
        tv_check.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        iv_contacts.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_sub.setOnClickListener(this);
        et_number.setEnabled(true);
        for (LinearLayout ll : llList) {
            ll.setOnClickListener(this);
        }
        cb_diyong.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                isCheck = isChecked;
                btn_add.setEnabled(isChecked);
                btn_sub.setEnabled(isChecked);
                et_number.setEnabled(isChecked);
                if (isChecked) {
                    // ToastUtil.showTextToast(context, "选择");
                } else {
                    // ToastUtil.showTextToast(context, "取消");
                    num = 0;
                }
                et_number.setText("" + num);
                setMoney();
            }
        });
        cet_phone_num.addTextChangedListener(new TextWatcher() {


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
                phoneNum = cet_phone_num.getText().toString().trim();
                phoneNum = phoneNum.replaceAll(" ", "");
                if (phoneNum.length() >= 11) {
                    ll_user_msg.setVisibility(View.GONE);
//					String yysName = PhoneUtils.getSimOperatorName(context);
//					tv_yys.setText(yysName);
                    boolean isMobile = RegexUtils.isMobileSimple(phoneNum);
                    boolean isTel = RegexUtils.isTel(phoneNum);
                    if (isMobile) {
//                        btn_ok.setEnabled(true);
                        if (operator == 4 || operator == 5) {
                            ToastUtils.showShortToast("手机缴费请到手机充值页面缴费！");
                        }else {
                            getYYS();
                        }
                    } else if (isTel) {
//                        btn_ok.setEnabled(true);
                        if (operator == 4 || operator == 5) {
                            getBalance();
                        }else{
                            ToastUtils.showShortToast("固话缴费请到生活缴费页面添加固话号！");
                        }
                    } else {
                        btn_ok.setEnabled(false);
                    }
//					boolean isTel = com.blankj.utilcode.utils.RegexUtils.isTel(phoneNum);
//					if (isTel) {
//						btn_ok.setEnabled(true);
//						getBalance();
//					}
                } else {
                    btn_ok.setEnabled(false);
                }
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
                if (s != null && !s.equals("")) {
                    int markVal = 0;
                    try {
                        markVal = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        markVal = 0;
                    }
                    if (markVal > jifen || markVal > count) {
                        if (markVal > count) {
                            et_number.setText("" + (int) count);
                            num = (int) count;
                            ToastUtil.showTextToast(context, "使用积分不能超过" + count);
                        } else {
                            num = jifen;
                            et_number.setText("" + jifen);
                            ToastUtil.showTextToast(context, "使用积分不能超过" + jifen);
                        }
                    } else if (markVal < 0) {
                        num = 0;
//						ToastUtil.showTextToast(context, "使用积分不能小于0");
                        et_number.setText(String.valueOf(0));
                    } else {
                        num = markVal;
                    }
                    setMoney();
                }
            }
        });
    }

    //获取话费余额
    private void getBalance() {
//        ll_user_msg.setVisibility(View.GONE);
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("phoneNumber", phoneNum);
        params.put("operator", operator + "");
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                BalanceResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(arg0,
                            BalanceResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    btn_ok.setEnabled(false);
                    ToastUtil.showTextToast(context, "获取话费信息失败！");
                    return;
                }
                if (resultBean.success) {
                    tv_balts.setText("");
                    btn_ok.setEnabled(true);
                    count = 10;
                    setView(0);
                    tv_balts.setVisibility(View.GONE);
                    ll_user_msg.setVisibility(View.VISIBLE);
                    jifen = ConvertUtil.obj2Int(resultBean.sumScore);
                    userName = resultBean.name;
                    queryId = resultBean.queryId;
                    fee = ConvertUtil.obj2Double(resultBean.fee);
                    tv_user_name.setText(userName);
                    tv_balance.setText(fee + "元");

                    tv_jifen.setText("" + jifen);
                } else if (resultBean.flag == 10) {
                    mApplication.login();
                } else {
                    tv_balts.setVisibility(View.VISIBLE);
                    btn_ok.setEnabled(false);
                    ToastUtil.showTextToast(context, resultBean.msg);
                    tv_balts.setText("对不起，暂不支持该号码交费！");
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                btn_ok.setEnabled(false);
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取话费信息失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_BALANCE_URL, params, callback);
    }

    // 获取运营商信息
    private void getYYS() {
//        ll_user_msg.setVisibility(View.GONE);
//		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("phoneNumber", phoneNum);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                YYSResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(arg0,
                            YYSResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取运营商信息失败！");
                    return;
                }
                if (resultBean.success) {
                    operator = resultBean.operatorQueryResp;
                    getBalance();
                    switch (operator) {
                        case 1:
                            tv_yys.setText("中国移动");
                            break;
                        case 2:
                            tv_yys.setText("中国联通");
                            break;
                        case 3:
                            tv_yys.setText("中国电信");
                            break;

                        default:
                            tv_yys.setText("");
                            break;
                    }
                } else if (resultBean.flag == 10) {
                    mApplication.login();
                } else {
                    ToastUtil.showTextToast(context, resultBean.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                btn_ok.setEnabled(false);
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取运营商信息失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_YYS_URL, params, callback);
    }

    @Override
    public void initData() {
        token = sp.getString(ConstantValues.TOKEN, "");
        Intent intent = getIntent();
        if (intent.hasExtra("operator")) {
            operator = intent.getIntExtra("operator", 0);
        }
        if (intent.hasExtra("accountNumber")) {
            phoneNum = intent.getStringExtra("accountNumber");
            cet_phone_num.setText(phoneNum);
        }
        switch (operator) {
            case 5:
                accountType = "5";
                iv_contacts.setVisibility(View.GONE);
                tv_check.setVisibility(View.VISIBLE);
                tv_title.setText("固话缴费");
                tv_yys.setText("电信固话");
                tv_tishi.setText(getResources().getString(R.string.ghts));
                cet_phone_num.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
//                getBalance();
                break;
            case 4:
                accountType = "6";
                iv_contacts.setVisibility(View.GONE);
                tv_check.setVisibility(View.VISIBLE);
                tv_title.setText("固话缴费");
                tv_yys.setText("联通固话");
                tv_tishi.setText(getResources().getString(R.string.ghts));
                cet_phone_num.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
//                getBalance();
                break;

            default:
                accountType = "10";
                tv_title.setText("手机充值");
                tv_tishi.setText(getResources().getString(R.string.sjts));
                iv_contacts.setVisibility(View.VISIBLE);
                tv_check.setVisibility(View.GONE);
                cet_phone_num.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                cet_phone_num.setText(PhoneUtils.getPhoneNumber(context));
                break;
        }
        tv_right.setText("充值记录");

        btn_ok.setEnabled(false);
        ll_user_msg.setVisibility(View.GONE);
        count = 0;
//        setView(0);
        et_number.setText("" + num);
        tv_jifen.setText(jifen + "分");
        cb_diyong.setChecked(isCheck);
        setMoney();
    }


    /*
     * 跳转联系人列表的回调函数 /(non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case 11:
                    try {
                        Uri uri = data.getData();
                        ContentResolver cr = getContentResolver();
                        // 取得电话本中开始一项的光标
                        Cursor cursor = cr.query(uri, null, null, null, null);
                        int nameFieldColumnIndex = cursor
                                .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
                        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                        cursor.moveToFirst();
                        // 联系人名称
                        String contactName = cursor.getString(nameFieldColumnIndex);
                        int idFieldIndex = cursor
                                .getColumnIndex(ContactsContract.Contacts._ID);
                        // 联系人id 根据列名取得该联系人的id；
                        int id = cursor.getInt(idFieldIndex);
                        Cursor phonecursor = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + "=?",
                                new String[]{Integer.toString(id)}, null);
                        phonecursor.moveToFirst();
                        int numberColumn = phonecursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String contactPhoneNum = phonecursor
                                .getString(numberColumn);
                        // ToastUtil.showTextToast(context, "所选手机号为：" +
                        // contactPhoneNum+"\n"+contactName);
                        contactPhoneNum = contactPhoneNum.replace(" ", "");
                        cet_phone_num.setText(contactPhoneNum);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showTextToast(context, "获取联系人信息失败!请设置读通讯录权限");
                    }
                    break;

                default:
                    break;
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_check:
                phoneNum = cet_phone_num.getText().toString().trim();
                phoneNum = phoneNum.replaceAll(" ", "");
                if (TextUtils.isEmpty(phoneNum)) {
                    ToastUtil.showTextToast(context, "请输入固话号！");
                    cet_phone_num.requestFocus();
                    return;
                }
                getBalance();
                break;
            case R.id.btn_right:
//			if ("5".equals(accountType)) {
//				accountType = "6";
//			}
                Intent intentHistory = new Intent(context, HistoryListActivity.class);
                intentHistory.putExtra("type", Integer.parseInt(accountType));
                startActivity(intentHistory);
                break;
            case R.id.btn_ok:
//			String phoneNumber = cet_phone_num.getText().toString().trim();
                // phoneNumber = phoneNumber.replaceAll(" ", "");
//			if (NumberUtil.add(fee, count) < 0) {
//				ToastUtil.showTextToast(context, "请选择大于欠费金额的缴费额！");
//				return;
//			}
                btn_ok.setEnabled(false);
                if (!TextUtils.isEmpty(phoneNum)) {
//				ToastUtil.showTextToast(context, "立即充值");
                    Intent intent = new Intent(context, PayActivityForJF.class);
                    intent.putExtra("accountType", accountType);
                    intent.putExtra("totalScore", num + "");
                    intent.putExtra("totleMoney", count + "");
                    intent.putExtra("factMoney", money + "");
                    intent.putExtra("accountNumber", phoneNum);
                    intent.putExtra("accountName", userName);
                    intent.putExtra("queryId", queryId);
                    intent.putExtra("operator", operator + "");
                    startActivity(intent);
                } else {
                    ToastUtil.showTextToast(context, "请输入正确的电话号码！");
                }
                break;
            case R.id.iv_contacts:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 11);
                break;
            case R.id.ll_10:
                count = 10;
                setView(0);
                break;
            case R.id.ll_20:
                count = 20;
                setView(1);
                break;
            case R.id.ll_30:
                count = 30;
                setView(2);
                break;
            case R.id.ll_50:
                count = 50;
                setView(3);
                break;
            case R.id.ll_100:
                count = 100;
                setView(4);
                break;
            case R.id.ll_200:
                count = 200;
                setView(5);
                break;
            case R.id.ll_300:
                count = 300;
                setView(6);
                break;
            case R.id.ll_500:
                count = 500;
                setView(7);

                break;
            case R.id.btn_add:
                // ToastUtil.showTextToast(context, "加");
                if (num >= jifen || num >= count) {
                    if (num >= count) {
                        et_number.setText("" + (int) count);
                    } else {
                        et_number.setText("" + jifen);
                    }
                    ToastUtil.showTextToast(context, "已经不能再加了，亲");
                } else {
                    num++;
                    et_number.setText("" + num);
                }
                setMoney();
                break;
            case R.id.btn_sub:
                // ToastUtil.showTextToast(context, "减");
                if (num <= 0) {
                    et_number.setText("" + 0);
                    ToastUtil.showTextToast(context, "已经不能再减了，亲");
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
        money = NumberUtil.sub(count, ConvertUtil.obj2Double(num)) < 0 ? 0 : NumberUtil.sub(count, ConvertUtil.obj2Double(num));
        BigDecimal moneyBig = NumberUtil.doubleToLong(money);
        tv_count.setText("￥" + moneyBig);
    }

    private void setView(int id) {
        money = NumberUtil.sub(count, ConvertUtil.obj2Double(num)) < 0 ? 0 : NumberUtil.sub(count, ConvertUtil.obj2Double(num));
        BigDecimal moneyBig = NumberUtil.doubleToLong(money);
        tv_count.setText("￥" + moneyBig);
        switch (id) {
            case 0:
                for (LinearLayout ll : llList) {
                    if (ll.equals(ll_10)) {
                        ll.setBackgroundResource(R.drawable.rect_green);
                    } else {
                        ll.setBackgroundResource(R.drawable.rect_lightgary);
                    }

                }

                for (TextView tv : tvList) {
                    if (tv.equals(tv_10)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }

                }
                for (TextView tv : tvList1) {
                    if (tv.equals(tv_101)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.LTGRAY);
                    }

                }
                break;
            case 1:
                for (LinearLayout ll : llList) {
                    if (ll.equals(ll_20)) {
                        ll.setBackgroundResource(R.drawable.rect_green);
                    } else {
                        ll.setBackgroundResource(R.drawable.rect_lightgary);
                    }

                }
                for (TextView tv : tvList) {
                    if (tv.equals(tv_20)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }

                }
                for (TextView tv : tvList1) {
                    if (tv.equals(tv_201)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.LTGRAY);
                    }

                }
                break;
            case 2:
                for (LinearLayout ll : llList) {
                    if (ll.equals(ll_30)) {
                        ll.setBackgroundResource(R.drawable.rect_green);
                    } else {
                        ll.setBackgroundResource(R.drawable.rect_lightgary);
                    }

                }
                for (TextView tv : tvList) {
                    if (tv.equals(tv_30)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }

                }
                for (TextView tv : tvList1) {
                    if (tv.equals(tv_301)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.LTGRAY);
                    }

                }
                break;
            case 3:
                for (LinearLayout ll : llList) {
                    if (ll.equals(ll_50)) {
                        ll.setBackgroundResource(R.drawable.rect_green);
                    } else {
                        ll.setBackgroundResource(R.drawable.rect_lightgary);
                    }

                }
                for (TextView tv : tvList) {
                    if (tv.equals(tv_50)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }

                }
                for (TextView tv : tvList1) {
                    if (tv.equals(tv_501)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.LTGRAY);
                    }

                }
                break;
            case 4:
                for (LinearLayout ll : llList) {
                    if (ll.equals(ll_100)) {
                        ll.setBackgroundResource(R.drawable.rect_green);
                    } else {
                        ll.setBackgroundResource(R.drawable.rect_lightgary);
                    }

                }
                for (TextView tv : tvList) {
                    if (tv.equals(tv_100)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }

                }
                for (TextView tv : tvList1) {
                    if (tv.equals(tv_1001)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.LTGRAY);
                    }

                }
                break;
            case 5:
                for (LinearLayout ll : llList) {
                    if (ll.equals(ll_200)) {
                        ll.setBackgroundResource(R.drawable.rect_green);
                    } else {
                        ll.setBackgroundResource(R.drawable.rect_lightgary);
                    }

                }
                for (TextView tv : tvList) {
                    if (tv.equals(tv_200)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }

                }
                for (TextView tv : tvList1) {
                    if (tv.equals(tv_2001)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.LTGRAY);
                    }

                }
                break;
            case 6:
                for (LinearLayout ll : llList) {
                    if (ll.equals(ll_300)) {
                        ll.setBackgroundResource(R.drawable.rect_green);
                    } else {
                        ll.setBackgroundResource(R.drawable.rect_lightgary);
                    }

                }
                for (TextView tv : tvList) {
                    if (tv.equals(tv_300)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }

                }
                for (TextView tv : tvList1) {
                    if (tv.equals(tv_3001)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.LTGRAY);
                    }

                }
                break;
            case 7:
                for (LinearLayout ll : llList) {
                    if (ll.equals(ll_500)) {
                        ll.setBackgroundResource(R.drawable.rect_green);
                    } else {
                        ll.setBackgroundResource(R.drawable.rect_lightgary);
                    }

                }
                for (TextView tv : tvList) {
                    if (tv.equals(tv_500)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }

                }
                for (TextView tv : tvList1) {
                    if (tv.equals(tv_5001)) {
                        tv.setTextColor(Color.WHITE);
                    } else {
                        tv.setTextColor(Color.LTGRAY);
                    }

                }
                break;

            default:
                break;
        }

    }

}
