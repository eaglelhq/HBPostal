package com.ksource.hbpostal.bean;

import java.util.List;

public class HomeResultBean {
	public int flag;
    public String msg;
    public boolean success;

    public List<HomeListBean> homeList;


    public static class HomeListBean {
        public String ID;
        public String HOME_NAME;
//        public String TYPE_KEY;
//        public String TYPE_NAME;

    }
}
