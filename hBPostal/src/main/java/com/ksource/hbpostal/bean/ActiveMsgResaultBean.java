package com.ksource.hbpostal.bean;

import java.util.List;

public class ActiveMsgResaultBean {

    /**
     * addressList : [{"ID":"12312431434324","NAME":"万人往","MOBILE":"13888888888","PROVINCE_ID":"accca8e3696048c2bd0c5bf651cf73cf","PROVINCE_NAME":"河南省","CITY_ID":"81b2e247331c4735b996a852fca38588","CITY_NAME":"盘锦市","AREA_ID":"0e159e921cad4ae798aaedd7d221ff19","AREA_NAME":"安龙县","ADDRESS":"啊实打实的","IS_DEFAULT_ADDRESS":1,"ADD_TIME":null,"MEMBER_ID":"272dab18a37e4b6bb4bc9d48253c7c2c"}]
     * flag : 0
     * msg : 获取成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;

    public List<HdxxListBean> hdxxList;

   

    public static class HdxxListBean {
        public String ID;
        public String TITLE;
        public int NUM;
        public String IMAGE;
        public String CREATE_NAME;
        public String UPDATE_TIME;

       
    }
    public List<TzggListBean> tzggList;
    
    
    
    public static class TzggListBean {
    	public String ID;
    	public String TITLE;
    	public int NUM;
    	public String IMAGE;
    	public String CREATE_NAME;
    	public String UPDATE_TIME;
    	
    	
    }
    public List<CjwtListBean> cjwtList;
    
    
    
    public static class CjwtListBean {
    	public String ID;
    	public String TITLE;
    	public String LV2_NAME;
    	public String LV1_NAME;
    	public String CREATE_TIME;
    	
    	
    }

}
