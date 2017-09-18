package com.ksource.hbpostal.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.SPUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.CardResaultBean;
import com.ksource.hbpostal.bean.CardResaultBean.BankCardListBean;
import com.ksource.hbpostal.bean.CheckResultBean;
import com.ksource.hbpostal.bean.MenberResultBean;
import com.ksource.hbpostal.bean.SignCountResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.yitao.widget.RoundImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

public class ActivityMe extends BaseActivity {


    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView iv_setting;
    private TextView tv_user_name;
    private TextView tv_jifen, tv_jinbi;
    private TextView tv_dzf_msg, tv_dfh_msg, tv_dsh_msg, tv_dpj_msg;
    private TextView tv_card_num;
    private ImageView iv_level, iv_sign, iv_scan;
    private RoundImageView iv_user_avatar;
    private LinearLayout ll_jinbi, ll_jifen;
    private RelativeLayout rl_dzf, rl_dfh, rl_dsh, rl_dpj;
    private TextView tv_all_order;
    private RelativeLayout rl_card, rl_history, rl_invite, rl_manager, rl_addr,rl_addr_jj,
            rl_check_version, rl_pay_history,rl_yhq;
    private Button btn_exit;

    private String memberName;
    private String memberImg;
    private int memberGrade;
    String yjbScore;
    private String sumScore;
    private int dzfMsg, dfhMsg, dshMsg, dpjMsg;

    private LoadDialog mLoadDialog;

