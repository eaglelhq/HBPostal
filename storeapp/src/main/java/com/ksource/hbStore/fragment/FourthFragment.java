package com.ksource.hbStore.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.PhoneUtils;
import com.google.gson.Gson;
import com.ksource.hbStore.R;
import com.ksource.hbStore.activity.EditPwdActivity;
import com.ksource.hbStore.activity.LoginActivity;
import com.ksource.hbStore.activity.SetCardActivity;
import com.ksource.hbStore.activity.UpDateActivity;
import com.ksource.hbStore.bean.CheckResultBean;
import com.ksource.hbStore.bean.UserInfoBean;
import com.ksource.hbStore.config.ConstantValues;
import com.ksource.hbStore.util.AESOperator;
import com.ksource.hbStore.util.DataUtil;
import com.ksource.hbStore.util.UtilTool;
import com.yitao.dialog.CallOrSendMessageDialog;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

public class FourthFragment extends BaseFragment implements OnClickListener {
    private static final int PHOTO_REQUEST_CUT = 103;// 结果
    private static final int PHOTO_REQUEST_CAMERA = 101;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 102;// 从相册中选择
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_title;
    private ImageView iv_back;
    private ImageView iv_user_avatar;
    private TextView tv_shop_name, tv_sell_name, tv_sell_num, tv_sell_phone, tv_sell_addr,
            tv_user_name,tv_sell_card;
    private Button btn_logout;
    private LinearLayout ll_phone, ll_setting, ll_pwd, ll_vision, ll_card;
    public LoadDialog mLoadDialog;
    private String phone;
    private String token;
    private String name, card;
    private CallOrSendMessageDialog mCameraOrPic;

    private File tempFile;
    private Bitmap bitmap;
    private boolean isUpdate;

    @Override
    public View initView() {
        return View.inflate(context, R.layout.fragment_tab4, null);
    }

    @Override
    public void initData() {
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("个人中心");
        iv_back = (ImageView) view.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        iv_user_avatar = (ImageView) view.findViewById(R.id.iv_user_avatar);
        tv_shop_name = (TextView) view.findViewById(R.id.tv_shop_name);
        tv_sell_name = (TextView) view.findViewById(R.id.tv_sell_name);
        tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
        tv_sell_phone = (TextView) view.findViewById(R.id.tv_sell_phone);
        tv_sell_addr = (TextView) view.findViewById(R.id.tv_sell_addr);
        tv_sell_card = (TextView) view.findViewById(R.id.tv_sell_card);
        tv_sell_num = (TextView) view.findViewById(R.id.tv_sell_num);
        btn_logout = (Button) view.findViewById(R.id.btn_logout);
        ll_phone = (LinearLayout) view.findViewById(R.id.ll_phone);
        ll_card = (LinearLayout) view.findViewById(R.id.ll_card);
        ll_vision = (LinearLayout) view.findViewById(R.id.ll_vision);
        ll_pwd = (LinearLayout) view.findViewById(R.id.ll_pwd);
        ll_setting = (LinearLayout) view.findViewById(R.id.ll_setting);
        ll_setting.setVisibility(View.GONE);
        ll_phone.setOnClickListener(this);
        ll_card.setOnClickListener(this);
        ll_vision.setOnClickListener(this);
        ll_pwd.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        iv_user_avatar.setOnClickListener(this);
        token = spUtils.getString(ConstantValues.TOKEN);

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            // SwipeRefreshLayout接管其包裹的ListView下拉事件。
            // 每一次对ListView的下拉动作，将触发SwipeRefreshLayout的onRefresh()。
            @Override
            public void onRefresh() {
                getSellInfo();
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getSellInfo();
            }
        });
    }


    //获取商家信息
    private void getSellInfo() {
        swipeRefreshLayout.setRefreshing(true);
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                swipeRefreshLayout.setRefreshing(false);
//                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                UserInfoBean resultBean = null;
                try {
                    resultBean = gson.fromJson(arg0, UserInfoBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean == null) {
                    ToastUtil.showTextToast(context, "获取商家信息失败！");
                    return;
                }
                if (resultBean.success) {
                    tv_shop_name.setText(resultBean.seller.SHOP_NAME);
                    name = resultBean.seller.SELLER_NAME;
                    tv_sell_name.setText("店主： " + name);
                    tv_user_name.setText("姓名： " + resultBean.seller.zsxm);
                    tv_sell_phone.setText(resultBean.seller.MOBILE);
                    tv_sell_addr.setText(resultBean.seller.PROVINCE_NAME + resultBean.seller.CITY_NAME + resultBean.seller.AREA_NAME + resultBean.seller.ADDRESS);
                    card = resultBean.seller.ACCOUNT_NUM;
                    isUpdate = resultBean.seller.isUpdate == 1;
                    tv_sell_card.setText(UtilTool.showBankCard(card));
                    tv_sell_num.setText(resultBean.seller.SHOP_CODE);
                } else {
//                    if (resultBean.flag == 10) {
                        mApplication.login();
//                    }
                    ToastUtil.showTextToast(context, resultBean.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                swipeRefreshLayout.setRefreshing(false);
//                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取商家信息失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.SELLER_INFO, params, callback);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_phone:
                if (!TextUtils.isEmpty(phone))
                    PhoneUtils.call(phone);
                break;
            case R.id.iv_user_avatar:
//                chooseImagesByCarmeraOrPic();
                break;
            case R.id.ll_card:
                if(isUpdate){
                    Intent intent = new Intent(context, SetCardActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("card", card);
                    startActivity(intent);
                }
                break;
            case R.id.ll_pwd:
                startActivity(new Intent(context, EditPwdActivity.class));
                break;
            case R.id.ll_setting:
                //TODO 设置
//                startActivity(new Intent(context,SettingActivity.class));
                break;
            case R.id.ll_vision:
                checkVision();
                break;
            case R.id.btn_logout:
                final AlertDialog dialog = new AlertDialog.Builder(context).create();
                dialog.setTitle("退出登录");
                dialog.setMessage("确认要退出登录吗？");
                dialog.setButton(dialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                });
                dialog.setButton(dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gary));
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

                break;

            default:
                break;
        }
    }

    // 注销
    private void logout() {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
//                new SPUtils(ConstantValues.SP_NAME).clear();
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
//                new SPUtils(ConstantValues.SP_NAME).clear();
            }
        };
        DataUtil.doGetData(mLoadDialog, context, ConstantValues.LOGOUT_URL, callback);
        spUtils.putString(ConstantValues.KEY_PASSWORD, "");
        spUtils.putString(ConstantValues.TOKEN, "");
        getActivity().finish();
        startActivity(new Intent(context, LoginActivity.class));
    }

    //检查版本
    private void checkVision() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "检查更新");
        Map<String, String> params = new HashMap<String, String>();
