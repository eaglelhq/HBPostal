package com.ksource.hbpostal.bean;

import java.util.List;

public class InviteReusltBean {
	public int flag;
	public String msg;
	public boolean success;

	public List<InvitedHistoryListBean> invitedHistoryList;

	public static class InvitedHistoryListBean {
		public String INVI_MEMBER_NAME;
		public int SCORE;
		public String HEAD_IMAGE;
		public String CREATE_TIME;

	}
}
