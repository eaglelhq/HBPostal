package com.ksource.hbpostal.bean;

import java.util.List;

public class MyYHQResultBean {

    /**
     * couponList : [{"COUPON_STATE":4,"COUPON_ID":"0b891fb894a5481ebb05b4f23263f51a","COUPON_NAME":"满10元减1元","COUPON_CATE":2,"SHOP_NAME":"新大陆","USE_GOLD_COINS":11,"VALID_DAYS":11,"TAKE_EFFECT_PRICE":10,"DENOMINATION":1,"COUPON_TYPE":1},{"COUPON_STATE":4,"COUPON_ID":"0b891fb894a5481ebb05b4f23263f51a","COUPON_NAME":"满10元减1元","COUPON_CATE":2,"SHOP_NAME":"新大陆","USE_GOLD_COINS":11,"VALID_DAYS":11,"TAKE_EFFECT_PRICE":10,"DENOMINATION":1,"COUPON_TYPE":1},{"COUPON_STATE":4,"COUPON_ID":"0b891fb894a5481ebb05b4f23263f51a","COUPON_NAME":"满10元减1元","COUPON_CATE":2,"SHOP_NAME":"新大陆","USE_GOLD_COINS":11,"VALID_DAYS":11,"TAKE_EFFECT_PRICE":10,"DENOMINATION":1,"COUPON_TYPE":1}]
     * flag : 0
     * msg : 获取我的优惠券成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;
    public List<CouponListBean> couponList;


    public static class CouponListBean {
        /**
         * COUPON_STATE : 4
         * COUPON_ID : 0b891fb894a5481ebb05b4f23263f51a
         * COUPON_NAME : 满10元减1元
         * COUPON_CATE : 2
         * SHOP_NAME : 新大陆
         * USE_GOLD_COINS : 11
         * VALID_DAYS : 11
         * TAKE_EFFECT_PRICE : 10
         * DENOMINATION : 1
         * COUPON_TYPE : 1
         */

        public int COUPON_STATE;
        public String COUPON_ID;
        public String COUPON_NAME;
        public int COUPON_CATE;
        public String SHOP_NAME;
        public String RECEIVE_TIME;
        public int USE_GOLD_COINS;
        public int VALID_DAYS;
        public String TAKE_EFFECT_PRICE;
        public String DENOMINATION;
        public int COUPON_TYPE;
        public String OUT_TIME;
        public int DAILY_LIMIT;
    }
}
