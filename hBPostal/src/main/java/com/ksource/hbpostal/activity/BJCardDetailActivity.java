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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.MyApplication;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BJCardResultBean;
import com.ksource.hbpostal.bean.BJPriceResultBean;
import com.ksource.hbpostal.bean.RecordResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.interfaces.ClickBackJsInterface;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.util.TimeUtil;
import com.ksource.hbpostal.widgets.CustomPopWindow;
import com.ksource.hbpostal.widgets.MyListView;
import com.ksource.hbpostal.widgets.RollViewPager;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.NetStateUtils;
import com.yitao.util.ToastUtil;
import com.yitao.widget.BottomScrollView;
import com.yitao.widget.BottomScrollView.OnScrollListener;
import com.yitao.widget.RoundImageView;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;

public class BJCardDetailActivity extends FragmentActivity implements
        OnClickListener, OnScrollListener {

    private int size;
    private TextView tv_title;
    private ImageView iv_back;
    private Context context;
    // private Banner banner;
    private MyApplication mApplication;
    private LinearLayout ll_top_tabs, ll_top;

    private SharedPreferences sp;

    private TextView tv_goods_name, tv_goods_price, tv_chioce,
            tv_jifen_name, tv_add, tv_market_price, tv_duihuan;
    //    private TextView tv_cart;
    private TextView tv_cart_count;
    private TextView btn_clearing;

    private LoadDialog mLoadDialog;

    private BJCardResultBean goodsInfo = new BJCardResultBean();
    private String[] imagesUrl = {""};
    private String goodsId = "";

    private TextView tv_goods, tv_logistics;
    private TextView tv_top_goods, tv_top_logistics;
    private WebView mWebView;

    private MyListView lv_record;
    private RelativeLayout rl_null;
    private BottomScrollView bsv;

    // 购买记录
    private BaseAdapter recordAdapter;
    private List<RecordResultBean.OrderListBean> recordDatas;
    private int currPageRecord = 1;

    private String token;

    private boolean isHasMoreRecord;
    private int currTab;

    private WebSettings settings;
    private String url;

    // 颜色
    int blue;
    int black;

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
    private CustomPopWindow mListPopWindow;
    private TextView tv_price;
    private String price;

    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        context = this;
        mApplication = MyApplication.getInstance();
        mApplication.addActivity(this);
        sp = getSharedPreferences(ConstantValues.SP_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_zhai_detail);

        initView();
        initListener();
        initData();
    }

    private void initView() {
        goodsId = getIntent().getStringExtra("goodsId");
        // 轮播图
        linearLayout = (LinearLayout) findViewById(R.id.top_news_viewpager_ll);
        pointLinearLayout = (LinearLayout) findViewById(R.id.dots_ll);

        bsv = (BottomScrollView) findViewById(R.id.bsv);
        ll_top_tabs = (LinearLayout) findViewById(R.id.ll_top_tabs);
        ll_top = (LinearLayout) findViewById(R.id.ll_top);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("商品详情");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        mWebView = (WebView) findViewById(R.id.webView);
        lv_record = (MyListView) findViewById(R.id.lv_record);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);

        tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);
        tv_goods_price = (TextView) findViewById(R.id.tv_goods_price);
        tv_chioce = (TextView) findViewById(R.id.tv_chioce);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_jifen_name = (TextView) findViewById(R.id.tv_jifen_name);
        tv_market_price = (TextView) findViewById(R.id.tv_market_price);
        tv_market_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tv_duihuan = (TextView) findViewById(R.id.tv_duihuan);
