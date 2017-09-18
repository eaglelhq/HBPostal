package com.yitao.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
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
	 * @param activity
	 * @param type
	 * @param requestCode
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
	public static String getAppInfo(Context context) {
		try {
			String pkName = context.getPackageName();
			String versionName = context.getPackageManager().getPackageInfo(pkName, 0).versionName;
			int versionCode = context.getPackageManager().getPackageInfo(pkName, 0).versionCode;
			return pkName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 启动照相机
	 */
	public static String startCarmera(String cache_url,Activity activity, int CAMERA_BACK){
		String imagePath = null;
		try {
			Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			imagePath = cache_url + UUID.randomUUID().toString() + ".jpg";
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
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
	 * 银行卡字符串加星号
	 */
	public static String showBankCard(String card){

		if (!TextUtils.isEmpty(card)){
			if (card.length()<8){
				return card;
			}else{
				return card.substring(0,3)+"*********"+card.substring(card.length()-4);
			}
		}
		return "";
	}

	/**
	 * 手机号字符串加星号
	 */
	public static String showStarPhone(String num){

		if (!TextUtils.isEmpty(num)){
			if (num.length()<8){
				return num;
			}else{
				return num.substring(0,3)+"****"+num.substring(num.length()-4);
			}
		}
		return "";
	}
	/**
	 * 姓名字符串加星号
	 * 第二位加*
	 */
	public static String showStarName(String str){

		if (!TextUtils.isEmpty(str)){
			if (str.length()<2){
				return str;
			}else{
				return str.substring(0,str.length()-1)+"*";
			}
		}
		return "";
	}

	/**
	 * 字符串加星号
	 * @param content 字符串
	 * @param begin 开始
	 * @param end 结束
	 * @return
	 */
	public static String showStarAddr(String content, int begin, int end){

		if (begin >= content.length() || begin < 0) {
			return content;
		}
		if (end >= content.length() || end < 0) {
			return content;
		}
		if (begin >= end) {
			return content;
		}
		String starStr = "";
		for (int i = begin; i < end; i++) {
			starStr = starStr + "*";
		}
		return content.substring(0, begin) + starStr + content.substring(end, content.length());

	}

	/**
	 * 对字符串处理:将指定位置到指定位置的字符以星号代替
	 *
	 * @param content
	 *            传入的字符串
	 * @param begin
	 *            开始位置
	 * @param end
	 *            结束位置
	 * @return
	 */
	private static String getStarString(String content, int begin, int end) {

		if (begin >= content.length() || begin < 0) {
			return content;
		}
		if (end >= content.length() || end < 0) {
			return content;
		}
		if (begin >= end) {
			return content;
		}
		String starStr = "";
		for (int i = begin; i < end; i++) {
			starStr = starStr + "*";
		}
		return content.substring(0, begin) + starStr + content.substring(end, content.length());

	}

	/**
	 * 对字符加星号处理：除前面几位和后面几位外，其他的字符以星号代替
	 *
	 * @param content
	 *            传入的字符串
	 * @param frontNum
	 *            保留前面字符的位数
	 * @param endNum
	 *            保留后面字符的位数
	 * @return 带星号的字符串
	 */

	private static String getStarString2(String content, int frontNum, int endNum) {

		if (frontNum >= content.length() || frontNum < 0) {
			return content;
		}
		if (endNum >= content.length() || endNum < 0) {
			return content;
		}
		if (frontNum + endNum >= content.length()) {
			return content;
		}
		String starStr = "";
		for (int i = 0; i < (content.length() - frontNum - endNum); i++) {
			starStr = starStr + "*";
		}
		return content.substring(0, frontNum) + starStr
				+ content.substring(content.length() - endNum, content.length());

	}

}