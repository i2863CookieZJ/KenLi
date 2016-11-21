package com.sobey.cloud.webtv.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Toast工具类
 */
public class ToastUtil {
	/**
	 * Toast对象
	 */
	private static Toast toast;

	/**
	 * 关闭时间(毫秒为单位,默认500)
	 */
	private static int closeTime = 500;

	/**
	 * 显示Toast消息并且关闭
	 *
	 * @param context
	 * @param toastText
	 * @param isShort
	 */
	public static void showToast(Context context, String toastText, boolean isShort) {
		if (toast == null) {
			toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
		} else {
			toast.setText(toastText);
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.show();
		if (isShort) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					toast.cancel();
				}
			}, closeTime);
		}
	}

	/**
	 * 显示Toast消息并且关闭
	 *
	 * @param context
	 * @param toastText
	 */
	public static void showToast(Context context, String toastText) {
		showToast(context, toastText, false);
	}
}
