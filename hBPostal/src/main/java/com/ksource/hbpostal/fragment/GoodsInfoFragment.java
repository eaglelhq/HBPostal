package com.ksource.hbpostal.fragment;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ksource.hbpostal.R;
import com.ksource.hbpostal.interfaces.ClickBackJsInterface;

public class GoodsInfoFragment extends BaseFragment {

	private WebView mWebView;

	private WebSettings settings;
	/** 触摸时按下的点 **/
	PointF downP = new PointF();
	/** 触摸时当前的点 **/
	PointF curP = new PointF();

	private String url;

	@Override
	public View initView() {
		return View.inflate(context, R.layout.fragment_goods_info, null);
	}

	@Override
	public void initData() {
//		url = ConstantValues.GoodsDetail_HTML_URL + "?goodsId="
//				+ GoodsDetailActivity.goodsId;
		mWebView = (WebView) view.findViewById(R.id.wv_goods_info);
		settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true); // 支持javascript
//		settings.setUseWideViewPort(true); // 设置webview推荐使用的窗口，使html界面自适应屏幕
//		settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//		settings.setBuiltInZoomControls(false); // 不支持缩放
//		settings.setAllowFileAccess(true); // 设置可以访问文件
//		settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM); // 设置中等像素密度，medium=160dpi
		// settings.setSupportZoom(true); //设置支持缩放
		settings.setLoadsImagesAutomatically(true); // 设置自动加载图片
		// settings.setBlockNetworkImage(true); //设置网页在加载的时候暂时不加载图片
		// settings.setAppCachePath(""); //设置缓存路径
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 设置缓存模式
		// 设置默认缩放级别
		// mWebView.setInitialScale(25);
//		mWebView.requestFocus();

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
			// 新开页面时用自己定义的webview来显示，不用系统自带的浏览器来显示
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}

			// 加载错误时要做的工作
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				mWebView.loadUrl("file:///android_asset/error.html");
				mWebView.addJavascriptInterface(new ClickBackJsInterface() {
					@JavascriptInterface
					public void clickOnAndroid(String type) {
						mWebView.post(new Runnable() {

							@Override
							public void run() {
								mWebView.loadUrl(url);
							}
						});
					}

					@JavascriptInterface
					public void jf(String type) {
					}
				}, "error");

				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});
		mWebView.addJavascriptInterface(new ClickBackJsInterface() {

			@JavascriptInterface
			public void clickOnAndroid(String type) {
				if (getActivity() != null)
					getActivity().finish();
			}

			@JavascriptInterface
			public void jf(String type) {
				// openActivityForResult(CaptureActivity.class, null, 0);
			}

		}, "demo");

		mWebView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 每次进行onTouch事件都记录当前的按下的坐标
				curP.x = event.getX();
				curP.y = event.getY();

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 记录按下时候的坐标
					// 切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
					downP.x = event.getX();
					downP.y = event.getY();
					// 此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
//					((ViewGroup) v).requestDisallowInterceptTouchEvent(true);
				}

				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					float distanceX = curP.x - downP.x;
					float distanceY = curP.y - downP.y;
					// 接近水平滑动，ViewPager控件捕获手势，水平滚动
					System.out.println("GoodsInfoFragment-yLast===" + distanceY);
					System.out.println("GoodsInfoFragment-xLast===" + distanceX);
					if (Math.abs(Math.abs(distanceY) - Math.abs(distanceX))>20 &&Math.abs(distanceY) >100) {
						// 接近垂直滑动，交给父控件处理
//						((ViewGroup) v)
//								.requestDisallowInterceptTouchEvent(false);
						return true;
					} else {
						// 此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
//						((ViewGroup) v)
//								.requestDisallowInterceptTouchEvent(true);
						return false;
					}
				}

				return false;

			}
		});
		
		mWebView.loadUrl(url);

	}
}
