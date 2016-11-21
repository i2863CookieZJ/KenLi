package com.sobey.cloud.webtv.broadcast;


import com.sobey.cloud.webtv.utils.SobeyConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

public class LogStateChangeReceiver extends BroadcastReceiver {
	private final String TAG = this.getClass().getName();
	private Handler mHandler;
	
	public LogStateChangeReceiver(Handler handler){
		this.mHandler = handler;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive-->");
		String action = intent.getAction();
		if(!TextUtils.isEmpty(action) && action.equals(SobeyConstants.ACTION_LOG_STATE_CHANGE)){
			mHandler.sendEmptyMessage(SobeyConstants.CODE_FOR_LOG_STATE_CHANGE);
		}
	}

}
