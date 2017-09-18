package com.ksource.hbpostal.bean;

public class FAQResaultBean {

	/**
	 * flag : 0 cateList :
	 * [{"ID":"9fe5d18674f44b05a53cd72ee2f3528f","LEVEL":1,"UP_ID"
	 * :null,"LV1_NAME":"感受得到的","LV2_NAME":null,"ejTree":[{"ID":
	 * "b3bb46a0d4814f43bca4070d7b3c5d64"
	 * ,"LEVEL":2,"UP_ID":"9fe5d18674f44b05a53cd72ee2f3528f"
	 * ,"LV1_NAME":"而特人","LV2_NAME"
	 * :null,"sjTree":[]},{"ID":"5d26e34035244963931e6d28a70f99fa"
	 * ,"LEVEL":2,"UP_ID"
	 * :"9fe5d18674f44b05a53cd72ee2f3528f","LV1_NAME":"sdf","LV2_NAME"
	 * :"感受得到的","sjTree":[]}]}] msg : 获取常见问题分类成功 success : true
	 */

	public int flag;
	public String msg;
	public boolean success;
	/**
	 * ID : 9fe5d18674f44b05a53cd72ee2f3528f LEVEL : 1 UP_ID : null LV1_NAME :
	 * 感受得到的 LV2_NAME : null ejTree :
	 * [{"ID":"b3bb46a0d4814f43bca4070d7b3c5d64","LEVEL"
	 * :2,"UP_ID":"9fe5d18674f44b05a53cd72ee2f3528f"
	 * ,"LV1_NAME":"而特人","LV2_NAME":
	 * null,"sjTree":[]},{"ID":"5d26e34035244963931e6d28a70f99fa"
	 * ,"LEVEL":2,"UP_ID"
	 * :"9fe5d18674f44b05a53cd72ee2f3528f","LV1_NAME":"sdf","LV2_NAME"
	 * :"感受得到的","sjTree":[]}]
	 */

	public java.util.List<CateListBean> cateList;

	

	public static class CateListBean {
		public String ID;
		public int LEVEL;
		public String UP_ID;
		public String LV1_NAME;
		public String LV2_NAME;
		/**
		 * ID : b3bb46a0d4814f43bca4070d7b3c5d64 LEVEL : 2 UP_ID :
		 * 9fe5d18674f44b05a53cd72ee2f3528f LV1_NAME : 而特人 LV2_NAME : null
		 * sjTree : []
		 */

		public java.util.List<EjTreeBean> ejTree;

		

		public static class EjTreeBean {
			public String ID;
			public int LEVEL;
			public String UP_ID;
			public String LV1_NAME;
			public String LV2_NAME;
			public java.util.List<?> sjTree;


			public static class SjTreeBean {
				public String ID;
				public int LEVEL;
				public String UP_ID;
				public String LV1_NAME;
				public String LV2_NAME;
			}
		}
	}

}
