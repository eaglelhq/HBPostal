package com.ksource.hbpostal.bean;

import java.util.List;

public class UsefulYhqResultBean {

    /**
     * flag : 0
     * msg : 获取成功
     * success : true
     * info : [{"NAME":"满10元减1元","DENOMINATION":1,"TAKE_EFFECT_PRICE":10,"ID":"f6a47c5bc6ea4166b5c23d175961b82b"},{"NAME":"满10元减1元","DENOMINATION":1,"TAKE_EFFECT_PRICE":10,"ID":"10534a327c8c43f6b55420a597f453b1"}]
     */

    public int flag;
    public String msg;
    public boolean success;
    public List<InfoBean> info;

    public static class InfoBean {
        public InfoBean(String NAME, int DENOMINATION, int TAKE_EFFECT_PRICE, String ID) {
            this.NAME = NAME;
            this.DENOMINATION = DENOMINATION;
            this.TAKE_EFFECT_PRICE = TAKE_EFFECT_PRICE;
            this.ID = ID;
        }

        /**
         * NAME : 满10元减1元
         * DENOMINATION : 1
         * TAKE_EFFECT_PRICE : 10
         * ID : f6a47c5bc6ea4166b5c23d175961b82b
         */

        public String NAME;
        public int DENOMINATION;
        public int TAKE_EFFECT_PRICE;
        public String ID;

    }
}
