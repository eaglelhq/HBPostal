package com.ksource.hbStore.bean;

public class PayResultBean {


	/**
	 * content : {"PAY_TIME":"20170710153824","TOTAL_AMOUNT":"1.00","MESSAGEID":"5e024e217b9143f4b3d20b9ba66a4458"}
	 * title : 收钱成功
	 * url : 5e024e217b9143f4b3d20b9ba66a4458
	 * orderId : 5e024e217b9143f4b3d20b9ba66a4458
	 */

	public ContentBean content;
	public String title;
	public String url;
	public String orderId;



	public static class ContentBean {
		/**
		 * PAY_TIME : 20170710153824
		 * TOTAL_AMOUNT : 1.00
		 * MESSAGEID : 5e024e217b9143f4b3d20b9ba66a4458
		 */

		public String PAY_TIME;
		public String TOTAL_AMOUNT;
		public String MESSAGEID;


	}
}
