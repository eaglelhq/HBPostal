package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.ToastUtils;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.widgets.ClearEditText;
import com.yitao.util.ToastUtil;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SearchActivity extends BaseActivity {

    private TextView tv_search;
    private ImageView iv_del;
    private ClearEditText cet_search;
    //	private Set<String> values= new HashSet<String>();
    private String history;
    private List<String> datas;

    //	private LinearLayout ll_history;
//	private FlowLayout fl_history;
    private TextView textView;
    //	private ScrollView scrollView;
    private TagFlowLayout flowlayout;
    private TagAdapter<String> adapter;


    @Override
    public int getLayoutResId() {
        return R.layout.activity_search;
    }

    @Override
    public void initView() {
        tv_search = (TextView) findViewById(R.id.tv_search);
        iv_del = (ImageView) findViewById(R.id.iv_del);
        cet_search = (ClearEditText) findViewById(R.id.cet_search);
        flowlayout = (TagFlowLayout) findViewById(R.id.flowlayout);
    }

    @Override
    public void initListener() {
        tv_search.setOnClickListener(this);
        iv_del.setOnClickListener(this);
        flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, com.zhy.view.flowlayout.FlowLayout parent) {
//				Toast.makeText(context, datas.get(position), Toast.LENGTH_SHORT).show();
                cet_search.setText(datas.get(position));
                return true;
            }
        });
//		fl_history.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				ToastUtil.showTextToast(context,"5454");
//			}
//		});
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }

    private void setData() {
//		Set<String> historySet = sp.getStringSet("searchHistory", null);
        datas.clear();
        history = sp.getString("searchHistory", "");
        if (!TextUtils.isEmpty(history)) {
            String[] historys = history.split("#");
            Collections.addAll(datas, historys);
            final LayoutInflater mInflater = LayoutInflater.from(context);
            adapter = new TagAdapter<String>(datas) {
                @Override
                public View getView(com.zhy.view.flowlayout.FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.tv,
                            flowlayout, false);
                    tv.setText(s);
                    return tv;
                }
            };
            flowlayout.setAdapter(adapter);
        }
    }

    @Override
    public void initData() {
        datas = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_del:
                if (datas == null || datas.size() == 0) {
                    ToastUtils.showShortToast("您还没有搜索历史！");
                    return;
                }
                SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                dialog.setTitleText("确定要删除搜索历史吗？")
                        .setConfirmText("删除")
                        .setCancelText("取消")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                datas.clear();
                                sp.edit().putString("searchHistory", null).apply();
                                if (adapter != null) {
                                    adapter.notifyDataChanged();
                                }
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();

                break;
            case R.id.tv_search:
                String keyWord = cet_search.getText().toString().trim();
                if (TextUtils.isEmpty(keyWord)) {
                    ToastUtil.showTextToast(context, "请输入关键字");
                    return;
                }
//			values.add(keyWord);
                if (!datas.contains(keyWord)) {
                    history = history + keyWord + "#";
                    sp.edit().putString("searchHistory", history).apply();
                }
                Intent intent = new Intent(context, CateGoryListActivity.class);
                intent.putExtra("keyWorld", keyWord);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

}
