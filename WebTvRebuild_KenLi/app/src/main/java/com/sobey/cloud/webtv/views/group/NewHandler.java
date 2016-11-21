package com.sobey.cloud.webtv.views.group;

import android.os.Handler;
import android.os.Message;

public class NewHandler extends Handler {
	BaseActivity4Group activity;

	public NewHandler(BaseActivity4Group activity) {
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message msg) {
		activity.handleMessage(msg);
	}
}