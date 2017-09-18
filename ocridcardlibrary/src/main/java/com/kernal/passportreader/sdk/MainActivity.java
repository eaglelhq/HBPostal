package com.kernal.passportreader.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.kernal.lisence.DeviceFP;
import com.kernal.passport.sdk.utils.ActivityRecogUtils;
import com.kernal.passport.sdk.utils.PermissionActivity;
import com.kernal.passport.sdk.utils.AppManager;
import com.kernal.passport.sdk.utils.CheckPermission;
import com.kernal.passport.sdk.utils.Devcode;
import com.kernal.passport.sdk.utils.SharedPreferencesHelper;
import com.kernal.passportreader.sdk.R;

import android.Manifest;
import android.hardware.usb.UsbInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import kernal.idcard.android.AuthParameterMessage;
import kernal.idcard.android.AuthService;
import kernal.idcard.android.RecogParameterMessage;
import kernal.idcard.android.RecogService;
import kernal.idcard.android.ResultMessage;

public class MainActivity extends Activity implements OnClickListener {
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private int srcWidth, srcHeight;
    private Button btn_chooserIdCardType, btn_takePicture, btn_exit,
            btn_importRecog, btn_ActivationProgram;
    private boolean isQuit = false;
    private Timer timer = new Timer();
    private String[][] type2 = {{"机读码", "3000"}, {"护照", "13"},
            {"二代身份证", "2"}, {"港澳通行证", "9"}, {"大陆居民往来台湾通行证", "11"},
            {"签证", "12"}, {"新版港澳通行证", "22"}, {"中国驾照", "5"},
            {"中国行驶证", "6"}, {"香港身份证", "1001"}, {"回乡证(正面)", "14"},
            {"回乡证(背面)", "15"}, {"澳门身份证", "1005"}, {"新版澳门身份证", "1012"},
            {"台胞证", "10"}, {"新版台胞证(正面)", "25"}, {"新版台胞证(背面)", "26"},
            {"台湾身份证(正面)", "1031"}, {"台湾身份证(背面)", "1032"},{"中国军官证", "7"},
            {"全民健康保险卡", "1030"}, {"马来西亚身份证", "2001"}, {"新加坡身份证", "2004"},
            {"新西兰驾照", "2003"}, {"加利福尼亚驾照", "2002"}, {"印度尼西亚身份证", "2010"}, {"泰国身份证", "2011"}};
    private int nMainID = 0;
    public static int DIALOG_ID = -1;
    private String[] type;
    public RecogService.recogBinder recogBinder;
    private String recogResultString = "";
    private String selectPath = "";
    private int nMainId = 2;
    private String[] recogTypes = {"机读码(2*44)", "机读码(2*36)", "机读码(3*30)"};
    private String[] IDCardTypes = {"身份证（正面）", "身份证（背面）"};
    private AuthService.authBinder authBinder;
    private int ReturnAuthority = -1;
    private String sn = "";
    private AlertDialog dialog;
    private EditText editText;
    private String devcode = Devcode.devcode;// 项目授权开发码
    public ServiceConnection authConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            authBinder = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            authBinder = (AuthService.authBinder) service;
            try {

                AuthParameterMessage apm = new AuthParameterMessage();
                // apm.datefile = "assets"; // PATH+"/IDCard/wtdate.lsc";//预留
                apm.devcode = devcode;// 预留
                apm.sn = sn;
                ReturnAuthority = authBinder.getIDCardAuth(apm);
                if (ReturnAuthority != 0) {
                    Toast.makeText(getApplicationContext(),
                            "ReturnAuthority:" + ReturnAuthority,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.activation_success),
                            Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.license_verification_failed),
                        Toast.LENGTH_LONG).show();

            } finally {
                if (authBinder != null) {
                    unbindService(authConn);
                }
            }
        }
    };
    public static final String[] PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE, // 读取权限
            Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE, Manifest.permission.INTERNET,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        srcWidth = displayMetrics.widthPixels;
        srcHeight = displayMetrics.heightPixels;
        setContentView(getResources().getIdentifier("activity_main", "layout",
                getPackageName()));
        findView();
        // 需要释放掉相机界面
        AppManager.getAppManager().finishAllActivity();

    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        type2 = intiList();
        type = new String[type2.length];

        for (int i = 0; i < type2.length; i++) {
            type[i] = type2[i][0];
        }
    }

    /**
     * @Title: findView @Description: TODO(这里用一句话描述这个方法的作用) @param 设定文件 @return
     * void 返回类型 @throws
     */
    private void findView() {
        // TODO Auto-generated method stub

        btn_chooserIdCardType = (Button) findViewById(getResources().getIdentifier("btn_chooserIdCardType", "id",
                getPackageName()));
        btn_takePicture = (Button) findViewById(getResources().getIdentifier("btn_takePicture", "id",
                getPackageName()));
        btn_exit = (Button) findViewById(getResources().getIdentifier("btn_exit", "id",
                getPackageName()));
        btn_importRecog = (Button) findViewById(getResources().getIdentifier("btn_importRecog", "id",
                getPackageName()));
        btn_ActivationProgram = (Button) findViewById(getResources().getIdentifier("btn_ActivationProgram", "id",
                getPackageName()));
        btn_ActivationProgram.setOnClickListener(this);
        btn_chooserIdCardType.setOnClickListener(this);
        btn_takePicture.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_importRecog.setOnClickListener(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                srcWidth / 2, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) (srcHeight * 0.25);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btn_ActivationProgram.setLayoutParams(params);
        params = new RelativeLayout.LayoutParams(srcWidth / 2,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.btn_ActivationProgram);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btn_chooserIdCardType.setLayoutParams(params);
        params = new RelativeLayout.LayoutParams(srcWidth / 2,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.btn_chooserIdCardType);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btn_takePicture.setLayoutParams(params);
        params = new RelativeLayout.LayoutParams(srcWidth / 2,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.btn_takePicture);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btn_importRecog.setLayoutParams(params);
        params = new RelativeLayout.LayoutParams(srcWidth / 2,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.btn_importRecog);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btn_exit.setLayoutParams(params);

    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent;
        if (getResources()
                .getIdentifier("btn_ActivationProgram", "id", this.getPackageName()) == v.getId()) {
            if (Build.VERSION.SDK_INT >= 23) {
                CheckPermission checkPermission = new CheckPermission(this);
                if (checkPermission.permissionSet(PERMISSION)) {
                    PermissionActivity.startActivityForResult(this, 0,
                            SharedPreferencesHelper.getInt(
                                    getApplicationContext(), "nMainId", 2),
                            devcode, 0, 0, PERMISSION);
                } else {
                    activationProgramOpera();
                }
            } else {
                activationProgramOpera();
            }

        } else if (getResources()
                .getIdentifier("btn_chooserIdCardType", "id", this.getPackageName()) == v.getId()) {

                dialog();


        } else if (getResources()
                .getIdentifier("btn_takePicture", "id", this.getPackageName()) == v.getId()) {
/**
 * 由于在相机界面释放相机等资源会耗费很多时间， 为了优化用户体验，需要在调用相机的那个界面的oncreate()方法中
 * 调用AppManager.getAppManager().finishAllActivity();
 * 如果调用和识别的显示界面是同一个界面只需调用一次即可， 如果是不同界面，需要在显示界面的oncreate()方法中
 * 调用AppManager.getAppManager().finishAllActivity();即可，
 * 否则会造成相机资源的内存溢出。
 */
            intent = new Intent(MainActivity.this, CameraActivity.class);
            if (Build.VERSION.SDK_INT >= 23) {
                CheckPermission checkPermission = new CheckPermission(this);
                if (checkPermission.permissionSet(PERMISSION)) {
                    PermissionActivity.startActivityForResult(this, 0,
                            SharedPreferencesHelper.getInt(
                                    getApplicationContext(), "nMainId", 2),
                            devcode, 0, 0, PERMISSION);
                } else {
                    intent.putExtra("nMainId", SharedPreferencesHelper.getInt(
                            getApplicationContext(), "nMainId", 2));
                    intent.putExtra("devcode", devcode);
                    intent.putExtra("flag", 0);
                    MainActivity.this.finish();
                    startActivity(intent);
                }
            } else {
                intent.putExtra("nMainId", SharedPreferencesHelper.getInt(
                        getApplicationContext(), "nMainId", 2));
                intent.putExtra("devcode", devcode);
                intent.putExtra("flag", 0);
                MainActivity.this.finish();
                startActivity(intent);
            }
        } else if (getResources()
                .getIdentifier("btn_importRecog", "id", this.getPackageName()) == v.getId()) {
// 相册
            intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            try {
                startActivityForResult(Intent.createChooser(intent, "请选择一张图片"),
                        9);
            } catch (Exception e) {
                Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
            }
            // 文件管理器
            // intent = new Intent(Intent.ACTION_GET_CONTENT);
            // intent.setType("image/*");
            // intent.addCategory(Intent.CATEGORY_OPENABLE);
            // try {
            // startActivityForResult(intent, 9);
            // } catch (android.content.ActivityNotFoundException ex) {
            // Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
            // }
        } else if (getResources()
                .getIdentifier("btn_exit", "id", this.getPackageName()) == v.getId()) {
            MainActivity.this.finish();

        }


    }

    /**
     * @Title: activationProgramOpera @Description: TODO(这里用一句话描述这个方法的作用) @param
     * 设定文件 @return void 返回类型 @throws
     */
    private void activationProgramOpera() {
        // TODO Auto-generated method stub
        DIALOG_ID = 1;
        View view = getLayoutInflater().inflate(R.layout.serialdialog, null);
        editText = (EditText) view.findViewById(R.id.serialdialogEdittext);
        dialog = new Builder(MainActivity.this)
                .setView(view)
                .setPositiveButton(getString(R.string.online_activation),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm.isActive()) {
                                    imm.toggleSoftInput(
                                            InputMethodManager.SHOW_IMPLICIT,
                                            InputMethodManager.HIDE_NOT_ALWAYS);
                                }
                                String editsString = editText.getText()
                                        .toString().toUpperCase();
                                if (editsString != null) {
                                    sn = editsString;
                                }
                                if (isNetworkConnected(MainActivity.this)) {
                                    if (isWifiConnected(MainActivity.this)
                                            || isMobileConnected(MainActivity.this)) {
                                        startAuthService();
                                        dialog.dismiss();
                                    } else if (!isWifiConnected(MainActivity.this)
                                            && !isMobileConnected(MainActivity.this)) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                getString(R.string.network_unused),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            getString(R.string.please_connect_network),
                                            Toast.LENGTH_SHORT).show();
                                }

                            }

                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();

                            }

                        }).create();
        dialog.show();
    }

    /**
     * @Title: dialog @Description: TODO(这里用一句话描述这个方法的作用) @param 设定文件 @return
     * void 返回类型 @throws
     */
    private void dialog() {
        // TODO Auto-generated method stub

        int checkedItem = -1;
        /*
         * if
		 * (getResources().getConfiguration().locale.getLanguage().equals("zh")
		 * && getResources().getConfiguration().locale.getCountry()
		 * .equals("CN")) {
		 */
        for (int i = 0; i < type2.length; i++) {

            if (Integer.valueOf(type2[i][1]) == SharedPreferencesHelper.getInt(
                    getApplicationContext(), "nMainId", 2)) {
                checkedItem = i;
                break;
            }
        }

        // }
        // 能够使类型列表的选中效果不消失
        DIALOG_ID = 1;
        Builder dialog = createAlertDialog(
                getString(R.string.chooseRecogType), null);
        dialog.setPositiveButton(getString(R.string.confirm), dialogListener);
        dialog.setNegativeButton(getString(R.string.cancel), dialogListener);
        dialog.setSingleChoiceItems(type, checkedItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

						/*
						 * if (getResources().getConfiguration().locale
						 * .getLanguage().equals("zh") &&
						 * getResources().getConfiguration().locale
						 * .getCountry().equals("CN")) {
						 */
                        for (int i = 0; i < type2.length; i++) {
                            if (which == i) {
                                nMainID = Integer.valueOf(type2[i][1]);
                                break;
                            }
                        }

                        // }
                    }
                });
        dialog.show();
    }

    /**
     * @Title: createAlertDialog @Description: TODO(这里用一句话描述这个方法的作用) @param @param
     * string @param @param object @param @return 设定文件 @return Builder
     * 返回类型 @throws
     */
    private Builder createAlertDialog(String title, String message) {
        // TODO Auto-generated method stub
        Builder dialog = new Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.create();
        return dialog;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (isQuit == false) {
                isQuit = true;
                Toast.makeText(getBaseContext(), R.string.back_confirm, 2000)
                        .show();
                TimerTask task = null;
                task = new TimerTask() {
                    @Override
                    public void run() {
                        isQuit = false;
                    }
                };
                timer.schedule(task, 2000);
            } else {
                finish();
            }
        }
        return true;
    }

    public DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (DIALOG_ID) {

                case 1:
                    if (dialog.BUTTON_POSITIVE == which) {
                        if (nMainID == 0) {
                            if (SharedPreferencesHelper.getInt(
                                    getApplicationContext(), "nMainId", 2) != 2) {
                                nMainID = SharedPreferencesHelper.getInt(
                                        getApplicationContext(), "nMainId", 2);
                            } else {
                                nMainID = 2;
                            }
                        }

                        SharedPreferencesHelper.putInt(getApplicationContext(),
                                "nMainId", nMainID);
                        dialog.dismiss();
                    } else if (dialog.BUTTON_NEGATIVE == which) {
                        dialog.dismiss();
                    }
                    break;

            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 9 && resultCode == Activity.RESULT_OK) {
            if (Build.VERSION.SDK_INT >= 23) {
                CheckPermission checkPermission = new CheckPermission(this);
                if (checkPermission.permissionSet(PERMISSION)) {
                    PermissionActivity.startActivityForResult(this, 0,
                            SharedPreferencesHelper.getInt(
                                    getApplicationContext(), "nMainId", 2),
                            devcode, 0, 0, PERMISSION);
                } else {
                    Uri uri = data.getData();
                    selectPath = getPath(getApplicationContext(), uri);
                    RecogService.nMainID = SharedPreferencesHelper.getInt(
                            getApplicationContext(), "nMainId", 2);
			RecogService.isRecogByPath = true;
			Intent recogIntent = new Intent(MainActivity.this,
					RecogService.class);
			bindService(recogIntent, recogConn, Service.BIND_AUTO_CREATE);
                    //ActivityRecogUtils.getRecogResult(MainActivity.this, selectPath, RecogService.nMainID, true);
                }
            } else {
                Uri uri = data.getData();
                selectPath = getPath(getApplicationContext(), uri);
                RecogService.nMainID = SharedPreferencesHelper.getInt(
                        getApplicationContext(), "nMainId", 2);
			RecogService.isRecogByPath = true;
			Intent recogIntent = new Intent(MainActivity.this,
					RecogService.class);
			bindService(recogIntent, recogConn, Service.BIND_AUTO_CREATE);
                //ActivityRecogUtils.getRecogResult(MainActivity.this, selectPath, RecogService.nMainID, true);

            }

        } else if (requestCode == 8 && resultCode == Activity.RESULT_OK) {
            //Activtiy识别返回的识别结果
            int ReturnAuthority = data.getIntExtra("ReturnAuthority", -100000);// 取激活状态
            ResultMessage resultMessage = new ResultMessage();
            resultMessage.ReturnAuthority = data.getIntExtra("ReturnAuthority", -100000);// 取激活状态
            resultMessage.ReturnInitIDCard = data
                    .getIntExtra("ReturnInitIDCard", -100000);// 取初始化返回值
            resultMessage.ReturnLoadImageToMemory = data.getIntExtra(
                    "ReturnLoadImageToMemory", -100000);// 取读图像的返回值
            resultMessage.ReturnRecogIDCard = data.getIntExtra("ReturnRecogIDCard",
                    -100000);// 取识别的返回值
            resultMessage.GetFieldName = (String[]) data
                    .getSerializableExtra("GetFieldName");
            resultMessage.GetRecogResult = (String[]) data
                    .getSerializableExtra("GetRecogResult");
            ActivityRecogUtils.goShowResultActivity(MainActivity.this, resultMessage,0,selectPath,selectPath.substring(0,selectPath.indexOf(".jpg"))+ "Cut.jpg");
        }
    }

    // 识别验证
    public ServiceConnection recogConn = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            recogBinder = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {

            recogBinder = (RecogService.recogBinder) service;

            RecogParameterMessage rpm = new RecogParameterMessage();
            rpm.nTypeLoadImageToMemory = 0;
            rpm.nMainID = SharedPreferencesHelper.getInt(
                    getApplicationContext(), "nMainId", 2);
            rpm.nSubID = null;
            rpm.GetSubID = true;
            rpm.GetVersionInfo = true;
            rpm.logo = "";
            rpm.userdata = "";
            rpm.sn = "";
            rpm.authfile = "";
            rpm.isCut = true;
            rpm.triggertype = 0;
            rpm.devcode = devcode;
            // nProcessType：0-取消所有操作，1－裁切，
            // 2－旋转，3－裁切+旋转，4－倾斜校正，5－裁切+倾斜校正，6－旋转+倾斜校正，7－裁切+旋转+倾斜校正
            rpm.nProcessType = 7;
            rpm.nSetType = 1;// nSetType: 0－取消操作，1－设置操作
            rpm.lpFileName = selectPath; // rpm.lpFileName当为空时，会执行自动识别函数
            // rpm.lpHeadFileName = selectPath;//保存证件头像
            rpm.isSaveCut = true;// 保存裁切图片 false=不保存 true=保存
            if (SharedPreferencesHelper.getInt(getApplicationContext(),
                    "nMainId", 2) == 2) {
                rpm.isAutoClassify = true;
                rpm.isOnlyClassIDCard = true;
            } else if (SharedPreferencesHelper.getInt(getApplicationContext(),
                    "nMainId", 2) == 3000) {
                rpm.nMainID = 1034;
            }
            // end
            try {

                ResultMessage resultMessage;
                resultMessage = recogBinder.getRecogResult(rpm);
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

                    Intent intent = new Intent(MainActivity.this,
                            ShowResultActivity.class);
                    intent.putExtra("recogResult", recogResultString);
                    intent.putExtra("fullPagePath", selectPath);
                    intent.putExtra("importRecog", true);
                    intent.putExtra("cutPagePath", selectPath.substring(0,selectPath.indexOf(".jpg"))+ "Cut.jpg");
                    MainActivity.this.finish();
                    startActivity(intent);
                } else {
                    String string = "";
                    if (resultMessage.ReturnAuthority == -100000) {
                        string = getString(R.string.exception)
                                + resultMessage.ReturnAuthority;
                    } else if (resultMessage.ReturnAuthority != 0) {
                        string = getString(R.string.exception1)
                                + resultMessage.ReturnAuthority;
                    } else if (resultMessage.ReturnInitIDCard != 0) {
                        string = getString(R.string.exception2)
                                + resultMessage.ReturnInitIDCard;
                    } else if (resultMessage.ReturnLoadImageToMemory != 0) {
                        if (resultMessage.ReturnLoadImageToMemory == 3) {
                            string = getString(R.string.exception3)
                                    + resultMessage.ReturnLoadImageToMemory;
                        } else if (resultMessage.ReturnLoadImageToMemory == 1) {
                            string = getString(R.string.exception4)
                                    + resultMessage.ReturnLoadImageToMemory;
                        } else {
                            string = getString(R.string.exception5)
                                    + resultMessage.ReturnLoadImageToMemory;
                        }
                    } else if (resultMessage.ReturnRecogIDCard <= 0) {
                        if (resultMessage.ReturnRecogIDCard == -6) {
                            string = getString(R.string.exception9);
                        } else {
                            string = getString(R.string.exception6)
                                    + resultMessage.ReturnRecogIDCard;
                        }
                    }
                    Intent intent = new Intent(MainActivity.this,
                            ShowResultActivity.class);
                    intent.putExtra("exception", string);
                    intent.putExtra("importRecog", true);
                    MainActivity.this.finish();
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.recognized_failed),
                        Toast.LENGTH_SHORT).show();

            } finally {
                if (recogBinder != null) {
                    unbindService(recogConn);
                }
            }

        }
    };

    private void startAuthService() {
        RecogService.isOnlyReadSDAuthmodeLSC = false;
        Intent authIntent = new Intent(MainActivity.this, AuthService.class);
        bindService(authIntent, authConn, Service.BIND_AUTO_CREATE);
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /***
     * @param @param context @param @param uri @param @return 设定文件 @return
     *               String 返回类型 @throws
     * @Title: getPath
     * @Description: TODO(这里用一句话描述这个方法的作用) 获取图片路径
     */

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) { // 忽略大小写
            // String[] projection = { "_data" };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null,
                        null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private String[][] intiList() {
        String[][] list = null;
        if (getResources().getConfiguration().locale.getLanguage().equals("zh")
                && getResources().getConfiguration().locale.getCountry()
                .equals("CN")) {
            String[][] temp = {{"机读码", "3000"}, {"护照", "13"},
                    {"二代身份证", "2"}, {"港澳通行证", "9"},
                    {"大陆居民往来台湾通行证", "11"}, {"签证", "12"},
                    {"新版港澳通行证", "22"}, {"中国驾照", "5"},{ "中国驾照副页", "28" }, {"中国行驶证", "6"},
                    {"香港身份证", "1001"}, {"回乡证(正面)", "14"},
                    {"回乡证(背面)", "15"}, {"澳门身份证", "1005"},
                    {"新版澳门身份证", "1012"}, {"台胞证", "10"},
                    {"新版台胞证(正面)", "25"}, {"新版台胞证(背面)", "26"},
                    {"台湾身份证(正面)", "1031"}, {"台湾身份证(背面)", "1032"},{"中国军官证", "7"},
                    {"全民健康保险卡", "1030"}, {"马来西亚身份证", "2001"},
                    {"新加坡身份证", "2004"}, {"新西兰驾照", "2003"},
                    {"加利福尼亚驾照", "2002"}, {"印度尼西亚身份证", "2010"}, {"泰国身份证", "2011"},{ "北京社保卡", "1021" }};
            list = temp;
        } else if (getResources().getConfiguration().locale.getLanguage()
                .equals("zh")
                && getResources().getConfiguration().locale.getCountry()
                .equals("TW")) {
            String[][] temp = {{"機讀碼", "3000"}, {"護照", "13"},
                    {"二代身份證", "2"}, {"港澳通行證", "9"},
                    {"大陸居民往來台灣通行證", "11"}, {"簽證", "12"},
                    {"新版港澳通行證", "22"}, {"中國駕照", "5"},{ "中國駕照副頁", "28" }, {"中國行駛證", "6"},
                    {"香港身份證", "1001"}, {"回鄉證(正面)", "14"},
                    {"回鄉證(背面)", "15"}, {"澳門身份證", "1005"},
                    {"新版澳門身份證", "1012"}, {"台胞證", "10"},
                    {"台灣身份證(正面)", "1031"}, {"台灣身份證(背面)", "1032"},
                    {"新版台胞证(正面)", "25"}, {"新版台胞证(背面)", "26"},{"中國軍官証", "7"},
                    {"全民健康保險卡", "1030"}, {"馬來西亞身份證", "2001"},
                    {"新加坡身份證", "2004"}, {"新西蘭駕照", "2003"},
                    {"加利福尼亞駕照", "2002"}, {"印度尼西亞身份證", "2010"}, {"泰國身份證", "2011"},{ "北京社保卡", "1021" }};
            list = temp;
        } else {
            String[][] temp = {{"Machine readable zone", "3000"},
                    {"Passport", "13"}, {"Chinese ID card", "2"},
                    {"Exit-Entry Permit to HK/Macau", "9"},
                    {"Taiwan pass", "11"}, {"Visa", "12"},
                    {"e-EEP to HK/Macau", "22"},
                    {"Chinese Driving license", "5"},
                    { "Chinese Driving license(second)", "28" },
                    {"Chinese vehicle license", "6"},
                    {"HK ID card", "1001"},
                    {"Home return permit (Obverse)", "14"},
                    {"Home return permit (Reverse)", "15"},
                    {"Macau ID card", "1005"},
                    {"New Macau ID card", "1012"},
                    {"To the Mainland Travel Permit", "10"},
                    {"Taiwan ID card (Obverse)", "1031"},
                    {"Taiwan ID card (Reverse)", "1032"},
                    {"To the New Mainland Travel Permit(Obverse)", "25"},
                    {"To the New Mainland Travel Permit(Reverse)", "26"},{"Chinese certificate of officers", "7"},
                    {"National health care card", "1030"},
                    {"MyKad", "2001"}, {"Singapore ID card", "2004"},
                    {"New Zealand Driving license", "2003"},
                    {"California driving license", "2002"}, {"Indonesia Identity Card", "2010"}, {"Thailand's Identity Card", "2011"} ,
                    {"Beijing social security card","1021"}};
            list = temp;
        }
        return list;
    }
}
