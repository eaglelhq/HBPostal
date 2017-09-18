package com.ksource.hbStore.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbStore.R;
import com.ksource.hbStore.bean.BaseResultBean;
import com.ksource.hbStore.config.ConstantValues;
import com.ksource.hbStore.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 设置支付账号页面
 */
public class SetCardActivity extends BaseActivity {

    private EditText et_name, et_card;
    private TextView tv_title;
    private ImageView iv_back;
    private Button btn_ok;
    private LoadDialog mLoadDialog;
    private String name;
    private String card;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_set_pay_card;
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);

        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(et_name.getText()) || TextUtils.isEmpty(et_card.getText())) {
                    btn_ok.setEnabled(false);
                } else {
                    btn_ok.setEnabled(true);
                }
            }
        });
        et_card.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(et_card.getText()) || TextUtils.isEmpty(et_name.getText())) {
                    btn_ok.setEnabled(false);
                } else {
                    btn_ok.setEnabled(true);
                }
            }
        });

    }

    @Override
    public void initData() {
        tv_title.setText("支付账号设置");

        name = getIntent().getStringExtra("name");
        card = getIntent().getStringExtra("card");

        et_name.setText(name);
        et_card.setText(card);
    }

    @Override
    public void initView() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_card = (EditText) findViewById(R.id.et_card);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setEnabled(false);
        // boolean isAutoLogin = sp.getBoolean(ConstantValues.KEY_ISAUTOLOGIN,
        // true);
        // if (isAutoLogin) {
        // login();
        // }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_ok:
                setCard();
                break;
            default:
                break;
        }
    }

    //设置收款账号
    private void setCard() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "设置收款账号...");
        Map<String, String> params = new HashMap<String, String>();
        String token = spUtils.getString(ConstantValues.TOKEN);
        params.put("token", token);
        params.put("accountName", et_name.getText().toString().trim());
        params.put("accountNum", et_card.getText().toString().trim());
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                BaseResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(arg0, BaseResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean != null && resultBean.success) {
                    ToastUtil.showTextToast(context, resultBean.msg);
                    finish();
                } else {
                    if (TextUtils.isEmpty(resultBean.msg))
                        ToastUtil.showTextToast(context, "设置收款账号失败！");
                    else
                        ToastUtil.showTextToast(context, resultBean.msg);

                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "设置收款账号失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.SET_CARD_URL, params, callback);
    }
}
