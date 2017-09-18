package com.ksource.hbpostal.bean;

import java.util.List;

public class PushMsgResaultBean {


    public int flag;
    public String msg;
    public boolean success;

    public List<MessageListBean> messageList;
    public List<HelpInfoListBean> helpInfoList;

   

    public static class MessageListBean {
        public String MESSAGE_ID;
        public String MESSAGE_TITLE;
        public int IS_READ;
        public String DEAL_TIME;
        
        public String IMAGE;
        public String CREATE_NAME;
       
    }
    
    public static class HelpInfoListBean {
    	public String ID;
    	public String TITLE;
    	
    }
    

}
