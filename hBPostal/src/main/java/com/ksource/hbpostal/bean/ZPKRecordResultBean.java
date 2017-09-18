package com.ksource.hbpostal.bean;

import java.util.List;

public class ZPKRecordResultBean {

	/**
	 * flag : 0
	 * zpkRecord : [{"KEY_NAME":"5人","OPTION_KEY_NAME":"1次","ORDER_ID":"DD-20170316-927211","ID":"0e57bfe2aa2e43d0ae09d8e3596f34ac","TOTAL_PRICE":26,"PAY_STATE":1,"DISTRIBU_TIME":"0100100","NAME":"sdf","IMAGE":"/upload/infofiles/20170311193252_2416.jpg","all_num":2,"yps_num":0},{"KEY_NAME":"5人","OPTION_KEY_NAME":"1次","ORDER_ID":"DD-20170316-909929","ID":"23776935084a4809a3b40f3460541e6f","TOTAL_PRICE":26,"PAY_STATE":0,"DISTRIBU_TIME":null,"NAME":"sdf","IMAGE":"/upload/infofiles/20170311193252_2416.jpg","all_num":0,"yps_num":0},{"KEY_NAME":"5人","OPTION_KEY_NAME":"1次","ORDER_ID":"DD-20170316-495664","ID":"33cc07fe5c834d049b0b138bddf28e17","TOTAL_PRICE":26,"PAY_STATE":0,"DISTRIBU_TIME":null,"NAME":"sdf","IMAGE":"/upload/infofiles/20170311193252_2416.jpg","all_num":0,"yps_num":0},{"KEY_NAME":"3人","OPTION_KEY_NAME":"1次","ORDER_ID":"DD-20170316-295242","ID":"39483800ca4145a2a0ee46cc5592b149","TOTAL_PRICE":26,"PAY_STATE":0,"DISTRIBU_TIME":null,"NAME":"sdf","IMAGE":"/upload/infofiles/20170311193252_2416.jpg","all_num":0,"yps_num":0},{"KEY_NAME":"5人","OPTION_KEY_NAME":"1次","ORDER_ID":"DD-20170316-584554","ID":"47cc616d8c134c9b9942e6effc4cc863","TOTAL_PRICE":26,"PAY_STATE":0,"DISTRIBU_TIME":null,"NAME":"sdf","IMAGE":"/upload/infofiles/20170311193252_2416.jpg","all_num":0,"yps_num":0},{"KEY_NAME":"3人","OPTION_KEY_NAME":"1季","ORDER_ID":"DD-20170316-197035","ID":"4829d2bd44174038924e65a7d60ae61e","TOTAL_PRICE":200,"PAY_STATE":1,"DISTRIBU_TIME":"0100100","NAME":"sdf","IMAGE":"/upload/infofiles/20170311193252_2416.jpg","all_num":81,"yps_num":0},{"KEY_NAME":"3人","OPTION_KEY_NAME":"1季","ORDER_ID":"DD-20170316-510063","ID":"6b58fa594b48462e922cda4d21022333","TOTAL_PRICE":200,"PAY_STATE":0,"DISTRIBU_TIME":null,"NAME":"sdf","IMAGE":"/upload/infofiles/20170311193252_2416.jpg","all_num":0,"yps_num":0},{"KEY_NAME":"5人","OPTION_KEY_NAME":"1次","ORDER_ID":"DD-20170316-610621","ID":"7da3fc08194246de94d3d63def3f0eca","TOTAL_PRICE":26,"PAY_STATE":0,"DISTRIBU_TIME":null,"NAME":"sdf","IMAGE":"/upload/infofiles/20170311193252_2416.jpg","all_num":0,"yps_num":0},{"KEY_NAME":"5人","OPTION_KEY_NAME":"1次","ORDER_ID":"DD-20170316-636991","ID":"922d712695814d92bd10ad855e0c55dc","TOTAL_PRICE":26,"PAY_STATE":0,"DISTRIBU_TIME":null,"NAME":"sdf","IMAGE":"/upload/infofiles/20170311193252_2416.jpg","all_num":0,"yps_num":0},{"KEY_NAME":"3人","OPTION_KEY_NAME":"1季","ORDER_ID":"DD-20170316-846049","ID":"9330b03a349a4d6da36edaed5cf80ded","TOTAL_PRICE":233,"PAY_STATE":0,"DISTRIBU_TIME":null,"NAME":"宅配卡1","IMAGE":"/upload/infofiles/20170315095906_1257.jpg","all_num":0,"yps_num":0}]
	 * msg : 获取成功
	 * success : true
	 */

	public int flag;
	public String msg;
	public boolean success;
	public List<ZpkRecordBean> zpkRecord;


	public static class ZpkRecordBean {
		/**
		 * KEY_NAME : 5人
		 * OPTION_KEY_NAME : 1次
		 * ORDER_ID : DD-20170316-927211
		 * ID : 0e57bfe2aa2e43d0ae09d8e3596f34ac
		 * TOTAL_PRICE : 26
		 * PAY_STATE : 1
		 * DISTRIBU_TIME : 0100100
		 * NAME : sdf
		 * IMAGE : /upload/infofiles/20170311193252_2416.jpg
		 * all_num : 2
		 * yps_num : 0
		 */

		public String KEY_NAME;
		public String OPTION_KEY_NAME;
		public String ORDER_ID;
		public String ID;
		public String TOTAL_PRICE;
		public int PAY_STATE;
		public String DISTRIBU_TIME;
		public String NAME;
		public String IMAGE;
		public int all_num;
		public int yps_num;

	}
}
