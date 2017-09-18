//package com.ksource.hbStore.util;
//
//import android.app.Activity;
//import android.content.Context;
//import android.widget.Toast;
//
//import java.util.HashMap;
//
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.onekeyshare.OnekeyShare;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import cn.sharesdk.tencent.qq.QQ;
//import cn.sharesdk.tencent.qzone.QZone;
//import cn.sharesdk.wechat.friends.Wechat;
//import cn.sharesdk.wechat.moments.WechatMoments;
//
//public class ShareSDKManager {
//
//	/**
//	 * 微信朋友圈
//	 */
//	public static void shareToWechatMoment(Activity activity, String title,
//			String text, String url, String imageUrl) {
//		if (NetConnect.getInstance().isNetConnect(activity)) {
//			WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
//			sp.setShareType(Platform.SHARE_WEBPAGE);
//			sp.setTitle(title);
//			sp.setText(text);
//			sp.setUrl(url);
//			sp.setImageUrl(imageUrl);
//
//			Platform plat = ShareSDK.getPlatform(activity, WechatMoments.NAME);
//			plat.setPlatformActionListener(new PaListener(activity));
//			plat.share(sp);
////			if (plat.isValid()) {
////			} else {
////				Toast.makeText(activity, "授权失败！", 0).show();
////			}
//		} else {
//			Toast.makeText(activity, "网络连接异常！", Toast.LENGTH_LONG).show();
//		}
//	}
//
//	/**
//	 * 微信好友
//	 */
//	public static void shareToWechat(Activity activity, String title,
//			String text, String url, String imageUrl) {
//		if (NetConnect.getInstance().isNetConnect(activity)) {
//
//			final Platform weixin = ShareSDK.getPlatform(activity, Wechat.NAME);
//			Wechat.ShareParams sp = new Wechat.ShareParams();
//			// sp.shareType = Platform.SHARE_TEXT;
//			sp.setShareType(Platform.SHARE_WEBPAGE);
//			sp.setTitle(title);
//			sp.setText(text);
//			sp.setUrl(url);
//			sp.setImageUrl(imageUrl);
//			weixin.share(sp);
//
//		} else {
//			Toast.makeText(activity, "网络连接异常！", Toast.LENGTH_LONG).show();
//		}
//	}
//
//	/**
//	 * 分享到QQ好友
//	 */
//	public static void shareToQQ(Activity activity, String title, String text,
//			String url, String imageUrl) {
//		if (NetConnect.getInstance().isNetConnect(activity)) {
//			QQ.ShareParams qq = new QQ.ShareParams();
//			qq.title = title;
//			qq.text = text;
//			qq.titleUrl = url;
//			qq.imageUrl = imageUrl;
//			Platform pf = ShareSDK.getPlatform(activity, QQ.NAME);
//			pf.setPlatformActionListener(new PaListener(activity)); // 设置分享事件回调
//			pf.share(qq); // 执行图文分享
//		} else {
//			Toast.makeText(activity, "网络连接异常！", Toast.LENGTH_LONG).show();
//		}
//	}
//
//	/**
//	 * 分享到QQ空间
//	 */
//	public static void shareToQQZone(Activity activity, String title,
//			String text, String url, String imageUrl) {
//		if (NetConnect.getInstance().isNetConnect(activity)) {
//
//			QZone.ShareParams qzone = new QZone.ShareParams();
//			qzone.title = title;
//			qzone.text = text;
//			qzone.titleUrl = url;
//			qzone.imageUrl = imageUrl;
//			Platform pf = ShareSDK.getPlatform(activity, QZone.NAME);
//			pf.setPlatformActionListener(new PaListener(activity)); // 设置分享事件回调
//			pf.share(qzone); // 执行图文分享
//		} else {
//			Toast.makeText(activity, "网络连接异常！", Toast.LENGTH_LONG).show();
//		}
//	}
//
//	/**
//	 * 分享到新浪微博
//	 */
//	public static void shareToSina(Activity activity, String title,
//			String text, String url, String imageUrl) {
//		if (NetConnect.getInstance().isNetConnect(activity)) {
//
//			SinaWeibo.ShareParams sinaWeibo = new SinaWeibo.ShareParams();
//			sinaWeibo.setTitle(title);
//			sinaWeibo.setText(text + "\n" + url);
////			sinaWeibo.setUrl(url);
//			sinaWeibo.setImageUrl(imageUrl);
//
////			sinaWeibo.text = text + "\n" + url;
////			sinaWeibo.imageUrl = imageUrl;
//			Platform pf = ShareSDK.getPlatform(activity, SinaWeibo.NAME);
//			pf.setPlatformActionListener(new PaListener(activity)); // 设置分享事件回调
//			pf.share(sinaWeibo); // 执行图文分享
//		} else {
//			Toast.makeText(activity, "网络连接异常！", Toast.LENGTH_LONG).show();
//		}
//	}
//
//	/**
//	 * 一键分享
//	 */
//	public static void showShare(Context context, String title, String text,
//			String url, String imageUrl) {
//		OnekeyShare oks = new OnekeyShare();
//		// 关闭sso授权
//		// oks.disableSSOWhenAuthorize();
//
//		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
//		// oks.setNotification(R.drawable.ic_launcher,
//		// getString(R.string.app_name));
//		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//		oks.setTitle(title);
//		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		oks.setTitleUrl(url);
//		// text是分享文本，所有平台都需要这个字段
//		oks.setText(text);
//		// 分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//		oks.setImageUrl(imageUrl);
//		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//		// url仅在微信（包括好友和朋友圈）中使用
//		oks.setUrl(url);
//		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		// oks.setComment("鹤壁邮政邮支付平台");
//		// site是分享此内容的网站名称，仅在QQ空间使用
//		oks.setSite(title);
//		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
//		oks.setSiteUrl(url);
//
//		// 启动分享GUI
//		oks.show(context);
//	}
//
//	/**
//	 * 分享的监听
//	 */
//	public static class PaListener implements PlatformActionListener {
//		private Activity activity;
//
//		public PaListener(Activity activity) {
//			this.activity = activity;
//		}
//
//		@Override
//		public void onCancel(Platform arg0, int arg1) {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					Toast.makeText(activity, "分享取消", Toast.LENGTH_LONG).show();
//				}
//			});
//		}
//
//		@Override
//		public void onComplete(Platform arg0, int arg1,
//				HashMap<String, Object> arg2) {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					Toast.makeText(activity, "分享成功", Toast.LENGTH_LONG).show();
//				}
//			});
//		}
//
//		@Override
//		public void onError(Platform arg0, int arg1, final Throwable arg2) {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					System.out.println(""+arg2);
//					Toast.makeText(activity, "分享失败"+arg2, Toast.LENGTH_LONG).show();
//				}
//			});
//		}
//	}
//
//}
