package com.ksource.hbpostal.fragment;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ksource.hbpostal.R;
import com.yitao.util.LogUtils;

public class GoodsDetailTopFragment extends BaseFragment {

	private WebView mWebView;

	private WebSettings settings;


	@Override
	public View initView() {
		return View.inflate(context, R.layout.fragment_goods_info, null);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void initData() {
		mWebView = (WebView) view.findViewById(R.id.wv_goods_info);
		settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true); // 支持javascript
		settings.setUseWideViewPort(true); // 设置webview推荐使用的窗口，使html界面自适应屏幕
		settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
		settings.setBuiltInZoomControls(false); // 不支持缩放
		settings.setAllowFileAccess(true); // 设置可以访问文件
		settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM); // 设置中等像素密度，medium=160dpi
		// settings.setSupportZoom(true); //设置支持缩放
		settings.setLoadsImagesAutomatically(true); // 设置自动加载图片
		// settings.setBlockNetworkImage(true); //设置网页在加载的时候暂时不加载图片
		// settings.setAppCachePath(""); //设置缓存路径
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 设置缓存模式
		// 设置默认缩放级别
		mWebView.setInitialScale(25);
		mWebView.requestFocus();

		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}

		});
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				return false;

			}
		});
		mWebView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x1 = 0;
				float x2 = 0;
				float y1 = 0;
				float y2 = 0;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 当手指按下的时候
					x1 = event.getX();
					y1 = event.getY();
				}
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					// 当手指离开的时候
					x2 = event.getX();
					y2 = event.getY();
					LogUtils.e("MotionEventx1", "------" + x1);
					LogUtils.e("MotionEventy1", "------" + y1);
					LogUtils.e("MotionEventx2", "------" + x2);
					LogUtils.e("MotionEventy2", "------" + y2);
					if (y1 - y2 < 100 || y2 - y1 < 100) {
						((WebView) v).requestDisallowInterceptTouchEvent(true);
						return true;
					} else {
						((WebView) v).requestDisallowInterceptTouchEvent(false);
//						return false;
					}
				}
				return false;

			}
		});
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("goodsId", GoodsDetailActivity.goodsId);
//		String paramStr = "";
//		try {
//			paramStr = AESOperator.getInstance().encrypt(new Gson().toJson(params));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		mWebView.loadUrl(ConstantValues.GoodsDetail_HTML_URL
//				+ "?goodsId="+GoodsDetailActivity.goodsId);

	}
}
