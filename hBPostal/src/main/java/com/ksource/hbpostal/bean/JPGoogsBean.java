package com.ksource.hbpostal.bean;

import java.util.List;

public class JPGoogsBean {

    /**
     * goodsList : [{"ID":"632d111418e14cfc9b7d9a243e0be2f3","NAME":"枕头","PRICE":10,"INTEGRAL":0,"BUY_TYPE":1,"IMAGE":"/upload/infofiles/20161023153905_2435.jpg","BUY_NUM":null},{"ID":"5a017180dab44740a7b0e35dbbe174aa","NAME":"vivo22","PRICE":1052,"INTEGRAL":1052,"BUY_TYPE":2,"IMAGE":"/upload/infofiles/20161023153841_6606.jpg","BUY_NUM":null},{"ID":"a24ac9a627e343428392fb5b7aae6dc3","NAME":"耳机","PRICE":0,"INTEGRAL":5000,"BUY_TYPE":3,"IMAGE":"/upload/infofiles/20161019135515_3499.jpg","BUY_NUM":8},{"ID":"1a8beaca5cfd48ccbc2c9fe88013477a","NAME":"iPhone5","PRICE":5000,"INTEGRAL":0,"BUY_TYPE":1,"IMAGE":"/upload/infofiles/20161023153830_6956.jpg","BUY_NUM":2},{"ID":"9adfa7f821484be685c69d2a67ef9a53","NAME":"保温杯","PRICE":100,"INTEGRAL":100,"BUY_TYPE":2,"IMAGE":"/upload/infofiles/20161023153852_1755.jpg","BUY_NUM":null}]
     * flag : 0
     * msg : 获取商品列表成功  首页
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;
    /**
     * ID : 632d111418e14cfc9b7d9a243e0be2f3
     * NAME : 枕头
     * PRICE : 10
     * INTEGRAL : 0
     * BUY_TYPE : 1
     * IMAGE : /upload/infofiles/20161023153905_2435.jpg
     * BUY_NUM : null
     */

    public List<GoodsListBean> goodsList;


    public static class GoodsListBean {
        public String ID;
        public String GOODS_CODE;
        public String NAME;
        public String PRICE;
        public String MARKET_PRICE;
        public String INTEGRAL;
        public int BUY_TYPE;
        public String IMAGE;
        public int BUY_NUM;
        public int SALE_NUM;
        public int GOODS_TYPE;
    }

}
