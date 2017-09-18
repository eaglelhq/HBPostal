package com.ksource.hbpostal.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksource.hbpostal.MyApplication;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.MainViewpagerAdapter;
import com.ksource.hbpostal.fragment.AllOrderFragment;
import com.ksource.hbpostal.fragment.DfhOrderFragment;
import com.ksource.hbpostal.fragment.DpjOrderFragment;
import com.ksource.hbpostal.fragment.DshOrderFragment;
import com.ksource.hbpostal.fragment.DzfOrderFragment;
import com.ksource.hbpostal.widgets.LazyViewPager;
import com.ksource.hbpostal.widgets.LazyViewPager.OnPageChangeListener;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的订单页面
 */
public class OrderActivity extends FragmentActivity implements OnClickListener {

	private TextView tv_title;
	private ImageView iv_back;
	private Context context;
	private MyApplication mApplication;
	
	// 数据源
	List<Fragment> datas = new ArrayList<Fragment>();
	private MainViewpagerAdapter adapter;
	private TextView tv_all, tv_dzf, tv_dfh,tv_dsh,tv_dpj;
	private LazyViewPager vp_detail;
	private View indicate_line;
	private int lineWidth;// 指示线宽度

	private int orderItem;

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		context = this;
		mApplication = MyApplication.getInstance();
		mApplication.addActivity(this);
		setContentView(R.layout.activity_order);
		initView();
		initListener();
		initData();
	}

	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("我的订单");
		iv_back = (ImageView) findViewById(R.id.iv_back);
		// 标签:
		tv_all = (TextView) findViewById(R.id.tv_all);
		tv_dzf = (TextView) findViewById(R.id.tv_success);
		tv_dfh = (TextView) findViewById(R.id.tv_dfh);
		tv_dsh = (TextView) findViewById(R.id.tv_dsh);
		tv_dpj = (TextView) findViewById(R.id.tv_dpj);
		vp_detail = (LazyViewPager) findViewById(R.id.vp_detail);
		// 指示线
		indicate_line = findViewById(R.id.indicate_line);

	}

	public void initListener() {
		// viewpager添加界面改变监听
		vp_detail.setOnPageChangeListener(new OnMainPageChangeListener());
		// 给标签添加点击事件
		iv_back.setOnClickListener(this);
		tv_all.setOnClickListener(this);
		tv_dzf.setOnClickListener(this);
		tv_dfh.setOnClickListener(this);
		tv_dsh.setOnClickListener(this);
		tv_dpj.setOnClickListener(this);
	}

	public void initData() {
		orderItem = getIntent().getIntExtra("order", 0);
		datas.add(new AllOrderFragment());
		datas.add(new DzfOrderFragment());
		datas.add(new DfhOrderFragment());
		datas.add(new DshOrderFragment());
		datas.add(new DpjOrderFragment());
		// 添加Viewpager适配器
		adapter = new MainViewpagerAdapter(getSupportFragmentManager(), datas);
		vp_detail.setAdapter(adapter);
		// 初始化数据,数据源数据
//		adapter.notifyDataSetChanged();
		// 指示线初始化
		int screenWidth = getResources().getDisplayMetrics().widthPixels;// 屏幕宽度
		lineWidth = screenWidth / 5;
		indicate_line.getLayoutParams().width = lineWidth;
		vp_detail.setCurrentItem(orderItem);

	}

	private final class OnMainPageChangeListener implements
			OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			/**
			 * 1. 标签颜色改变 2. 标签执行缩放动画 3. 指示线跟着手指一起变化
			 */
			// 1. 标签颜色改变
			int green = getResources().getColor(R.color.green);
			int black = getResources().getColor(R.color.gary);
			tv_all.setTextColor(position == 0 ? green : black);
			tv_dzf.setTextColor(position == 1 ? green : black);
			tv_dfh.setTextColor(position == 2 ? green : black);
			tv_dsh.setTextColor(position == 3 ? green : black);
			tv_dpj.setTextColor(position == 4 ? green : black);
			ViewHelper.setTranslationX(indicate_line, lineWidth*position);
		}

		/**
		 * 滑动过程中的回调 3. 指示线跟着手指一起变化? 最终位置 = 起始位置+位移距离 起始位置 = position*自身宽度 位移距离 =
		 * 手指在屏幕上划过距离百分比* 自身宽度
		 */
		/**
		 * position:显示的第一个界面的位置 positionOffset: 第一个界面在屏幕上偏移距离百分比
		 * positionOffsetPixels:第一个界面在屏幕上偏移距离
		 */
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// 指示线偏移
			// ViewPropertyAnimator.animate(main_indicate_line).translationX(50);
			float disX = positionOffset * lineWidth;
			float startX = position * lineWidth;
			float endX = startX + disX;
			ViewHelper.setTranslationX(indicate_line, endX);// 直接改变属性(位移坐标)
			// ViewPropertyAnimator.animate(main_indicate_line).translationX(endX);
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_all:
			vp_detail.setCurrentItem(0);
			break;
		case R.id.tv_success:
			vp_detail.setCurrentItem(1);
			break;
		case R.id.tv_dfh:
			vp_detail.setCurrentItem(2);
			break;
		case R.id.tv_dsh:
			vp_detail.setCurrentItem(3);
			break;
		case R.id.tv_dpj:
			vp_detail.setCurrentItem(4);
			break;
		default:
			break;
		}

	}

}
