package com.ksource.hbStore.bean;

import java.util.List;

public class JSDListResultBean {

	/**
	 * history : [{"ID":"8dc704a3059f4a58ad3f643176303af5","SETTLE_CODE":"JSD-20170701-304155","OUT_SETTLE_BILL_TIME":20170701190333,"SETTLE_TIME":20170701190343,"SETTLE_STATE":1,"SETTLENUM":3,"TOTALAMOUNT":4}]
	 * flag : 0
	 * msg : 获取结算单成功
	 * success : true
	 */

	public int flag;
	public String msg;
	public boolean success;
	public List<HistoryBean> history;



	public static class HistoryBean {
		/**
		 * ID : 8dc704a3059f4a58ad3f643176303af5
		 * SETTLE_CODE : JSD-20170701-304155
		 * OUT_SETTLE_BILL_TIME : 20170701190333
		 * SETTLE_TIME : 20170701190343
		 * SETTLE_STATE : 1
		 * SETTLENUM : 3
		 * TOTALAMOUNT : 4
		 */
		public String ID;
		public String SETTLE_CODE;
		public String OUT_SETTLE_BILL_TIME;
		public String SETTLE_TIME;
		public int SETTLE_STATE;
		public int SETTLENUM;
		public double TOTALAMOUNT;

	}
}
