package com.ksource.hbpostal.bean;

import java.util.List;

public class DeliverResaultBean {

	public int showapi_res_code;
	public String showapi_res_error;
	public ShowapiResBodyBean showapi_res_body;


	public static class ShowapiResBodyBean {

		public String mailNo;
		public long update;
		public String updateStr;
		public String msg;
		public int ret_code;
		public boolean flag;
		public int status;
		public String tel;
		public String expSpellName;
		public String expTextName;
		public List<DataBean> data;


		public static class DataBean {
			/**
			 * time : 2016-12-14 13:26:07 context : [河南郑州公司高新技术开发区分部]快件已被 已签收 签收
			 */

			public String time;
			public String context;

		}
	}
}
