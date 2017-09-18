package com.ksource.hbpostal.bean;

public class MenberDetailResultBean {




    /**
     * flag : 0
     * memberInfo : {"NAME":"会员名5","NICK_NAME":null,"SEX":"男","ID_CARD":"412856198909351258","BIRTHDAY":"19890935","HEAD_IMAGE":null,"CUSTOMER_MANAGER_NAME":"电子商务三级","MOBILE":"13999999999","PROVINCE_ID":"dedc1d32f92a404dab38b2383f8039fb","PROVINCE_NAME":"湖南省","CITY_ID":"d8aa514e779b4b8bb3c3140294d0af41","CITY_NAME":"长沙市","AREA_ID":"fc12a228cf2940b6b379197a31b25544","AREA_NAME":"芙蓉区","ADDRESS":"ASF撒大个"}
     * msg : 获取会员详细信息成功
     * success : true
     */

    public int flag;
    /**
     * NAME : 会员名5
     * NICK_NAME : null
     * SEX : 男
     * ID_CARD : 412856198909351258
     * BIRTHDAY : 19890935
     * HEAD_IMAGE : null
     * CUSTOMER_MANAGER_NAME : 电子商务三级
     * MOBILE : 13999999999
     * PROVINCE_ID : dedc1d32f92a404dab38b2383f8039fb
     * PROVINCE_NAME : 湖南省
     * CITY_ID : d8aa514e779b4b8bb3c3140294d0af41
     * CITY_NAME : 长沙市
     * AREA_ID : fc12a228cf2940b6b379197a31b25544
     * AREA_NAME : 芙蓉区
     * ADDRESS : ASF撒大个
     */

    public MemberInfoBean memberInfo;
    public String msg;
    public boolean success;


    public static class MemberInfoBean {
        public String NAME;
        public String NICK_NAME;
        public String SEX;
        public String ID_CARD;
        public String BIRTHDAY;
        public String HEAD_IMAGE;
        public String CUSTOMER_MANAGER_NAME;
        public String MOBILE;
        public String PROVINCE_ID;
        public String PROVINCE_NAME;
        public String CITY_ID;
        public String CITY_NAME;
        public String AREA_ID;
        public String AREA_NAME;
        public String ADDRESS;
        public int PAY_PWD;
    }

}
