package com.ksource.hbpostal.bean;

import java.io.Serializable;
import java.util.List;

public class CartResultBean implements Serializable{
    /**
     * flag : 0
     * goods : [{"CART_ID":"123456","NUMBER":12,"GOODS_ID":"632d111418e14cfc9b7d9a243e0be2f3","ABSTRUCT":"没什么特点","PRICE":10,"INTEGRAL":0,"IMAGE":"/upload/infofiles/20161023153905_2435.jpg"},{"CART_ID":"456789","NUMBER":5,"GOODS_ID":"9adfa7f821484be685c69d2a67ef9a53","ABSTRUCT":"黑色 金字 靓丽","PRICE":100,"INTEGRAL":100,"IMAGE":"/upload/infofiles/20161023153852_1755.jpg"}]
     * msg : 获取成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;
    /**
     * CART_ID : 123456
     * NUMBER : 12
     * GOODS_ID : 632d111418e14cfc9b7d9a243e0be2f3
     * ABSTRUCT : 没什么特点
     * PRICE : 10
     * INTEGRAL : 0
     * IMAGE : /upload/infofiles/20161023153905_2435.jpg
     */

    public List<GoodsBean> goods;


    public static class GoodsBean implements Serializable{
        public String CART_ID;
        public int NUMBER;
        public int KC;
        public String GOODS_ID;
        public String LV1_NAME;
        public String LV2_NAME;
        public String LV3_NAME;
        public String ABSTRUCT;
        public String NAME;
        public String PRICE;
        public String INTEGRAL;
        public int BUY_TYPE;
        public int IS_NCP;
        public String IMAGE;
		public boolean isChioce;

    }
}
