package com.ksource.hbpostal.config;

import android.os.Environment;

/**
 * 常量类
 *
 * @author Administrator
 */

public class ConstantValues {
    // 测试 77
//	 public static final String BASE_URL = "http://10.10.10.10:8187";
    // 研发
//    public static final String BASE_URL = "http://10.10.10.10:65532"; //裴
//    public static final String BASE_URL = "http://10.10.10.10:60011"; //梁创
    //	 public static final String BASE_URL = "http://10.10.10.10:65530";
    // hehe
//    public static final String BASE_URL = "http://172.26.165.1:8080";  //裴
//    public static final String BASE_URL = "http://172.22.169.1:8080";  //裴
    //	 public static final String BASE_URL = "http://172.16.129.1:8080";
//    public static final String BASE_URL = "http://172.18.193.1:8080/larks_hbyz";
    public static final String BASE_URL = "http://123.15.56.103:8888";//正式
//    	 public static final String BASE_URL = "http://121.42.156.76:9060";//测试
//    外网
//    public static final String BASE_URL = "http://yzf.yunque365.com";
//    public static final String BASE_URL = "http://139.129.118.140:8900";
    public static final String TTS_APP_ID = "28557349290FF743C07ACA2F55FA9E69";

    // 应用下载URL
    public static final String APP_DOWNLOAD_URL = BASE_URL
            + "/business/appdown.html";
    // H5-用户协议URL
    public static final String USER_XIEYI_HTML_URL = BASE_URL
            + "/mobile/front/memberAgreement.html";

    // H5-BannerURL
    public static final String BANAER_HTML_URL = BASE_URL
            + "/mobile/front/bannerInfoH5.html";
    //	public static final String BANAER_HTML_URL = "http://123.56.245.211:9090/scwyw/scWywIndex.html";
    // H5-淘宝客URL
    public static final String TBK_HTML_URL = "http://shihuidashu.cc/";
    // H5-都邦保险URL
    public static final String DBBX_HTML_URL = "http://test.mobisoft.com.cn/h5/products/products.html";
    // H5-自助缴费协议URL
    public static final String JF_XIEYI_HTML_URL = BASE_URL
            + "/mobile/front/payFeesHelp.html";
    // H5-商品详情URL
    public static final String GoodsDetail_HTML_URL = BASE_URL
            + "/mobile/front/goodsDetail.html?goodsId=";
    // H5-活动信息URL
    public static final String HDXX_HTML_URL = BASE_URL
            + "/mobile/front/hdxxInfo.html?id=";
    // H5-通知公告URL
    public static final String TZGG_HTML_URL = BASE_URL
            + "/mobile/front/tzggInfo.html?id=";
    // H5-常见问题URL
    public static final String CJWT_HTML_URL = BASE_URL
            + "/mobile/front/cjwtInfo.html?id=";
    // H5-使用帮助URL
    public static final String USE_HELP_HTML_URL = BASE_URL + "";
    // H5-客户咨询URL
    public static final String KHZX_HTML_URL = BASE_URL
            + "/mobile/member/khzxInfo.html?id=";
    // H5-客户投诉URL
    public static final String KHTS_HTML_URL = BASE_URL
            + "/mobile/member/khtsInfo.html?id=";
    // H5-推送消息URL
    public static final String PUSH_MSG_HTML_URL = BASE_URL
            + "/mobile/member/messageInfo.html?id=";
    // H5-八戒新闻URL
    public static final String NPC_NEWS_URL = BASE_URL
            + "/mobile/farm/newsInfo.html?id=";
    // H5-八戒活动URL
    public static final String NPC_ACT_URL = BASE_URL
            + "/mobile/farm/activityInfo.html?id=";
    // H5-缴费帮助URL
    public static final String HELP_HTML_URL = BASE_URL
            + "/mobile/front/helpInforH5.html?id=";
    // H5-会员等级URL
    public static final String GRADE_HTML_URL = BASE_URL
            + "/mobile/member/memberGrade.html";
    // H5-积分/邮金币说明URL
    public static final String SCORE_HTML_URL = BASE_URL
            + "/mobile/scoreDetail/getMemberJfsm.html?type=";
    // H5-积分/邮金币明细URL
    public static final String SCORE_URL = BASE_URL
            + "/mobile/member/getMemberScoreDetail.html";

