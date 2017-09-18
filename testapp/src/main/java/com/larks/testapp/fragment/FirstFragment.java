package com.larks.testapp.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.larks.testapp.R;


public class FirstFragment extends BaseFragment implements OnClickListener {

    private TextView tv_title;
    private ImageView iv_back;
    private ImageView iv_code;
    private TextView tv_set_money, tv_save_code, tv_money;
    private String id, name;
    private static final int SET_MONEY_REQUEST = 100;
    private String money;
    private String beizhu;
    private String sellerId;

    @Override
    public View initView() {
        return View.inflate(context, R.layout.fragment_tab1, null);
    }

    @Override
    public void initData() {
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        iv_back = (ImageView) view.findViewById(R.id.iv_back);
        iv_code = (ImageView) view.findViewById(R.id.iv_code);
        tv_set_money = (TextView) view.findViewById(R.id.tv_set_money);
        tv_save_code = (TextView) view.findViewById(R.id.tv_save_code);
        tv_money = (TextView) view.findViewById(R.id.tv_money);
        tv_save_code.setOnClickListener(this);
        tv_set_money.setOnClickListener(this);
//        iv_back.setVisibility(View.GONE);
//        tv_title.setText("二维码收款");
//		getCode();
//        showCode(id + "#" + name + "#" + sellerId);
    }

    //显示商家二维码
//    private void showCode(final String content) {
//        new AsyncTask<Void, Void, Bitmap>() {
//            @Override
//            protected Bitmap doInBackground(Void... params) {
////                Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
//                //二维码
//                return QRCodeEncoder.syncEncodeQRCode(content, BGAQRCodeUtil.dp2px(context, 150)
//                        , Color.BLACK);
//            }
//
//            @Override
//            protected void onPostExecute(Bitmap bitmap) {
//                if (bitmap != null) {
//                    iv_code.setImageBitmap(bitmap);
//                } else {
//                    ToastUtils.showShortToast("生成二维码失败");
//                }
//            }
//        }.execute();
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_set_money:
                if (TextUtils.isEmpty(money)) {
//                    startActivityForResult(new Intent(context, SetMoneyActivity.class), SET_MONEY_REQUEST);
                } else {
                    money = "";
                    tv_money.setText("");
                    tv_set_money.setText("设置金额");
                    tv_money.setVisibility(View.GONE);
//                    showCode(id + "#" + name + "#" + sellerId);
                }
                break;
            case R.id.tv_save_code:
                //保存二维码
//                new AsyncTask<Void, Void, Bitmap>() {
//                    @Override
//                    protected Bitmap doInBackground(Void... params) {
////                Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
//                        //生成二维码
//                        return QRCodeEncoder.syncEncodeQRCode(id + "#" + name + "#" + sellerId, BGAQRCodeUtil.dp2px(context, 350)
//                                , Color.BLACK);
//                    }
//
//                    @Override
//                    protected void onPostExecute(Bitmap bitmap) {
//                        if (bitmap != null) {
//                            if (ImageUtils.save(bitmap, ConstantValues.DOWNLOADFILEPATH + "code.jpg", Bitmap.CompressFormat.JPEG)) {
//                                ToastUtils.showShortToast("二维码已保存至：" + ConstantValues.DOWNLOADFILEPATH + "code.jpg");
//                            } else {
//                                ToastUtils.showShortToast("二维码保存失败！");
//                            }
//
//                        } else {
//                            ToastUtils.showShortToast("生成二维码失败");
//                        }
//                    }
//                }.execute();

                break;
            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            money = data.getStringExtra("money");
            beizhu = data.getStringExtra("beizhu");
//            money = NumberUtil.toDecimal2(ConvertUtil.obj2Double(money));
            if (TextUtils.isEmpty(money)) {
//                showCode(id + "#" + name + "#" + sellerId);
                tv_money.setVisibility(View.GONE);
                tv_set_money.setText("设置金额");
            } else {
                tv_money.setVisibility(View.VISIBLE);
                tv_money.setText("￥" + money);
//                showCode(id + "#" + name + "#" + sellerId + "#" + money);
                tv_set_money.setText("清除金额");
                if (!TextUtils.isEmpty(beizhu)) {
//                    showCode(id + "#" + name + "#" + sellerId + "#" + money + "#" + beizhu);
                }
            }
        }
    }
}