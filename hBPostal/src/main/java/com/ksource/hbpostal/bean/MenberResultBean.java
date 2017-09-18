package com.ksource.hbpostal.bean;

public class MenberResultBean {

    /**
     * flag : 0
     * memberScore : {"memberScore":63,"xfScore":23,"jrScore":40,"yjbScore":20}
     * memberInfo : {"NAME":"会员名5","HEAD_IMAGE":null,"MEMBER_GRADE":3,"SUM_SCORE":0,"YJB_SCORE":0}
     * msg : 获取会员信息成功
     * success : true
     * orderCount : {"3":2,"2":0,"1":0,"0":2}
     */

    public int flag;
    public String msg;
    public boolean success;
    /**
     * memberScore : 63
     * xfScore : 23
     * jrScore : 40
     * yjbScore : 20
     */

    public MemberScoreBean memberScore;
    /**
     * NAME : 会员名5
     * HEAD_IMAGE : null
     * MEMBER_GRADE : 3
     * SUM_SCORE : 0
     * YJB_SCORE : 0
     */

    public MemberInfoBean memberInfo;
    /**
     * 3 : 2
     * 2 : 0
     * 1 : 0
     * 0 : 2
     */

    public OrderCountBean orderCount;


    public static class MemberScoreBean {
        public double memberScore;
        public double xfScore;
        public double jrScore;
        public int yjbScore;

    }

    public static class MemberInfoBean {
        public String NAME;
        public String ID;
        public String NICK_NAME;
        public String HEAD_IMAGE;
        public int MEMBER_GRADE;
        public String SUM_SCORE;
        public String YJB_SCORE;
        public int PAY_PWD;
    }

    public static class OrderCountBean {
        public int L3;
        public int L2;
        public int L1;
        public int L0;

    }

}
