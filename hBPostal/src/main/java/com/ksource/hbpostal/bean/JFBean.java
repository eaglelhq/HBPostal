package com.ksource.hbpostal.bean;

public class JFBean {
	
	public String userName;
	public String time;
	public int jinbi;
	public String userNum;
	public double money;
	public JFBean(String userName, String time, int jinbi, String userNum,
			float money) {
		super();
		this.userName = userName;
		this.time = time;
		this.jinbi = jinbi;
		this.userNum = userNum;
		this.money = money;
	}

}
