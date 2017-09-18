package com.ksource.hbpostal.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.InventResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ShareSDKManager;
import com.ksource.hbpostal.widgets.CustomPopWindow;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;

/**
 * 邀请好友页面
 */
public class InviteActivity extends BaseActivity {

    private TextView tv_title, tv_right;
    private ImageView iv_back;
    private ImageView iv_ercode;
//    private TextView tv_qq, tv_space, tv_wechat, tv_firend, tv_blog;
    private HashMap<String, String> shareMap;
    private LoadDialog mLoadDialog;
    private TextView btn_invite, btn_share;

    private CustomPopWindow mListPopWindow;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_invite;
    }

    @Override
    public void initView() {
        ShareSDK.initSDK(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.btn_right);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_ercode = (ImageView) findViewById(R.id.iv_ercode);
//        tv_qq = (TextView) findViewById(R.id.tv_qq);
//        tv_space = (TextView) findViewById(R.id.tv_space);
//        tv_wechat = (TextView) findViewById(R.id.tv_wechat);
//        tv_firend = (TextView) findViewById(R.id.tv_firend);
//        tv_blog = (TextView) findViewById(R.id.tv_blog);
        btn_invite = (TextView) findViewById(R.id.btn_invite);
        btn_share = (TextView) findViewById(R.id.btn_share);
    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
//        tv_qq.setOnClickListener(this);
//        tv_space.setOnClickListener(this);
//        tv_wechat.setOnClickListener(this);
//        tv_firend.setOnClickListener(this);
//        tv_blog.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        btn_invite.setOnClickListener(this);
        iv_ercode.setOnClickListener(this);
    }

    @Override
    public void initData() {
        shareMap = new HashMap<>();
        getUrl();
        tv_title.setText("邀请好友");
        tv_right.setText("邀请历史");
        tv_right.setVisibility(View.VISIBLE);

    }

    private void getUrl() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
                "数据加载中...");
        Map<String, String> params = new HashMap<>();
        String token = sp.getString(ConstantValues.TOKEN, "");
        params.put("token", token);
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                Gson gson = new Gson();
                InventResultBean result = null;
                try {
                    result = gson.fromJson(arg0, InventResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result == null) {
                    ToastUtil.showTextToast(context, "获取分享链接失败！");
                    return;
                }
                if (result.success) {
                    shareMap.put("title", "邮支付平台");
                    shareMap.put("content", "欢迎注册！邮支付平台");
                    shareMap.put("file_url", result.invitedUrl);
                    shareMap.put("share_image", ConstantValues.BASE_URL + "/mobile/images/life_icon_logo.png");
                } else {
                    ToastUtil.showTextToast(context, result.msg);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取分享链接失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.CREAT_INVENT_URL, params, callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_ercode:
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                View erImage = getLayoutInflater().inflate(R.layout.dialog_ercode,null);
                TextView tv_msg = (TextView) erImage.findViewById(R.id.tv_msg);
                ImageView iv_code = (ImageView) erImage.findViewById(R.id.iv_code);
                tv_msg.setText("扫描二维码，下载邮支付APP");
                iv_code.setImageResource(R.drawable.appdown_l);
                final AlertDialog dialog = adb.setView(erImage).show();
                iv_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
//                adb.setView(erImage).show();
                break;
            case R.id.btn_right:
                Intent intent = new Intent(context, InviteHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_qq:// qq分享
                ShareSDKManager.shareToQQ((Activity) context,
                        shareMap.get("title"), shareMap.get("content"),
                        shareMap.get("file_url"), shareMap.get("share_image"));
//			showShare(true, QQ.NAME);
                break;
            case R.id.tv_space:// qq分享
                ShareSDKManager.shareToQQZone((Activity) context,
                        shareMap.get("title"), shareMap.get("content"),
                        shareMap.get("file_url"), shareMap.get("share_image"));
//			ShareSDKManager.shareToQQ((Activity) context,
//					shareMap.get("title"), shareMap.get("content"),
//					shareMap.get("file_url"), shareMap.get("share_image"));
                break;
            case R.id.tv_blog:// 新浪分享
                ShareSDKManager.shareToSina((Activity) context,
                        shareMap.get("title"), shareMap.get("content"),
                        shareMap.get("file_url"), shareMap.get("share_image"));
                break;
            case R.id.tv_wechat:// 微信好友
                ShareSDKManager.shareToWechat((Activity) context,
                        shareMap.get("title"), shareMap.get("content"),
                        shareMap.get("file_url"), shareMap.get("share_image"));
                break;
            case R.id.tv_firend:// 朋友圈分享
                ShareSDKManager.shareToWechatMoment((Activity) context,
                        shareMap.get("title"), shareMap.get("content"),
                        shareMap.get("file_url"), shareMap.get("share_image"));
                break;
            case R.id.btn_invite:// 分享注册
//                popShare();
                ShareSDKManager.showShare( context,
                        shareMap.get("title"), shareMap.get("content"),
                        shareMap.get("file_url"), shareMap.get("share_image"));
                break;
            case R.id.btn_share:// 分享二维码
                ShareSDKManager.showShare(context,
                        shareMap.get("title"), "快来下载邮支付APP!\n足不出户，安然生活！",
                        ConstantValues.APP_DOWNLOAD_URL, shareMap.get("share_image"));
                break;
            default:
                break;
        }
    }

    private void popShare() {

        View contentView = LayoutInflater.from(this).inflate(R.layout.item_share, null);

//        创建并显示popWindow
        mListPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
                .create()
                .showAtLocation(btn_invite, Gravity.BOTTOM, 0, 0);
//                .showAsDropDown(btn_invite,0,20);
//                .showAsDropDown(btn_clearing, 0, -btn_clearing.getHeight());
//         设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("邮支付");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(ConstantValues.APP_DOWNLOAD_URL);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("鹤壁邮政邮支付平台！");
        // 分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(ConstantValues.BASE_URL + "/upload/2.png");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(ConstantValues.APP_DOWNLOAD_URL);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("鹤壁邮政邮支付平台");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("鹤壁邮政邮支付平台");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(ConstantValues.APP_DOWNLOAD_URL);

        // 启动分享GUI
        oks.show(this);
    }

}
