package com.sobey.cloud.webtv.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageCarousel;
import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.uiparts.listview.DragListView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.GeneralNewsDetailActivity;
import com.sobey.cloud.webtv.PhotoNewsDetailActivity;
import com.sobey.cloud.webtv.VideoNewsDetailActivity;
import com.sobey.cloud.webtv.adapter.Video_Image_Text_NewsAdaptor;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.obj.CacheData;
import com.sobey.cloud.webtv.obj.ViewHolderGeneralNews;
import com.sobey.cloud.webtv.utils.JsonCache;
import com.sobey.cloud.webtv.utils.MConfig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NormalNewsUtil extends GeneralNewsHome {

	private RelativeLayout loadingMask;
	private Activity mActivity;
	public NormalNewsUtil(int state) {
		super(state);
	}

	@Override
	public void init(Context context, String catalogId, DragListView listView,
			BaseAdapter adapter, AdvancedImageCarousel imageCarousel,
			ImageView imageCarouselBottomViewIcon,
			TextView imageCarouselBottomViewTitle) {
		mContext = context;
		mCatalogId = catalogId;
		mListView = listView;
		mAdapter = adapter;
		mImageCarousel = imageCarousel;
		mImageCarouselBottomViewIcon = imageCarouselBottomViewIcon;
		mImageCarouselBottomViewTitle = imageCarouselBottomViewTitle;
		if(mImageCarouselBottomViewTitle!=null)
			mImageCarouselBottomViewTitle.invalidate();

		inflater = LayoutInflater.from(mContext);

		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setListener(this);
		mListView.setHeaderColor(0xffffffff);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterBackgroundColor(0xffffffff);
		mListView.setBackgroundColor(0xffffffff);
		int width = mContext.getResources().getDisplayMetrics().widthPixels;
		mAdapter =new Video_Image_Text_NewsAdaptor(mArticles, mContext, catalogId, width) ;
		new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderGeneralNews viewHolderGeneralNews = null;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_generalnews_util, null);
					viewHolderGeneralNews = new ViewHolderGeneralNews();
					viewHolderGeneralNews.setNormalLayout((LinearLayout) convertView.findViewById(R.id.normal_layout));
					viewHolderGeneralNews.setTitle((TextView) convertView.findViewById(R.id.title));
					viewHolderGeneralNews.setSummary((TextView) convertView.findViewById(R.id.summary));
					viewHolderGeneralNews.setComments((TextView) convertView.findViewById(R.id.comments));
					viewHolderGeneralNews.setThumbnail((AdvancedImageView) convertView.findViewById(R.id.image));
					viewHolderGeneralNews.setType((ImageView) convertView.findViewById(R.id.type));
					viewHolderGeneralNews.setPictureLayout((LinearLayout) convertView.findViewById(R.id.picture_layout));
					viewHolderGeneralNews.setPicture_title((TextView) convertView.findViewById(R.id.picture_title));
					viewHolderGeneralNews.setPicture_image1((AdvancedImageView) convertView.findViewById(R.id.picture_image1));
					viewHolderGeneralNews.setPicture_image2((AdvancedImageView) convertView.findViewById(R.id.picture_image2));
					viewHolderGeneralNews.setPicture_image3((AdvancedImageView) convertView.findViewById(R.id.picture_image3));
					viewHolderGeneralNews.setPicture_comments((TextView) convertView.findViewById(R.id.picture_comments));
					viewHolderGeneralNews.setPicture_refername((TextView) convertView.findViewById(R.id.picture_refername));
					convertView.setTag(viewHolderGeneralNews);
					loadViewHolder(position, convertView, viewHolderGeneralNews);
				} else {
					loadViewHolder(position, convertView, viewHolderGeneralNews);
				}
				return convertView;
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
//					if (mArticles == null || mArticles.size() < 1)
//						return count;
//					if (mHasImageBanner) {
//						if (mArticles.size() < 4) {
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
//					int count = mArticles.size();
//					return count;
//				}
				int count = mArticles.size();
				return count;
			}
		};
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos =position-mListView.getHeaderViewsCount();
//				if (state!=1) {
//					if (mHasImageBanner) {
//						pos += (mArticles.size() < 4) ? (mArticles.size() - 1) : 4;
//					}
//				}else {
//					pos=position - 2;
//				}
//				
				Log.v("pos", pos+"");
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
		mListView.setAdapter(mAdapter);
		mListView.setAsOuter();

		mPageIndex = -1;
		mArticles.clear();
		mAdapter.notifyDataSetChanged();
		if (mImageCarousel != null) {
			mImageCarousel.removeAllCarouselView();
			mImageCarousel.setVisibility(View.GONE);
			mImageCarousel.setIntervalTime(mImageCarouselIntervalTime);
		}
		loadingMask.setVisibility(View.VISIBLE);
		loadMore(mCatalogId, mPageIndex);
	}
	
	protected void loadViewHolder(int position, View convertView, ViewHolderGeneralNews viewHolderGeneralNews) {
		viewHolderGeneralNews = (ViewHolderGeneralNews) convertView.getTag();
		viewHolderGeneralNews.getPicture_image1().clear();
		viewHolderGeneralNews.getPicture_image2().clear();
		viewHolderGeneralNews.getPicture_image3().clear();
		viewHolderGeneralNews.getThumbnail().clear();
		try {
			int pos = position;//-mListView.getHeaderViewsCount();
//			if (state!=1) {
//				if (mHasImageBanner) {
//					pos += (mArticles.size() < 4) ? mArticles.size() : 4;
//				}
//			}else {
//
//			}
			JSONObject obj = mArticles.get(pos);
			switch (Integer.valueOf(obj.getString("type"))) {
			case MConfig.TypePicture:
				viewHolderGeneralNews.getPictureLayout().setVisibility(View.VISIBLE);
				viewHolderGeneralNews.getNormalLayout().setVisibility(View.GONE);
				viewHolderGeneralNews.getPicture_title().setText(obj.getString("title"));
				viewHolderGeneralNews.getPicture_comments().setText(obj.getString("commcount"));
				viewHolderGeneralNews.getPicture_refername().setText(obj.getString("refername"));
				JSONArray pictureArray = obj.getJSONArray("content");
				int pictureSum = pictureArray.length();
				viewHolderGeneralNews.getPicture_image1().setNetImage(obj.getString("logo"));
				if (pictureSum > 1) {
					viewHolderGeneralNews.getPicture_image2().setNetImage(((JSONObject) pictureArray.get(0)).getString("filepath"));
					viewHolderGeneralNews.getPicture_image3().setNetImage(((JSONObject) pictureArray.get(1)).getString("filepath"));
					viewHolderGeneralNews.getPicture_comments().setVisibility(View.GONE);
					convertView.findViewById(R.id.commentContainer).setVisibility(View.GONE);
				}
				DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
				int width = (int) ((metrics.widthPixels - 50.0) / 3.0);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, (int) (width * 3.0 / 4.0));
				params.setMargins(3, 3, 3, 3);
//				viewHolderGeneralNews.getPicture_image1().setLayoutParams(params);
//				viewHolderGeneralNews.getPicture_image2().setLayoutParams(params);
//				viewHolderGeneralNews.getPicture_image3().setLayoutParams(params);
				break;
			default:
				viewHolderGeneralNews.getPictureLayout().setVisibility(View.GONE);
				viewHolderGeneralNews.getNormalLayout().setVisibility(View.VISIBLE);
				viewHolderGeneralNews.getTitle().setText(obj.getString("title"));
				viewHolderGeneralNews.getSummary().setText(obj.getString("summary"));
				viewHolderGeneralNews.getComments().setText(obj.getString("commcount"));
				viewHolderGeneralNews.getThumbnail().setNetImage(obj.getString("logo"));
				if (obj.getString("attribute").contains("video") || Integer.valueOf(obj.getString("type")) == MConfig.TypeVideo) {
					viewHolderGeneralNews.getType().setImageResource(R.drawable.type_video_icon);
					viewHolderGeneralNews.getType().setVisibility(View.VISIBLE);
				} else {
					viewHolderGeneralNews.getType().setVisibility(View.GONE);
				}
				break;
			}
		} catch (Exception e) {
			convertView = null;
			e.printStackTrace();
		}
		//		convertView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}
	public NormalNewsUtil(RelativeLayout mask,Activity activity) {
		loadingMask=mask;
		mActivity=activity;
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
	@Override
	protected void loadMore(final String catalogId,final int pageIndex) {
		isLoading = true;
//		if (pageIndex == -1) {
//			CacheData cacheData = mCacheDatas.get(catalogId);
//			if (cacheData != null) {
//				mArticles.clear();
//				mArticles = cacheData.getArticles();
//				mPageIndex = cacheData.getPageIndex();
//				mAdapter.notifyDataSetChanged();
//				if (state==1) {
//
//				}else {
//					setNetImageBanner();
//				}
//				if (mArticles.size() >= cacheData.getTotal()) {
//					mListView.setPullLoadEnable(false);
//				} else {
//					mListView.setPullLoadEnable(true);
//				}
//				if (mCatalogId == catalogId) {
//					loadingMask.setVisibility(View.GONE);
//				}
//				isLoading = false;
//				return;
//			} else {
//				JsonCacheObj obj = JsonCache.getInstance().get(catalogId);
//				if (obj != null) {
//					try {
//						JSONObject result = (JSONObject) obj.getContent();
//						int total = result.getInt("total");
//						JSONArray array = result.getJSONArray("articles");
//						mArticles.clear();
//						for (int i = 0; i < array.length(); i++) {
//							mArticles.add(array.getJSONObject(i));
//						}
//						mAdapter.notifyDataSetChanged();
//						if (state==1) {
//
//						}else {
//							setNetImageBanner();
//						}
//						if (mArticles.size() >= total) {
//							mListView.setPullLoadEnable(false);
//						} else {
//							mListView.setPullLoadEnable(true);
//						}
//						mListView.setPullRefreshEnable(true);
//						mListView.stopRefresh();
//						mListView.stopLoadMore();
//						mCacheDatas.add(new CacheData((pageIndex == -1 ? 1 : pageIndex), catalogId, mArticles, total));
//						new Handler().postDelayed(new Runnable() {
//							@Override
//							public void run() {
//								isLoading = false;
//							}
//						}, 500);
//						return;
//					} catch (Exception e) {
//						mListView.setPullRefreshEnable(true);
//						mListView.stopRefresh();
//						mListView.stopLoadMore();
//					} finally {
//						if (mCatalogId == catalogId) {
//							loadingMask.setVisibility(View.GONE);
//						}
//					}
//				} else {
//					loadingMask.setVisibility(View.VISIBLE);
//				}
//			}
//		}
		News.getArticleList(0, catalogId, mPageSize, (pageIndex == -1 ? 1 : pageIndex), mContext, new OnJsonObjectResultListener() {
			@Override
			public void onOK(JSONObject result) {
				try {
					((Video_Image_Text_NewsAdaptor)mAdapter).filterLen=0;
					int total = result.getInt("total");
					JSONArray array = result.getJSONArray("articles");
					if (pageIndex <= 1) {
						mArticles.clear();
					}
					for (int i = 0; i < array.length(); i++) {
						mArticles.add(array.getJSONObject(i));
					}
					((Video_Image_Text_NewsAdaptor)mAdapter).filterImageListNews();
					mAdapter.notifyDataSetChanged();
					if (state==1) {

					}else {
//						setNetImageBanner();
					}
					if (mArticles.size() >= total-((Video_Image_Text_NewsAdaptor)mAdapter).filterLen) {
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
						loadingMask.setVisibility(View.GONE);
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
					loadingMask.setVisibility(View.GONE);
				}
			}

			@Override
			public void onCancel() {
				mListView.setPullRefreshEnable(true);
				mListView.stopRefresh();
				mListView.stopLoadMore();
				isLoading = false;
				if (mCatalogId == catalogId) {
					loadingMask.setVisibility(View.GONE);
				}
			}
		});
	}

}