    // 消息数量
    public static final String GET_MSG_NUM_URL = BASE_URL
            + "/mobile/front/getMessagePrompt.html";
    // 首页轮播图
    public static final String BANNER_URL = BASE_URL
            + "/mobile/front/getAdvert.html";
    // 首页精品推荐
    public static final String JPTJ_URL = BASE_URL
            + "/mobile/front/getIndexGoods.html";
    // 首页最新活动
    public static final String NEW_ACTIVITE_URL = BASE_URL
            + "/mobile/front/getZxhdggList.html";
    // 商品分类
    public static final String CATEGREY_URL = BASE_URL
            + "/mobile/goods/getGoodsCate.html";
    // 商品详情
    public static final String GOODS_DETAIL_URL = BASE_URL
            + "/mobile/goods/getGoodsInfo.html";
    // 商品搜索
    public static final String GOODS_SEARCH_URL = BASE_URL
            + "/mobile/front/searchGoods.html";
    // 商品品牌
    public static final String GOODS_BRAND_URL = BASE_URL
            + "/mobile/goods/getBrandByGoodsCate.html";
    // 登录接口url
    public static final String LOGIN_URL = BASE_URL
            + "/mobile/front/login.html";
    // 登出接口url
    public static final String LOGOUT_URL = BASE_URL
            + "/mobile/member/loginOut.html";
    // 加入购物车url
    public static final String ADD_CART_URL = BASE_URL
            + "/mobile/member/saveCart.html";
    // 购物车列表url
    public static final String MY_CART_URL = BASE_URL
            + "/mobile/member/myCart.html";
    // 购物车数量url
    public static final String CART_COUNT_URL = BASE_URL
            + "/mobile/member/getMemberCartCount.html";
    // 购物车商品删除url
    public static final String DEL_CART_URL = BASE_URL
            + "/mobile/member/delFromCart.html";
    // 商品评价url
    public static final String GOODS_RATED_URL = BASE_URL
            + "/mobile/front/getGoodsRated.html";
    // 商品购买记录url
    public static final String GOODS_RECOED_URL = BASE_URL
            + "/mobile/front/getGoodsByRecord.html";

    // 订单- 提交url
    public static final String SUBMIT_ORDER_URL = BASE_URL
            + "/mobile/member/saveOrder.html";
    // 订单- 列表url
    public static final String GET_ORDER_URL = BASE_URL
            + "/mobile/member/order/getOrder.html";
    // 订单- 取消url
    public static final String DEL_ORDER_URL = BASE_URL
            + "/mobile/member/order/deleteOrderById.html";
    // 订单- 确认收货url
    public static final String CONFIRM_ORDER_URL = BASE_URL
            + "/mobile/member/order/shouhuo.html";
    // 订单- 详情url
    public static final String DETAIL_ORDER_URL = BASE_URL
            + "/mobile/member/order/getOrderInfo.html";
    // 订单- 支付url
    public static final String PAY_ORDER_URL = BASE_URL
            + "/mobile/member/payOrder.html";
    // 订单- 评价url
    public static final String APPRAISE_ORDER_URL = BASE_URL
            + "/mobile/member/appraise.html";
    // 订单- 付款成功推荐商品url
    public static final String PAY_SUCCESS_RESULT_URL = BASE_URL
            + "/mobile/member/order/getRoundGoodsList.html";

    // 获取自提网点列表url
    public static final String GET_STATION_URL = BASE_URL
            + "/mobile/front/getBranch.html";
    // 获取自提网点列表url
    public static final String GET_POST_MONEY_URL = BASE_URL
            + "/mobile/front/getPostageMoney.html";
    // 获取收获地址列表url
    public static final String GET_ADDR_URL = BASE_URL
            + "/mobile/member/getAddress.html";
    // 删除收获地址url
    public static final String DEL_ADDR_URL = BASE_URL
            + "/mobile/member/delAddress.html";
    // 设置默认收获地址url
    public static final String DEFAULT_ADDR_URL = BASE_URL
            + "/mobile/member/defaultAddress.html";
    // 提交收获地址url
    public static final String SUBMIT_ADDR_URL = BASE_URL
            + "/mobile/member/editAddress.html";
    // 获取全国地区url
    public static final String GET_AREA_URL = BASE_URL
            + "/mobile/member/getArea.html";
    // 获取鹤壁三级地区url
    public static final String GET_HB_AREA_URL = BASE_URL
            + "/mobile/front/getHbAreaList.html";

