package com.yitao.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberUtil {

	 private static final int DEF_DIV_SCALE = 10;
	
	/**
	 * 四舍五入
	 * 
	 * @param v
	 * @param scale
	 * @return 作者：杨凯
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	

	/**
	 * 两数相加
	 * 
	 * @param v1
	 * @param v2
	 * @return 作者：杨凯
	 */
	public static Double add(Double v1, Double v2) {
		BigDecimal b1 = new BigDecimal(v1.toString());
		BigDecimal b2 = new BigDecimal(v2.toString());
		return b1.add(b2).doubleValue();
	}
	
	/**
	 * 两数相减
	 * @param v1
	 * @param v2
	 * @return
	 * 作者：杨凯
	 */
	public static Double sub(Double v1,Double v2){
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.subtract(b2).doubleValue();
    }
	
	 /**
	  * 两数相乘
	 * @param v1
	 * @param v2
	 * @return
	 * 作者：杨凯
	 */
	public static Double mul(Double v1,Double v2){
	        BigDecimal b1 = new BigDecimal(v1.toString());
	        BigDecimal b2 = new BigDecimal(v2.toString());
	        return b1.multiply(b2).doubleValue();
	    }
	/**
     * 两个Double数相除
     * @param v1
     * @param v2
     * @return Double
     */
    public static Double div(Double v1,Double v2){
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.divide(b2,DEF_DIV_SCALE,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 两个Double数相除，并保留scale位小数
     * @param v1
     * @param v2
     * @param scale
     * @return Double
     */
    public static Double div(Double v1,Double v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
            "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    /**
     * double四舍五入取整
     */
    public static BigDecimal doubleToLong(Double num){
    	return new BigDecimal(num).setScale(0, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 强制保留2位小数
     * @param x
     * @return String
     */
    public static String toDecimal2(double x) {              

    	DecimalFormat df = new DecimalFormat("0.00");
    	String db = df.format(x);             
    	return db;   
    }
    
    
    public static void main(String[] args) {
		System.out.println(0.01+0.01);
	}
}
