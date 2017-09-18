package com.ksource.hbpostal.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BannerBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.yitao.util.ImageLoaderUtil;
import com.yitao.util.LogUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 启动页轮播图
 */
public class ImageActivity extends BaseActivity {

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:
				sp.edit().putBoolean(ConstantValues.IS_SHOW_BANNER, false)
						.commit();
				startActivity(new Intent(context, MainActivity.class));
				finish();
				break;

			default:
				break;
			}
		}
	};

	// private Banner banner;
	private LinearLayout ll_go;
	private TextView tv_count_down;
	private ImageView iv_banner;

	// 倒计时
	private int recTimer;
	TimerTask task;
	Timer timer;

	private BannerBean bannerBean;



	@Override
	public int getLayoutResId() {
		return R.layout.activity_banner;
	}

	@Override
	public void initView() {
		tv_count_down = (TextView) findViewById(R.id.tv_count_down);
		ll_go = (LinearLayout) findViewById(R.id.ll_go);
		iv_banner = (ImageView) findViewById(R.id.iv_banner);
		// ll_go.setVisibility(View.GONE);

	}

	@Override
	public void initListener() {
		ll_go.setOnClickListener(this);
		iv_banner.setOnClickListener(this);
	}

	@Override
	public void initData() {
		String bannerjson = sp.getString(ConstantValues.SPLASH_BANNER, null);
		Gson gson = new Gson();
		bannerBean = gson.fromJson(bannerjson, BannerBean.class);
		if (bannerBean == null ||bannerBean.advertList.size() == 0) {
			startActivity(new Intent(context,MainActivity.class));
			return;
		}
		ImageLoaderUtil.loadLocationPhotoWithoutError("file://"+ConstantValues.IMAGEFILEPATH+"banner.jpg", iv_banner);
		timer = new Timer();
		task = new MyTask();
		recTimer = 3;
		tv_count_down.setText("" + recTimer);
		timer.schedule(task, 1000, 1000);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_go:
			// autoOpen = false;
			LogUtils.e("OPEN_MAIN", "onClick");
			startActivity(new Intent(context, MainActivity.class));
			finish();
			break;
		case R.id.iv_banner:
			// autoOpen = false;
			String url = bannerBean.advertList.get(0).URL;
			String title = bannerBean.advertList.get(0).TITLE;
			String type = bannerBean.advertList.get(0).LINK_TYPE;
			finish();
			startActivity(new Intent(context, MainActivity.class));
				switch (Integer.parseInt(type)) {
					case 1://http链接
						if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
							Intent intent = new Intent(context,
									BaseHtmlActivity.class);
							intent.putExtra("title", title);
							intent.putExtra("url", url);
							startActivity(intent);
						}
						break;
					case 2://商品详情
						Intent intent = new Intent(context,
								GoodsDetailActivity.class);
						intent.putExtra("goodsId", url);
						startActivity(intent);
						break;
					case 3://生活缴费
						startActivity(new Intent(context, SHJFActivity.class));
						break;
					case 4://个人中心
						if (MainActivity.rg_home != null) {
							// getActivity().getSupportFragmentManager().beginTransaction()
							// .replace(R.id.fl_content, new HomeFragment()).commit();
							MainActivity.rg_home.check(R.id.rb_me);
						}
						break;
					case 5://签到
						Intent intentSign = new Intent(context, SignActivity.class);
						startActivity(intentSign);
						break;
					case 6://订单列表
						Intent intentOrder = new Intent(context, OrderActivity.class);
						intentOrder.putExtra("order", 0);
						startActivity(intentOrder);
						break;
					case 7://互动交流
						startActivity(new Intent(context, HDJLActivity.class));
						break;
					case 8://八戒农场
						startActivity(new Intent(context, BJNCActivity1.class));
						break;
					case 9://手机缴费
						startActivity(new Intent(context, SJCZActivity.class));
						break;
					case 10://交通违章
						startActivity(new Intent(context, JTWZActivity.class));
						break;
					default:

						break;
				}

//			Intent intent = new Intent(context, BaseHtmlActivity.class);
//			intent.putExtra("title", title);
//			intent.putExtra("url", url);
//			startActivity(intent);
			break;

		default:
			break;
		}
	}

	class MyTask extends TimerTask {
		@Override
		public void run() {

			runOnUiThread(new Runnable() { // 在主线程中执行
				@Override
				public void run() {
					recTimer--;
					tv_count_down.setText("" + recTimer);
					if (recTimer <= 0) {
						timer.cancel();
						task.cancel();
						startActivity(new Intent(context, MainActivity.class));
						finish();
					}
				}
			});
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		if (task != null) {
			task.cancel();
		}
	}
	
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
	}

}
