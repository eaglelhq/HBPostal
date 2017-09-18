package com.ksource.hbpostal.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.FAQResaultBean;
import com.ksource.hbpostal.bean.FAQResaultBean.CateListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 常见问题列表，两级可折叠列表
 */
public class FAQActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;

	private ExpandableListView expandableListView;
	private LoadDialog mLoadDialog;
	private RelativeLayout rl_error, rl_faq_null;
	// private MyExpandableListViewAdapter adapter;

	private List<CateListBean> datas;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_faq;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		rl_error = (RelativeLayout) findViewById(R.id.rl_error);
		rl_faq_null = (RelativeLayout) findViewById(R.id.rl_faq_null);
		expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
		expandableListView.setGroupIndicator(null);
		datas = new ArrayList<>();
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		rl_error.setOnClickListener(this);
		// 监听组点击
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if (datas.get(groupPosition).ejTree == null
						|| datas.get(groupPosition).ejTree.size() == 0) {
					return true;
				}
				return false;
			}
		});

		// 监听每个分组里子控件的点击事件
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				Intent intent = new Intent(context, FAQListActivity.class);
				intent.putExtra("id",
						datas.get(groupPosition).ejTree.get(childPosition).ID);
				startActivity(intent);

				return false;
			}
		});

		// adapter = new MyExpandableListViewAdapter(this);
		// expandableListView.setAdapter(adapter);
	}

	@Override
	public void initData() {
		tv_title.setText("常见问题");
		getData();
	}

	// 获取数据
	private void getData() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"数据加载中...");
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);

				Gson gson = new Gson();
				FAQResaultBean faqResult = null;
				try {
					faqResult = gson.fromJson(arg0,
							FAQResaultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (faqResult == null) {
					rl_error.setVisibility(View.VISIBLE);
					return;
				}
				if (faqResult.success) {
					rl_error.setVisibility(View.GONE);
					// datas = cardResult.bankCardList;
					// adapter = new ZHAdapter(datas);
					// listView.setAdapter(adapter);
					datas = faqResult.cateList;
					if (datas == null || datas.size() == 0) {
						rl_faq_null.setVisibility(View.VISIBLE);
					} else {
						rl_faq_null.setVisibility(View.GONE);
						expandableListView
								.setAdapter(new MyExpandableListViewAdapter(
										context));
					}
				} else {
					if (faqResult.flag == 10) {
						mApplication.login();
					}
					ToastUtil.showTextToast(context, faqResult.msg);
					rl_error.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				rl_error.setVisibility(View.VISIBLE);
			}
		};
		DataUtil.doGetData(mLoadDialog, context, ConstantValues.CJWT_URL, callback);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_error:
			getData();
			break;

		default:
			break;
		}

	}

	// 用过ListView的人一定很熟悉，只不过这里是BaseExpandableListAdapter
	class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

		private Context context;

		public MyExpandableListViewAdapter(Context context) {
			this.context = context;
		}

		/**
		 * 
		 * 获取组的个数
		 * 
		 * @return
		 * @see android.widget.ExpandableListAdapter#getGroupCount()
		 */
		@Override
		public int getGroupCount() {
			return datas.size();
		}

		/**
		 * 
		 * 获取指定组中的子元素个数
		 * 
		 * @param groupPosition
		 * @return
		 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			return datas.get(groupPosition).ejTree.size();
		}

		/**
		 * 
		 * 获取指定组中的数据
		 * 
		 * @param groupPosition
		 * @return
		 * @see android.widget.ExpandableListAdapter#getGroup(int)
		 */
		@Override
		public Object getGroup(int groupPosition) {
			return datas.get(groupPosition);
		}

		/**
		 * 
		 * 获取指定组中的指定子元素数据。
		 * 
		 * @param groupPosition
		 * @param childPosition
		 * @return
		 * @see android.widget.ExpandableListAdapter#getChild(int, int)
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return datas.get(groupPosition).ejTree.get(childPosition);
		}

		/**
		 * 
		 * 获取指定组的ID，这个组ID必须是唯一的
		 * 
		 * @param groupPosition
		 * @return
		 * @see android.widget.ExpandableListAdapter#getGroupId(int)
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/**
		 * 
		 * 获取指定组中的指定子元素ID
		 * 
		 * @param groupPosition
		 * @param childPosition
		 * @return
		 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/**
		 * 
		 * 组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
		 * 
		 * @return
		 * @see android.widget.ExpandableListAdapter#hasStableIds()
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}

		/**
		 * 
		 * 获取显示指定组的视图对象
		 * 
		 * @param groupPosition
		 *            组位置
		 * @param isExpanded
		 *            该组是展开状态还是伸缩状态
		 * @param convertView
		 *            重用已有的视图对象
		 * @param parent
		 *            返回的视图对象始终依附于的视图组
		 * @return
		 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
		 *      View, ViewGroup)
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHolder groupHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.expendlist_group, null);
				groupHolder = new GroupHolder();
				groupHolder.txt = (TextView) convertView.findViewById(R.id.txt);
				groupHolder.img = (ImageView) convertView
						.findViewById(R.id.img);
				convertView.setTag(groupHolder);
			} else {
				groupHolder = (GroupHolder) convertView.getTag();
			}

			if (!isExpanded) {
				groupHolder.img.setBackgroundResource(R.drawable.common_more);
				// groupHolder.img.setBackgroundResource(R.drawable.rightarrow);
			} else {
				groupHolder.img
						.setBackgroundResource(R.drawable.city_small_icon);
				// groupHolder.img.setBackgroundResource(R.drawable.downarrow);
			}

			groupHolder.txt.setText(datas.get(groupPosition).LV1_NAME);
			return convertView;
		}

		/**
		 * 
		 * 获取一个视图对象，显示指定组中的指定子元素数据。
		 * 
		 * @param groupPosition
		 *            组位置
		 * @param childPosition
		 *            子元素位置
		 * @param isLastChild
		 *            子元素是否处于组中的最后一个
		 * @param convertView
		 *            重用已有的视图(View)对象
		 * @param parent
		 *            返回的视图(View)对象始终依附于的视图组
		 * @return
		 * @see android.widget.ExpandableListAdapter#getChildView(int, int,
		 *      boolean, View, ViewGroup)
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ItemHolder itemHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.expendlist_item, null);
				itemHolder = new ItemHolder();
				itemHolder.txt = (TextView) convertView.findViewById(R.id.txt);
				convertView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) convertView.getTag();
			}
			itemHolder.txt.setText(datas.get(groupPosition).ejTree
					.get(childPosition).LV2_NAME);
			return convertView;
		}

		/**
		 * 
		 * 是否选中指定位置上的子元素。
		 * 
		 * @param groupPosition
		 * @param childPosition
		 * @return
		 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	class GroupHolder {
		public TextView txt;
		public ImageView img;
	}

	class ItemHolder {
		public TextView txt;
	}

}
