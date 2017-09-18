package com.yitao.util;

import com.yitao.dialog.CallOrSendMessageDialog;
import com.yitao.dialog.LoadDialog;
import com.yitao.dialog.SelectItemDialog;
import com.yitao.dialog.TiShiDialog;
import com.yitao.dialog.TiShiOnlySureDialog;
import com.yitao.dialog.TimeSelectDialog;
import com.yitao.dialog.UpOrDownLoadDialog;
import com.yitao.dialog.UpdateValueDialog;
import com.yitao.library_tao.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 类说明：公用类, 单例模式 <br>
 * 类名：com.ksource.oa.utils.DialogUtil <br>
 * 作者： 易涛 <br>
 * 时间：2015-10-15 上午8:57:26 <br>
 * 修改者：<br>
 * 修改日期：<br>
 * 修改内容：<br>
 */
public class DialogUtil {
	private static DialogUtil instance;

	private DialogUtil() {
	}

	/**
	 * 获得该类实例
	 * 
	 * @return
	 */
	public static DialogUtil getInstance() {
		if (instance == null) {
			instance = new DialogUtil();
		}
		return instance;
	}

	/**
	 * 方法说明：显示load dialog<br>
	 * 作者：易涛 <br>
	 * 时间：2015-10-15 上午8:59:57 <br>
	 * 
	 * @param context
	 * @param text
	 * @return <br>
	 */
	public LoadDialog showLoadDialog(Context context, String text) {
		LoadDialog dialog = new LoadDialog(context, R.style.popup_dialog_style);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER);

		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.y = 60;// 设置y坐标
		window.setAttributes(lp);
		window.setWindowManager(mWindowManager, null, null);
		dialog.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域不可关闭Dialog
		dialog.show();
		if (text != null && !text.equals("")) {
			dialog.setLoadText(text);
		}
		return dialog;
	}

	/**
	 * 方法说明：显示load dialog<br>
	 * 作者：易涛 <br>
	 * 时间：2015-10-15 上午8:59:42 <br>
	 * 
	 * @param context
	 * @return <br>
	 */
	public LoadDialog showLoadDialog(Context context) {
		LoadDialog dialog = new LoadDialog(context, R.style.popup_dialog_style);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.y = 60;// 设置y坐标
		window.setAttributes(lp);
		window.setWindowManager(mWindowManager, null, null);
		dialog.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域不可关闭Dialog
		dialog.show();
		return dialog;
	}

	/**
	 * 方法说明：上传下载dialog<br>
	 * 作者：易涛 <br>
	 * 时间：2015-10-14 下午2:20:13 <br>
	 * 
	 * @param context
	 * @return <br>
	 */
	public UpOrDownLoadDialog showUpAndDownDialog(Context context, String text) {
		UpOrDownLoadDialog dialog = new UpOrDownLoadDialog(context,
				R.style.popup_dialog_style);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER);

		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		window.setWindowManager(mWindowManager, null, null);
		dialog.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域不可关闭Dialog
		dialog.show();
		if (text != null && !text.equals("")) {
			dialog.setLoadText(text);
		}
		return dialog;
	}

	/**
	 * 方法说明：提示dialog<br>
	 * 作者：易涛 <br>
	 * 时间：2015-10-15 上午9:00:15 <br>
	 * 
	 * @param context
	 * @param text
	 * @return <br>
	 */
	public TiShiDialog showTiShiDialog(Context context, String text) {
		TiShiDialog dialog = new TiShiDialog(context,
				R.style.popup_dialog_style);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER);

		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		window.setWindowManager(mWindowManager, null, null);
		dialog.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域不可关闭Dialog
//		window.setWindowAnimations(R.style.dialogTiShiAnimation);
		dialog.show();
		if (text != null && !text.equals("")) {
			dialog.setLoadText(text);
		}
		return dialog;
	}

	/**
	 * 提示框中包含图片
	 * 
	 * @param context
	 * @param resId
	 * @param text
	 * @return
	 */
	public TiShiDialog showTiShiDialog(Context context, int resId, String text) {
		TiShiDialog dialog = new TiShiDialog(context,
				R.style.popup_dialog_style);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER);

		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		window.setWindowManager(mWindowManager, null, null);
		dialog.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域不可关闭Dialog