    // 获取当前积分
    public static final String GET_SCORE_URL = BASE_URL
            + "/mobile/member/getMemberScore.html";
    // 个人中心-信息显示
    public static final String GET_MEMBER_URL = BASE_URL
            + "/mobile/member/getMemberCenter.html";
    // 银行卡-显示列表
    public static final String GET_BANKCARD_URL = BASE_URL
            + "/mobile/member/getMemberBankCard.html";
    // 银行卡-删除银行卡
    public static final String DEL_BANKCARD_URL = BASE_URL
            + "/mobile/member/deleteBankCard.html";
    // 银行卡-增加银行卡
    public static final String ADD_BANKCARD_URL = BASE_URL
            + "/mobile/member/saveBankCard.html";
    // 银行卡-验证银行卡
    public static final String CHECK_BANKCARD_URL = BASE_URL
            + "/mobile/member/checkBankCard.html";
    // 银行卡-签约银行卡
    public static final String SIGN_BANKCARD_URL = BASE_URL
            + "/mobile/member/signBankCard.html";
    // 银行卡-银行鉴权
    public static final String AUTH_BANKCARD_URL = BASE_URL
            + "/mobile/member/authentication.html";

    //支付密码-检查是否设置支付密码
    public static final String CHECK_PWD_URL = BASE_URL
            + "/mobile/member/queryPayPwd.html";
    //支付密码-检查是否设置支付密码
    public static final String CHANGE_PWD_URL = BASE_URL
            + "/mobile/member/setPayPwd.html";

    // 请求签到信息url
    public static final String GET_SIGN_DATA_URL = BASE_URL
            + "/mobile/member/getSignCalendar.html";
    // 请求签到次数url
    public static final String GET_SIGN_COUNT_URL = BASE_URL
            + "/mobile/member/memberSignCount.html";
    // 签到url
    public static final String DO_SIGN_URL = BASE_URL
            + "/mobile/member/doMemberSign.html";
    // 请求客户经理信息url
    public static final String GET_MANAGER_BYSIGN_URL = BASE_URL
            + "/mobile/front/getCusManagerBySign.html";
    // 请求客户经理信息url
    public static final String GET_MANAGER_URL = BASE_URL
            + "/mobile/member/getCustomManger.html";
    // 请求邀请历史信息url
    public static final String GET_INVITE_URL = BASE_URL
            + "/mobile/member/getMyInvitedHistory.html";

    // 请求用户信息url
    public static final String GET_USERINFO_URL = BASE_URL
            + "/mobile/member/getMemberDetail.html";
    // 修改用户信息url
    public static final String UPDATE_USERINFO_URL = BASE_URL
            + "/mobile/member/updateMemberDetail.html";
    // 修改密码url
    public static final String EDIT_PWD_URL = BASE_URL
            + "/mobile/member/updateMemberPassword.html";
    // 重置密码url
    public static final String RESET_PWD_URL = BASE_URL
            + "/mobile/front/setMemberPassword.html";
    // 获取验证码url
    public static final String GET_REGISTER_YZM_URL = BASE_URL
            + "/mobile/front/getVerificationCode.html";
    // 获取验证码url
    public static final String GET_YZM_URL = BASE_URL
            + "/mobile/front/getPhoneCode.html";
    // 获取验证码url
    public static final String GET_RESET_URL = BASE_URL
            + "/mobile/front/getResetCode.html";
    // 会员注册url
    public static final String REGISTER_URL = BASE_URL
            + "/mobile/front/doMemberRegist.html";
    // 检查验证码url
    public static final String CHECK_CODE_URL = BASE_URL
            + "/mobile/front/checkPhoneCode.html";
    // 检查原手机号url
    public static final String CHECK_PHONE_URL = BASE_URL
            + "/mobile/member/updateMemberMobileCheck.html";
    // 检查手机号唯一性url
    public static final String CHECK_PHONE_ONLY_URL = BASE_URL
            + "/mobile/front/checkPhone.html";
    // 更改新手机号url
    public static final String UPDATE_PHONE_URL = BASE_URL
            + "/mobile/member/updateMemberMobile.html";

