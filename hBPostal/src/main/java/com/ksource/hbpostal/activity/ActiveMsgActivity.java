package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.ActiveMsgResaultBean;
import com.ksource.hbpostal.bean.ActiveMsgResaultBean.HdxxListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.util.TimeUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.pulltorefresh.PullToRefreshBase;
import com.yitao.pulltorefresh.PullToRefreshBase.OnRefreshListener;
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
 * 互动交流-活动信息列表
 */
public class ActiveMsgActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private PullToRefreshListView lv;
    private ListView listView;
    private List<HdxxListBean> datas;
    private BaseAdapter adapter;
    private LoadDialog mLoadDialog;
    private RelativeLayout rl_error, rl_active_msg_null;

    private boolean isUpdate;
    private int currPage;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_active_msg;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv = (PullToRefreshListView) findViewById(R.id.lv_active_msg);
        rl_active_msg_null = (RelativeLayout) findViewById(R.id.rl_null);
        rl_error = (RelativeLayout) findViewById(R.id.rl_error);

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
        lv.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                isUpdate = true;
                lv.onPullDownRefreshComplete();
                lv.onPullUpRefreshComplete();
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
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(context, HTMLActivity.class);
                intent.putExtra("title", "活动信息");
                intent.putExtra("newsTitle", datas.get(position).TITLE);
                intent.putExtra("url",
                        ConstantValues.HDXX_HTML_URL + datas.get(position).ID);
                // 活动信息详情
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        datas = new ArrayList<>();
        tv_title.setText("活动信息");
        adapter = null;
        currPage = 1;
		lv.doPullRefreshing(true, 500);
//        getData();
    }

    private void getData() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("pageSize", "10");
        params.put("currPage", "" + currPage);

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ActiveMsgResaultBean msgResult = null;
                Gson gson = new Gson();
                try {
                    msgResult = gson.fromJson(arg0,
                            ActiveMsgResaultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (msgResult == null) {
                    rl_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (msgResult.success) {
                    rl_error.setVisibility(View.GONE);
                    if (isUpdate) {
                        datas.clear();
                    }
                    datas.addAll(msgResult.hdxxList);
                    if (datas.size() == 0) {
                        rl_active_msg_null
                                .setVisibility(View.VISIBLE);
                    } else {
                        rl_active_msg_null.setVisibility(View.GONE);
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
                    lv.setHasMoreData(msgResult.hdxxList.size() >= 10);
                } else if (msgResult.flag == 10) {
                    mApplication.login();
                } else {
                    rl_error.setVisibility(View.VISIBLE);
                    ToastUtil.showTextToast(context, msgResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                lv.onPullDownRefreshComplete();
                lv.onPullUpRefreshComplete();
                lv.setHasMoreData(false);
                rl_error.setVisibility(View.VISIBLE);
            }
        };

        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.HDXX_URL, params, callback);
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
                isUpdate = true;
                currPage = 1;
                getData();
//                lv.doPullRefreshing(true, 500);
                break;

            default:
                break;
        }

    }

    class MyAdapter extends DefaultBaseAdapter<HdxxListBean> {

        private ViewHolder holder;

        public MyAdapter(List<HdxxListBean> datas) {
            super(datas);
        }

        @SuppressWarnings("finally")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_news_msg,
                        null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView
                        .findViewById(R.id.iv_icon);
                holder.tv_card_number = (TextView) convertView
                        .findViewById(R.id.tv_card_number);
                holder.tv_name = (TextView) convertView
                        .findViewById(R.id.tv_name);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_name.setText(datas.get(position).TITLE);
            holder.tv_card_number.setText(TimeUtil.formatTimeMin(datas
                    .get(position).UPDATE_TIME));

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
        private TextView tv_card_number;
    }

}
