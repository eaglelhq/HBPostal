package com.ksource.hbStore.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksource.hbStore.R;

/**
 * 设置备注页面
 */
public class SetBeiZhuActivity extends BaseActivity {

    private static final int SET_BEIZHU_RESULT = 103;
    private EditText et_money;
    private TextView tv_title;
    private ImageView iv_back;
    private Button btn_ok;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_set_beizhu;
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);

        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(et_money.getText())) {
                    btn_ok.setEnabled(false);
                } else {
                    btn_ok.setEnabled(true);
                }
            }
        });

    }

    @Override
    public void initData() {
        tv_title.setText("收钱备注");

    }

    @Override
    public void initView() {
        et_money = (EditText) findViewById(R.id.et_money);

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
                Intent intent = new Intent();
                intent.putExtra("beizhu",et_money.getText().toString().trim());
                setResult(SET_BEIZHU_RESULT,intent);
                finish();
                break;
            default:
                break;
        }
    }
}
