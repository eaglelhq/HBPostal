package com.ksource.hbpostal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.MyApplication;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.CountResultBean;
import com.ksource.hbpostal.bean.EvaluateResultBean;
import com.ksource.hbpostal.bean.EvaluateResultBean.GoodsRatedListBean;
import com.ksource.hbpostal.bean.GoodsResultBean;
import com.ksource.hbpostal.bean.GoodsResultBean.GoodsInfoBean;
import com.ksource.hbpostal.bean.GoodsResultBean.GoodsInfoBean.GoodsImgListBean;
import com.ksource.hbpostal.bean.RecordResultBean;
import com.ksource.hbpostal.bean.RecordResultBean.GoodsByRecordListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.interfaces.ClickBackJsInterface;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.util.TimeUtil;
import com.ksource.hbpostal.widgets.MyListView;
import com.ksource.hbpostal.widgets.RollViewPager;
import com.ksource.hbpostal.dialog.SelfDialog;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.NetStateUtils;
import com.yitao.util.ToastUtil;
import com.yitao.widget.BottomScrollView;
import com.yitao.widget.BottomScrollView.OnScrollListener;
import com.yitao.widget.RoundImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 商品详情页面
 */
public class GoodsDetailActivity extends FragmentActivity implements
        OnClickListener, OnScrollListener {

    private int size;
    private TextView tv_title;
    private ImageView iv_back;
    private Context context;
    // private Banner banner;
    private MyApplication mApplication;
    private LinearLayout ll_top_tabs, ll_top;

    private SharedPreferences sp;

    private TextView tv_goods_name, tv_goods_price, tv_goods_jifen,
            tv_jifen_name, tv_add, tv_market_price, tv_kc, tv_duihuan;
    private TextView tv_cart;
    private TextView tv_cart_count;
    private TextView btn_add_cart, btn_clearing;

    // 加减数量
    private Button btn_sub, btn_add;
    private EditText et_number;
    private int num = 1;
    private int count = 1;

    private SelfDialog selfDialog;
    private LoadDialog mLoadDialog;

    private GoodsInfoBean goodsInfoBean = new GoodsInfoBean();
    private String[] imagesUrl = {""};
    private String goodsId = "";

    private TextView tv_goods, tv_accept, tv_logistics;
    private TextView tv_top_goods, tv_top_accept, tv_top_logistics;
    private WebView mWebView;

    private MyListView lv_rated;
    private MyListView lv_record;
    private RelativeLayout rl_null;
    private BottomScrollView bsv;

    // 商品评价
    private BaseAdapter ratedAdapter;
    private List<GoodsRatedListBean> ratedDatas;
    private int currPageRated = 1;
    // 购买记录
    private BaseAdapter recordAdapter;
    private List<GoodsByRecordListBean> recordDatas;
    private int currPageRecord = 1;

    private String token;
    private boolean isNcp;
    private boolean isTiyan;

    private boolean isHasMoreRated, isHasMoreRecord;
    private int currTab;

    private WebSettings settings;
    private String url;

    // 颜色
    int blue;
    int black;

    private EvaluateResultBean resultBean;
    private RecordResultBean recordResultBean;

    // 存放轮播图的线性布局
    private LinearLayout linearLayout;
    // 存放指示点的线性布局
    private LinearLayout pointLinearLayout;
    // 存放指示点的集合
    private List<ImageView> pointList = new ArrayList<>();
    // 上一个指示点
    private int lastPosition = 0;
    private RollViewPager rollViewPager;

    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        context = this;
        mApplication = MyApplication.getInstance();
        mApplication.addActivity(this);
        // 透明状态栏
        // getWindow()
        // .addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // // 透明导航栏
        // getWindow().addFlags(
        // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        sp = getSharedPreferences(ConstantValues.SP_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_goods_detail);
        initView();
        initListener();
        initData();
        // mHandler.sendEmptyMessageDelayed(1, 10);
    }

    private void initView() {
        // ptlm = (PullUpToLoadMore) findViewById(R.id.ptlm);
        // 轮播图
        linearLayout = (LinearLayout) findViewById(R.id.top_news_viewpager_ll);
        pointLinearLayout = (LinearLayout) findViewById(R.id.dots_ll);

        bsv = (BottomScrollView) findViewById(R.id.bsv);
        ll_top_tabs = (LinearLayout) findViewById(R.id.ll_top_tabs);
        ll_top = (LinearLayout) findViewById(R.id.ll_top);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("商品详情");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        // banner = (Banner) findViewById(R.id.banner);
        // banner.setDelayTime(3000);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_sub = (Button) findViewById(R.id.btn_sub);
        et_number = (EditText) findViewById(R.id.et_number);
        // slv_user = (UiScrollView) findViewById(R.id.slv_user);
        // slv_detail = (MyScrollView) findViewById(R.id.slv_detail);
        mWebView = (WebView) findViewById(R.id.webView);
        lv_rated = (MyListView) findViewById(R.id.lv_rated);
        lv_record = (MyListView) findViewById(R.id.lv_record);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);

        tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);
        tv_goods_price = (TextView) findViewById(R.id.tv_goods_price);
        tv_goods_jifen = (TextView) findViewById(R.id.tv_goods_jifen);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_jifen_name = (TextView) findViewById(R.id.tv_jifen_name);
        tv_market_price = (TextView) findViewById(R.id.tv_market_price);
        tv_market_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tv_kc = (TextView) findViewById(R.id.tv_kc);
        tv_duihuan = (TextView) findViewById(R.id.tv_duihuan);
        tv_cart = (TextView) findViewById(R.id.tv_cart);
        tv_cart_count = (TextView) findViewById(R.id.tv_cart_count);
        btn_add_cart = (TextView) findViewById(R.id.btn_add_cart);
        btn_clearing = (TextView) findViewById(R.id.btn_clearing);
        et_number.setEnabled(false);
        // 标签:
        tv_goods = (TextView) findViewById(R.id.tv_goods);
        tv_accept = (TextView) findViewById(R.id.tv_accept);
        tv_logistics = (TextView) findViewById(R.id.tv_logistics);
        tv_top_goods = (TextView) findViewById(R.id.tv_top_goods);
        tv_top_accept = (TextView) findViewById(R.id.tv_top_accept);
        tv_top_logistics = (TextView) findViewById(R.id.tv_top_logistics);

    }

    private void initListener() {
        iv_back.setOnClickListener(this);
        tv_cart.setOnClickListener(this);
        // btn_go_top.setOnClickListener(this);

        btn_add.setOnClickListener(this);
        btn_sub.setOnClickListener(this);
        btn_add_cart.setOnClickListener(this);
        btn_clearing.setOnClickListener(this);
        // 给标签添加点击事件
        tv_goods.setOnClickListener(this);
        tv_accept.setOnClickListener(this);
        tv_logistics.setOnClickListener(this);
        tv_top_goods.setOnClickListener(this);
        tv_top_accept.setOnClickListener(this);
        tv_top_logistics.setOnClickListener(this);

        bsv.setOnScrollListener(this);
        // 当布局的状态或者控件的可见性发生改变回调的接口
        findViewById(R.id.parent_layout).getViewTreeObserver()
                .addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // 这一步很重要，使得上面的布局和下面的布局重合
                        size = ll_top.getHeight() ;//+ ll_top_tabs.getHeight()
                        onScroll(bsv.getScrollY());

                        System.out.println(bsv.getScrollY());
                    }
                });
    }

    private void initData() {
        token = sp.getString(ConstantValues.TOKEN, "");
        ratedDatas = new ArrayList<>();
        recordDatas = new ArrayList<>();
        // setData();
        et_number.setText("" + num);
        // slv_user.fullScroll(ScrollView.FOCUS_UP);
        hasNewRatedDatas = true;
        hasNewRecordDatas = true;
        getData();
        if (!TextUtils.isEmpty(token)) {
            getCount();
        }
    }

    // 初始化banner
    @SuppressWarnings("deprecation")
    private void setBanner() {
        lastPosition = 0;

        rollViewPager = new RollViewPager(context);
        linearLayout.addView(rollViewPager);

        // int[] imageUrls =
        // {R.drawable.banner1,R.drawable.banner2,R.drawable.banner3};
        // 第二步：传递轮播图需要的图片url集合或者数组（在真实项目中我们需要获得图片的url集合，然后传递给轮播图）
        rollViewPager.setImageUrls(imagesUrl);
        // rollViewPager.setImageUrls(imageUrls);

        // 第三步：添加指示点
        addPoints();

        // 第四步：给轮播图添加界面改变监听，切换指示点
        rollViewPager
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        position = position % imagesUrl.length;
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
                        // ToastUtil.showTextToast(context, "浏览大图");
                        if (imagesUrl != null && imagesUrl.length > 0)
                            imageBrower(position, imagesUrl);
                    }
                });

        // 第六步：设置当前页面，最好不要写最大数除以2，其实写了50就足够了
        rollViewPager.setCurrentItem(50 - 50 % imagesUrl.length);

        // 第七步：设置完之后就可以轮播了：开启自动轮播
