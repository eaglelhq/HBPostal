package com.ksource.hbpostal.activity;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.interfaces.ClickBackJsInterface;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.LogUtils;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;

/**
 * 互动交流-活动信息，通知公告，H5页面，可分享
 */
public class HTMLActivity extends BaseActivity {

	private TextView tv_title, tv_right;
	private ImageView iv_back;
	private WebView mWebView;
	private LoadDialog mLoadDialog;
	private String url;
	private String title;
	private String newsTitle;
	private static String token;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			goBack();
			break;
		case R.id.btn_right:
			showShare(context, newsTitle, "鹤壁邮支付平台\n足不出户，安然生活", url
					+ "&share=true", ConstantValues.BASE_URL
					+ "/mobile/images/life_icon_logo.png");

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
		newsTitle = getIntent().getStringExtra("newsTitle");
		url = getIntent().getStringExtra("url");
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_right = (TextView) findViewById(R.id.btn_right);
		tv_right.setVisibility(View.VISIBLE);
		tv_title.setText(title);
		tv_right.setText("分享");
		iv_back = (ImageView) findViewById(R.id.iv_back);
		mWebView = (WebView) findViewById(R.id.webview);
		token = sp.getString(ConstantValues.TOKEN, null);
	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		tv_right.setOnClickListener(this);
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
		ShareSDK.initSDK(context);
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"数据加载中...");
		mWebView.setHorizontalScrollBarEnabled(false);// 水平不显示
		mWebView.setVerticalScrollBarEnabled(false); // 垂直不显示
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

	/**
	 * 一键分享
	 */
	public static void showShare(final Context context, String title,
			String text, String url, String imageUrl) {
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		// oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(title);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(url);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(text);
		// 分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
		oks.setImageUrl(imageUrl);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(url);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("最好用的鹤壁邮政邮支付平台");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(title);
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(url);

		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				ToastUtil.showTextToast(context, "分享失败");
			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				ToastUtil.showTextToast(context, "分享成功");
				Map<String, String> params = new HashMap<String, String>();
				params.put("token", token);
				StringCallback callback = new StringCallback() {
					
					@Override
					public void onResponse(String arg0, int arg1) {
						Gson gson = new Gson();
						BaseResultBean resultBean = null;
						try {
							resultBean = gson.fromJson(arg0,
									BaseResultBean.class);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (resultBean != null && resultBean.success) {
							ToastUtil.showTextToast(context, resultBean.msg);
						}
						LogUtils.e("Share", arg0);
					}
					
					@Override
					public void onError(Call arg0, Exception arg1, int arg2) {
						
					}
				};
				DataUtil.doPostAESData(null,context,ConstantValues.SHARE_SUCCESS_URL, params, callback);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				ToastUtil.showTextToast(context, "分享取消");
			}
		});
		// 启动分享GUI
		oks.show(context);
	}

}