//		window.setWindowAnimations(R.style.dialogTiShiAnimation);
		dialog.show();
		if (resId != 0) {
			dialog.setLoadImage(resId);
		}
		if (text != null && !text.equals("")) {
			dialog.setLoadText(text);
		}
		return dialog;
	}

	/**
	 * 方法说明：提示dialog<br>
	 * 作者：易涛 <br>
	 * 时间：2015-10-15 上午9:00:15 <br>
	 * 
	 * @param context
	 * @param text
	 * @return <br>
	 */
	public UpdateValueDialog showUpdateValueDialog(Context context,
			String title, String text) {
		UpdateValueDialog dialog = new UpdateValueDialog(context,
				R.style.popup_dialog_style);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER);

		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		window.setWindowManager(mWindowManager, null, null);
		dialog.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域不可关闭Dialog
//		window.setWindowAnimations(R.style.dialogTiShiAnimation);
		dialog.show();
		if (text != null && !text.equals("")) {
			dialog.setLoadText(title, text);
		}
		return dialog;
	}

	public TimeSelectDialog showTimeSelectDialog(Context context,
			TextView tv_view, String time, String dateFormat) {
		TimeSelectDialog dialog = new TimeSelectDialog(context,
				R.style.popup_dialog_style, tv_view, time, dateFormat);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);

		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		window.setWindowManager(mWindowManager, null, null);
		dialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域不可关闭Dialog
		window.setWindowAnimations(R.style.timeselectPopAnimation);
		dialog.show();
		return dialog;
	}

	/**
	 * 方法说明：选项弹出框<br>
	 * 作者：易涛 <br>
	 * 时间：2015-10-15 下午2:32:17 <br>
	 * 
	 * @param context
	 * @param text
	 * @return <br>
	 */
	public SelectItemDialog showSelectItemDialog(Context context, String text) {
		SelectItemDialog dialog = new SelectItemDialog(context,
				R.style.popup_dialog_style);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);

		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		window.setWindowManager(mWindowManager, null, null);
		dialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域不可关闭Dialog
		window.setWindowAnimations(R.style.timeselectPopAnimation);
		dialog.show();
		if (text != null && !text.equals("")) {
			dialog.setTitle(text);
		}
		return dialog;
	}

	public void dialogDismiss(Dialog dialog) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	/**
	 * 方法说明：提示dialog<br>
	 * 作者：易涛 <br>
	 * 时间：2015-10-15 上午9:00:15 <br>
	 * 
	 * @param context
	 * @param text
	 * @return <br>
	 */
	public TiShiOnlySureDialog showTiShiOnlySureDialog(Context context,
			String title, String text) {
		TiShiOnlySureDialog dialog = new TiShiOnlySureDialog(context,
				R.style.popup_dialog_style);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER);

		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		window.setWindowManager(mWindowManager, null, null);
		dialog.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域不可关闭Dialog
//		window.setWindowAnimations(R.style.dialogTiShiAnimation);
		dialog.show();
		if (text != null && !text.equals("")) {
			dialog.setLoadText(title, text);
		}
		return dialog;
	}

	/**
	 * 带图片的提示框
	 * @param context
	 * @param title
	 * @param resId
	 * @param text
	 * @return
	 */
	public TiShiOnlySureDialog showTiShiOnlySureDialog(Context context,
			String title, int resId, String text) {
		TiShiOnlySureDialog dialog = new TiShiOnlySureDialog(context,
				R.style.popup_dialog_style);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.CENTER);

		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		window.setWindowManager(mWindowManager, null, null);
		dialog.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域不可关闭Dialog
//		window.setWindowAnimations(R.style.dialogTiShiAnimation);
		dialog.show();
		if (resId != 0) {
			dialog.setLoadImage(resId);
		}
		if (text != null && !text.equals("")) {
			dialog.setLoadText(title, text);
		}
		return dialog;
	}

	/**
	 * 弹出打电话和发短信对话框
	 */
	public CallOrSendMessageDialog showCallOrSendMessageDialog(Context context,
			String phoneNum) {
		CallOrSendMessageDialog dialog = new CallOrSendMessageDialog(context,
				R.style.popup_dialog_style);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);

		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		window.setWindowManager(mWindowManager, null, null);
		dialog.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域不可关闭Dialog
//		window.setWindowAnimations(R.style.dialogTiShiAnimation);
		dialog.show();
		if (phoneNum != null && !phoneNum.equals("")) {
			dialog.setItemTextForCall(phoneNum);
		}
		return dialog;
	}

	/**
	 * 弹出拍照与相册对话框
	 */
	public CallOrSendMessageDialog showCameraOrPicDialog(Context context) {
		CallOrSendMessageDialog dialog = new CallOrSendMessageDialog(context,
				R.style.popup_dialog_style);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);

		WindowManager mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		window.setWindowManager(mWindowManager, null, null);
		dialog.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域不可关闭Dialog
//		window.setWindowAnimations(R.style.dialogTiShiAnimation);
		dialog.show();
		dialog.setItemTextForPic();
		return dialog;
	}

}
