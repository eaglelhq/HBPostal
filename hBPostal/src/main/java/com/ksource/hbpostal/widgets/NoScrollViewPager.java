package com.ksource.hbpostal.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class NoScrollViewPager extends ViewPager {
	private boolean noScroll = false;

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollViewPager(Context context) {
		super(context);
	}

	public void setNoScroll(boolean noScroll) {
		this.noScroll = noScroll;
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
	}

	// 滑动距离及坐标
	private float xLast, yLast;
	private float xCurr;
	private float yCurr;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/* return false;//super.onTouchEvent(arg0); */
		if (noScroll)
			return false;
		else {
			// return super.onTouchEvent(event);
			// return false;
			int height = getHeight();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				xLast = event.getX();
				yLast = event.getY();
				if (yLast < height / 2) {
					return false;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				xCurr = event.getX();
				yCurr = event.getY();
				float disX,disY;
				disX = Math.abs(xCurr - xLast);
				disY = Math.abs(yCurr - yLast);
				System.out.println("NoScrollViewPager-yLast===" + disY);
				System.out.println("NoScrollViewPager-xLast===" + disX);

				if (Math.abs(Math.abs(disY) - Math.abs(disX)) > 20
						&& yLast < height / 2) {
					return false;
				} else {
					return super.onTouchEvent(event);
				}
			}
			return super.onTouchEvent(event);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (noScroll) {
			return false;
		} else {
			// int height = getHeight();
			// switch (event.getAction()) {
			// case MotionEvent.ACTION_DOWN:
			// xLast = event.getX();
			// yLast = event.getY();
			// if (yLast < height / 2) {
			// return false;
			// }
			// break;
			// case MotionEvent.ACTION_MOVE:
			//
			// System.out.println("yLast===" + yLast);
			// System.out.println("xLast===" + xLast);
			//
			// if (Math.abs(Math.abs(yLast) - Math.abs(xLast)) > 20
			// && yLast < height / 2) {
			// return true;
			// } else {
			// return false;
			// }
			// }
			return super.onInterceptTouchEvent(event);
		}
		// return super.onInterceptTouchEvent(event);
		// return true;
	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		super.setCurrentItem(item, smoothScroll);
	}

	@Override
	public void setCurrentItem(int item) {
		super.setCurrentItem(item);
	}

	// @Override
	// public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// int expandSpec = MeasureSpec.makeMeasureSpec(
	// Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
	// super.onMeasure(widthMeasureSpec, expandSpec);
	// }
}
