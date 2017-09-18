package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshListView;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 八戒农场-体验装
 */
public class TiyanActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private PullToRefreshListView lv;
    private ListView listView;
    private List<JPGoogsBean.GoodsListBean> datas;
    private BaseAdapter adapter;
    private LoadDialog mLoadDialog;

    private RelativeLayout rl_error, rl_null;
    private boolean isUpdate;
    private int currPage;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_tiyan;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv = (PullToRefreshListView) findViewById(R.id.lv_tiyan);
        rl_error = (RelativeLayout) findViewById(R.id.rl_error);
        rl_null = (RelativeLayout) findViewById(R.id.rl_null);

    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        rl_error.setOnClickListener(this);
        lv.setPullRefreshEnabled(true);
        lv.setPullLoadEnabled(false);
        lv.setScrollLoadEnabled(true);
        listView = lv.getRefreshableView();
        listView.setDivider(null);
        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                isUpdate = true;
                currPage = 1;
                getData();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                isUpdate = false;
                currPage++;
                getData();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(context, GoodsDetailActivity.class);
                intent.putExtra("goodsId", datas.get(position).ID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        tv_title.setText("体验装");
        lv.doPullRefreshing(true,500);
        datas = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_error:
                mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                        "数据加载中...");
                adapter = null;
                currPage = 1;
                getData();
                break;
        }
    }

    // 获取商品列表
    private void getData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageSize", "10");
        params.put("currPage", "" + currPage);
        params.put("IS_NCP", "1");
        params.put("NCP_FL", "1");
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                Gson gson = new Gson();
                JPGoogsBean goodsBean = null;
                try {
                    goodsBean = gson.fromJson(arg0, JPGoogsBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (goodsBean == null) {
                    rl_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (goodsBean.success) {
                    rl_error.setVisibility(View.GONE);
                     if (isUpdate) {
                     datas.clear();
                     }
                    if (goodsBean.goodsList != null) {
                        datas.addAll(goodsBean.goodsList);
                    } else{
                        lv.setHasMoreData(false);
                    }
                    if (datas.size() == 0) {
                        rl_null.setVisibility(View.VISIBLE);
                    } else {
                        rl_null.setVisibility(View.GONE);
                        if (adapter == null) {
                            adapter = new MyAdapter(datas);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                     if (isUpdate) {
                     listView.setSelection(0);
                     }
                    listView.setVisibility(View.VISIBLE);
                    lv.onPullDownRefreshComplete();
                    lv.onPullUpRefreshComplete();
                    lv.setHasMoreData(goodsBean.goodsList != null && goodsBean.goodsList.size() < 10 ? false
                            : true);
                } else if (goodsBean.flag == 10) {
                    mApplication.login();
                } else {
                    rl_error.setVisibility(View.VISIBLE);
                    ToastUtil.showTextToast(context, goodsBean.msg);
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                rl_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.JPTJ_URL, params, callback);
    }

    class MyAdapter extends DefaultBaseAdapter<JPGoogsBean.GoodsListBean> {

        private ViewHolder holder;

        public MyAdapter(List<JPGoogsBean.GoodsListBean> datas) {
            super(datas);
        }

        @SuppressWarnings("finally")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_farm_goods,
                        null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView
                        .findViewById(R.id.iv_goods);
                holder.tv_name = (TextView) convertView
                        .findViewById(R.id.tv_goods_name);
                holder.tv_goods_price = (TextView) convertView
                        .findViewById(R.id.tv_goods_price);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_name.setText(datas.get(position).NAME);
            String text = "￥" + datas.get(position).PRICE + " ";
            holder.tv_goods_price.setText(SpannableUtils.setTextSize(text, 1,
                    text.length() - 1, ConvertUtils.dp2px(14)));

            // holder.tv_goods_price.setText("￥"+datas.get(position).PRICE);

            try {
                ImageLoaderUtil.loadNetPic(
                        ConstantValues.BASE_URL + datas.get(position).IMAGE,
                        holder.iv_icon);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                return convertView;
            }
        }

    }

    class ViewHolder {
        // private TextView tv_card_name;
        private TextView tv_name;
        private ImageView iv_icon;
        private TextView tv_goods_price;
    }
}
