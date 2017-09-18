package com.ksource.hbpostal.bean;


public class DefAddrResaultBean {


    public int flag;
    public String msg;
    public boolean success;
    public AddressBean address;

    public static class AddressBean {
        public String ID;
        public String NAME;
        public String MOBILE;
        public String PROVINCE_ID;
        public String PROVINCE_NAME;
        public String CITY_ID;
        public String CITY_NAME;
        public String AREA_ID;
        public String AREA_NAME;
        public String ADDRESS;
        public int IS_DEFAULT_ADDRESS;
        public Object ADD_TIME;
        public String MEMBER_ID;

       
    }

}
