package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.SPUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BannerBean;
import com.ksource.hbpostal.bean.BannerBean.AdvertListBean;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.util.LogUtils;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 闪屏界面
 */
public class SplashActivity extends BaseActivity {
	private String TAG = "TAG";

	private ImageView splash_view;
	// private Banner banner;

	// "file://" +ConstantValues.DOWNLOADFILEPATH
	// private String[] imagesUrl = { "" };
	// private int[] imagesUrl = { R.drawable.loading_pic };

	private boolean showBanner;
	private boolean isGuide;
	private Animation animation;

	// private boolean timeOut;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_splash;
	}

	@Override
	public void initView() {
		splash_view = (ImageView) findViewById(R.id.icon_splash);
	}

	@Override
	public void initListener() {
	}

	@Override
	public void initData() {
		// final boolean isShowBanner =
		// sp.getBoolean(ConstantValues.IS_SHOW_BANNER,
		// true);
		// handler.sendEmptyMessageDelayed(OPEN_MAIN, 4000);
		isGuide = sp.getBoolean(ConstantValues.KEY_IS_GUIDE, true);
		showBanner = sp.getBoolean(ConstantValues.IS_SHOW_BANNER, false);
		isConnected = NetworkUtils.isConnected();
		if (!isConnected) {
			ToastUtil.showTextToast(context, "网络未连接！");
		} else {
			getBanner();
			checkPwd();
		}

		initAnim();
		// banner.setImagesUrl(imagesUrl);
		// banner.setImagesRes(imagesUrl);
//		if (isGuide){
//			startActivity(new Intent(context,GuideActivity.class));
//			finish();
//		}else {
			splash_view.setAnimation(animation);
//		}
	}

	/**
	 * 检查是否设置支付密码
	 */
	private void checkPwd() {
		Map<String, String> params = new HashMap<>();
		String token = sp.getString(ConstantValues.TOKEN,null);
		params.put("token",token);
		StringCallback callback = new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int i) {
				SPUtils spUtils = new SPUtils(ConstantValues.SP_NAME);
				spUtils.putBoolean(ConstantValues.PAY_PWD,false);
			}

			@Override
			public void onResponse(String s, int i) {
				BaseResultBean baseBean = null;
				Gson gson = new Gson();
				try{
					baseBean = gson.fromJson(s,BaseResultBean.class);
				}catch (Exception e){
					e.printStackTrace();
				}
				if (baseBean != null && baseBean.success && baseBean.pwd == 0) {
					SPUtils spUtils = new SPUtils(ConstantValues.SP_NAME);
					spUtils.putBoolean(ConstantValues.PAY_PWD,true);
				}else{
						SPUtils spUtils = new SPUtils(ConstantValues.SP_NAME);
						spUtils.putBoolean(ConstantValues.PAY_PWD,false);
				}
			}
		};
		if (TextUtils.isEmpty(token)){
			DataUtil.doPostAESData(null,context,ConstantValues.CHECK_PWD_URL, params, callback);
		}else{
			SPUtils spUtils = new SPUtils(ConstantValues.SP_NAME);
			spUtils.putBoolean(ConstantValues.PAY_PWD,false);
		}
	}

	private void initAnim() {
		animation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(2000);
		animation.setFillAfter(true);
		animation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// timeOut = true;
				// splash_view.setVisibility(View.GONE);
				if (showBanner) {
					startActivity(new Intent(context, ImageActivity.class));
				} else {
					startActivity(new Intent(context, MainActivity.class));
				}
				finish();
			}
		});
	}


	@Override
	public void onClick(View v) {

	}

	private List<AdvertListBean> advertList;
	private boolean isConnected;

	private void getBanner() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("advertType", "1");
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				BannerBean bannerBean = null;
				Gson gson = new Gson();
				try {
					bannerBean = gson.fromJson(arg0, BannerBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (bannerBean == null) {
					return;
				}
				if (bannerBean.success) {
//					String cacheBanner = sp.getString(
//							ConstantValues.SPLASH_BANNER, null);
//					if (bannerResault.equals(cacheBanner)) {
//						// sp.edit().putBoolean(ConstantValues.IS_SHOW_BANNER,
//						// false).apply();
//						return;
//					}
					advertList = bannerBean.advertList;
					if (advertList == null || advertList.size() == 0) {
						// autoOpen = true;
						LogUtils.e("OPEN_MAIN", "advertList.size() == 0");
						sp.edit()
								.putBoolean(ConstantValues.IS_SHOW_BANNER,
										false).apply();
						return;
					}
					sp.edit()
							.putString(ConstantValues.SPLASH_BANNER,
									arg0).apply();
					sp.edit().putBoolean(ConstantValues.IS_SHOW_BANNER, true)
							.apply();
					for (int i = 0; i < advertList.size(); i++) {
						// imagesUrl[i] = ConstantValues.BASE_URL
						// + advertList.get(i).IMAGE;
						String imageFile = advertList.get(i).IMAGE;
						// String imageName =
						// imageFile.substring(imageFile.lastIndexOf("."));
						// boolean fileExists =
						// FileUtils.isFileExists(ConstantValues.IMAGEFILEPATH+"banner.jpg");
						// if (!fileExists) {
						sp.edit()
								.putBoolean(ConstantValues.IS_SHOW_BANNER, true)
								.apply();
						OkHttpUtils
								.get()
								.url(ConstantValues.BASE_URL + imageFile)//
								.build()
								.execute(
										new FileCallBack(
												ConstantValues.IMAGEFILEPATH,
												"banner.jpg")//
										{

											@Override
											public void onError(Call arg0,
													Exception arg1, int arg2) {
												Log.e(TAG,
														"onError :"
																+ arg1.getMessage());
											}

											@Override
											public void onResponse(File arg0,
													int arg1) {
												Log.e(TAG,
														"onResponse :"
																+ arg0.getAbsolutePath());
											}

										});
						// }
					}

				} else {
					sp.edit().putBoolean(ConstantValues.IS_SHOW_BANNER, false)
							.apply();
					sp.edit().putString(ConstantValues.SPLASH_BANNER, null)
							.apply();
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
//				ToastUtil.showTextToast(context, arg1.getMessage());
			}
		};
		DataUtil.doPostAESData(null,context,ConstantValues.BANNER_URL, params, callback);
	}
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
	}

}
