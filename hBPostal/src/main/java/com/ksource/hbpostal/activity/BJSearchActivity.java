package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.ksource.hbpostal.widgets.CustomPopWindow;
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
 * 八戒农场搜索页面
 * Created by Administrator on 2017/3/8 0008.
 */

public class BJSearchActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private PullToRefreshGridView ptrGv;
    private GridView gridView;
    private int currpage = 1;
    private RelativeLayout gv_item_error, gv_item_null;
    private BaseAdapter gridAdapter;
    private List<JPGoogsBean.GoodsListBean> mealDatas;
    private boolean isUpdate;
    private EditText et_search;
    private TextView btn_search,tv_sea_type;
//    private Spinner spinner;
//    private ArrayAdapter<String> adapter;
    private String keyWord;
    private int seaType;
    private List<String> list;
    private CustomPopWindow mCustomPopWindow;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_bj_search;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        gv_item_null = (RelativeLayout) findViewById(R.id.gv_item_null);
        gv_item_error = (RelativeLayout) findViewById(R.id.gv_item_error);

        et_search = (EditText) findViewById(R.id.et_search);
        btn_search = (TextView) findViewById(R.id.btn_search);
        tv_sea_type = (TextView) findViewById(R.id.tv_sea_type);
//        spinner = (Spinner) findViewById(R.id.spinner);
        // 得到控件
        ptrGv = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
        gridView = ptrGv.getRefreshableView();
        gridView.setNumColumns(2);
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        tv_sea_type.setOnClickListener(this);
        gv_item_error.setOnClickListener(this);
        btn_search.setOnClickListener(this);
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
                if (mealDatas.get(position).GOODS_TYPE == 4) {
                    Intent intent = new Intent(context, BJCardDetailActivity.class);
                    intent.putExtra("goodsId", mealDatas.get(position).ID);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, GoodsDetailActivity.class);
                    intent.putExtra("goodsId", mealDatas.get(position).ID);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void initData() {
        tv_title.setText("搜索");
        keyWord = getIntent().getStringExtra("keyWord");
        et_search.setText(keyWord);
        mealDatas = new ArrayList<>();
//        getData();
        ptrGv.doPullRefreshing(true, 500);

        list = new ArrayList<String>();
        list.add("全部");
        list.add("体验装");
        list.add("特色抢购");
        list.add("假日礼包");
        list.add("宅配卡");
//
//        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setDropDownVerticalOffset(SizeUtils.dp2px(30));
//        seaType = getIntent().getIntExtra("seaType", 0);
    }


    private void getData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageSize", "10");
        params.put("currPage", "" + currpage);
        if (seaType == 0)
            params.put("goods_type", "");
        else
            params.put("goods_type", seaType + "");
        params.put("goods_name", keyWord);

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
                if (goodsBean == null) {
                    gv_item_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (goodsBean.success) {
                    gv_item_error.setVisibility(View.GONE);
                    if (goodsBean.goodsList == null) {
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
                    ptrGv.setHasMoreData(goodsBean.goodsList.size() >= 10);
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
        DataUtil.doPostAESData(null,context,ConstantValues.BJ_SEARCH, params, callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_search:
                keyWord = et_search.getText().toString().trim();
                currpage = 1;
                getData();
                break;
            case R.id.gv_item_error:
                keyWord = et_search.getText().toString().trim();
                currpage = 1;
                getData();
                break;
            case R.id.tv_sea_type:
                showPopMenu();
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
                holder.tv_type = (TextView) convertView
                        .findViewById(R.id.tv_type);

                convertView.setTag(holder);
            } else {
                holder = (GridViewHolder) convertView.getTag();
            }

            // 填充数据  9781137455803
            int type = datas.get(position).GOODS_TYPE;
            if (type > 0 && type < 5) {
                holder.tv_type.setVisibility(View.VISIBLE);
                holder.tv_type.setText(list.get(type));
            }
            String text;
            text = "￥" + datas.get(position).PRICE + " ";
            holder.sale.setText(SpannableUtils.setTextSize(text, 1,
                    text.length() - 1, ConvertUtils.dp2px(14)));
            holder.name.setText(datas.get(position).NAME);
            holder.count.setText("售" + datas.get(position).SALE_NUM);
            try {
                ImageLoaderUtil.loadNetPic(ConstantValues.BASE_URL + datas.get(position).IMAGE,
                        holder.icon);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

    }

    private class GridViewHolder {
        private ImageView icon;
        private TextView name, sale, count, tv_type;
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
}
