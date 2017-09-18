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
import com.ksource.hbpostal.bean.PushMsgResaultBean;
import com.ksource.hbpostal.bean.PushMsgResaultBean.MessageListBean;
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
 * 推送消息列表页面
 */
public class PushMsgActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private PullToRefreshListView lv;
    private ListView listView;
    private List<MessageListBean> datas;
    private BaseAdapter adapter;
    private RelativeLayout rl_error, rl_push_msg_null;

    private LoadDialog mLoadDialog;

    private boolean isUpdate;
    private int currPage;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_push_msg;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        rl_error = (RelativeLayout) findViewById(R.id.rl_error);
        rl_push_msg_null = (RelativeLayout) findViewById(R.id.rl_null);
        lv = (PullToRefreshListView) findViewById(R.id.lv_push_msg);

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
                lv.setHasMoreData(false);
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
                // 通知公告详情
                Intent intent = new Intent(context, BaseHtmlActivity.class);
                intent.putExtra("title", "推送消息");
                intent.putExtra("newsTitle", datas.get(position).MESSAGE_TITLE);
                String token = sp.getString(ConstantValues.TOKEN, "");
                intent.putExtra("url",
                        ConstantValues.PUSH_MSG_HTML_URL
                                + datas.get(position).MESSAGE_ID + "&token=" + token);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        datas = new ArrayList<>();
        tv_title.setText("推送消息");
        adapter = null;
        currPage = 1;
        lv.doPullRefreshing(true, 500);

    }

    private void getData() {
        Map<String, String> params = new HashMap<>();
        String token = sp.getString(ConstantValues.TOKEN, "");
        params.put("token", token);
        params.put("pageSize", "10");
        params.put("currPage", "" + currPage);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                PushMsgResaultBean msgResult = null;
                try {
                    msgResult = gson.fromJson(arg0,
                            PushMsgResaultBean.class);
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
                    datas.addAll(msgResult.messageList);
                    if (datas.size() == 0) {
                        rl_push_msg_null.setVisibility(View.VISIBLE);
                    } else {
                        rl_push_msg_null.setVisibility(View.GONE);
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
                    lv.setHasMoreData(msgResult.messageList.size() >= 10);
                } else if (msgResult.flag == 10) {
                    mApplication.login();
                } else {
                    ToastUtil.showTextToast(context, msgResult.msg);
                    rl_error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                rl_error.setVisibility(View.VISIBLE);
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_PUSH_MSG_URL, params, callback);
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

            default:
                break;
        }

    }

    class MyAdapter extends DefaultBaseAdapter<MessageListBean> {

        private ViewHolder holder;

        public MyAdapter(List<MessageListBean> datas) {
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

            holder.iv_icon.setVisibility(View.GONE);
            holder.tv_name.setText(datas.get(position).MESSAGE_TITLE);
            holder.tv_card_number.setText(TimeUtil.formatTimeMin(datas
                    .get(position).DEAL_TIME));

            try {
                ImageLoaderUtil.loadNetPic(
                        ConstantValues.BASE_URL + datas.get(position).IMAGE,
                        holder.iv_icon);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return convertView;
        }

    }

    class ViewHolder {
        // private TextView tv_card_name;
        private TextView tv_name;
        private ImageView iv_icon;
        private TextView tv_card_number;
    }

}
