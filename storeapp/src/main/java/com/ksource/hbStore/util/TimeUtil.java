package com.ksource.hbStore.util;

import android.text.TextUtils;

public class TimeUtil {

	/**
	 * 2016-11-16 11:05
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTimeMin(String time) {
		String resultTime = "";
		if (!TextUtils.isEmpty(time)) {
			resultTime = time.substring(0, 4) + "-" + time.substring(4, 6)
					+ "-" + time.substring(6, 8) + " " + time.substring(8, 10)
					+ ":" + time.substring(10, 12);
		}
		return resultTime;
	}

	/**
	 * 2016-11-16 11:05:20
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTimeSec(String time) {
		String resultTime = "";
		if (!TextUtils.isEmpty(time)) {
			resultTime = time.substring(0, 4) + "-" + time.substring(4, 6)
					+ "-" + time.substring(6, 8) + " " + time.substring(8, 10)
					+ ":" + time.substring(10, 12) + ":"
					+ time.substring(12, 14);
		}
		return resultTime;
	}

	/**
	 * 2016-11-16
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTimeDay(String time) {
		String resultTime = "";
		if (!TextUtils.isEmpty(time)) {
			resultTime = time.substring(0, 4) + "-" + time.substring(4, 6)
					+ "-" + time.substring(6, 8);
		}
		return resultTime;
	}

	/**
	 * 2016年11月
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTimeMon(String time) {
		String resultTime = "";
		if (!TextUtils.isEmpty(time)) {
			resultTime = time.substring(0, 4) + "年" + time.substring(4, 6)
					+ "月";
		}
		return resultTime;
	}

	/**
	 * 2016年11月12日
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTimeDayHZ(String time) {
		String resultTime = "";
		if (!TextUtils.isEmpty(time)) {
			resultTime = time.substring(0, 4) + "年" + time.substring(4, 6)
					+ "月" + time.substring(6, 8) + "日";
		}
		return resultTime;
	}
}
