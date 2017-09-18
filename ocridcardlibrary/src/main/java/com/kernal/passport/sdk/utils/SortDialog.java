package com.kernal.passport.sdk.utils;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.kernal.passportreader.sdk.ShowResultActivity;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;

public class SortDialog extends DialogFragment implements
		View.OnClickListener {
	private int width, height;
	private TextView tv_sort_title,tv_QQ_share_way,tv_email_share_way,tv_null,tv_wexin_share_way,tv_http_share_way;
	private int SortType;
	public static  Context context;
	private Tencent mTencent;
	private static final String APP_ID = "1105395207";
	public void SetSortType(int sortType) {
		// TODO Auto-generated method stub
		this.SortType = sortType;

	}
	private void onClickShareToQQ() {
		try {
			Bundle params = new Bundle();
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, ShowResultActivity.fullPagePath);
			params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "快证通SDK");
			params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
			params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
			mTencent.shareToQQ((Activity) context, params, new BaseUiListener());
		}catch (Exception e)
		{

		}
	}
	public class BaseUiListener implements IUiListener {

		protected void doComplete(Object values) {

		}
		@Override
		public void onComplete(Object response) {
			doComplete(response);
		}

		@Override
		public void onError(UiError e) {
			showResult("onError:", "code:" + e.errorCode + ", msg:"
					+ e.errorMessage + ", detail:" + e.errorDetail);
		}

		@Override
		public void onCancel() {
			showResult("onCancel", "");
		}
	}
	private Handler mHandler;
	//private int mTargetScene = SendMessageToWX.Req.WXSceneSession;
	private void showResult(final String base, final String msg) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
			}
		});
	}
	@Override
	public void onClick(View v) {
		if(NetworkProber.isNetworkAvailable(context)){
			if (getResources().getIdentifier("tv_QQ_share_way", "id",
					context.getPackageName()) == v.getId()) {
				onClickShareToQQ();
				dismiss();
			}else if(getResources().getIdentifier("tv_email_share_way", "id",
					context.getPackageName()) == v.getId()){
				try {
					Intent email = new Intent(
							android.content.Intent.ACTION_SEND);
					if (ShowResultActivity.fullPagePath != null) {//flashImagepathString 为图片的本地路径
						email.setType("image/jpeg");
						File file = new File(ShowResultActivity.fullPagePath);
						Uri outputFileUri;
						if(android.os.Build.VERSION.SDK_INT <24)
						{
							outputFileUri = Uri.fromFile(file);
						}else{
							outputFileUri = FileProvider.getUriForFile(context.getApplicationContext(), "com.kernal.passportreader.myapplication.fileprovider", file);
						}

						email.putExtra(Intent.EXTRA_STREAM, outputFileUri);
					}
					email.setType("plain/text");
					email.putExtra(Intent.EXTRA_SUBJECT, "快证通SDK问题反馈");//邮件主题
					email.putExtra(Intent.EXTRA_TEXT, "");//邮件内容
					context.startActivity(email);
				}catch (Exception e)
				{

				}
				dismiss();
			}else if(getResources().getIdentifier("tv_wexin_share_way", "id",
					context.getPackageName()) == v.getId()){
//				Bitmap bitmap= BitmapFactory.decodeFile(ShowResultActivity.fullPagePath);
//				WXImageObject imgObj=new WXImageObject(bitmap);
//				WXMediaMessage msg=new WXMediaMessage();
//				msg.mediaObject=imgObj;
//				Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
//				bitmap.recycle();
//				msg.thumbData= com.kernal.passport.sdk.utils.weixin.Util.bmpToByteArray(thumbBmp,true);
//				SendMessageToWX.Req req = new SendMessageToWX.Req();
//				req.transaction = buildTransaction("img");
//				req.message = msg;
//				req.scene = mTargetScene;
//				api.sendReq(req);
				dismiss();

			}else if(getResources().getIdentifier("tv_http_share_way", "id",
					context.getPackageName()) == v.getId())
			{
				dismiss();
				HttpUploadDialog.context=context;
				HttpUploadDialog myHttpUploadDialog = new HttpUploadDialog();
				myHttpUploadDialog.show(getFragmentManager(), "HttpUploadDialog");
			}
		}else{
			Toast.makeText(context,context.getString(getResources().getIdentifier("networkunused", "string",
					context.getPackageName())),Toast.LENGTH_SHORT).show();
			dismiss();
		}

	}
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
    private  static  final  String APP_weixin_ID="wxd20371aa1b0f967a";
