package com.ksource.hbStore.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksource.hbStore.R;
import com.ksource.hbStore.adapter.MainViewpagerAdapter;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class ThreeFragment extends BaseFragment implements OnClickListener {

	private TextView tv_title;
	private ImageView iv_back;

	// 数据源
	List<Fragment> datas = new ArrayList<>();
	private MainViewpagerAdapter adapter;
	private TextView tv_all, tv_success, tv_dfh;
	private ViewPager vp_detail;
	private View indicate_line;
	private int lineWidth;// 指示线宽度

	private int orderItem;

	@Override
	public View initView() {
		return View.inflate(context, R.layout.fragment_tab3, null);
	}

	@Override
	public void initData() {
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		iv_back = (ImageView) view.findViewById(R.id.iv_back);
		iv_back.setVisibility(View.GONE);
		tv_title.setText("结算明细");
		// 标签:
		tv_all = (TextView) view.findViewById(R.id.tv_all);
		tv_success = (TextView) view.findViewById(R.id.tv_success);
		tv_dfh = (TextView) view.findViewById(R.id.tv_dfh);
		vp_detail = (ViewPager) view.findViewById(R.id.vp_detail);
		// 指示线
		indicate_line = view.findViewById(R.id.indicate_line);
		// viewpager添加界面改变监听
		vp_detail.setOnPageChangeListener(new OnMainPageChangeListener());
		// 给标签添加点击事件
		iv_back.setOnClickListener(this);
		tv_all.setOnClickListener(this);
		tv_success.setOnClickListener(this);
		tv_dfh.setOnClickListener(this);

		orderItem = 0;
		datas.add(new JSDBillFragment());
		datas.add(new DJSBillFragment());
		datas.add(new YJSBillFragment());
		// 添加Viewpager适配器
		adapter = new MainViewpagerAdapter(getChildFragmentManager(), datas);
		vp_detail.setAdapter(adapter);
		// 初始化数据,数据源数据
//		adapter.notifyDataSetChanged();
		// 指示线初始化
		int screenWidth = getResources().getDisplayMetrics().widthPixels;// 屏幕宽度
		lineWidth = screenWidth / 3;
		indicate_line.getLayoutParams().width = lineWidth;
		vp_detail.setCurrentItem(orderItem);
	}



	private final class OnMainPageChangeListener implements
			ViewPager.OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			/**
			 * 1. 标签颜色改变 2. 标签执行缩放动画 3. 指示线跟着手指一起变化
			 */
			// 1. 标签颜色改变
			int green = getResources().getColor(R.color.green);
			int black = getResources().getColor(R.color.gary);
			tv_all.setTextColor(position == 0 ? green : black);
			tv_success.setTextColor(position == 1 ? green : black);
			tv_dfh.setTextColor(position == 2 ? green : black);
			ViewHelper.setTranslationX(indicate_line, lineWidth * position);
		}

		/**
		 * 滑动过程中的回调 3. 指示线跟着手指一起变化? 最终位置 = 起始位置+位移距离 起始位置 = position*自身宽度 位移距离 =
		 * 手指在屏幕上划过距离百分比* 自身宽度
		 */
		/**
		 * position:显示的第一个界面的位置
		 * positionOffset: 第一个界面在屏幕上偏移距离百分比
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
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.tv_all:
				vp_detail.setCurrentItem(0);
				break;
			case R.id.tv_success:
				vp_detail.setCurrentItem(1);
				break;
			case R.id.tv_dfh:
				vp_detail.setCurrentItem(2);
				break;
			default:
				break;
		}
	}

}