package com.ksource.hbpostal.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public class BJNCNewsResaultBean {

    /**
     * newsList : [{"id":"3d86ed9724e544f7888cf25d0d64a7ad","title":"测试一下新闻修改","image":"/upload/infofiles/20170311144453_7346.jpg","abstract":"新闻摘要","content":"<p>新闻内容<\/p>","create_time":20170311140952},{"id":"0b5b012b9a4f4bf596ec4ea8dac0dd38","title":"测试新闻2","image":null,"abstract":"没有摘要","content":"<p>没有内容<\/p>","create_time":20170311152311},{"id":"edfa194ed7574c2a8c9649555902dbc1","title":"新闻1","image":null,"abstract":"没有","content":"<p>没有<br/><\/p>","create_time":20170313100807},{"id":"06cb80fdaf03470595393702d5898650","title":"新闻0","image":null,"abstract":"没有","content":"<p>没有&nbsp;&nbsp;&nbsp;&nbsp;<br/><\/p>","create_time":20170313100842},{"id":"a2877004127a42249376057dc83956ae","title":"新闻4","image":null,"abstract":"没有","content":"<p>没有<br/><\/p>","create_time":20170313100955},{"id":"e67185d06f5d4299ae7eb42238d116a6","title":"新闻5","image":null,"abstract":"么有","content":"<p>没有<br/><\/p>","create_time":20170313101017},{"id":"93372c02d2554bd3a1dce87a26152b9c","title":"新闻6","image":null,"abstract":"没有","content":"<p>没有<br/><\/p>","create_time":20170313101039}]
     * flag : 0
     * msg : 请求成功
     * success : true
     */

    public int flag;
    public String msg;
    public boolean success;
    public List<NewsListBean> newsList;


    public static class NewsListBean {
        /**
         * id : 3d86ed9724e544f7888cf25d0d64a7ad
         * title : 测试一下新闻修改
         * image : /upload/infofiles/20170311144453_7346.jpg
         * abstract : 新闻摘要
         * content : <p>新闻内容</p>
         * create_time : 20170311140952
         */

        public String id;
        public String title;
        public String image;
        public String abs;
        public String content;
        public String create_time;
        public long deadline;
        public int isSign;


    }
}
