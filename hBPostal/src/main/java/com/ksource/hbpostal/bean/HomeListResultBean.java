package com.ksource.hbpostal.bean;

import java.util.List;

public class HomeListResultBean {
	public int flag;
    public String msg;
    public boolean success;

    public List<PamentAccountListBean> pamentAccountList;


    public static class PamentAccountListBean {
        public String accountId;//账户ID
        public int accountType;//缴费类型
        public String accountTypeName;//缴费类型名
        public String accountNumber;//户号
        public String accountName;//户名
		public String key;//缴费单位key
		public String keyName;//缴费单位name

    }
}
