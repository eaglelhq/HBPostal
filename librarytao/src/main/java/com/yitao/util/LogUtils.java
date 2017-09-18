package com.yitao.util;

import android.util.Log;

public class LogUtils {
    private static final boolean ENABLE = true;
    
    public static void i(String tag, String msg) {
        if (ENABLE) {
            Log.i(tag, msg);
        }
    }
    public static void e(String tag, String msg) {
    	if (ENABLE) {
    		Log.e(tag, msg);
    	}
    }
    
    public static void i(Class<?> cls, String msg) {
        if (ENABLE) {
            Log.i("air_" + cls.getSimpleName(), msg);
        }
    }
}
