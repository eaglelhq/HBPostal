package com.ksource.hbpostal.bean;

import java.util.List;

public class CompanyResultBean {
	public int flag;
    public String msg;
    public boolean success;

    public List<CompanyListBean> companyList;


    public static class CompanyListBean {
        public String DATAKEY;
        public String DATAVALUE;
//        public String TYPE_KEY;
//        public String TYPE_NAME;

    }
}
