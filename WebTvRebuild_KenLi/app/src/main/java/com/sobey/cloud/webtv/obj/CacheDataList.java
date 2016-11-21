package com.sobey.cloud.webtv.obj;

import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONObject;

public class CacheDataList {
	LinkedList<CacheData> mCacheDatas = new LinkedList<CacheData>();

	public void clear() {
		mCacheDatas.clear();
	}
	
	public int size() {
		return mCacheDatas.size();
	}

	public void add(CacheData cacheData) {
		for(int i = 0; i < mCacheDatas.size(); i++) {
			if(mCacheDatas.get(i).getCatalogId() == cacheData.getCatalogId()) {
				mCacheDatas.set(i, new CacheData(cacheData.getPageIndex(), cacheData.getCatalogId(), new ArrayList<JSONObject>(cacheData.getArticles()), cacheData.getTotal()));
			}
		}
		mCacheDatas.addLast(new CacheData(cacheData.getPageIndex(), cacheData.getCatalogId(), new ArrayList<JSONObject>(cacheData.getArticles()), cacheData.getTotal()));
	}

	public CacheData get(String catalogId) {
		for(int i = 0; i < mCacheDatas.size(); i++) {
			if(mCacheDatas.get(i).getCatalogId() == catalogId) {
				return (new CacheData(mCacheDatas.get(i).getPageIndex(), mCacheDatas.get(i).getCatalogId(), new ArrayList<JSONObject>(mCacheDatas.get(i).getArticles()), mCacheDatas.get(i).getTotal()));
			}
		}
		return null;
	}
}
