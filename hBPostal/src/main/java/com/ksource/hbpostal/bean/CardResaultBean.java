package com.ksource.hbpostal.bean;

import java.util.List;

public class CardResaultBean {
    /**
     * bankCardList : [{"ID":"462a41424fa94302876a13f2b9d19419","BANK_CARD":"6222520624510232","IS_DEFAULT_BANK_NO":1,"SIGN_STATE":null,"MOBILE":"15803816250","BANK_CARD_NAME":"江小磊"},{"ID":"132ef9b36dc44ad1a04f07333648c6bb","BANK_CARD":"6222620620022896608","IS_DEFAULT_BANK_NO":1,"SIGN_STATE":null,"MOBILE":"15803816250","BANK_CARD_NAME":"江小磊"},{"ID":"7abe5143647541bdb3beeca50a4ef064","BANK_CARD":"622991125300836415","IS_DEFAULT_BANK_NO":1,"SIGN_STATE":null,"MOBILE":"15803816250","BANK_CARD_NAME":"江小磊"},{"ID":"90f328f9f0304f2a861adb92e4fbda0c","BANK_CARD":"6217868000001119580","IS_DEFAULT_BANK_NO":1,"SIGN_STATE":null,"MOBILE":"15803816250","BANK_CARD_NAME":"江小磊"},{"ID":"d19d356a995d4b5a83ffaaa46dbebd39","BANK_CARD":"6222600620035012155","IS_DEFAULT_BANK_NO":1,"SIGN_STATE":null,"MOBILE":"15803816250","BANK_CARD_NAME":"江小磊"}]
     * flag : 0
     * msg : 获取会员银行卡列表成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;
    public List<BankCardListBean> bankCardList;


    public static class BankCardListBean {
        /**
         * ID : 462a41424fa94302876a13f2b9d19419
         * BANK_CARD : 6222520624510232
         * IS_DEFAULT_BANK_NO : 1
         * SIGN_STATE : null
         * MOBILE : 15803816250
         * BANK_CARD_NAME : 江小磊
         */

        public String ID;
        public String BANK_CARD;
        public int IS_DEFAULT_BANK_NO;
        public int SIGN_STATE;
        public String MOBILE;
        public String BANK_CARD_NAME;
        public String ID_CARD;


    }
}