//	private IWXAPI api;
//	private void regToWx(){
//		api= WXAPIFactory.createWXAPI(context,APP_weixin_ID,true);
//		api.registerApp(APP_weixin_ID);
//	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		mTencent = Tencent.createInstance(APP_ID, context);
		mHandler = new Handler();
		//regToWx();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		View view = inflater.inflate(getResources().getIdentifier("sort_dialog",
				"layout", context.getPackageName()), null);
		tv_sort_title = (TextView) view.findViewById(getResources().getIdentifier("tv_sort_title",
				"id", context.getPackageName()));
		tv_QQ_share_way= (TextView) view.findViewById(getResources().getIdentifier("tv_QQ_share_way",
				"id", context.getPackageName()));
		tv_email_share_way= (TextView) view.findViewById(getResources().getIdentifier("tv_email_share_way",
				"id", context.getPackageName()));
		tv_null= (TextView) view.findViewById(getResources().getIdentifier("tv_null",
				"id", context.getPackageName()));
		tv_wexin_share_way= (TextView) view.findViewById(getResources().getIdentifier("tv_wexin_share_way",
				"id", context.getPackageName()));
		tv_http_share_way= (TextView) view.findViewById(getResources().getIdentifier("tv_http_share_way",
				"id", context.getPackageName()));
		LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
				(int) (width * 0.9), (int) (height * 0.07));
		tv_sort_title.setLayoutParams(lparams);
		tv_sort_title.setPadding((int) (width * 0.04), 0, 0, 0);

		lparams = new LinearLayout.LayoutParams(
				(int) (width * 0.9), (int) (height * 0.05));
		tv_QQ_share_way.setLayoutParams(lparams);
		tv_QQ_share_way.setPadding((int) (width * 0.1), 0, 0, 0);
		tv_QQ_share_way.setOnClickListener(this);
		lparams = new LinearLayout.LayoutParams(
				(int) (width * 0.9), (int) (height * 0.05));
		tv_email_share_way.setLayoutParams(lparams);
		tv_email_share_way.setPadding((int) (width * 0.1), 0, 0,0);
		tv_email_share_way.setOnClickListener(this);
		lparams = new LinearLayout.LayoutParams(
				(int) (width * 0.9), (int) (height * 0.05));
		tv_wexin_share_way.setLayoutParams(lparams);
		tv_wexin_share_way.setPadding((int) (width * 0.1), 0, 0,0);
		tv_wexin_share_way.setOnClickListener(this);
		lparams = new LinearLayout.LayoutParams(
				(int) (width * 0.9), (int) (height * 0.05));
		tv_http_share_way.setLayoutParams(lparams);
		tv_http_share_way.setPadding((int) (width * 0.1), 0, 0,0);
		tv_http_share_way.setOnClickListener(this);
		lparams = new LinearLayout.LayoutParams(
				(int) (width * 0.9), (int) (height * 0.02));
		tv_null.setLayoutParams(lparams);
		tv_null.setPadding((int) (width * 0.1), 0, 0,0);


		return view;
	}

	@Override
	public void show(FragmentManager manager, String tag) {
		// TODO Auto-generated method stub

		FragmentTransaction ft = manager.beginTransaction();
		ft.add(this, tag);
		ft.commit();
	}



}
