package com.ksource.hbpostal.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ksource.hbpostal.R;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.interfaces.ClickBackJsInterface;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;

/**
 * 打开H5页面
 */
public class BaseHtmlActivity extends BaseActivity {

    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    private TextView tv_title;
    private ImageView iv_back;
    private WebView mWebView;
    private LoadDialog mLoadDialog;
    private String url;
    private String title;
    private String token;
    private String accessType;
    private LinearLayout ll_bottom;
    private Button btn_submit;
    private String act_id;
    private boolean isSign;
    private String deadline;

    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private Uri imageUri;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                if (accessType != null && accessType.equals("1")) {
                    mApplication.finishAllActivity();
                    startActivity(new Intent(context, MainActivity.class));
                }
                break;

            case R.id.btn_submit:
                if (TextUtils.isEmpty(token)) {
                    ToastUtil.showTextToast(context, "登录过后才可以报名！");
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra("isBack", true);
                    startActivity(intent);
                    return;
                }
                finish();
                Intent intent = new Intent(context, EnlistActivity.class);
                intent.putExtra("actId", act_id);
                intent.putExtra("title", title);
                intent.putExtra("deadline", deadline + "");
                startActivity(intent);
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
        token = sp.getString(ConstantValues.TOKEN, "");
        isSign = getIntent().getBooleanExtra("isSign", false);
        accessType = getIntent().getStringExtra("type");
        deadline = getIntent().getStringExtra("deadline");
        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        act_id = getIntent().getStringExtra("act_id");
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        mWebView = (WebView) findViewById(R.id.webview);
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        if (!TextUtils.isEmpty(act_id)) {
            ll_bottom.setVisibility(View.VISIBLE);
        }
        btn_submit.setEnabled(!isSign);
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                take();
                return true;
            }


            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                take();
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                take();
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                take();
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if ("都邦保险".equals(BaseHtmlActivity.this.title)) {
                    tv_title.setText(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 80) {
                    DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                }
            }

        });
        mWebView.setWebViewClient(new WebViewClient() {
            // 新开页面时用自己定义的webview来显示，不用系统自带的浏览器来显示
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 当有新连接时使用当前的webview进行显示
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(url));
                    context.startActivity(intent);
                    return true;
                } else if (url.startsWith("sms:")) {
                    Intent intent = new Intent();
                    // 系统默认的action，用来打开默认的短信界面
                    intent.setAction(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse(url));
                    context.startActivity(intent);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
//                view.loadUrl(url);
//                return true;
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
                if (accessType != null && accessType.equals("1")) {
                    mApplication.finishAllActivity();
                    startActivity(new Intent(context, MainActivity.class));
                }
            }

            @JavascriptInterface
            public void jf(String type) {
                // openActivityForResult(CaptureActivity.class, null, 0);
//				ToastUtil.showTextToast(context, "去缴费"+type);
                startJfAct(Integer.parseInt(type));
            }

        }, "demo");
    }

    private void startJfAct(int type) {
        if (TextUtils.isEmpty(token)) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("isBack", true);
            startActivity(intent);
            return;
        }
        if (type == 6) {
            startActivity(new Intent(context, SJCZActivity.class));
        } else if (type == 7) {
            startActivity(new Intent(context, JTWZActivity.class));
        } else {
            Intent intent = new Intent(context, SHJFActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
        }
    }

    @Override
    public void initData() {
        ShareSDK.initSDK(context);
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "数据加载中...");
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
//        WebSettings settings = mWebView.getSettings();
        WebSettings wvSetting = mWebView.getSettings();
        wvSetting.setJavaScriptEnabled(true);
        wvSetting.setUseWideViewPort(true); // 设置webview推荐使用的窗口，使html界面自适应屏幕
        wvSetting.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        wvSetting.setAllowFileAccess(true); // 设置可以访问文件
        wvSetting.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM); // 设置中等像素密度，medium=160dpi
        wvSetting.setSupportZoom(true); //设置支持缩放
        wvSetting.setLoadsImagesAutomatically(true); // 设置自动加载图片
        // settings.setBlockNetworkImage(true); //设置网页在加载的时候暂时不加载图片
        // settings.setAppCachePath(""); //设置缓存路径
        wvSetting.setCacheMode(WebSettings.LOAD_NO_CACHE); // 设置缓存模式
        wvSetting.setDomStorageEnabled(true);
        wvSetting.setAppCacheMaxSize(1024 * 1024 * 8);
//		String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        wvSetting.setAppCachePath(ConstantValues.CACHE_PATH);
        wvSetting.setAppCacheEnabled(true);
        wvSetting.setBlockNetworkImage(false);
        wvSetting.setBuiltInZoomControls(false);
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mWebView.loadUrl(url);
    }

    private void take() {
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
        // Create the storage directory if it does not exist
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        imageUri = Uri.fromFile(file);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent i = new Intent(captureIntent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);

        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        goBack();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {

                if (result != null) {
                    String path = getPath(getApplicationContext(),
                            result);
                    Uri uri = Uri.fromFile(new File(path));
                    mUploadMessage
                            .onReceiveValue(uri);
                } else {
                    mUploadMessage.onReceiveValue(imageUri);
                }
                mUploadMessage = null;
            }
        }
    }

    @SuppressWarnings("null")
    @TargetApi(Build.VERSION_CODES.BASE)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
                || mUploadCallbackAboveL == null) {
            return;
        }

        Uri[] results = null;

        if (resultCode == Activity.RESULT_OK) {

            if (data == null) {

                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        if (results != null) {
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        } else {
            results = new Uri[]{imageUri};
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }

        return;
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private void goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
            if (accessType != null && accessType.equals("1")) {
                mApplication.finishAllActivity();
                startActivity(new Intent(context, MainActivity.class));
            }
        }
    }

}
