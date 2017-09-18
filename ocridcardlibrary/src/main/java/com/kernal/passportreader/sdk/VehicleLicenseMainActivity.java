package com.kernal.passportreader.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import kernal.idcard.android.AuthParameterMessage;
import kernal.idcard.android.AuthService;
import kernal.idcard.android.RecogParameterMessage;
import kernal.idcard.android.RecogService;
import kernal.idcard.android.ResultMessage;

import com.kernal.passport.sdk.utils.ActivityRecogUtils;
import com.kernal.passport.sdk.utils.AppManager;
import com.kernal.passport.sdk.utils.CheckPermission;
import com.kernal.passport.sdk.utils.Devcode;
import com.kernal.passport.sdk.utils.PermissionActivity;
import com.kernal.passport.sdk.utils.SharedPreferencesHelper;

import android.Manifest;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VehicleLicenseMainActivity extends Activity implements
		OnClickListener {
	private DisplayMetrics displayMetrics = new DisplayMetrics();
	private int srcWidth, srcHeight;
	private Button btn_chooserIdCardType, btn_takePicture, btn_exit,
			btn_importRecog, btn_ActivationProgram;
	private boolean isQuit = false;
	private Timer timer = new Timer();
	public static int DIALOG_ID = -1;
	private String[] type;
	public RecogService.recogBinder recogBinder;
	private String recogResultString = "";
	private String selectPath = "";
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
	static final String[] PERMISSION = new String[] {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
			Manifest.permission.READ_EXTERNAL_STORAGE, // 读取权限
			Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.VIBRATE, Manifest.permission.INTERNET };

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
		SharedPreferencesHelper.putInt(getApplicationContext(), "nMainId", 6);
	}

	/**
	 * @Title: findView @Description: TODO(这里用一句话描述这个方法的作用) @param 设定文件 @return
	 *         void 返回类型 @throws
	 */
	private void findView() {
		// TODO Auto-generated method stub
		btn_chooserIdCardType = (Button) findViewById(getResources()
				.getIdentifier("btn_chooserIdCardType", "id", getPackageName()));
		btn_takePicture = (Button) findViewById(getResources().getIdentifier(
				"btn_takePicture", "id", getPackageName()));
		btn_exit = (Button) findViewById(getResources().getIdentifier(
				"btn_exit", "id", getPackageName()));
		btn_importRecog = (Button) findViewById(getResources().getIdentifier(
				"btn_importRecog", "id", getPackageName()));
		btn_ActivationProgram = (Button) this
				.findViewById(getResources().getIdentifier(
						"btn_ActivationProgram", "id", getPackageName()));
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
		btn_chooserIdCardType.setVisibility(View.GONE);
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
		if (getResources().getIdentifier("btn_ActivationProgram", "id",
				this.getPackageName()) == v.getId()) {
			activationProgramOpera();
		} else if (getResources().getIdentifier("btn_takePicture", "id",
				this.getPackageName()) == v.getId()) {
			/**
			 * 由于在相机界面释放相机等资源会耗费很多时间， 为了优化用户体验，需要在调用相机的那个界面的oncreate()方法中
			 * 调用AppManager.getAppManager().finishAllActivity();
			 * 如果调用和识别的显示界面是同一个界面只需调用一次即可， 如果是不同界面，需要在显示界面的oncreate()方法中
			 * 调用AppManager.getAppManager().finishAllActivity();即可，
			 * 否则会造成相机资源的内存溢出。
			 */
			if (Build.VERSION.SDK_INT >= 23) {
				CheckPermission checkPermission = new CheckPermission(this);
				if (checkPermission.permissionSet(PERMISSION)) {
					PermissionActivity.startActivityForResult(this, 0,
							SharedPreferencesHelper.getInt(
									getApplicationContext(), "nMainId", 2),
							devcode, 0, 1, PERMISSION);
				} else {
					intent = new Intent(VehicleLicenseMainActivity.this,
							CameraActivity.class);
					intent.putExtra("nMainId", SharedPreferencesHelper.getInt(
							getApplicationContext(), "nMainId", 6));
					intent.putExtra("devcode", devcode);
					intent.putExtra("flag", 0);
					intent.putExtra("VehicleLicenseflag", 1);
					VehicleLicenseMainActivity.this.finish();
					startActivity(intent);
				}
			} else {
				intent = new Intent(VehicleLicenseMainActivity.this,
						CameraActivity.class);
				intent.putExtra("nMainId", SharedPreferencesHelper.getInt(
						getApplicationContext(), "nMainId", 6));
				intent.putExtra("devcode", devcode);
				intent.putExtra("flag", 0);
				intent.putExtra("VehicleLicenseflag", 1);
				VehicleLicenseMainActivity.this.finish();
				startActivity(intent);
			}
		} else if (getResources().getIdentifier("btn_exit", "id",
				this.getPackageName()) == v.getId()) {
			VehicleLicenseMainActivity.this.finish();

		} else if (getResources().getIdentifier("btn_importRecog", "id",
				this.getPackageName()) == v.getId()) {
			// 导入识别按钮
			// 相册
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

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
		}

	}

	/**
	 * @Title: activationProgramOpera @Description: TODO(这里用一句话描述这个方法的作用) @param
	 *         设定文件 @return void 返回类型 @throws
	 */
	private void activationProgramOpera() {
		// TODO Auto-generated method stub
		DIALOG_ID = 1;
		View view = getLayoutInflater().inflate(R.layout.serialdialog, null);
		editText = (EditText) view.findViewById(R.id.serialdialogEdittext);
		dialog = new AlertDialog.Builder(VehicleLicenseMainActivity.this)
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
								if (isNetworkConnected(VehicleLicenseMainActivity.this)) {
									if (isWifiConnected(VehicleLicenseMainActivity.this)
											|| isMobileConnected(VehicleLicenseMainActivity.this)) {
										startAuthService();
										dialog.dismiss();
									} else if (!isWifiConnected(VehicleLicenseMainActivity.this)
											&& !isMobileConnected(VehicleLicenseMainActivity.this)) {
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 9 && resultCode == Activity.RESULT_OK) {
			if (Build.VERSION.SDK_INT >= 23) {
				CheckPermission checkPermission = new CheckPermission(this);
				if (checkPermission.permissionSet(PERMISSION)) {
					PermissionActivity.startActivityForResult(this, 0,
							SharedPreferencesHelper.getInt(
									getApplicationContext(), "nMainId", 6),
							devcode, 0, 0, PERMISSION);
				} else {
					Uri uri = data.getData();
					selectPath = getPath(getApplicationContext(), uri);

					RecogService.nMainID = SharedPreferencesHelper.getInt(
							getApplicationContext(), "nMainId", 6);
					RecogService.isRecogByPath = true;
					Intent recogIntent = new Intent(VehicleLicenseMainActivity.this,
							RecogService.class);
					bindService(recogIntent, recogConn, Service.BIND_AUTO_CREATE);
					//ActivityRecogUtils.getRecogResult(MainActivity.this, selectPath, RecogService.nMainID, true);
				}
			} else {
				Uri uri = data.getData();
				selectPath = getPath(getApplicationContext(), uri);
				RecogService.nMainID = SharedPreferencesHelper.getInt(
						getApplicationContext(), "nMainId", 6);
				RecogService.isRecogByPath = true;
				Intent recogIntent = new Intent(VehicleLicenseMainActivity.this,
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
			ActivityRecogUtils.goShowResultActivity(VehicleLicenseMainActivity.this, resultMessage,1,selectPath,selectPath.substring(0,selectPath.indexOf(".jpg"))+ "Cut.jpg");
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
			rpm.nMainID = 6;
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
					Intent intent = new Intent(VehicleLicenseMainActivity.this,
							ShowResultActivity.class);
					intent.putExtra("fullPagePath", selectPath);
					intent.putExtra("cutPagePath", selectPath.substring(0,selectPath.indexOf(".jpg"))+ "Cut.jpg");
					intent.putExtra("recogResult", recogResultString);
					intent.putExtra("VehicleLicenseflag", 1);
					intent.putExtra("importRecog", true);
					VehicleLicenseMainActivity.this.finish();
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
					Intent intent = new Intent(VehicleLicenseMainActivity.this,
							ShowResultActivity.class);
					intent.putExtra("exception", string);
					intent.putExtra("VehicleLicenseflag", 1);
					intent.putExtra("importRecog", true);
					VehicleLicenseMainActivity.this.finish();
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
		Intent authIntent = new Intent(VehicleLicenseMainActivity.this,
				AuthService.class);
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
	 *
	 * @Title: getPath
	 * @Description: TODO(这里用一句话描述这个方法的作用) 获取图片路径
	 * @param @param context @param @param uri @param @return 设定文件 @return
	 *        String 返回类型 @throws
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
}
