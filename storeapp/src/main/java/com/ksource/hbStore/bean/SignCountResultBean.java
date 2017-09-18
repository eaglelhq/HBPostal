package com.ksource.hbStore.bean;

import java.util.List;

public class SignCountResultBean {

	/**
     * flag : 0
     * memberSignCount : {"SUM_SCORE":10,"SUM_COUNT":1}
     * msg : 获取会员签到统计成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;
    public int isSign;
    public List<CalendarListBean> calendarList;
    
    /**
     * SUM_SCORE : 10
     * SUM_COUNT : 1
     */

    public MemberSignCountBean memberSignCount;



    public static class MemberSignCountBean {
        public String SUM_SCORE;
        public String SUM_COUNT;

    }
    public static class CalendarListBean {
    	public String DAY;
    	public String MONTH;
    	public String YEAR;
    	
    }


}
