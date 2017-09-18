package com.ksource.hbpostal.bean;

public class AccountInfoResultBean {
	public int flag;
	public String msg;
	public boolean success;
	public String pay_unit_name;
	public String PAY_UNIT_KEY;
	public String accountNumber;
	public String GD_PAPERTYPE;
	public PamentMapBean pamentMap;
	public String error;

    public class PamentMapBean{
		public String accountName;
		public String address;
		public String area_name;
		public double money;
		public String late_month;
		public String paymentBal;
		public String queryId;
		public boolean success;
		public int isSd;
		
	}
	
	public String sumScore;
	public String memberScore;
}
