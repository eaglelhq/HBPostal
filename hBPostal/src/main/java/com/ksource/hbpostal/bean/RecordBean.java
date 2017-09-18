package com.ksource.hbpostal.bean;

public class RecordBean {
	
	public String imgUrl;
	public String userName;
	public double price;
	public int jifen;
	public String recordTime;
	public RecordBean(String imgUrl, String userName, float price, int jifen,
			String recordTime) {
		super();
		this.imgUrl = imgUrl;
		this.userName = userName;
		this.price = price;
		this.jifen = jifen;
		this.recordTime = recordTime;
	}

	
	
}
