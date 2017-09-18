package com.kernal.passport.sdk.utils;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

//将android本地的图片和相应的文件存储到远程PC机的文件夹下。
public class WriteToPCTask extends AsyncTask<String, String, String> {
    private static final String BOUNDARY = "----WebKitFormBoundaryT1HoybnYeFOGFlBR";
    private int flag;
    private int ReturnHttpImage = 0;
    private int ReturnHttpFile = 0;
    private int ReturnHttpImage1 = 0;
    private Context context;
    //public static String httpPath = "http://123.56.102.63:80/emailServer/servlet/UploadFile?fileName=";
    public static String httpPath ="http://123.56.102.63:80/emailServer/servlet/UploadFileTxt?fileName=";
    public WriteToPCTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);

            if (ReturnHttpFile == 1) { // 自定义的toast
                Toast toast = new Toast(context);
                View layout = LayoutInflater.from(context).inflate(
                        context.getResources().getIdentifier("toast_layout",
                                "layout", context.getPackageName()), null);
                TextView text = (TextView) layout
                        .findViewById(context.getResources().getIdentifier("showToastInformation",
                                "id", context.getPackageName()));
                text.setText(context.getString(context.getResources().getIdentifier("upload_success",
                        "string", context.getPackageName())));
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setView(layout);
                toast.show();
                // 自定义的toast end
            }  else if ( ReturnHttpFile == 0) {
                // 自定义的toast
                Toast toast = new Toast(context);
                View layout = LayoutInflater.from(context).inflate(
                        context.getResources().getIdentifier("toast_layout",
                                "layout", context.getPackageName()), null);
                TextView text = (TextView) layout
                        .findViewById(context.getResources().getIdentifier("showToastInformation",
                                "id", context.getPackageName()));
                text.setText(context.getString(context.getResources().getIdentifier("upload_File_failed",
                        "string", context.getPackageName())));
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setView(layout);
                toast.show();

                // 自定义的toast end
            }

    }

    // params[0] 本地图片的路径，params[1] 识别信息的路径,
    // "http://192.168.0.64:8080/strurts2fileupload/uploadAction"
    @Override
    protected String doInBackground(String... params) {
            String url = SharedPreferencesHelper
                        .getString(context, "URL",
                                WriteToPCTask.httpPath);
            ReturnHttpFile = upLoadFileByHttpProtocol(
            httpPath , params[0].substring(
                            params[0].lastIndexOf("/") + 1, params[0].length()),
                    params[0],params[1]);

        return "";

    }
    private static String generateFileName(String fileName) {
        DateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        String formatDate = format.format(new Date());
        return fileName + formatDate;
    }
    public int upLoadFileByHttpProtocol(String path, String fileName,
                                        String filePath,String txtcontent) {
        int returnMessage = 0;
        try {

            URL url = new URL(path + fileName+"&txtcontent="+txtcontent);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            // connection.addRequestProperty("FileName", fileName);
            connection.setRequestProperty("content-type", "text/html");
            BufferedOutputStream out = new BufferedOutputStream(
                    connection.getOutputStream());

            // 读取文件上传到服务器
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int numReadByte = 0;
            while ((numReadByte = fileInputStream.read(bytes, 0, 1024)) > 0) {
                out.write(bytes, 0, numReadByte);
            }

            out.flush();
            fileInputStream.close();
            // 读取URLConnection的响应
            DataInputStream in = new DataInputStream(
                    connection.getInputStream());
            returnMessage = 1;
        } catch (Exception e) {
            e.printStackTrace();
            returnMessage = 0;
            return returnMessage;
        }
        return returnMessage;
    }
}