    // 活动信息url
    public static final String HDXX_URL = BASE_URL
            + "/mobile/front/getHdxxList.html";
    // 通知公告url
    public static final String TZGG_URL = BASE_URL
            + "/mobile/front/getTzggList.html";
    // 推送消息url
    public static final String GET_PUSH_MSG_URL = BASE_URL
            + "/mobile/member/getMessageList.html";
    // 常见问题url
    public static final String CJWT_URL = BASE_URL
            + "/mobile/front/getCjwtCateList.html";
    // 客户咨询url
    public static final String KHZX_URL = BASE_URL
            + "/mobile/member/getKhzxList.html";
    // 添加咨询url
    public static final String ADDZX_URL = BASE_URL
            + "/mobile/member/saveKhzx.html";
    // 客户投诉url
    public static final String KHTS_URL = BASE_URL
            + "/mobile/member/getKhtsList.html";
    // 添加投诉url
    public static final String ADDTS_URL = BASE_URL
            + "/mobile/member/saveKhts.html";
    // 常见问题列表url
    public static final String CJWT_LIST_URL = BASE_URL
            + "/mobile/front/getCjwtListByCate.html";
    // 生成邀请链接url
    public static final String CREAT_INVENT_URL = BASE_URL
            + "/mobile/member/memberInviteCode.html";
    // 分享成功回调url
    public static final String SHARE_SUCCESS_URL = BASE_URL
            + "/mobile/member/memberShareNews.html";

    // 上传头像url
    public static final String UPDATE_IMG_URL = BASE_URL
            + "/mobile/member/updateHeadImage.html";

    // 检查应用更新url
    public static final String CKECK_UPDATA_URL = BASE_URL
            + "/mobile/front/getLastApkVersion.html";

    // 获取会员已添加缴费家庭类别url
    public static final String GET_MEMBER_HOME_URL = BASE_URL
            + "/mobile/member/payment/getMemberPaymentHome.html";
    // 获取我能添加的家庭类型url
    public static final String GET_ADD_HOME_URL = BASE_URL
            + "/mobile/member/payment/getMyHomeType.html";
    // 获取家庭详情url
    public static final String GET_HOME_DETAIL_URL = BASE_URL
            + "/mobile/member/payment/getPamentHome.html";
    // 添加家庭url
    public static final String ADD_HOME_URL = BASE_URL
            + "/mobile/member/payment/saveMemberPaymentHome.html";
    // 修改家庭url
    public static final String UPDATE_HOME_URL = BASE_URL
            + "/mobile/member/payment/updateMemberPaymentHome.html";
    // 删除家庭url
    public static final String DELETE_HOME_URL = BASE_URL
            + "/mobile/member/payment/deletePamentHome.html";
    // 获取家庭url
    public static final String GET_HOME_INFO_URL = BASE_URL
            + "/mobile/member/payment/getMyPaymentHomeByType.html";
    // 获取家庭项目列表url
    public static final String GET_HOME_LIST_URL = BASE_URL
            + "/mobile/member/payment/getPaymentAccountByHome.html";
    // 获取缴费单位列表url
    public static final String GET_COMPANY_LIST_URL = BASE_URL
            + "/mobile/member/payment/getCompanyByPayment.html";
    // 添加缴费账户url
    public static final String ADD_JF_ACCOUNT_URL = BASE_URL
            + "/mobile/member/payment/savePaymentAccount.html";
    // 修改缴费账户url
    public static final String UPDATE_JF_ACCOUNT_URL = BASE_URL
            + "/mobile/member/payment/updatePaymentAccount.html";
    // 解绑缴费账户url
    public static final String DEL_JF_ACCOUNT_URL = BASE_URL
            + "/mobile/member/payment/doUnbundingPaymentAccount.html";
    // 获得缴费单信息url
    public static final String GET_JF_ACCOUNT_INFO_URL = BASE_URL
            + "/mobile/member/payment/getPaymentAccountInfo.html";

    // 缴费单水费支付url
    public static final String PAY_WATER_BILL_URL = BASE_URL
            + "/mobile/member/payment/doPaymentBill.html";
    // 缴费单电费支付url
    public static final String PAY_ELE_BILL_URL = BASE_URL
            + "/mobile/member/payment/doPayElectricityCharge.html";
    // 燃气费缴费支付url
    public static final String PAY_GAS_BILL_URL = BASE_URL + "/mobile/member/payment/doPayGasCharge.html";
    // 有线电视支付url
    public static final String PAY_TV_BILL_URL = BASE_URL + "/mobile/member/payment/doPayOnlineTVCharge.html";
    // 固话宽带支付url
    public static final String PAY_BROAD_BILL_URL = BASE_URL
            + "/mobile/member/payment/phoneChange.html";
    // 手机缴费支付url
    public static final String PAY_MOBILE_BILL_URL = BASE_URL
            + "/mobile/member/payment/phoneChange.html";

