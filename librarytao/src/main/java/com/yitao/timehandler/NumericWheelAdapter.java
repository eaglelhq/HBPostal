/*
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.yitao.timehandler;


/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter implements WheelAdapter {
	
	/**
	 * Ĭ�����ֵ
	 */
	public static final int DEFAULT_MAX_VALUE = 9;

	/** 
	 * Ĭ����Сֵ 
	 */
	private static final int DEFAULT_MIN_VALUE = 0;
	
	//��Сֵ
	private int minValue;
	//���ֵ
	private int maxValue;
	
	/**
	 * ��ʶ��0--�����ȣ�1--ֻ���·�
	 */
	private int mFlag=-1;
	
	private String[] items = new String[]{"ȫ��","1����","2����","3����","4����","01��","02��","03��","04��","05��","06��","07��","08��","09��","10��","11��","12��"};
	
	private String[] itemMonths = new String[]{"01��","02��","03��","04��","05��","06��","07��","08��","09��","10��","11��","12��"};

	
	/**
	  * ����˵�������캯���޲���<br>
	  * ���ߣ����� <br>
	  * ʱ�䣺2015-7-31 ����11:02:05 <br> <br>
	  */
	public NumericWheelAdapter() {
		this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
	}

	/**
	  * ����˵�������캯�����÷�Χ�����ֵ����Сֵ<br>
	  * ���ߣ����� <br>
	  * ʱ�䣺2015-7-31 ����11:02:21 <br>
	  * @param minValue
	  * @param maxValue <br>
	  */
	public NumericWheelAdapter(int minValue, int maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	/**
	  * ����˵�����������ͣ�0--�����ȣ�1--ֻ���·�<br>
	  * ���ߣ����� <br>
	  * ʱ�䣺2015-7-31 ����11:12:17 <br>
	  * @param flag <br>
	  */
	public void setFlag(int flag){
		this.mFlag = flag;
	}

	@Override
	public String getItem(int index) {
		if (mFlag == 0){// ��ȵ�
			if (index >= 0 && index < getItemsCount()) {
				return items[index];
			}
		} else if (mFlag == 1){// ֻ���µ�
			if (index >= 0 && index < getItemsCount()) {
				return itemMonths[index];
			}
		} else {
			if (index >= 0 && index < getItemsCount()) {
				int value = minValue + index;
				return Integer.toString(value);
			}
		}
		return null;

	}

	@Override
	public int getItemsCount() {
		if(mFlag == 0){//���
			return items.length;
		}else if(mFlag == 1){
			return itemMonths.length;
		}
		return maxValue - minValue + 1;
	}
	
	@Override
	public int getMaximumLength() {
		int max = Math.max(Math.abs(maxValue), Math.abs(minValue));
		int maxLen = Integer.toString(max).length();
		if (minValue < 0) {
			maxLen++;
		}
		return maxLen;
	}
}
