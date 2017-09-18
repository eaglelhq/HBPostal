package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.ConvertUtils;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BannerBean;
import com.ksource.hbpostal.bean.JPGoogsBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.util.SpannableUtils;
import com.ksource.hbpostal.widgets.MyGridView;
import com.yitao.widget.BottomScrollView;
import com.yitao.widget.RollViewPager;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 八戒农场-假日礼包页面
 */
public class LibaoActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    //    private PullToRefreshGridView ptrGv;
    private MyGridView gridView;
    private PullToRefreshScrollView mPullRefreshScrollView;
    private BottomScrollView mScrollView;

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

    private int currpage = 1;
    private RelativeLayout gv_item_error, gv_item_null;
    private BaseAdapter gridAdapter;
    private List<JPGoogsBean.GoodsListBean> mealDatas;
    private boolean isUpdate;
    private String token;
    private boolean isHasMore;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_libao;
    }

    @Override
    public void initView() {
        token = sp.getString(ConstantValues.TOKEN, null);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        gv_item_null = (RelativeLayout) findViewById(R.id.gv_item_null);
        gv_item_error = (RelativeLayout) findViewById(R.id.gv_item_error);
        // 得到控件
        gridView = (MyGridView) findViewById(R.id.pull_refresh_grid);
//        gridView = ptrGv.getRefreshableView();
        gridView.setNumColumns(2);

        linearLayout = (LinearLayout) findViewById(R.id.top_news_viewpager_ll);
        pointLinearLayout = (LinearLayout) findViewById(R.id.dots_ll);

        mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mScrollView = mPullRefreshScrollView.getRefreshableView();
        mPullRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mScrollView.setVerticalScrollBarEnabled(false);
        mScrollView.setOnScrollToBottomLintener(new BottomScrollView.OnScrollToBottomListener() {

            @Override
            public void onScrollBottomListener(boolean isBottom) {
                if (isBottom && isHasMore) {
                    // ToastUtil.showTextToast(context, "SCROLLVIEW"+isBottom +
                    // "");
                    // mLoadDialog =
                    // DialogUtil.getInstance().showLoadDialog(context,
                    // "数据加载中...");
                    currpage++;
                    getData();
                }

            }
        });
        mPullRefreshScrollView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<BottomScrollView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<BottomScrollView> refreshView) {
                        if (rollViewPager != null) {
                            rollViewPager.stopRoll();
                            linearLayout.removeAllViews();
                        }
//                        getBanner();
                        setBanner();
                        mealDatas.clear();
                        currpage = 1;
                        getData();
                    }

                });
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
//        ptrGv.setPullRefreshEnabled(true);
//        ptrGv.setPullLoadEnabled(false);
//        ptrGv.setScrollLoadEnabled(true);
//        ptrGv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
//                isUpdate = true;
//                currpage = 1;
//                if (mealDatas != null) {
//                    mealDatas.clear();
//                }
//                getData();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
//                isUpdate = false;
//                currpage++;
//                getData();
//            }
//        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, GoodsDetailActivity.class);
                intent.putExtra("goodsId", mealDatas.get(position).ID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        tv_title.setText("假日礼包");
        mealDatas = new ArrayList<>();
//        getData();
//        ptrGv.doPullRefreshing(true, 500);
        mealDatas.clear();
        currpage = 1;
        getData();
        setBanner();
    }

    // 初始化banner
    @SuppressWarnings("deprecation")
    private void setBanner() {
        lastPosition = 0;
        rollViewPager = new RollViewPager(context);
        linearLayout.addView(rollViewPager);

        imageUrls = new int[]{R.drawable.jrlb};
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
//                        if (advertList != null && advertList.size() > 0) {
//                            String type = advertList.get(position).LINK_TYPE;
//                            String url = advertList.get(position).URL;
//                            switch (Integer.parseInt(type)) {
//                               case 1://http链接
//                                   if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
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
        DataUtil.doPostAESData(null,context,ConstantValues.BANNER_URL, params, callback);
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

    private void getData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageSize", "10");
        params.put("currPage", "" + currpage);
        params.put("IS_NCP", "1");
        params.put("NCP_FL", "3");

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                mPullRefreshScrollView.onRefreshComplete();
//                gridView.onRefreshComplete();
                Gson gson = new Gson();
                JPGoogsBean goodsBean = null;
                try {
                    goodsBean = gson.fromJson(arg0,
                            JPGoogsBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (goodsBean == null
                        && (mealDatas == null || mealDatas.size() == 0)) {
                    gv_item_error.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.GONE);

                    return;
                }
                if (goodsBean != null) {
                    // Message msgGoods = new Message();
                    if (goodsBean.success
                            && goodsBean.goodsList != null) {
                        gv_item_error.setVisibility(View.GONE);
                        gv_item_null.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                        mealDatas.addAll(goodsBean.goodsList);
                        if (gridAdapter == null) {
                            gridAdapter = new GridAdapter(mealDatas);
                            gridView.setAdapter(gridAdapter);
                        } else {
                            gridAdapter.notifyDataSetChanged();
                        }
                        isHasMore = goodsBean.goodsList.size() >= 10 ;
                    } else {
                        isHasMore = false;
                        gv_item_null.setVisibility(View.VISIBLE);
                        // rl_error.setVisibility(View.VISIBLE);
                        // gridview_home.setVisibility(View.GONE);
                    }
                }
                if (goodsBean == null) {
                    gv_item_error.setVisibility(View.VISIBLE);
                    return;
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                mPullRefreshScrollView.onRefreshComplete();
                if (mealDatas == null || mealDatas.size() == 0)
                    gv_item_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(null,context,ConstantValues.JPTJ_URL, params, callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private class GridAdapter extends DefaultBaseAdapter<JPGoogsBean.GoodsListBean> {

        public GridAdapter(List<JPGoogsBean.GoodsListBean> datas) {
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
