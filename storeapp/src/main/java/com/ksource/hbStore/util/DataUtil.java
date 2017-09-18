package com.ksource.hbStore.util;

import android.content.Context;

import com.blankj.utilcode.utils.NetworkUtils;
import com.google.gson.Gson;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * OKHttp请求数据
 *
 * @author Administrator
 */
public class DataUtil {

    /**
     * OKHttp 请求物流信息
     *
     * @param num
     * @param callback
     * @return
     */
    public static void getDeliver(String num, StringCallback callback) {
        // Gson gson = new Gson();
        // String paramStr = gson.toJson(paramValues);
        // try {
        // paramStr = AESOperator.getInstance().encrypt(paramStr);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // 联网请求数据
        OkHttpUtils
                .get()
                .url("http://ali-deliver.showapi.com/showapi_expInfo")
                .addHeader("Authorization",
                        "APPCODE d77777ee976c4c05bcabf074508d46c2")
                .addParams("com", "auto").addParams("nu", num).build()
                .execute(callback);
    }

    //private static
    /**
     * OKHttp Post请求数据
     *
     * @param url
     * @param params
     * @param callback
     * @return
     */
    public static void doPostAESData(LoadDialog mLoadDialog, Context context, String url, Map<String, String> params,
                                     StringCallback callback) {
        if (!NetworkUtils.isConnected()) {
            if (mLoadDialog != null) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
            }
            SweetAlertDialog dialog = new SweetAlertDialog(context,
                    SweetAlertDialog.WARNING_TYPE);
//            if (dialog == null) {
//                dialog = new SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE);
//            }
            dialog.setTitleText("网络未连接！").setContentText("去设置网络")
                    .setCancelText("取消")
                    .setConfirmText("去设置")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            NetworkUtils.openWirelessSettings();
                        }
                    }).show();
            return;
        }
        Gson gson = new Gson();
        String paramValues = gson.toJson(params);
        try {
            paramValues = AESOperator.getInstance().encrypt(paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 联网请求数据
        OkHttpUtils.post().url(url).addParams("params", paramValues).build()
                .execute(callback);
    }

    /**
     * OKHttp Post请求数据
     *
     * @param url
     * @param callback
     * @return
     */
    public static void doGetData(LoadDialog mLoadDialog, Context context, String url,
                                 StringCallback callback) {
        if (!NetworkUtils.isConnected()) {
            if (mLoadDialog != null) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
            }
            SweetAlertDialog dialog = new SweetAlertDialog(context,
                    SweetAlertDialog.WARNING_TYPE);
            dialog.setTitleText("网络未连接！").setContentText("去设置网络")
                    .setCancelText("取消")
                    .setConfirmText("去设置")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            NetworkUtils.openWirelessSettings();
                        }
                    }).show();
            return;
        }
        // 联网请求数据
        //.addParams("params", paramValues)
        OkHttpUtils.get().url(url).build()
                .execute(callback);
    }

}
