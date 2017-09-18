package com.ksource.hbpostal.bean;

import java.io.Serializable;
import java.util.List;

public class DYYHQResultBean {

	/**
	 * couponList : [{"COUPON_ID":"6798885a7ef04446bd5622e1eff76ee0","COUPON_NAME":"优惠券测试数据","COUPON_CATE":1,"SHOP_NAME":null,"USE_GOLD_COINS":2,"VALID_DAYS":3,"TAKE_EFFECT_PRICE":10,"DENOMINATION":1},{"COUPON_ID":"b25f70f62f7e4bfc9e2797b116b19a08","COUPON_NAME":"adcdefg","COUPON_CATE":2,"SHOP_NAME":"银联测试商家","USE_GOLD_COINS":2,"VALID_DAYS":2,"TAKE_EFFECT_PRICE":2,"DENOMINATION":2},{"COUPON_ID":"7291cb4cafe4481592c74c04e04a5f98","COUPON_NAME":"aaa","COUPON_CATE":1,"SHOP_NAME":null,"USE_GOLD_COINS":2,"VALID_DAYS":2,"TAKE_EFFECT_PRICE":2,"DENOMINATION":2},{"COUPON_ID":"25cc4a561bcd4883b3417683d0d6bd13","COUPON_NAME":"234","COUPON_CATE":1,"SHOP_NAME":"导出","USE_GOLD_COINS":2,"VALID_DAYS":33,"TAKE_EFFECT_PRICE":33,"DENOMINATION":33},{"COUPON_ID":"5354ca3646a64d5c93c8ba0cd6b43088","COUPON_NAME":"222","COUPON_CATE":1,"SHOP_NAME":null,"USE_GOLD_COINS":22,"VALID_DAYS":122,"TAKE_EFFECT_PRICE":2,"DENOMINATION":22},{"COUPON_ID":"839b14dfcc584b5892a50a61ec8502be","COUPON_NAME":"1123","COUPON_CATE":2,"SHOP_NAME":"浚县卫贤袁香菜村邮站","USE_GOLD_COINS":32,"VALID_DAYS":22,"TAKE_EFFECT_PRICE":22,"DENOMINATION":22},{"COUPON_ID":"5ae55e9abcee4281a85e35a4568d1dbc","COUPON_NAME":"112","COUPON_CATE":1,"SHOP_NAME":"新大陆","USE_GOLD_COINS":2,"VALID_DAYS":2,"TAKE_EFFECT_PRICE":3,"DENOMINATION":2},{"COUPON_ID":"394d8cf8a87b452b8dc8671921f875df","COUPON_NAME":"11","COUPON_CATE":2,"SHOP_NAME":null,"USE_GOLD_COINS":11,"VALID_DAYS":11,"TAKE_EFFECT_PRICE":11,"DENOMINATION":1},{"COUPON_ID":"7e5dce861e1747159f6e2f545210afac","COUPON_NAME":"2111","COUPON_CATE":2,"SHOP_NAME":null,"USE_GOLD_COINS":11,"VALID_DAYS":11,"TAKE_EFFECT_PRICE":11,"DENOMINATION":11},{"COUPON_ID":"eda09403e29b44a68d36e4a63446435d","COUPON_NAME":"33","COUPON_CATE":2,"SHOP_NAME":"新大陆","USE_GOLD_COINS":11,"VALID_DAYS":11,"TAKE_EFFECT_PRICE":11,"DENOMINATION":11}]
	 * flag : 0
	 * couponPackageList : [{"PACKAGE_ID":"58c58c9a2c5a4a79954beaf99f42e704","PACKAGE_NAME":"11","COUPON_NAME":"aaa","USE_GOLD_COINS":22}]
	 * msg : 获取优惠券列表成功
	 * success : true
	 */

	public int flag;
	public int COINS;
	public String msg;
	public boolean success;
	public List<CouponListBean> couponList;
	public List<CouponPackageListBean> couponPackageList;



	public static class CouponListBean implements Serializable {
		/**
		 * COUPON_ID : 6798885a7ef04446bd5622e1eff76ee0
		 * COUPON_NAME : 优惠券测试数据
		 * COUPON_CATE : 1
		 * SHOP_NAME : null
		 * USE_GOLD_COINS : 2
		 * VALID_DAYS : 3
		 * TAKE_EFFECT_PRICE : 10
		 * DENOMINATION : 1
		 */

		public String COUPON_ID;
		public String COUPON_NAME;
		public int COUPON_CATE;
		public int COUNT;
		public String SHOP_NAME;
		public int USE_GOLD_COINS;
		public int VALID_DAYS;
		public String TAKE_EFFECT_PRICE;
		public String DENOMINATION;


	}

	public static class CouponPackageListBean {
		/**
		 * PACKAGE_ID : 58c58c9a2c5a4a79954beaf99f42e704
		 * PACKAGE_NAME : 11
		 * COUPON_NAME : aaa
		 * USE_GOLD_COINS : 22
		 */

		public String PACKAGE_ID;
		public String PACKAGE_NAME;
		public String COUPON_NAME;
		public int USE_GOLD_COINS;


	}
}
