package com.sobey.cloud.webtv.app;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sobey.cloud.webtv.utils.Display;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.support.multidex.MultiDex;
import android.view.WindowManager;
import cn.jpush.android.api.JPushInterface;

public class ExceptionApplication extends CommonApp {
	public static ExceptionApplication app;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		ExceptionHandler exceptionHandler = ExceptionHandler.getInstance();
		exceptionHandler.init(getApplicationContext());
		initImageLoader(getApplicationContext());

		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display.ScreenWidth = windowManager.getDefaultDisplay().getWidth();
		Display.ScreenHeight = windowManager.getDefaultDisplay().getHeight();
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}


	public void initImageLoader(Context context) {

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.defaultDisplayImageOptions(defaultOptions);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		// config.writeDebugLogs(); // Remove for release app
		ImageLoader.getInstance().init(config.build());
	}
}
