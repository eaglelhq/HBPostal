package com.ksource.hbStore.bean;

import java.util.List;

public class ScanHistoryResultBean {

    /**
     * history : [{"ID":"2666cbf3bc0747a681ffde908e7f6b43","ORDER_ID":"DD-20170705-958614","PAY_AMOUNT":0,"SCORE_NUM":1,"PAY_TIME":"20170705164653","PAY_TYPE":1,"APPEAL_STATE":0,"NAME":"江小磊","IS_SETTLE":0},{"ID":"e216e22700494793bc86eca72ef95317","ORDER_ID":"DD-20170703-707725","PAY_AMOUNT":0,"SCORE_NUM":1,"PAY_TIME":"20170703103741","PAY_TYPE":1,"APPEAL_STATE":0,"NAME":"江小磊","IS_SETTLE":0},{"ID":"16df733a06954ea8b597543e80312307","ORDER_ID":"DD-20170703-487272","PAY_AMOUNT":0,"SCORE_NUM":1,"PAY_TIME":"20170703102214","PAY_TYPE":1,"APPEAL_STATE":0,"NAME":"江小磊","IS_SETTLE":0},{"ID":"40b980b87e7d4a25bdc46a0eb3ae4730","ORDER_ID":"DD-20170702-699576","PAY_AMOUNT":0,"SCORE_NUM":2,"PAY_TIME":"20170702114429","PAY_TYPE":1,"APPEAL_STATE":0,"NAME":"江小磊","IS_SETTLE":0},{"ID":"29c8c0d0ee714b7f8645eb91223e030a","ORDER_ID":"DD-20170702-885456","PAY_AMOUNT":0,"SCORE_NUM":1,"PAY_TIME":"20170702110931","PAY_TYPE":1,"APPEAL_STATE":0,"NAME":"江小磊","IS_SETTLE":0},{"ID":"6b6fb8735d524daaa76bfb62e68a91a9","ORDER_ID":"DD-20170701-245702","PAY_AMOUNT":0,"SCORE_NUM":10,"PAY_TIME":"20170701200913","PAY_TYPE":1,"APPEAL_STATE":0,"NAME":"江小磊","IS_SETTLE":0},{"ID":"29bead0e8a284851bb4545b3dcc83c90","ORDER_ID":"DD-20170701-363304","PAY_AMOUNT":0,"SCORE_NUM":1,"PAY_TIME":"20170701185215","PAY_TYPE":1,"APPEAL_STATE":0,"NAME":"江小磊","IS_SETTLE":1},{"ID":"8f95c60e08e84d63b610b253d02c1a7d","ORDER_ID":"DD-20170701-233376","PAY_AMOUNT":0,"SCORE_NUM":1,"PAY_TIME":"20170701175439","PAY_TYPE":1,"APPEAL_STATE":0,"NAME":"江小磊","IS_SETTLE":1},{"ID":"d7c14ca28f64439ab34c44990effae81","ORDER_ID":"DD-20170701-958775","PAY_AMOUNT":0,"SCORE_NUM":2,"PAY_TIME":"20170701115306","PAY_TYPE":1,"APPEAL_STATE":0,"NAME":"江小磊","IS_SETTLE":0},{"ID":"555545c1a5c049fab7536507166900e0","ORDER_ID":"DD-20170701-572591","PAY_AMOUNT":0,"SCORE_NUM":2,"PAY_TIME":"20170701104241","PAY_TYPE":1,"APPEAL_STATE":0,"NAME":"江小磊","IS_SETTLE":0}]
     * flag : 0
     * msg : 获取收款历史成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;
    public List<HistoryBean> history;


    public static class HistoryBean {
        /**
         * ID : 2666cbf3bc0747a681ffde908e7f6b43
         * ORDER_ID : DD-20170705-958614
         * PAY_AMOUNT : 0
         * SCORE_NUM : 1
         * PAY_TIME : 20170705164653
         * PAY_TYPE : 1
         * APPEAL_STATE : 0
         * NAME : 江小磊
         * IS_SETTLE : 0
         */

        public String ID;
        public String ORDER_ID;
        public double PAY_AMOUNT;
        public double TOTAL_AMOUNT;
        public int SCORE_NUM;
        public String PAY_TIME;
        public int PAY_TYPE;
        public int APPEAL_STATE;
        public String NAME;
        public int IS_SETTLE;

    }
}
