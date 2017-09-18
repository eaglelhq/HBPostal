package com.yitao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ListView;


public class ListViewForScroll extends ListView {
	
	public ListViewForScroll(Context context, AttributeSet attrs) { 
		super(context, attrs); 
	} 
	
	public ListViewForScroll(Context context) { 
		super(context); 
	} 
	
	public ListViewForScroll(Context context, AttributeSet attrs, int defStyle) { 
		super(context, attrs, defStyle); 
	} 
	
	@Override 
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST); 
		super.onMeasure(widthMeasureSpec, expandSpec); 
	} 
}
