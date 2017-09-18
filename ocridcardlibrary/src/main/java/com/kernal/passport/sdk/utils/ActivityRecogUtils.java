package com.kernal.passport.sdk.utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.kernal.passportreader.sdk.MainActivity;
import com.kernal.passportreader.sdk.ShowResultActivity;
import com.kernal.passportreader.sdk.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import kernal.idcard.android.RecogService;
import kernal.idcard.android.ResultMessage;

/**
 * Created by huangzhen on 2017/2/6.
 */

public class ActivityRecogUtils {
    /**
     *调用Activity识别，具有动画效果
     * @param context 上下文环境
     * @param selectPath 所要识别的图片的路径
     * @param nMainID 所需要识别的证件类型
     * @param cutBoolean 是否需要进行裁切
     */
    public static void getRecogResult(Context context, String selectPath, int nMainID, boolean cutBoolean) {
        try {
            RecogService.isRecogByPath = true;
            String logopath = "";
            // String logopath = getSDPath() + "/photo_logo.png";
            Intent intent = new Intent("kernal.idcard");
            Bundle bundle = new Bundle();
            int nSubID[] = null;// {0x0001};
            bundle.putBoolean("isGetRecogFieldPos", false);// 是否获取识别字段在原图的位置信息，默认为假，不获取
            // 必须在核心裁好的图上裁切才行
            bundle.putString("cls", "checkauto.com.IdcardRunner");
            bundle.putInt("nTypeInitIDCard", 0); // 保留，传0即可
            bundle.putString("lpFileName", selectPath);// 指定的图像路径
            bundle.putString("cutSavePath", "");// 裁切图片的存储路径
            bundle.putInt("nTypeLoadImageToMemory", 0);// 0不确定是哪种图像，1可见光图，2红外光图，4紫外光图
            // if (nMainID == 1000) {
            // nSubID[0] = 3;
            // }
            bundle.putInt("nMainID", nMainID); // 证件的主类型。6是行驶证，2是二代证，这里只可以传一种证件主类型。每种证件都有一个唯一的ID号，可取值见证件主类型说明
            bundle.putIntArray("nSubID", nSubID); // 保存要识别的证件的子ID，每个证件下面包含的子类型见证件子类型说明。nSubID[0]=null，表示设置主类型为nMainID的所有证件。
            // bundle.putBoolean("GetSubID", true);
            // //GetSubID得到识别图像的子类型id
            // bundle.putString("lpHeadFileName",
            // "/mnt/sdcard/head.jpg");//保存路径名，后缀只能为jpg、bmp、tif
            bundle.putBoolean("GetVersionInfo", true); // 获取开发包的版本信息
            bundle.putString("sn", "");
            // bundle.putString("datefile",
            // "assets");//Environment.getExternalStorageDirectory().toString()+"/wtdate.lsc"
            bundle.putString("devcode", Devcode.devcode);
            // bundle.putBoolean("isCheckDevType", true); // 强制验证设备型号开关
            // bundle.putString("versionfile",
            // "assets");//Environment.getExternalStorageDirectory().toString()+"/wtversion.lsc"
            // bundle.putString("sn", "XS4XAYRWEFRY248YY4LHYY178");
            // //序列号激活方式,XS4XAYRWEFRY248YY4LHYY178已使用
            // bundle.putString("server",
            // "http://192.168.0.36:8080");//http://192.168.0.36:8888
            // bundle.putString("authfile", ""); // 文件激活方式 //
            // /mnt/sdcard/AndroidWT/357816040594713_zj.txt
            bundle.putString("logo", logopath); // logo路径，logo显示在识别等待页面右上角
            bundle.putInt("nProcessType", 7);//2－旋转，3－裁切+旋转，4－倾斜校正，5－裁切+倾斜校正，6－旋转+倾斜校正，7－裁切+旋转+倾斜校正
            bundle.putInt("nSetType", 1);// nSetType: 0－取消操作，1－设置操作
            bundle.putBoolean("isCut", cutBoolean); // 如不设置此项默认自动裁切
            bundle.putBoolean("isSaveCut", true);// 是否保存裁切图片
            bundle.putString("returntype", "withvalue");// 返回值传递方式withvalue带参数的传值方式（新传值方式）
            intent.putExtras(bundle);
            ((Activity) context).startActivityForResult(intent, 8);
        } catch (Exception e) {
            Toast.makeText(
                    context,
                    context.getString(R.string.noFoundProgram)
                            + "wintone.idcard", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取相应的识别结果并且跳转到结果显示界面
     * @param context 上下文环境
     * @param resultMessage 识别返回的参数
     * @param VehicleLicenseflag 首界面是行驶证首界面还是普通首界面
     */
    public static void goShowResultActivity(Context context,ResultMessage resultMessage,int VehicleLicenseflag,String fullPicturePath,String cutPicturePath) {
        try {
            String recogResultString = "";
            if (resultMessage.ReturnAuthority == 0
                    && resultMessage.ReturnInitIDCard == 0
                    && resultMessage.ReturnLoadImageToMemory == 0
                    && resultMessage.ReturnRecogIDCard > 0) {
                String iDResultString = "";
                String[] GetFieldName = resultMessage.GetFieldName;
                String[] GetRecogResult = resultMessage.GetRecogResult;

                for (int i = 1; i < GetFieldName.length; i++) {
                    if (GetRecogResult[i] != null) {
                        if (!recogResultString.equals(""))
                            recogResultString = recogResultString
                                    + GetFieldName[i] + ":"
                                    + GetRecogResult[i] + ",";
                        else {
                            recogResultString = GetFieldName[i] + ":"
                                    + GetRecogResult[i] + ",";
                        }
                    }
                }
                Intent intent = new Intent(context,
                        ShowResultActivity.class);
                intent.putExtra("recogResult", recogResultString);
                intent.putExtra("VehicleLicenseflag", VehicleLicenseflag);
                intent.putExtra("fullPagePath", fullPicturePath);
                intent.putExtra("cutPagePath", cutPicturePath);
                intent.putExtra("importRecog", true);
                ((Activity)context).finish();
                ((Activity)context).startActivity(intent);
            } else {
                String string = "";
                if (resultMessage.ReturnAuthority == -100000) {
                    string = context.getString(R.string.exception)
                            + resultMessage.ReturnAuthority;
                } else if (resultMessage.ReturnAuthority != 0) {
                    string = context.getString(R.string.exception1)
                            + resultMessage.ReturnAuthority;
                } else if (resultMessage.ReturnInitIDCard != 0) {
                    string = context.getString(R.string.exception2)
                            + resultMessage.ReturnInitIDCard;
                } else if (resultMessage.ReturnLoadImageToMemory != 0) {
                    if (resultMessage.ReturnLoadImageToMemory == 3) {
                        string = context.getString(R.string.exception3)
                                + resultMessage.ReturnLoadImageToMemory;
                    } else if (resultMessage.ReturnLoadImageToMemory == 1) {
                        string = context.getString(R.string.exception4)
                                + resultMessage.ReturnLoadImageToMemory;
                    } else {
                        string = context.getString(R.string.exception5)
                                + resultMessage.ReturnLoadImageToMemory;
                    }
                } else if (resultMessage.ReturnRecogIDCard <= 0) {
                    if (resultMessage.ReturnRecogIDCard == -6) {
                        string = context.getString(R.string.exception9);
                    } else {
                        string = context.getString(R.string.exception6)
                                + resultMessage.ReturnRecogIDCard;
                    }
                }
                Intent intent = new Intent(context,
                        ShowResultActivity.class);
                intent.putExtra("exception", string);
                intent.putExtra("VehicleLicenseflag", VehicleLicenseflag);
                ((Activity)context).finish();
                ((Activity)context).startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,
                    context.getString(R.string.recognized_failed),
                    Toast.LENGTH_SHORT).show();

        }

    }
}
