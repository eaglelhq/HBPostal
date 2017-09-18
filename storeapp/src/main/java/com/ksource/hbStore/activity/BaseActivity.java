package com.ksource.hbStore.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.blankj.utilcode.utils.SPUtils;
import com.ksource.hbStore.MyApplication;
import com.ksource.hbStore.config.ConstantValues;
import com.yitao.util.LogUtils;

/**
 * activity基类
 */
public abstract class BaseActivity extends FragmentActivity implements
		OnClickListener {
	
	public Context context;
	public MyApplication mApplication;
	public SPUtils spUtils = new SPUtils(ConstantValues.SP_NAME);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = MyApplication.getInstance();
		mApplication.addActivity(this);
		//最终方案
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////5.0 全透明实现
////getWindow.setStatusBarColor(Color.TRANSPARENT)
//			Window window = getWindow();
//			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//			window.setStatusBarColor(Color.TRANSPARENT);
//		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			//4.4 全透明状态栏
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//		}
		//透明状态栏
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//		//透明导航栏  
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); 
		setContentView(getLayoutResId());
		context = this;
		initView();
		initListener();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mApplication.removeActivity(this);
	}
	/**
	 * 获取当前界面的布局
	 * 
	 * @return
	 */
	public abstract int getLayoutResId();

	/**
	 * 查找控件
	 */
	public abstract void initView();

	/**
	 * 给控件添加监听
	 */
	public abstract void initListener();

	/**
	 * 初始化数据 给控件填充内容
	 */
	public abstract void initData();

	/**
	 * 子类界面处理按钮的单击事件
	 * 
	 * @param v
	 */
	public void onInnerClick(View v) {

	}

	/**
	 * 弹出吐司
	 */
	public void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public void showToast(int msgResId) {
		Toast.makeText(getApplicationContext(), msgResId, Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * 打印log
	 */
	public void logI(String msg) {
		LogUtils.i(getClass(), msg);
	}

}
