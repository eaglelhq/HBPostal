package com.ksource.hbpostal.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.RegexUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.widgets.CustomPopWindow;

import java.io.IOException;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanActivity extends BaseActivity implements QRCodeView.Delegate {
    private static final String TAG = ScanActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private QRCodeView mQRCodeView;
    private TextView tv_title;
    private ImageView iv_back, iv_right;
    private boolean shouldPlayBeep;
    MediaPlayer mediaPlayer = new MediaPlayer();
    private CustomPopWindow mCustomPopWindow;
//    setVolumeControlStream(AudioManager.STREAM_MUSIC);

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_test_scan;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        iv_right.setVisibility(View.VISIBLE);
        iv_right.setImageResource(R.drawable.life_button_edit);
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioManager audioService = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            shouldPlayBeep = false;
        }

        //注册事件。当播放完毕一次后，重新指向流文件的开头，以准备下次播放。
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer player) {
                player.seekTo(0);
            }
        });
        AssetFileDescriptor file = getResources().openRawResourceFd(
                R.raw.beep);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(6.0f, 6.0f);
            mediaPlayer.prepare();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            mediaPlayer = null;
        }
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        iv_right.setOnClickListener(this);
    }

    @Override
    public void initData() {
        tv_title.setText("扫描二维码");
    }

    //震动
    private void vibrate() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
//        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
//        Gson gson = new Gson();
//        ScanResultBean scanBean = gson.fromJson(result,ScanResultBean.class);
        if (TextUtils.isEmpty(result)) {
            mQRCodeView.startSpot();
        } else {
            vibrate();
            if (RegexUtils.isURL(result)) {
                if (result.contains("https://www.baidu.com/")) {
                    String[] resArray = result.split("\\?");
                    String arg1 = "";
                    if (resArray.length > 1){
                        int index = resArray[1].indexOf("=");
                        arg1 = resArray[1].substring(index);
                    }
                    ToastUtils.showShortToast("当前链接："+result+"参数："+arg1);
                    return;
                }
                Intent intent1 = new Intent(context, BaseHtmlActivity.class);
                intent1.putExtra("title", "扫码付");
                intent1.putExtra("url", result);

                startActivity(intent1);
                return;
            }
            String[] results = result.split("#");
            String shopId = results[0];//商户ID
            String name = results[1];//收款人信息
            String sellerId = results[2];//营业员ID
            String money = "";//收款金额
            String msg = "";//收款备注
//            if (results.length >= 2) {
//                name = results[1];
//            }
            if (results.length >= 4) {
                money = results[3];
            }
            if (results.length >= 5) {
                msg = results[4];
            }
            if (TextUtils.isEmpty(shopId) || TextUtils.isEmpty(name) || TextUtils.isEmpty(sellerId)) {
                ToastUtils.showShortToast("不支持的二维码！");
                mQRCodeView.startSpot();
            } else {
                // 跳转到扫描确认页面
                Intent intent = new Intent(context, ScanResultActivity.class);
                intent.putExtra("id", shopId);
                intent.putExtra("name", name);
                intent.putExtra("sellerId", sellerId);
                intent.putExtra("money", money);
                intent.putExtra("msg", msg);
                startActivity(intent);
            }
        }
//        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
        ToastUtils.showShortToast("打开相机出错");
    }

    //显示弹出菜单
    private void showPopMenu() {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_callphone_dialog, null);
        //处理popWindow 显示内容

        handleLogic(contentView);
        //创建并显示popWindow
        mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        // 设置背景颜色变亮
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1.0f;
                        getWindow().setAttributes(lp);
                    }
                })
                .setView(contentView)
                .size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
                .create()
//                .showAsDropDown(iv_right,5,0);
                .showAtLocation(iv_right, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     *
     * @param contentView
     */
    private void handleLogic(View contentView) {
        TextView tv_photo = (TextView) contentView.findViewById(R.id.tv_call);
        TextView tv_cancel = (TextView) contentView.findViewById(R.id.tv_message);
        LinearLayout dialog_cancle = (LinearLayout) contentView.findViewById(R.id.dialog_cancle);
        tv_photo.setText("从相册中选取");
        tv_cancel.setText("取消");
        tv_photo.setTextColor(getResources().getColor(R.color.green));
        tv_cancel.setTextColor(getResources().getColor(R.color.gary));
        dialog_cancle.setVisibility(View.GONE);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
                }
//                // 设置背景颜色变暗
//                WindowManager.LayoutParams lp = getWindow()
//                        .getAttributes();
//                lp.alpha = 1.0f;
//                getWindow().setAttributes(lp);
                switch (v.getId()) {
                    case R.id.tv_call:
//                        ToastUtils.showShortToast("335422");
//                        startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
                        break;

                    case R.id.tv_message:
                        mCustomPopWindow.dissmiss();
                        break;
                }
//                Toast.makeText(context,"选择："+showContent, Toast.LENGTH_SHORT).show();
            }
        };
        tv_photo.setOnClickListener(listener);
        tv_cancel.setOnClickListener(listener);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
                showPopMenu();
                break;
//            case R.id.start_spot:
//                mQRCodeView.startSpot();
//                break;
//            case R.id.stop_spot:
//                mQRCodeView.stopSpot();
//                break;
//            case R.id.start_spot_showrect:
//                mQRCodeView.startSpotAndShowRect();
//                break;
//            case R.id.stop_spot_hiddenrect:
//                mQRCodeView.stopSpotAndHiddenRect();
//                break;
//            case R.id.show_rect:
//                mQRCodeView.showScanRect();
//                break;
//            case R.id.hidden_rect:
//                mQRCodeView.hiddenScanRect();
//                break;
//            case R.id.start_preview:
//                mQRCodeView.startCamera();
//                break;
//            case R.id.stop_preview:
//                mQRCodeView.stopCamera();
//                break;
//            case R.id.open_flashlight:
//                mQRCodeView.openFlashlight();
//                break;
//            case R.id.close_flashlight:
//                mQRCodeView.closeFlashlight();
//                break;
//            case R.id.scan_barcode:
//                mQRCodeView.changeToScanBarcodeStyle();
//                break;
//            case R.id.scan_qrcode:
//                mQRCodeView.changeToScanQRCodeStyle();
//                break;
//            case R.id.choose_qrcde_from_gallery:
//                /*
//                从相册选取二维码图片，这里为了方便演示，使用的是
//                https://github.com/bingoogolapple/BGAPhotoPicker-Android
//                这个库来从图库中选择二维码图片，这个库不是必须的，你也可以通过自己的方式从图库中选择图片
//                 */
////                startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
//                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mQRCodeView.showScanRect();

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
//            final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);
            Uri uri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            final String picturePath = actualimagecursor.getString(actual_image_column_index);
//            final String picturePath = data.getData().toString();

            /*
            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
             */
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
                }

                @Override
                protected void onPostExecute(String result) {
                    if (TextUtils.isEmpty(result)) {
                        Toast.makeText(ScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(ScanActivity.this, result, Toast.LENGTH_SHORT).show();
                        onScanQRCodeSuccess(result);
                    }
                }
            }.execute();
        }
    }


}