package com.ksource.hbpostal.bean;

public class BJPriceResultBean {


	/**
	 * price : {"PRICE":218,"MARKET_PRICE":3434}
	 * flag : 0
	 * msg : 获取商品价格成功
	 * success : true
	 */

	public PriceBean price;
	public int flag;
	public String msg;
	public boolean success;


	public static class PriceBean {
		/**
		 * PRICE : 218
		 * MARKET_PRICE : 3434
		 */

		public String PRICE;
		public String MARKET_PRICE;

	}
}
