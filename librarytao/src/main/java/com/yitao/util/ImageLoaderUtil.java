package com.yitao.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.yitao.library_tao.R;

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
		.showImageOnFail(R.drawable.plugin_camera_no_pictures)
		.showImageOnLoading(R.drawable.plugin_camera_no_pictures)
		.showImageForEmptyUri(null).cacheInMemory(true)
		.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		imageLoader.displayImage(path, iv, options);
	}
	/**
	 * 加载网络头像用
	 */
	public static void loadNetPicWithoutError(String path, ImageView iv) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
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
		.showImageOnFail(R.drawable.plugin_camera_no_pictures)
		.showImageOnLoading(R.drawable.plugin_camera_no_pictures)
		.cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new SimpleBitmapDisplayer())
		.build();
		imageLoader.displayImage(path, image, options);
	}
	/**
	 * 加载本地相册图片用
	 */
	public static void loadLocationPhotoWithoutError(String path, ImageView image) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.bitmapConfig(Bitmap.Config.RGB_565)
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
