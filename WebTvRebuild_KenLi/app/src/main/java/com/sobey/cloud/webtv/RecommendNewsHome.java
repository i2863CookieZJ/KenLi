package com.sobey.cloud.webtv;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dylan.common.utils.DateParse;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.obj.CatalogObj;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class RecommendNewsHome implements IDragListViewListener {
	private CatalogObj mCatalogObj;
	private ArrayList<JSONObject> mArticles = new ArrayList<JSONObject>();
	private int mPageIndex = 0;
	private int mPageSize = 5;
	private JSONArray mJsonCache = new JSONArray();
	private Context context;
	private Handler handler;
	public RecommendNewsHome(Context context,CatalogObj mCatalogObj,Handler handler){
		this.handler=handler;
		this.mCatalogObj=mCatalogObj;
		this.context=context;
	}	
	public void init() {
		initContent();
	}
	private void initContent() {
		SharedPreferences recommendCache = context.getSharedPreferences("recommend_cache", 0);
		try {
			mJsonCache = new JSONArray(recommendCache.getString("json", null));
		} catch (Exception e) {
			mJsonCache = new JSONArray();
		}
		mPageIndex = 1;
		mArticles.clear();
		parseData(mPageIndex, mJsonCache);
		loadMore();
	}
	private void loadMore() {
		String publishTime = null;
		if (mJsonCache != null && mJsonCache.length() > 0) {
			JSONObject result;
			try {
				result = mJsonCache.optJSONObject(mJsonCache.length() - 1);
				publishTime = DateParse.getDate(0, 0, 0, 1, result.optString("PushTime"), null, null);
			} catch (Exception e) {
				publishTime = null;
			}
		}
		News.pushHomePageRec(publishTime, context, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					if (result != null && result.length() > 0) {
						for (int i = result.length() - 1; i >= 0; i--) {
							JSONObject obj = result.optJSONObject(i);
							if (!TextUtils.isEmpty(obj.optString("PushTime"))) {
								JSONArray jsonArray = new JSONArray(mJsonCache.toString());
								jsonArray.put(obj);
								if (mJsonCache == null || mJsonCache.length() < 1 || !mJsonCache.optJSONObject(mJsonCache.length() - 1).optString("PushTime").equalsIgnoreCase(obj.optString("PushTime"))) {
									if (parseData(mPageIndex, jsonArray)) {
										mJsonCache.put(obj);
										SharedPreferences recommendCache = context.getSharedPreferences("recommend_cache", 0);
										Editor editor = recommendCache.edit();
										editor.putString("json", mJsonCache.toString());
										editor.commit();
									}
								}
							}
						}
					}
				} catch (Exception e) {
				} finally {
				}
			}

			@Override
			public void onNG(String reason) {
			}

			@Override
			public void onCancel() {
			}
		});
	}

	@SuppressWarnings("null")
	private boolean parseData(int pageIndex, JSONArray jsonArray) {
		boolean res = true;
		try {
			int mLastSize = mArticles.size();
			mArticles.clear();
			if (jsonArray == null && jsonArray.length() < 0) {
				return false;
			}
			int minIndex = 0;
			if (mPageSize * pageIndex > jsonArray.length()) {
				minIndex = jsonArray.length();
			} else {
				minIndex = mPageSize * pageIndex;
			}
			for (int i = minIndex - 1; i >= 0; i--) {
				JSONObject result = jsonArray.optJSONObject(jsonArray.length() - 1 - i);
				String publishTime = result.optString("PushTime");
				JSONObject timeObj = new JSONObject();
				timeObj.put("PushTime", publishTime);
				timeObj.put("type", 0);
			//	mArticles.add(timeObj);
				JSONArray array = result.getJSONArray("recList");
				if (array.length() > 1) {
					for (int j = 0; j < array.length(); j++) {
						JSONObject articalObj = array.getJSONObject(j);
						articalObj.put("id", articalObj.optString("ArtilecID"));
						if (j == 0) {
							articalObj.put("type", 2);
						} else {
							articalObj.put("type", 3);
							if (j == array.length() - 1) {
								articalObj.put("divider", false);
							} else {
								articalObj.put("divider", true);
							}
						}
						mArticles.add(articalObj);
					}
				} else {
					JSONObject articalObj = array.getJSONObject(0);
					articalObj.put("id", articalObj.optString("ArtilecID"));
					articalObj.put("type", 1);
					mArticles.add(articalObj);
				}
			}
			if (mArticles!=null&&mArticles.size()>0) {
				List<JSONObject> jList=new ArrayList<JSONObject>();
				for (int i = mArticles.size()-1; i >=0; i--) {
					jList.add(mArticles.get(i));
				}
				mArticles=(ArrayList<JSONObject>) jList;
				System.out.println("推荐list size"+mArticles.size());
				Message msg=new Message();
				msg.obj=mArticles;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {
			res = false;
		} finally {
		}
		return res;
	}

	@Override
	public void onRefresh() {
	}

	@Override
	public void onLoadMore() {
	}
	
}
