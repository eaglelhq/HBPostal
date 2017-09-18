package com.yitao.timehandler;

import android.app.Activity;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.yitao.library_tao.R;

import java.util.Arrays;
import java.util.List;


/**
  * 类说明：选择时间 <br>
  * 类名：com.byl.testdate.widget.WheelMain <br>
  * 作者： 易涛 <br>
  * 时间：2015-8-3 上午9:41:24 <br>
  * 修改者：<br>
  * 修改日期：<br>
  * 修改内容：<br>
  */
public class WheelMain {
//	private PopupWindow mPop;
	private View view;
	private TextView label_year;
	private WheelView wv_year;
	
	private TextView label_month;
	private WheelView wv_month;
	
	private TextView label_day;
	private WheelView wv_day;
	
	private TextView label_hours;
	private WheelView wv_hours;
	
	private TextView label_mins;
	private WheelView wv_mins;
	
	private TextView label_seconds;
	private WheelView wv_seconds;
	
	private static int START_YEAR = 1990, END_YEAR = 2100;
	private Activity activity;
	private int initType = 0;//0--初始化年月日，1--初始化---年月日时分，2--初始化--年月日时分秒，3--初始化--时分秒
	//字体大小是14sp
	private static final int textSize = 14;

//	public View getView() {
//		return view;
//	}
//
//	public void setView(View view) {
//		this.view = view;
//	}

	public static int getSTART_YEAR() {
		return START_YEAR;
	}

	public static void setSTART_YEAR(int sTART_YEAR) {
		START_YEAR = sTART_YEAR;
	}

	public static int getEND_YEAR() {
		return END_YEAR;
	}

	public static void setEND_YEAR(int eND_YEAR) {
		END_YEAR = eND_YEAR;
	}
	
	public WheelMain(Activity activity,View view) {
		this.activity = activity;
		this.view = view;
	}
	
	/**
	 * 初始化到天
	 * 
	 * @param agr0 year
	 * @param agr1 month
	 * @param arg2 day
	 */
	public void initDateTimePicker(int type, int agr0, int agr1, int arg2) {
		if(type == 0){//初始化 年-月-日
			initType = 0;
			this.initDateTimePickera(agr0, agr1, arg2, -1, -1,-1);
		}else{
			initType = 3;
			this.initDateTimePickera(-1, -1, -1, agr0, agr1, arg2);
		}
		
	}

	/**
	 * 初始化到年
	 * 
	 * @param year
	 */
	public void initDateTimePickerYear(int year) {
		this.initQuarterPickeraYear(year);
	}

	/**
	 * 初始化到季度
	 * 
	 * @param year
	 * @param quarter
	 */
	public void initDateTimePicker(int year, int quarter) {
		this.initQuarterPickera(year, quarter);
	}

	/**
	 * 初始化到时分
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 */
	public void initDateTimePicker(int year, int month, int day, int hour, int minute) {
		initType = 1;
		this.initDateTimePickera(year, month, day, hour, minute, -1);
	}
	
	public void initDateTimePicker(int year, int month, int day, int hour, int minute, int seconds) {
		initType = 2;
		this.initDateTimePickera(year, month, day, hour, minute, seconds);
	}

