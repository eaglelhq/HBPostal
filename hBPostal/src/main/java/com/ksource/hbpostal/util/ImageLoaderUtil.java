package com.ksource.hbpostal.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.ksource.hbpostal.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * 图片加载
 */
public class ImageLoaderUtil {

	public static ImageLoader imageLoader = ImageLoader.getInstance();

	/**
	 * 加载网络头像用
	 */
	public static void loadNetPic(String path, ImageView iv) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnFail(R.drawable.fail_img)
		.showImageOnLoading(R.drawable.fail_img)
		.showImageForEmptyUri(null).cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		imageLoader.displayImage(path, iv, options);
	}

	/**
	 * 加载本地相册图片用
	 */
	public static void loadLocationPhoto(String path, ImageView image) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnFail(R.drawable.fail_img)
		.showImageOnLoading(R.drawable.fail_img)
		.cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new SimpleBitmapDisplayer())
		.build();
		imageLoader.displayImage(path, image, options);
	}

	/**
	 * 清除缓存
	 * */
	public static void clearCache() {
		imageLoader.clearMemoryCache();
		imageLoader.clearDiscCache();
	}
}
