package com.ksource.hbpostal.bean;

import java.util.List;

public class PayDetailResultBean {

    /**
     * couponList : {"currPage":"","datas":[{"ID":"fa8eeaada25344daaf3dda7361472a85","COUPON_NAME":"满10元减1元","COUPON_CATE":2,"SHOP_NAME":"新大陆","TAKE_EFFECT_PRICE":10,"DENOMINATION":1}],"pageSize":"","rowNum":0}
     * isExist : 1
     * msg : 获取成功
     * success : true
     * info : {"ID":"7b279cc7bc0b42369015e8ca5896cf34","SELLER_ID":"fb55445134f2486f8bb536c0aa206bf0","MEMBER_ID":"5d71d941a4bd425191263dbf4ad9e46c","ORDER_ID":"DD-20170802-927585","PAY_FLOW_ID":"201708021525034656","PAY_WAY":2,"PAY_TIME":"20170802152503","PAY_TYPE":1,"APPEAL_STATE":0,"REPLY_STATE":0,"TOTAL_AMOUNT":12,"PAY_AMOUNT":0,"SCORE_NUM":11,"MONEY_SET":1,"MEMBER_LEAVE_MSG":"","SELLER_LEVEL_MSG":"","IS_CHECK_BILL":0,"APPEAL_CONTENT":null,"APPEAL_TIME":null,"REPLY_CONTENT":null,"REPLY_USER_ID":null,"REPLY_TIME":null,"IS_SETTLE":0,"IS_OUT_SETTLE_BILL":0,"SETTLE_BILL_ID":null,"CLOSE_USER_ID":null,"CLOSE_REASON":null,"CLOSE_TIME":null,"JS_ACCOUNT_NUM":null,"JS_ACCOUNT_NAME":null,"CLERK_ID":"4866b7ca221c4f4ca94d7fca58ae5782","DEPT_ID":"a0bfc2960a934939b5fe81c5461b309e","SHOP_NAME":"新大陆"}
     */

    public CouponListBean couponList;
    public int isExist;
    public String msg;
    public int flag;
    public boolean success;
    public InfoBean info;


    public static class CouponListBean {
        /**
         * currPage : 
         * datas : [{"ID":"fa8eeaada25344daaf3dda7361472a85","COUPON_NAME":"满10元减1元","COUPON_CATE":2,"SHOP_NAME":"新大陆","TAKE_EFFECT_PRICE":10,"DENOMINATION":1}]
         * pageSize : 
         * rowNum : 0
         */

        public String currPage;
        public String pageSize;
        public int rowNum;
        public List<DatasBean> datas;


        public static class DatasBean {
            /**
             * ID : fa8eeaada25344daaf3dda7361472a85
             * COUPON_NAME : 满10元减1元
             * COUPON_CATE : 2
             * SHOP_NAME : 新大陆
             * TAKE_EFFECT_PRICE : 10
             * DENOMINATION : 1
             */

            public String ID;
            public String COUPON_NAME;
            public int COUPON_CATE;
            public String SHOP_NAME;
            public int TAKE_EFFECT_PRICE;
            public int DENOMINATION;

        }
    }

    public static class InfoBean {
        /**
         * ID : 7b279cc7bc0b42369015e8ca5896cf34
         * SELLER_ID : fb55445134f2486f8bb536c0aa206bf0
         * MEMBER_ID : 5d71d941a4bd425191263dbf4ad9e46c
         * ORDER_ID : DD-20170802-927585
         * PAY_FLOW_ID : 201708021525034656
         * PAY_WAY : 2
         * PAY_TIME : 20170802152503
         * PAY_TYPE : 1
         * APPEAL_STATE : 0
         * REPLY_STATE : 0
         * TOTAL_AMOUNT : 12
         * PAY_AMOUNT : 0
         * SCORE_NUM : 11
         * MONEY_SET : 1
         * MEMBER_LEAVE_MSG : 
         * SELLER_LEVEL_MSG : 
         * IS_CHECK_BILL : 0
         * APPEAL_CONTENT : null
         * APPEAL_TIME : null
         * REPLY_CONTENT : null
         * REPLY_USER_ID : null
         * REPLY_TIME : null
         * IS_SETTLE : 0
         * IS_OUT_SETTLE_BILL : 0
         * SETTLE_BILL_ID : null
         * CLOSE_USER_ID : null
         * CLOSE_REASON : null
         * CLOSE_TIME : null
         * JS_ACCOUNT_NUM : null
         * JS_ACCOUNT_NAME : null
         * CLERK_ID : 4866b7ca221c4f4ca94d7fca58ae5782
         * DEPT_ID : a0bfc2960a934939b5fe81c5461b309e
         * SHOP_NAME : 新大陆
         */

        public String ID;
        public String SELLER_ID;
        public String MEMBER_ID;
        public String ORDER_ID;
        public String PAY_FLOW_ID;
        public int PAY_WAY;
        public String PAY_TIME;
        public int PAY_TYPE;
        public int APPEAL_STATE;
        public int REPLY_STATE;
        public String TOTAL_AMOUNT;
        public String PAY_AMOUNT;
        public int SCORE_NUM;
        public int MONEY_SET;
        public String MEMBER_LEAVE_MSG;
        public String SELLER_LEVEL_MSG;
        public int IS_CHECK_BILL;
        public String APPEAL_CONTENT;
        public String APPEAL_TIME;
        public String REPLY_CONTENT;
        public String REPLY_USER_ID;
        public String REPLY_TIME;
        public int IS_SETTLE;
        public int IS_OUT_SETTLE_BILL;
        public String SETTLE_BILL_ID;
        public String CLOSE_USER_ID;
        public String CLOSE_REASON;
        public String CLOSE_TIME;
        public String JS_ACCOUNT_NUM;
        public String JS_ACCOUNT_NAME;
        public String CLERK_ID;
        public String DEPT_ID;
        public String SHOP_NAME;
        public String PAY_CARD;
        public String DZSXM;
        public String CNAME;
    }
}
