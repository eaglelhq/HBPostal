package com.ksource.hbpostal.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.MyAdapter;
import com.ksource.hbpostal.bean.CateResultBean;
import com.ksource.hbpostal.bean.CateResultBean.CateListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.fragment.CategoryDetailFragment;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 商品分类 左侧一级列表，右侧商品
 */
public class ActivityCate extends BaseFragmentActivity implements OnItemClickListener {


    private String[] strs;
    private ListView listView;
    private MyAdapter adapter;
    private TextView tv_title;
    private ImageView iv_back;
    private CategoryDetailFragment myFragment;
    private LoadDialog mLoadDialog;
    private RelativeLayout rl_error;
    public static int mPosition;

    private List<CateListBean> firstCateList;
//    private List<CateListBean> secondCateList;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_category;
    }

    @Override
    public void initListener() {
        listView.setOnItemClickListener(this);
        rl_error.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void initView() {
        listView = (ListView) findViewById(R.id.listview);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("商品分类");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        rl_error = (RelativeLayout) findViewById(R.id.rl_error);
//        iv_back.setVisibility(View.GONE);

    }

    @Override
    public void initData() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "数据加载中...");
        getData("1", "");

        // 创建MyFragment对象
        // setFragment();
    }

    private void setFragment(String cateId) {
        myFragment = new CategoryDetailFragment();
        // FragmentActivity activity = getActivity();
        // if (activity == null) {
        // return;
        // }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, myFragment);
        // 通过bundle传值给MyFragment
        Bundle bundle = new Bundle();
//		bundle.putSerializable(CategoryDetailFragment.TAG,
//				(Serializable) secondCateList);
        bundle.putString("cateId", cateId);
        myFragment.setArguments(bundle);
        fragmentTransaction.commitAllowingStateLoss();
    }

    // 获取一级商品分类list
    private void getData(final String type, final String firstId) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("firstCateId", firstId);

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                CateResultBean baseBean = null;
                Gson gson = new Gson();
                try {
                    baseBean = gson.fromJson(arg0, CateResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (baseBean == null) {
                    rl_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (baseBean.success) {
                    rl_error.setVisibility(View.GONE);
                    if ("1".equals(type)) {
                        firstCateList = baseBean.cateList;
                        strs = new String[firstCateList.size()];
                        for (int i = 0; i < firstCateList.size(); i++) {
                            strs[i] = firstCateList.get(i).LV1_NAME;
                        }
                        adapter = new MyAdapter(context, strs);
                        listView.setAdapter(adapter);
                        setFragment(firstCateList.get(0).ID);
//						getData("2", firstCateList.get(mPosition).ID);

                    } else {
//                        secondCateList = baseBean.cateList;
//						setFragment();
                    }
                } else {
                    rl_error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                rl_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.CATEGREY_URL, params, callback);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (mPosition != position) {
            // 拿到当前位置
            mPosition = position;
            // 即使刷新adapter
            adapter.notifyDataSetChanged();
            setFragment(firstCateList.get(position).ID);
//		getData("2", firstCateList.get(position).ID);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_error:
                rl_error.setVisibility(View.VISIBLE);
                initData();
                break;
            case R.id.iv_back:
                finish();
                break;

            default:
                break;
        }

    }

//    private long mExitTime = 0;

//    @Override
//    public void onBackPressed() {
//        if ((System.currentTimeMillis() - mExitTime) > 2000) {
//            Toast.makeText(getApplicationContext(), "再按一次退出程序",
//                    Toast.LENGTH_SHORT).show();
//            mExitTime = System.currentTimeMillis();
//        } else {
//            finish();
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPosition = 0;
    }
}
