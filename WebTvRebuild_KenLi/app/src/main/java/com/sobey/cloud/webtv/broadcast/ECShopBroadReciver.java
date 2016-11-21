package com.sobey.cloud.webtv.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ECShopBroadReciver extends BroadcastReceiver {
	public static final String ECSHOP_BROAD = "ECSHOP_BROAD";
	public static final String SHOW_ACTION_BAR = "SHOW_ACTION_BAR";
	public static final String HIDE_ACTION_BAR = "HIDE_ACTION_BAR";
	/**
	 * 登录后等执行那几个script标记里面的url后再作跳转
	 */
	public static final String REDIRECT_AFTERLOGIN = "REDIRECT_AFTERLOGIN";
	public ReciveHandle handle;

	@Override
	public void onReceive(Context arg0, Intent intent) {
		if (handle != null)
			handle.handle(intent);
	}

	public static interface ReciveHandle {
		void handle(Intent intent);
	}
}
