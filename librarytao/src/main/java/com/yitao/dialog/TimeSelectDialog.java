package com.yitao.dialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.yitao.library_tao.R;
import com.yitao.timehandler.WheelMain;
import com.yitao.util.DateStringUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ParseException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
/**
 * 选项弹出框
 * @author Administrator
 *
 */
public class TimeSelectDialog extends Dialog {
	private Context mContext;
	private Button mSure;
	private Button mCancel;
	private View view;
	private WheelMain wheelMain;
	private String timeSelect;
	private String dateFormatString;
	private TextView tv_view;
	
	public TimeSelectDialog(Context context) {
		super(context);
		this.mContext = context;
	}

	public TimeSelectDialog(Context context, int theme,TextView tv_view, String time, String dateFormat) {
		super(context, theme);
		this.mContext = context;
		this.tv_view = tv_view;
		this.timeSelect = time;
		this.dateFormatString = dateFormat;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = LayoutInflater.from(mContext).inflate(R.layout.dialog_date_pick, null);
		setContentView(view);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		initView();
		initEvent();
	}

	@SuppressLint("SimpleDateFormat")
	private void initView() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
		mSure = (Button) findViewById(R.id.set);
		mCancel = (Button) findViewById(R.id.cancel);
		wheelMain = new WheelMain((Activity)mContext,view);
		Calendar calendar = Calendar.getInstance();
		if (DateStringUtil.isDate(timeSelect, dateFormatString)) {
			try {
				try {
					calendar.setTime(dateFormat.parse(timeSelect));
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		if ("yyyy-MM-dd".equals(dateFormatString)) {
			wheelMain.initDateTimePicker(0, year, month, day);
		} else if ("HH:mm:ss".equals(dateFormatString)) {
			wheelMain.initDateTimePicker(1, hour, minute, seconds);
		} else if ("yyyy-MM-dd HH:mm:ss".equals(dateFormatString)) {
			wheelMain.initDateTimePicker(year, month, day, hour, minute, seconds);
		} else {
			wheelMain.initDateTimePicker(year, month, day, hour, minute);
		}
	}
	
	private void initEvent(){
		mSure.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tv_view.setText(wheelMain.getResult());
				dismiss();
			}
		});
		
		mCancel.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	public interface OnClickForSelectTimeListener{
		public void onClickForSelectTime(String time);
	}
}
