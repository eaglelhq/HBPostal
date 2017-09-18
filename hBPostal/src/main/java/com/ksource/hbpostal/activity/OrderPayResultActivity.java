package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.JPGoogsBean;
import com.ksource.hbpostal.bean.JPGoogsBean.GoodsListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.ksource.hbpostal.util.ImageLoaderUtil;
import com.ksource.hbpostal.widgets.MyGridView;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;

/**
 * 订单支付完成页面
 */
public class OrderPayResultActivity extends BaseActivity {

	private TextView tv_change;
	private MyGridView gv_pay_result;
	private Button btn_share, btn_see;
	private GridAdapter adapter;
	private TextView tv_see_order;
	private List<GoodsListBean> datas;
	private LoadDialog mLoadDialog;
	private String goodsName;
	private String image;
	private String token;
	private boolean isBJ;
	@Override
	public int getLayoutResId() {
		return R.layout.activity_pay_order_result;
	}

	@Override
	public void initView() {
		tv_change = (TextView) findViewById(R.id.tv_change);
		tv_see_order = (TextView) findViewById(R.id.tv_see_order);
		btn_share = (Button) findViewById(R.id.btn_share);
		btn_see = (Button) findViewById(R.id.btn_see);
		gv_pay_result = (MyGridView) findViewById(R.id.gv_pay_result);

		isBJ = getIntent().getBooleanExtra("isBJ",false);
		goodsName = getIntent().getStringExtra("goodsName");
		image = getIntent().getStringExtra("image");
		token = sp.getString(ConstantValues.TOKEN, null);
		if (TextUtils.isEmpty(token)) {
//			finish();
			startActivity(new Intent(context, LoginActivity.class));
		}
	}

	@Override
	public void initListener() {
		tv_change.setOnClickListener(this);
		tv_see_order.setOnClickListener(this);
		btn_share.setOnClickListener(this);
		btn_see.setOnClickListener(this);
		gv_pay_result.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(context, GoodsDetailActivity.class);
				intent.putExtra("goodsId", datas.get(position).ID);
				startActivity(intent);
				
			}
			
		});
	}

	@Override
	public void initData() {

		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"数据加载中...");
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				Gson gson = new Gson();
				JPGoogsBean resultBean = null;
				try {
					resultBean = gson.fromJson(arg0, JPGoogsBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (resultBean == null) {
					ToastUtil.showTextToast(context, "获取商品列表失败！");
					return;
				}
				if (resultBean.success) {
					if (resultBean.goodsList != null && resultBean.goodsList.size()>0) {
						datas = resultBean.goodsList;
						adapter = new GridAdapter(datas);
						gv_pay_result.setAdapter(adapter);
					}
				} else if (resultBean.flag == 10) {
					mApplication.login();
//					initData();
				} else {
					ToastUtil.showTextToast(context, resultBean.msg);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "获取商品列表失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.PAY_SUCCESS_RESULT_URL, params, callback);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_change:
			initData();
			
			break;
		case R.id.btn_share:
			showShare();
			break;
		case R.id.btn_see:
			mApplication.finishAllActivity();
			startActivity(new Intent(context, MainActivity.class));
			startActivity(new Intent(context, ActivityCate.class));
			break;
		case R.id.tv_see_order:
			if (isBJ){
				startActivity(new Intent(context, NPCOrderActivity.class));
			}else {
				Intent orderIntent = new Intent(context, OrderActivity.class);
				orderIntent.putExtra("order", 0);
				startActivity(orderIntent);
			}
			break;

		default:
			break;
		}
	}
	
	private void showShare() {
		 ShareSDK.initSDK(this);
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 
		 
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle("邮支付平台");
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		 oks.setTitleUrl(ConstantValues.APP_DOWNLOAD_URL);
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText("我在邮支付APP上买了"+goodsName+"\n足不出户，安然生活");
		 //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
		 if (TextUtils.isEmpty(image)) {
			image = ConstantValues.BASE_URL + "/mobile/images/life_icon_logo.png";
		} 
		 oks.setImageUrl(ConstantValues.BASE_URL +image);
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		 // url仅在微信（包括好友和朋友圈）中使用
		 oks.setUrl(ConstantValues.APP_DOWNLOAD_URL);
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		 oks.setComment("鹤壁邮政邮支付平台\n足不出户，安然生活");
		 // site是分享此内容的网站名称，仅在QQ空间使用
		 oks.setSite("鹤壁邮政邮支付平台");
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
		 oks.setSiteUrl(ConstantValues.APP_DOWNLOAD_URL);
		 oks.setCallback(new PlatformActionListener() {
				
				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					ToastUtil.showTextToast(context, "分享取消");
				}
				
				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
					ToastUtil.showTextToast(context, "分享成功");
				}
				
				@Override
				public void onCancel(Platform arg0, int arg1) {
					ToastUtil.showTextToast(context, "分享失败");
				}
		 });
		// 启动分享GUI
		 oks.show(this);
		 }
	
	
	class GridAdapter extends DefaultBaseAdapter<GoodsListBean> {

		public GridAdapter(List<GoodsListBean> datas) {
			super(datas);
		}

		@SuppressWarnings("finally")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item_goods, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.iv_goods);
				holder.name = (TextView) convertView
						.findViewById(R.id.tv_goods_name);
				holder.sale = (TextView) convertView
						.findViewById(R.id.tv_goods_sale);
				holder.count = (TextView) convertView
						.findViewById(R.id.tv_goods_count);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 填充数据
			int type = datas.get(position).BUY_TYPE;
			switch (type) {
			case 1:
				holder.sale.setText("￥"+datas.get(position).PRICE);
				
				break;
			case 2:
				holder.sale.setText("￥"+datas.get(position).PRICE+"+"+datas.get(position).INTEGRAL+"积分");
				
				break;
			case 3:
				holder.sale.setText(datas.get(position).INTEGRAL+"积分");
				
				break;

			default:
				break;
			}
			holder.name.setText(datas.get(position).NAME);
			holder.count.setText("已兑换" + datas.get(position).BUY_NUM);
			try {
				ImageLoaderUtil.loadNetPic(ConstantValues.BASE_URL+datas.get(position).IMAGE, holder.icon);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}

	}

	private class ViewHolder {
		private ImageView icon;
		private TextView name, sale, count;
	}

}
