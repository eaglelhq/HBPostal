package com.ksource.hbpostal.bean;

import java.util.List;

public class EvaluateResultBean {
	  public int flag;
	    public String msg;
	    public boolean success;
	    public List<GoodsRatedListBean> goodsRatedList;
	    

	    public static class GoodsRatedListBean {
	        public String APPRAISE_TIME;
	        public String MEMBER_NAME;
	        public String NICK_NAME;
	        public String HEAD_IMAGE;
	        public String APPRAISE_CONTENT;
	        public int APPRAISE_LEVEL;
	        public String REVIEW_NAME;
	        

	    }
}
