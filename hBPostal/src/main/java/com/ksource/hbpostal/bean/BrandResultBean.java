package com.ksource.hbpostal.bean;

import java.util.List;
import java.util.Map;

public class BrandResultBean {

	/**
	 * flag : 2 success : true msg : 成功
	 * 退出成功  加入购物车成功  删除商品  增删改收货地址
	 */

	public int flag;
	public boolean success;
	public String msg;

	public List<Map<String, String>> brandList;

	@Override
	public String toString() {
		return "LoginResult [flag=" + flag + ", success=" + success + ", msg="
				+ msg + "]";
	}

}