    // 获取水费缴费历史url
    public static final String GET_WATER_PAY_HISTORY_URL = BASE_URL
            + "/mobile/member/payment/getWaterPaymentHistory.html";
    // 获取电费缴费历史url
    public static final String GET_ELE_PAY_HISTORY_URL = BASE_URL
            + "/mobile/member/payment/getElectricityPaymentHistory.html";
    // 获取燃气费缴费历史url
    public static final String GET_GAS_PAY_HISTORY_URL = BASE_URL + "/mobile/member/payment/getGasPaymentHistory.html";
    // 获取有线电视缴费历史url
    public static final String GET_TV_PAY_HISTORY_URL = BASE_URL + "/mobile/member/payment/getOnlineTVPaymentHistory.html";
    // 获取固话宽带缴费历史url
    public static final String GET_BROAD_PAY_HISTORY_URL = BASE_URL
            + "/mobile/member/payment/phoneChangeHistory.html";
    // 获取手机缴费历史url
    public static final String GET_MOBILE_PAY_HISTORY_URL = BASE_URL
            + "/mobile/member/payment/phoneChangeHistory.html";

    // 获取水费缴费单详情url
    public static final String GET_WATER_PAY_DETAIL_URL = BASE_URL
            + "/mobile/member/payment/getWaterPaymentInfo.html";
    // 获取电费费缴费单详情url
    public static final String GET_ELE_PAY_DETAIL_URL = BASE_URL
            + "/mobile/member/payment/getElectricityPaymentInfo.html";
    // 获取燃气费缴费单详情url
    public static final String GET_GAS_PAY_DETAIL_URL = BASE_URL
            + "/mobile/member/payment/getGasPaymentInfo.html";
    // 获取有线电视缴费单详情url
    public static final String GET_TV_PAY_DETAIL_URL = BASE_URL
            + "/mobile/member/payment/getOnlineTVPaymentInfo.html";
    // 获取固话宽带缴费单详情url
    public static final String GET_BROAD_PAY_DETAIL_URL = BASE_URL
            + "/mobile/member/payment/phoneChangeDetail.html";
    // 获取手机缴费缴费单详情url
    public static final String GET_MOBILE_PAY_DETAIL_URL = BASE_URL
            + "/mobile/member/payment/phoneChangeDetail.html";
    // 获取手机号运营商信息url
    public static final String GET_YYS_URL = BASE_URL
            + "/mobile/member/payment/getOperator.html";
    // 获取手机号余额信息url
    public static final String GET_BALANCE_URL = BASE_URL
            + "/mobile/member/payment/getFeeBalance.html";
    // 获取使用帮助列表url
    public static final String GET_HELP_LIST_URL = BASE_URL
            + "/mobile/front/getHelpList.html";
    // 验证邀请码url
    public static final String CHECKCODE = BASE_URL
            + "/mobile/member/verifyCode.html";
    //获取默认收货地址url
    public static final String GET_DEFAULT_ADDR = BASE_URL + "/mobile/member/getDefaultAddress.html";
    //八戒新闻url
    public static final String BJ_NEWS_LIST = BASE_URL + "/mobile/farm/getNewsList.html";
    //八戒新闻url
    public static final String BJ_ACT_LIST = BASE_URL + "/mobile/farm/getActivityList.html";
    //活动报名url
    public static final String BJ_ACT_JOIN_LIST = BASE_URL + "/mobile/farm/saveSignUp.html";
    //宅配卡列表url
    public static final String BJ_ZHAI_LIST = BASE_URL + "/mobile/farm/getGoodsList.html";
    //宅配卡详情url
    public static final String BJ_ZHAI_INFO = BASE_URL + "/mobile/farm/goodsInfo.html";
    //H5-宅配卡详情url
    public static final String BJ_ZHAI_DETAIL = BASE_URL + "/mobile/farm/goodsDetail.html?id=";
    //获取宅配卡价格url
    public static final String BJ_ZHAI_PRICE = BASE_URL + "/mobile/farm/getPriceByItem.html";
    //提交宅配卡订单url
    public static final String SUBMIT_ZHAI_ORDER = BASE_URL + "/mobile/member/saveZpk.html";
    //提交宅配卡订单url
    public static final String PAY_ZHAI_ORDER = BASE_URL + "/mobile/member/payZpk.html";
    //我的宅配卡订单列表url
    public static final String ZHAI_ORDER_LIST = BASE_URL + "/mobile/member/zpkRecord.html";
    //我的宅配卡订单详情url
    public static final String ZHAI_ORDER_DETAIL = BASE_URL + "/mobile/member/zpkDetail.html";
    //H5-农场简介url
    public static final String NCJJ = BASE_URL + "/mobile/farm/getIntroduction.html";
    //八戒农场搜索url
    public static final String BJ_SEARCH = BASE_URL + "/mobile/farm/searchFarmGoods.html";
    //宅配卡购买记录url
    public static final String ZPK_RECORD = BASE_URL + "/mobile/farm/getOrderList.html";
    //网点标记列表url
    public static final String MAP_DATA = BASE_URL + "/mobile/front/mapData.html";
    //网点标记详情url
    public static final String MAP_POINT_DETAIL = BASE_URL + "/mobile/front/bzDetail.html";
    public static final String MAP_AREA_INFO = BASE_URL + "/mobile/front/getAreaInfo.html";
    public static final String MAP_ORG_INFO = BASE_URL + "/mobile/front/getOrgInfo.html";

