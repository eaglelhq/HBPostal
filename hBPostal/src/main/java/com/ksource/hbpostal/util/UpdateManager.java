package com.ksource.hbpostal.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.widget.ProgressBar;

import com.ksource.hbpostal.config.ConstantValues;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateManager {

	private Context mContext;
	
	//提示语
	private String describes = "有最新的软件包哦，亲快下载吧~";
	//版本名
	private String version_name;
	//返回的安装包url
	private String apkUrl;
	
	private Dialog noticeDialog;
	
	private Dialog downloadDialog;
	 /* 下载包安装路径 */
//    private static final String savePath = "/sdcard/updatedemo/";
    
    private static final String saveFileName = ConstantValues.DOWNLOADFILEPATH + "Update.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;

    
    private static final int DOWN_UPDATE = 1;
    
    private static final int DOWN_OVER = 2;
    
    private int progress;
    
    private Thread downLoadThread;
    
    private boolean interceptFlag = false;
    
    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case DOWN_UPDATE:
//				progressDialog.setProgress(progress);
				break;
			case DOWN_OVER:
				
				installApk();
				break;
			default:
				break;
			}
    	};
    };

    
	public UpdateManager(Context context,String apkUrl, String describes, String version_name) {
		this.mContext = context;
		this.apkUrl = apkUrl;
		this.describes = describes;
		this.version_name = version_name;
	}
	
	//外部接口让主Activity调用
	public void checkUpdateInfo(){
		showNoticeDialog();
	}
	public void downloadInfo(){
		showDownloadDialog();
	}

	
	private void showNoticeDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("软件版本更新");
		builder.setMessage("发现新版本："+version_name+"\n"+describes);
		builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
//		noticeDialog.setCanceledOnTouchOutside(false);
		noticeDialog.setCancelable(false);
		noticeDialog.show();
//		SweetAlertDialog dialog = new SweetAlertDialog(mContext);
//		dialog.setTitleText("软件版本更新")
//		.setContentText("发现新版本："+version_name+"\n"+describes)
//		.setConfirmText("下载")
//		.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//
//			@Override
//			public void onClick(SweetAlertDialog sweetAlertDialog) {
//				sweetAlertDialog.dismiss();
//				showDownloadDialog();
//			}
//		})
//		.setCancelText("暂不更新")
//		.showCancelButton(true)
//		.show();
	}
	
	private void showDownloadDialog(){
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
        progressDialog.setTitle("正在下载");  
        progressDialog.setMessage("请稍候...");  
        progressDialog.setProgress(0);
		progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.dismiss();
            }
        });
        progressDialog.show(); 
//		AlertDialog.Builder builder = new Builder(mContext);
//		builder.setTitle("软件版本更新");
//		
//		final LayoutInflater inflater = LayoutInflater.from(mContext);
//		View v = inflater.inflate(R.layout.progress, null);
//		mProgress = (ProgressBar)v.findViewById(R.id.progress);
//		
//		builder.setView(v);
//		builder.setNegativeButton("取消", new OnClickListener() {	
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				interceptFlag = true;
//			}
//		});
//		downloadDialog = builder.create();
//		downloadDialog.show();
		downloadApk();
	}
	
	private Runnable mdownApkRunnable = new Runnable() {	
		@Override
		public void run() {
			try {
				URL url = new URL(apkUrl);
			
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				progressDialog.setMax(length);
				InputStream is = conn.getInputStream();
				
				File file = new File(ConstantValues.DOWNLOADFILEPATH);
				if(!file.exists()){
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);
				
				int count = 0;
				byte buf[] = new byte[10240];
				
				do{   		   		
		    		int numread = is.read(buf);
		    		count += numread;
		    	    progress =(int)(((float)count / length) * 100);
		    		progressDialog.setProgress(count/1024);
					progressDialog.setMax(length/1024);
		    	    //更新进度
		    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
		    		if(numread <= 0){	
		    			//下载完成通知安装
		    			mHandler.sendEmptyMessage(DOWN_OVER);
		    			
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);//点击取消就停止下载.
				
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}
			
		}
	};

	private ProgressDialog progressDialog;
	
	 /**
     * 下载apk
     */
	
	private void downloadApk(){
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	 /**
     * 安装apk
     */
	private void installApk(){
		File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }    
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setAction("android.intent.action.VIEW");  
        i.addCategory("android.intent.category.DEFAULT");  
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
//        android.os.Process.killProcess(android.os.Process.myPid());
		progressDialog.dismiss();
        mContext.startActivity(i);
//        Intent intent = new Intent(Intent.ACTION_VIEW);  
//        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),  
//                "application/vnd.android.package-archive");  
//        mContext.startActivityForResult(intent, 0);// 如果用户取消安装的话,  
        // 会返回结果,回调方法onActivityResult  
	}
}
