package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BannerBean;
import com.ksource.hbpostal.bean.BannerBean.AdvertListBean;
import com.ksource.hbpostal.bean.CardResaultBean;
import com.ksource.hbpostal.bean.MsgNumResultBean;
import com.ksource.hbpostal.bean.NewActResaultBean;
import com.ksource.hbpostal.bean.NewActResaultBean.ZxhdxxListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.widgets.AdvBean;
import com.ksource.hbpostal.widgets.BaseAutoScrollUpTextView;
import com.ksource.hbpostal.widgets.ScrollUpAdvView;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.yitao.widget.RollViewPager;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 新版首页 轮播图，功能模块
 */
public class ActivityIndex1 extends BaseActivity {

    private LinearLayout ll_shjf1, ll_shjf2, ll_shjf3, ll_shjf4;
    private LinearLayout ll_shjf, ll_sjcz, ll_jtwz, ll_hdjl, ll_mmnc, ll_tbk, ll_jfsc, ll_jqqd;
    private TextView tv_search;
    private ImageView iv_has_msg;
    private RelativeLayout rl_msg;
    private List<AdvertListBean> advertList;
    private String[] imagesUrl;

    private ScrollUpAdvView mScrollUpAdvView;
    private LinearLayout ll_hot;
    private List<ZxhdxxListBean> newActList;
    private List<CardResaultBean.BankCardListBean> cardDatas;
    private ArrayList<AdvBean> mDataList = new ArrayList<>();

    // 存放轮播图的线性布局
    private LinearLayout linearLayout;
    // 存放指示点的线性布局
    private LinearLayout pointLinearLayout;
    // 存放指示点的集合
    private List<ImageView> pointList = new ArrayList<>();
    // 上一个指示点
    private int lastPosition = 0;

