package com.ksource.hbpostal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.MyApplication;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.MainViewpagerAdapter;
import com.ksource.hbpostal.bean.HomeResultBean;
import com.ksource.hbpostal.bean.HomeResultBean.HomeListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.fragment.ItemFragment;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 生活缴费页面
 */
public class SHJFActivity extends FragmentActivity implements OnClickListener {

    private TextView tv_title, tv_right;
    private ImageView iv_back;
    private ImageView iv_add_user;
    private Context context;
    private MyApplication mApplication;
    private RelativeLayout rl_error, rl_null;

    private List<HomeListBean> homeList;
    // 缴费类型
    private int type;
    private SharedPreferences sp;

    private LoadDialog mLoadDialog;
    // private TabPageIndicator indicator;

    List<Fragment> datas = new ArrayList<>();
    private MainViewpagerAdapter adapter;
    private TextView tv_item1, tv_item2, tv_item3, tv_item4, tv_item5;
    private ViewPager pager;
    private static int currItem;
    private boolean canAdd; //是否可以新增

    // private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        context = this;
        mApplication = MyApplication.getInstance();
        mApplication.addActivity(this);
        sp = getSharedPreferences(ConstantValues.SP_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_shjf);
        initView();
        initListener();
        getHome();
    }

    public void initView() {
        homeList = new ArrayList<>();
        type = getIntent().getIntExtra("type", 1);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("生活缴费");
        tv_right = (TextView) findViewById(R.id.btn_right);
        tv_right.setText("缴费记录");
        tv_right.setVisibility(View.VISIBLE);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_add_user = (ImageView) findViewById(R.id.iv_edit_user);
        pager = (ViewPager) findViewById(R.id.pager);
        tv_item1 = (TextView) findViewById(R.id.tv_item1);
        tv_item2 = (TextView) findViewById(R.id.tv_item2);
        tv_item3 = (TextView) findViewById(R.id.tv_item3);
        tv_item4 = (TextView) findViewById(R.id.tv_item4);
        tv_item5 = (TextView) findViewById(R.id.tv_item5);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);
        // indicator = (TabPageIndicator) findViewById(R.id.indicator);
    }

    public void initListener() {
        pager.setOnPageChangeListener(new OnMainPageChangeListener());
        tv_item1.setOnClickListener(this);
        tv_item2.setOnClickListener(this);
        tv_item3.setOnClickListener(this);
        tv_item4.setOnClickListener(this);
        tv_item5.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        iv_add_user.setOnClickListener(this);
        // indicator.setVisibility(View.GONE);
    }



    // 获取家庭集合
    private void getHome() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
        Map<String, String> params = new HashMap<>();
        String token = sp.getString(ConstantValues.TOKEN, "");
        params.put("token", token);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                HomeResultBean homeResultBean = null;
                Gson gson = new Gson();
                try {
                    homeResultBean = gson.fromJson(arg0,
                            HomeResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (homeResultBean == null) {
                    ToastUtil.showTextToast(context, "获取家庭失败！");
                    rl_null.setVisibility(View.VISIBLE);
                } else {
                    if (homeResultBean.success) {
                        if (homeResultBean.homeList == null) {
                            return;
                        }
                        datas.clear();
                        homeList = homeResultBean.homeList;
                        if (homeList.size() == 0) {
                            rl_null.setVisibility(View.VISIBLE);
                        } else {
                            rl_null.setVisibility(View.GONE);
                            canAdd = homeList.size() < 5;
                            for (int i = 0; i < homeList.size(); i++) {
                                switch (i) {
                                    case 0:
                                        tv_item1.setText(homeList.get(i).HOME_NAME);
                                        tv_item1.setVisibility(View.VISIBLE);
                                        tv_item2.setVisibility(View.GONE);
                                        tv_item3.setVisibility(View.GONE);
                                        tv_item4.setVisibility(View.GONE);
                                        tv_item5.setVisibility(View.GONE);
                                        break;
                                    case 1:
                                        tv_item2.setText(homeList.get(i).HOME_NAME);
                                        tv_item1.setVisibility(View.VISIBLE);
                                        tv_item2.setVisibility(View.VISIBLE);
                                        tv_item3.setVisibility(View.GONE);
                                        tv_item4.setVisibility(View.GONE);
                                        tv_item5.setVisibility(View.GONE);
                                        break;
                                    case 2:
                                        tv_item3.setText(homeList.get(i).HOME_NAME);
                                        tv_item1.setVisibility(View.VISIBLE);
                                        tv_item2.setVisibility(View.VISIBLE);
                                        tv_item3.setVisibility(View.VISIBLE);
                                        tv_item4.setVisibility(View.GONE);
                                        tv_item5.setVisibility(View.GONE);
                                        break;
                                    case 3:
                                        tv_item4.setText(homeList.get(i).HOME_NAME);
                                        tv_item1.setVisibility(View.VISIBLE);
                                        tv_item2.setVisibility(View.VISIBLE);
                                        tv_item3.setVisibility(View.VISIBLE);
                                        tv_item4.setVisibility(View.VISIBLE);
                                        tv_item5.setVisibility(View.GONE);
                                        break;
                                    case 4:
                                        tv_item5.setText(homeList.get(i).HOME_NAME);
                                        tv_item1.setVisibility(View.VISIBLE);
                                        tv_item2.setVisibility(View.VISIBLE);
                                        tv_item3.setVisibility(View.VISIBLE);
                                        tv_item4.setVisibility(View.VISIBLE);
                                        tv_item5.setVisibility(View.VISIBLE);
                                        break;

                                    default:
                                        break;
                                }
                                ItemFragment fragment = new ItemFragment();
                                Bundle args = new Bundle();
                                args.putString("homeId",
                                        homeList.get(i).ID);
                                args.putString("homeName",
                                        homeList.get(i).HOME_NAME);
                                args.putInt("type", type);
                                fragment.setArguments(args);
                                datas.add(fragment);
                            }
                            // 添加Viewpager适配器
                            adapter = new MainViewpagerAdapter(
                                    getSupportFragmentManager(), datas);
                            pager.setAdapter(adapter);
                            pager.setCurrentItem(currItem);
                        }
                    } else {
                        rl_null.setVisibility(View.VISIBLE);
                        if (homeResultBean.flag == 10) {
                            mApplication.login();
                        } else {
                            ToastUtil.showTextToast(context, homeResultBean.msg);
                        }
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取家庭失败！");
                rl_null.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_MEMBER_HOME_URL, params, callback);
    }

    private final class OnMainPageChangeListener implements
            OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            // 1. 标签颜色改变
            currItem = position;
            int green = getResources().getColor(R.color.green);
            int black = getResources().getColor(R.color.gary);
            @SuppressWarnings("deprecation")
            Drawable bgChioce = getResources().getDrawable(
                    R.drawable.base_tabpager_indicator_selected);
            tv_item1.setTextColor(position == 0 ? green : black);
            tv_item2.setTextColor(position == 1 ? green : black);
            tv_item3.setTextColor(position == 2 ? green : black);
            tv_item4.setTextColor(position == 3 ? green : black);
            tv_item5.setTextColor(position == 4 ? green : black);
            tv_item1.setBackground(position == 0 ? bgChioce : null);
            tv_item2.setBackground(position == 1 ? bgChioce : null);
            tv_item3.setBackground(position == 2 ? bgChioce : null);
            tv_item4.setBackground(position == 3 ? bgChioce : null);
            tv_item5.setBackground(position == 4 ? bgChioce : null);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            currItem = position;

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                startActivity(new Intent(context, HistoryActivity.class));
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_edit_user:
                Intent intent = new Intent(context, EditUserActivity.class);
                intent.putExtra("homeId", homeList.get(currItem).ID);
                intent.putExtra("canAdd", canAdd);
                startActivity(intent);
                break;
            case R.id.tv_item1:
                if (homeList.size() > 0) {
                    currItem = 0;
                    pager.setCurrentItem(0);
                }
                break;
            case R.id.tv_item2:
                if (homeList.size() > 1) {
                    currItem = 1;
                    pager.setCurrentItem(1);
                }
                break;
            case R.id.tv_item3:
                if (homeList.size() > 2) {
                    currItem = 2;
                    pager.setCurrentItem(2);
                }
                break;
            case R.id.tv_item4:
                if (homeList.size() > 3) {
                    currItem = 3;
                    pager.setCurrentItem(3);
                }
                break;
            case R.id.tv_item5:
                if (homeList.size() > 4) {
                    currItem = 4;
                    pager.setCurrentItem(4);
                }
                break;

            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getHome();
    }
}
