package com.sobey.cloud.webtv.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageCarousel;
import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.uiparts.listview.DragListView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.GeneralNewsDetailActivity;
import com.sobey.cloud.webtv.PhotoNewsDetailActivity;
import com.sobey.cloud.webtv.VideoNewsDetailActivity;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.obj.CacheData;
import com.sobey.cloud.webtv.obj.JsonCacheObj;
import com.sobey.cloud.webtv.obj.ViewHolderVideoNews;
import com.sobey.cloud.webtv.utils.JsonCache;
import com.sobey.cloud.webtv.utils.MConfig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VideoNewsUtil extends VideoNewsHome 
{
	private RelativeLayout loadingMask;
	private Activity mActivity;

	public VideoNewsUtil(int state) 
	{
		super(state);
	}
	public VideoNewsUtil(RelativeLayout mask,Activity activity) {
		loadingMask=mask;
		mActivity=activity;
	}
	public void init(Context context, String catalogId, DragListView listView, BaseAdapter adapter, AdvancedImageCarousel imageCarousel) {
		mContext= context;
		mCatalogId = catalogId;
		mListView = listView;
		mAdapter = adapter;
		if(imageCarousel!=null)
		{
			imageCarousel.removeAllCarouselView();
			imageCarousel.setVisibility(View.GONE);
		}
		inflater = LayoutInflater.from(mContext);

		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setListener(this);
		mListView.setHeaderColor(0xffffffff);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterBackgroundColor(0xffffffff);
		mListView.setBackgroundColor(0xffffffff);

//		mAdapter =new NewsVideoAdapter(mArticles,mContext);
		mAdapter =	new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderVideoNews viewHolderVideoNews = null;
				int type = getItemViewType(position);

				if(convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_videonews_util, null);
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
//				if (state!=1) {
//					int count = 0;
//					if(mHasAdBanner) {
//						count++;
//					}
//					if(mArticles == null || mArticles.size() < 1)
//						return count;
//					if(mHasImageBanner) {
//						count++;
//						if(mArticles.size() < 4) {
//							return count;
//						} else {
//							count += mArticles.size() - 4;
//							return count;
//						}
//					} else {
//						count += mArticles.size();
//						return count;
//					}
//				}else {
//					return mArticles.size();
//				}
				return mArticles.size();
			}

		};
		mListView.setAdapter(mAdapter);
		mListView.setAsOuter();
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos = position-mListView.getHeaderViewsCount();
//				if (state!=1) {
//					if(mHasAdBanner) {
//						if(pos == 0) {
//							// TODO
//						} else {
//							pos--;
//						}
//					}
//					if(mHasImageBanner) {
//						pos += (mArticles.size() < 4) ? (mArticles.size() - 1) : 5;
//					}
//				}else {
//
//				}
				JSONObject obj = mArticles.get(pos);
				try {
					openDetailActivity(Integer.valueOf(obj.optString("type")), obj.toString());
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
	public void openDetailActivity(int type, String information) {
		switch (type) {
		case MConfig.TypePicture:
			Intent intent = new Intent(mContext, PhotoNewsDetailActivity.class);
			intent.putExtra("information", information);
			//			if (!TextUtils.isEmpty(mVoteInformation)) {
			//				intent.putExtra("vote", mVoteInformation);
			//			}
			mActivity.startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(mContext);
			Intent intent1 = new Intent(mContext, VideoNewsDetailActivity.class);
			intent1.putExtra("information", information);
			//						if (!TextUtils.isEmpty(mVoteInformation)) {
			//							intent1.putExtra("vote", mVoteInformation);
			//						}
			mActivity.startActivity(intent1);
			break;
		case MConfig.TypeNews:
			Intent intent2 = new Intent(mActivity, GeneralNewsDetailActivity.class);
			intent2.putExtra("information", information);
			mActivity.startActivity(intent2);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(mActivity);
			Intent intent3 = new Intent(mActivity, VideoNewsDetailActivity.class);
			intent3.putExtra("information", information);
			mActivity.startActivity(intent3);
			break;
		}
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
					loadingMask.setVisibility(View.GONE);
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
							loadingMask.setVisibility(View.GONE);
						}
					}
				} else {
					loadingMask.setVisibility(View.VISIBLE);
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
						loadingMask.setVisibility(View.GONE);
					}
				}
				if(mCatalogId == catalogId) {
					loadingMask.setVisibility(View.GONE);
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
					loadingMask.setVisibility(View.GONE);
				}
			}

			@Override
			public void onCancel() {
				mListView.setPullRefreshEnable(true);
				mListView.stopRefresh();
				mListView.stopLoadMore();
				isLoading = false;
				if(mCatalogId == catalogId) {
					loadingMask.setVisibility(View.GONE);
				}
			}
		});
	}
}
