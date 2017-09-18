package com.yitao.photo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yitao.library_tao.R;


/**
  * 类说明： 加载本地或网络图片<br>
  * 类名：com.yitao.netpic.NetPicShowActivity <br>
  * 作者： 易涛 <br>
  * 时间：2015-7-17 上午9:54:16 <br>
  * 修改者：<br>
  * 修改日期：<br>
  * 修改内容：<br>
  */
public class NetPicShowActivity extends Activity{
	/***************** 常用控件  ********************/
	private Intent intent = null;
	String str1 = null, str2 = null;
	/** 图片集浏览 */
	private HackyViewPager viewPager = null;
	private LinearLayout hideLayout = null;
	private Button back = null;

	private List<String> listPic = null;
	private int item = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pic_show);
		init();
	}

	@SuppressWarnings("unchecked")
	private void init() {
		intent = getIntent();
		hideLayout = (LinearLayout) findViewById(R.id.hide_layout);
		back = (Button) findViewById(R.id.hide_btn_cancle);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		Bundle bundle = intent.getExtras();
		if(bundle.containsKey("item")){
			item = bundle.getInt("item");
		}
		listPic = (List<String>) bundle.getSerializable("listPath");
		viewPager = (HackyViewPager) findViewById(R.id.viewpager);
		new Thread() {
			public void run() {
				Message msg = mHandler.obtainMessage(0);
				msg.sendToTarget();
			}
		}.start();
	}

	OnItemClickListener clickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, final int position, long id) {
		}
	};

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				SamplePagerNetAdapter netAdapter = new SamplePagerNetAdapter(listPic, viewPager);
				viewPager.setAdapter(netAdapter);
				viewPager.setOffscreenPageLimit(2);
				viewPager.setCurrentItem(item);
				break;
			}
		};
	};

	class SamplePagerNetAdapter extends PagerAdapter {
		private List<String> picNameList = new ArrayList<String>();
		private Animator mCurrentAnimator;
		float startScale;
		HackyViewPager viewPager;
		Rect startBounds;
		float startScaleFinal;

		public SamplePagerNetAdapter(List<String> list, HackyViewPager hackyViewPager) {
			this.viewPager = hackyViewPager;
			this.picNameList = list;
		}

		@Override
		public int getCount() {
			if (null != picNameList && picNameList.size() > 0) {
				return picNameList.size();
			}
			return 0;
		}

		@Override
		public View instantiateItem(ViewGroup container, final int position) {
			// mIndex=position;
			final PhotoView photoView = new PhotoView(container.getContext());
			DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnFail(R.drawable.url_image_failed)
			.showImageOnLoading(R.drawable.url_image_failed)
			.showImageForEmptyUri(null)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();
			ImageLoader.getInstance().displayImage(picNameList.get(position), photoView ,options);
			
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
				public void onPhotoTap(View paramAnonymousView, float paramAnonymousFloat1, float paramAnonymousFloat2) {
					if (mCurrentAnimator != null) {
						mCurrentAnimator.cancel();
					}
					onBackPressed();
				}
			});
			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		public boolean getScaleFinalBounds(int position) {
			View childView = viewPager.getChildAt(position);
			startBounds = new Rect();
			final Rect finalBounds = new Rect();
			final Point globalOffset = new Point();
			try {
				childView.getGlobalVisibleRect(startBounds);
			} catch (Exception e) {
				return false;
			}
			startBounds.offset(-globalOffset.x, -globalOffset.y);
			finalBounds.offset(-globalOffset.x, -globalOffset.y);

			if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
				startScale = (float) startBounds.height() / finalBounds.height();
				float startWidth = startScale * finalBounds.width();
				float deltaWidth = (startWidth - startBounds.width()) / 2;
				startBounds.left -= deltaWidth;
				startBounds.right += deltaWidth;
			} else {
				startScale = (float) startBounds.width() / finalBounds.width();
				float startHeight = startScale * finalBounds.height();
				float deltaHeight = (startHeight - startBounds.height()) / 2;
				startBounds.top -= deltaHeight;
				startBounds.bottom += deltaHeight;
			}
			startScaleFinal = startScale;
			return true;
		}
	}

	/** 5秒钟隐藏menu */
	private Runnable hiddenMenu = new Runnable() {

		@Override
		public void run() {
			hideLayout.setVisibility(View.GONE);
			mHandler.removeCallbacks(hiddenMenu);// 移除该线程.
		}
	};
}
