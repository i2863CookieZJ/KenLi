package com.sobey.cloud.webtv.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 销毁RadioPlayer实例的类
 * 
 * @author zouxudong
 *
 */
public class DestoryRadioInstanceReciver extends BroadcastReceiver {

	public static final String DestoryRadioInstance = "DestoryRadioInstance";
	public static final String Destory = "Destory";
	public static ReciveHandle handle;

	public DestoryRadioInstanceReciver() {
	}

	@Override
	public void onReceive(Context arg0, Intent intent) {
		if (handle != null)
			handle.handle(intent);
	}

	public static interface ReciveHandle {
		void handle(Intent intent);
	}

}
