package com.ksource.hbpostal.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/14 0014.
 */

public class MapDataBean {

    /**
     * datas : [{"JD":null,"WD":null,"NAME":"山城村3局","TYPE":1,"PROVINCE_NAME":null,"CITY_NAME":null,"AREA_NAME":null,"ADDRESS":null},{"JD":"114.30002","WD":"35.708589","NAME":"鹤壁市广场支行","TYPE":1,"PROVINCE_NAME":"淇滨区","CITY_NAME":"黎阳路办事处","AREA_NAME":"夏庄","ADDRESS":"中街1号"},{"JD":null,"WD":null,"NAME":"山城村支局","TYPE":1,"PROVINCE_NAME":null,"CITY_NAME":null,"AREA_NAME":null,"ADDRESS":null},{"JD":"114.30002","WD":"35.762966","NAME":"测试便民服务站","TYPE":2,"PROVINCE_NAME":"淇滨区","CITY_NAME":"钜桥镇","AREA_NAME":"白庄","ADDRESS":"白庄街道","SELLER_NAME":"张三","MOBILE":"15803816250"}]
     * flag : 0
     * msg : 获取成功
     * success : true
     */

    public String flag;
    public String msg;
    public boolean success;
    public List<DatasBean> datas;



    public static class DatasBean {
        /**
         * JD : null
         * WD : null
         * NAME : 山城村3局
         * TYPE : 1
         * PROVINCE_NAME : null
         * CITY_NAME : null
         * AREA_NAME : null
         * ADDRESS : null
         * SELLER_NAME : 张三
         * MOBILE : 15803816250
         */

        public double JD;
        public double WD;
        public String NAME;
        public String ID;
        public int TYPE;
        public String PROVINCE_NAME;
        public String CITY_NAME;
        public String CUN_NAME;
        public String AREA_NAME;
        public String ADDRESS;
        public String SELLER_NAME;
        public String MOBILE;


    }
}
