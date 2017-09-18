package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.ConvertUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.JPGoogsBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.util.SpannableUtils;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshGridView;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 八戒农场-特色抢购页面
 * Created by Administrator on 2017/3/8 0008.
 */

public class QiangActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private PullToRefreshGridView ptrGv;
    private GridView gridView;
    private int currpage = 1;
    private RelativeLayout gv_item_error, gv_item_null;
    private BaseAdapter gridAdapter;
    private List<JPGoogsBean.GoodsListBean> mealDatas;
    private boolean isUpdate;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_qiang;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        gv_item_null = (RelativeLayout) findViewById(R.id.gv_item_null);
        gv_item_error = (RelativeLayout) findViewById(R.id.gv_item_error);
        // 得到控件
        ptrGv = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
        gridView = ptrGv.getRefreshableView();
        gridView.setNumColumns(2);
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        ptrGv.setPullRefreshEnabled(true);
        ptrGv.setPullLoadEnabled(false);
        ptrGv.setScrollLoadEnabled(true);
        ptrGv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                isUpdate = true;
                currpage = 1;
                if (mealDatas != null) {
                    mealDatas.clear();
                }
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                isUpdate = false;
                currpage++;
                getData();
            }
        });
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
        tv_title.setText("特色抢购");
        mealDatas = new ArrayList<>();
//        getData();
        ptrGv.doPullRefreshing(true, 500);
    }


    private void getData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageSize", "10");
        params.put("currPage", "" + currpage);
        params.put("IS_NCP", "1");
        params.put("NCP_FL", "2");

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
//                gridView.onRefreshComplete();
                Gson gson = new Gson();
                JPGoogsBean goodsBean = null;
                try {
                    goodsBean = gson.fromJson(arg0,
                            JPGoogsBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (goodsBean == null ) {
                    gv_item_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (goodsBean.success) {
                    gv_item_error.setVisibility(View.GONE);
                    if (goodsBean.goodsList == null ) {
                        gv_item_null.setVisibility(View.VISIBLE);
                        return;
                    }
                    if (isUpdate) {
                        mealDatas.clear();
                    }
                    mealDatas.addAll(goodsBean.goodsList);
                    if (mealDatas.size() == 0) {
                        gv_item_null.setVisibility(View.VISIBLE);
                    } else {
                        gv_item_null.setVisibility(View.GONE);
                        if (gridAdapter == null) {
                            gridAdapter = new GridAdapter(mealDatas);
                            gridView.setAdapter(gridAdapter);
                        } else {
                            gridAdapter.notifyDataSetChanged();
                        }
                    }
                    ptrGv.onPullDownRefreshComplete();
                    ptrGv.onPullUpRefreshComplete();
                    ptrGv.setHasMoreData(goodsBean.goodsList.size() < 10 ? false
                            : true);
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
}
