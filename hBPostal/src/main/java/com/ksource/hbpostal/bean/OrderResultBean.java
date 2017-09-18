package com.ksource.hbpostal.bean;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;

public class OrderResultBean implements Serializable{

    /**
     * total : 4
     * flag : 0
     * pageInfo : {"currPage":"1","datas":[{"ID":"1706b09f95214ea89e77f3457f560fc3","ORDER_ID":"DD-20161024-241665","MEMBER_ID":"272dab18a37e4b6bb4bc9d48253c7c2c","STATE":0,"CREATE_TIME":20161024095414,"PAY_STATE":0,"PAY_TYPE":null,"BUY_WAY":0,"TOTAL_PRICE":30000,"TOTAL_JF":0,"SEND_TYPE":1,"ORDER_TYPE":1,"TOTAL_NUM":6,"MEMBER_NAME":"会员名5","goodsList":[{"ID":"779cbaf895794481ae980039adce2eb9","GOODS_ID":"1a8beaca5cfd48ccbc2c9fe88013477a","GOODS_NAME":"iPhone5","ONE_USE_SCORE":0,"ONE_USE_MONEY":5000,"BUY_NUM":6,"TOTAL_MONEY":30000,"TOTAL_SCORE":0,"GOODS_NOTE":null,"ORDER_ID":"1706b09f95214ea89e77f3457f560fc3","GOODS_TYPE":null,"REVIEW_STATE":0,"GOODS_CODE":"22222221","IMAGE":"/upload/infofiles/20161023153830_6956.jpg"}]},{"ID":"e5f4c0be5a874a4bbc3218405ef5d053","ORDER_ID":"DD-20161017-601112","MEMBER_ID":"272dab18a37e4b6bb4bc9d48253c7c2c","STATE":3,"CREATE_TIME":20161017140716,"PAY_STATE":1,"PAY_TYPE":3,"BUY_WAY":2,"TOTAL_PRICE":0,"TOTAL_JF":5000,"SEND_TYPE":2,"ORDER_TYPE":null,"TOTAL_NUM":1,"MEMBER_NAME":"会员名5","goodsList":[{"ID":"b4bfe490332343bdb067e75b8ca7e55b","GOODS_ID":"a24ac9a627e343428392fb5b7aae6dc3","GOODS_NAME":"耳机","ONE_USE_SCORE":5000,"ONE_USE_MONEY":0,"BUY_NUM":1,"TOTAL_MONEY":0,"TOTAL_SCORE":5000,"GOODS_NOTE":null,"ORDER_ID":"e5f4c0be5a874a4bbc3218405ef5d053","GOODS_TYPE":null,"REVIEW_STATE":0,"GOODS_CODE":"12134523","IMAGE":"/upload/infofiles/20161019135515_3499.jpg"}]},{"ID":"456123","ORDER_ID":"789456123","MEMBER_ID":"272dab18a37e4b6bb4bc9d48253c7c2c","STATE":0,"CREATE_TIME":20161013165911,"PAY_STATE":0,"PAY_TYPE":1,"BUY_WAY":1,"TOTAL_PRICE":15000,"TOTAL_JF":0,"SEND_TYPE":1,"ORDER_TYPE":1,"TOTAL_NUM":7,"MEMBER_NAME":"会员名5","goodsList":[{"ID":"46465","GOODS_ID":"a24ac9a627e343428392fb5b7aae6dc3","GOODS_NAME":"耳机","ONE_USE_SCORE":1000,"ONE_USE_MONEY":1000,"BUY_NUM":5,"TOTAL_MONEY":5000,"TOTAL_SCORE":5000,"GOODS_NOTE":"无","ORDER_ID":"456123","GOODS_TYPE":1,"REVIEW_STATE":null,"GOODS_CODE":"12134523","IMAGE":"/upload/infofiles/20161019135515_3499.jpg"},{"ID":"45454","GOODS_ID":"1a8beaca5cfd48ccbc2c9fe88013477a","GOODS_NAME":"iPhone5","ONE_USE_SCORE":5000,"ONE_USE_MONEY":5000,"BUY_NUM":2,"TOTAL_MONEY":10000,"TOTAL_SCORE":10000,"GOODS_NOTE":"fffffffff","ORDER_ID":"456123","GOODS_TYPE":1,"REVIEW_STATE":null,"GOODS_CODE":"22222221","IMAGE":"/upload/infofiles/20161023153830_6956.jpg"}]},{"ID":"456852","ORDER_ID":"654654155","MEMBER_ID":"272dab18a37e4b6bb4bc9d48253c7c2c","STATE":3,"CREATE_TIME":20161013165911,"PAY_STATE":1,"PAY_TYPE":2,"BUY_WAY":1,"TOTAL_PRICE":4000,"TOTAL_JF":2000,"SEND_TYPE":2,"ORDER_TYPE":2,"TOTAL_NUM":2,"MEMBER_NAME":"会员名5","goodsList":[{"ID":"12121","GOODS_ID":"a24ac9a627e343428392fb5b7aae6dc3","GOODS_NAME":"耳机","ONE_USE_SCORE":1000,"ONE_USE_MONEY":1000,"BUY_NUM":2,"TOTAL_MONEY":2000,"TOTAL_SCORE":2000,"GOODS_NOTE":"aaaaaa","ORDER_ID":"456852","GOODS_TYPE":2,"REVIEW_STATE":null,"GOODS_CODE":"12134523","IMAGE":"/upload/infofiles/20161019135515_3499.jpg"}]}],"pageSize":"20","rowNum":0}
     * msg : 获取订单列表成功
     * success : true
     */

