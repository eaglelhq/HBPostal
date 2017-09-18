package com.yitao.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;

public class ImageAdapter extends BaseAdapter {

	private Animator mCurrentAnimator;

	private int mShortAnimationDuration = 300;

	private Context mContext;
	LayoutInflater inflater;

	private int[] imageIds;//图片资源id
	private Bitmap[]bitmaps=null;//图片集合
	public ImageAdapter(Context c) {
		mContext = c;
	}

	public int getCount() {
		return imageIds.length;
	}

	public Object getItem(int position) {
		return imageIds[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		final ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			imageView = (ImageView) convertView;
		}

		//imageView.setImageResource(mThumbIds[position]);
		//imageView.setTag(mThumbIds[position]);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				//zoomImageFromThumb(view, position);
			}
		});

		return imageView;
	}
}