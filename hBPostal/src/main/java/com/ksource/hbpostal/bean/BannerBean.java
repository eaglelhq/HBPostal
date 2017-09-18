package com.ksource.hbpostal.bean;

import java.util.List;

public class BannerBean {
    /**
     * flag : 0
     * advertList : [{"TITLE":"爱上富商大贾","IMAGE":"/upload/infofiles/20161013085017_625.png","URL":"http://www.baidu.com"}]
     * msg : 获取启动页广告成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;
    /**
     * TITLE : 爱上富商大贾
     * IMAGE : /upload/infofiles/20161013085017_625.png
     * URL : http://www.baidu.com
     */

    public List<AdvertListBean> advertList;

    

    public static class AdvertListBean {
        public String TITLE;
        public String IMAGE;
        public String URL;
        public String LINK_TYPE;

       
    }
}
