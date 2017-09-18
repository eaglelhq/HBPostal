package com.ksource.hbStore.bean;

import java.util.List;

public class CalResultBean {
	/**
	 * toBeSettle : []
	 * flag : 0
	 * clsdSettle : [{"YEAR":"2017","MONTH":"07","DAY":"01"}]
	 * msg : 获取结算单日历成功
	 * success : true
	 */

	public int flag;
	public String msg;
	public boolean success;
	public List<ToBeSettleBean> toBeSettle;
	public List<ClsdSettleBean> clsdSettle;

	public static class ClsdSettleBean {
		/**
		 * YEAR : 2017
		 * MONTH : 07
		 * DAY : 01
		 */
		public String YEAR;
		public String MONTH;
		public String DAY;
	}
	public static class ToBeSettleBean {
		/**
		 * YEAR : 2017
		 * MONTH : 07
		 * DAY : 01
		 */
		public String YEAR;
		public String MONTH;
		public String DAY;


	}
}
