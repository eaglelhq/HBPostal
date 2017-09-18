package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blankj.utilcode.utils.ConvertUtils;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BannerBean;
import com.ksource.hbpostal.bean.JPGoogsBean;
import com.ksource.hbpostal.bean.JPGoogsBean.GoodsListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.util.SpannableUtils;
import com.ksource.hbpostal.widgets.CustomPopWindow;
import com.ksource.hbpostal.widgets.MyGridView;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.yitao.widget.BottomScrollView;
import com.yitao.widget.RollViewPager;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 八戒农场
 */
public class BJNCActivity1 extends BaseActivity {

    private PullToRefreshScrollView mPullRefreshScrollView;
    private BottomScrollView mScrollView;
    private TextView tv_title;
    private EditText et_search;
    private ImageView iv_back;
    private MyGridView gv_meal, gv_meal2;
    private List<GoodsListBean> mealDatas, mealDatas2;
    private BaseAdapter gridAdapter, gridAdapter2;
    private LoadDialog mLoadDialog;
    private RelativeLayout gv_item_error, gv_item_null, gv_item_error2, gv_item_null2;

    private ImageView btn_go_top;

    // 存放轮播图的线性布局
    private LinearLayout linearLayout;
    // 存放指示点的线性布局
    private LinearLayout pointLinearLayout;
    // 存放指示点的集合
    private List<ImageView> pointList = new ArrayList<>();
    // 上一个指示点
    private int lastPosition = 0;
    private int[] imageUrls;
    private List<BannerBean.AdvertListBean> advertList;
    private String[] imagesUrl;
    private RollViewPager rollViewPager;
    private LinearLayout ll_tiyan, ll_qiang, ll_card, ll_news, ll_act, ll_libao, ll_tc_card;
    private TextView tv_check,tv_sea_type;
//    private Spinner spinner;

    // private boolean isUpdate;
    private int currpageMeal;