//        tv_cart = (TextView) findViewById(R.id.tv_cart);
        tv_cart_count = (TextView) findViewById(R.id.tv_cart_count);
        btn_clearing = (TextView) findViewById(R.id.btn_clearing);
        // 标签:
        tv_goods = (TextView) findViewById(R.id.tv_goods);
        tv_logistics = (TextView) findViewById(R.id.tv_logistics);
        tv_top_goods = (TextView) findViewById(R.id.tv_top_goods);
        tv_top_logistics = (TextView) findViewById(R.id.tv_top_logistics);

    }

    private void initListener() {
        iv_back.setOnClickListener(this);
//        tv_cart.setOnClickListener(this);
        // btn_go_top.setOnClickListener(this);

        btn_clearing.setOnClickListener(this);
        // 给标签添加点击事件
        tv_goods.setOnClickListener(this);
        tv_logistics.setOnClickListener(this);
        tv_top_goods.setOnClickListener(this);
        tv_top_logistics.setOnClickListener(this);
        tv_chioce.setOnClickListener(this);
        bsv.setOnScrollListener(this);
        // 当布局的状态或者控件的可见性发生改变回调的接口
        findViewById(R.id.parent_layout).getViewTreeObserver()
                .addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // 这一步很重要，使得上面的购买布局和下面的购买布局重合
                        size = ll_top.getHeight();// + ll_top_tabs.getHeight()
                        onScroll(bsv.getScrollY());

                        System.out.println(bsv.getScrollY());
                    }
                });
    }

    private void initData() {

        token = sp.getString(ConstantValues.TOKEN, "");
        recordDatas = new ArrayList<RecordResultBean.OrderListBean>();
        getBJData();
        hasNewRecordDatas = true;
//		if (!TextUtils.isEmpty(token)) {
//			getCount();
//		}
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
//		rollViewPager.stopRoll();
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
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        context.startActivity(intent);
    }

    //加载商品详情webview
    private void loadWebView() {
        url = ConstantValues.BJ_ZHAI_DETAIL + goodsId;
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
//		isTiyan = goodsInfoBean.NCP_FL == 1;
        // isTiyan = getIntent().getBooleanExtra("tiyan",false);
//			btn_clearing.setEnabled(false);
        // banner.setOnClickListener(this);
        // banner.setImagesUrl(imagesUrl);
        // 商品信息
        tv_goods_name.setText("" + goodsInfo.goodsInfo.name);
//		isNcp = goodsInfoBean.IS_NCP == 1 ? true : false;
        tv_goods_price.setText("￥" + goodsInfo.priceMap.PRICE);
        tv_add.setVisibility(View.GONE);
        tv_jifen_name.setVisibility(View.GONE);

        tv_market_price.setText("￥" + goodsInfo.priceMap.MARKET_PRICE);
        tv_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tv_duihuan.setText("" + goodsInfo.sales_num);
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
                mApplication.finishAllActivity();
                Intent intent1 = new Intent(context, MainActivity.class);
                intent1.putExtra("item", 1);
                startActivity(intent1);
                break;
            case R.id.btn_clearing:

                if (TextUtils.isEmpty(token)) {
                    startActivity(new Intent(context, LoginActivity.class));
                    return;
                }

                if (!TextUtils.isEmpty(selectPeoperKey) && !TextUtils.isEmpty(selectTimeKey)) {
                    gotoOrder();
                } else {
                    //选择属性
                    chioceItem();
                }
                break;

            case R.id.tv_goods:
            case R.id.tv_top_goods:
                currTab = 0;
                // 颜色
                blue = getResources().getColor(R.color.green);
                black = getResources().getColor(R.color.gary);
                tv_goods.setTextColor(blue);
                tv_logistics.setTextColor(black);
                tv_logistics.setBackground(null);
                tv_goods.setBackgroundResource(R.drawable.base_tabpager_indicator_selected);
                mWebView.setVisibility(View.VISIBLE);
                lv_record.setVisibility(View.GONE);
                rl_null.setVisibility(View.GONE);
                break;
            case R.id.tv_accept:
            case R.id.tv_top_accept:
                currTab = 1;
                rl_null.setVisibility(View.GONE);
                blue = getResources().getColor(R.color.green);
                black = getResources().getColor(R.color.gary);
                tv_goods.setTextColor(black);
                tv_logistics.setTextColor(black);
                tv_goods.setBackground(null);
                tv_logistics.setBackground(null);
                mWebView.setVisibility(View.GONE);
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
                tv_logistics.setTextColor(blue);
                tv_goods.setBackground(null);
                tv_logistics
                        .setBackgroundResource(R.drawable.base_tabpager_indicator_selected);
                mWebView.setVisibility(View.GONE);
                lv_record.setVisibility(View.VISIBLE);
                break;

            case R.id.tv_chioce:
                //选择属性
                chioceItem();
                break;
            default:
                break;
        }
    }

    //选择属性
    private void chioceItem() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_list, null);
        //处理popWindow 显示内容
        handleListView(contentView);

        //创建并显示popWindow
        mListPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        // 设置背景颜色变亮
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1.0f;
                        getWindow().setAttributes(lp);
                    }
                })
                .setView(contentView)
                .size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
                .create()
                .showAtLocation(btn_clearing, Gravity.BOTTOM, 0, 0);
