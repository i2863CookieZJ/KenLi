package com.sobey.cloud.webtv.app;

import com.sobey.cloud.webtv.core.ContextApplication;
import com.sobey.cloud.webtv.model.provide.ServerData;

import android.app.Service;
import android.os.Vibrator;
import android.widget.TextView;

public class CommonApp extends ContextApplication implements Runnable {

	private static final String TAG = "CommonApp";

	public TextView mLocationResult, logMsg;
	public TextView trigger, exit;
	public Vibrator mVibrator;

	/**
	 * HixApp对象
	 */
	private static CommonApp mInstance = null;

	@Override
	public void onCreate() {
		super.onCreate();

		ServerData.init(this);

		mVibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
		mInstance = this;
	}

	public void logMsg(String str) {
		try {
			if (mLocationResult != null) {
				mLocationResult.setText(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static CommonApp getInstance() {
		return mInstance;
	}

	@Override
	public void run() {

	}
}
