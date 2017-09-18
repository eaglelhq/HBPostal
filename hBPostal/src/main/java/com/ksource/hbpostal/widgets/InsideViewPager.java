package com.ksource.hbpostal.widgets;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * 此ViewPager解决与父容器ScrollView冲突的问题,无法完美解决.有卡顿 此自定义组件和下拉刷新scrollview配合暂时小完美，有待改善
 * 
 * @author bavariama
 * 
 */

public class InsideViewPager extends ViewPager {
//	float curX = 0f;
//	float downX = 0f;
	OnSingleTouchListener onSingleTouchListener;

	  /** 触摸时按下的点 **/
    PointF downP = new PointF();
    /** 触摸时当前的点 **/
    PointF curP = new PointF();

    /** 自定义手势**/
    private GestureDetector mGestureDetector;
	public InsideViewPager(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		 mGestureDetector = new GestureDetector(context, new XScrollDetector());
	}

	public InsideViewPager(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
		 mGestureDetector = new GestureDetector(context, new XScrollDetector());
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
//		return super.onInterceptTouchEvent(ev);
		//接近水平滑动时子控件处理该事件，否则交给父控件处理
      return !mGestureDetector.onTouchEvent(ev);
//		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		curX = ev.getX();
//		// TODO Auto-generated method stub
//		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//			downX = curX;
//		}
//		int curIndex = getCurrentItem();
//		if (curIndex == 0) {
//			if (downX <= curX) {
//				getParent().requestDisallowInterceptTouchEvent(false);
//			} else {
//				getParent().requestDisallowInterceptTouchEvent(true);
//			}
//		} else if (curIndex == getAdapter().getCount() - 1) {
//			if (downX >= curX) {
//				getParent().requestDisallowInterceptTouchEvent(false);
//			} else {
//				getParent().requestDisallowInterceptTouchEvent(true);
//			}
//		} else {
//			getParent().requestDisallowInterceptTouchEvent(true);
//		}
//		getParent().requestDisallowInterceptTouchEvent(false);
//
//		return super.onTouchEvent(ev);
		 //每次进行onTouch事件都记录当前的按下的坐标
        curP.x = ev.getX();
        curP.y = ev.getY();

        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            //记录按下时候的坐标
            //切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
            downP.x = ev.getX();
            downP.y = ev.getY();
            //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        if(ev.getAction() == MotionEvent.ACTION_MOVE){
            float distanceX = curP.x - downP.x;
            float distanceY = curP.y - downP.y;
            //接近水平滑动，ViewPager控件捕获手势，水平滚动
            if(Math.abs(distanceX) > Math.abs(distanceY)){
                //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
                getParent().requestDisallowInterceptTouchEvent(false);
            }else{
                //接近垂直滑动，交给父控件处理
                getParent().requestDisallowInterceptTouchEvent(true);
            }
        }

        return super.onTouchEvent(ev);
	}

	private class XScrollDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            return super.onScroll(e1, e2, distanceX, distanceY);

            //接近水平滑动时子控件处理该事件，否则交给父控件处理
            return (Math.abs(distanceX) > Math.abs(distanceY));
        }
    }


	
	public void onSingleTouch() {
		if (onSingleTouchListener != null) {
			onSingleTouchListener.onSingleTouch();
		}
	}

	public interface OnSingleTouchListener {
		public void onSingleTouch();
	}

	public void setOnSingleTouchListner(
			OnSingleTouchListener onSingleTouchListener) {
		this.onSingleTouchListener = onSingleTouchListener;
	}

}
