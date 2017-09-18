package com.ksource.hbpostal.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.HBAreaResaultBean;
import com.ksource.hbpostal.bean.HBAreaResaultBean.AreaListBean;
import com.ksource.hbpostal.bean.MenberDetailResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.AESOperator;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.yitao.dialog.CallOrSendMessageDialog;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.yitao.widget.RoundImageView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 会员信息页面
 */
public class MemberInfoActivity extends BaseActivity {

    private static final int PHOTO_REQUEST_CUT = 103;// 结果
    private static final int PHOTO_REQUEST_CAMERA = 101;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 102;// 从相册中选择
    // private static final int PHONE_REQUEST_CODE = 100;
    private static final int INFO_REQUEST_CODE = 104;
    private static final int AREA_REQUEST_CODE = 105;

    private TextView tv_title;
    private ImageView iv_back;
    private LoadDialog mLoadDialog;
    private RelativeLayout rl_user_image, rl_user_name, rl_nick_name, rl_phone,
            rl_id_number, rl_area, rl_edit_pwd, rl_manager_num,rl_edit_pay_pwd,rl_reset_pay_pwd;
    private RoundImageView iv_user_avatar;
    private TextView tv_user_name, tv_nick_name, tv_manager_name, tv_phone,
            tv_id_number, tv_area,tv_pwd;
    private EditText et_addr;
    private Button btn_submit;

    private String memberImg = "";
    private String memberName = "";
    private String phone = "";
    private String idCard = "";
    private String managerName = "";
    private String managerId = "";
    private String areaId = "";
    private String areaName = "";
    private String address = "";
    private String cityId = "";
    private String cityName = "";
    private String provinceName = "";
    private String provinceId = "";
    private String nickName = "";
    private String areaStr = "";
    private String currArea = "";

    private boolean areaChange;
    private List<AreaListBean> provinceList;
    private List<AreaListBean> cityList;
    private List<AreaListBean> areaList;

    private String[] mProvinceDatas;
    private String[] mCityDatas;
    private String[] mAreaDatas;
    private CallOrSendMessageDialog mCameraOrPic;

    // private String imagePath = null;// 图片路径

    private Bitmap bitmap;

