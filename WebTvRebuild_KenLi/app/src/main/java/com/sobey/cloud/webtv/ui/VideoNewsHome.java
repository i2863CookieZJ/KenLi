package com.sobey.cloud.webtv.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageCarousel;
import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.HomeActivity;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.obj.CacheData;
import com.sobey.cloud.webtv.obj.CacheDataList;
import com.sobey.cloud.webtv.obj.JsonCacheObj;
import com.sobey.cloud.webtv.obj.ViewHolderVideoNews;
import com.sobey.cloud.webtv.utils.JsonCache;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VideoNewsHome implements IDragListViewListener {
	protected static final int ItemType_AdBanner = 0;
	protected static final int ItemType_Banner = 1;
	protected static final int ItemType_Normal = 2;

	protected Context mContext;
	protected DragListView mListView;
	protected BaseAdapter mAdapter;
	protected CacheDataList mCacheDatas = new CacheDataList();
	protected ArrayList<JSONObject> mArticles = new ArrayList<JSONObject>();
	protected String mCatalogId = null;
	protected int mPageIndex = 0;
	protected int mPageSize = 10;
	protected boolean mHasAdBanner = false;
	protected boolean mHasImageBanner = false;
	protected boolean isLoading = false;
	protected LayoutInflater inflater;
	protected int state=0;
	public VideoNewsHome(){
		
	}
	public VideoNewsHome(int state){
		this.state=state;
	}
	public void init(Context context, String catalogId, DragListView listView, BaseAdapter adapter, AdvancedImageCarousel imageCarousel) {
		mContext= context;
		mCatalogId = catalogId;
		mListView = listView;
		mAdapter = adapter;
		imageCarousel.removeAllCarouselView();
		imageCarousel.setVisibility(View.GONE);
		
		inflater = LayoutInflater.from(mContext);

		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setListener(this);
		mListView.setHeaderColor(0xffffffff);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterBackgroundColor(0xffffffff);
		mListView.setBackgroundColor(0xffffffff);

		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderVideoNews viewHolderVideoNews = null;
				int type = getItemViewType(position);

				if(convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_videonews, null);
					viewHolderVideoNews = new ViewHolderVideoNews();
					viewHolderVideoNews.setNormalLayout((LinearLayout) convertView.findViewById(R.id.normal_layout));
					viewHolderVideoNews.setTitle((TextView) convertView.findViewById(R.id.title));
					viewHolderVideoNews.setComments((TextView) convertView.findViewById(R.id.comments));
					viewHolderVideoNews.setPlaycount((TextView) convertView.findViewById(R.id.playcount));
					viewHolderVideoNews.setDuration((TextView) convertView.findViewById(R.id.duration));
					viewHolderVideoNews.setThumbnail((AdvancedImageView) convertView.findViewById(R.id.image));
					convertView.setTag(viewHolderVideoNews);
					loadViewHolder(position, convertView, viewHolderVideoNews, type);
				} else {
					loadViewHolder(position, convertView, viewHolderVideoNews, type);
				}
				return convertView;
			}

			@Override
			public int getItemViewType(int position) {
				if(position == 0 && mHasAdBanner)
					return ItemType_AdBanner;
				else if(position == 0 && !mHasAdBanner && mHasImageBanner)
					return ItemType_Banner;
				else if(position == 1 && mHasAdBanner && mHasImageBanner)
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
				if (state!=1) {
					int count = 0;
					if(mHasAdBanner) {
						count++;
					}
					if(mArticles == null || mArticles.size() < 1)
						return count;
					if(mHasImageBanner) {
						count++;
						if(mArticles.size() < 4) {
							return count;
						} else {
							count += mArticles.size() - 4;
							return count;
						}
					} else {
						count += mArticles.size();
						return count;
					}
				}else {
					return mArticles.size();
				}
			}
		};
		mListView.setAdapter(mAdapter);
		mListView.setAsOuter();
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos = position > 2 ? position - 2 : 0;
				if (state!=1) {
					if(mHasAdBanner) {
						if(pos == 0) {
							// TODO
						} else {
							pos--;
						}
					}
					if(mHasImageBanner) {
						pos += (mArticles.size() < 4) ? (mArticles.size() - 1) : 3;
					}
				}else {
					
				}
				JSONObject obj = mArticles.get(pos);
				try {
					((HomeActivity) mContext).openDetailActivity(Integer.valueOf(obj.optString("type")), obj.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		mPageIndex = -1;
		mArticles.clear();
		mAdapter.notifyDataSetChanged();
		loadMore(mCatalogId, mPageIndex);
	}
	
	protected void loadViewHolder(int position, View convertView, ViewHolderVideoNews viewHolderVideoNews, int type) {
		viewHolderVideoNews = (ViewHolderVideoNews) convertView.getTag();
		viewHolderVideoNews.getNormalLayout().setVisibility(View.VISIBLE);
		try {
			int pos = position;
			if (state!=1) {
				if(mHasAdBanner) {
					pos--;
				}
				if(mHasImageBanner) {
					pos += (mArticles.size() < 4) ? (mArticles.size() - 1) : 3;
				}
			}else {
				
			}
			JSONObject obj = mArticles.get(pos);
			viewHolderVideoNews.getTitle().setText(obj.optString("title"));
			viewHolderVideoNews.getComments().setText(obj.optString("commcount"));
			viewHolderVideoNews.getPlaycount().setText(obj.optString("hitcount"));
			viewHolderVideoNews.getThumbnail().setNetImage(obj.optString("logo"));
			viewHolderVideoNews.getDuration().setText(obj.optString("duration"));
		} catch (Exception e) {
			e.printStackTrace();
		}
//		convertView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}

	protected void loadMore(final String catalogId, final int pageIndex) {
		isLoading = true;
		if(pageIndex == -1) {
			CacheData cacheData = mCacheDatas.get(catalogId);
			if(cacheData != null) {
				mArticles.clear();
				mArticles = cacheData.getArticles();
				mPageIndex = cacheData.getPageIndex();
				mAdapter.notifyDataSetChanged();
				if(mArticles.size() >= cacheData.getTotal()) {
					mListView.setPullLoadEnable(false);
				} else {
					mListView.setPullLoadEnable(true);
				}
				if(mCatalogId == catalogId) {
					((HomeActivity) mContext).mCloseLoadingIcon();
				}
				return;
			} else {
				JsonCacheObj obj = JsonCache.getInstance().get(catalogId);
				if(obj != null) {
					try {
						JSONObject result = (JSONObject) obj.getContent();
						int total = result.getInt("total");
						JSONArray array = result.getJSONArray("articles");
						mArticles.clear();
						for(int i = 0; i < array.length(); i++) {
							mArticles.add(array.getJSONObject(i));
						}
						mAdapter.notifyDataSetChanged();
						if(mArticles.size() >= total) {
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
						if(mCatalogId == catalogId) {
							((HomeActivity) mContext).mCloseLoadingIcon();
						}
					}
				} else {
					((HomeActivity) mContext).mOpenLoadingIcon();
				}
			}
		}
		isLoading = true;
		News.getArticleList(0, catalogId, mPageSize, (pageIndex == -1 ? 1 : pageIndex), mContext, new OnJsonObjectResultListener() {
			@Override
			public void onOK(JSONObject result) {
				try {
					int total = result.getInt("total");
					JSONArray array = result.getJSONArray("articles");
					if(pageIndex <= 1) {
						mArticles.clear();
					}
					for(int i = 0; i < array.length(); i++) {
						mArticles.add(array.getJSONObject(i));
					}
					mAdapter.notifyDataSetChanged();
					if(mArticles.size() >= total) {
						mListView.setPullLoadEnable(false);
					} else {
						mListView.setPullLoadEnable(true);
					}
					mListView.setPullRefreshEnable(true);
					mListView.stopRefresh();
					mListView.stopLoadMore();
					mCacheDatas.add(new CacheData((pageIndex == -1 ? 1 : pageIndex), catalogId, mArticles, total));
					JsonCache.getInstance().set(catalogId, "list", result);
				} catch (Exception e) {
					mListView.setPullRefreshEnable(true);
					mListView.stopRefresh();
					mListView.stopLoadMore();
					if(mCatalogId == catalogId) {
						((HomeActivity) mContext).mCloseLoadingIcon();
					}
				}
				if(mCatalogId == catalogId) {
					((HomeActivity) mContext).mCloseLoadingIcon();
				}
				isLoading = false;
			}

			@Override
			public void onNG(String reason) {
				mListView.setPullRefreshEnable(true);
				mListView.stopRefresh();
				mListView.stopLoadMore();
				isLoading = false;
				if(mCatalogId == catalogId) {
					((HomeActivity) mContext).mCloseLoadingIcon();
				}
			}

			@Override
			public void onCancel() {
				mListView.setPullRefreshEnable(true);
				mListView.stopRefresh();
				mListView.stopLoadMore();
				isLoading = false;
				if(mCatalogId == catalogId) {
					((HomeActivity) mContext).mCloseLoadingIcon();
				}
			}
		});
	}

	@Override
	public void onRefresh() {
		if(!isLoading) {
			mPageIndex = 1;
			loadMore(mCatalogId, mPageIndex);
		}
	}

	@Override
	public void onLoadMore() {
		if(!isLoading) {
			if(mPageIndex == -1) {
				mPageIndex = 2;
			} else {
				mPageIndex++;
			}
			loadMore(mCatalogId, mPageIndex);
		}
	}
}