    private LoadDialog mLoadDialog;
    private String token;
    private int[] imageUrls;
    private HashMap<String, String> params;
    private Gson gson;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_home;
    }

    @Override
    public void initListener() {
        ll_shjf1.setOnClickListener(this);
        ll_shjf2.setOnClickListener(this);
        ll_shjf3.setOnClickListener(this);
        ll_shjf4.setOnClickListener(this);
        ll_shjf.setOnClickListener(this);
        ll_sjcz.setOnClickListener(this);
        ll_jtwz.setOnClickListener(this);
        ll_hdjl.setOnClickListener(this);
        ll_mmnc.setOnClickListener(this);
        ll_jqqd.setOnClickListener(this);
        ll_jfsc.setOnClickListener(this);
        ll_tbk.setOnClickListener(this);
        rl_msg.setOnClickListener(this);
        tv_search.setOnClickListener(this);

    }

    @Override
    public void initView() {

        mScrollUpAdvView = (ScrollUpAdvView) findViewById(R.id.adv_view);
        ll_shjf1 = (LinearLayout) findViewById(R.id.ll_shjf1);
        ll_shjf2 = (LinearLayout) findViewById(R.id.ll_shjf2);
        ll_shjf3 = (LinearLayout) findViewById(R.id.ll_shjf3);
        ll_shjf4 = (LinearLayout) findViewById(R.id.ll_shjf4);
        ll_shjf = (LinearLayout) findViewById(R.id.ll_shjf);
        ll_sjcz = (LinearLayout) findViewById(R.id.ll_sjcz);
        ll_jtwz = (LinearLayout) findViewById(R.id.ll_jtwz);
        ll_hdjl = (LinearLayout) findViewById(R.id.ll_hdjl);
        ll_mmnc = (LinearLayout) findViewById(R.id.ll_mmnc);
        ll_jfsc = (LinearLayout) findViewById(R.id.ll_jfsc);
        ll_jqqd = (LinearLayout) findViewById(R.id.ll_jqqd);
        ll_tbk = (LinearLayout) findViewById(R.id.ll_tbk);
        ll_hot = (LinearLayout) findViewById(R.id.ll_hot);
        tv_search = (TextView) findViewById(R.id.tv_search);
        iv_has_msg = (ImageView) findViewById(R.id.iv_has_msg);
        rl_msg = (RelativeLayout) findViewById(R.id.rl_msg);
        // gridview_home = (GridView) view.findViewById(R.id.gridview_home);
        linearLayout = (LinearLayout) findViewById(R.id.top_news_viewpager_ll);
        pointLinearLayout = (LinearLayout) findViewById(R.id.dots_ll);
        // mScrollView = (BottomScrollView) findViewById(R.id.id_scroll);

    }

    @Override
    public void initData() {
        token = sp.getString(ConstantValues.TOKEN, null);
        newActList = new ArrayList<>();
        cardDatas = new ArrayList<>();
        params = new HashMap<>();
        params.put("token", token);
        gson = new Gson();

        getBanner();
//		setBanner();
        getNewAct();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMsg();
    }

    // 初始化banner
    @SuppressWarnings("deprecation")
    private void setBanner() {
        lastPosition = 0;

        rollViewPager = new RollViewPager(context);
        linearLayout.addView(rollViewPager);

        imageUrls = new int[]{R.drawable.banner1};
        // 第二步：传递轮播图需要的图片url集合或者数组（在真实项目中我们需要获得图片的url集合，然后传递给轮播图）
        if (imagesUrl != null && imagesUrl.length > 0) {
            rollViewPager.setImageUrls(imagesUrl);
        } else {
            rollViewPager.setImageUrls(imageUrls);
        }

        // 第三步：添加指示点
        addPoints();

        // 第四步：给轮播图添加界面改变监听，切换指示点
        rollViewPager
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        if (imagesUrl != null && imagesUrl.length > 0) {
                            position = position % imagesUrl.length;
                        } else {
                            position = position % imageUrls.length;
                        }
                        // 切换指示点
                        pointList.get(lastPosition).setImageResource(
                                R.drawable.dot_gray);
                        pointList.get(position).setImageResource(
                                R.drawable.dot_green);
                        lastPosition = position;
                    }

                    @Override
                    public void onPageScrolled(int position,
                                               float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

        // 第五步：轮播图点击监听
        rollViewPager
                .setOnItemClickListener(new RollViewPager.OnItemClickListener() {

                    @Override
                    public void onItemClick(int position) {
                        if (advertList != null && advertList.size() > 0) {
                            String type = advertList.get(position).LINK_TYPE;
                            String url = advertList.get(position).URL;
                            switch (Integer.parseInt(type)) {
                                case 1://http链接
                                    if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                                        Intent intent = new Intent(context,
                                                BaseHtmlActivity.class);
                                        intent.putExtra("title", advertList.get(position).TITLE);
                                        intent.putExtra("url", url);
                                        startActivity(intent);
                                    }
                                    break;
                                case 2://商品详情
                                    Intent intent = new Intent(context,
                                            GoodsDetailActivity.class);
                                    intent.putExtra("goodsId", url);
                                    startActivity(intent);
                                    break;
                                case 3://生活缴费
                                    startJfAct(1);
                                    break;
                                case 4://个人中心
                                    if (MainActivity.rg_home != null) {
                                        // getActivity().getSupportFragmentManager().beginTransaction()
                                        // .replace(R.id.fl_content, new HomeFragment()).commit();
                                        MainActivity.rg_home.check(R.id.rb_me);
                                    }
                                    break;
                                case 5://签到
                                    Intent intentSign = new Intent(context, SignActivity.class);
                                    startActivity(intentSign);
                                    break;
                                case 6://订单列表
                                    Intent intentOrder = new Intent(context, OrderActivity.class);
                                    intentOrder.putExtra("order", 0);
                                    startActivity(intentOrder);
                                    break;
                                case 7://互动交流
                                    startActivity(new Intent(context, HDJLActivity.class));
                                    break;
                                case 8://八戒农场
                                    startActivity(new Intent(context, BJNCActivity1.class));
                                    break;
                                case 9://手机缴费
                                    startJfAct(6);
                                    break;
                                case 10://交通违章
                                    startJfAct(7);
                                    break;
                                default:

                                    break;
                            }

                        } else {
                            Intent intent = new Intent(context,
                                    BaseHtmlActivity.class);
                            intent.putExtra("title", "邮支付缴费");
                            intent.putExtra("url", ConstantValues.BANAER_HTML_URL);
                            startActivity(intent);
                        }
                    }
                });

        // 第六步：设置当前页面，最好不要写最大数除以2，其实写了50就足够了
        if (imagesUrl != null && imagesUrl.length > 0) {
            rollViewPager.setCurrentItem(50 - 50 % imagesUrl.length);
        } else {
            rollViewPager.setCurrentItem(50 - 50 % imageUrls.length);
        }

        // 第七步：设置完之后就可以轮播了：开启自动轮播
        if (imagesUrl.length > 1)
            rollViewPager.startRoll();
        else
            rollViewPager.stopRoll();
    }

    private void addPoints() {
        pointList.clear();
        pointLinearLayout.removeAllViews();
//		for (int x = 0; x < 3; x++) {
        if (imagesUrl != null && imagesUrl.length > 0) {
            for (int x = 0; x < imagesUrl.length; x++) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.dot_gray);
                // 导包的时候指示点的父View是什么布局就导什么布局的params,这里导的是线性布局
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                // 设置间隔
                params.leftMargin = 10;
                // 添加到线性布局;params指定布局参数，不然点就按在一起了
                pointLinearLayout.addView(imageView, params);
                // 把指示点存放到集合中
                pointList.add(imageView);
            }
        } else {
            for (int x = 0; x < imageUrls.length; x++) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.dot_gray);
                // 导报的时候指示点的父View是什么布局就导什么布局的params,这里导的是线性布局
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                // 设置间隔
                params.leftMargin = 10;
                // 添加到线性布局;params指定布局参数，不然点就按在一起了
                pointLinearLayout.addView(imageView, params);
                // 把指示点存放到集合中
                pointList.add(imageView);
            }

        }
    }


    // 获取消息数
    private void getMsg() {
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                MsgNumResultBean msgNUmBean = null;
                Gson gson = new Gson();
                try {
                    msgNUmBean = gson.fromJson(arg0,
                            MsgNumResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (msgNUmBean == null) {
                    iv_has_msg.setVisibility(View.GONE);
                    return;
                }
                if (msgNUmBean.success) {
                    if (msgNUmBean.messageNum == 0
                            && msgNUmBean.tsNum == 0
                            && msgNUmBean.zxNum == 0) {
                        iv_has_msg.setVisibility(View.GONE);
                    } else {
                        iv_has_msg.setVisibility(View.VISIBLE);
                    }
                } else {
                    iv_has_msg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                iv_has_msg.setVisibility(View.GONE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_MSG_NUM_URL, params, callback);

    }

    // 轮播图
    private void getBanner() {
        Map<String, String> params = new HashMap<>();
        params.put("advertType", "2");

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                parseBannerJson(arg0);
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                parseBannerJson(null);
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.BANNER_URL, params, callback);
    }

    // 最新活动
    private void getNewAct() {
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                Gson gson = new Gson();
                NewActResaultBean resaultBean = null;
                try {
                    resaultBean = gson.fromJson(arg0,
                            NewActResaultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resaultBean != null) {
                    // 加载失败
//                    ll_hot.setVisibility(View.GONE);
//                } else {
                    if (resaultBean.success
                            && resaultBean.zxhdxxList.size() > 0) {
//                        ll_hot.setVisibility(View.VISIBLE);
                        newActList = resaultBean.zxhdxxList;
                        for (ZxhdxxListBean bean : newActList) {
                            String type = "";
                            if (bean.TYPE == 1) {
                                type = "活动信息";
                            } else if (bean.TYPE == 2) {
                                type = "通知公告";
                            }
                            AdvBean advertisementObject = new AdvBean();
                            advertisementObject.title = type;
                            advertisementObject.info = bean.TITLE;
                            mDataList.add(advertisementObject);
                        }
                        mScrollUpAdvView.setData(mDataList);
                        mScrollUpAdvView.setTextSize(14);
                        mScrollUpAdvView
                                .setOnItemClickListener(new BaseAutoScrollUpTextView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(int position) {
                                        startActivity(new Intent(
                                                context,
                                                HDJLActivity.class));
                                    }
                                });
                        mScrollUpAdvView.stop();
                        mScrollUpAdvView.setTimer(3000);
                        mScrollUpAdvView.start();
                    }
                    //else {
//                        ll_hot.setVisibility(View.GONE);
//                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
//                ll_hot.setVisibility(View.GONE);
            }
        };
        DataUtil.doGetData(mLoadDialog,context,ConstantValues.NEW_ACTIVITE_URL, callback);
    }

    // 解析banner
    private void parseBannerJson(final String bannerResault) {
        Gson gson = new Gson();
        BannerBean bannerBean = null;
        try {
            bannerBean = gson.fromJson(bannerResault, BannerBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String bannerCache = sp.getString(ConstantValues.BANNER_URL, null);
        if (bannerBean == null) {

            if (!TextUtils.isEmpty(bannerCache)) {
                parseBannerJson(bannerCache);
            }
            return;
        }
        if (bannerBean.success) {
            sp.edit().putString(ConstantValues.BANNER_URL, bannerResault)
                    .apply();
            advertList = bannerBean.advertList;
            imagesUrl = new String[advertList.size()];
            for (int i = 0; i < advertList.size(); i++) {
                imagesUrl[i] = ConstantValues.BASE_URL
                        + advertList.get(i).IMAGE;
            }
            setBanner();
        } else {
            if (!TextUtils.isEmpty(bannerCache)) {
                parseBannerJson(bannerCache);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_shjf:
//                startJfAct(1);
                if (TextUtils.isEmpty(token)) {
                    ToastUtils.showShortToast("请先登录！");
                    startActivity(new Intent(context, LoginActivity.class));
                    return;
                }
                mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
                StringCallback cardCallback = new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                        ToastUtil.showTextToast(context, "获取银行卡失败！");
                    }

                    @Override
                    public void onResponse(String arg0, int arg1) {
                        DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                        CardResaultBean cardResult = null;
                        try {
                            cardResult = gson.fromJson(arg0,
                                    CardResaultBean.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (cardResult == null) {
                            ToastUtil.showTextToast(context, "获取银行卡失败！");
                            return;
                        }
                        if (cardResult.success) {
                            cardDatas = cardResult.bankCardList;
                            if (cardDatas != null && cardDatas.size() > 0) {
                                startActivity(new Intent(context,ScanActivity.class));
                            } else {
//                                ToastUtil.showTextToast(context, cardResult.msg);
                                SweetAlertDialog dialog = new SweetAlertDialog(context,
                                        SweetAlertDialog.WARNING_TYPE);
                                dialog.setTitleText("您现在是未认证会员").setCancelText("先不了")
                                        .setConfirmText("实名认证")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                Intent intent = new Intent(context,
                                                        AddCardStep1Activity.class);
                                                intent.putExtra("title", "实名认证");
                                                startActivity(intent);
                                            }
                                        }).show();
                            }
                        }

                    }
                };
                DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_BANKCARD_URL, params, cardCallback);
//                startActivity(new Intent(context,ScanActivity.class));
                break;
            case R.id.ll_shjf1:
                startJfAct(1);
                break;
//            case R.id.ll_shjf2:
//                startJfAct(3);
//                break;
//            case R.id.ll_shjf3:
//                startJfAct(4);
//                break;
//            case R.id.ll_shjf4:
//                startJfAct(5);
//                break;
            case R.id.ll_sjcz:
                startJfAct(6);
                break;
            case R.id.ll_jtwz:
//                ToastUtil.showTextToast(context, "该功能暂未开放！");
//                startJfAct(7);
                startActivity(new Intent(context,ActivityCate.class));
                break;
            case R.id.ll_hdjl:
                startActivity(new Intent(context, HDJLActivity.class));
                break;
            case R.id.ll_mmnc:
                // ToastUtil.showTextToast(context, "mimi农场");
                startActivity(new Intent(context, BJNCActivity1.class));
                break;
            case R.id.ll_tbk:
                // startActivity(new Intent(context, HDJLActivity.class));
                // ToastUtil.showTextToast(context, "淘宝客");
                if (TextUtils.isEmpty(token)) {
                    ToastUtils.showShortToast("请先登录！");
                    startActivity(new Intent(context, LoginActivity.class));
                    return;
                }
                mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
                StringCallback cardCallbackTBK = new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                        ToastUtil.showTextToast(context, "获取银行卡失败！");
                    }

                    @Override
                    public void onResponse(String arg0, int arg1) {
                        DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                        CardResaultBean cardResult = null;
                        try {
                            cardResult = gson.fromJson(arg0,
                                    CardResaultBean.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (cardResult == null) {
                            ToastUtil.showTextToast(context, "获取银行卡失败！");
                            return;
                        }
                        if (cardResult.success) {
                            cardDatas = cardResult.bankCardList;
                            if (cardDatas != null && cardDatas.size() > 0) {
                                Intent intent1 = new Intent(context, BaseHtmlActivity.class);
                                intent1.putExtra("title", "天天特价");
                                intent1.putExtra("url", ConstantValues.TBK_HTML_URL);

                                startActivity(intent1);
                            } else {
//                                ToastUtil.showTextToast(context, cardResult.msg);
                                SweetAlertDialog dialog = new SweetAlertDialog(context,
                                        SweetAlertDialog.WARNING_TYPE);
                                dialog.setTitleText("您现在是未认证会员").setCancelText("先不了")
                                        .setConfirmText("实名认证")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                Intent intent = new Intent(context,
                                                        AddCardStep1Activity.class);
                                                intent.putExtra("title", "实名认证");
                                                startActivity(intent);
                                            }
                                        }).show();
                            }
                        }

                    }
                };
                DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_BANKCARD_URL, params, cardCallbackTBK);

                break;
            case R.id.tv_search:
                startActivity(new Intent(context, SearchActivity.class));

                break;
            case R.id.ll_jfsc:
                //网点导航
                startActivity(new Intent(context, MapActivity.class));
