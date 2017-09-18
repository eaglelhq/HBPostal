package com.ksource.hbpostal.bean;


public class SimpleManagerReusltBean {
    /**
     * flag : 0
     * msg : 获取客户经理成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;

    public CusManInfo cusManInfo;

    public static class CusManInfo {
        public String ID ;
		public String ZSXM;
    }
}
