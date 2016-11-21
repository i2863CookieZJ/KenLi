package com.dylan.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dylan.common.digest.MD5;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class AsyncImageLoader {
	private static final String TAG = "AsyncImageLoader";
	private ThreadPoolExecutor mPoolExecutor;
	private HashMap<String, SoftReference<Bitmap>> imageCache;
	private Handler mMainThreadHandler;

	public AsyncImageLoader() {
		this(5, 20);
	}

	public AsyncImageLoader(int maxPoolSize, int queueSize) {
		this(2, maxPoolSize, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueSize), new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	public AsyncImageLoader(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
		mPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		mMainThreadHandler = new Handler(Looper.getMainLooper());
	}

	public void loadDrawable(Context context, String imageUrl, long imageFlag, ImageCallback imageCallback) {
		loadDrawable(context, imageUrl, imageFlag, false, imageCallback);
	}

	public void loadDrawable(Context context, String imageUrl, long imageFlag, boolean useCache, ImageCallback imageCallback) {
		if (imageCallback == null)
			return;

		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
			Bitmap bitmap = softReference.get();
			if (bitmap != null) {
				imageCallback.onLoaded(bitmap, imageFlag);
				return;
			}
		}

		LoadImageTask task = new LoadImageTask(imageUrl, imageFlag, useCache ? context : null, this, mMainThreadHandler, imageCallback);
		mPoolExecutor.execute(task);
	}

	public void shutdown() {
		mPoolExecutor.shutdown();
		imageCache.clear();
	}

	private void cache(String url, Bitmap bitmap) {
		imageCache.put(url, new SoftReference<Bitmap>(bitmap));
	}

	private static final class LoadImageTask implements Runnable {
		private Handler mHandler;
		private ImageCallback mCallback;
		private AsyncImageLoader mLoader;
		private String mPath;
		private long mImageFlag;
		private Context mContext;

		public LoadImageTask(String imgPath, long imageFlag, Context context, AsyncImageLoader loader, Handler handler, ImageCallback imageCallback) {
			Log.d(TAG, "start a task for load image:" + imgPath);
			this.mHandler = handler;
			this.mPath = imgPath;
			this.mImageFlag = imageFlag;
			this.mLoader = loader;
			this.mContext = context;
			this.mCallback = imageCallback;
		}

		@Override
		public void run() {
			try {
				String[] localFile = null;
				if (mContext != null) {
					localFile = localFileForUrl(mContext, mPath);
					if (localFile.length > 0) {
						final Bitmap drawable = loadLocalFile(localFile);
						if (drawable != null) {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									Log.d(TAG, "load image success from local:" + mPath);
									mCallback.onLoaded(drawable, mImageFlag);
								}
							});
							return;
						}
					}
				}

				URL url = new URL(mPath);
				URLConnection conn = url.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] cache = new byte[1024 * 10];
				int len = 0;
				while ((len = is.read(cache)) != -1) {
					baos.write(cache, 0, len);
				}
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = 2;
				final Bitmap bmp = BitmapFactory.decodeStream(bais, null, opts);
				mLoader.cache(mPath, bmp);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Log.d(TAG, "load image success:" + mPath);
						mCallback.onLoaded(bmp, mImageFlag);
					}
				});
				tryWriteFile(baos, localFile);
			} catch (final Exception e) {
				Log.e(TAG, e.getMessage() + "", e);
				mLoader.cache(mPath, null);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Log.d(TAG, "load image failed:" + mPath);
						mCallback.onError(e, mImageFlag);
					}
				});
			} catch (OutOfMemoryError e) {
				Log.w("dylan", "mem: OutOfMemoryError");
				System.gc();
			}
		}

		private Bitmap loadLocalFile(String[] files) {
			for (String path : files) {
				try {
					File file = new File(path);
					if (!file.exists())
						continue;
					FileInputStream fis = new FileInputStream(file);
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inSampleSize = 2;
					Bitmap res = BitmapFactory.decodeStream(fis, null, opts);
					if (res != null)
						return res;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		private String[] localFileForUrl(Context context, String url) {
			String cache = Environment.getDownloadCacheDirectory().getAbsolutePath() + "/image/";
			String data = null;
			try {
				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					String external = Environment.getExternalStorageDirectory().getAbsolutePath();
					PackageManager pm = context.getPackageManager();
					PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
					external += "/android/data/";
					external += info.packageName;
					external += "/image/";
					File file = new File(external);
					if (!file.exists())
						file.mkdirs();
					if (file.exists())
						data = external;
				}
			} catch (NameNotFoundException e) {
			}
			File file = new File(cache);
			if (!file.exists())
				file.mkdirs();
			String extName = null;
			int pos = url.lastIndexOf('.');
			if (pos >= 0)
				extName = url.substring(pos);
			String fileName = new MD5(url).asHex();
			if (data != null) {
				return new String[] { data + fileName + extName, cache + fileName + extName };
			} else {
				return new String[] { cache + fileName + extName };
			}
		}

		private void tryWriteFile(ByteArrayOutputStream baos, String[] localFile) {
			if (localFile == null || localFile.length < 1)
				return;
			for (String path : localFile) {
				try {
					ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
					File file = new File(path);
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					byte[] data = new byte[1024];
					int len = 0;
					while ((len = is.read(data)) != -1) {
						fos.write(data, 0, len);
					}
					fos.flush();
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static interface ImageCallback {
		public void onLoaded(Bitmap bitmap, long imageFlag);

		public void onError(Exception e, long imageFlag);
	}
}
