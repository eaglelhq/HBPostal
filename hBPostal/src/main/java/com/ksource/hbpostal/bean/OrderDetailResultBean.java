package com.ksource.hbpostal.bean;

import java.io.Serializable;
import java.util.List;

public class OrderDetailResultBean implements Serializable{

    /**
     * ID : d40425fe4ca9450586b99ec43b48b82c
     * ORDER_ID : DD-20161027-207742
     * MEMBER_ID : 272dab18a37e4b6bb4bc9d48253c7c2c
     * STATE : 0
     * CREATE_TIME : 20161027195312
     * PAY_STATE : 0
     * PAY_TYPE : null
     * BUY_WAY : 0
     * TOTAL_PRICE : 5000
     * TOTAL_JF : 0
     * SEND_TYPE : 1
     * TAKE_PLACE :
     * TAKE_PLACE_ID :
     * ORDER_TYPE : 1
     * PROVINCE_ID : 2668e3fd58e84288a9a23cccaa902612
     * PROVINCE_NAME : 新疆维吾尔自治区
     * CITY_ID : 365d8d0215dc4b269d35c22f780a7f91
     * CITY_NAME : 阿拉尔
     * AREA_ID :
     * AREA_NAME :
     * ADDRESS : 哈哈哈哈哈哈
     * PERSON : null
     * MOBILE : 25555555
     * ORDER_NOTE :
     * SEND_USER_ID : null
     * SEND_USER_NAME : null
     * EXPRESS_CODE : null
     * ZF_TIME : null
     * FH_TIME : null
     * COMPLETE_TIME : null
     * PAY_CODE : null
     * DEL_STATE : 1
     * SEND_PRICE : null
     * EXPRESS : null
     * DHCZ_ID : null
     * DHCZ_NAME : null
     * BUY_NUM : 1
     * TOTAL_NUM : 1
     * TOTAL_MONEY : 5000
     * MEMBER_NAME : 会员名5
     * goodsList : [{"ID":"b3f16d66f9d4404c89da7f91780e7e1b","GOODS_ID":"1a8beaca5cfd48ccbc2c9fe88013477a","GOODS_NAME":"iPhone5","ONE_USE_SCORE":0,"ONE_USE_MONEY":5000,"BUY_NUM":1,"TOTAL_MONEY":5000,"TOTAL_SCORE":0,"GOODS_NOTE":null,"ORDER_ID":"d40425fe4ca9450586b99ec43b48b82c","GOODS_TYPE":null,"REVIEW_STATE":0,"GOODS_CODE":"22222221","IMAGE":"/upload/infofiles/20161028091920_3020.jpg","PAY_TYPE":1,"LV1_NAME":"电脑、办公","LV2_NAME":"水生动物","LV3_NAME":"鲸鱼"}]
     */

