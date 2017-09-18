package com.ksource.hbpostal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.ScoreResultBean;
import com.ksource.hbpostal.bean.UsefulYhqResultBean;
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
 * 扫码结果-转账确认
 */
public class ScanResultActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private TextView tv_pay_user, tv_curr_jf, tv_pay, tv_add_ly, tv_ly, tv_jifen, tv_yhq;
    private EditText et_pay_money;
    private CheckBox cb_diyong;
    private Button btn_pay;

    private Button btn_sub, btn_add;
    private EditText et_number;

    private LoadDialog mLoadDialog;
    private String ly;
    private String moneyStr;
    private double money;
    private double count;
    private int jifen;
    private String id;
    private String name;
    private String sellerId;
    private boolean isCheck;
    private int num;
    private String payMoney;

    private String sellerMsg;
    List<UsefulYhqResultBean.InfoBean> datas;
    private int chioceItem = 0;
    private String token;
    private String yhqId;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_scan_result;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        tv_pay_user = (TextView) findViewById(R.id.tv_pay_user);
        tv_curr_jf = (TextView) findViewById(R.id.tv_curr_jf);
        tv_pay = (TextView) findViewById(R.id.tv_pay);
        tv_add_ly = (TextView) findViewById(R.id.tv_add_ly);
        tv_ly = (TextView) findViewById(R.id.tv_ly);
        tv_jifen = (TextView) findViewById(R.id.tv_jifen);
        tv_yhq = (TextView) findViewById(R.id.tv_yhq);
        et_pay_money = (EditText) findViewById(R.id.et_pay_money);
        et_number = (EditText) findViewById(R.id.et_number);
        btn_sub = (Button) findViewById(R.id.btn_sub);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        cb_diyong = (CheckBox) findViewById(R.id.cb_diyong);
        btn_add.setEnabled(false);
        btn_sub.setEnabled(false);
        et_number.setEnabled(false);
    }

    @Override
    public void initListener() {
        btn_pay.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_sub.setOnClickListener(this);
        tv_add_ly.setOnClickListener(this);
        tv_yhq.setOnClickListener(this);
        cb_diyong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                isCheck = isChecked;
                if (jifen < 0) {
                    btn_sub.setEnabled(false);
                    btn_add.setEnabled(false);
                    et_number.setEnabled(false);
                } else {
                    btn_add.setEnabled(isChecked);
                    btn_sub.setEnabled(isChecked);
                    et_number.setEnabled(isChecked);
                }
                if (!isChecked) {
                    // ToastUtil.showTextToast(context, "选择");
//                } else {
                    // ToastUtil.showTextToast(context, "取消");
                    num = 0;
                }
                et_number.setText("" + num);
                setMoney();
            }
        });
        et_pay_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable)) {
                    try {
                        count = ConvertUtil.obj2Double(editable.toString());
                    } catch (NumberFormatException e) {
                        count = 0.00;
                    }
                    tv_yhq.setText("请选择");
                    chioceItem = 0;
                    num = 0;
                    if (jifen >= 0)
                        et_number.setText("0");
                    setMoney();
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
                if (!TextUtils.isEmpty(s)) {
                    int markVal = 0;
                    try {
                        markVal = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        markVal = 0;
                    }
                    double yhq = 0;
                    if (chioceItem == 0) {
                        yhq = 0;
                    } else {
                        yhq = ConvertUtil.obj2Double(datas.get(chioceItem).DENOMINATION);
                    }
                    double max = NumberUtil.sub(count, yhq);
//                    if (jifen >= 0) {
                    if (markVal > jifen || markVal > max) {
//                        if (jifen < 0) {
////                            et_number.setText("0");
//                            num = 0;
//                            return;
//                        }
                        if (markVal > max) {
                            num = (int) max;
                            et_number.setText("" + num);
                            ToastUtil.showTextToast(context, "使用积分不能超过" + num);
                        } else {
//                            if (jifen < 0) {
//                                et_number.setText("0");
//                                num = 0;
//                            }else {
                            et_number.setText("" + jifen);
                            num = jifen;
//                            }
                            ToastUtil.showTextToast(context, "使用积分不能超过" + num);
                        }
                    } else if (markVal < 0) {
                        num = 0;
//						ToastUtil.showTextToast(context, "使用积分不能小于0");
                        et_number.setText("0");
                    } else {
                        num = markVal;
                    }
//                    }
                    setMoney();
                }
            }
        });
    }

    @Override
    public void initData() {
        tv_title.setText("支付");
        token = sp.getString(ConstantValues.TOKEN, "");
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        sellerId = getIntent().getStringExtra("sellerId");
        payMoney = getIntent().getStringExtra("money");
        sellerMsg = getIntent().getStringExtra("msg");
        datas = new ArrayList<>();
        if (!TextUtils.isEmpty(payMoney)) {
            money = ConvertUtil.obj2Double(payMoney);
            et_pay_money.setText(payMoney);
            et_pay_money.setEnabled(false);
        } else {
            et_pay_money.setEnabled(true);
        }
        tv_pay_user.setText("向商户“" + name + "”转账");
        setMoney();
        getJF();
    }

    //获取可用优惠券
    private void getYhq() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("seller_id", id);
        params.put("price", count + "");
        StringCallback yhqCallback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                final UsefulYhqResultBean resultBean = gson.fromJson(arg0,
                        UsefulYhqResultBean.class);
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取优惠券失败！");
                    return;
                }
                if (resultBean.success) {
                    datas = resultBean.info;
                    if (datas == null) {
                        ToastUtils.showShortToast("没有可用的优惠券！");
                    }
                    datas.add(0, new UsefulYhqResultBean.InfoBean("请选择", 0, 0, null));
                    showPopwindow();
                } else if (resultBean.flag == 10) {
                    mApplication.login();
                } else {
                    ToastUtil.showTextToast(context, resultBean.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取优惠券失败！");
            }
        };

        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.USEFUL_YHQ_URL, params, yhqCallback);
    }

    //获取用户积分
    private void getJF() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
        Map<String, String> params = new HashMap<>();

        params.put("token", token);
        StringCallback scoreCallback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                final ScoreResultBean resultBean = gson.fromJson(arg0,
                        ScoreResultBean.class);
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取会员积分失败！");
                    return;
                }
                if (resultBean.success) {
                    jifen = Integer.parseInt(resultBean.memberScore.memberScore);
                    tv_curr_jf.setText(jifen + "");
                    if (jifen < 0) {
                        cb_diyong.setEnabled(false);
                        btn_sub.setEnabled(false);
                        btn_add.setEnabled(false);
                        et_number.setEnabled(false);
                    }
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

    //设置金额
    private void setMoney() {
        money = NumberUtil.sub(count, ConvertUtil.obj2Double(num));
        if (datas != null && datas.size() > 0)
            money = NumberUtil.sub(money, ConvertUtil.obj2Double(datas.get(chioceItem).DENOMINATION));
        money = money < 0 ? 0 : money;
        if (isCheck) {
            if (money == 0) {
                tv_pay.setText(num + "");
                tv_jifen.setVisibility(View.VISIBLE);
            } else if (num == 0) {
                tv_pay.setText("￥" + NumberUtil.toDecimal2(money));
                tv_jifen.setVisibility(View.GONE);
            } else {
                tv_pay.setText("￥" + NumberUtil.toDecimal2(money) + "+" + num);
                tv_jifen.setVisibility(View.VISIBLE);
            }
        } else {
            num = 0;
            tv_pay.setText("￥" + NumberUtil.toDecimal2(money));
            tv_jifen.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_add_ly:
                showDialog();
                break;
            case R.id.tv_yhq:
                // 选择优惠券
//                ToastUtils.showShortToast("选择优惠券");
                getYhq();
                break;
            case R.id.btn_pay:
                moneyStr = et_pay_money.getText().toString().trim();
                if (TextUtils.isEmpty(moneyStr)) {
                    ToastUtil.showTextToast(context, "请输入转账金额！");
                    et_pay_money.requestFocus();
                    return;
                }
                // 跳转支付页面
                Intent intent = new Intent(context, ScanPayActivity.class);
                intent.putExtra("shop_id", id);
                intent.putExtra("seller_id", sellerId);
                if (chioceItem != 0) {
                    yhqId = datas.get(chioceItem).ID;
                    intent.putExtra("yhq_id", yhqId);
                    intent.putExtra("yhq_money", datas.get(chioceItem).DENOMINATION + "");
                }
                intent.putExtra("name", name);
                intent.putExtra("total_amount", count + "");
                intent.putExtra("pay_amount", money + "");
                intent.putExtra("score_num", num + "");
                intent.putExtra("money_set", TextUtils.isEmpty(payMoney) ? "1" : "2");
                intent.putExtra("member_leave_msg", ly == null ? "" : ly);
                intent.putExtra("seller_leave_msg", TextUtils.isEmpty(sellerMsg) ? "" : sellerMsg);
                startActivity(intent);
                break;
            case R.id.btn_add:
                // ToastUtil.showTextToast(context, "加");
                if (num >= jifen || num >= count) {
                    if (num >= count) {
                        et_number.setText("" + (int) count);
                    } else {
                        et_number.setText("" + jifen);
                    }
                    ToastUtil.showTextToast(context, "已经不能再加了！");
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
                    ToastUtil.showTextToast(context, "已经不能再减了！");
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

    /**
     * 底部弹出选择
     */
    private void showPopwindow() {
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(this, R.layout.bottom_pop_menu, null);

        RelativeLayout rl_null = (RelativeLayout) popView.findViewById(R.id.rl_null);
        ImageView iv_back = (ImageView) popView.findViewById(R.id.iv_back);
        ListView lv_yhq = (ListView) popView.findViewById(R.id.lv_yhq);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels / 2;

        final PopupWindow popWindow = new PopupWindow(popView, width, height);
        popWindow.setAnimationStyle(R.style.AnimBottom);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置允许在外点击消失
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                if (chioceItem != 0) {
                tv_yhq.setText(datas.get(chioceItem).NAME);
                setMoney();
//                }else{
//                    tv_yhq.setText(datas.get(chioceItem).NAME);
//                }
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow()
                        .getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
            }
        });

        if (datas == null) {
            rl_null.setVisibility(View.VISIBLE);
        } else {
            rl_null.setVisibility(View.GONE);
            final PopAdapter popAdapter = new PopAdapter(datas);
            lv_yhq.setAdapter(popAdapter);
            lv_yhq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    chioceItem = i;
//                        ConvertUtil.obj2Int(NumberUtil.sub(money,ConvertUtil.obj2Double(datas.get(chioceItem).DENOMINATION)));
                    if (isCheck) {
                        num = ConvertUtil.obj2Int(NumberUtil.sub(count, ConvertUtil.obj2Double(datas.get(chioceItem).DENOMINATION)));
//                        num = num - (int) money;
                        et_number.setText("" + num);
//                        et_number.setText("0");
//                        num = 0;
                    }
                    setMoney();
                    popAdapter.notifyDataSetChanged();
                    popWindow.dismiss();
                }
            });
        }
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
    }

    /**
     * 弹出对话框
     */
    private void showDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.item_edittext, null);
        final EditText et_ly = (EditText) view.findViewById(R.id.et_ly);
        dialog.setView(view);
        dialog.setButton(dialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ly = et_ly.getText().toString().trim();
                if (!TextUtils.isEmpty(ly)) {
                    tv_ly.setText(ly);
                }
            }
        });
        dialog.setButton(dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gary));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }


    class PopAdapter extends DefaultBaseAdapter<UsefulYhqResultBean.InfoBean> {

        private ViewHolder holder;

        public PopAdapter(List<UsefulYhqResultBean.InfoBean> datas) {
            super(datas);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View
                        .inflate(context, R.layout.item_yhq, null);
                holder = new ViewHolder();
                holder.tv_yhq = (TextView) convertView
                        .findViewById(R.id.tv_yhq);
                holder.iv_chioce = (ImageView) convertView
                        .findViewById(R.id.iv_chioce);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 填充数据
            holder.tv_yhq.setText(datas.get(position).NAME);
            if (chioceItem == position) {
                holder.iv_chioce.setImageResource(R.drawable.good_icon_check_cur);
            } else {
                holder.iv_chioce.setImageResource(R.drawable.good_icon_check_nor);
            }

            return convertView;
        }
    }

    class ViewHolder {
        // private TextView tv_card_name;
        private TextView tv_yhq;
        private ImageView iv_chioce;
    }
}
