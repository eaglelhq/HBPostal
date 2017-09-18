package com.ksource.hbpostal.bean;


public class HomeInfoResultBean {
    /**
     * flag : 0
     * msg : 获取家庭详情成功
     * success : true
     * info : {"ID":"d2f8e28f84bb48fd9f7715d5616d3cbe","TYPE_KEY":"WDJT","TYPE_NAME":"我的","HOME_NAME":"我的","PROVINCE_ID":null,"PROVINCE_NAME":null,"CITY_ID":null,"CITY_NAME":null,"AREA_ID":null,"AREA_NAME":null,"ADDRESS":null}
     */

    public int flag;
    public String msg;
    public boolean success;
    public InfoBean info;

    public static class InfoBean {
        /**
         * ID : d2f8e28f84bb48fd9f7715d5616d3cbe
         * TYPE_KEY : WDJT
         * TYPE_NAME : 我的
         * HOME_NAME : 我的
         * PROVINCE_ID : null
         * PROVINCE_NAME : null
         * CITY_ID : null
         * CITY_NAME : null
         * AREA_ID : null
         * AREA_NAME : null
         * ADDRESS : null
         */

        public String ID;
//        public String TYPE_KEY;
//        public String TYPE_NAME;
        public String HOME_NAME;
        public String PROVINCE_ID;
        public String PROVINCE_NAME;
        public String CITY_ID;
        public String CITY_NAME;
        public String AREA_ID;
        public String AREA_NAME;
        public String ADDRESS;

        
    }
}
