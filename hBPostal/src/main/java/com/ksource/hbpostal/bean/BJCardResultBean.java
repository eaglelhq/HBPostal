package com.ksource.hbpostal.bean;

import java.util.List;

public class BJCardResultBean {

    /**
     * flag : 0
     * goodsImgList : [{"FILE_NAME":"","FILE_PATH":"/upload/infofiles/20170311174809_6920.png"},{"FILE_NAME":"","FILE_PATH":"/upload/infofiles/20170311193320_872.png"}]
     * goodsInfo : {"abstruct":"werwre","goods_code":"123321","id":"6b044584da9d4d0bbaf6ab04b40f8fd0","name":"sdf"}
     * msg : 获取商品详情成功
     * peopleItem : [{"KEY_CODE":"ZPK3","KEY_NAME":"3人"},{"KEY_CODE":"ZPK5","KEY_NAME":"5人"}]
     * priceMap : {"GOODS_ID":"6b044584da9d4d0bbaf6ab04b40f8fd0","ID":"90052b5f82654fa3bbf400afb7095759","KEY_CODE":"ZPK5","KEY_NAME":"5人","MARKET_PRICE":2328,"OPTION_KEY_CODE":"ZPK1CI","OPTION_KEY_NAME":"1次","PRICE":26}
     * sales_num : 0
     * success : true
     * timeItem : [{"OPTION_KEY_CODE":"ZPK1CI","OPTION_KEY_NAME":"1次"},{"OPTION_KEY_CODE":"ZPK1JI","OPTION_KEY_NAME":"1季"}]
     */

    public int flag;
    public GoodsInfoBean goodsInfo;
    public String msg;
    public PriceMapBean priceMap;
    public int sales_num;
    public boolean success;
    public List<GoodsImgListBean> goodsImgList;
    public List<PeopleItemBean> peopleItem;
    public List<TimeItemBean> timeItem;


    public static class GoodsInfoBean {
        /**
         * abstruct : werwre
         * goods_code : 123321
         * id : 6b044584da9d4d0bbaf6ab04b40f8fd0
         * name : sdf
         */

        public String abstruct;
        public String goods_code;
        public String id;
        public String name;

    }

    public static class PriceMapBean {
        /**
         * GOODS_ID : 6b044584da9d4d0bbaf6ab04b40f8fd0
         * ID : 90052b5f82654fa3bbf400afb7095759
         * KEY_CODE : ZPK5
         * KEY_NAME : 5人
         * MARKET_PRICE : 2328
         * OPTION_KEY_CODE : ZPK1CI
         * OPTION_KEY_NAME : 1次
         * PRICE : 26
         */

        public String GOODS_ID;
        public String ID;
        public String KEY_CODE;
        public String KEY_NAME;
        public int MARKET_PRICE;
        public String OPTION_KEY_CODE;
        public String OPTION_KEY_NAME;
        public int PRICE;

    }

    public static class GoodsImgListBean {
        /**
         * FILE_NAME : 
         * FILE_PATH : /upload/infofiles/20170311174809_6920.png
         */

        public String FILE_NAME;
        public String FILE_PATH;

    }

    public static class PeopleItemBean {
        /**
         * KEY_CODE : ZPK3
         * KEY_NAME : 3人
         */

        public String KEY_CODE;
        public String KEY_NAME;

    }

    public static class TimeItemBean {
        /**
         * OPTION_KEY_CODE : ZPK1CI
         * OPTION_KEY_NAME : 1次
         */

        public String OPTION_KEY_CODE;
        public String OPTION_KEY_NAME;

    }
}
