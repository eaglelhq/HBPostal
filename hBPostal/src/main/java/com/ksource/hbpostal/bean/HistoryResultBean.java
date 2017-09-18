package com.ksource.hbpostal.bean;

import java.util.List;

public class HistoryResultBean {

	public int flag;
	public String msg;
	public boolean success;
	public int sumCount;


	public List<PaymentListBean> paymentList;

	public static class PaymentListBean {
		public String ID;
		public String PAY_TIME;
		public String FACT_MONEY;
		public String TOTAL_INTEGRAL;
		public int PAY_STATE;
		public String ACCOUNT_ID;
		public String ACCOUNT_NAME;
		public String UPDATE_TIME;
		public String MEMBER_NAME;
		public String DEVICE_NUM;
		public String GD_PAPERNO;
		public String GD_PAPERTYPE;

	}

}
