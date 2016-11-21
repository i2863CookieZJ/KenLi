package com.higgses.griffin.core.app;

import java.util.List;

import com.higgses.griffin.annotation.app.GinInjector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Fragment
 */
public class GinFragment extends Fragment {
	private Activity mActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getApp().getInjector().inject(this, getView());
		initActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// GinNetworkStateReceiver.registerObserver(new GinNetChangeObserver()
		// {
		// @Override
		// public void onDisConnect()
		// {
		// super.onDisConnect();
		// GinFragment.this.onDisConnect();
		// }
		//
		// @Override
		// public void onConnect(NetType type)
		// {
		// super.onConnect(type);
		// GinFragment.this.onConnect(type);
		// }
		// });
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		List<Fragment> fragments = getChildFragmentManager().getFragments();
		for (Fragment fragment : fragments) {
			GinFragment ginFragment = (GinFragment) fragment;
			ginFragment.onGinActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * 修复系统中Fragment嵌套onActivityResult不调用的BUG,在最内Fragment使用：getParentFragment().
	 * startActivityForResult() 方式启动Activity，然后这个方法会代替onActivityResult被回调
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 *
	 * @see #onActivityResult(int, int, android.content.Intent)
	 */
	public void onGinActivityResult(int requestCode, int resultCode, Intent data) {

	}

	// /**
	// * 网络没连接上
	// */
	// public void onDisConnect()
	// {
	//
	// }
	//
	// /**
	// * 网络连接上
	// *
	// * @param type
	// */
	// public void onConnect(NetType type)
	// {
	//
	// }

	/**
	 * 初始化
	 */
	private void initActivity() {
		initInjector();
	}

	/**
	 * 初始化注入器
	 */
	private void initInjector() {
		GinInjector injector = getApp().getInjector();
		injector.injectResource(this);
		injector.inject(this, getView());
	}

	public GinApplication getApp() {
		return (GinApplication) mActivity.getApplication();
	}
}
