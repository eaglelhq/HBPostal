package com.ksource.hbpostal.bean;


public class ScoreResultBean {


    public int flag;
    public String msg;
    public boolean success;
    public MemberScoreBean memberScore;
	
    public static class MemberScoreBean {
        public String memberScore;
        public String xfScore;
        public String jrScore;
        public String yjbScore;

    }
}
