package com.ksource.hbpostal.activity;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksource.hbpostal.R;
import com.ksource.hbpostal.config.ConstantValues;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;

import cn.sharesdk.framework.ShareSDK;

/**
 * 通用H5页面
 */
public class SimpleHtmlActivity extends BaseActivity {

	private TextView tv_title;
	private ImageView iv_back;
	private WebView mWebView;
	private LoadDialog mLoadDialog;
	private String url;
	private String title;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			goBack();
			break;

		default:
			break;
		}

	}

	@Override
	public int getLayoutResId() {
		return R.layout.activity_html;
	}

	@Override
	public void initView() {
		title = getIntent().getStringExtra("title");
		url = getIntent().getStringExtra("url");
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(title);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		mWebView = (WebView) findViewById(R.id.webview);
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
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
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				return false;

			}
		});
	}

	@Override
	public void initData() {
		ShareSDK.initSDK(context);
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"数据加载中...");
		mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
		mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
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

		String token = sp.getString(ConstantValues.TOKEN, "");

		mWebView.loadUrl(url);
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
