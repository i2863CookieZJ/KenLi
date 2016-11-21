package com.sobey.cloud.webtv.utils;

import java.util.HashMap;
import java.util.Map;

import com.sobey.cloud.webtv.obj.JsonCacheObj;

public class JsonCache {
	private static JsonCache mInstance;
	private static Map<String, JsonCacheObj> mCache;

	public static JsonCache getInstance() {
		if (mInstance == null) {
			mInstance = new JsonCache();
		}
		checkCache();
		return mInstance;
	}

	public JsonCacheObj get(String id) {
		checkCache();
		JsonCacheObj obj = mCache.get(id);
		if (obj == null || obj.getDeadLine() < System.currentTimeMillis()) {
			return null;
		} else {
			return obj;
		}
	}

	public void set(String id, JsonCacheObj obj) {
		checkCache();
		if (obj.getType().equalsIgnoreCase("catalog")) {
			obj.setDeadLine(System.currentTimeMillis() + MConfig.CatalogDuration);
		} else {
			obj.setDeadLine(System.currentTimeMillis() + MConfig.ListDuration);
		}
		mCache.put(id, obj);
	}

	public void set(String id, String type, Object content) {
		checkCache();
		JsonCacheObj obj = new JsonCacheObj(id, type, content);
		if (obj.getType().equalsIgnoreCase("catalog")) {
			obj.setDeadLine(System.currentTimeMillis() + MConfig.CatalogDuration);
		} else {
			obj.setDeadLine(System.currentTimeMillis() + MConfig.ListDuration);
		}
		mCache.put(id, obj);
	}

	public void clear() {
		try {
			mCache.clear();
		} catch (Exception e) {
		}
	}

	public void destory() {
		clear();
		mCache = null;
		mInstance = null;
	}

	private static void checkCache() {
		if (mCache == null) {
			mCache = new HashMap<String, JsonCacheObj>();
		}
	}
}
