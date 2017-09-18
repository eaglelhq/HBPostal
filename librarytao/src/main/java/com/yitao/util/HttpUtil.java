package com.yitao.util;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpUtil {
	
	
	/**
	 * use doGet() 
	 * 	通过Http Get协议访问网址，并返回内容
	 * @param strUrl
	 * @return
	 */
	@Deprecated
	public static String getContentForGet(String strUrl){
		try {
			// 创建url对象
			URL url = new URL(strUrl);
			// 打开url连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// 设置url请求方式 ‘get’ 或者 ‘post’
			connection.setRequestMethod("GET");
			// 发送
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer=new StringBuffer();
			int ch=0;
			while((ch=in.read())!=-1)
			buffer.append((char)ch);
			in.close();
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	
	
	
	/**
	 * 使用Http Post协议访问URL
	 * @param url 要访问的URL
	 * @param paramValues 参数
	 * @param charset 字符集
	 * @return
	 */
	public static String  doPost(String url,Map<String, String> paramValues,HttpCharset charset) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httpost = new HttpPost(url);
			
			Gson gson = new Gson();
			String paramStr = gson.toJson(paramValues);
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("params", paramStr);
			
			
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			
			String param = "";
			if (params != null && !params.isEmpty()) {
				for (Iterator iterator = params.keySet().iterator(); iterator.hasNext();) {
					String key = (String)iterator.next();
					nvps.add(new BasicNameValuePair(key, params.get(key)));
					param += key + "=" + params.get(key) + ";";
				}
			}

			
	        httpost.setEntity(new UrlEncodedFormEntity(nvps,charset.toString()));

	        HttpResponse response = httpclient.execute(httpost);
	        HttpEntity entity = response.getEntity();
	        BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent(),charset.toString()));
			StringBuffer buffer=new StringBuffer();
			int ch=0;
			while((ch=in.read())!=-1)
			buffer.append((char)ch);
			in.close();
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return "";
	}
	
	/**
	 * 使用Http Get协议访问URL
	 * @param url 要访问的URL，不带问号”？“
	 * @param params 参数
	 * @param charset 字符集
	 * @return
	 */
	public static String  doGet(String url,Map<String, String> params,HttpCharset charset){
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		//===========================================================
		//数据以json格式传输，所以这里加了通用限制  by jiangxiaolei  2014-05-05
		
		/*Gson gson = new Gson();
		String paramStr = gson.toJson(paramValues);
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("param", paramStr);*/
		//===========================================================
		
		
		try {
			String param = "";
			if (params != null && !params.isEmpty()) {
				for (Iterator iterator = params.keySet().iterator(); iterator.hasNext();) {
					String key = (String)iterator.next();
					param += "&"+key +"=" + params.get(key);
				}
				param = param.substring(1,param.length());
				param = "?"+param;
			}
			
			String myurl = url+param;
			System.out.println("-------->myurl"+myurl);
			
			HttpGet httpost = new HttpGet(myurl);
	        HttpResponse response = httpclient.execute(httpost);
	        HttpEntity entity = response.getEntity();
	        BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent(),charset.toString()));
			StringBuffer buffer=new StringBuffer();
			int ch=0;
			while((ch=in.read())!=-1)
			buffer.append((char)ch);
			in.close();
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.e("doGet",e.getMessage());
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return "";
	}
	
	
	/**
	 * 以POST方式发送内容体
	 * @param targetUrl
	 * @param content
	 * @param charset
	 * @version V1.0.0
	 * @author 杨凯
	 * @date Feb 10, 2015 11:21:27 AM
	 */
	public static String doPost(String targetUrl,String content,HttpCharset charset){
		try { 
			// 建立连接 
			URL url = new URL(targetUrl); 
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection(); 
	
			// //设置连接属性 
			httpConn.setDoOutput(true);// 使用 URL 连接进行输出 
			httpConn.setDoInput(true);// 使用 URL 连接进行输入 
			httpConn.setUseCaches(false);// 忽略缓存 
			httpConn.setRequestMethod("POST");// 设置URL请求方法 
	
			 
			// 设置请求属性 
			// 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致 
			byte[] requestStringBytes = content.getBytes(charset.toString()); 
			httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length); 
			httpConn.setRequestProperty("Content-Type", "application/octet-stream"); 
			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接 
			httpConn.setRequestProperty("Charset", "UTF-8"); 
			// 
//			String name = URLEncoder.encode("黄武艺", "utf-8"); 
//			httpConn.setRequestProperty("NAME", name); 
	
			 
			// 建立输出流，并写入数据 
			OutputStream outputStream = httpConn.getOutputStream(); 
			outputStream.write(requestStringBytes); 
			outputStream.close(); 
			// 获得响应状态 
			int responseCode = httpConn.getResponseCode(); 
	
			 
			if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功 
			// 当正确响应时处理数据 
			StringBuffer sb = new StringBuffer(); 
			String readLine; 
			BufferedReader responseReader; 
			// 处理响应流，必须与服务器响应流输出的编码一致 
			 responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), charset.toString())); 
			while ((readLine = responseReader.readLine()) != null) { 
				sb.append(readLine).append("\n"); 
				} 
				responseReader.close(); 
				return sb.toString();
			} 
		} catch (Exception ex) { 
		ex.printStackTrace(); 
		} 

		 return "";
		} 
	
	public static void main(String[] args) {
		System.out.println(System.getProperties().getProperty("os.name"));
	}
	
}
