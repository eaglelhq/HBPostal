package com.ksource.hbpostal.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ksource.hbpostal.MyApplication;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.config.ConstantValues;
import com.yitao.dialog.CommonProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * app升级弹出页
 */
public class UpDateActivity extends Activity implements View.OnClickListener {

    private TextView tv_desc, btn_ok, tv_tishi;
    private String describes;
    private String version;
    private ImageView iv_close;
    private RelativeLayout rl_upload;

    private Context context;

    private String downloadUrl;
    private MyApplication mApplication;

    private CommonProgressDialog mDialog;
    private int dataCount = 0;// 记录累加的值
    private boolean isCancel; // 用来判断是否点击了取消

    private Handler mHandler = new Handler() {
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
        }

        ;
    };
    private AlertDialog downloadDialog;
    private boolean isUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        // this.savedInstanceState = savedInstanceState;
        context = this;
        mApplication = MyApplication.getInstance();
        mApplication.addActivity(this);
        initView();
        initListener();
        initData();
    }


    public void initView() {
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_tishi = (TextView) findViewById(R.id.tv_tishi);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        rl_upload = (RelativeLayout) findViewById(R.id.rl_upload);

    }

    public void initListener() {
        btn_ok.setOnClickListener(this);
        iv_close.setOnClickListener(this);
    }

    public void initData() {
        downloadUrl = getIntent().getStringExtra("url");
        describes = getIntent().getStringExtra("describes");
        version = getIntent().getStringExtra("version");
        isUpdate = getIntent().getBooleanExtra("isUpdate",false);
        tv_desc.setText(describes);
        tv_tishi.setText(version);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                rl_upload.setVisibility(View.GONE);
                showDialog();
                // 开启服务
//			Intent intent=new Intent(this,DownLoadService.class);
//	        intent.putExtra("download_url",downloadUrl);
//	        startService(intent);
//	        finish();
                break;
            case R.id.iv_close:
                if (isUpdate){
                    mApplication.finishAllActivity();
                }else {
                    finish();
                }
                break;

            default:
                break;
        }
    }

    public void showDialog() {

        mDialog = new CommonProgressDialog(this);

        mDialog.setMessage("正在下载...");
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setMax(100 * 1024 * 1024);
        mDialog.setIndeterminate(true);
        mDialog.setOnOkAndCancelListener(new CommonProgressDialog.OnOkAndCancelListener() {
            @Override
            public void onCancel(View v) {
                mDialog.dismiss();
                isCancel = true;
                finish();
//				rl_upload.setVisibility(View.VISIBLE);
            }
        });
        mDialog.setCancelable(false);
        mDialog.show();
        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(downloadUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
//				progressDialog.setMax(length);
                mDialog.setMax(length);
                InputStream is = conn.getInputStream();

                File file = new File(ConstantValues.DOWNLOADFILEPATH);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[10 * 1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
//		    	    progress =(int)(((float)count / length) * 100);
//					progressDialog.setProgress(count);
                    mDialog.setProgress(count);
                    //更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        //下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);

                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!isCancel);//点击取消就停止下载.
                if (!isCancel) {
                    // 如果没有点击取消,正常走完,重新赋值为0
                    dataCount = 0;
                }
                // 当停止以后,更新标记
//				mHandler.sendEmptyMessage(DOWN_OVER);
                isCancel = false;
                mDialog.cancel();
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                mDialog.cancel();
            }

        }
    };

    private ProgressDialog progressDialog;
    private static final String saveFileName = ConstantValues.DOWNLOADFILEPATH + "Update.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private boolean interceptFlag = false;
    private Thread downLoadThread;

    /**
     * 下载apk
     */

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.addCategory("android.intent.category.DEFAULT");
//        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        android.os.Process.killProcess(android.os.Process.myPid());
        context.startActivity(i);
        progressDialog.dismiss();
        finish();
//        Intent intent = nw Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
//                "application/vnd.android.package-archive");
//        mContext.startActivityForResult(intent, 0);// 如果用户取消安装的话,
        // 会返回结果,回调方法onActivityResult
    }

    @Override
    public void onBackPressed() {
//		super.onBackPressed();
    }

}
