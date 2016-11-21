package com.sobey.cloud.webtv.broadcast;

import com.sobey.cloud.webtv.utils.SobeyConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class PostSubjectSuccessReciever extends BroadcastReceiver {
	
	private Handler mHandler;
	public PostSubjectSuccessReciever(Handler handler){
		this.mHandler = handler;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(SobeyConstants.ACTION_POST_SUBJECT.equals(action)){
			mHandler.sendEmptyMessage(0);
		}
	}

}