    //    private boolean isHasMore;
    private String token;
//    private ArrayAdapter<String> adapter;
    private TextView btn_search;
    private int seaType = 0;
    private CustomPopWindow mCustomPopWindow;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_bjnc1;
    }

    @Override
    public void initView() {
        token = sp.getString(ConstantValues.TOKEN, null);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        gv_meal = (MyGridView) findViewById(R.id.gv_meal);
        gv_meal2 = (MyGridView) findViewById(R.id.gv_meal2);
        gv_item_null = (RelativeLayout) findViewById(R.id.gv_item_null);
        gv_item_error = (RelativeLayout) findViewById(R.id.gv_item_error);
        gv_item_null2 = (RelativeLayout) findViewById(R.id.gv_item_null2);
        gv_item_error2 = (RelativeLayout) findViewById(R.id.gv_item_error2);
        btn_go_top = (ImageView) findViewById(R.id.btn_go_top);
        mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mScrollView = mPullRefreshScrollView.getRefreshableView();
        linearLayout = (LinearLayout) findViewById(R.id.top_news_viewpager_ll);
        pointLinearLayout = (LinearLayout) findViewById(R.id.dots_ll);
        ll_tiyan = (LinearLayout) findViewById(R.id.ll_tiyan);
        ll_qiang = (LinearLayout) findViewById(R.id.ll_qiang);
        ll_card = (LinearLayout) findViewById(R.id.ll_card);
        ll_news = (LinearLayout) findViewById(R.id.ll_news);
        ll_act = (LinearLayout) findViewById(R.id.ll_act);
        ll_libao = (LinearLayout) findViewById(R.id.ll_libao);
        ll_tc_card = (LinearLayout) findViewById(R.id.ll_tc_card);
        tv_check = (TextView) findViewById(R.id.tv_check);
        tv_sea_type = (TextView) findViewById(R.id.tv_sea_type);
        et_search = (EditText) findViewById(R.id.et_search);
        btn_search = (TextView) findViewById(R.id.btn_search);
//        spinner = (Spinner) findViewById(R.id.spinner);

    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        tv_sea_type.setOnClickListener(this);
        gv_item_error.setOnClickListener(this);
        gv_item_error2.setOnClickListener(this);
        btn_go_top.setOnClickListener(this);
        ll_tiyan.setOnClickListener(this);
        ll_qiang.setOnClickListener(this);
        ll_card.setOnClickListener(this);
        ll_news.setOnClickListener(this);
        ll_act.setOnClickListener(this);
        ll_libao.setOnClickListener(this);
        tv_check.setOnClickListener(this);
        mPullRefreshScrollView.setMode(Mode.PULL_FROM_START);
        mScrollView.setVerticalScrollBarEnabled(false);
        mScrollView.setOnScrollListener(new BottomScrollView.OnScrollListener() {

            @Override
            public void onScroll(int scrollY) {
                if (scrollY > 2000) {
                    btn_go_top.setVisibility(View.VISIBLE);
                } else {
                    btn_go_top.setVisibility(View.GONE);
                }
            }
        });
        mPullRefreshScrollView
                .setOnRefreshListener(new OnRefreshListener<BottomScrollView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<BottomScrollView> refreshView) {
                        currpageMeal = 1;
                        getMealData();
                        getMeal2Data();
                    }

                });
        gv_meal.setFocusable(false);
        gv_meal.setNumColumns(2);
        gv_meal.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(context, GoodsDetailActivity.class);
                intent.putExtra("goodsId", mealDatas.get(position).ID);
                startActivity(intent);
            }
        });
        gv_meal2.setFocusable(false);
        gv_meal2.setNumColumns(2);
        gv_meal2.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(context, BJCardDetailActivity.class);
                intent.putExtra("goodsId", mealDatas2.get(position).ID);
                startActivity(intent);
            }
        });
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                seaType = position;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
////                seaType = 0;
//            }
//        });
    }

    @Override
    public void initData() {
        tv_title.setText("八戒生态农场");
        mealDatas = new ArrayList<>();
        mealDatas2 = new ArrayList<>();
        currpageMeal = 1;
        getMealData();
        getMeal2Data();
        setBanner();
//        List<String> list = new ArrayList<String>();
//        list.add("全部");
//        list.add("体验装");
//        list.add("特色抢购");
//        list.add("假日礼包");
//        list.add("宅配卡");
//
//        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,list);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setDropDownVerticalOffset(SizeUtils.dp2px(30));
    }

    // 初始化banner
    @SuppressWarnings("deprecation")
    private void setBanner() {
        lastPosition = 0;

        rollViewPager = new RollViewPager(context);
        linearLayout.addView(rollViewPager);

        imageUrls = new int[]{R.drawable.ncjj, R.drawable.z3, R.drawable.nchd, R.drawable.jrlb};
        // 第二步：传递轮播图需要的图片url集合或者数组
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
                        switch (position) {
                            case 1:
                                startActivity(new Intent(context, TiyanActivity.class));
                                break;
                            case 2:
                                startActivity(new Intent(context, ActActivity.class));
                                break;
                            case 0:
                                Intent intent = new Intent(context,
                                        BaseHtmlActivity.class);
                                intent.putExtra("title", "农场简介");
                                intent.putExtra("url", ConstantValues.NCJJ);
                                startActivity(intent);
                                break;
                            case 3:
                                startActivity(new Intent(context, LibaoActivity.class));
                                break;
                            default:
                                break;
                        }
//                        if (advertList != null && advertList.size() > 0) {
//                            String type = advertList.get(position).LINK_TYPE;
//                            String url = advertList.get(position).URL;
//                            switch (Integer.parseInt(type)) {
//                                case 1://http链接
//                                    if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
//                                        Intent intent = new Intent(context,
//                                                BaseHtmlActivity.class);
//                                        intent.putExtra("title", advertList.get(position).TITLE);
//                                        intent.putExtra("url", url);
//                                        startActivity(intent);
//                                    }
//                                    break;
//                                case 2://商品详情
//                                    Intent intent = new Intent(context,
//                                            GoodsDetailActivity.class);
//                                    intent.putExtra("goodsId", url);
//                                    startActivity(intent);
//                                    break;
//                                case 3://生活缴费
//                                    startJfAct(1);
//                                    break;
//                                case 4://个人中心
//                                    if (MainActivity.rg_home != null) {
//                                        // getActivity().getSupportFragmentManager().beginTransaction()
//                                        // .replace(R.id.fl_content, new HomeFragment()).commit();
//                                        MainActivity.rg_home.check(R.id.rb_me);
//                                    }
//                                    break;
//                                case 5://签到
//                                    Intent intentSign = new Intent(context, SignActivity.class);
//                                    startActivity(intentSign);
//                                    break;
//                                case 6://订单列表
//                                    Intent intentOrder = new Intent(context, OrderActivity.class);
//                                    intentOrder.putExtra("order", 0);
//                                    startActivity(intentOrder);
//                                    break;
//                                case 7://互动交流
//                                    startActivity(new Intent(context, HDJLActivity.class));
//                                    break;
//                                case 8://八戒农场
//                                    startActivity(new Intent(context, BJNCActivity.class));
//                                    break;
//                                case 9://手机缴费
//                                    startJfAct(6);
//                                    break;
//                                case 10://交通违章
//                                    startJfAct(7);
//                                    break;
//                                default:
//
//                                    break;
//                            }
//
//                        } else {
//                            Intent intent = new Intent(context,
//                                    BaseHtmlActivity.class);
//                            intent.putExtra("title", "邮支付缴费");
//                            intent.putExtra("url", ConstantValues.BANAER_HTML_URL);
//                            startActivity(intent);
//                        }
                    }
                });

        // 第六步：设置当前页面，最好不要写最大数除以2，其实写了50就足够了
        if (imagesUrl != null && imagesUrl.length > 0) {
            rollViewPager.setCurrentItem(50 - 50 % imagesUrl.length);
        } else {
            rollViewPager.setCurrentItem(50 - 50 % imageUrls.length);
        }

        // 第七步：设置完之后就可以轮播了：开启自动轮播
        if ((imagesUrl != null && imagesUrl.length > 1) || (imageUrls != null && imageUrls.length > 1))
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

    // 轮播图
    private void getBanner() {
        Map<String, String> params = new HashMap<String, String>();
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
                    .commit();
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

    // 获取单品列表
    private void getMealData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageSize", "20");
        params.put("currPage", "" + currpageMeal);
        params.put("IS_NCP", "1");
        params.put("HOT", "1");

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                mPullRefreshScrollView.onRefreshComplete();
                Gson gson = new Gson();
                JPGoogsBean goodsBean = null;
                try {
                    goodsBean = gson.fromJson(arg0,
                            JPGoogsBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (goodsBean == null) {
                    gv_item_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (goodsBean.success) {
                    gv_item_error.setVisibility(View.GONE);
                    if (goodsBean.goodsList != null) {
                        mealDatas = goodsBean.goodsList;
                    }
                    if (mealDatas.size() == 0) {
                        gv_item_null.setVisibility(View.VISIBLE);
                    } else {
                        gv_item_null.setVisibility(View.GONE);
                        if (gridAdapter == null) {
                            gridAdapter = new GridAdapter(mealDatas);
                            gv_meal.setAdapter(gridAdapter);
                        } else {
                            gridAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    if (goodsBean.flag == 10) {
                        mApplication.login();
                    } else {
                        gv_item_null.setVisibility(View.VISIBLE);
                        ToastUtil.showTextToast(context, goodsBean.msg);
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                gv_item_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.JPTJ_URL, params, callback);
    }

    // 获取单品列表
    private void getMeal2Data() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageSize", "20");
        params.put("currPage", "" + currpageMeal);
        params.put("HOT", "1");
//        params.put("NCP_FL", "0");

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                mPullRefreshScrollView.onRefreshComplete();
                Gson gson = new Gson();
                JPGoogsBean goodsBean = null;
                try {
                    goodsBean = gson.fromJson(arg0,
                            JPGoogsBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (goodsBean == null) {
                    gv_item_error2.setVisibility(View.VISIBLE);
                    return;
                }
                if (goodsBean.success) {
                    gv_item_error2.setVisibility(View.GONE);
                    if (goodsBean.goodsList != null) {
                        mealDatas2 = goodsBean.goodsList;
                    }
                    if (mealDatas2.size() == 0) {
                        gv_item_null2.setVisibility(View.VISIBLE);
                    } else {
                        gv_item_null2.setVisibility(View.GONE);
                        if (gridAdapter2 == null) {
                            gridAdapter2 = new GridAdapter(mealDatas2);
                            gv_meal2.setAdapter(gridAdapter2);
                        } else {
                            gridAdapter2.notifyDataSetChanged();
                        }
                    }
                } else {
                    if (goodsBean.flag == 10) {
                        mApplication.login();
                    } else {
                        gv_item_null2.setVisibility(View.VISIBLE);
                        ToastUtil.showTextToast(context, goodsBean.msg);
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                gv_item_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.BJ_ZHAI_LIST, params, callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.gv_item_error:
                mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                        "数据加载中...");
                gridAdapter = null;
                currpageMeal = 1;
                getMealData();
                break;
            case R.id.ll_tiyan:
                startActivity(new Intent(context, TiyanActivity.class));
                break;
            case R.id.ll_qiang:
                startActivity(new Intent(context, QiangActivity.class));
                break;
            case R.id.ll_card:
                startActivity(new Intent(context, ZhaiActivity.class));
                break;
            case R.id.ll_news:
                startActivity(new Intent(context, NewsActivity.class));
                break;
            case R.id.ll_act:
                startActivity(new Intent(context, ActActivity.class));
                break;
            case R.id.ll_libao:
                startActivity(new Intent(context, LibaoActivity.class));
                break;
            case R.id.tv_check:
                startActivity(new Intent(context, NPCOrderActivity.class));
                break;
            case R.id.btn_go_top:
                mScrollView.fullScroll(ScrollView.FOCUS_UP);
                btn_go_top.setVisibility(View.GONE);
                break;
            case R.id.tv_sea_type:
                showPopMenu();
                break;
            case R.id.btn_search:
                // 搜索
                Intent intent = new Intent(context,BJSearchActivity.class);
                String keyWord = et_search.getText().toString().trim();
                intent.putExtra("keyWord",keyWord);
                intent.putExtra("seaType",seaType);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private class GridAdapter extends DefaultBaseAdapter<GoodsListBean> {

        public GridAdapter(List<GoodsListBean> datas) {
            super(datas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_goods, null);
                holder = new GridViewHolder();
                holder.icon = (ImageView) convertView
                        .findViewById(R.id.iv_goods);
                holder.name = (TextView) convertView
                        .findViewById(R.id.tv_goods_name);
                holder.sale = (TextView) convertView
                        .findViewById(R.id.tv_goods_sale);
                holder.count = (TextView) convertView
                        .findViewById(R.id.tv_goods_count);

                convertView.setTag(holder);
            } else {
                holder = (GridViewHolder) convertView.getTag();
            }

            // 填充数据
            int type = datas.get(position).BUY_TYPE;
            String text;
            switch (type) {
                case 1:
                    text = "￥" + datas.get(position).PRICE + " ";
                    holder.sale.setText(SpannableUtils.setTextSize(text, 1,
                            text.length() - 1, ConvertUtils.dp2px(14)));

                    break;
                case 2:
                    text = "￥" + datas.get(position).PRICE + "+"
                            + datas.get(position).INTEGRAL + "积分";
                    holder.sale.setText(SpannableUtils.setTextSize(text, 1,
                            text.length() - 2, ConvertUtils.dp2px(14)));

                    break;
                case 3:
                    text = datas.get(position).INTEGRAL + "积分";
                    holder.sale.setText(SpannableUtils.setTextSize(text, 0,
                            text.length() - 2, ConvertUtils.dp2px(14)));

                    break;

                default:
                    text = "￥" + datas.get(position).PRICE + " ";
                    holder.sale.setText(SpannableUtils.setTextSize(text, 1,
                            text.length() - 1, ConvertUtils.dp2px(14)));
                    break;
            }
            holder.name.setText(datas.get(position).NAME);
            holder.count.setText("售" + datas.get(position).BUY_NUM);
            try {
                ImageLoaderUtil.loadNetPic(
                        ConstantValues.BASE_URL + datas.get(position).IMAGE,
                        holder.icon);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

    }

    private class GridViewHolder {
        private ImageView icon;
        private TextView name, sale, count;
    }

    //显示弹出菜单
    private void showPopMenu(){
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        View contentView = LayoutInflater.from(this).inflate(R.layout.sea_menu,null);
        //处理popWindow 显示内容
        handleLogic(contentView);
        //创建并显示popWindow
        mCustomPopWindow= new CustomPopWindow.PopupWindowBuilder(this)
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
                .create()
                .showAsDropDown(tv_sea_type,5,0);

    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     * @param contentView
     */
    private void handleLogic(View contentView){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCustomPopWindow!=null){
                    mCustomPopWindow.dissmiss();
                }
//                // 设置背景颜色变暗
//                WindowManager.LayoutParams lp = getWindow()
//                        .getAttributes();
//                lp.alpha = 1.0f;
//                getWindow().setAttributes(lp);
                String showContent = "";
                switch (v.getId()){
                    case R.id.menu1:
                        seaType = 0;
                        showContent = "全部";
                        break;
                    case R.id.menu2:
                        seaType = 1;
                        showContent = "体验装";
                        break;
                    case R.id.menu3:
                        seaType = 2;
                        showContent = "特色抢购";
                        break;
                    case R.id.menu4:
                        seaType = 3;
                        showContent = "假日礼包";
                        break;
                    case R.id.menu5:
                        seaType = 4;
                        showContent = "宅配卡";
                        break;
                }
                tv_sea_type.setText(showContent);
//                Toast.makeText(context,"选择："+showContent, Toast.LENGTH_SHORT).show();
            }
        };
        contentView.findViewById(R.id.menu1).setOnClickListener(listener);
        contentView.findViewById(R.id.menu2).setOnClickListener(listener);
        contentView.findViewById(R.id.menu3).setOnClickListener(listener);
        contentView.findViewById(R.id.menu4).setOnClickListener(listener);
        contentView.findViewById(R.id.menu5).setOnClickListener(listener);
    }
    //开启缴费页面
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
}