    //面对面付-银联支付
    public static final String F2F_UNION_PAY = BASE_URL + "/mobile/member/f2fUnionPay.html";
    //面对面付-交易历史
    public static final String DEAL_HISTORY = BASE_URL + "/mobile/member/dealHistory.html";
    //面对面付-交易详情
    public static final String DEAL_INFO = BASE_URL + "/mobile/member/dealInfo.html";
    //面对面付-交易申诉
    public static final String DEAL_APPEAL = BASE_URL + "/mobile/member/appealDeal.html";
    //面对面付-支付密码
    public static final String DEAL_CHECK_PWD = BASE_URL + "/mobile/member/checkPayPwd.html";
    //面对面付-查询是否设置支付密码
    public static final String DEAL_QUERY_PWD = BASE_URL + "/mobile/member/queryPayPwd.html";
    //面对面付-重置支付密码-发送验证码
    public static final String GET_RESET_PWD_CODE = BASE_URL + "/mobile/member/getResetPwdCode.html";
    //面对面付-重置支付密码
    public static final String RESET_PAY_PWD = BASE_URL + "/mobile/member/doResetPayPwd.html";

    //我的优惠券列表
    public static final String GET_YHQ_LIST_URL = BASE_URL + "/mobile/member/myCouponList.html";
    //兑换组合优惠券列表
    public static final String GET_ZH_YHQ_LIST_URL = BASE_URL + "/mobile/member/couponPackageDetail.html";
    //兑换优惠券列表
    public static final String GET_DH_YHQ_LIST_URL = BASE_URL + "/mobile/member/couponList.html";
    //支付时获取GET_可用优惠券
    public static final String USEFUL_YHQ_URL = BASE_URL + "/mobile/member/getCoupon.html";
    //支付成功时获取奖励优惠券
    public static final String AWARD_YHQ_URL = BASE_URL + "/mobile/member/getAwardCoupons.html";
    //领取优惠券
    public static final String GET_YHQ_URL = BASE_URL + "/mobile/member/exchange.html";

    // SP文件名
    public static final String SP_NAME = "config";
    /*
     * SP文件Key
     */
    // 用户名
    public static final String KEY_USERNAME = "username";
    //是否需要导航
    public static final String KEY_IS_GUIDE = "isGuide";
    // 密码
    public static final String KEY_PASSWORD = "password";
    // 用户ID
    public static final String USER_ID = "userId";
    // 是否设置支付密码
    public static final String PAY_PWD = "pay_pwd";
    // 搜索记录
    public static final String KEY_SEARCH = "search";

    // 缓存目录
    public static final String CACHE_PATH = Environment
            .getExternalStorageDirectory().getPath()
            + "/com.ksource.hbpostal/cache/";
    // 下载目录
    public static final String DOWNLOADFILEPATH = Environment
            .getExternalStorageDirectory().getPath()
            + "/com.ksource.hbpostal/download/";
    // 图片目录
    public static final String IMAGEFILEPATH = Environment
            .getExternalStorageDirectory().getPath()
            + "/com.ksource.hbpostal/images/";

    // 用户TOKEN
    public static final String TOKEN = "token";

    // 启动页轮播图缓存KEY
    public static final String SPLASH_BANNER = "splashBanner";
    // 是否显示启动页轮播图KEY
    public static final String IS_SHOW_BANNER = "isShowBanner";
    // 是否更新应用KEY
    public static final String IS_UPDATE = "isUpdate";

}
