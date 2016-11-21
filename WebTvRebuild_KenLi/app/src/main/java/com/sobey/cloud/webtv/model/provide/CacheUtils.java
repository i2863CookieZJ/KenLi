package com.sobey.cloud.webtv.model.provide;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.higgses.griffin.cache.CacheParams;
import com.higgses.griffin.cache.FileCache;
import com.higgses.griffin.cache.ImageCache;
import com.higgses.griffin.cache.ImageFetcher;
import com.sobey.cloud.webtv.config.AppConfig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * 缓存工具类 Created by higgses on 14-5-13.
 */
public class CacheUtils {
	private final static int FILE = 0;
	private final static int IMAGE = 1;
	private static CacheParams fileCacheParams;
	private static CacheParams imageCacheParams;
	private static FileCache mFileCache;
	/**
	 * 图片抓取
	 */
	private static ImageFetcher mImageFetcher;

	/**
	 * 返回缓存参数
	 *
	 * @param context
	 * @param type
	 *
	 * @return
	 */
	private static CacheParams getCacheParams(Context context, int type) {
		CacheParams params = null;
		switch (type) {
		case FILE:
			params = fileCacheParams;
			break;
		case IMAGE:
			params = imageCacheParams;
			break;
		}
		if (params == null) {
			synchronized (CacheUtils.class) {
				switch (type) {
				case FILE:
					params = new CacheParams(context, AppConfig.DATA_CACHE_DIR);
					fileCacheParams = params;
					break;
				case IMAGE:
					params = new CacheParams(context, AppConfig.IMAGE_CACHE_DIR);
					imageCacheParams = params;
					break;
				}
			}
		}
		return params;
	}

	/**
	 * 初始化文件缓存
	 *
	 * @param context
	 */
	private static void initFileCache(Context context) {
		if (mFileCache == null) {
			synchronized (CacheUtils.class) {
				if (mFileCache == null) {
					mFileCache = new FileCache(getCacheParams(context, FILE));
				}
			}
		}
	}

	/**
	 * 添加数据到缓存
	 *
	 * @param context
	 * @param url
	 * @param data
	 */
	public static void addFileCache(Context context, String url, String data) {
		if (mFileCache == null) {
			initFileCache(context);
		}
		mFileCache.addBufferToCache(url, data.getBytes());
	}

	/**
	 * 读取缓存数据
	 *
	 * @param context
	 * @param url
	 *
	 * @return
	 */
	public static byte[] loadFileData(Context context, String url) {
		if (mFileCache == null) {
			initFileCache(context);
		}
		byte[] cache = mFileCache.getBufferFromMemoryCache(url);
		if (cache == null || cache.length == 0) {
			cache = mFileCache.getBufferFromDiskCache(url);
		}
		return cache;
	}

	/**
	 * 读取缓存
	 *
	 * @param context
	 * @param url
	 * @param classType
	 * @param <T>
	 *
	 * @return
	 */
	public static <T> T loadDataEntity(Context context, String url, Class<? extends T> classType) {
		Gson gson = new Gson();
		byte[] data = loadFileData(context, url);
		String json = "";
		if (data != null) {
			json = new String(data);
		}
		return gson.fromJson(json, classType);
	}

	/**
	 * 读取缓存
	 *
	 * @param context
	 * @param url
	 * @param type
	 *
	 * @return
	 */
	public static <T> T loadDataEntity(Context context, String url, Type type) {
		Gson gson = new Gson();
		byte[] data = loadFileData(context, url);
		String json = "";
		if (data != null) {
			json = new String(data);
		}
		return gson.fromJson(json, type);
	}

	/**
	 * 读取缓存
	 *
	 * @param context
	 * @param url
	 *
	 * @return
	 */
	public static String loadStringData(Context context, String url) {
		Gson gson = new Gson();
		byte[] data = loadFileData(context, url);
		String json = "";
		if (data != null) {
			json = new String(data);
		}
		return json;
	}

	public static Bitmap getImageFromCache(Context context, String data) {
		ImageCache cache = ImageCache.getInstance(getCacheParams(context, IMAGE));
		BitmapDrawable drawable = cache.getBitmapFromMemCache(data);
		Bitmap bitmap = null;
		if (drawable != null) {
			bitmap = drawable.getBitmap();
		}
		if (bitmap == null) {
			bitmap = cache.getBitmapFromDiskCache(data);
		}

		return bitmap;
	}
}
