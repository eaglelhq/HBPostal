package com.yitao.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadFile {
	private static final String TAG = "uploadFile";
	private static String reqUrl = "http://yzf.yunque365.com";

	/**
	 * android上传文件到服务器,单文件上传
	 * 
	 * @param file
	 *            需要上传的文件
	 * @param handler
	 *            上传回调
	 * @return 返回响应的内容
	 */
	public static void uploadFile(final File file, final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String LINEND = "\r\n";
				String PREFIX = "--";
				String BOUNDARY = java.util.UUID.randomUUID().toString(); // 数据分界线
				String MULTIPART_FROM_DATA = "multipart/form-data";
				String CHARSET = "charset=UTF-8";
				long totalCount = file.length();
				try {
					URL url = new URL(reqUrl);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					/* 允许Input、Output，不使用Cache */
					con.setDoInput(true);
					con.setDoOutput(true);
					con.setUseCaches(false);
					con.setRequestMethod("POST");
					con.setRequestProperty("Connection", "Keep-Alive");
					// con.setRequestProperty("Cookie",
					// MyApplication.cookie.getName() + "=" +
					// MyApplication.cookie.getValue() + "; domain=" +
					// MyApplication.cookie.getDomain());
					con.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";" + CHARSET + ";boundary=" + BOUNDARY);
					con.setRequestProperty("user-agent", "mozilla/4.7 [en] (win98; i)");

					/* 设置DataOutputStream */
					DataOutputStream ds = new DataOutputStream(con.getOutputStream());
					String uploadStr = PREFIX + BOUNDARY + LINEND;// 数据开始标识
					uploadStr += "Content-Disposition: form-data; " + "name=\"file\";filename=\"" + file.getName() + "\"" + LINEND;
					uploadStr += LINEND;
					ds.write(uploadStr.getBytes());

					/* 取得文件的FileInputStream */
					FileInputStream fStream = new FileInputStream(file);
					byte[] buffer = new byte[1024];
					int length = -1;
					int count = 0;
					/* 从文件读取数据至缓冲区 */
					while ((length = fStream.read(buffer)) != -1) {
						ds.write(buffer, 0, length);
						count += length;
//						Message message = new Message();
//						message.what = 0;
//						message.obj = ((count * 100) / totalCount) + "";
//						handler.sendMessage(message);
					}
					ds.write(LINEND.getBytes());
					byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes("UTF-8");
					ds.write(end_data);
					;
					fStream.close();
					ds.flush();
					/* 取得Response内容 */
					InputStream is = con.getInputStream();
					try {
						JSONObject json = new JSONObject(InputStreamTOString(is));
						boolean success = json.getBoolean("success");
						Message message = new Message();
						if (success) {
							String filePath = json.getString("file_path");
							String fileName = json.getString("file_name");
							message.what = 1;// 上传成功
							Bundle bundle = new Bundle();
							bundle.putString("filePath", filePath);
							bundle.putString("fileName", fileName);
							message.setData(bundle);
							handler.sendMessage(message);
						} else {
							message.what = 2;// 上传失败
							handler.sendMessage(message);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					ds.close();
				} catch (Exception e) {
					e.printStackTrace();
					Message message = new Message();
					message.what = 2;// 上传失败
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	/**
	 * 方法说明：解码<br>
	 * 作者：易涛 <br>
	 * 时间：2015-10-13 下午2:48:01 <br>
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 * <br>
	 */
	public static String InputStreamTOString(InputStream in) throws Exception {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int count = -1;
		while ((count = in.read(data, 0, 1024)) != -1)
			outStream.write(data, 0, count);
		data = null;
		return new String(outStream.toByteArray(), "utf-8");
	}

	/**
	 * 方法说明：多文件上传<br>
	 * 作者：易涛 <br>
	 * 时间：2015-10-8 上午10:06:51 <br>
	 * 
	 * @param filePathList
	 * @param handler
	 * <br>
	 */
	public static void uploadFiles(final List<String> filePathList, final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String end = "\r\n";
				String twoHyphens = "--";
				String boundary = "*****";
				try {
					// http://10.65.12.52:8088/DemoUpload/upload.do
					URL url = new URL(reqUrl);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					/* 允许Input、Output，不使用Cache */
					con.setDoInput(true);
					con.setDoOutput(true);
					con.setUseCaches(false);
					/* 设置传送的method=POST */
					con.setRequestMethod("POST");
					/* setRequestProperty */
					con.setRequestProperty("Connection", "Keep-Alive");
					// con.setRequestProperty("Cookie",
					// MyApplication.cookie.getName() + "=" +
					// MyApplication.cookie.getValue() + "; domain=" +
					// MyApplication.cookie.getDomain());
					con.setRequestProperty("Charset", "UTF-8");
					con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
					/* 设置DataOutputStream */
					DataOutputStream ds = new DataOutputStream(con.getOutputStream());
					InputStream in = null;
					if (filePathList != null)
						for (String str : filePathList) {
							File file = new File(str);
							ds.writeBytes(twoHyphens + boundary + end);
							ds.writeBytes("Content-Disposition: form-data; " + "name=\"file\";filename=\"" + file.getName() + "\"" + end);
							ds.writeBytes(end);
							/* 取得文件的FileInputStream */
							FileInputStream fStream = new FileInputStream(file);
							/* 设置每次写入1024bytes */
							int bufferSize = 1024;
							byte[] buffer = new byte[bufferSize];
							int length = -1;
							/* 从文件读取数据至缓冲区 */
							while ((length = fStream.read(buffer)) != -1) {
								/* 将资料写入DataOutputStream中 */
								ds.write(buffer, 0, length);
							}
							fStream.close();
							ds.writeBytes(end);
						}
					ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
					/* close streams */
					ds.flush();
					// 得到响应码
					int res = con.getResponseCode();
					if (res == 200) {
						in = con.getInputStream();
						int ch;
						StringBuilder str = new StringBuilder();
						while ((ch = in.read()) != -1) {
							str.append((char) ch);
						}
						String ss = str.toString();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void uploadFileList(final List<String> filePathList, final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File file = null;
				List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
				if (filePathList != null && filePathList.size() != 0) {
					boolean complete = true;
					for (int i = 0; i < filePathList.size(); i++) {
						file = new File(filePathList.get(i));
						Map<String, String> map = upload(file);
						if (map != null) {
							mapList.add(map);
						} else {
							complete = false;
							break;
						}
					}
					Message message = new Message();
					if (complete) {
						message.what = 0;// 上传成功
						Bundle bundle = new Bundle();
						bundle.putSerializable("fileMap", (Serializable) mapList);
						message.setData(bundle);
					} else {
						message.what = 1;// 上传失败
					}
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	private static Map<String, String> upload(File file) {
		Map<String, String> map = null;
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			URL url = new URL(reqUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			// con.setRequestProperty("Cookie", MyApplication.cookie.getName() +
			// "=" + MyApplication.cookie.getValue() + "; domain=" +
			// MyApplication.cookie.getDomain());
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"file\";filename=\"" + file.getName() + "\"" + end);
			ds.writeBytes(end);
			/* 取得文件的FileInputStream */
			FileInputStream fStream = new FileInputStream(file);
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
			fStream.close();
			ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			try {
				JSONObject json = new JSONObject(b.toString());
				boolean success = json.getBoolean("success");
				Message message = new Message();
				if (success) {
					String fileId = json.getString("fileId");
					String fileName = json.getString("fileName");
					map = new HashMap<String, String>();
					map.put("id", fileId);
					map.put("name", fileName);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

}
