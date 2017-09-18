package com.ksource.hbpostal.bean;

import java.util.List;

public class SearchResultBean {
    
    /**
     * 
     * goodsList : [{"ID":"5a017180dab44740a7b0e35dbbe174aa","NAME":"vivo22","PRICE":1052,"INTEGRAL":1052,"BUY_TYPE":2,"IMAGE":"/upload/infofiles/20161023153841_6606.jpg","NUM":2,"LV1_ID":"50341bd827494153b5b933ea47ceec44","LV2_ID":"d5778007cfd146f6aa86329440ffd9db","LV3_ID":"1b78b7dd7a9f48118865db1797b5c4b0","BUY_NUM":null},{"ID":"632d111418e14cfc9b7d9a243e0be2f3","NAME":"枕头","PRICE":10,"INTEGRAL":0,"BUY_TYPE":1,"IMAGE":"/upload/infofiles/20161023153905_2435.jpg","NUM":2,"LV1_ID":"8d67a2d2e78448d094bd91c4bf588490","LV2_ID":"26b6a3628aa34dbe9d577aae3315a06e","LV3_ID":"c2d83ad2edcb47f8874304030681dc9e","BUY_NUM":null},{"ID":"1a8beaca5cfd48ccbc2c9fe88013477a","NAME":"iPhone5","PRICE":5000,"INTEGRAL":0,"BUY_TYPE":1,"IMAGE":"/upload/infofiles/20161023153830_6956.jpg","NUM":5,"LV1_ID":"50341bd827494153b5b933ea47ceec44","LV2_ID":"ec0ab5bc977f4cb78f780470a3ed7322","LV3_ID":"accd3ff474e144938c65cbf19bf76e48","BUY_NUM":8},{"ID":"a24ac9a627e343428392fb5b7aae6dc3","NAME":"耳机","PRICE":0,"INTEGRAL":5000,"BUY_TYPE":3,"IMAGE":"/upload/infofiles/20161019135515_3499.jpg","NUM":5,"LV1_ID":"8d67a2d2e78448d094bd91c4bf588490","LV2_ID":"ed7414af90834447987221c32bf1ef51","LV3_ID":"da9ba7b7695d4849b497f263b765fbc6","BUY_NUM":8},{"ID":"9adfa7f821484be685c69d2a67ef9a53","NAME":"保温杯","PRICE":100,"INTEGRAL":100,"BUY_TYPE":2,"IMAGE":"/upload/infofiles/20161023153852_1755.jpg","NUM":6,"LV1_ID":"50341bd827494153b5b933ea47ceec44","LV2_ID":"d5778007cfd146f6aa86329440ffd9db","LV3_ID":"1b78b7dd7a9f48118865db1797b5c4b0","BUY_NUM":null}]
     * flag : 0
     * msg : 请求成功
     * success : true
     */

    public String flag;
    public String msg;
    public boolean success;
    /**
     * ID : 5a017180dab44740a7b0e35dbbe174aa
     * NAME : vivo22
     * PRICE : 1052
     * INTEGRAL : 1052
     * BUY_TYPE : 2
     * IMAGE : /upload/infofiles/20161023153841_6606.jpg
     * NUM : 2
     * LV1_ID : 50341bd827494153b5b933ea47ceec44
     * LV2_ID : d5778007cfd146f6aa86329440ffd9db
     * LV3_ID : 1b78b7dd7a9f48118865db1797b5c4b0
     * BUY_NUM : null
     */

    public List<GoodsListBean> goodsList;

    public static class GoodsListBean {
        public String ID;
        public String NAME;
        public String PRICE;
        public String INTEGRAL;
        public int BUY_TYPE;
        public String IMAGE;
        public int NUM;
        public String LV1_ID;
        public String LV2_ID;
        public String LV3_ID;
        public int BUY_NUM;

    }

}
