package com.ksource.hbStore.bean;

import java.util.List;

public class PayDetailResultBean {

    /**
     * ID : 2666cbf3bc0747a681ffde908e7f6b43
     * PAY_AMOUNT : 0
     * SCORE_NUM : 1
     * NAME : 江小磊
     * MEMBER_LEAVE_MSG :
     * PAY_TYPE : 1
     * PAY_TIME : 20170705164653
     * ORDER_ID : DD-20170705-958614
     * TOTAL_AMOUNT : 1
     * success : true
     * msg : 获取收款历史成功
     * flag : 0
     */

    public String ID;
    public String PAY_AMOUNT;
    public int SCORE_NUM;
    public String NAME;
    public String CNAME;
    public String DZSXM;
    public String MEMBER_LEAVE_MSG;
    public int PAY_TYPE;
    public String PAY_TIME;
    public String ORDER_ID;
    public String TOTAL_AMOUNT;
    public boolean success;
    public String msg;
    public int flag;
    public String SHOP_NAME;


    public String CLOSE_TIME;
    public int APPEAL_STATE;
    public String APPEAL_CONTENT;
    public int REPLY_STATE;
    public String REPLY_CONTENT;

    public InfoBean info;
    public int isExist;


    public static class InfoBean {
        /**
         * currPage :
         * datas : [{"ID":"1","COUPON_NAME":"满100减20","COUPON_CATE":2,"SHOP_NAME":"浚县卫贤柴王庄田国华缴费点","TAKE_EFFECT_PRICE":200,"DENOMINATION":100},{"ID":"2","COUPON_NAME":"满100减20","COUPON_CATE":2,"SHOP_NAME":"浚县卫贤柴王庄田国华缴费点","TAKE_EFFECT_PRICE":200,"DENOMINATION":100},{"ID":"3","COUPON_NAME":"满100减20","COUPON_CATE":2,"SHOP_NAME":"浚县卫贤柴王庄田国华缴费点","TAKE_EFFECT_PRICE":200,"DENOMINATION":100},{"ID":"10534a327c8c43f6b55420a597f453b1","COUPON_NAME":"满10元减1元","COUPON_CATE":2,"SHOP_NAME":"新大陆","TAKE_EFFECT_PRICE":10,"DENOMINATION":1}]
         * pageSize :
         * rowNum : 0
         */

        public String currPage;
        public String pageSize;
        public int rowNum;
        public List<DatasBean> datas;

        public static class DatasBean {
            /**
             * ID : 1
             * COUPON_NAME : 满100减20
             * COUPON_CATE : 2
             * SHOP_NAME : 浚县卫贤柴王庄田国华缴费点
             * TAKE_EFFECT_PRICE : 200
             * DENOMINATION : 100
             */

            public String ID;
            public String COUPON_NAME;
            public int COUPON_CATE;
            public String SHOP_NAME;
            public int TAKE_EFFECT_PRICE;
            public int DENOMINATION;
        }
    }
}