	/**
	 * 初始化到  年月日时分秒
	 */
	public void initDateTimePickera(int year, int month, int day, int h, int m, int seconds) {
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_day = (WheelView) view.findViewById(R.id.day);

		label_year = (TextView) view.findViewById(R.id.label_year);
		label_month = (TextView) view.findViewById(R.id.label_mon);
		label_day = (TextView) view.findViewById(R.id.label_day);

		if (year != -1 && month != -1 && day != -1) {
			wv_year.setVisibility(View.VISIBLE);
			wv_month.setVisibility(View.VISIBLE);
			wv_day.setVisibility(View.VISIBLE);
			label_year.setVisibility(View.VISIBLE);
			label_month.setVisibility(View.VISIBLE);
			label_day.setVisibility(View.VISIBLE);
			
			
			
			wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
			wv_year.setCyclic(true);// 可循环滚动
//			wv_year.setLabel("年");// 添加文字
			wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

			// 月
			wv_month.setAdapter(new NumericWheelAdapter(1, 12));
			wv_month.setCyclic(true);
//			wv_month.setLabel("月");
			wv_month.setCurrentItem(month);

			// 日
			wv_day.setCyclic(true);
			// 判断大小月及是否闰年,用来确定"日"的数据
			if (list_big.contains(String.valueOf(month + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 31));
			} else if (list_little.contains(String.valueOf(month + 1))) {
				wv_day.setAdapter(new NumericWheelAdapter(1, 30));
			} else {
				// 闰年
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
					wv_day.setAdapter(new NumericWheelAdapter(1, 29));
				else
					wv_day.setAdapter(new NumericWheelAdapter(1, 28));
			}
//			wv_day.setLabel("日");
			wv_day.setCurrentItem(day - 1);
		}else{
			wv_year.setVisibility(View.GONE);
			wv_month.setVisibility(View.GONE);
			wv_day.setVisibility(View.GONE);
			label_year.setVisibility(View.GONE);
			label_month.setVisibility(View.GONE);
			label_day.setVisibility(View.GONE);
		}
		wv_hours = (WheelView) view.findViewById(R.id.hour);
		wv_mins = (WheelView) view.findViewById(R.id.mins);
		
		label_hours = (TextView) view.findViewById(R.id.label_hour);
		label_mins = (TextView) view.findViewById(R.id.label_minits);
		
		if (h != -1 && m != -1) {
			wv_hours.setVisibility(View.VISIBLE);
			wv_mins.setVisibility(View.VISIBLE);
			label_hours.setVisibility(View.VISIBLE);
			label_mins.setVisibility(View.VISIBLE);

			wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
			wv_hours.setCyclic(true);// 可循环滚动
//			wv_hours.setLabel("时");// 添加文字
			wv_hours.setCurrentItem(h);

			wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
			wv_mins.setCyclic(true);// 可循环滚动
//			wv_mins.setLabel("分");// 添加文字
			wv_mins.setCurrentItem(m);
		} else {
			wv_hours.setVisibility(View.GONE);
			wv_mins.setVisibility(View.GONE);
			label_hours.setVisibility(View.GONE);
			label_mins.setVisibility(View.GONE);
		}
		wv_seconds = (WheelView) view.findViewById(R.id.seconds);
		label_seconds = (TextView) view.findViewById(R.id.label_seconds);
		if (seconds != -1) {
			wv_seconds.setVisibility(View.VISIBLE);
			label_seconds.setVisibility(View.VISIBLE);
			
			wv_seconds.setAdapter(new NumericWheelAdapter(0, 59));
			wv_seconds.setCyclic(true);// 可循环滚动
//			wv_seconds.setLabel("秒");// 添加文字
			wv_seconds.setCurrentItem(seconds);
		}else{
			wv_seconds.setVisibility(View.GONE);
			label_seconds.setVisibility(View.GONE);
		}

		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						int day = wv_day.getCurrentItem() + 1;
						if (day > 29) {
							wv_day.setCurrentItem(28);
						} else {
							wv_day.setCurrentItem(day - 1);
						}
					}

