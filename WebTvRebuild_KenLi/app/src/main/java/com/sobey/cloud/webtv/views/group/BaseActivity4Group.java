package com.sobey.cloud.webtv.views.group;

import com.sobey.cloud.webtv.core.BaseActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

public class BaseActivity4Group extends BaseActivity implements OnClickListener {
	protected NewHandler handler = null;
	protected Context mContext = null;
	private int type = -1;
	public String TAG;

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		handler = new NewHandler(this);
		mContext = this;
		TAG = this.getClass().getName();
	}

	public void setFinishType(int type) {
		this.type = type;
	}

	/**
	 * 
	 * @param type
	 *            0 左进左出 1 右进右出
	 */
	public void finish(int type) {
		this.finish();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	/** 处理回调消息 */
	protected void handleMessage(Message msg) {
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return 0;
	}
}
