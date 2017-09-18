package com.ksource.hbpostal.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.ksource.hbpostal.activity.GoodsDetailActivity;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.SearchResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.util.SpannableUtils;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshGridView;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class CategoryDetailFragment extends BaseFragment {
    public static final String TAG = "MyFragment";
    private List<SearchResultBean.GoodsListBean> mealDatas;
    private PullToRefreshGridView ptrGv;
    private GridView gridView;
    private int currpage = 1;
    private RelativeLayout gv_item_error, gv_item_null;
    private BaseAdapter gridAdapter;
    private boolean isUpdate;
    private LoadDialog mLoadDialog;
    private String cateId;


    @Override
    public View initView() {
        return View.inflate(context, R.layout.fragment_category_detail, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initData() {
        mealDatas = new ArrayList<>();
        gv_item_null = (RelativeLayout) view.findViewById(R.id.gv_item_null);
        gv_item_error = (RelativeLayout) view.findViewById(R.id.gv_item_error);
        ptrGv = (PullToRefreshGridView) view.findViewById(R.id.pull_refresh_grid);
        gridView = ptrGv.getRefreshableView();
        gridView.setNumColumns(2);
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
                getTypeList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                isUpdate = false;
                currpage++;
                getTypeList();
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
        getTypeList();

    }


    //获取商品列表
    private void getTypeList() {
//        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
        Bundle bundle = getArguments();
//		mealDatas = (List<CateListBean>) bundle.getSerializable(TAG);
        cateId = bundle.getString("cateId");
        Map<String, String> params = new HashMap<String, String>();
        params.put("cateId", cateId);
        params.put("cateLvl", "1");
        params.put("currPage", "" + currpage);
        params.put("pageSize", "10");

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                Gson gson = new Gson();
                SearchResultBean result = null;
                try {
                    result = gson.fromJson(arg0,
                            SearchResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result == null) {
                    gv_item_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (result.success) {
                    gv_item_error.setVisibility(View.GONE);
                    if (result.goodsList != null) {

                        if (isUpdate) {
                            mealDatas.clear();
                        }
                        mealDatas.addAll(result.goodsList);
                        if (gridAdapter == null) {
                            gridAdapter = new MyGridAdapter(mealDatas);
                            gridView.setAdapter(gridAdapter);
                        } else {
                            gridAdapter.notifyDataSetChanged();
                        }
                        if (isUpdate) {
                            gridView.setSelection(0);
                        }
                        ptrGv.onPullDownRefreshComplete();
                        ptrGv.onPullUpRefreshComplete();
                        ptrGv.setHasMoreData(result.goodsList.size() < 10 ? false
                                : true);
                    }
                    if (mealDatas == null || mealDatas.size() == 0) {
                        gv_item_null.setVisibility(View.VISIBLE);
                    } else {
                        gv_item_null.setVisibility(View.GONE);
                    }
                } else {
                    gv_item_error.setVisibility(View.VISIBLE);
                    ToastUtil.showTextToast(context, result.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                gv_item_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GOODS_SEARCH_URL, params, callback);

    }


    class MyGridAdapter extends DefaultBaseAdapter<SearchResultBean.GoodsListBean> {

        public MyGridAdapter(List<SearchResultBean.GoodsListBean> mealDatas) {
            super(mealDatas);
        }

        @SuppressWarnings("finally")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.list_pro_type_item, null);
                holder = new GridHolder();
                holder.icon = (ImageView) convertView
                        .findViewById(R.id.typeicon);
                holder.name = (TextView) convertView
                        .findViewById(R.id.typename);
                holder.price = (TextView) convertView
                        .findViewById(R.id.price);

                convertView.setTag(holder);
            } else {
                holder = (GridHolder) convertView.getTag();
            }

            // 填充数据
            holder.name.setText(mealDatas.get(position).NAME);
            int type = datas.get(position).BUY_TYPE;
            String text;
            switch (type) {
                case 1:
                    text = "￥" + datas.get(position).PRICE+" ";
                    holder.price.setText(SpannableUtils.setTextSize(text, 1, text.length()-1, ConvertUtils.dp2px(14)));

                    break;
                case 2:
                    text = "￥" + datas.get(position).PRICE+ "+" + datas.get(position).INTEGRAL
                            + "积分";
                    holder.price.setText(SpannableUtils.setTextSize(text, 1, text.length()-2, ConvertUtils.dp2px(14)));

                    break;
                case 3:
                    text = datas.get(position).INTEGRAL + "积分";
                    holder.price.setText(SpannableUtils.setTextSize(text, 0, text.length()-2, ConvertUtils.dp2px(14)));

                    break;

                default:
                    break;
            }
//            holder.price.setText(mealDatas.get(position).PRICE);
            try {
                ImageLoaderUtil.loadNetPic(ConstantValues.BASE_URL + mealDatas.get(position).IMAGE, holder.icon);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return convertView;
        }

    }

    private class GridHolder {
        private ImageView icon;
        private TextView name;
        private TextView price;
    }

}
