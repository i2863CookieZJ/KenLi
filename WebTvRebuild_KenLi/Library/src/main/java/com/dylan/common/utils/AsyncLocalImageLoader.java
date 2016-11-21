package com.dylan.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.util.LruCache;
import android.util.Log;

public class AsyncLocalImageLoader {
	private static final String TAG = "AsyncLocalImageLoader";
	private ThreadPoolExecutor mPoolExecutor;
	private LruCache<String, Bitmap> imageCache;
	private Handler mMainThreadHandler;

	public AsyncLocalImageLoader(Context context) {
		this(context, 5, 20);
	}

	public AsyncLocalImageLoader(Context context, int maxPoolSize, int queueSize) {
		this(context, 2, maxPoolSize, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueSize), new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	public AsyncLocalImageLoader(Context context, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int cacheSize = 1024 * 1024 * memClass / 8;
		imageCache = new LruCache<String, Bitmap>(cacheSize) {
			protected int sizeOf(String key, Bitmap value) {
				return value.getHeight() * value.getWidth() * 4;
			};
		};
		mPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		mMainThreadHandler = new Handler(Looper.getMainLooper());
	}

	public void loadDrawable(String imageFilePath, long imageFlag, LocalImageCallback localImageCallback) {
		if (localImageCallback == null)
			return;
		if (imageCache.get(imageFilePath) != null) {
			Bitmap bitmap = imageCache.get(imageFilePath);
			if (bitmap != null) {
				localImageCallback.onLoaded(bitmap, imageFlag);
				return;
			}
		}

		LoadImageTask task = new LoadImageTask(imageFilePath, imageFlag, this, mMainThreadHandler, localImageCallback);
		mPoolExecutor.execute(task);
	}

	public void shutdown() {
		mPoolExecutor.shutdown();
		imageCache.evictAll();
	}

	private void cache(String filePath, Bitmap bitmap) {
		imageCache.put(filePath, bitmap);
	}

	private static final class LoadImageTask implements Runnable {
		private Handler mHandler;
		private LocalImageCallback mCallback;
		private AsyncLocalImageLoader mLoader;
		private String mPath;
		private long mImageFlag;

		public LoadImageTask(String imgPath, long imageFlag, AsyncLocalImageLoader loader, Handler handler, LocalImageCallback imageCallback) {
			Log.d(TAG, "start a task for load image:" + imgPath);
			this.mHandler = handler;
			this.mPath = imgPath;
			this.mImageFlag = imageFlag;
			this.mLoader = loader;
			this.mCallback = imageCallback;
		}

		@Override
		public void run() {
			try {
				if (mPath.contains("mp4") || mPath.contains("wmv") || mPath.contains("avi") || mPath.contains("3gp")) {
					final Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mPath, Thumbnails.MICRO_KIND);
					if (bitmap != null) {
						mLoader.cache(mPath, bitmap);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mCallback.onLoaded(bitmap, mImageFlag);
							}
						});
					}
				} else {
					final Bitmap bitmap = loadLocalFile(mPath);
					if (bitmap != null) {
						mLoader.cache(mPath, bitmap);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mCallback.onLoaded(bitmap, mImageFlag);
							}
						});
					}
				}
			} catch (final Exception e) {
				mLoader.cache(mPath, null);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mCallback.onError(e, mImageFlag);
					}
				});
			} catch (OutOfMemoryError e) {
				mLoader.cache(mPath, null);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mCallback.onError(new Exception("OutOfMemoryError"), mImageFlag);
					}
				});
				System.gc();
			}
		}

		private Bitmap loadLocalFile(String filePath) {
			try {
				File file = new File(filePath);
				if (!file.exists())
					return null;
				FileInputStream fis = new FileInputStream(file);
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = 2;
				Bitmap res = BitmapFactory.decodeStream(fis, null, opts);
				if (res != null)
					return res;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static interface LocalImageCallback {
		public void onLoaded(Bitmap bitmap, long imageFlag);

		public void onError(Exception e, long imageFlag);
	}
}
