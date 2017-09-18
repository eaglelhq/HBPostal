package com.ksource.hbpostal.bean;

import java.util.List;


public class PostalResultBean {
	


    public int flag;
    public String msg;
    public boolean success;
    public double money;
    public List<PostalBean> branchList;

    public static class PostalBean {
        public String ID;
        public String NAME;

    }


}
