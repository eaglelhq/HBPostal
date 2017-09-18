package com.ksource.hbStore.config;

import android.os.Environment;

/**
 * 常量类
 *
 * @author Administrator
 */

public class ConstantValues {
    // 研发
//    public static final String BASE_URL = "http://10.10.10.10:65532"; //裴
//    public static final String BASE_URL = "http://10.10.10.10:60011"; //梁创
//    public static final String BASE_URL = "http://172.26.165.1:8080";  //裴
//    public static final String BASE_URL = "http://172.22.169.1:8080";  //裴
    //	 public static final String BASE_URL = "http://172.16.129.1:8080";
//    public static final String BASE_URL = "http://172.18.193.1:8080/larks_hbyz";
	 public static final String BASE_URL = "http://123.15.56.103:8888";//正式
//    public static final String BASE_URL = "http://121.42.156.76:9060";//测试
//    外网
//    public static final String BASE_URL = "http://yzf.yunque365.com";
//    public static final String BASE_URL = "http://139.129.118.140:8900";

    //面对面付-交易历史 / 待结算明细 / 已结算明细
    public static final String DEAL_HISTORY = BASE_URL + "/mobile/seller/dealHistory.html";
    //面对面付-交易 / 待结算明细 / 已结算明细详情
    public static final String DEAL_DETAIL = BASE_URL + "/mobile/seller/dealHistoryDetail.html";
    //面对面付-交易申诉
    public static final String DEAL_APPEAL = BASE_URL + "/mobile/seller/appealDeal.html";
    //面对面付-商家登陆
    public static final String DEAL_LOGIN = BASE_URL + "/mobile/sellerFront/login.html";
    //面对面付-检查升级
    public static final String CKECK_UPDATA_URL = BASE_URL + "/mobile/sellerFront/updateVersion.html";
    //面对面付-设置收款账户
    public static final String SET_CARD_URL = BASE_URL + "/mobile/seller/updateAccount.html";
    //面对面付-获取商家信息
    public static final String SELLER_INFO = BASE_URL + "/mobile/seller/getSellerInfo.html";
    //面对面付-获取结算单列表
    public static final String JIESUAN_LIST = BASE_URL + "/mobile/seller/statementList.html";
    //面对面付-获取结算单日历
    public static final String JIESUAN_CAL = BASE_URL + "/mobile/seller/calenderList.html";
    //面对面付-退出登录
    public static final String LOGOUT_URL = BASE_URL + "/mobile/seller/closeLogin.html";
    //面对面付-修改密码
    public static final String EDIT_PWD_URL = BASE_URL + "/mobile/seller/updatePassword.html";
    //面对面付-忘记密码-发送验证码
    public static final String GET_YZM_URL = BASE_URL + "/mobile/sellerFront/getSellerPwdCode.html";
    //面对面付-重置密码
    public static final String RESET_PWD_URL = BASE_URL + "/mobile/sellerFront/doSellerPayPwd.html";
    //面对面付-修改头像
    public static final String UPDATE_IMG_URL = BASE_URL + "/mobile/seller/.html";

//    //面对面付-支付密码
//    public static final String DEAL_CHECK_PWD = BASE_URL + "/mobile/member/checkPayPwd.html";
//    //面对面付-查询是否设置支付密码
//    public static final String DEAL_QUERY_PWD = BASE_URL + "/mobile/member/queryPayPwd.html";
//    //面对面付-重置支付密码-发送验证码
//    public static final String GET_RESET_PWD_CODE = BASE_URL + "/mobile/member/getResetPwdCode.html";
//    //面对面付-重置支付密码
//    public static final String RESET_PAY_PWD = BASE_URL + "/mobile/member/doResetPayPwd.html";

    // SP文件名
    public static final String SP_NAME = "config";
    /*
     * SP文件Key
     */
    // 用户名
    public static final String KEY_USERNAME = "username";
    // 密码
    public static final String KEY_PASSWORD = "password";
    // 商户ID
    public static final String USER_ID = "userId";
    // 商户ID
    public static final String SELLER_ID = "sellerId";
    // 商户name
    public static final String USER_NAME = "shopName";
    // 用户TOKEN
    public static final String TOKEN = "token";

    // 缓存目录
    public static final String CACHE_PATH = Environment
            .getExternalStorageDirectory().getPath()
            + "/com.ksource.hbStore/cache/";
    // 下载目录
    public static final String DOWNLOADFILEPATH = Environment
            .getExternalStorageDirectory().getPath()
            + "/com.ksource.hbStore/download/";

    // 图片目录
    public static final String IMAGEFILEPATH = Environment
            .getExternalStorageDirectory().getPath()
            + "/com.ksource.hbStore/images/";


}
