package com.ksource.hbpostal.bean;

import java.util.List;

public class ManagerReusltBean {
    /**
     * flag : 0
     * customManList : [{"ZSXM":"电子商务三级","MANAGE_LV":3,"MOBILE":"15515786195","SEX":"男","MANEGE_PID":"5e44706246e54390a40633408ae2170a"},{"ZSXM":"电子商务二级","MANAGE_LV":2,"MOBILE":"15515786195","SEX":"女","MANEGE_PID":"e56c9d4dbdf24ed9a58be48f390ad233"},{"ZSXM":"电子商务一级","MANAGE_LV":1,"MOBILE":"15515786195","SEX":null,"MANEGE_PID":""}]
     * msg : 获取客户经理成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;
    /**
     * ZSXM : 电子商务三级
     * MANAGE_LV : 3
     * MOBILE : 15515786195
     * SEX : 男
     * MANEGE_PID : 5e44706246e54390a40633408ae2170a
     */

    public List<CustomManListBean> customManList;


    public static class CustomManListBean {
        public String QQ ;
		public String ZSXM;
        public int MANAGE_LV;
        public String MOBILE;
        public String SEX;
        public String MANEGE_PID;

    }
}
