package com.yitao.util;

public enum HttpCharset {

	UTF8("utf8"),
	
	GBK("gbk");
	
	private final String charset;
	
	private HttpCharset(String charset){
		this.charset = charset;
	}
	
	
}