//			ToastUtil.showTextToast(context, "积分商城，敬请期待！");
//                if (MainActivity.rg_home != null) {
//                    // getActivity().getSupportFragmentManager().beginTransaction()
//                    // .replace(R.id.fl_content, new HomeFragment()).commit();
//                    MainActivity.rg_home.check(R.id.rb_delivery);
//                }
//			startActivity(new Intent(context, CateGoryListActivity.class));
                break;
            case R.id.ll_jqqd:
//                startActivity(new Intent(this, ScanActivity.class));
//                ToastUtil.showTextToast(context, "更多功能，敬请期待！");
//                Intent intentweb = new Intent(context,BaseHtmlActivity.class);
//                intentweb.putExtra("title","都邦保险");
//                intentweb.putExtra("url",ConstantValues.DBBX_HTML_URL);
//                startActivity(intentweb);
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(context, LoginActivity.class));
                    return;
                }
                Intent intentYhq = new Intent(context, DHYhqActivity.class);
                startActivity(intentYhq);
                break;
            case R.id.rl_msg:
                startActivity(new Intent(context, HDJLActivity.class));
                // MyApplication instance = MyApplication.getInstance();
                // instance.login();
                break;
            default:
                break;
        }
    }

    private void startJfAct(int type) {
        if (TextUtils.isEmpty(token)) {
            startActivity(new Intent(context, LoginActivity.class));
            return;
        }
        if (type == 6) {
            startActivity(new Intent(context, SJCZActivity.class));
        } else if (type == 7) {
            startActivity(new Intent(context, JTWZActivity.class));
        } else {
            Intent intent = new Intent(context, SHJFActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
        }
    }

    // private BottomScrollView scroll;
    private long mExitTime = 0;
    private RollViewPager rollViewPager;

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

}