    List<BankCardListBean> cardList;
    private Intent intentOrder;
    private boolean isSign;
    private String id;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_me;
    }

    @Override
    public void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        iv_setting = (ImageView) findViewById(R.id.iv_setting);
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
//        iv_scan.setVisibility(View.GONE);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_jifen = (TextView) findViewById(R.id.tv_jifen);
        tv_jinbi = (TextView) findViewById(R.id.tv_jinbi);
        tv_dzf_msg = (TextView) findViewById(R.id.tv_dzf_msg);
        tv_dfh_msg = (TextView) findViewById(R.id.tv_dfh_msg);
        tv_dsh_msg = (TextView) findViewById(R.id.tv_dsh_msg);
        tv_dpj_msg = (TextView) findViewById(R.id.tv_dpj_msg);
        tv_card_num = (TextView) findViewById(R.id.tv_card_num);

        iv_user_avatar = (RoundImageView) findViewById(R.id.iv_user_avatar);
        iv_level = (ImageView) findViewById(R.id.iv_level);
        iv_sign = (ImageView) findViewById(R.id.iv_sign);

        ll_jifen = (LinearLayout) findViewById(R.id.ll_jifen);
        ll_jinbi = (LinearLayout) findViewById(R.id.ll_jinbi);

        tv_all_order = (TextView) findViewById(R.id.tv_all_order);
        rl_dpj = (RelativeLayout) findViewById(R.id.rl_dpj);
        rl_dzf = (RelativeLayout) findViewById(R.id.rl_dzf);
        rl_dfh = (RelativeLayout) findViewById(R.id.rl_dfh);
        rl_dsh = (RelativeLayout) findViewById(R.id.rl_dsh);
        iv_user_avatar.setOnClickListener(this);

        rl_card = (RelativeLayout) findViewById(R.id.rl_card);
        rl_history = (RelativeLayout) findViewById(R.id.rl_history);
        rl_invite = (RelativeLayout) findViewById(R.id.rl_invite);
        rl_manager = (RelativeLayout) findViewById(R.id.rl_manager);
        rl_addr = (RelativeLayout) findViewById(R.id.rl_addr);
        rl_addr_jj = (RelativeLayout) findViewById(R.id.rl_addr_jj);
        rl_check_version = (RelativeLayout) findViewById(R.id.rl_check_version);
        rl_pay_history = (RelativeLayout) findViewById(R.id.rl_pay_history);
        rl_yhq = (RelativeLayout) findViewById(R.id.rl_yhq);
        btn_exit = (Button) findViewById(R.id.btn_exit);

    }

    @Override
    public void initListener() {
        iv_level.setOnClickListener(this);
        iv_sign.setOnClickListener(this);
        iv_scan.setOnClickListener(this);
        ll_jinbi.setOnClickListener(this);
        ll_jifen.setOnClickListener(this);
        tv_all_order.setOnClickListener(this);
        rl_dpj.setOnClickListener(this);
        rl_dzf.setOnClickListener(this);
        rl_dfh.setOnClickListener(this);
        rl_dsh.setOnClickListener(this);
        rl_card.setOnClickListener(this);
        rl_history.setOnClickListener(this);
        rl_invite.setOnClickListener(this);
        rl_manager.setOnClickListener(this);
        rl_addr.setOnClickListener(this);
        rl_addr_jj.setOnClickListener(this);
        rl_check_version.setOnClickListener(this);
        rl_pay_history.setOnClickListener(this);
        rl_yhq.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
    }

    @Override
    public void initData() {

        token = sp.getString(ConstantValues.TOKEN, null);
        if (TextUtils.isEmpty(token)) {
            // getActivity().finish();
            startActivity(new Intent(context, LoginActivity.class));
            return;
        }
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            // SwipeRefreshLayout接管其包裹的ListView下拉事件。
            // 每一次对ListView的下拉动作，将触发SwipeRefreshLayout的onRefresh()。
            @Override
            public void onRefresh() {
                getData();
            }
        });
        // getData();
        // swipeRefreshLayout.post(new Runnable() {
        // @Override
        // public void run() {
        // getData();
        // }
        // });
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        });
    }

    private void getData() {
        // true，刷新开始，所以启动刷新的UI样式.
        //TODO 优化，三个数据从一个接口获取，减少请求
        swipeRefreshLayout.setRefreshing(true);
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        final Gson gson = new Gson();
        StringCallback memberCallback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                swipeRefreshLayout.setRefreshing(false);
                MenberResultBean memberResult = null;
                try {
                    memberResult = gson.fromJson(arg0, MenberResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (memberResult == null) {
                    ToastUtil.showTextToast(context, "获取会员信息失败！");
                } else {
                    if (memberResult.success) {
                        memberName = memberResult.memberInfo.NICK_NAME;
                        memberImg = memberResult.memberInfo.HEAD_IMAGE;
                        memberGrade = memberResult.memberInfo.MEMBER_GRADE;
                        sumScore = memberResult.memberInfo.SUM_SCORE;
                        yjbScore = memberResult.memberInfo.YJB_SCORE;
                        id = memberResult.memberInfo.ID;
                        SPUtils spUtils = new SPUtils(ConstantValues.SP_NAME);
                        spUtils.putBoolean(ConstantValues.PAY_PWD, memberResult.memberInfo.PAY_PWD == 1);
                        dzfMsg = memberResult.orderCount.L0;
                        dfhMsg = memberResult.orderCount.L1;
                        dshMsg = memberResult.orderCount.L2;
                        dpjMsg = memberResult.orderCount.L3;

                        setMemberData();
                        setOrderData();
                    } else if (memberResult.flag == 10) {
                        mApplication.login();
                        // getData();
                    } else {
                        ToastUtil.showTextToast(context, memberResult.msg);
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_MEMBER_URL, params, memberCallback);
        StringCallback signCallback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                swipeRefreshLayout.setRefreshing(false);
                SignCountResultBean signCountResult = null;
                try {
                    signCountResult = gson.fromJson(arg0,
                            SignCountResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (signCountResult == null) {
                    ToastUtil.showTextToast(context, "获取签到信息失败！");
                } else {
                    if (signCountResult.success) {
                        isSign = signCountResult.isSign == 1;
                        if (isSign) {
                            // iv_sign.setVisibility(View.INVISIBLE);
                            iv_sign.setImageResource(R.drawable.mine_icon_sign2);
                        } else {
                            // iv_sign.setVisibility(View.VISIBLE);
                            iv_sign.setImageResource(R.drawable.mine_icon_sign);
                        }

                    } else if (signCountResult.flag == 10) {
                        mApplication.login();
                    } else {
                        ToastUtil.showTextToast(context, signCountResult.msg);
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_SIGN_COUNT_URL, params, signCallback);
        StringCallback cardCallback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                swipeRefreshLayout.setRefreshing(false);
                CardResaultBean cardResult = null;
                try {
                    cardResult = gson.fromJson(arg0,
                            CardResaultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cardResult == null) {
                    ToastUtil.showTextToast(context, "获取信息失败！");
                } else {
                    if (cardResult.success) {
                        cardList = cardResult.bankCardList;
                        tv_card_num.setText(cardList.size() + "张");
                    } else if (cardResult.flag == 10) {
                        mApplication.login();
                    } else {
                        ToastUtil.showTextToast(context, cardResult.msg);
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_BANKCARD_URL, params, cardCallback);
    }

    // 设置会员信息
    private void setMemberData() {
        switch (memberGrade) {
            case 1:
                iv_level.setBackgroundResource(R.drawable.mine_pic_grade3);
                break;
            case 2:
                iv_level.setBackgroundResource(R.drawable.mine_pic_grade2);
                break;
            case 3:
                iv_level.setBackgroundResource(R.drawable.mine_pic_grade1);
                break;
            case 4:
            default:
                iv_level.setBackgroundResource(R.drawable.mine_pic_grade0);
                break;
        }
        tv_user_name.setText(memberName);
        tv_jifen.setText("" + sumScore);
        tv_jinbi.setText("" + yjbScore);
        try {
            ImageLoaderUtil.loadNetPic(ConstantValues.BASE_URL + memberImg,
                    iv_user_avatar);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    // 设置订单信息
    private void setOrderData() {
        if (dzfMsg > 0) {
            tv_dzf_msg.setText("" + dzfMsg);
            tv_dzf_msg.setVisibility(View.VISIBLE);
        } else {
            tv_dzf_msg.setVisibility(View.GONE);
        }
        if (dfhMsg > 0) {
            tv_dfh_msg.setText("" + dfhMsg);
            tv_dfh_msg.setVisibility(View.VISIBLE);
        } else {
            tv_dfh_msg.setVisibility(View.GONE);
        }
        if (dshMsg > 0) {
            tv_dsh_msg.setText("" + dshMsg);
            tv_dsh_msg.setVisibility(View.VISIBLE);
        } else {
            tv_dsh_msg.setVisibility(View.GONE);
        }
        if (dpjMsg > 0) {
            tv_dpj_msg.setText("" + dpjMsg);
            tv_dpj_msg.setVisibility(View.VISIBLE);
        } else {
            tv_dpj_msg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_user_avatar:
                // ToastUtil.showTextToast(context, "设置头像");
                // Intent intent = new Intent(context, MemberInfoActivity.class);
                // startActivity(intent);
                break;
            case R.id.iv_scan:
                //弹出个人二维码
                showQRCode();
                break;
            case R.id.iv_setting:
                Intent intent = new Intent(context, MemberInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_level:
                Intent intentLevel = new Intent(context, BaseHtmlActivity.class);
                intentLevel.putExtra("title", "会员等级");
                intentLevel.putExtra("url", ConstantValues.GRADE_HTML_URL
                        + "?token=" + token);
                startActivity(intentLevel);
                break;
            case R.id.iv_sign:
                // ToastUtil.showTextToast(context, "去签到");
                Intent intentSign = new Intent(context, Sign1Activity.class);
                intentSign.putExtra("isSign", isSign);
                startActivity(intentSign);
                break;
            case R.id.ll_jinbi:
                // ToastUtil.showTextToast(context, "去金币");
                Intent intentGold = new Intent(context, GoldActivity.class);
                startActivity(intentGold);
                break;
            case R.id.ll_jifen:
                // ToastUtil.showTextToast(context, "去积分");
                Intent intentJifen = new Intent(context, JifenActivity.class);
                startActivity(intentJifen);
                break;
            case R.id.tv_all_order:
                //全部订单
                intentOrder = new Intent(context, OrderActivity.class);
                intentOrder.putExtra("order", 0);
                startActivity(intentOrder);
                break;
            case R.id.rl_dzf:
                //待支付订单
                intentOrder = new Intent(context, OrderActivity.class);
                intentOrder.putExtra("order", 1);
                startActivity(intentOrder);
                break;
            case R.id.rl_dfh:
                //待发货订单
                intentOrder = new Intent(context, OrderActivity.class);
                intentOrder.putExtra("order", 2);
                startActivity(intentOrder);
                break;
            case R.id.rl_dsh:
                //待收货订单
                intentOrder = new Intent(context, OrderActivity.class);
                intentOrder.putExtra("order", 3);
                startActivity(intentOrder);
                break;
            case R.id.rl_dpj:
                //待评价订单
                intentOrder = new Intent(context, OrderActivity.class);
                intentOrder.putExtra("order", 4);
                startActivity(intentOrder);
                break;
            case R.id.rl_card:
                //我的银行卡
                Intent intentCard = new Intent(context, CardActivity.class);
                startActivity(intentCard);

                break;
            case R.id.rl_yhq:
                //我的优惠券
                Intent intentYhq = new Intent(context, YhqActivity.class);
//                Intent intentYhq = new Intent(context, DHYhqActivity.class);
                startActivity(intentYhq);
//                Intent intentweb = new Intent(context,BaseHtmlActivity.class);
//                intentweb.putExtra("title","都邦保险");
//                intentweb.putExtra("url",ConstantValues.DBBX_HTML_URL);
//                startActivity(intentweb);
                break;
            case R.id.rl_history:
                //缴费历史
                Intent intentHistory = new Intent(context, HistoryActivity.class);
                startActivity(intentHistory);
                break;
            case R.id.rl_invite:
                //邀请好友
                Intent intentInvite = new Intent(context, InviteActivity.class);
                startActivity(intentInvite);
                break;
            case R.id.rl_manager:
                //我的客户经理
                Intent intentManager = new Intent(context, ManagerActivity.class);
                startActivity(intentManager);
                break;
            case R.id.rl_addr:
                //收货地址
                Intent intentAddr = new Intent(context, AddrActivity.class);
                intentAddr.putExtra("type",1);
                startActivity(intentAddr);
                break;
            case R.id.rl_addr_jj:
                //寄件地址
                Intent intentJJAddr = new Intent(context, AddrActivity.class);
                intentJJAddr.putExtra("type",2);
                startActivity(intentJJAddr);
                break;
            case R.id.rl_check_version:
                //检查更新
                // Intent intentAddr = new Intent(context, AddrActivity.class);
                // startActivity(intentAddr);
                checkVersion();
                break;
            case R.id.rl_pay_history:
                //面对面付明细
                Intent intent1 = new Intent(context,PayHistoryActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_exit:
                //退出应用
                SweetAlertDialog dialog = new SweetAlertDialog(context,
                        SweetAlertDialog.WARNING_TYPE);
                dialog.setTitleText("确定要退出登录？")
                        .setContentText("退出登录后下次需要重新登录！")
                        .setCancelText("取消")
                        .setConfirmText("退出登录")
                        .showCancelButton(true)
                        .setConfirmClickListener(
                                new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        logout();
                                        sp.edit().putString(ConstantValues.TOKEN,"").apply();
                                        sDialog.dismiss();
                                    }
                                }).show();
                // startActivity(new Intent(context, LoginActivity.class));
                break;

            default:
                break;
        }

    }

    /**
     * 展示个人二维码
     */
    private void showQRCode() {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
//        adb.setTitle("我的二维码");
        View erImage = getLayoutInflater().inflate(R.layout.dialog_ercode, null);
//        TextView tv_msg = (TextView) erImage.findViewById(R.id.tv_msg);
        final ImageView iv_code = (ImageView) erImage.findViewById(R.id.iv_code);
//        tv_msg.setText("我的二维码");
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
//                Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
                //生成二维码
                return QRCodeEncoder.syncEncodeQRCode(id, BGAQRCodeUtil.dp2px(context, 150)
                        , Color.BLACK);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    iv_code.setImageBitmap(bitmap);
                } else {
                    ToastUtils.showShortToast("生成二维码失败");
                }
            }
        }.execute();
        final AlertDialog dialog = adb.setView(erImage).show();
        iv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    // 注销
    private void logout() {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
//                new SPUtils(ConstantValues.SP_NAME).clear();
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
//                new SPUtils(ConstantValues.SP_NAME).clear();
            }
        };
        DataUtil.doGetData(mLoadDialog, context, ConstantValues.LOGOUT_URL, callback);
        sp.edit().putString(ConstantValues.KEY_PASSWORD, "").apply();
        sp.edit().putString(ConstantValues.TOKEN, "").apply();
        finish();
        startActivity(new Intent(context, LoginActivity.class));
    }

    /**
     * 检查版本
     */
    private void checkVersion() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "检查更新");
        Map<String, String> params = new HashMap<String, String>();
        params.put("version_name", "" + getVersionCode(context));
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                CheckResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(arg0, CheckResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean != null) {
                    if (resultBean.flag == 1) {
                        Intent intent = new Intent(context,
                                UpDateActivity.class);
                        intent.putExtra("url", ConstantValues.BASE_URL
                                + resultBean.url);
                        intent.putExtra("describes", resultBean.describes);
                        intent.putExtra("version", resultBean.version_name);
                        intent.putExtra("isUpdate",resultBean.FORCE_UPDATE == 1);
                        startActivity(intent);
                    } else {
                        new SweetAlertDialog(context,
                                SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(resultBean.msg)
                                .setContentText(getVersionCode(context))
                                .setConfirmText("确定")
                                .show();
                    }
                } else {
                    ToastUtil.showTextToast(context, "检查版本失败！");
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "检查版本失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.CKECK_UPDATA_URL, params, callback);
    }

    /**
     * 获取apk的版本号
     *
     * @param context
     * @return
     */
    public String getVersionCode(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private long mExitTime = 0;
    private String token;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
    }
}
