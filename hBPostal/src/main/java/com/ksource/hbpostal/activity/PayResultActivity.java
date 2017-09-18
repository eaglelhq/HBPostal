package com.ksource.hbpostal.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.AwardResultBean;
import com.ksource.hbpostal.bean.HistoryResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ShareSDKManager;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import okhttp3.Call;

/**
 * 生活缴费支付完成页面
 */
public class PayResultActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back, iv_right;
    private TextView tv_qq, tv_space, tv_wechat, tv_firend, tv_blog;
    private TextView tv_should_pay, tv_true_pay, tv_use_jifen, tv_pay_record;
    private HashMap<String, String> shareMap;
    private String accountType;
    private LoadDialog mLoadDialog;
    private String token;
    private String id;
    private List<AwardResultBean.InfoBean> datas;
    private MyAdapter adapter;
    private String url;
    private int type;

    //	private LoadDialog mLoadDialog;
    @Override
    public int getLayoutResId() {
        return R.layout.activity_pay_result;
    }

    @Override
    public void initView() {
        ShareSDK.initSDK(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("生活缴费");
        iv_right = (ImageView) findViewById(R.id.iv_right);
        iv_right.setImageResource(R.drawable.life_icon_home);
        iv_right.setVisibility(View.VISIBLE);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_qq = (TextView) findViewById(R.id.tv_qq);
        tv_space = (TextView) findViewById(R.id.tv_space);
        tv_wechat = (TextView) findViewById(R.id.tv_wechat);
        tv_firend = (TextView) findViewById(R.id.tv_firend);
        tv_blog = (TextView) findViewById(R.id.tv_blog);
        tv_should_pay = (TextView) findViewById(R.id.tv_should_pay);
        tv_true_pay = (TextView) findViewById(R.id.tv_true_pay);
        tv_use_jifen = (TextView) findViewById(R.id.tv_use_jifen);
        tv_pay_record = (TextView) findViewById(R.id.tv_pay_record);
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        tv_qq.setOnClickListener(this);
        tv_space.setOnClickListener(this);
        tv_wechat.setOnClickListener(this);
        tv_firend.setOnClickListener(this);
        tv_blog.setOnClickListener(this);
        tv_pay_record.setOnClickListener(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        token = sp.getString(ConstantValues.TOKEN, "");
        accountType = intent.getStringExtra("accountType");
        type = Integer.parseInt(accountType);
        switch (type) {
            case 1:
                tv_title.setText("水费缴费历史");
                url = ConstantValues.GET_WATER_PAY_HISTORY_URL;
                break;
            case 2:
                tv_title.setText("电费缴费历史");
                url = ConstantValues.GET_ELE_PAY_HISTORY_URL;
                break;
            case 3:
                tv_title.setText("燃气费缴费历史");
                url = ConstantValues.GET_GAS_PAY_HISTORY_URL;
                break;
            case 4:
                tv_title.setText("有线电视缴费历史");
                url = ConstantValues.GET_TV_PAY_HISTORY_URL;
                break;
            case 5:
            case 6:
                type = 6;
                tv_title.setText("固话宽带缴费历史");
                url = ConstantValues.GET_BROAD_PAY_HISTORY_URL;
                break;
            case 10:
                tv_title.setText("手机缴费历史");
                url = ConstantValues.GET_MOBILE_PAY_HISTORY_URL;
                break;
            default:
                break;
        }
        String totleMoney = intent.getStringExtra("totleMoney");
        String totalScore = intent.getStringExtra("totalScore");
        String factMoney = intent.getStringExtra("factMoney");
        tv_should_pay.setText("应缴金额：" + totleMoney);
        tv_true_pay.setText("实缴金额：" + factMoney);
        tv_use_jifen.setText("使用积分：" + totalScore);
        shareMap = new HashMap<>();
        shareMap.put("title", "鹤壁邮支付平台缴费");
        shareMap.put("content", "快来使用鹤壁邮支付平台缴费吧！\n方便快捷，足不出户，安然生活！");
        shareMap.put("file_url", ConstantValues.APP_DOWNLOAD_URL);
        shareMap.put("share_image", ConstantValues.BASE_URL + "/mobile/images/life_icon_logo.png");
        getData();
    }


    // 获取列表信息
    private void getData() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "数据加载中...");
        Map<String, String> params = new HashMap<>();
        String token = sp.getString(ConstantValues.TOKEN, "");
        params.put("token", token);
        params.put("type", type+"");
        params.put("pageSize", "1");
        params.put("currPage", "1");
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                Gson gson = new Gson();
                HistoryResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(
                            arg0, HistoryResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean != null) {
                    if (resultBean.success) {
                        if (resultBean.paymentList != null && resultBean.paymentList.size() > 0) {
                            //TODO 订单id,获取优惠券
                            id = resultBean.paymentList.get(0).ID;
                            getYhq();
                        }
                    } else {
                        if (resultBean.flag == 10) {
                            mApplication.login();
                            // getData(currPage);
                        }
                        ToastUtil.showTextToast(context,
                                resultBean.msg);
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, url, params, callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
                mApplication.finishAllActivity();
                startActivity(new Intent(context, MainActivity.class));
                break;
            case R.id.tv_qq:// qq分享
                ShareSDKManager.shareToQQ((Activity) context,
                        shareMap.get("title"), shareMap.get("content"),
                        shareMap.get("file_url"), shareMap.get("share_image"));
//			showShare(true, QQ.NAME);
                break;
            case R.id.tv_space:// qq分享
                ShareSDKManager.shareToQQZone((Activity) context,
                        shareMap.get("title"), shareMap.get("content"),
                        shareMap.get("file_url"), shareMap.get("share_image"));
                ShareSDKManager.shareToQQ((Activity) context,
                        shareMap.get("title"), shareMap.get("content"),
                        shareMap.get("file_url"), shareMap.get("share_image"));
                break;
            case R.id.tv_blog:// 新浪分享
                ShareSDKManager.shareToSina((Activity) context,
                        shareMap.get("title"), shareMap.get("content"),
                        shareMap.get("file_url"), shareMap.get("share_image"));
                break;
            case R.id.tv_wechat:// 微信好友
                ShareSDKManager.shareToWechat((Activity) context,
                        shareMap.get("title"), shareMap.get("content"),
                        shareMap.get("file_url"), shareMap.get("share_image"));
                break;
            case R.id.tv_firend:// 朋友圈分享
                ShareSDKManager.shareToWechatMoment((Activity) context,
                        shareMap.get("title"), shareMap.get("content"),
                        shareMap.get("file_url"), shareMap.get("share_image"));
                break;
            case R.id.tv_pay_record:// 查看缴费记录
//			ToastUtil.showTextToast(context, "查看缴费记录");
                Intent intent = new Intent(context, HistoryListActivity.class);
                intent.putExtra("type", type);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 显示获得的优惠券
     */
    private void showYhq() {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_yhq, null);

        TextView btn_get = (TextView) view.findViewById(R.id.btn_get);
        ImageView iv_close = (ImageView) view.findViewById(R.id.iv_close);
        ListView lv_yhq = (ListView) view.findViewById(R.id.lv_yhq);
        lv_yhq.setAdapter(adapter);
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //领取优惠券
//                ToastUtils.showShortToast("优惠券已领取，可在我的优惠券中查看");
                dialog.dismiss();
                startActivity(new Intent(context, YhqActivity.class));
            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 获取优惠券
     */
    private void getYhq() {
//        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("orderId", id);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                AwardResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(
                            arg0, AwardResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取优惠券失败！");
                    return;
                }
                if (resultBean.success) {
                    datas = resultBean.info;
                    if (datas != null && datas.size() > 0) {
                        adapter = new MyAdapter(datas);
                        showYhq();
                    }
                } else if (resultBean.flag == 10) {
                    mApplication.login();
                } else {
                    ToastUtil.showTextToast(context, resultBean.msg);
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.AWARD_YHQ_URL, params, callback);

    }

    class MyAdapter extends DefaultBaseAdapter<AwardResultBean.InfoBean> {

        public MyAdapter(List<AwardResultBean.InfoBean> datas) {
            super(datas);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View
                        .inflate(context, R.layout.item_yhq_award, null);
                holder = new ViewHolder();

                holder.tv_quan_name = (TextView) convertView.findViewById(R.id.tv_quan_name);
                holder.tv_quan_use = (TextView) convertView.findViewById(R.id.tv_quan_use);
                holder.tv_quan_time = (TextView) convertView.findViewById(R.id.tv_quan_time);
                holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (datas.get(position).COUPON_CATE == 1) {
                holder.tv_quan_name.setText("邮政快递优惠券");
            } else {
                holder.tv_quan_name.setText(datas.get(position).SHOP_NAME + "商家优惠券");
            }
            holder.tv_quan_use.setText("满" + datas.get(position).TAKE_EFFECT_PRICE + "元即可使用");
            holder.tv_quan_time.setText("· 有效期： " + datas.get(position).VALID_DAYS + "天以内有效 ·");
            holder.tv_money.setText(datas.get(position).DENOMINATION + "");

            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv_quan_name;
        TextView tv_quan_use;
        TextView tv_quan_time;
        TextView tv_money;
    }
}
