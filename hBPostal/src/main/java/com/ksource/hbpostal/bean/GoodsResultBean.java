package com.ksource.hbpostal.bean;

import java.io.Serializable;
import java.util.List;

public class GoodsResultBean {
	

    /**
     * ID : 632d111418e14cfc9b7d9a243e0be2f3
     * NAME : 枕头
     * LV1_NAME : 水果
     * LV2_NAME : 苹果
     * LV3_NAME : 大苹果
     * PRICE : 10
     * INTEGRAL : 0
     * BUY_TYPE : 1
     * BRAND : 丹姿
     * SPECIFICATION : 10*10*10
     * MARKET_PRICE : 222
     * NUMBER : 12
     * goodsImgList : [{"ID":"c2bd2bddc7224944a023538a8436b819","FILE_NAME":"","FILE_PATH":"/upload/infofiles/20161014135641_5720.jpg","FILE_SIZE":"","FILE_FORMAT":"undefined","BUSINESS_ID":"632d111418e14cfc9b7d9a243e0be2f3","ORDERNUM":0,"FILE_NOTE":null,"FILE_TYPE":"","EXTEND1":null,"EXTEND2":null,"EXTEND3":null,"CREATE_TIME":20161023153908,"DEL_FLAG":1,"USER_ID":null,"USER_NAME":null,"FOLDER_ID":null,"AUDIT_STATE":null,"CURRENT_ID":null,"DOWN_NUM":0}]
     */

    public GoodsInfoBean goodsInfo;
    /**
     * goodsInfo : {"ID":"632d111418e14cfc9b7d9a243e0be2f3","NAME":"枕头","LV1_NAME":"水果","LV2_NAME":"苹果","LV3_NAME":"大苹果","PRICE":10,"INTEGRAL":0,"BUY_TYPE":1,"BRAND":"丹姿","SPECIFICATION":"10*10*10","MARKET_PRICE":222,"NUMBER":12,"goodsImgList":[{"ID":"c2bd2bddc7224944a023538a8436b819","FILE_NAME":"","FILE_PATH":"/upload/infofiles/20161014135641_5720.jpg","FILE_SIZE":"","FILE_FORMAT":"undefined","BUSINESS_ID":"632d111418e14cfc9b7d9a243e0be2f3","ORDERNUM":0,"FILE_NOTE":null,"FILE_TYPE":"","EXTEND1":null,"EXTEND2":null,"EXTEND3":null,"CREATE_TIME":20161023153908,"DEL_FLAG":1,"USER_ID":null,"USER_NAME":null,"FOLDER_ID":null,"AUDIT_STATE":null,"CURRENT_ID":null,"DOWN_NUM":0}]}
     * flag : 0
     * msg : 获取商品详情成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;

    

    public static class GoodsInfoBean implements Serializable{
        public String ID;
        public String NAME;
        public String LV1_NAME;
        public String LV2_NAME;
        public String LV3_NAME;
        public String PRICE;
        public String INTEGRAL;
        public int BUY_TYPE;
        public int BUY_NUM;
        public int NCP_FL;
        public String BRAND;
        public String SPECIFICATION;
        public String MARKET_PRICE;
        public int NUMBER;
        public int IS_NCP;
        /**
         * ID : c2bd2bddc7224944a023538a8436b819
         * FILE_NAME : 
         * FILE_PATH : /upload/infofiles/20161014135641_5720.jpg
         * FILE_SIZE : 
         * FILE_FORMAT : undefined
         * BUSINESS_ID : 632d111418e14cfc9b7d9a243e0be2f3
         * ORDERNUM : 0
         * FILE_NOTE : null
         * FILE_TYPE : 
         * EXTEND1 : null
         * EXTEND2 : null
         * EXTEND3 : null
         * CREATE_TIME : 20161023153908
         * DEL_FLAG : 1
         * USER_ID : null
         * USER_NAME : null
         * FOLDER_ID : null
         * AUDIT_STATE : null
         * CURRENT_ID : null
         * DOWN_NUM : 0
         */

        public List<GoodsImgListBean> goodsImgList;

       

        public static class GoodsImgListBean implements Serializable{
            public String ID;
            public String FILE_NAME;
            public String FILE_PATH;
            public String FILE_SIZE;
            public String FILE_FORMAT;
            public String BUSINESS_ID;
            public int ORDERNUM;
            public Object FILE_NOTE;
            public String FILE_TYPE;
            public Object EXTEND1;
            public Object EXTEND2;
            public Object EXTEND3;
            public long CREATE_TIME;
            public int DEL_FLAG;
            public Object USER_ID;
            public Object USER_NAME;
            public Object FOLDER_ID;
            public Object AUDIT_STATE;
            public Object CURRENT_ID;
            public int DOWN_NUM;

            
        }
    }


}
