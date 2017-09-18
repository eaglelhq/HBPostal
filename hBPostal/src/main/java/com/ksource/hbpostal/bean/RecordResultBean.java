package com.ksource.hbpostal.bean;

import java.util.List;

public class RecordResultBean {
    public int flag;
    public String msg;
    public boolean success;
    public List<GoodsByRecordListBean> goodsByRecordList;
    public List<OrderListBean> orderList;


    public static class GoodsByRecordListBean {
        public String CREATE_TIME;
        public String NAME;
        public String HEAD_IMAGE;
        public String NICK_NAME;
        public String TOTAL_MONEY;
        public String TOTAL_SCORE;

    }
    public static class OrderListBean {
        public String create_time;
        public String person;
        public String head_image;
        public String nick_name;
        public String total_price;

    }
}
