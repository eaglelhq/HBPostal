package com.ksource.hbpostal.bean;

import java.util.List;

public class NewActResaultBean {


    public int flag;
    public String msg;
    public boolean success;

    public List<ZxhdxxListBean> zxhdxxList;

   

    public static class ZxhdxxListBean {
        public int NUM;
        public String TITLE;
        public String UPDATE_TIME;
        public int TYPE;

       
    }

}
