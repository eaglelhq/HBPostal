package com.ksource.hbpostal.bean;

import java.util.List;

public class AwardResultBean {

	/**
	 * flag : 0
	 * msg : 获取成功
	 * success : true
	 * info : [{"ID":"6891b8e853dc4429bc9e021faa52e247"},{"ID":"f9984a05f7484a42a7f8acc2ff4483f9"}]
	 */

	public int flag;
	public String msg;
	public boolean success;
	public List<InfoBean> info;



	public static class InfoBean {
		/**
		 * ID : 6891b8e853dc4429bc9e021faa52e247
		 */

		public String ID;
		public String COUPON_NAME;
		public String SHOP_NAME;
		public int COUPON_CATE;
		public String TAKE_EFFECT_PRICE;
		public String DENOMINATION;
		public int VALID_DAYS;
    }
}
