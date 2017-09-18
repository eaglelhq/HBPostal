package com.ksource.hbpostal.widgets;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * 能够兼容ViewPager的ScrollView
 * 
 * @Description: 解决了ViewPager在ScrollView中的滑动反弹问题
 */
public class ScrollViewExtend extends ScrollView {
	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	public ScrollViewExtend(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		int height = getHeight();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = event.getX();
			yLast = event.getY();
			if (yLast > height / 2) {
				return false;
			}
			break;
		case MotionEvent.ACTION_MOVE:

			System.out.println("yLast==="+yLast);
			System.out.println("xLast==="+xLast);
			
			if (Math.abs(Math.abs(yLast)-Math.abs(xLast)) > 20 && yLast < height / 2) {
				return true;
			} else {
				return super.onInterceptTouchEvent(event);
			}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			return super.onInterceptTouchEvent(event);
		}
		return super.onInterceptTouchEvent(event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		int height = getHeight();
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			xDistance = yDistance = 0f;
//			xLast = event.getX();
//			yLast = event.getY();
//			if (yLast < height / 2) {
//				return false;
//			}
//			break;
//		case MotionEvent.ACTION_MOVE:
//
//			System.out.println("yLast==="+yLast);
//			System.out.println("xLast==="+xLast);
//			
//			if (Math.abs(Math.abs(yLast)-Math.abs(xLast)) > 20 && yLast < height / 2) {
//				return false;
//			} else {
//				return super.onTouchEvent(event);
//			}
//		}
		return false;
//		return super.onTouchEvent(event);
	}
	
}
