package com.ksource.hbpostal.widgets;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 制作主页的向上广告滚动条
 * AdvertisementObject是主页的数据源，假如通过GSON或FastJson获取的实体类
 */
public class ScrollUpAdvView extends
		BaseAutoScrollUpTextView<AdvBean> {

	public ScrollUpAdvView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScrollUpAdvView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollUpAdvView(Context context) {
		super(context);
	}

	@Override
	public String getTextTitle(AdvBean data) {
		return data.title;
	}

	@Override
	public String getTextInfo(AdvBean data) {
		return data.info;
	}
	
//	@Override
//	public String getText2Title(AdvBean data) {
//		return data.title2;
//	}
//	
//	@Override
//	public String getText2Info(AdvBean data) {
//		return data.info2;
//	}

	/**
	 * 这里面的高度应该和你的xml里设置的高度一致
	 */
	@Override
	protected int getAdertisementHeight() {
		return 40;
	}

}