    public OrderInfoBean orderInfo;
    /**
     * orderInfo : {"ID":"d40425fe4ca9450586b99ec43b48b82c","ORDER_ID":"DD-20161027-207742","MEMBER_ID":"272dab18a37e4b6bb4bc9d48253c7c2c","STATE":0,"CREATE_TIME":20161027195312,"PAY_STATE":0,"PAY_TYPE":null,"BUY_WAY":0,"TOTAL_PRICE":5000,"TOTAL_JF":0,"SEND_TYPE":1,"TAKE_PLACE":"","TAKE_PLACE_ID":"","ORDER_TYPE":1,"PROVINCE_ID":"2668e3fd58e84288a9a23cccaa902612","PROVINCE_NAME":"新疆维吾尔自治区","CITY_ID":"365d8d0215dc4b269d35c22f780a7f91","CITY_NAME":"阿拉尔","AREA_ID":"","AREA_NAME":"","ADDRESS":"哈哈哈哈哈哈","PERSON":null,"MOBILE":"25555555","ORDER_NOTE":"","SEND_USER_ID":null,"SEND_USER_NAME":null,"EXPRESS_CODE":null,"ZF_TIME":null,"FH_TIME":null,"COMPLETE_TIME":null,"PAY_CODE":null,"DEL_STATE":1,"SEND_PRICE":null,"EXPRESS":null,"DHCZ_ID":null,"DHCZ_NAME":null,"BUY_NUM":1,"TOTAL_NUM":1,"TOTAL_MONEY":5000,"MEMBER_NAME":"会员名5","goodsList":[{"ID":"b3f16d66f9d4404c89da7f91780e7e1b","GOODS_ID":"1a8beaca5cfd48ccbc2c9fe88013477a","GOODS_NAME":"iPhone5","ONE_USE_SCORE":0,"ONE_USE_MONEY":5000,"BUY_NUM":1,"TOTAL_MONEY":5000,"TOTAL_SCORE":0,"GOODS_NOTE":null,"ORDER_ID":"d40425fe4ca9450586b99ec43b48b82c","GOODS_TYPE":null,"REVIEW_STATE":0,"GOODS_CODE":"22222221","IMAGE":"/upload/infofiles/20161028091920_3020.jpg","PAY_TYPE":1,"LV1_NAME":"电脑、办公","LV2_NAME":"水生动物","LV3_NAME":"鲸鱼"}]}
     * flag : 0
     * msg : 获取订单详情列表成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;


    public static class OrderInfoBean implements Serializable {
        public String ID;
        public String ORDER_ID;
        public String MEMBER_ID;
        public int STATE;
        public String CREATE_TIME;
        public int PAY_STATE;
        public int PAY_TYPE;
        public int BUY_WAY;
        public double TOTAL_PRICE;
        public String TOTAL_JF;
        public int SEND_TYPE;
        public String TAKE_PLACE;
        public String TAKE_PLACE_ID;
        public int ORDER_TYPE;
        public String PROVINCE_ID;
        public String PROVINCE_NAME;
        public String CITY_ID;
        public String CITY_NAME;
        public String AREA_ID;
        public String AREA_NAME;
        public String ADDRESS;
        public String PERSON;
        public String MOBILE;
        public String ORDER_NOTE;
        public String SEND_USER_ID;
        public String SEND_USER_NAME;
        public String EXPRESS_CODE;
        public String ZF_TIME;
        public String FH_TIME;
        public String COMPLETE_TIME;
        public String PAY_CODE;
        public int DEL_STATE;
        public String SEND_PRICE;
        public String EXPRESS;
        public String DHCZ_ID;
        public String DHCZ_NAME;
        public int BUY_NUM;
        public int TOTAL_NUM;
        public double TOTAL_MONEY;
        public String MEMBER_NAME;
        /**
         * ID : b3f16d66f9d4404c89da7f91780e7e1b
         * GOODS_ID : 1a8beaca5cfd48ccbc2c9fe88013477a
         * GOODS_NAME : iPhone5
         * ONE_USE_SCORE : 0
         * ONE_USE_MONEY : 5000
         * BUY_NUM : 1
         * TOTAL_MONEY : 5000
         * TOTAL_SCORE : 0
         * GOODS_NOTE : null
         * ORDER_ID : d40425fe4ca9450586b99ec43b48b82c
         * GOODS_TYPE : null
         * REVIEW_STATE : 0
         * GOODS_CODE : 22222221
         * IMAGE : /upload/infofiles/20161028091920_3020.jpg
         * PAY_TYPE : 1
         * LV1_NAME : 电脑、办公
         * LV2_NAME : 水生动物
         * LV3_NAME : 鲸鱼
         */

        public List<GoodsListBean> goodsList;


        public static class GoodsListBean implements Serializable{
            public String ID;
            public String GOODS_ID;
            public String GOODS_NAME;
            public String ONE_USE_SCORE;
            public String ONE_USE_MONEY;
            public int BUY_NUM;
            public int BUY_TYPE;
            public String TOTAL_MONEY;
            public String TOTAL_SCORE;
            public String GOODS_NOTE;
            public String ORDER_ID;
            public int GOODS_TYPE;
            public int REVIEW_STATE;
            public int STATE;
            public String GOODS_CODE;
            public String IMAGE;
            public String LV1_NAME;
            public String LV2_NAME;
            public String LV3_NAME;

        }
    }

	
	
}
