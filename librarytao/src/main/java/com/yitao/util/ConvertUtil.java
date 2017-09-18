package com.yitao.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;



public class ConvertUtil {
	
	public static Boolean obj2Boolean(Object obj){
		return obj == null ? false : Boolean.getBoolean(obj.toString());
	}
	
	public static Double obj2Double(Object obj){
		if (obj == null) {
			return null;
		}
		return TextUtils.isEmpty(obj2Str(obj)) ? null : Double.valueOf(obj.toString());
	}
	
	public static void main(String[] args) {
		String str = "0.123";
		System.out.println(obj2Double(str));
	}
	
	
	/**
	 * List转Map
	 * @param dataList
	 * @param key
	 * @return
	 * 作者：杨凯
	 */
	public static Map list2Map(List<Map> dataList,Object key){
		Map resultMap = new HashMap();
		if (dataList != null && dataList.size() > 0) {
			for (Map map : dataList) {
				if (map.get(key) != null) {
					resultMap.put(map.get(key), map);
				}
			}
		}
		return resultMap;
	}
	
	/**
	 * List转Map
	 * @param dataList
	 * @param key
	 * @param value
	 * @return
	 * 作者：杨凯
	 */
	public static Map list2Map(List<Map> dataList,Object key,Object value){
		Map resultMap = new LinkedHashMap();
		if (dataList != null && dataList.size() > 0) {
			for (Map map : dataList) {
				resultMap.put(map.get(key), map.get(value));
			}
		}
		return resultMap;
	}
 	
	
	
	
	/**將GBK转化为GBK
	 * @param str
	 * @return
	 * 作者：杨凯
	 */
	public static String gbk2UTF8(String str){
		if (TextUtils.isEmpty(str)) {
			return str;
		}
		try {
			str = URLEncoder.encode(str, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static String html2Text(String html){
		return html.replaceAll("<[^>]+>", "").replaceAll("&nbsp;","");
	}
	
	/**
	 * 功能描述：Object类转换为String，避免在Object为null时，直接toString()出错<BR>
	 * @param obj
	 * @return
	 * @author:杨凯<BR>
	 * 时间：Mar 22, 2009 10:17:29 PM<BR>
	 */
	public static String obj2Str(Object obj) {
		return obj == null ? null : obj.toString();
	}
	public static String obj2StrBlank(Object obj) {
		return obj == null ? "" : obj.toString();
	}
	public static Integer obj2Integer(Object obj){
		return obj == null || obj.toString().trim().equals("")  ? null : Integer.parseInt(obj.toString()); 
	}
	
	public static int obj2Int(Object obj){
		if(null==obj) {
			System.out.println("1121313");
		}
		return obj == null || obj.toString().trim().equals("")  ? null : Double.valueOf(obj.toString()).intValue(); 
	}
	
	public static Long obj2Long(Object obj){
		return obj == null || obj.toString().trim().equals("") ? null : Long.parseLong(obj.toString()); 
	}
	
	public static Long obj2Long(Object obj,boolean filter){
		if (!filter) {
			return obj == null || obj.toString().trim().equals("") ? null : Long.parseLong(obj.toString()); 
		} else {
			return obj == null || obj.toString().trim().equals("") ? null : Long.parseLong(obj.toString().replaceAll("[^0-9]", "")); 
		}
	}
	

    
    /**
     * 将byte转化为K或M，大于1024K的按M计算，返回*K或*M
     * @param byteSize
     * @param scale 保留小数位数
     * @return
     */
    public static String byte2KM(double byteSize, int scale) {
    	BigDecimal b1 = new BigDecimal(byteSize);
        BigDecimal b2 = new BigDecimal(1024);
        double k = b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    	
        //大于1024，转化为M
    	if (k > 1024) {
    		b2 = new BigDecimal(1024*1024);
    		k = b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    		
    		return k + "M";
    	}
    	
    	return k + "K";
    }
}