    /* 头像名称 */
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;
    private String token;
    private boolean has_pay_pwd;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_member_info;
    }

    @Override
    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        iv_user_avatar = (RoundImageView) findViewById(R.id.iv_user_avatar);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);
        tv_manager_name = (TextView) findViewById(R.id.tv_manager_num);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_id_number = (TextView) findViewById(R.id.tv_id_number);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_pwd = (TextView) findViewById(R.id.tv_pwd);
        et_addr = (EditText) findViewById(R.id.et_addr);

        rl_user_image = (RelativeLayout) findViewById(R.id.rl_user_image);
        rl_user_name = (RelativeLayout) findViewById(R.id.rl_user_name);
        rl_nick_name = (RelativeLayout) findViewById(R.id.rl_nick_name);
        rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
        rl_id_number = (RelativeLayout) findViewById(R.id.rl_id_number);
        rl_manager_num = (RelativeLayout) findViewById(R.id.rl_manager_num);
        rl_area = (RelativeLayout) findViewById(R.id.rl_area);
        rl_edit_pwd = (RelativeLayout) findViewById(R.id.rl_edit_pwd);
        rl_edit_pay_pwd = (RelativeLayout) findViewById(R.id.rl_edit_pay_pwd);
        rl_reset_pay_pwd = (RelativeLayout) findViewById(R.id.rl_reset_pay_pwd);

        btn_submit = (Button) findViewById(R.id.btn_submit);
    }

    @Override
    public void initListener() {

        iv_back.setOnClickListener(this);
        rl_user_image.setOnClickListener(this);
        rl_user_name.setOnClickListener(this);
        rl_nick_name.setOnClickListener(this);
        rl_phone.setOnClickListener(this);
        rl_id_number.setOnClickListener(this);
        rl_manager_num.setOnClickListener(this);
        rl_area.setOnClickListener(this);
        rl_edit_pwd.setOnClickListener(this);
        rl_edit_pay_pwd.setOnClickListener(this);
        rl_reset_pay_pwd.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        et_addr.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String addrStr = et_addr.getText().toString().trim();
                if (!TextUtils.isEmpty(addrStr) && !addrStr.equals(address)) {
                    btn_submit.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void initData() {
        tv_title.setText("个人信息");
        token = sp.getString(ConstantValues.TOKEN, null);
        if (TextUtils.isEmpty(token)) {
            // finish();
            startActivity(new Intent(context, LoginActivity.class));
        }

        getData();
    }

    // 获取会员信息
    private void getData() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "数据加载中...");
        Map<String, String> params = new HashMap<>();
        String token = sp.getString(ConstantValues.TOKEN, "");
        params.put("token", token);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                MenberDetailResultBean memberResult = null;
                Gson gson = new Gson();
                try {
                    memberResult = gson.fromJson(arg0,
                            MenberDetailResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                }
                if (memberResult == null) {
                    ToastUtil.showTextToast(context, "获取会员信息失败！");
                    return;
                }
                if (memberResult.success) {
                    if (memberResult.memberInfo.HEAD_IMAGE != null) {
                        memberImg = memberResult.memberInfo.HEAD_IMAGE;
                    }
                    if (memberResult.memberInfo.NAME != null) {
                        memberName = memberResult.memberInfo.NAME;
                    }
                    if (memberResult.memberInfo.ID_CARD != null) {
                        idCard = memberResult.memberInfo.ID_CARD;
                    }
                    if (memberResult.memberInfo.CUSTOMER_MANAGER_NAME != null) {
                        managerName = memberResult.memberInfo.CUSTOMER_MANAGER_NAME;
                    }
                    if (memberResult.memberInfo.MOBILE != null) {
                        phone = memberResult.memberInfo.MOBILE;
                    }
                    if (memberResult.memberInfo.NICK_NAME != null) {
                        nickName = memberResult.memberInfo.NICK_NAME;
                    }
                    if (memberResult.memberInfo.ADDRESS != null) {
                        address = memberResult.memberInfo.ADDRESS;
                    }
                    if (memberResult.memberInfo.AREA_ID != null) {
                        areaId = memberResult.memberInfo.AREA_ID;
                    }
                    if (memberResult.memberInfo.AREA_NAME != null) {
                        areaName = memberResult.memberInfo.AREA_NAME;
                    }
                    if (memberResult.memberInfo.CITY_NAME != null) {
                        cityName = memberResult.memberInfo.CITY_NAME;
                    }
                    if (memberResult.memberInfo.CITY_ID != null) {
                        cityId = memberResult.memberInfo.CITY_ID;
                    }
                    if (memberResult.memberInfo.PROVINCE_ID != null) {
                        provinceId = memberResult.memberInfo.PROVINCE_ID;
                    }
                    if (memberResult.memberInfo.PROVINCE_NAME != null) {
                        provinceName = memberResult.memberInfo.PROVINCE_NAME;
                    }
                    has_pay_pwd = memberResult.memberInfo.PAY_PWD == 1;
                    if (has_pay_pwd){
                        tv_pwd.setText("修改支付密码");
                    }else{
                        tv_pwd.setText("设置支付密码");
                    }
                    areaStr = provinceName + cityName + areaName;
                    setText();
                } else if (memberResult.flag == 10) {
                    mApplication.login();
                    // getData();
                } else {
                    ToastUtil.showTextToast(context, memberResult.msg);
                }


            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_USERINFO_URL, params, callback);
    }

    // 填充数据
    private void setText() {
        tv_user_name.setText(memberName);
        tv_nick_name.setText(nickName);
        if (phone != null && phone.length() > 4) {
            phone = phone.substring(0, 3) + "****"
                    + phone.substring(phone.length() - 4);
        }
        tv_phone.setText(phone);
        if (!TextUtils.isEmpty(idCard) && idCard.length() > 15) {
            idCard = idCard.substring(0, 3) + "***********"
                    + idCard.substring(14);
        }
        tv_id_number.setText(idCard);
        tv_manager_name.setText(managerName);
        tv_area.setText(TextUtils.isEmpty(areaStr) ? "请选择地区" : areaStr);
        et_addr.setText(address);

        try {
            ImageLoaderUtil.loadNetPic(ConstantValues.BASE_URL + memberImg,
                    iv_user_avatar);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            case INFO_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                String content = data.getStringExtra("content");
                int type = data.getIntExtra("type", 0);
                if (getIntent().hasExtra("managerId")) {
                    managerId = getIntent().getStringExtra("managerId");
                }
                switch (type) {
                    case 1:
                        // 修改姓名
                        tv_user_name.setText(content);
                        // btn_submit.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        // 修改昵称
                        tv_nick_name.setText(content);
                        // btn_submit.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        // 修改客户经理编号
                        tv_manager_name.setText(content);
                        // btn_submit.setVisibility(View.VISIBLE);
                        break;

                    default:
                        break;
                }

                break;
            case AREA_REQUEST_CODE:

                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_user_image:
                chooseImagesByCarmeraOrPic();
                break;
            case R.id.rl_user_name:
                // if (TextUtils.isEmpty(tv_user_name.getText().toString().trim()))
                // {
                // // showEditDialog(tv_user_name, "添加用户名");
                // Intent nameIntent = new Intent(context, EditInfoActivity.class);
                // nameIntent.putExtra("itemName", "添加用户名");
                // nameIntent.putExtra("type", 1);
                // nameIntent.putExtra("hint", "请输入用户姓名");
                // nameIntent.putExtra("tishi", "*用户名为用户真实姓名！\n*添加后不可更改！");
                // startActivityForResult(nameIntent, INFO_REQUEST_CODE);
                // } else {
                // // notifyDialog("该姓名不能更改！");
                // // ToastUtil.showTextToast(context, "该姓名不能更改！");
                // new
                // SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE).setTitleText("该姓名不能更改！").show();
                // }
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("该姓名不能更改！").setContentText("该姓名以绑定银行卡的姓名为准")
                        .show();
                break;
            case R.id.rl_nick_name:
                // showEditDialog(tv_nick_name, "修改昵称");
                Intent nickIntent = new Intent(context, EditInfoActivity.class);
                nickIntent.putExtra("itemName", "编辑昵称");
                nickIntent.putExtra("type", 2);
                nickIntent.putExtra("text", nickName);
                nickIntent.putExtra("hint", "请输入昵称");
                nickIntent.putExtra("tishi", "*此昵称非登录会员名！\n*昵称长度应小于10字符！");
                startActivityForResult(nickIntent, INFO_REQUEST_CODE);
                break;
            case R.id.rl_phone:
                Intent intent = new Intent(context, EditPhoneActivity.class);
                intent.putExtra("phone",phone);
                startActivity(intent);
                break;
            case R.id.rl_id_number:
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("身份证号码不能更改！")
                        .setContentText("该身份证号码以绑定银行卡的身份证号码为准").show();
                // if (TextUtils.isEmpty(tv_id_number.getText().toString().trim()))
                // {
                // // showEditDialog(tv_id_number, "添加身份证号码");
                // Intent idIntent = new Intent(context, EditInfoActivity.class);
                // idIntent.putExtra("itemName", "添加身份证号");
                // idIntent.putExtra("type", 3);
                // startActivityForResult(idIntent, INFO_REQUEST_CODE);
                // } else {
                // // notifyDialog("该姓名不能更改！");
                // // ToastUtil.showTextToast(context, "身份证号码不能更改！");
                // }
                break;
            case R.id.rl_manager_num:
                if (TextUtils.isEmpty(tv_manager_name.getText().toString().trim())) {
                    // showEditDialog(tv_id_number, "添加身份证号码");
                    Intent idIntent = new Intent(context, EditInfoActivity.class);
                    idIntent.putExtra("itemName", "选择客户经理");
                    idIntent.putExtra("type", 3);
                    idIntent.putExtra("hint", "请输入客户经理编号");
                    idIntent.putExtra("tishi",
                            "*客户经理编号，可匹配到对应的客户经理！\n*客户经理确定后不可更改！");
                    startActivityForResult(idIntent, INFO_REQUEST_CODE);
                } else {
                    // notifyDialog("该姓名不能更改！");
                    // ToastUtil.showTextToast(context, "身份证号码不能更改！");
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("客户经理不能更改")
                            .setContentText("已绑定客户经理不能更改!").show();
                }
                break;
            case R.id.rl_area:
                // currArea = "";
                getArea("", 1);
                break;
            case R.id.rl_edit_pwd:
                Intent intent1 = new Intent(context, EditPwdActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_edit_pay_pwd:
                Intent intent2 = new Intent(context, EditPayPwdActivity.class);
                intent2.putExtra("has_pay_pwd",has_pay_pwd);
                startActivity(intent2);
                break;
            case R.id.rl_reset_pay_pwd:
                Intent intent3 = new Intent(context, MissPwdActivity.class);
                intent3.putExtra("type", 2);
                startActivity(intent3);
                break;
            case R.id.btn_submit:
                submitInfo();
                break;

            default:
                break;
        }

    }

    // 修改个人信息
    private void submitInfo() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "提交个人信息...");
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        if (areaChange) {
            params.put("provinceId", provinceId);
            params.put("provinceName", provinceName);
            params.put("cityId", cityId);
            params.put("cityName", cityName);
            params.put("areaId", areaId);
            params.put("areaName", areaName);
        }
        String addressStr = et_addr.getText().toString().trim();
        if (!addressStr.equals(address)) {
            params.put("address", addressStr);
        }
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                BaseResultBean baseResult = null;
                Gson gson = new Gson();
                try {
                    baseResult = gson.fromJson(arg0,
                            BaseResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (baseResult == null) {
                    ToastUtil.showTextToast(context, "修改个人信息失败！");
                    return;
                }
                if (baseResult.success) {
                    SweetAlertDialog dialog = new SweetAlertDialog(
                            context, SweetAlertDialog.SUCCESS_TYPE);
                    dialog.setTitleText("修改地址成功！")
                            .setConfirmClickListener(
                                    new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(
                                                SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            // finish();
                                            btn_submit
                                                    .setVisibility(View.GONE);
                                        }
                                    }).show();
                } else if (baseResult.flag == 10) {
                    mApplication.login();
                    // submitData();
                } else {
                    ToastUtil.showTextToast(context, baseResult.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "修改个人信息失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.UPDATE_USERINFO_URL, params, callback);
    }

    // 获取地区
    private void getArea(final String upId, final int type) {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "数据加载中...");
        Map<String, String> params = new HashMap<String, String>();
        params.put("pId", upId);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                Gson gson = new Gson();
                HBAreaResaultBean areaResult = null;
                try {
                    areaResult = gson.fromJson(arg0,
                            HBAreaResaultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (areaResult == null) {
                    ToastUtil.showTextToast(context, "获取地区失败！");
                    return;
                }
                if (areaResult.success) {
                    switch (type) {
                        case 1:
                            provinceList = areaResult.araList;
                            if (areaResult.araList != null
                                    && areaResult.araList.size() > 0) {
                                mProvinceDatas = new String[provinceList
                                        .size()];
                                for (int i = 0; i < provinceList.size(); i++) {
                                    mProvinceDatas[i] = provinceList.get(i).NAME;
                                }
                                chioceItem("一级区域", mProvinceDatas, 1);
                            }

                            break;
                        case 2:
                            cityList = areaResult.araList;
                            if (areaResult.araList != null
                                    && areaResult.araList.size() > 0) {
                                mCityDatas = new String[cityList.size()];
                                for (int i = 0; i < cityList.size(); i++) {
                                    mCityDatas[i] = cityList.get(i).NAME;
                                }
                                chioceItem("二级区域", mCityDatas, 2);
                            }

                            break;
                        case 3:
                            areaList = areaResult.araList;
                            if (areaResult.araList != null
                                    && areaResult.araList.size() > 0) {
                                mAreaDatas = new String[areaList.size()];
                                for (int i = 0; i < areaList.size(); i++) {
                                    mAreaDatas[i] = areaList.get(i).NAME;
                                }
                                chioceItem("三级区域", mAreaDatas, 3);
                            }

                            break;

                        default:
                            break;
                    }
                } else {
                    ToastUtil.showTextToast(context, areaResult.msg);
                }


            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取地区失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_HB_AREA_URL, params, callback);
    }

    // 选择地区
    private void chioceItem(String title, final String[] itemDatas,
                            final int type) {

        Builder builder = new Builder(this);
        builder.setTitle("请选" + title);
        builder.setItems(itemDatas, new DialogInterface.OnClickListener() {

            /*
             * 第一个参数代表对话框对象 第二个参数是点击对象的索引
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type == 1) {
                    currArea = "";
                    provinceId = "";
                    cityId = "";
                    areaId = "";
                }
                currArea += itemDatas[which];
                tv_area.setText(currArea);
                switch (type) {
                    case 1:
                        provinceName = itemDatas[which];
                        provinceId = provinceList.get(which).ID;
                        getArea(provinceId, 2);
                        break;
                    case 2:
                        cityName = itemDatas[which];
                        cityId = cityList.get(which).ID;
                        getArea(cityId, 3);
                        break;
                    case 3:
                        areaName = itemDatas[which];
                        areaId = areaList.get(which).ID;
                        if (currArea.equals(areaStr)) {
                            btn_submit.setVisibility(View.GONE);
                            areaChange = false;
                        } else {
                            btn_submit.setVisibility(View.VISIBLE);
                            areaChange = true;
                        }
                        break;

                    default:
                        break;
                }
            }
        });
        builder.show();

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

            String token = sp.getString(ConstantValues.TOKEN, "");
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
}