//                .showAsDropDown(btn_clearing,0,0);
//                .showAsDropDown(btn_clearing, 0, -btn_clearing.getHeight());
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
    }

    private TagAdapter<String> adapterPeoper, adapterTime;
    private String selectPeoperStr = "", selectPeoperKey = "";
    private String selectTimeStr = "", selectTimeKey = "";
    private int selectPeoper = -1, selectTime = -1;

    //处理popWindow 显示内容
    private void handleListView(final View contentView) {
        final TagFlowLayout fl_peoper = (TagFlowLayout) contentView.findViewById(R.id.fl_peoper);
        final TagFlowLayout fl_time = (TagFlowLayout) contentView.findViewById(R.id.fl_time);
        tv_price = (TextView) contentView.findViewById(R.id.tv_price);
        final TextView tv_desc = (TextView) contentView.findViewById(R.id.tv_desc);
        final TextView btn_buy = (TextView) contentView.findViewById(R.id.btn_buy);
        final List<String> peoperDatas = new ArrayList<>();
        for (BJCardResultBean.PeopleItemBean bean : goodsInfo.peopleItem
                ) {
            peoperDatas.add(bean.KEY_NAME);
        }
        final LayoutInflater mInflater = LayoutInflater.from(context);
        adapterPeoper = new TagAdapter<String>(peoperDatas) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tv,
                        fl_peoper, false);
                tv.setText(s);
                return tv;
            }

        };
        fl_peoper.setAdapter(adapterPeoper);
        if (selectPeoper != -1) {
            adapterPeoper.setSelectedList(selectPeoper);
        }

        final List<String> timeDatas = new ArrayList<>();
        for (BJCardResultBean.TimeItemBean bean : goodsInfo.timeItem
                ) {
            timeDatas.add(bean.OPTION_KEY_NAME);
        }
        adapterTime = new TagAdapter<String>(timeDatas) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tv,
                        fl_time, false);
                tv.setText(s);
                return tv;
            }

        };
        fl_time.setAdapter(adapterTime);
        if (selectTime != -1) {
            adapterTime.setSelectedList(selectTime);
        }
        if (!TextUtils.isEmpty(selectPeoperStr) && !TextUtils.isEmpty(selectTimeStr)) {
            btn_buy.setEnabled(true);
        }
        fl_peoper.setOnSelectListener(new TagFlowLayout.OnSelectListener() {

            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                if (selectPosSet != null && selectPosSet.size() != 0) {
                    selectPeoper = (int) selectPosSet.toArray()[0];
                }
                if (selectPeoper != -1) {
                    selectPeoperStr = peoperDatas.get(selectPeoper);
                    selectPeoperKey = goodsInfo.peopleItem.get(selectPeoper).KEY_CODE;
                } else {
                    selectPeoperStr = "";
                    selectPeoperKey = "";
                }
                tv_desc.setText("已选择：" + selectPeoperStr + " " + selectTimeStr);
                tv_chioce.setText("已选择：" + selectPeoperStr + " " + selectTimeStr);
                if (!TextUtils.isEmpty(selectPeoperStr) && !TextUtils.isEmpty(selectTimeStr)) {
                    getPrice();
                    btn_buy.setEnabled(true);
                }
            }
        });
        fl_time.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                if (selectPosSet != null && selectPosSet.size() != 0) {
                    selectTime = (int) selectPosSet.toArray()[0];
                }
                if (selectTime != -1) {
                    selectTimeStr = timeDatas.get(selectTime);
                    selectTimeKey = goodsInfo.timeItem.get(selectTime).OPTION_KEY_CODE;
                } else {
                    selectTimeStr = "";
                    selectTimeKey = "";
                }
                tv_desc.setText("已选择：" + selectPeoperStr + " " + selectTimeStr);
                tv_chioce.setText("已选择：" + selectPeoperStr + " " + selectTimeStr);
                if (!TextUtils.isEmpty(selectPeoperStr) && !TextUtils.isEmpty(selectTimeStr)) {
                    getPrice();
                    btn_buy.setEnabled(true);
                }
            }
        });
        tv_desc.setText("已选择：" + selectPeoperStr + " " + selectTimeStr);
        btn_buy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoOrder();
            }
        });
    }

    //去提交订单
    private void gotoOrder() {
        Intent intent = new Intent(context, ConfirmOrderForBJ.class);
        intent.putExtra("goods_id", goodsId);
        intent.putExtra("goods_name", goodsInfo.goodsInfo.name);
        intent.putExtra("goods_image", goodsInfo.goodsImgList.get(0).FILE_PATH);
        intent.putExtra("goods_desc", goodsInfo.goodsInfo.abstruct);
        intent.putExtra("total_price", price);
        intent.putExtra("key_code", selectPeoperKey);
        intent.putExtra("key_name", selectPeoperStr);
        intent.putExtra("option_key_code", selectTimeKey);

        intent.putExtra("option_key_name", selectTimeStr);
        startActivity(intent);
    }

    //获取所选商品价格
    private void getPrice() {
        if (!TextUtils.isEmpty(selectPeoperKey) && !TextUtils.isEmpty(selectTimeKey)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("id", goodsId);
            params.put("key_code", selectPeoperKey);
            params.put("option_key_code", selectTimeKey);
            StringCallback callback = new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int i) {
                    ToastUtil.showTextToast(context, "计算商品价格失败！");
                }

                @Override
                public void onResponse(String s, int i) {
                    Gson gson = new Gson();
                    BJPriceResultBean goodsPrice = null;
                    try {
                        goodsPrice = gson.fromJson(s,
                                BJPriceResultBean.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    price = goodsPrice.price.PRICE;
                    if (TextUtils.isEmpty(price)) {
                        selectPeoperStr = "";
                        selectPeoperKey = "";
                        selectTimeStr = "";
                        selectTimeKey = "";
                        mListPopWindow.dissmiss();
                        chioceItem();
                        ToastUtil.showTextToast(context, "没有这个组合，请重新选择！");
                    } else {
                        tv_price.setText("￥" + price);
                        tv_goods_price.setText("￥" + price);
                        tv_market_price.setText(goodsPrice.price.MARKET_PRICE);
                    }
                }
            };
            DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.BJ_ZHAI_PRICE, params, callback);
        }

    }

    // 获取农产品商品信息
    private void getBJData() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "数据加载中...");
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", goodsId);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                loadWebView();
                getRecordData();
                Gson gson = new Gson();
                BJCardResultBean goodsResult = null;
                try {
                    goodsResult = gson.fromJson(arg0,
                            BJCardResultBean.class);
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
                    goodsInfo = goodsResult;
                    if (goodsResult.goodsImgList != null
                            && goodsResult.goodsImgList
                            .size() > 0) {
                        List<BJCardResultBean.GoodsImgListBean> advertList = goodsResult.goodsImgList;
                        imagesUrl = new String[advertList.size()];
                        for (int i = 0; i < advertList.size(); i++) {
                            imagesUrl[i] = ConstantValues.BASE_URL
                                    + advertList.get(i).FILE_PATH;
                        }
                    }
                    btn_clearing.setEnabled(true);
                    setData();
                } else {
                    ToastUtil.showTextToast(context, goodsResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取商品信息失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.BJ_ZHAI_INFO, params, callback);
    }

    private TextView viewRecord;
    private boolean hasNewRecordDatas;
    private int lastRecordCurrPage;

    // 获取列表信息
    private void getRecordData() {

        if (!NetStateUtils.isNetworkAvailable(context)) {
            ToastUtil.showTextToast(context, "当前网络不可用！");
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("goods_id", goodsId);
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
                if (currTab == 2) {
                    rl_null.setVisibility(View.VISIBLE);
                    isHasMoreRecord = false;
                }
                ToastUtil.showTextToast(context, "获取数据失败!");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.ZPK_RECORD, params, callback);
    }

    // 购买记录
    private void showRecordData() {
        hasNewRecordDatas = !(lastRecordCurrPage == currPageRecord);
        lv_record.removeFooterView(viewRecord);
        if (recordResultBean == null) {
            rl_null.setVisibility(View.VISIBLE);
            isHasMoreRecord = false;
            ToastUtil.showTextToast(context, "获取数据失败！");
            return;
        }
        if (recordResultBean.success) {
            if (hasNewRecordDatas) {
                recordDatas.addAll(recordResultBean.orderList);
            }
            lastRecordCurrPage = currPageRecord;
            if (recordAdapter == null) {
                recordAdapter = new MyAdapter(recordDatas);
                lv_record.setAdapter(recordAdapter);
            } else {
                recordAdapter.notifyDataSetChanged();
            }
            // lv_record.setVisibility(View.VISIBLE);
            isHasMoreRecord = recordResultBean.orderList.size() >= 10 ;
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

    class MyAdapter extends DefaultBaseAdapter<RecordResultBean.OrderListBean> {

        public MyAdapter(List<RecordResultBean.OrderListBean> datas) {
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
            String headImage = datas.get(position).head_image;
            String nickName = datas.get(position).nick_name;
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
            String price = datas.get(position).total_price;
            String jifen = null;
            String feiyong = getFei(price, jifen);
            holder.tv_price.setText(feiyong);
            String creatTime = TimeUtil
                    .formatTimeMin(datas.get(position).create_time);
            holder.tv_record_time.setText(creatTime);
            if (headImage == null || TextUtils.isEmpty(headImage)) {
                holder.iv_user_avatar
                        .setImageResource(R.drawable.head_fail_img);
            } else {
                try {
                    ImageLoaderUtil.loadNetPic(
                            ConstantValues.BASE_URL
                                    + datas.get(position).head_image,
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
                    tv_top_logistics.setTextColor(black);
                    tv_top_logistics.setBackground(null);
                    tv_top_goods
                            .setBackgroundResource(R.drawable.base_tabpager_indicator_selected);
                    break;
                case 1:
                    tv_top_goods.setTextColor(black);
                    tv_top_logistics.setTextColor(black);
                    tv_top_goods.setBackground(null);
                    tv_top_logistics.setBackground(null);
                    break;
                case 2:
                    tv_top_goods.setTextColor(black);
                    tv_top_logistics.setTextColor(blue);
                    tv_top_goods.setBackground(null);
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
