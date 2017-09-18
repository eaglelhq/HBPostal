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
 * 设置页面
 */
public class SettingActivity extends BaseActivity {

    private static final int SET_MONEY_RESULT = 101;
    private static final int SET_BEIZHU_REQUEST = 102;
    private EditText et_money;
    private TextView tv_beizhu,tv_content;
    private TextView tv_title;
    private ImageView iv_back;
    private Button btn_ok;
    private String beizhu;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_set_money;
    }

    @Override
    public void initListener() {
        tv_beizhu.setOnClickListener(this);
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
        tv_title.setText("设置收款金额");

    }

    @Override
    public void initView() {
        et_money = (EditText) findViewById(R.id.et_money);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_beizhu = (TextView) findViewById(R.id.tv_beizhu);
        tv_content = (TextView) findViewById(R.id.tv_content);
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
            case R.id.btn_ok:
                Intent intent = new Intent();
                intent.putExtra("money",et_money.getText().toString().trim());
                if (!TextUtils.isEmpty(beizhu)){
                    intent.putExtra("beizhu",beizhu);
                }
                setResult(SET_MONEY_RESULT,intent);
                finish();
                break;
            case R.id.tv_beizhu:
                startActivityForResult(new Intent(context, SetBeiZhuActivity.class),SET_BEIZHU_REQUEST);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            beizhu = data.getStringExtra("beizhu");
            if (TextUtils.isEmpty(beizhu)){
                tv_content.setVisibility(View.GONE);
            }else{
                tv_content.setVisibility(View.VISIBLE);
                tv_content.setText(beizhu);
            }
        }
    }
}
