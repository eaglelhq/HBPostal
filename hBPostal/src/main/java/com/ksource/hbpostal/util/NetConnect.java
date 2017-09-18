package com.ksource.hbpostal.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.ksource.hbpostal.MyApplication;

/***
 * UI帮助类
 * 
 * @author ld
 */
public class NetConnect {

	private NetConnect() {

	}
	private static NetConnect netConnect;
	
	/**
	 * 判断是否有网络连接
	 * 
	 * @return isConnection
	 */
	public static boolean isConnection()
	{
		try {
			ConnectivityManager _ConnectivityMabager = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo _NetworkInfo = _ConnectivityMabager.getActiveNetworkInfo();
			return _NetworkInfo.isConnected();
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @return 实例
	 */
	public static NetConnect getInstance(){
		if (netConnect == null) {
			netConnect = new NetConnect();
		}
		return netConnect;
	}
	/***
	 * 网络判断
	 */
	public  boolean JudgeNet(Context ctx) {
		ConnectivityManager conManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		NetworkInfo mobileInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if ((networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) || (null != wifiInfo && wifiInfo.isAvailable() && wifiInfo.isConnected()) || (null != mobileInfo && mobileInfo.isAvailable() && mobileInfo.isConnected())) {
			return true;
		}
		return false;
	}

	/***
	 * 网络判断
	 */
	public  boolean isNetConnect(final Context ctx) {
		if (!JudgeNet(ctx)) {
			Toast.makeText(ctx, "网络连接异常,请检查您的网络连接", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}
	/***
	 * 网络判断不提示
	 */
	public  boolean isNetConnectNotTitle(final Context ctx) {
		if (!JudgeNet(ctx)) {
			return false;
		} else {
			return true;
		}
	}
	/**
	 * 判断WIFI是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWIFIConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (info != null) {
			return info.isConnected();
		}
		return false;
	}
}
