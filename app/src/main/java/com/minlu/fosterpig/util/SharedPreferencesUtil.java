package com.minlu.fosterpig.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

	// 注意：在获取SharedPreferences对象时，如果getSharedPreferences方法的参数一是已经存在的文件时，那就不重新创建该文件，而是打开该文件存储取出数据

	private static SharedPreferences spf;

	// 存储布尔值
	public static void saveboolean(Context context, String key, boolean value) {
		if (spf == null)
			spf = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		spf.edit().putBoolean(key, value).commit();
	}

	public static boolean getboolean(Context context, String key,
									 boolean defvalue) {
		if (spf == null)
			spf = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return spf.getBoolean(key, defvalue);
	}

	// 存int值
	public static void saveint(Context context, String key, int value) {
		if (spf == null)
			spf = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		spf.edit().putInt(key, value).commit();
	}

	public static int getint(Context context, String key, int defvalue) {
		if (spf == null)
			spf = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return spf.getInt(key, defvalue);
	}

	// 存String值
	public static void saveStirng(Context context, String key, String value) {
		if (spf == null)
			spf = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		spf.edit().putString(key, value).commit();
	}

	public static String getString(Context context, String key, String defvalue) {
		if (spf == null)
			spf = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return spf.getString(key, defvalue);
	}


	// 存Long值
	public static void saveLong(Context context, String key, long value) {
		if (spf == null)
			spf = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		spf.edit().putLong(key, value).commit();
	}

	public static long getLong(Context context, String key, long defvalue) {
		if (spf == null)
			spf = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return spf.getLong(key, defvalue);
	}


}