    public int total;
    public int flag;
    /**
     * currPage : 1
     * datas : [{"ID":"1706b09f95214ea89e77f3457f560fc3","ORDER_ID":"DD-20161024-241665","MEMBER_ID":"272dab18a37e4b6bb4bc9d48253c7c2c","STATE":0,"CREATE_TIME":20161024095414,"PAY_STATE":0,"PAY_TYPE":null,"BUY_WAY":0,"TOTAL_PRICE":30000,"TOTAL_JF":0,"SEND_TYPE":1,"ORDER_TYPE":1,"TOTAL_NUM":6,"MEMBER_NAME":"会员名5","goodsList":[{"ID":"779cbaf895794481ae980039adce2eb9","GOODS_ID":"1a8beaca5cfd48ccbc2c9fe88013477a","GOODS_NAME":"iPhone5","ONE_USE_SCORE":0,"ONE_USE_MONEY":5000,"BUY_NUM":6,"TOTAL_MONEY":30000,"TOTAL_SCORE":0,"GOODS_NOTE":null,"ORDER_ID":"1706b09f95214ea89e77f3457f560fc3","GOODS_TYPE":null,"REVIEW_STATE":0,"GOODS_CODE":"22222221","IMAGE":"/upload/infofiles/20161023153830_6956.jpg"}]},{"ID":"e5f4c0be5a874a4bbc3218405ef5d053","ORDER_ID":"DD-20161017-601112","MEMBER_ID":"272dab18a37e4b6bb4bc9d48253c7c2c","STATE":3,"CREATE_TIME":20161017140716,"PAY_STATE":1,"PAY_TYPE":3,"BUY_WAY":2,"TOTAL_PRICE":0,"TOTAL_JF":5000,"SEND_TYPE":2,"ORDER_TYPE":null,"TOTAL_NUM":1,"MEMBER_NAME":"会员名5","goodsList":[{"ID":"b4bfe490332343bdb067e75b8ca7e55b","GOODS_ID":"a24ac9a627e343428392fb5b7aae6dc3","GOODS_NAME":"耳机","ONE_USE_SCORE":5000,"ONE_USE_MONEY":0,"BUY_NUM":1,"TOTAL_MONEY":0,"TOTAL_SCORE":5000,"GOODS_NOTE":null,"ORDER_ID":"e5f4c0be5a874a4bbc3218405ef5d053","GOODS_TYPE":null,"REVIEW_STATE":0,"GOODS_CODE":"12134523","IMAGE":"/upload/infofiles/20161019135515_3499.jpg"}]},{"ID":"456123","ORDER_ID":"789456123","MEMBER_ID":"272dab18a37e4b6bb4bc9d48253c7c2c","STATE":0,"CREATE_TIME":20161013165911,"PAY_STATE":0,"PAY_TYPE":1,"BUY_WAY":1,"TOTAL_PRICE":15000,"TOTAL_JF":0,"SEND_TYPE":1,"ORDER_TYPE":1,"TOTAL_NUM":7,"MEMBER_NAME":"会员名5","goodsList":[{"ID":"46465","GOODS_ID":"a24ac9a627e343428392fb5b7aae6dc3","GOODS_NAME":"耳机","ONE_USE_SCORE":1000,"ONE_USE_MONEY":1000,"BUY_NUM":5,"TOTAL_MONEY":5000,"TOTAL_SCORE":5000,"GOODS_NOTE":"无","ORDER_ID":"456123","GOODS_TYPE":1,"REVIEW_STATE":null,"GOODS_CODE":"12134523","IMAGE":"/upload/infofiles/20161019135515_3499.jpg"},{"ID":"45454","GOODS_ID":"1a8beaca5cfd48ccbc2c9fe88013477a","GOODS_NAME":"iPhone5","ONE_USE_SCORE":5000,"ONE_USE_MONEY":5000,"BUY_NUM":2,"TOTAL_MONEY":10000,"TOTAL_SCORE":10000,"GOODS_NOTE":"fffffffff","ORDER_ID":"456123","GOODS_TYPE":1,"REVIEW_STATE":null,"GOODS_CODE":"22222221","IMAGE":"/upload/infofiles/20161023153830_6956.jpg"}]},{"ID":"456852","ORDER_ID":"654654155","MEMBER_ID":"272dab18a37e4b6bb4bc9d48253c7c2c","STATE":3,"CREATE_TIME":20161013165911,"PAY_STATE":1,"PAY_TYPE":2,"BUY_WAY":1,"TOTAL_PRICE":4000,"TOTAL_JF":2000,"SEND_TYPE":2,"ORDER_TYPE":2,"TOTAL_NUM":2,"MEMBER_NAME":"会员名5","goodsList":[{"ID":"12121","GOODS_ID":"a24ac9a627e343428392fb5b7aae6dc3","GOODS_NAME":"耳机","ONE_USE_SCORE":1000,"ONE_USE_MONEY":1000,"BUY_NUM":2,"TOTAL_MONEY":2000,"TOTAL_SCORE":2000,"GOODS_NOTE":"aaaaaa","ORDER_ID":"456852","GOODS_TYPE":2,"REVIEW_STATE":null,"GOODS_CODE":"12134523","IMAGE":"/upload/infofiles/20161019135515_3499.jpg"}]}]
     * pageSize : 20
     * rowNum : 0
     */