					else {
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
						int day = wv_day.getCurrentItem() + 1;
						if (day > 28) {
							wv_day.setCurrentItem(27);
						} else {
							wv_day.setCurrentItem(day - 1);
						}
					}
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					int day = wv_day.getCurrentItem() + 1;
					if (day > 30) {
						wv_day.setCurrentItem(29);
					} else {
						wv_day.setCurrentItem(day - 1);
					}
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year.getCurrentItem() + START_YEAR) % 100 != 0) || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						int day = wv_day.getCurrentItem() + 1;
						// 判断如果是闰年
						if (day > 29) {
							wv_day.setCurrentItem(28);
						} else {
							wv_day.setCurrentItem(day - 1);
						}
					} else {
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
						int day = wv_day.getCurrentItem() + 1;
						if (day > 28) {
							wv_day.setCurrentItem(27);
						} else {
							wv_day.setCurrentItem(day - 1);
						}
					}
				}
			}

		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = getTextSize();
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;
		wv_seconds.TEXT_SIZE = textSize;
	}
	
	private int getTextSize(){
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return sp2px(textSize, metric.density);
	}
	
	 /**
	  * 方法说明：将sp转换成px<br>
	  * 作者：易涛 <br>
	  * 时间：2015-8-3 上午9:11:40 <br>
	  * @param spValue
	  * @param fontScale
	  * @return <br>
	  */
	public static int sp2px(float spValue, float fontScale) {
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * @Description: TODO 弹出日期时间选择器(年)
	 */
	public void initQuarterPickeraYear(int year) {
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = getTextSize();
		wv_year.TEXT_SIZE = textSize;
		wv_year.setPadding(0, 0, 0, 0);

	}

	/**
	 * @Description: TODO 弹出日期时间选择器(季度)
	 */
	public void initQuarterPickera(int year, int quarter) {
		// 添加大小月月份并将其转换为list,方便之后的判断
		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		wv_month = (WheelView) view.findViewById(R.id.month);
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(1, 17);
		numericWheelAdapter.setFlag(0);
		wv_month.setAdapter(numericWheelAdapter);
		wv_month.setCyclic(true);
		wv_month.setCurrentItem(quarter);

		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = getTextSize();
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				wv_month.scroll(0, 20);
			}
		}, 50);

	}

	/**
	 * @Description: TODO 弹出日期时间选择器(只有年月的)
	 */
	public void initQuarterPickeraForMonth(int year, int month) {
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
		// 月
		wv_month = (WheelView) view.findViewById(R.id.month);
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(1, 12);
		numericWheelAdapter.setFlag(1);// 只含有月的标识
		wv_month.setAdapter(numericWheelAdapter);
		wv_month.setCyclic(true);
		wv_month.setCurrentItem(month);

		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = getTextSize();
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				wv_month.scroll(0, 20);
			}
		}, 50);

	}

	public String getResult(){
		return getCurrentSelect();
	}
	
	private String getCurrentSelect(){
		int year = wv_year.getCurrentItem() + START_YEAR;
		int month = wv_month.getCurrentItem() + 1;
		int day = wv_day.getCurrentItem() + 1;
		int hour = wv_hours.getCurrentItem();
		int min = wv_mins.getCurrentItem();
		int seconds = wv_seconds.getCurrentItem();
		if(initType == 0){
			return year+"-"+(month>9?""+month:"0"+month)+"-"+(day>9?""+day:"0"+day);
		}else if(initType == 1){
			return year+"-"+(month>9?""+month:"0"+month)+"-"+(day>9?""+day:"0"+day)+" "+(hour>9?""+hour:"0"+hour)+":"+(min>9?""+min:"0"+min);
		}else if(initType == 2){
			return year+"-"+(month>9?""+month:"0"+month)+"-"+(day>9?""+day:"0"+day)+" "+(hour>9?""+hour:"0"+hour)+":"+(min>9?""+min:"0"+min)+":"+(seconds>9?""+seconds:"0"+seconds);
		}else{
			return (hour>9?""+hour:"0"+hour)+":"+(min>9?""+min:"0"+min)+":"+(seconds>9?""+seconds:"0"+seconds);
		}
	}

	/***
	 * 得到选中的年和季度
	 * 
	 * @return
	 */
	public String getYearAndQuarter() {
		StringBuffer sb = new StringBuffer();
		sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-").append((wv_month.getCurrentItem()));
		System.out.println("--选中的季度--" + sb.toString());
		return sb.toString();
	}

	/***
	 * 得到选中的年
	 * 
	 * @return
	 */
	public String getYear() {
		StringBuffer sb = new StringBuffer();
		sb.append((wv_year.getCurrentItem() + START_YEAR));
		System.out.println("--选中的年--" + sb.toString());
		return sb.toString();
	}
}
