//package com.ksource.hbpostal.widgets;
//
//import android.util.Log;
//import android.view.View;
//import android.widget.AbsListView;
//import android.widget.AbsListView.OnScrollListener;
//
//import com.ksource.hbpostal.R;
//
//
//private class GridViewScrollListener implements OnScrollListener{
//
//		@Override
//		public void onScrollStateChanged(AbsListView view, int scrollState) {}
//
//		@Override
//		public void onScroll(AbsListView view, //报告滚动状态的视图
//		int firstVisibleItem,//第一个可见item的索引
//		int visibleItemCount,//可见item的数量
//		 int totalItemCount)//项目列表中的适配器的数量
//		  {
//			if (firstVisibleItem==0) {
//				View view2 = gridView.getChildAt(firstVisibleItem);
//				if (view2!=null) {
//					Log.i("TAG","view2.getY()"+view2.getY());
//					if (view2.getY() == 8) {//在顶部
//						button_up.setBackgroundResource(R.drawable.upb);//向上的键为灰色
//						button_up.setClickable(false);//且不能按
//					} else {
//						button_up.setBackgroundResource(R.drawable.upg);//向上的键为白色
//						button_up.setClickable(true);//能按
//					}
//				}
//			}else{
//				button_up.setBackgroundResource(R.drawable.upg);//向上的键为白色
//				button_up.setClickable(true);//能按
//			}if ((firstVisibleItem+visibleItemCount)==totalItemCount) {
//				View view3 = gridView.getChildAt(totalItemCount-1-firstVisibleItem);//scrollview所占的高度
//				if (view3!=null) {
//					Log.i("TAG","view3.getY()"+view3.getY());
//					if (view3.getY() == 246) {//在底部
//						button_down.setBackgroundResource(R.drawable.downb);	//向下的键为灰色
//						button_down.setClickable(false);	//且不能按
//					} else {
//						button_down.setBackgroundResource(R.drawable.downg);	//向下的键为白色
//						button_down.setClickable(true);	//能按
//					}
//				}
//			}else{//在中部
//				button_down.setBackgroundResource(R.drawable.downg);	//向下的键为白色
//				button_down.setClickable(true);	//能按
//			}
//		}
//	}