//        params.put("token", token);
        params.put("versionName", getVersionCode(context));
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                CheckResultBean resultBean = null;
                try {
                    resultBean = gson.fromJson(arg0, CheckResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resultBean != null) {
                    if (resultBean.isUpdate == 0) {
                        Intent intent = new Intent(context,
                                UpDateActivity.class);
                        intent.putExtra("url",
                                ConstantValues.BASE_URL
                                        + resultBean.URL);
                        intent.putExtra("describes",
                                resultBean.DESCRIBES);
                        intent.putExtra("version",
                                resultBean.VERSION_NAME);
                        startActivity(intent);
//                        UpdateManager updateManager = new UpdateManager(context, ConstantValues.BASE_URL + resultBean.URL, resultBean.DESCRIBES, resultBean.VERSION_NAME);
//                        updateManager.checkUpdateInfo();
                    } else {
                        new SweetAlertDialog(context,
                                SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(resultBean.msg)
                                .setContentText(getVersionCode(context))
                                .setConfirmText("确定")
                                .show();
                    }
                } else {
                    ToastUtil.showTextToast(context, "检查版本失败！");
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "检查版本失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog, context, ConstantValues.CKECK_UPDATA_URL, params, callback);
    }

    /**
     * 获取apk的版本号
     *
     * @param context
     * @return
     */
    public String getVersionCode(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST_CAMERA:
                tempFile = new File(ConstantValues.CACHE_PATH, PHOTO_FILE_NAME);
                if (tempFile != null)
                    crop(Uri.fromFile(tempFile));

                break;
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    crop(uri);
                }

                break;
            case PHOTO_REQUEST_CUT:
                try {
                    bitmap = data.getParcelableExtra("data");
                    iv_user_avatar.setImageBitmap(bitmap);
                    upload();
                    boolean delete = tempFile.delete();
                    System.out.println("delete = " + delete);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            // case PHONE_REQUEST_CODE:
            // if (data == null) {
            // return;
            // }
            // String phone = data.getStringExtra("phone");
            // tv_phone.setText(phone);
            // btn_submit.setVisibility(View.VISIBLE);
            // break;

            default:
                break;
        }
    }

    /**
     * 选择图片
     */
    private void chooseImagesByCarmeraOrPic() {

        if (mCameraOrPic == null) {
            mCameraOrPic = DialogUtil.getInstance().showCameraOrPicDialog(
                    context);
            mCameraOrPic.setItemTextForPic();
        } else {
            mCameraOrPic.setItemTextForPic();
            mCameraOrPic.show();
        }
        mCameraOrPic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    // 拍照
                    case R.id.dialog_call:
                        camera();
                        mCameraOrPic.dismiss();
                        break;
                    // 相册选择照片
                    case R.id.dialog_message:
                        gallery();
                        mCameraOrPic.dismiss();
                        break;
                    case R.id.dialog_cancle:
                        mCameraOrPic.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /**
     * 从相机获取
     */
    public void camera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                ConstantValues.CACHE_PATH, PHOTO_FILE_NAME)));

        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
    }

    /**
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 上传图片
     */
    public void upload() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
            byte[] buffer = out.toByteArray();

            String photo = Base64.encodeToString(buffer, Base64.DEFAULT);

            String token = spUtils.getString(ConstantValues.TOKEN, "");
            Map<String, String> paramsV = new HashMap<String, String>();
            paramsV.put("token", token);
            paramsV.put("imgBytes", photo);
            // String param = "{'token':'"+token +"','imgBytes':'"+photo+"'}";
            String param = AESOperator.getInstance().encrypt(
                    new Gson().toJson(paramsV));

            OkHttpUtils.post().addParams("params", param)
                    .url(ConstantValues.UPDATE_IMG_URL).build()
                    .execute(new StringCallback() {

                        @Override
                        public void onResponse(String result, int arg1) {
                            JSONObject jsonObj;
                            try {
                                jsonObj = new JSONObject(result);
                                boolean success = (boolean) jsonObj
                                        .get("success");
                                String msg = (String) jsonObj.get("msg");
                                ToastUtil.showTextToast(context, msg);
//                                if (success) {
//                                    new SweetAlertDialog(context,
//                                            SweetAlertDialog.SUCCESS_TYPE)
//                                            .setTitleText(msg).show();
//                                } else {
//                                    new SweetAlertDialog(context,
//                                            SweetAlertDialog.ERROR_TYPE)
//                                            .setTitleText(msg).show();
//                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Call arg0, Exception arg1, int arg2) {
                            new SweetAlertDialog(context,
                                    SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("上传头像失败！")
                                    .setContentText(arg1.toString()).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}