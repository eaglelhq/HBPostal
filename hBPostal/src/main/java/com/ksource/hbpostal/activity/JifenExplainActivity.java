package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.MainViewpagerAdapter;
import com.ksource.hbpostal.fragment.GoldExplainFragment;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class JifenExplainActivity extends BaseFragmentActivity {

	private TextView tv_title;
	private ImageView iv_back;

	// 数据源
	List<Fragment> datas = new ArrayList<Fragment>();
	private MainViewpagerAdapter adapter;
	private TextView tv_first, tv_second, tv_third;
	private View indicate_line;
	private ViewPager vp_detail;
	private int lineWidth;// 指示线宽度

	@Override
	public int getLayoutResId() {
		return R.layout.activity_explain;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_back = (ImageView) findViewById(R.id.iv_back);

		tv_first = (TextView) findViewById(R.id.tv_first);
		tv_second = (TextView) findViewById(R.id.tv_second);
		tv_third = (TextView) findViewById(R.id.tv_third);
		vp_detail = (ViewPager) findViewById(R.id.vp_detail);
		// 指示线
		indicate_line = findViewById(R.id.indicate_line);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void initListener() {
		vp_detail.setOnPageChangeListener(new OnMainPageChangeListener());
		iv_back.setOnClickListener(this);
		tv_first.setOnClickListener(this);
		tv_second.setOnClickListener(this);
		tv_third.setOnClickListener(this);
	}

	@Override
	public void initData() {
		String title = getIntent().getStringExtra("title");
		tv_title.setText(title);
		
		
		datas.add(new GoldExplainFragment());
		datas.add(new GoldExplainFragment());
		datas.add(new GoldExplainFragment());
		// 添加Viewpager适配器
		adapter = new MainViewpagerAdapter(getSupportFragmentManager(), datas);
		vp_detail.setAdapter(adapter);

		// 指示线初始化
		int screenWidth = getResources().getDisplayMetrics().widthPixels;// 屏幕宽度
		lineWidth = screenWidth / 3;
		indicate_line.getLayoutParams().width = lineWidth;
		vp_detail.setCurrentItem(0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_first:
			vp_detail.setCurrentItem(0);
			break;
		case R.id.tv_second:
			vp_detail.setCurrentItem(1);
			break;
		case R.id.tv_third:
			vp_detail.setCurrentItem(2);
			break;
		case R.id.iv_right:
			// ToastUtil.showTextToast(context, "回到首页");
			mApplication.finishAllActivity();
			startActivity(new Intent(context, MainActivity.class));
			break;

		default:
			break;
		}
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
			tv_first.setTextColor(position == 0 ? green : black);
			tv_second.setTextColor(position == 1 ? green : black);
			tv_third.setTextColor(position == 2 ? green : black);
			ViewHelper.setTranslationX(indicate_line, lineWidth * position);
			
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

}