//		rollViewPager.startRoll();
        rollViewPager.stopRoll();
    }

    // 添加轮播指示点
    private void addPoints() {
        pointList.clear();
        pointLinearLayout.removeAllViews();
        // for (int x = 0; x < 3; x++) {
        for (int x = 0; x < imagesUrl.length; x++) {
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

    // 浏览图片
    private void imageBrower(int position, String[] urls) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        // 图片url
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        context.startActivity(intent);
    }

    //加载商品详情webview
    private void loadWebView() {
        url = ConstantValues.GoodsDetail_HTML_URL + goodsId;
        settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true); // 支持javascript
        // settings.setUseWideViewPort(true); // 设置webview推荐使用的窗口，使html界面自适应屏幕
        // settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        // settings.setBuiltInZoomControls(false); // 不支持缩放
        // settings.setAllowFileAccess(true); // 设置可以访问文件
        // settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM); //
        // 设置中等像素密度，medium=160dpi
        // settings.setSupportZoom(true); //设置支持缩放
        settings.setLoadsImagesAutomatically(true); // 设置自动加载图片
        // settings.setBlockNetworkImage(true); //设置网页在加载的时候暂时不加载图片
        // settings.setAppCachePath(""); //设置缓存路径
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 设置缓存模式
        // 设置默认缩放级别
        // mWebView.setInitialScale(25);
        // mWebView.requestFocus();

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

        });
        mWebView.setWebViewClient(new WebViewClient() {
            // 新开页面时用自己定义的webview来显示，不用系统自带的浏览器来显示
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            // 加载错误时要做的工作
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                mWebView.loadUrl("file:///android_asset/error.html");
                mWebView.addJavascriptInterface(new ClickBackJsInterface() {
                    @JavascriptInterface
                    public void clickOnAndroid(String type) {
                        mWebView.post(new Runnable() {

                            @Override
                            public void run() {
                                mWebView.loadUrl(url);
                            }
                        });
                    }

                    @JavascriptInterface
                    public void jf(String type) {
                    }
                }, "error");

                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        mWebView.addJavascriptInterface(new ClickBackJsInterface() {

            @JavascriptInterface
            public void clickOnAndroid(String type) {
                finish();
            }

            @JavascriptInterface
            public void jf(String type) {
                // openActivityForResult(CaptureActivity.class, null, 0);
            }

        }, "demo");
        mWebView.loadUrl(url);
        mWebView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void setData() {

        // 商品图片
        setBanner();
        isTiyan = goodsInfoBean.NCP_FL == 1;
        // isTiyan = getIntent().getBooleanExtra("tiyan",false);
        count = goodsInfoBean.NUMBER;
        if (!(count > 0)) {
            btn_add_cart.setEnabled(false);
            btn_clearing.setEnabled(false);
        }
        // banner.setOnClickListener(this);
        // banner.setImagesUrl(imagesUrl);
        // 商品信息
        tv_goods_name.setText("" + goodsInfoBean.NAME);
        int type = goodsInfoBean.BUY_TYPE;
        isNcp = goodsInfoBean.IS_NCP == 1 ? true : false;

        switch (type) {
            case 1:
                tv_goods_price.setText("￥" + goodsInfoBean.PRICE);
                tv_add.setVisibility(View.GONE);
                tv_jifen_name.setVisibility(View.GONE);
                break;
            case 2:
                tv_goods_price.setText("￥" + goodsInfoBean.PRICE);
                tv_add.setVisibility(View.VISIBLE);
                tv_goods_jifen.setText(goodsInfoBean.INTEGRAL + "");
                tv_jifen_name.setVisibility(View.VISIBLE);
                break;
            case 3:
                tv_add.setVisibility(View.GONE);
                tv_goods_jifen.setText(goodsInfoBean.INTEGRAL + "");
                tv_jifen_name.setVisibility(View.VISIBLE);

                break;

            default:
                break;
        }
        tv_market_price.setText("￥" + goodsInfoBean.MARKET_PRICE);
        tv_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tv_kc.setText(goodsInfoBean.NUMBER + "件");
        tv_duihuan.setText("" + goodsInfoBean.BUY_NUM);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_cart:
                finish();
                // mApplication.finishActivity("MainActivity");
                // mApplication.finishActivity("CateGoryListActivity");
                if (mApplication.isActivityExists("MainActivity"))
                    mApplication.finishActivity("MainActivity");
                Intent intent1 = new Intent(context, MainActivity.class);
                intent1.putExtra("item", 1);
                startActivity(intent1);
                break;
            case R.id.btn_add_cart:
                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(context, LoginActivity.class));
                    return;
                }
                if (isTiyan && isNcp) {
                    selfDialog = new SelfDialog(context);
                    selfDialog.setTitle("温馨提示");
                    selfDialog.setMessage("购买该商品需要邀请码！");
                    // selfDialog.showEdittext(true);
                    selfDialog.setEtHint("请输入邀请码");
                    selfDialog.setYesOnclickListener("确定",
                            new SelfDialog.onYesOnclickListener() {
                                @Override
                                public void onYesClick() {
                                    String content = selfDialog
                                            .getEdittextContent();
                                    if (TextUtils.isEmpty(content)) {
                                        ToastUtil.showTextToast(context, "请输入邀请码！");
                                    } else {
                                        checkCode(content, 1);
                                        // selfDialog.dismiss();
                                    }
                                }
                            });
                    selfDialog.setNoOnclickListener("取消",
                            new SelfDialog.onNoOnclickListener() {
                                @Override
                                public void onNoClick() {
                                    selfDialog.dismiss();
                                }
                            });
                    selfDialog.show();
                } else {
                    addCart();
                }
                break;
            case R.id.btn_clearing:

                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(context, LoginActivity.class));
                    return;
                }

                if (isTiyan && isNcp) {
                    selfDialog = new SelfDialog(context);
                    selfDialog.setTitle("温馨提示");
                    selfDialog.setMessage("购买该商品需要邀请码！");
                    // selfDialog.showEdittext(true);
                    selfDialog.setEtHint("请输入邀请码");
                    selfDialog.setYesOnclickListener("确定",
                            new SelfDialog.onYesOnclickListener() {
                                @Override
                                public void onYesClick() {
                                    String content = selfDialog
                                            .getEdittextContent();
                                    if (TextUtils.isEmpty(content)) {
                                        ToastUtil.showTextToast(context, "请输入邀请码！");
                                    } else {
                                        checkCode(content, 2);
                                        // selfDialog.dismiss();
                                    }
                                }
                            });
                    selfDialog.setNoOnclickListener("取消",
                            new SelfDialog.onNoOnclickListener() {
                                @Override
                                public void onNoClick() {
                                    selfDialog.dismiss();
                                }
                            });
                    selfDialog.show();
                } else {
                    Intent intent = new Intent(context, ConfirmOrder.class);
                    intent.putExtra("goodsBean", goodsInfoBean);
                    intent.putExtra("number", num);
                    startActivity(intent);
                }

                break;

            case R.id.btn_add:
                if (isNcp) {
                    ToastUtil.showTextToast(context, "该商品每人限购一件!");
                    num = 1;
                } else {
                    if (num >= count) {
                        // et_number.setText("" + count);
                        num = count;
                        // et_number.setEnabled(false);
                    } else {
                        // et_number.setEnabled(true);
                        num++;
                    }
                }
                et_number.setText("" + num);
                break;
            case R.id.btn_sub:
                if (num <= 1) {
                    // et_number.setText("" + 1);
                    num = 1;
                    // et_number.setEnabled(false);
                } else {
                    // et_number.setEnabled(true);
                    num--;
                    et_number.setText("" + num);
                }
                break;
            case R.id.tv_goods:
            case R.id.tv_top_goods:
                currTab = 0;
                // 颜色
                blue = getResources().getColor(R.color.green);
                black = getResources().getColor(R.color.gary);
                tv_goods.setTextColor(blue);
                tv_accept.setTextColor(black);
                tv_logistics.setTextColor(black);
                tv_logistics.setBackground(null);
                tv_accept.setBackground(null);
                tv_goods.setBackgroundResource(R.drawable.base_tabpager_indicator_selected);
                mWebView.setVisibility(View.VISIBLE);
                lv_rated.setVisibility(View.GONE);
                lv_record.setVisibility(View.GONE);
                rl_null.setVisibility(View.GONE);
                break;
            case R.id.tv_accept:
            case R.id.tv_top_accept:
                currTab = 1;
                rl_null.setVisibility(View.GONE);
                showRatedData();
                blue = getResources().getColor(R.color.green);
                black = getResources().getColor(R.color.gary);
                tv_goods.setTextColor(black);
                tv_accept.setTextColor(blue);
                tv_logistics.setTextColor(black);
                tv_goods.setBackground(null);
                tv_logistics.setBackground(null);
                tv_accept
                        .setBackgroundResource(R.drawable.base_tabpager_indicator_selected);
                mWebView.setVisibility(View.GONE);
                lv_rated.setVisibility(View.VISIBLE);
                lv_record.setVisibility(View.GONE);
                break;
            case R.id.tv_logistics:
            case R.id.tv_top_logistics:
                currTab = 2;
                rl_null.setVisibility(View.GONE);
                showRecordData();
                blue = getResources().getColor(R.color.green);
                black = getResources().getColor(R.color.gary);
                tv_goods.setTextColor(black);
                tv_accept.setTextColor(black);
                tv_logistics.setTextColor(blue);
                tv_goods.setBackground(null);
                tv_accept.setBackground(null);
                tv_logistics
                        .setBackgroundResource(R.drawable.base_tabpager_indicator_selected);
                mWebView.setVisibility(View.GONE);
                lv_rated.setVisibility(View.GONE);
                lv_record.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    // 获取购物车数量
    private void getCount() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                Gson gson = new Gson();
                CountResultBean cartCountResult = null;
                try {
                    cartCountResult = gson.fromJson(
                            arg0, CountResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cartCountResult == null) {
                    ToastUtil.showTextToast(context, "获取购物车数量失败！");
                    tv_cart_count.setVisibility(View.GONE);
                    return;
                }
                if (cartCountResult.success) {
                    if (cartCountResult.cartCount > 0) {
                        tv_cart_count.setVisibility(View.VISIBLE);
                        tv_cart_count.setText("" + cartCountResult.cartCount);
                    } else {
                        tv_cart_count.setVisibility(View.GONE);
                    }
                } else if (cartCountResult.flag == 10) {
                    mApplication.login();
                } else {
                    ToastUtil.showTextToast(context,
                            cartCountResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                tv_cart_count.setVisibility(View.GONE);
                ToastUtil.showTextToast(context, "获取购物车数量失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context, ConstantValues.CART_COUNT_URL, params, callback);
    }

    // 获取商品信息
    private void getData() {
        goodsId = getIntent().getStringExtra("goodsId");
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "数据加载中...");
        Map<String, String> params = new HashMap<String, String>();
        params.put("goodsId", goodsId);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                loadWebView();
                getRatedData();
                getRecordData();
                Gson gson = new Gson();
                GoodsResultBean goodsResult = null;
                try {
                    goodsResult = gson.fromJson(arg0,
                            GoodsResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (goodsResult == null) {
                    ToastUtil.showTextToast(context, "获取商品信息失败！");
                    return;
                }
                if (goodsResult.success) {
                    if (goodsResult.goodsInfo == null) {
                        ToastUtil.showTextToast(context, "获取商品信息失败！");
                        return;
                    }
                    goodsInfoBean = goodsResult.goodsInfo;
                    if (goodsResult.goodsInfo.goodsImgList != null
                            && goodsResult.goodsInfo.goodsImgList
                            .size() > 0) {
                        List<GoodsImgListBean> advertList = goodsInfoBean.goodsImgList;
                        imagesUrl = new String[advertList.size()];
                        for (int i = 0; i < advertList.size(); i++) {
                            imagesUrl[i] = ConstantValues.BASE_URL
                                    + advertList.get(i).FILE_PATH;
                        }
                    }
                    btn_add_cart.setEnabled(true);
                    btn_clearing.setEnabled(true);
                    setData();
                } else {
                    ToastUtil.showTextToast(context, goodsResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

            }
        };
        DataUtil.doPostAESData(mLoadDialog,context, ConstantValues.GOODS_DETAIL_URL, params, callback);
    }

    // 校验邀请码
    private void checkCode(final String content, final int type) {

        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "验证邀请码...");
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("code", content);
        params.put("user_id", sp.getString(ConstantValues.USER_ID, ""));
        // params.put("user_id", "skdjfhs");
        params.put("goods_id", goodsId);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                BaseResultBean baseResult = null;
                Gson gson = new Gson();
                try {
                    baseResult = gson.fromJson(arg0,
                            BaseResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (baseResult == null) {
                    ToastUtil.showTextToast(context, "校验邀请码失败！");
                    return;
                }
                // ToastUtil.showTextToast(context, baseResult.msg);
                if (baseResult.success) {
                    selfDialog.dismiss();
                    if (type == 1) {
                        addCart();
                    } else if (type == 2) {
                        Intent intent = new Intent(context,
                                ConfirmOrder.class);
                        intent.putExtra("goodsBean", goodsInfoBean);
                        intent.putExtra("number", num);
                        startActivity(intent);
                    }
                } else if (baseResult.flag == 10) {
                    mApplication.login();
                    // addCart(params);
                } else {
                    ToastUtil.showTextToast(context, baseResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "校验邀请码失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context, ConstantValues.CHECKCODE, params, callback);
    }

    // 加入购物车
    private void addCart() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "加入购物车...");
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("goods_id", goodsId);
        params.put("number", num + "");
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                BaseResultBean baseResult = null;
                Gson gson = new Gson();
                try {
                    baseResult = gson.fromJson(arg0, BaseResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (baseResult == null) {
                    ToastUtil.showTextToast(context, "加入购物车失败！");
                    return;
                }
                ToastUtil.showTextToast(context, baseResult.msg);
                if (baseResult.success) {
                    ToastUtil.showTextToast(context, "宝贝已添加到购物车，去看看吧！");
                    getCount();
                } else if (baseResult.flag == 10) {
                    mApplication.login();
                    // addCart(params);
                } else {
                    ToastUtil.showTextToast(context, baseResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "加入购物车失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context, ConstantValues.ADD_CART_URL, params, callback);
    }

    // 获取列表信息
    private void getRatedData() {
        if (!NetStateUtils.isNetworkAvailable(context)) {
            ToastUtil.showTextToast(context, "当前网络不可用！");
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("goodsId", goodsId);
        params.put("pageSize", "10");
        params.put("currPage", "" + currPageRated);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                resultBean = null;
                Gson gson = new Gson();
                try {
                    resultBean = gson.fromJson(arg0,
                            EvaluateResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (currTab == 1) {
                    showRatedData();
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                ToastUtil.showTextToast(context, "获取数据失败");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context, ConstantValues.GOODS_RATED_URL, params, callback);
    }

    // 商品评价
    private void showRatedData() {
        hasNewRatedDatas = !(lastRatedCurrPage == currPageRated);
        lv_rated.removeFooterView(viewRated);
        if (resultBean == null) {
            rl_null.setVisibility(View.VISIBLE);
            return;
        }
        if (resultBean.success) {
            // if (resultBean.goodsRatedList != null&&
            // resultBean.goodsRatedList.size() < 10) {
            // ratedDatas.clear();
            // }
            if (hasNewRatedDatas) {
                ratedDatas.addAll(resultBean.goodsRatedList);
            }
            lastRatedCurrPage = currPageRated;
            if (ratedDatas.size() == 0) {
                rl_null.setVisibility(View.VISIBLE);
                isHasMoreRated = false;
                return;
            } else {
                if (ratedAdapter == null) {
                    ratedAdapter = new MyRatedAdapter(ratedDatas);
                    lv_rated.setAdapter(ratedAdapter);
                } else {
                    ratedAdapter.notifyDataSetChanged();
                }
                // lv_rated.setVisibility(View.VISIBLE);
                isHasMoreRated = resultBean.goodsRatedList.size() < 10 ? false
                        : true;
                if (ratedDatas.size() == 0) {
                    rl_null.setVisibility(View.VISIBLE);
                    isHasMoreRecord = false;
                    return;
                }
                if (isHasMoreRated) {
                    viewRated = new TextView(context);
                    viewRated.setText("点击加载更多");
                    viewRated.setPadding(0, 20, 0, 20);
                    viewRated.setGravity(Gravity.CENTER);
                    lv_rated.addFooterView(viewRated);
                    viewRated.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            currPageRated++;
                            hasNewRatedDatas = true;
                            getRatedData();
                        }
                    });
                }
            }
        } else {
            if (resultBean.flag == 10) {
                mApplication.login();
            }
            isHasMoreRated = false;
            ToastUtil.showTextToast(context, resultBean.msg);
        }
    }

    class MyRatedAdapter extends DefaultBaseAdapter<GoodsRatedListBean> {

        public MyRatedAdapter(List<GoodsRatedListBean> datas) {
            super(datas);
        }

        @SuppressWarnings("finally")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RatedViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(context,
                        R.layout.item_evalute_listview, null);
                holder = new RatedViewHolder();
                holder.iv_user_avatar = (RoundImageView) convertView
                        .findViewById(R.id.iv_user_avatar);
                holder.tv_user_name = (TextView) convertView
                        .findViewById(R.id.tv_user_name);
                holder.rb_eva = (RatingBar) convertView
                        .findViewById(R.id.rb_eva);
                holder.tv_content = (TextView) convertView
                        .findViewById(R.id.tv_content);
                holder.tv_record_time = (TextView) convertView
                        .findViewById(R.id.tv_record_time);

                convertView.setTag(holder);
            } else {
                holder = (RatedViewHolder) convertView.getTag();
            }

            // 填充数据
            String nickName = datas.get(position).NICK_NAME;

            if (!TextUtils.isEmpty(nickName)) {
                if (nickName.length() > 2) {
                    nickName = nickName.substring(0, 1) + "**"
                            + nickName.substring(nickName.length() - 1);
                } else {
                    nickName = nickName.substring(0, 1) + "**";
                }
            } else {
                nickName = "匿名";
            }
            holder.tv_user_name.setText(nickName);
            holder.rb_eva.setRating(datas.get(position).APPRAISE_LEVEL);
            holder.rb_eva.setEnabled(false);
            holder.tv_content.setText(datas.get(position).APPRAISE_CONTENT);
            String creatTime = TimeUtil
                    .formatTimeMin(datas.get(position).APPRAISE_TIME);
            holder.tv_record_time.setText(creatTime);

            try {
                ImageLoaderUtil.loadNetPic(
                        ConstantValues.BASE_URL
                                + datas.get(position).HEAD_IMAGE,
                        holder.iv_user_avatar);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                holder.iv_user_avatar
                        .setImageResource(R.drawable.head_fail_img);

            } finally {

                return convertView;
            }
        }

    }

    class RatedViewHolder {
        private RoundImageView iv_user_avatar;
        private TextView tv_user_name;
        private RatingBar rb_eva;
        private TextView tv_content;
        private TextView tv_record_time;
    }

    private TextView viewRated, viewRecord;
    private boolean hasNewRecordDatas, hasNewRatedDatas;
    private int lastRatedCurrPage, lastRecordCurrPage;

    // 获取列表信息
    private void getRecordData() {

        if (!NetStateUtils.isNetworkAvailable(context)) {
            ToastUtil.showTextToast(context, "当前网络不可用！");
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("goodsId", goodsId);
        params.put("pageSize", "10");
        params.put("currPage", "" + currPageRecord);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                Gson gson = new Gson();
                try {
                    recordResultBean = gson.fromJson(
                            arg0, RecordResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (currTab == 2) {
                    showRecordData();
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                ToastUtil.showTextToast(context, "获取数据失败!");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context, ConstantValues.GOODS_RECOED_URL, params, callback);
    }

    // 购买记录
    private void showRecordData() {
        hasNewRecordDatas = !(lastRecordCurrPage == currPageRecord);
        lv_record.removeFooterView(viewRecord);
        if (recordResultBean == null) {
            ToastUtil.showTextToast(context, "获取数据失败！");
            return;
        }
        if (recordResultBean.success) {
            if (hasNewRecordDatas) {
                recordDatas.addAll(recordResultBean.goodsByRecordList);
            }
            lastRecordCurrPage = currPageRecord;
            if (recordAdapter == null) {
                recordAdapter = new MyAdapter(recordDatas);
                lv_record.setAdapter(recordAdapter);
            } else {
                recordAdapter.notifyDataSetChanged();
            }
            // lv_record.setVisibility(View.VISIBLE);
            isHasMoreRecord = recordResultBean.goodsByRecordList.size() < 10 ? false
                    : true;
            if (recordDatas.size() == 0) {
                rl_null.setVisibility(View.VISIBLE);
                isHasMoreRecord = false;
                return;
            }
            if (isHasMoreRecord) {
                viewRecord = new TextView(context);
                viewRecord.setText("点击加载更多");
                viewRecord.setPadding(0, 20, 0, 20);
                viewRecord.setGravity(Gravity.CENTER);
                lv_record.addFooterView(viewRecord);
                viewRecord.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        currPageRecord++;
                        hasNewRecordDatas = true;
                        getRecordData();
                    }
                });
            }
        } else if (recordResultBean.flag == 10) {
            mApplication.login();
        } else {
            isHasMoreRecord = false;
            ToastUtil.showTextToast(context, recordResultBean.msg);
        }
    }

    class MyAdapter extends DefaultBaseAdapter<GoodsByRecordListBean> {

        public MyAdapter(List<GoodsByRecordListBean> datas) {
            super(datas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(context,
                        R.layout.item_record_listview, null);
                holder = new ViewHolder();
                holder.iv_user_avatar = (RoundImageView) convertView
                        .findViewById(R.id.iv_user_avatar);
                holder.tv_user_name = (TextView) convertView
                        .findViewById(R.id.tv_user_name);
                holder.tv_price = (TextView) convertView
                        .findViewById(R.id.tv_price);
                holder.tv_record_time = (TextView) convertView
                        .findViewById(R.id.tv_record_time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 填充数据
            String headImage = datas.get(position).HEAD_IMAGE;
            String nickName = datas.get(position).NICK_NAME;
            if (!TextUtils.isEmpty(nickName)) {
                if (nickName.length() > 2) {
                    nickName = nickName.substring(0, 1) + "**"
                            + nickName.substring(nickName.length() - 1);
                } else {
                    nickName = nickName.substring(0, 1) + "**";
                }
            } else {
                nickName = "匿名";
            }
            holder.tv_user_name.setText(nickName);
            String price = datas.get(position).TOTAL_MONEY;
            String jifen = datas.get(position).TOTAL_SCORE;
            String feiyong = getFei(price, jifen);
            holder.tv_price.setText(feiyong);
            String creatTime = TimeUtil
                    .formatTimeMin(datas.get(position).CREATE_TIME);
            holder.tv_record_time.setText(creatTime);
            if (headImage == null || TextUtils.isEmpty(headImage)) {
                holder.iv_user_avatar
                        .setImageResource(R.drawable.head_fail_img);
            } else {
                try {
                    ImageLoaderUtil.loadNetPic(
                            ConstantValues.BASE_URL
                                    + datas.get(position).HEAD_IMAGE,
                            holder.iv_user_avatar);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    holder.iv_user_avatar
                            .setImageResource(R.drawable.head_fail_img);
                }
            }
            return convertView;
        }

    }

    private String getFei(String price, String jifen) {
        if (TextUtils.isEmpty(price) || "0".equals(price)) {
            return jifen + "积分";
        } else if (TextUtils.isEmpty(jifen) || "0".equals(jifen)) {
            return "￥" + price;
        } else {
            return "￥" + price + "+" + jifen + "积分";
        }
    }

    class ViewHolder {
        private RoundImageView iv_user_avatar;
        private TextView tv_user_name;
        private TextView tv_price;
        private TextView tv_record_time;
    }

    @Override
    public void onScroll(int scrollY) {
        // int mBuyLayout2ParentTop = Math.max(scrollY, ll_top_tabs.getTop());
        // ll_top.layout(0, mBuyLayout2ParentTop, ll_top_tabs.getWidth(),
        // mBuyLayout2ParentTop + ll_top_tabs.getHeight());
        if (scrollY >= size) {
            ll_top_tabs.setVisibility(View.VISIBLE);
            blue = getResources().getColor(R.color.green);
            black = getResources().getColor(R.color.gary);
            switch (currTab) {
                case 0:
                    tv_top_goods.setTextColor(blue);
                    tv_top_accept.setTextColor(black);
                    tv_top_logistics.setTextColor(black);
                    tv_top_logistics.setBackground(null);
                    tv_top_accept.setBackground(null);
                    tv_top_goods
                            .setBackgroundResource(R.drawable.base_tabpager_indicator_selected);
                    break;
                case 1:
                    tv_top_goods.setTextColor(black);
                    tv_top_accept.setTextColor(blue);
                    tv_top_logistics.setTextColor(black);
                    tv_top_goods.setBackground(null);
                    tv_top_logistics.setBackground(null);
                    tv_top_accept
                            .setBackgroundResource(R.drawable.base_tabpager_indicator_selected);
                    break;
                case 2:
                    tv_top_goods.setTextColor(black);
                    tv_top_accept.setTextColor(black);
                    tv_top_logistics.setTextColor(blue);
                    tv_top_goods.setBackground(null);
                    tv_top_accept.setBackground(null);
                    tv_top_logistics
                            .setBackgroundResource(R.drawable.base_tabpager_indicator_selected);
                    break;

                default:
                    break;
            }
        } else {
            ll_top_tabs.setVisibility(View.GONE);
        }
    }
}