    public PageInfoBean pageInfo;
    public String msg;
    public boolean success;

    

    public static class PageInfoBean implements Serializable{
        public String currPage;
        public String pageSize;
        public int rowNum;
        /**
         * ID : 1706b09f95214ea89e77f3457f560fc3
         * ORDER_ID : DD-20161024-241665
         * MEMBER_ID : 272dab18a37e4b6bb4bc9d48253c7c2c
         * STATE : 0
         * CREATE_TIME : 20161024095414
         * PAY_STATE : 0
         * PAY_TYPE : null
         * BUY_WAY : 0
         * TOTAL_PRICE : 30000
         * TOTAL_JF : 0
         * SEND_TYPE : 1
         * ORDER_TYPE : 1
         * TOTAL_NUM : 6
         * MEMBER_NAME : 会员名5
         * goodsList : [{"ID":"779cbaf895794481ae980039adce2eb9","GOODS_ID":"1a8beaca5cfd48ccbc2c9fe88013477a","GOODS_NAME":"iPhone5","ONE_USE_SCORE":0,"ONE_USE_MONEY":5000,"BUY_NUM":6,"TOTAL_MONEY":30000,"TOTAL_SCORE":0,"GOODS_NOTE":null,"ORDER_ID":"1706b09f95214ea89e77f3457f560fc3","GOODS_TYPE":null,"REVIEW_STATE":0,"GOODS_CODE":"22222221","IMAGE":"/upload/infofiles/20161023153830_6956.jpg"}]
         */

        public List<DatasBean> datas;

        

        public static class DatasBean implements Serializable{
            public String ID;
            public String ORDER_ID;
            public String MEMBER_ID;
            public String EXPRESS_CODE;
            public int STATE;
            public long CREATE_TIME;
            public int PAY_STATE;
            public int PAY_TYPE;
            public int BUY_WAY;
            public String TOTAL_PRICE;
            public String TOTAL_JF;
            public int SEND_TYPE;
            public int ORDER_TYPE;
            public int TOTAL_NUM;
            public String SEND_PRICE;
            public String MEMBER_NAME;
            /**
             * ID : 779cbaf895794481ae980039adce2eb9
             * GOODS_ID : 1a8beaca5cfd48ccbc2c9fe88013477a
             * GOODS_NAME : iPhone5
             * ONE_USE_SCORE : 0
             * ONE_USE_MONEY : 5000
             * BUY_NUM : 6
             * TOTAL_MONEY : 30000
             * TOTAL_SCORE : 0
             * GOODS_NOTE : null
             * ORDER_ID : 1706b09f95214ea89e77f3457f560fc3
             * GOODS_TYPE : null
             * REVIEW_STATE : 0
             * GOODS_CODE : 22222221
             * IMAGE : /upload/infofiles/20161023153830_6956.jpg
             */

            public List<GoodsListBean> goodsList;

           
            public static class GoodsListBean implements Serializable {
                public String ID;
                public String GOODS_ID;
                public String GOODS_NAME;
                public String LV1_NAME;
                public String LV2_NAME;
                public int BUY_TYPE;
                public String LV3_NAME;
                public String ONE_USE_SCORE;
                public String ONE_USE_MONEY;
                public int BUY_NUM;
                public String TOTAL_MONEY;
                public String TOTAL_SCORE;
                public String GOODS_NOTE;
                public String ORDER_ID;
                public String GOODS_TYPE;
                public int REVIEW_STATE;
                public int STATE;
                public String GOODS_CODE;
                public String IMAGE;

                
            }
        }
    }

}
