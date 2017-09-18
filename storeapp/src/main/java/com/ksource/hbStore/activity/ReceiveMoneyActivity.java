package com.ksource.hbStore.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksource.hbStore.R;
import com.ksource.hbStore.util.TimeUtil;

/**
 * 收款成功页面
 */
public class ReceiveMoneyActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private Button btn_ok;
    private TextView tv_amoney,tv_money,tv_state,tv_time;
    private RelativeLayout rl_order;
    private String money;
    private String orderId;
    private String time;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_receive_money;
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        rl_order.setOnClickListener(this);


    }

    @Override
    public void initData() {
        tv_title.setText("二维码收款");

        money = getIntent().getStringExtra("money");
        orderId = getIntent().getStringExtra("orderId");
        time = getIntent().getStringExtra("time");

        tv_amoney.setText("￥"+money);
        tv_money.setText("+"+money);
        tv_time.setText(TimeUtil.formatTimeSec(time));
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        rl_order = (RelativeLayout) findViewById(R.id.rl_order);
        tv_amoney = (TextView) findViewById(R.id.tv_amoney);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_time = (TextView) findViewById(R.id.tv_time);

        btn_ok = (Button) findViewById(R.id.btn_back);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
            case R.id.btn_back:
                finish();
                break;
            case R.id.rl_order:
                Intent intent = new Intent(context,ScanPayDetailActivity.class);
                intent.putExtra("id",orderId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
