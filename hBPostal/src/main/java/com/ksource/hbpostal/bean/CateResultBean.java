package com.ksource.hbpostal.bean;

import java.io.Serializable;
import java.util.List;

public class CateResultBean implements Serializable{
    /**
     * flag : 0
     * cateList : [{"ID":"26b6a3628aa34dbe9d577aae3315a06e","LV1_NAME":"水果","LEVEL":2,"IMAGE":null,"thirdCateList":[]},{"ID":"ed7414af90834447987221c32bf1ef51","LV1_NAME":"水果","LEVEL":2,"IMAGE":null,"thirdCateList":[{"ID":"1741188690854e8c98a77384bca28a2a","LV1_NAME":"水果","LEVEL":3,"IMAGE":null},{"ID":"da9ba7b7695d4849b497f263b765fbc6","LV1_NAME":"水果","LEVEL":3,"IMAGE":null}]}]
     * msg : 获取商品二三级分类列表成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;
    /**
     * ID : 26b6a3628aa34dbe9d577aae3315a06e
     * LV1_NAME : 水果
     * LEVEL : 2
     * IMAGE : null
     * thirdCateList : []
     */

    public List<CateListBean> cateList;


    public static class CateListBean implements Serializable{
        public String ID;
        public String LV1_NAME;
        public String LV2_NAME;
        public String LV3_NAME;
        public int LEVEL;
        public String IMAGE;
        public List<ThirdCateListBean> thirdCateList;

        public static class ThirdCateListBean implements Serializable {
            public String ID;
            public String LV2_NAME;
            public String LV3_NAME;
            public int LEVEL;
            public String IMAGE;
        }
    }
}
