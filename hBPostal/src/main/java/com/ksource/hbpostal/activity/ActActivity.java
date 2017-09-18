package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.TimeUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.BJNCNewsResaultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.util.TimeUtil;
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
 * 农产品活动信息列表
 */
public class ActActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private PullToRefreshListView lv;
    private ListView listView;
    private List<BJNCNewsResaultBean.NewsListBean> datas;
    private BaseAdapter adapter;
    private LoadDialog mLoadDialog;
    private RelativeLayout rl_error, rl_active_msg_null;

    private boolean isUpdate;
    private int currPage;
    private String token;

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
        token = sp.getString(ConstantValues.TOKEN,null);
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
                long deadline = datas.get(position).deadline;
                String nowTime = TimeUtils.getNowTimeString("yyyyMMddHHmmss");
                long nowline = Long.parseLong(nowTime);
                final boolean ing = deadline > nowline ? true : false;
                Intent intent = new Intent(context, BaseHtmlActivity.class);
                intent.putExtra("title", "活动信息");
                intent.putExtra("act_id", datas.get(position).id);
                if (ing){
                    intent.putExtra("isSign", datas.get(position).isSign == 1 ? true : false);
                }else {
                    intent.putExtra("isSign", true);
                }
                intent.putExtra("deadline", datas.get(position).deadline);
                intent.putExtra("newsTitle", datas.get(position).title);
                intent.putExtra("url",
                        ConstantValues.NPC_ACT_URL + datas.get(position).id);
                // 活动信息详情
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        datas = new ArrayList<>();
        tv_title.setText("活动信息");
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = null;
        isUpdate = true;
        currPage = 1;
        getData();
//        lv.doPullRefreshing(true, 500);
    }

    private void getData() {
        final Map<String, String> params = new HashMap<String, String>();
        String token = sp.getString(ConstantValues.TOKEN, null);
        params.put("token", token);
        params.put("pageSize", "10");
        params.put("currPage", "" + currPage);

        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                BJNCNewsResaultBean msgResult = null;
                Gson gson = new Gson();
                try {
                    msgResult = gson.fromJson(arg0,
                            BJNCNewsResaultBean.class);
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
                    datas.addAll(msgResult.newsList);
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
                    lv.setHasMoreData(msgResult.newsList.size() >= 10);
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
                rl_error.setVisibility(View.VISIBLE);
            }
        };

        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.BJ_ACT_LIST, params, callback);
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

    class MyAdapter extends DefaultBaseAdapter<BJNCNewsResaultBean.NewsListBean> {

        private ViewHolder holder;

        public MyAdapter(List<BJNCNewsResaultBean.NewsListBean> datas) {
            super(datas);
        }

        @SuppressWarnings("finally")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_act_msg,
                        null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView
                        .findViewById(R.id.iv_icon);
                holder.tv_card_number = (TextView) convertView
                        .findViewById(R.id.tv_card_number);
                holder.tv_name = (TextView) convertView
                        .findViewById(R.id.tv_name);
                holder.btn_sub = (TextView) convertView
                        .findViewById(R.id.btn_sub);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.tv_name.setText(datas.get(position).title);
            final boolean isSign = datas.get(position).isSign == 1 ? true : false;
            long deadline = datas.get(position).deadline;
            holder.tv_card_number.setText("截止时间:" + TimeUtil.formatTimeMin(deadline + ""));
            String nowTime = TimeUtils.getNowTimeString("yyyyMMddHHmmss");
            long nowline = Long.parseLong(nowTime);
            final boolean ing = deadline > nowline ? true : false;
            if (ing) {
                if (isSign) {
                    holder.btn_sub.setBackgroundResource(R.drawable.rect_lightgary);
                    holder.btn_sub.setText("已报名");
                } else {
                    holder.btn_sub.setBackgroundResource(R.drawable.rect_lightgreen);
                    holder.btn_sub.setText("报名");
                }
            } else {
                holder.btn_sub.setBackgroundResource(R.drawable.rect_lightgary);
                holder.btn_sub.setText("已结束");
            }
            holder.btn_sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ing) {
                        if (isSign) {
                            ToastUtil.showTextToast(context, "该活动您已报名！");
                        } else {
                            if (TextUtils.isEmpty(token)){
                                ToastUtil.showTextToast(context,"登录过后才可以报名！");
                                Intent intent = new Intent(context,LoginActivity.class);
                                intent.putExtra("isBack",true);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, EnlistActivity.class);
                                intent.putExtra("actId", datas.get(position).id);
                                intent.putExtra("title", datas.get(position).title);
                                intent.putExtra("deadline", datas.get(position).deadline + "");
                                startActivity(intent);
                            }
                        }
                    } else {
                        ToastUtil.showTextToast(context, "该活动已结束！");
                    }
                }
            });


            try {
                ImageLoaderUtil.loadNetPic(
                        ConstantValues.BASE_URL + datas.get(position).image,
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
        private TextView btn_sub;
    }
}
