package com.ksource.hbpostal.activity;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksource.hbpostal.R;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.interfaces.ClickBackJsInterface;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;

/**
 * 会员等级页面 - H5
 */
public class LevelActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back,iv_right;
	private WebView mWebView;
	private LoadDialog mLoadDialog;
	private String url;
	
	@Override
	public int getLayoutResId() {
		return R.layout.activity_level;
	}

	@Override
	public void initView() {
		String token = sp.getString(ConstantValues.TOKEN, "");
		url = ConstantValues.GRADE_HTML_URL + "?token=" + token;
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("会员等级");
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		iv_right.setVisibility(View.VISIBLE);
		mWebView = (WebView) findViewById(R.id.wv_level);
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		iv_right.setOnClickListener(this);
		mWebView.setWebChromeClient(new WebChromeClient() {


			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
					DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				}
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
				finish();
			}

			@JavascriptInterface
			public void jf(String type) {
				// openActivityForResult(CaptureActivity.class, null, 0);
			}

		}, "demo");
	}

	@Override
	public void initData() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"数据加载中...");
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true); // 支持javascript
		settings.setUseWideViewPort(true); // 设置webview推荐使用的窗口，使html界面自适应屏幕
		settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
		settings.setAllowFileAccess(true); // 设置可以访问文件
		settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM); // 设置中等像素密度，medium=160dpi
		// settings.setSupportZoom(true); //设置支持缩放
		settings.setLoadsImagesAutomatically(true); // 设置自动加载图片
		// settings.setBlockNetworkImage(true); //设置网页在加载的时候暂时不加载图片
		// settings.setAppCachePath(""); //设置缓存路径
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 设置缓存模式

		mWebView.loadUrl(url);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			goBack();
			break;
		case R.id.iv_right:
			finish();
//			ToastUtil.showTextToast(context, "回到首页");
//			mApplication.finishAllActivity();
//			startActivity(new Intent(context, MainActivity.class));
			break;
		

		default:
			break;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		goBack();
		return true;
	}

	private void goBack() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			finish();
		}
	}

}
