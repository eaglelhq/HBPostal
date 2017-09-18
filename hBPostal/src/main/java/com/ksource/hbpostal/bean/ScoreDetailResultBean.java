package com.ksource.hbpostal.bean;

public class ScoreDetailResultBean {
    /**
     * flag : 0
     * memberScore : 36
     * msg : 获取成功
     * success : true
     * scoreList : [{"CREATE_TIME":20161111183659,"BEGIN_TIME":"201611","IS_ADD":1,"OVER_TIME":20171101183659,"SCORE":12,"SCORE_SOURCE":"邀请好友"},{"CREATE_TIME":20161111181207,"BEGIN_TIME":"201611","IS_ADD":1,"OVER_TIME":20171101181207,"SCORE":12,"SCORE_SOURCE":"邀请好友"},{"CREATE_TIME":20161111173602,"BEGIN_TIME":"201611","IS_ADD":1,"OVER_TIME":20171101173602,"SCORE":12,"SCORE_SOURCE":"邀请好友"}]
     */

    public int flag;
    public String memberScore;
    public String msg;
    public boolean success;
    /**
     * CREATE_TIME : 20161111183659
     * BEGIN_TIME : 201611
     * IS_ADD : 1
     * OVER_TIME : 20171101183659
     * SCORE : 12
     * SCORE_SOURCE : 邀请好友
     */

    public java.util.List<ScoreListBean> scoreList;

    

    public static class ScoreListBean {
        public int TYPE ;
		public String CREATE_TIME;
        public String BEGIN_TIME;
        public int IS_ADD;
        public String OVER_TIME;
        public String SCORE;
        public String SCORE_SOURCE;

        
    }
}
