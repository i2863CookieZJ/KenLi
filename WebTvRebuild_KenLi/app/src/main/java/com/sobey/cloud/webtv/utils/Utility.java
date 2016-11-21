package com.sobey.cloud.webtv.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.widget.TextView;

public class Utility {

	public static void showMessage(Context ctx, String msg) {
		new AlertDialog.Builder(ctx)
			.setTitle("错误")
			.setCancelable(false)
			.setMessage(msg)
			.setPositiveButton("确定", null)
			.show();
	}
	public static void showMessage(Context ctx, String title, String msg) {
		new AlertDialog.Builder(ctx)
			.setTitle(title)
			.setCancelable(false)
			.setMessage(msg)
			.setPositiveButton("确定", null)
			.show();
	}
	public static void showMessage(Context ctx, String title, String msg, DialogInterface.OnClickListener listener) {
		new AlertDialog.Builder(ctx)
			.setTitle(title)
			.setCancelable(false)
			.setMessage(msg)
			.setPositiveButton("确定", listener)
			.show();
	}
	
	public static boolean isPhoneNo(String phoneNumber) {
		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}
	public static boolean isMobileNO(String mobiles){
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	public static boolean isPersonID(String pid) {
		String regx = "[0-9]{17}x";
		String reg1 = "[0-9]{15}";
		String regex = "[0-9]{18}";
		boolean flag = pid.matches(regx) || pid.matches(reg1) || pid.matches(regex);
		return flag;
	}
	public static boolean isEmail(String email) {
		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		boolean flag = email.matches(regex);
		return flag;
	 }

	public static byte[] readResource(InputStream inputStream) throws Exception{  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
        byte[] array=new byte[1024];  
        int len = 0;  
        while( (len = inputStream.read(array)) != -1){  
        	outputStream.write(array, 0, len);  
        }  
        inputStream.close();  
        outputStream.close();         
        return outputStream.toByteArray();  
    }
	
	private static final String PREFS_NAME = "DEFAULT"; 
	public static String[] readPreference(Context context, String[] keys) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);  
		String[] results = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			results[i] = settings.getString(keys[i], "");
		}
		return results;
	}
	public static String[] readPreference(Context context, String pref, String[] keys) {
		SharedPreferences settings = context.getSharedPreferences(pref, 0);  
		String[] results = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			results[i] = settings.getString(keys[i], "");
		}
		return results;
	}
	public static void savePreference(Context context, String[] keys, String[] values) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);  
		SharedPreferences.Editor editor = settings.edit();  
		for (int i = 0; i < keys.length; i++) {
			editor.putString(keys[i], values[i]);
		}
		editor.commit(); 
	}
	public static void savePreference(Context context, String pref, String[] keys, String[] values) {
		SharedPreferences settings = context.getSharedPreferences(pref, 0);  
		SharedPreferences.Editor editor = settings.edit();  
		for (int i = 0; i < keys.length; i++) {
			editor.putString(keys[i], values[i]);
		}
		editor.commit(); 
	}
	
	public static int getNumber(TextView v, int def) {
		try {
			return Integer.parseInt(v.getText().toString());
		} catch (Exception e) {
			v.setText(String.valueOf(def));
			return def;
		}
	}
	
	public static int getVerCode(Context context) {  
        int verCode = -1;  
        try {  
        	PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			verCode = info.versionCode;
        } catch (Exception e) {  
            e.printStackTrace();
        }  
        return verCode;  
    }
	
	private final static String WeekDayName[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", };
	public static String formatDateWeek(Calendar cal) {
		String week = WeekDayName[cal.get(Calendar.DAY_OF_WEEK) - 1];
		String date = "" + cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "    " + week;
		return date;		
	}
	public static String formatDate(Calendar cal) {
		String date = "" + cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
		return date;		
	}
	/**
	 * 放大/缩小bitmap
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		Bitmap newbmp = null;
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidht = ((float) w / width);
			float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidht, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
					true);
		}
		return newbmp;
	}
	/**
	 * dp转px
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dpToPx(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public static int px2DP(Context context,float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue/scale + 0.5f);
	}
}
