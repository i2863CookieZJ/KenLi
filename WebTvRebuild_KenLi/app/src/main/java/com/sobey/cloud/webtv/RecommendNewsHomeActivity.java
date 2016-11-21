package com.sobey.cloud.webtv;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
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
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.obj.ViewHolderRecommendNews;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecommendNewsHomeActivity extends BaseActivity implements IDragListViewListener {
	private static final int ItemType_AdBanner = 0;
	private static final int ItemType_Banner = 1;
	private static final int ItemType_Normal = 2;

	private CatalogObj mCatalogObj;
	private BaseAdapter mAdapter;
	private ArrayList<JSONObject> mArticles = new ArrayList<JSONObject>();
	private int mPageIndex = 0;
	private int mPageSize = 5;
	private boolean mHasAdBanner = false;
	private boolean mHasImageBanner = false;
	private boolean isLoading = false;
	private boolean mLoadAllFlag = false;
	private LayoutInflater inflater;
	private JSONArray mJsonCache = new JSONArray();

	@GinInjectView(id = R.id.mListView)
	DragListView mListView;

	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_recommend_home;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		try {
			mCatalogObj = MConfig.CatalogList.get(getIntent().getIntExtra("index", 0));
		} catch (Exception e) {
			if (e != null) {
				Log.i("dzy", e.toString());
			}
			finishActivity();
		}
		initSliding(false);
		setTitle(mCatalogObj.name);
		setModuleMenuSelectedItem(mCatalogObj.index);

		inflater = LayoutInflater.from(this);
		initContent();
	}

	public void openDetailActivity(int type, String information) {
		switch (type) {
		case MConfig.TypePicture:
			Intent intent = new Intent(RecommendNewsHomeActivity.this, PhotoNewsDetailActivity.class);
			intent.putExtra("information", information);
			RecommendNewsHomeActivity.this.startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(RecommendNewsHomeActivity.this);
			Intent intent1 = new Intent(RecommendNewsHomeActivity.this, VideoNewsDetailActivity.class);
			intent1.putExtra("information", information);
			RecommendNewsHomeActivity.this.startActivity(intent1);
			break;
		case MConfig.TypeNews:
			Intent intent2 = new Intent(RecommendNewsHomeActivity.this, GeneralNewsDetailActivity.class);
			intent2.putExtra("information", information);
			RecommendNewsHomeActivity.this.startActivity(intent2);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(RecommendNewsHomeActivity.this);
			Intent intent4 = new Intent(RecommendNewsHomeActivity.this, VideoNewsDetailActivity.class);
			intent4.putExtra("information", information);
			RecommendNewsHomeActivity.this.startActivity(intent4);
			break;
		}
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
		mListView.setHeaderColor(0xfff3f3f3);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterBackgroundColor(0xfff3f3f3);
		mListView.setBackgroundColor(0xfff3f3f3);

		SharedPreferences recommendCache = RecommendNewsHomeActivity.this.getSharedPreferences("recommend_cache", 0);
		try {
			mJsonCache = new JSONArray(recommendCache.getString("json", null));
		} catch (Exception e) {
			mJsonCache = new JSONArray();
		}

		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderRecommendNews viewHolderRecommendNews = null;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_recommendnews, null);
					viewHolderRecommendNews = new ViewHolderRecommendNews();
					viewHolderRecommendNews.setLargePicLayout(
							(RelativeLayout) convertView.findViewById(R.id.recommend_largepic_layout));
					viewHolderRecommendNews.setLargePicImage(
							(AdvancedImageView) convertView.findViewById(R.id.recommend_largepic_image));
					viewHolderRecommendNews
							.setLargePicTitle((TextView) convertView.findViewById(R.id.recommend_largepic_title));
					viewHolderRecommendNews
							.setSmallPicLayout((LinearLayout) convertView.findViewById(R.id.recommend_smallpic_layout));
					viewHolderRecommendNews.setSmallPicImage(
							(AdvancedImageView) convertView.findViewById(R.id.recommend_smallpic_image));
					viewHolderRecommendNews
							.setSmallPicTitle((TextView) convertView.findViewById(R.id.recommend_smallpic_title));
					viewHolderRecommendNews.setSinglePicLayout(
							(LinearLayout) convertView.findViewById(R.id.recommend_singlepic_layout));
					viewHolderRecommendNews.setSinglePicImage(
							(AdvancedImageView) convertView.findViewById(R.id.recommend_singlepic_image));
					viewHolderRecommendNews
							.setSinglePicTitle((TextView) convertView.findViewById(R.id.recommend_singlepic_title));
					viewHolderRecommendNews.setTime((TextView) convertView.findViewById(R.id.time));
					viewHolderRecommendNews.setDivider((LinearLayout) convertView.findViewById(R.id.divider));
					convertView.setTag(viewHolderRecommendNews);
					loadViewHolder(position, convertView, viewHolderRecommendNews);
				} else {
					loadViewHolder(position, convertView, viewHolderRecommendNews);
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
					openDetailActivity(Integer.valueOf(obj.getString("ArticleType")), obj.toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && mListView.getFirstVisiblePosition() == 0) {
					if (!isLoading && !mLoadAllFlag) {
						mPageIndex++;
						parseData(mPageIndex, mJsonCache);
					}
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
			}
		});

		mPageIndex = 1;
		mArticles.clear();
		mAdapter.notifyDataSetChanged();
		parseData(mPageIndex, mJsonCache);
		loadMore();
	}

	private void loadViewHolder(int position, View convertView, ViewHolderRecommendNews viewHolderRecommendNews) {
		viewHolderRecommendNews = (ViewHolderRecommendNews) convertView.getTag();
		try {
			int pos = position;
			if (mHasAdBanner) {
				pos--;
			}
			if (mHasImageBanner) {
				pos += (mArticles.size() < 4) ? (mArticles.size() - 1) : 3;
			}
			JSONObject obj = mArticles.get(pos);
			switch (obj.optInt("type")) {
			case 0:
				viewHolderRecommendNews.getLargePicLayout().setVisibility(View.GONE);
				viewHolderRecommendNews.getSmallPicLayout().setVisibility(View.GONE);
				viewHolderRecommendNews.getSinglePicLayout().setVisibility(View.GONE);
				viewHolderRecommendNews.getTime().setVisibility(View.VISIBLE);
				viewHolderRecommendNews.getTime().setText(obj.optString("PushTime"));
				break;
			case 1:
				viewHolderRecommendNews.getLargePicLayout().setVisibility(View.GONE);
				viewHolderRecommendNews.getSmallPicLayout().setVisibility(View.GONE);
				viewHolderRecommendNews.getSinglePicLayout().setVisibility(View.VISIBLE);
				viewHolderRecommendNews.getTime().setVisibility(View.GONE);
				viewHolderRecommendNews.getSinglePicImage().setNetImage(obj.optString("FirstRecImg"));
				viewHolderRecommendNews.getSinglePicTitle().setText(obj.optString("ArticleTitle"));
				break;
			case 2:
				viewHolderRecommendNews.getLargePicLayout().setVisibility(View.VISIBLE);
				viewHolderRecommendNews.getSmallPicLayout().setVisibility(View.GONE);
				viewHolderRecommendNews.getSinglePicLayout().setVisibility(View.GONE);
				viewHolderRecommendNews.getTime().setVisibility(View.GONE);
				viewHolderRecommendNews.getLargePicImage().setNetImage(obj.optString("FirstRecImg"));
				viewHolderRecommendNews.getLargePicTitle().setText(obj.optString("ArticleTitle"));
				break;
			default:
				viewHolderRecommendNews.getLargePicLayout().setVisibility(View.GONE);
				viewHolderRecommendNews.getSmallPicLayout().setVisibility(View.VISIBLE);
				viewHolderRecommendNews.getSinglePicLayout().setVisibility(View.GONE);
				viewHolderRecommendNews.getTime().setVisibility(View.GONE);
				viewHolderRecommendNews.getSmallPicImage().setNetImage(obj.optString("SmallLog"));
				viewHolderRecommendNews.getSmallPicTitle().setText(obj.optString("ArticleTitle"));
				if (obj.optBoolean("divider")) {
					viewHolderRecommendNews.getDivider().setVisibility(View.VISIBLE);
				} else {
					viewHolderRecommendNews.getDivider().setVisibility(View.GONE);
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadMore() {
		isLoading = true;
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
		News.pushHomePageRec(publishTime, this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					if (result != null && result.length() > 0) {
						for (int i = result.length() - 1; i >= 0; i--) {
							JSONObject obj = result.optJSONObject(i);
							if (!TextUtils.isEmpty(obj.optString("PushTime"))) {
								JSONArray jsonArray = new JSONArray(mJsonCache.toString());
								jsonArray.put(obj);
								if (mJsonCache == null || mJsonCache.length() < 1
										|| !mJsonCache.optJSONObject(mJsonCache.length() - 1).optString("PushTime")
												.equalsIgnoreCase(obj.optString("PushTime"))) {
									if (parseData(mPageIndex, jsonArray)) {
										mJsonCache.put(obj);
										SharedPreferences recommendCache = RecommendNewsHomeActivity.this
												.getSharedPreferences("recommend_cache", 0);
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
					mCloseLoadingIcon();
					isLoading = false;
				}
			}

			@Override
			public void onNG(String reason) {
				isLoading = false;
				mCloseLoadingIcon();
			}

			@Override
			public void onCancel() {
				isLoading = false;
				mCloseLoadingIcon();
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
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mArticles.size());
				mCloseLoadingIcon();
				return false;
			}
			int minIndex = 0;
			if (mPageSize * pageIndex > jsonArray.length()) {
				minIndex = jsonArray.length();
				mLoadAllFlag = true;
			} else {
				minIndex = mPageSize * pageIndex;
				mLoadAllFlag = false;
			}
			for (int i = minIndex - 1; i >= 0; i--) {
				JSONObject result = jsonArray.optJSONObject(jsonArray.length() - 1 - i);
				String publishTime = result.optString("PushTime");
				JSONObject timeObj = new JSONObject();
				timeObj.put("PushTime", publishTime);
				timeObj.put("type", 0);
				mArticles.add(timeObj);
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
			mAdapter.notifyDataSetChanged();
			if (pageIndex <= 1) {
				mListView.setSelection(mArticles.size());
			} else {
				mListView.setSelection(mArticles.size() - mLastSize);
			}
		} catch (Exception e) {
			res = false;
		} finally {
			mCloseLoadingIcon();
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
