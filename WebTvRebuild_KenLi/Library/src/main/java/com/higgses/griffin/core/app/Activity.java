package com.higgses.griffin.core.app;

import com.higgses.griffin.annotation.app.GinInjector;
import com.higgses.griffin.core.inf.GinIActivity;
import com.higgses.griffin.netstate.utils.GinUNetWork.NetType;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * 框架Activity，支持v4的FragmentActivity
 */
@SuppressLint("NewApi")
public class Activity extends FragmentActivity implements GinIActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		notifyApplicationActivityCreating();
		super.onCreate(savedInstanceState);
		getApp().getAppManager().addActivity(this);
		initActivity();
		notifyApplicationActivityCreated();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		getApp().getInjector().inject(this);
	}

	private void initActivity() {
		initInjector();
	}

	/**
	 * 初始化注入器
	 */
	private void initInjector() {
		GinInjector injector = getApp().getInjector();
		injector.injectResource(this);
		injector.inject(this);
	}

	@Override
	public void onBackPressed() {
		getApp().onBackPressed();
	}

	public void finishActivity() {
		getApp().finishActivity();
	}

	public GinApplication getApp() {
		return (GinApplication) getApplication();
	}

	private void notifyApplicationActivityCreating() {
		getApp().onActivityCreating(this);
	}

	private void notifyApplicationActivityCreated() {
		getApp().onActivityCreated(this);
	}

	@Override
	public void onConnect(NetType type) {

	}

	@Override
	public void onDisConnect() {

	}
}
