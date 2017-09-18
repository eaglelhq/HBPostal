package com.ksource.hbpostal.bean;

public class SHJFItem {
	
	private int itemId;
	private String bindNum;
	private String bindName;
	private boolean isBind;
	
	
	
	public SHJFItem(int itemId, String bindNum,
			String bindName, boolean isBind) {
		super();
		this.itemId = itemId;
		this.bindNum = bindNum;
		this.bindName = bindName;
		this.isBind = isBind;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getBindNum() {
		return bindNum;
	}
	public void setBindNum(String bindNum) {
		this.bindNum = bindNum;
	}
	public String getBindName() {
		return bindName;
	}
	public void setBindName(String bindName) {
		this.bindName = bindName;
	}
	public boolean isBind() {
		return isBind;
	}
	public void setBind(boolean isBind) {
		this.isBind = isBind;
	}

}
