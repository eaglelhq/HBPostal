package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.MainViewpagerAdapter;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.fragment.AllYHQFragment;
import com.ksource.hbpostal.fragment.WsyYHQFragment;
import com.ksource.hbpostal.fragment.YgqYHQFragment;
import com.ksource.hbpostal.fragment.YsyYHQFragment;
import com.ksource.hbpostal.widgets.LazyViewPager;
import com.nineoldandroids.view.ViewHelper;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的优惠券页面
 */
public class YhqActivity extends BaseActivity {

    private TextView tv_title;
//    private TextView btn_right;
    private ImageView iv_back;
    private PullToRefreshListView lv;
    private ListView lv_card;
    private LoadDialog mLoadDialog;
    private RelativeLayout rl_error, rl_null;
    //	private LinearLayout ll_type1,ll_type2,ll_type3;
    private String token;
    private TextView tv_type1, tv_type2, tv_type3, tv_type4;
    //	private List<BankCardListBean> datas;
    //private BaseAdapter adapter;

    // 数据源
    List<Fragment> datas = new ArrayList<>();
    private MainViewpagerAdapter adapter;
    private LazyViewPager vp_detail;
    private View indicate_line;
    private int lineWidth;// 指示线宽度


    @Override
    public int getLayoutResId() {
        return R.layout.activity_dh_yhq;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
//        btn_right = (TextView) findViewById(R.id.btn_right);
//        btn_right.setVisibility(View.VISIBLE);
//        btn_right.setText("领取优惠券");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv = (PullToRefreshListView) findViewById(R.id.lv_yhq);

        tv_type1 = (TextView) findViewById(R.id.tv_type1);
        tv_type2 = (TextView) findViewById(R.id.tv_type2);
        tv_type3 = (TextView) findViewById(R.id.tv_type3);
        tv_type4 = (TextView) findViewById(R.id.tv_type4);
//		ll_type1 = (LinearLayout) findViewById(R.id.ll_type1);
//		ll_type2 = (LinearLayout) findViewById(R.id.ll_type2);
//		ll_type3 = (LinearLayout) findViewById(R.id.ll_type3);
        rl_error = (RelativeLayout) findViewById(R.id.rl_error);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
        vp_detail = (LazyViewPager) findViewById(R.id.vp_detail);
        // 指示线
        indicate_line = findViewById(R.id.indicate_line);
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
//        btn_right.setOnClickListener(this);
//        rl_error.setOnClickListener(this);
        // viewpager添加界面改变监听
        vp_detail.setOnPageChangeListener(new OnMainPageChangeListener());
        tv_type1.setOnClickListener(this);
        tv_type2.setOnClickListener(this);
        tv_type3.setOnClickListener(this);
        tv_type4.setOnClickListener(this);
//		ll_type1.setOnClickListener(this);
//		ll_type2.setOnClickListener(this);
//		ll_type3.setOnClickListener(this);
//        lv.setPullRefreshEnabled(true);
//        lv.setPullLoadEnabled(false);
//        lv.setScrollLoadEnabled(true);
//        lv.setHasMoreData(false);
//        lv_card = lv.getRefreshableView();
//        lv_card.setDivider(null);
////		lv_card.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////			@Override
////			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////			}
////		});
//        lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
//
//            @Override
//            public void onPullDownToRefresh(
//                    PullToRefreshBase<ListView> refreshView) {
//                lv.onPullDownRefreshComplete();
//                lv.onPullUpRefreshComplete();
//                lv.setHasMoreData(false);
////                getData();
//            }
//
//            @Override
//            public void onPullUpToRefresh(
//                    PullToRefreshBase<ListView> refreshView) {
//                // isUpdate = false;
//                // getData();
//            }
//        });
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter = null;
////		isUpdate = true;
////		currPage = 1;
////        getData();
////		lv.doPullRefreshing(true, 500);
//    }

    @Override
    public void initData() {
        tv_title.setText("我的优惠券");
        token = sp.getString(ConstantValues.TOKEN, "");
        datas = new ArrayList<>();
        datas.add(new AllYHQFragment());
        datas.add(new WsyYHQFragment());
        datas.add(new YsyYHQFragment());
        datas.add(new YgqYHQFragment());
        // 添加Viewpager适配器
        adapter = new MainViewpagerAdapter(getSupportFragmentManager(), datas);
        vp_detail.setAdapter(adapter);
        // 初始化数据,数据源数据
//		adapter.notifyDataSetChanged();
        // 指示线初始化
        int screenWidth = getResources().getDisplayMetrics().widthPixels;// 屏幕宽度
        lineWidth = screenWidth / 4;
        indicate_line.getLayoutParams().width = lineWidth;
        vp_detail.setCurrentItem(0);
    }

    private final class OnMainPageChangeListener implements
            LazyViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            /**
             * 1. 标签颜色改变 2. 标签执行缩放动画 3. 指示线跟着手指一起变化
             */
            // 1. 标签颜色改变
            int green = getResources().getColor(R.color.green);
            int black = getResources().getColor(R.color.gary);
            tv_type1.setTextColor(position == 0 ? green : black);
            tv_type2.setTextColor(position == 1 ? green : black);
            tv_type3.setTextColor(position == 2 ? green : black);
            tv_type4.setTextColor(position == 3 ? green : black);
            ViewHelper.setTranslationX(indicate_line, lineWidth*position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            // 指示线偏移
            // ViewPropertyAnimator.animate(main_indicate_line).translationX(50);
            float disX = positionOffset * lineWidth;
            float startX = position * lineWidth;
            float endX = startX + disX;
            ViewHelper.setTranslationX(indicate_line, endX);// 直接改变属性(位移坐标)
            // ViewPropertyAnimator.animate(main_indicate_line).translationX(endX);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 获取优惠券信息
     */
//    private void getData() {
//        Map<String, String> params = new HashMap<>();
//        params.put("token", token);
//        StringCallback callback = new StringCallback() {
//
//            @Override
//            public void onResponse(String arg0, int arg1) {
//                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
//                Gson gson = new Gson();
//                CardResaultBean cardResult = null;
//                try {
//                    cardResult = gson.fromJson(
//                            arg0, CardResaultBean.class);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (cardResult == null) {
//                    ToastUtil.showTextToast(context, "获取优惠券失败！");
//                    return;
//                }
//                if (cardResult.success) {
//                    datas = cardResult.bankCardList;
//                    if (datas == null || datas.size() == 0) {
//                        rl_null.setVisibility(View.VISIBLE);
//                    } else {
//                        rl_null.setVisibility(View.GONE);
//                        adapter = new ZHAdapter(datas);
//                        lv_card.setAdapter(adapter);
//                    }
//                } else if (cardResult.flag == 10) {
//                    mApplication.login();
//                    // getData();
//                } else {
//                    ToastUtil
//                            .showTextToast(context, cardResult.msg);
//                }
//
//            }
//
//            @Override
//            public void onError(Call arg0, Exception arg1, int arg2) {
//                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
//                rl_error.setVisibility(View.VISIBLE);
//            }
//        };
//        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.GET_YHQ_LIST_URL, params, callback);
//
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
//            case R.id.rl_error:
//                mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
//                        "数据加载中...");
//                adapter = null;
////			isUpdate = true;
////			currPage = 1;
//                getData();
////			lv.doPullRefreshing(true, 500);
//                break;
            case R.id.btn_right:
                Intent intentYhq = new Intent(context, DHYhqActivity.class);
                startActivity(intentYhq);
                break;
            case R.id.ll_type1:

                break;
            case R.id.tv_type1:
                vp_detail.setCurrentItem(0);
                break;
            case R.id.tv_type2:
                vp_detail.setCurrentItem(1);
                break;
            case R.id.tv_type3:
                vp_detail.setCurrentItem(2);
                break;
            case R.id.tv_type4:
                vp_detail.setCurrentItem(3);
                break;
            default:
                break;
        }

    }


//    class ZHAdapter extends DefaultBaseAdapter<BankCardListBean> {
//
//        private ViewHolder holder;
//
//        public ZHAdapter(List<BankCardListBean> datas) {
//            super(datas);
//        }
//
//        @Override
//        public View getView(final int position, View convertView,
//                            ViewGroup parent) {
//            holder = null;
//            if (convertView == null) {
//                convertView = View
//                        .inflate(context, R.layout.item_my_card, null);
//                holder = new ViewHolder();
//                holder.iv_icon = (ImageView) convertView
//                        .findViewById(R.id.iv_icon);
//                holder.tv_card_number = (TextView) convertView
//                        .findViewById(R.id.tv_card_number);
//                // holder.tv_card_name = (TextView) convertView
//                // .findViewById(R.id.tv_card_name);
//                holder.iv_chioce = (ImageView) convertView
//                        .findViewById(R.id.iv_chioce);
//                holder.iv_sign = (ImageView) convertView
//                        .findViewById(R.id.iv_sign);
//
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            // 填充数据
//            holder.iv_icon.setImageResource(R.drawable.maincard_logo);
//
//            return convertView;
//        }
//    }
//
//    class ViewHolder {
//        // private TextView tv_card_name;
//        private TextView tv_card_number;
//        private ImageView iv_icon;
//        private ImageView iv_chioce;
//        private ImageView iv_sign;
//    }

}
