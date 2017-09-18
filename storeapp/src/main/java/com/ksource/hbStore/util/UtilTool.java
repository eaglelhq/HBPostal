package com.ksource.hbStore.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.ksource.hbStore.config.ConstantValues;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * 工具类
 */
public class UtilTool {

	

	/**
	 * final Activity activity ：调用该方法的Activity实例 long milliseconds ：震动的时长，单位是毫秒
	 * long[] pattern ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
	 * boolean isRepeat ： 是否反复震动，如果是true，反复震动，如果是false，只震动一次
	 */

	public static void Vibrate(final Activity activity, long milliseconds) {
		Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	public static void Vibrate(final Activity activity, long[] pattern, boolean isRepeat) {
		Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}
	
	/**
	 * 发消息
	 * @param context
	 * @param phoneNum
	 */
	public static void sendMessage(Context context,String phoneNum){
		Intent intent = new Intent();
		// 系统默认的action，用来打开默认的短信界面
		intent.setAction(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("smsto:" + phoneNum));
		context.startActivity(intent);
	}
	
	/**
	 * 打电话
	 * @param context
	 * @param phoneNum
	 */
	public static void callPhoneNum(Context context,String phoneNum){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + phoneNum));
		context.startActivity(intent);
	}
	
	/**
	 * 打开设置界面
	 * @param context
	 */
	public static void openSettings(Context context){
		if(android.os.Build.VERSION.SDK_INT > 10){
			context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
		}else{
			context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}
	}
	
	/**
	 * 打开系统联系人页面
	 */
	public static void openLinkManActivity(Activity activity,int type,int requestCode){
	    if(type == 1){//打开通讯录
	    	Intent intent = new Intent();
	    	intent.setClassName("com.android.contacts","com.android.contacts.activities.PeopleActivity");
	    	activity.startActivity(intent); 
	    }else{//选择人员
	    	Intent intent = new Intent(Intent.ACTION_PICK,android.provider.ContactsContract.Contacts.CONTENT_URI);
	    	activity.startActivityForResult(intent, requestCode); 
	    }
	}

	/**
	 * 获取apk的包名称
	 * @param context
	 * @return
	 */
	public static String getVersionCode(Context context) {
		try {
			String pkName = context.getPackageName();
			String versionName = context.getPackageManager().getPackageInfo(pkName, 0).versionName;
			return versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 初始化个推
	 * @param context
	 */
//	public static void initGeTui(Context context) {
//		// 个推推送服务器初始化
//		Log.d("GetuiSdkDemo", "initializing sdk...");
//		PushManager.getInstance().initialize(context);
//		boolean isLooper = false;
//		// 回调得到 个推的 clientId
//		String clientId = PushManager.getInstance().getClientid(context);
//		while (!isLooper) {
//			if (TextUtils.isEmpty(clientId)) {
//				// 个推推送服务器初始化
//				PushManager.getInstance().initialize(context);
//				clientId = PushManager.getInstance().getClientid(context);
//			}
//			isLooper = PushManager.getInstance().bindAlias(context, "绑定的id");
//		}
//	}
	
	/**
	 * 启动照相机
	 */
	public static String startCarmera(Activity activity, int CAMERA_BACK){
		String imagePath = null;
		try {
			Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			imagePath = ConstantValues.CACHE_PATH + "/" + UUID.randomUUID().toString() + ".jpg";
			Uri imageUri = Uri.fromFile(new File(imagePath));
			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			activity.startActivityForResult(intentFromCapture, CAMERA_BACK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imagePath;
	}
	
	/**
	 * 相册中选择图片
	 */
	public static void chooseFromPictures(Activity activity,int IMAGE_PICK){
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		activity.startActivityForResult(intent, IMAGE_PICK);
	}
	
	/**
	 * 获取上传文件的类型
	 * 1--图片
	 * 2--录音
	 * 3--文件
	 */
	public static int getUploadType(String filePath){
		if(filePath.toLowerCase().endsWith(".jpg") || filePath.toLowerCase().endsWith(".png") || filePath.toLowerCase().endsWith(".jpeg")){
			return 1;
		}else if(filePath.toLowerCase().endsWith(".wav") || filePath.toLowerCase().endsWith(".amr") || filePath.toLowerCase().endsWith(".mp3")){
			return 2;
		}else{
			return 3;
		}
	}

	/**
	 * 参数加密
	 * @param params
	 * @return
	 */
	public static String paramsEncrypt(Map<String, String> params){
		String paramStr = "";
		Gson gson = new Gson();
		try {
			paramStr = gson.toJson(params);
			paramStr = AESOperator.getInstance().encrypt(paramStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramStr;
	}

	/**
	 * 银行卡字符串加星号
	 */
	public static String showBankCard(String card){

		if (!TextUtils.isEmpty(card)){
			if (card.length()<8){
				return "非法卡号";
			}else{
				return card.substring(0,3)+"*********"+card.substring(card.length()-4);
			}
		}
		return "";
	}
}