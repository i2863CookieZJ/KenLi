package com.sobey.cloud.webtv;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageCarousel;
import com.appsdk.advancedimageview.AdvancedImageView;
import com.baidu.mobstat.StatService;
import com.dylan.common.animation.AnimationController;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.obj.CacheData;
import com.sobey.cloud.webtv.obj.CacheDataList;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.obj.JsonCacheObj;
import com.sobey.cloud.webtv.obj.ViewHolderLiveNews;
import com.sobey.cloud.webtv.utils.AdBanner;
import com.sobey.cloud.webtv.utils.JsonCache;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class LiveNewsHomeActivity extends BaseActivity implements IDragListViewListener {

	private static final int ItemType_AdBanner = 0;
	private static final int ItemType_Banner = 1;
	private static final int ItemType_Normal = 3;

	private CatalogObj mCatalogObj;
	private BaseAdapter mAdapter;
	private CacheDataList mCacheDatas = new CacheDataList();
	private ArrayList<JSONObject> mArticles = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> mCatalogs = new ArrayList<JSONObject>();
	private String mCatalogId = null;
	private int mPageIndex = 0;
	private int mPageSize = 20;
	private boolean mHasAdBanner = false;
	private boolean mHasImageBanner = false;
	private boolean isLoading = false;
	private LayoutInflater inflater;
	private RelativeLayout mTitleBarLayout;
	private String state;
	private String title;
	private String liveMark;

	@GinInjectView(id = R.id.mListView)
	DragListView mListView;
	@GinInjectView(id = R.id.mTabHost)
	TabHost mTabHost;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.mAdLayout)
	RelativeLayout mAdLayout;
	@GinInjectView(id = R.id.mAdImage)
	AdvancedImageCarousel mAdImage;
	@GinInjectView(id = R.id.mAdCloseBtn)
	ImageButton mAdCloseBtn;
	@GinInjectView(id = R.id.titlebar)
	RelativeLayout titlebar;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_livenews_home;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);

		findViewById(R.id.top_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		try {
			mCatalogObj = MConfig.CatalogList.get(getIntent().getIntExtra("index", 0));
			state = getIntent().getStringExtra("state");
			title = getIntent().getStringExtra("title");
			mCatalogId = state;
			titlebar.setVisibility(View.GONE);
			liveMark = getIntent().getStringExtra("liveMark");

		} catch (Exception e) {
			finish();
		}
		initSliding(false);
		setTitle(title);
		if (mCatalogObj != null)
			setModuleMenuSelectedItem(mCatalogObj.index);

		inflater = LayoutInflater.from(this);

		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setListener(this);
		mListView.setHeaderDividersEnabled(false);

		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderLiveNews viewHolderLiveNews = null;
				int type = getItemViewType(position);

				if (convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_livenews, null);
					viewHolderLiveNews = new ViewHolderLiveNews();
					viewHolderLiveNews.setThumbnail((AdvancedImageView) convertView.findViewById(R.id.image));
					viewHolderLiveNews.setTitle((TextView) convertView.findViewById(R.id.title));
					viewHolderLiveNews.setStartTime((TextView) convertView.findViewById(R.id.starttime));
					viewHolderLiveNews.setEndTime((TextView) convertView.findViewById(R.id.endtime));
					viewHolderLiveNews.setControlBtn((ImageButton) convertView.findViewById(R.id.controlbtn));
					convertView.setTag(viewHolderLiveNews);
					loadViewHolder(position, convertView, viewHolderLiveNews, type);
				} else {
					loadViewHolder(position, convertView, viewHolderLiveNews, type);
				}
				return convertView;
			}

			@Override
			public int getItemViewType(int position) {
				if (position == 0 && mHasAdBanner)
					return ItemType_AdBanner;
				else if (position == 0 && !mHasAdBanner && mHasImageBanner)
					return ItemType_Banner;
				else if (position == 1 && mHasAdBanner && mHasImageBanner)
					return ItemType_Banner;
				else
					return ItemType_Normal;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public int getCount() {
				int count = 0;
				if (mHasAdBanner) {
					count++;
				}
				if (mArticles == null || mArticles.size() < 1)
					return count;
				if (mHasImageBanner) {
					count++;
					if (mArticles.size() < 4) {
						return count;
					} else {
						count += mArticles.size() - 4;
						return count;
					}
				} else {
					count += mArticles.size();
					return count;
				}
			}
		};
		mListView.setAdapter(mAdapter);
		mListView.setAsOuter();
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos = position - 1;
				if (mHasAdBanner) {
					if (pos == 0) {
						// TODO
					} else {
						pos--;
					}
				}
				if (mHasImageBanner) {
					pos += (mArticles.size() < 4) ? (mArticles.size() - 1) : 3;
				}
				JSONObject obj = mArticles.get(pos);
				try {
					openDetailActivity(Integer.valueOf(obj.getString("type")), obj.toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		setupTabBar();

		SharedPreferences adManager = getSharedPreferences("ad_manager", 0);
		if (adManager.getBoolean("banner_enable", false)) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mCatalogObj != null)
						new AdBanner(LiveNewsHomeActivity.this, mCatalogObj.id, mAdLayout, mAdImage, mAdCloseBtn,
								false);
				}
			}, 5000);
		}
	}

	private void loadViewHolder(int position, View convertView, ViewHolderLiveNews viewHolderLiveNews, int type) {
		viewHolderLiveNews = (ViewHolderLiveNews) convertView.getTag();
		switch (type) {
		case ItemType_AdBanner:
			convertView = null;
			break;
		case ItemType_Banner:
			convertView = null;
			break;
		default:
			try {
				int pos = position;
				if (mHasAdBanner) {
					pos--;
				}
				if (mHasImageBanner) {
					pos += (mArticles.size() < 4) ? (mArticles.size() - 1) : 3;
				}
				JSONObject obj = mArticles.get(pos);
				switch (Integer.valueOf(obj.getString("type"))) {
				case MConfig.TypeLive:
					viewHolderLiveNews.getThumbnail().setNetImage(obj.getString("logo"));
					viewHolderLiveNews.getTitle().setText(obj.getString("title"));
					viewHolderLiveNews.getControlBtn().setImageResource(R.drawable.livenews_play_icon);
					JSONArray array = obj.getJSONArray("staticfilepaths");
					if (array != null && array.length() > 0) {
						if (!TextUtils.isEmpty(array.getJSONObject(0).getString("actlist"))) {
							JSONObject object = array.getJSONObject(0).getJSONObject("actlist");
							viewHolderLiveNews.getStartTime().setText(
									object.getString("ctime").substring(0, 5) + "  " + object.getString("ctitle"));
							viewHolderLiveNews.getEndTime().setText(
									object.getString("ntime").substring(0, 5) + "  " + object.getString("ntitle"));
						}
					}
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// convertView = null;
				e.printStackTrace();
			}
			if (convertView != null) {
				convertView.setPadding(0, 0, 0, 0);
			}
			break;
		}
	}

	private void loadMore(final String catalogId, final int pageIndex) {
		isLoading = true;
		if (pageIndex == -1) {
			CacheData cacheData = mCacheDatas.get(catalogId);
			if (cacheData != null) {
				mArticles.clear();
				mArticles = cacheData.getArticles();
				mPageIndex = cacheData.getPageIndex();
				mAdapter.notifyDataSetChanged();
				if (mArticles.size() >= cacheData.getTotal()) {
					mListView.setPullLoadEnable(false);
				} else {
					mListView.setPullLoadEnable(true);
				}
				if (mCatalogId == catalogId) {
					mCloseLoadingIcon();
				}
				return;
			} else {
				JsonCacheObj obj = JsonCache.getInstance().get(catalogId);
				if (obj != null) {
					try {
						JSONObject result = (JSONObject) obj.getContent();
						int total = result.getInt("total");
						JSONArray array = result.getJSONArray("articles");
						if (pageIndex <= 1) {
							mArticles.clear();
						}
						for (int i = 0; i < array.length(); i++) {
							mArticles.add(array.getJSONObject(i));
						}
						mAdapter.notifyDataSetChanged();
						if (mArticles.size() >= total) {
							mListView.setPullLoadEnable(false);
						} else {
							mListView.setPullLoadEnable(true);
						}
						mListView.setPullRefreshEnable(true);
						mListView.stopRefresh();
						mListView.stopLoadMore();
						mCacheDatas.add(new CacheData((pageIndex == -1 ? 1 : pageIndex), catalogId, mArticles, total));
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								isLoading = false;
							}
						}, 500);
						return;
					} catch (Exception e) {
						mListView.setPullRefreshEnable(true);
						mListView.stopRefresh();
						mListView.stopLoadMore();
					} finally {
						if (mCatalogId == catalogId) {
							mCloseLoadingIcon();
						}
					}
				} else {
					mOpenLoadingIcon();
				}
			}
		}
		News.getArticleList(0, catalogId, mPageSize, (pageIndex == -1 ? 1 : pageIndex), this,
				new OnJsonObjectResultListener() {
					@Override
					public void onOK(JSONObject result) {
						try {
							int total = result.getInt("total");
							JSONArray array = result.getJSONArray("articles");
							if (pageIndex <= 1) {
								mArticles.clear();
							}
							for (int i = 0; i < array.length(); i++) {
								mArticles.add(array.getJSONObject(i));
							}
							mAdapter.notifyDataSetChanged();
							if (mArticles.size() >= total) {
								mListView.setPullLoadEnable(false);
							} else {
								mListView.setPullLoadEnable(true);
							}
							mListView.setPullRefreshEnable(true);
							mListView.stopRefresh();
							mListView.stopLoadMore();
							mCacheDatas
									.add(new CacheData((pageIndex == -1 ? 1 : pageIndex), catalogId, mArticles, total));
							JsonCache.getInstance().set(catalogId, "list", result);
						} catch (JSONException e) {
							mListView.setPullRefreshEnable(true);
							mListView.stopRefresh();
							mListView.stopLoadMore();
							if (mCatalogId == catalogId) {
								mCloseLoadingIcon();
							}
						}
						if (mCatalogId == catalogId) {
							mCloseLoadingIcon();
						}
						isLoading = false;
					}

					@Override
					public void onNG(String reason) {
						mListView.setPullRefreshEnable(true);
						mListView.stopRefresh();
						mListView.stopLoadMore();
						isLoading = false;
						if (mCatalogId == catalogId) {
							mCloseLoadingIcon();
						}
					}

					@Override
					public void onCancel() {
						mListView.setPullRefreshEnable(true);
						mListView.stopRefresh();
						mListView.stopLoadMore();
						isLoading = false;
						if (mCatalogId == catalogId) {
							mCloseLoadingIcon();
						}
					}
				});
	}

	public void onTabChange() {
		if (!isLoading) {
			mPageIndex = -1;
			mArticles.clear();
			mAdapter.notifyDataSetChanged();
			loadMore(mCatalogId, mPageIndex);
		}
	}

	@Override
	public void onRefresh() {
		if (!isLoading) {
			mPageIndex = 1;
			loadMore(mCatalogId, mPageIndex);
		}
	}

	@Override
	public void onLoadMore() {
		if (!isLoading) {
			if (mPageIndex == -1) {
				mPageIndex = 2;
			} else {
				mPageIndex++;
			}
			loadMore(mCatalogId, mPageIndex);
		}
	}

	private void setupTabBar() {
		mTabHost.setup();
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
					View view = (View) mTabHost.getTabWidget().getChildAt(i);
					if (mTabHost.getCurrentTab() == i) {
						((TextView) view.findViewById(R.id.text))
								.setTextColor(getResources().getColor(R.color.home_tab_text_focus));
						view.setBackgroundResource(R.drawable.home_tab_background_selected);
						try {
							// mCatalogId = mCatalogs.get(i).getString("id");
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						((TextView) view.findViewById(R.id.text))
								.setTextColor(getResources().getColor(R.color.home_tab_text_normal));
						view.setBackgroundResource(R.drawable.trans);
					}
				}
				onTabChange();
			}
		});
		loadCatalogCache();
	}

	private void loadCatalogCache() {
		JsonCacheObj obj = null;
		if (mCatalogObj != null)
			obj = JsonCache.getInstance().get(mCatalogObj.id);
		if (obj != null) {
			try {
				JSONArray result = (JSONArray) obj.getContent();
				int width = getResources().getDisplayMetrics().widthPixels;
				LayoutParams params = new LayoutParams(width / 6, LayoutParams.MATCH_PARENT);
				params.setMargins(width / 6, 0, width / 6, 0);
				params.gravity = Gravity.CENTER;
				for (int i = 0; i < result.length(); i++) {
					mCatalogs.add(result.getJSONObject(i));
					View view = LayoutInflater.from(LiveNewsHomeActivity.this).inflate(R.layout.layout_tabitem_text,
							null);
					((TextView) view.findViewById(R.id.text)).setText(mCatalogs.get(i).optString("name"));
					view.setLayoutParams(params);
					((TextView) view.findViewById(R.id.text)).setGravity(Gravity.CENTER);
					mTabHost.addTab(mTabHost.newTabSpec(mCatalogs.get(i).optString("name")).setIndicator(view)
							.setContent(R.id.mListView));
				}
				mTabHost.setCurrentTab(1);
				mTabHost.setCurrentTab(0);
				if (result.length() == 1) {
					mTitleBarLayout = (RelativeLayout) findViewById(R.id.titlebar);
					mTitleBarLayout.setVisibility(View.GONE);
				}
			} catch (JSONException e) {
			}
		} else {
			loadCatalog();
		}
	}

	private void loadCatalog() {
		if (mCatalogObj == null)
			return;
		mOpenLoadingIcon();
		News.getCatalogList(mCatalogObj.id, 0, this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					int width = getResources().getDisplayMetrics().widthPixels;
					LayoutParams params = new LayoutParams(width / 6, LayoutParams.MATCH_PARENT);
					params.setMargins(width / 6, 0, width / 6, 0);
					params.gravity = Gravity.CENTER;
					for (int i = 0; i < result.length(); i++) {
						mCatalogs.add(result.getJSONObject(i));
						View view = LayoutInflater.from(LiveNewsHomeActivity.this).inflate(R.layout.layout_tabitem_text,
								null);
						((TextView) view.findViewById(R.id.text)).setText(mCatalogs.get(i).optString("name"));
						view.setLayoutParams(params);
						((TextView) view.findViewById(R.id.text)).setGravity(Gravity.CENTER);
						mTabHost.addTab(mTabHost.newTabSpec(mCatalogs.get(i).optString("name")).setIndicator(view)
								.setContent(R.id.mListView));
					}
					mTabHost.setCurrentTab(1);
					mTabHost.setCurrentTab(0);
					if (result.length() == 1) {
						mTitleBarLayout = (RelativeLayout) findViewById(R.id.titlebar);
						mTitleBarLayout.setVisibility(View.GONE);
					}
					JsonCache.getInstance().set(mCatalogObj.id, "catalog", result);
				} catch (JSONException e) {
					mCloseLoadingIcon();
				}
			}

			@Override
			public void onNG(String reason) {
				mCloseLoadingIcon();
			}

			@Override
			public void onCancel() {
				mCloseLoadingIcon();
			}
		});
		//
		//
		// int width = getResources().getDisplayMetrics().widthPixels;
		// LayoutParams params = new LayoutParams(width / 6,
		// LayoutParams.MATCH_PARENT);
		// params.setMargins(width / 6, 0, width / 6, 0);
		// params.gravity = Gravity.CENTER;
		// View view =
		// LayoutInflater.from(LiveNewsHomeActivity.this).inflate(R.layout.layout_tabitem_text,
		// null);
		// ((TextView) view.findViewById(R.id.text)).setText("电视");
		// view.setLayoutParams(params);
		// ((TextView) view.findViewById(R.id.text)).setGravity(Gravity.CENTER);
		// mTabHost.addTab(mTabHost.newTabSpec("电视").setIndicator(view).setContent(R.id.mListView));
		// View view1 =
		// LayoutInflater.from(LiveNewsHomeActivity.this).inflate(R.layout.layout_tabitem_text,
		// null);
		// ((TextView) view1.findViewById(R.id.text)).setText("广播");
		// view1.setLayoutParams(params);
		// ((TextView)
		// view1.findViewById(R.id.text)).setGravity(Gravity.CENTER);
		// mTabHost.addTab(mTabHost.newTabSpec("广播").setIndicator(view1).setContent(R.id.mListView));
		// mTabHost.setCurrentTab(1);
		// mTabHost.setCurrentTab(0);
	}

	private void openDetailActivity(int type, String information) {
		switch (type) {
		case MConfig.TypeLive:
			Intent intent = new Intent(LiveNewsHomeActivity.this, LiveNewsDetailActivity.class);
			intent.putExtra("information", information);
			intent.putExtra("liveMark", liveMark);
			LiveNewsHomeActivity.this.startActivity(intent);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.hide(mListView);
			animationController.show(mLoadingIconLayout);
		}
	}

	private void mCloseLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mListView);
			animationController.fadeOut(mLoadingIconLayout, 1000, 0);
		}
	}

	@Override
	public void onResume() {
		StatService.onResume(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		StatService.onPause(this);
		super.onPause();
	}
}
