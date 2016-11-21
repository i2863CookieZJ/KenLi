package com.sobey.cloud.webtv;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.baidu.mobstat.StatService;
import com.dylan.common.animation.AnimationController;
import com.dylan.common.utils.DateParse;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.obj.CacheData;
import com.sobey.cloud.webtv.obj.CacheDataList;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.obj.JsonCacheObj;
import com.sobey.cloud.webtv.obj.ViewHolderTopicNews;
import com.sobey.cloud.webtv.utils.JsonCache;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CouponNewsHomeActivity extends BaseActivity implements IDragListViewListener {
	private static final int ItemType_AdBanner = 0;
	private static final int ItemType_Banner = 1;
	private static final int ItemType_Normal = 2;

	private CatalogObj mCatalogObj;
	private BaseAdapter mAdapter;
	private ArrayList<JSONObject> mArticles = new ArrayList<JSONObject>();
	private int mPageIndex = 0;
	private int mPageSize = 10;
	private boolean mHasAdBanner = false;
	private boolean mHasImageBanner = false;
	private boolean isLoading = false;
	private LayoutInflater inflater;
	private CacheDataList mCacheDatas = new CacheDataList();
	private String mCatalogId = null;
	private JSONArray mResult;

	@GinInjectView(id = R.id.mListView)
	DragListView mListView;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_couponnews_home;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		init();
	}

	public void init() {
		try {
			mCatalogObj = MConfig.CatalogList.get(getIntent().getIntExtra("index", 0));
			initSliding(false);
			setTitle(mCatalogObj.name);
			setModuleMenuSelectedItem(mCatalogObj.index);
			mCatalogId = "coupon";
			if (TextUtils.isEmpty(mCatalogId)) {
				finish();
			}
		} catch (Exception e) {
			if (e != null) {
				Log.i("dzy", e.toString());
			}
			finish();
		}

		inflater = LayoutInflater.from(this);
		initContent();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.hide(mListView);
			animationController.show(mLoadingIconLayout);
		}
	}

	public void mCloseLoadingIcon() {
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

	private void initContent() {
		mOpenLoadingIcon();
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setListener(this);
		mListView.setHeaderColor(0xfff9f9f9);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterBackgroundColor(0xfff9f9f9);
		mListView.setBackgroundColor(0xfff9f9f9);

		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderTopicNews viewHolderTopicNews = null;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_couponnews, null);
					viewHolderTopicNews = new ViewHolderTopicNews();
					viewHolderTopicNews.setTitle((TextView) convertView.findViewById(R.id.title));
					viewHolderTopicNews.setSummary((TextView) convertView.findViewById(R.id.summary));
					viewHolderTopicNews.setDisplayNum((TextView) convertView.findViewById(R.id.displaynum));
					viewHolderTopicNews.setImage((AdvancedImageView) convertView.findViewById(R.id.image));
					convertView.setTag(viewHolderTopicNews);
					loadViewHolder(position, convertView, viewHolderTopicNews);
				} else {
					loadViewHolder(position, convertView, viewHolderTopicNews);
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
					Intent intent = new Intent(CouponNewsHomeActivity.this, CouponNewsDetailActivity.class);
					intent.putExtra("information", obj.toString());
					CouponNewsHomeActivity.this.startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mPageIndex = 1;
		mArticles.clear();
		mAdapter.notifyDataSetChanged();
		loadMore(mCatalogId, mPageIndex);
	}

	private void loadViewHolder(int position, View convertView, ViewHolderTopicNews viewHolderTopicNews) {
		viewHolderTopicNews = (ViewHolderTopicNews) convertView.getTag();
		try {
			int pos = position;
			if (mHasAdBanner) {
				pos--;
			}
			if (mHasImageBanner) {
				pos += (mArticles.size() < 4) ? (mArticles.size() - 1) : 3;
			}
			JSONObject obj = mArticles.get(pos);
			viewHolderTopicNews.getTitle().setText(obj.optString("Title"));
			viewHolderTopicNews.getSummary().setText(obj.optString("Detail"));
			viewHolderTopicNews.getDisplayNum()
					.setText(DateParse.getDate(0, 0, 0, 0, obj.optString("StartTime"), "yyyy-MM-dd HH:mm:ss",
							"yy-MM-dd") + "至"
					+ DateParse.getDate(0, 0, 0, 0, obj.optString("EndTime"), "yyyy-MM-dd HH:mm:ss", "yy-MM-dd"));
			viewHolderTopicNews.getImage().setNetImage(obj.optString("ListFlg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadMore(final String catalogId, final int pageIndex) {
		isLoading = true;
		if (pageIndex == 1) {
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
				isLoading = false;
				return;
			} else {
				JsonCacheObj obj = JsonCache.getInstance().get(catalogId);
				if (obj != null) {
					try {
						JSONArray result = (JSONArray) obj.getContent();
						int total = result.length();
						if (total > 0) {
							mResult = result;
						}
						mArticles.clear();
						for (int i = 0; i < (total > mPageSize ? mPageSize : total); i++) {
							mArticles.add(result.getJSONObject(i));
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
		} else if (pageIndex > 1) {
			try {
				int total = mResult.length();
				int max = pageIndex * mPageSize;
				for (int i = (pageIndex - 1) * mPageSize; i < (total > max ? max : total); i++) {
					mArticles.add(mResult.getJSONObject(i));
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
				isLoading = false;
			}
		}
		News.getCouponList(CouponNewsHomeActivity.this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					int total = result.length();
					if (total > 0) {
						mResult = result;
					}
					mArticles.clear();
					for (int i = 0; i < (total > mPageSize ? mPageSize : total); i++) {
						mArticles.add(result.getJSONObject(i));
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
					if (pageIndex == -1) {
						JsonCache.getInstance().set(catalogId, "list", result);
					}
				} catch (Exception e) {
					mListView.setPullRefreshEnable(true);
					mListView.stopRefresh();
					mListView.stopLoadMore();
				} finally {
					if (mCatalogId == catalogId) {
						mCloseLoadingIcon();
					}
					isLoading = false;
				}
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

	@Override
	public void onRefresh() {
		if (!isLoading) {
			mPageIndex = -1;
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
}
