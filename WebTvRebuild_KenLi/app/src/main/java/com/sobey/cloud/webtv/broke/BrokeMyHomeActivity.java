package com.sobey.cloud.webtv.broke;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.animation.AnimationController;
import com.dylan.common.utils.DateParse;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.broke.util.BrokeFooterControl;
import com.sobey.cloud.webtv.broke.util.BrokeStatus;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.obj.CacheData;
import com.sobey.cloud.webtv.obj.CacheDataList;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.obj.ViewHolderBrokeNews;
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
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

/**
 * 我的爆料信息
 * 
 * @author lgx
 *
 */
public class BrokeMyHomeActivity extends BaseActivity implements IDragListViewListener {

	private static final int ItemType_AdBanner = 0;
	private static final int ItemType_Banner = 1;
	private static final int ItemType_Normal = 2;

	private BaseAdapter mAdapter;
	private CacheDataList mCacheDatas = new CacheDataList();
	private CatalogObj mCatalogObj;
	private ArrayList<JSONObject> mArticles = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> mCatalogs = new ArrayList<JSONObject>();
	private String mCatalogId = null;
	private int mPageIndex = 0;
	private int mPageSize = 10;
	private boolean mHasAdBanner = false;
	private boolean mHasImageBanner = false;
	private boolean isLoading = false;
	private LayoutInflater inflater;
	private int paddingLeft;
	private int paddingRight;
	private int paddingTop;
	private int paddingBottom;
	private String mUserName;
	private boolean initFlag = false;

	@GinInjectView(id = R.id.mListView)
	DragListView mListView;

	@GinInjectView(id = R.id.mTabHost)
	TabHost mTabHost;

	@GinInjectView(id = R.id.mTitleBarAddBtn)
	ImageView mTitleBarAddBtn;

	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_broke_my_home;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		init();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus && initFlag) {
			SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(BrokeMyHomeActivity.this, BrokeNewsHomeActivity.class));
				finish();
			}
		}
		if (hasFocus) {
			SharedPreferences launchMode = this.getSharedPreferences("lauch_mode", 0);
			if (launchMode != null && launchMode.getBoolean("broke_task_home", false)) {
				startActivity(new Intent(BrokeMyHomeActivity.this, BrokeTaskHomeActivity.class));
				finish();
			}
		}
		super.onWindowFocusChanged(hasFocus);
	}

	public void init() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				initFlag = true;
			}
		}, 500);
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			startActivity(new Intent(BrokeMyHomeActivity.this, com.sobey.cloud.webtv.views.user.LoginActivity.class));
		}
		mUserName = userInfo.getString("id", "");

		try {
			mCatalogObj = MConfig.CatalogList.get(getIntent().getIntExtra("index", 0));
		} catch (Exception e) {
			finish();
		}
		initSliding(false);
		setTitle(mCatalogObj.name);
		setModuleMenuSelectedItem(mCatalogObj.index);
		BrokeFooterControl.setBrokeFooterSelectedItem(this, "我的", mCatalogObj.index);

		mTitleBarAddBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
			}
		});

		inflater = LayoutInflater.from(this);
		paddingLeft = (int) getResources().getDimension(R.dimen.headline_padding_left);
		paddingRight = (int) getResources().getDimension(R.dimen.headline_padding_right);
		paddingTop = (int) getResources().getDimension(R.dimen.headline_padding_top);
		paddingBottom = (int) getResources().getDimension(R.dimen.headline_padding_bottom);

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
				ViewHolderBrokeNews viewHolderBrokeNews = null;
				int type = getItemViewType(position);

				if (convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_brokenews, null);
					viewHolderBrokeNews = new ViewHolderBrokeNews();
					viewHolderBrokeNews.setImage((AdvancedImageView) convertView.findViewById(R.id.image));
					viewHolderBrokeNews.setHeadicon((AdvancedImageView) convertView.findViewById(R.id.headicon_image));
					viewHolderBrokeNews.setTime((TextView) convertView.findViewById(R.id.time_text));
					viewHolderBrokeNews.setCommentcount((TextView) convertView.findViewById(R.id.comment_text));
					viewHolderBrokeNews.setUsername((TextView) convertView.findViewById(R.id.username));
					viewHolderBrokeNews.setTitle((TextView) convertView.findViewById(R.id.title));
					viewHolderBrokeNews.setVideocount((TextView) convertView.findViewById(R.id.videocount_text));
					viewHolderBrokeNews.setVideocountImage((ImageView) convertView.findViewById(R.id.videocount_image));
					viewHolderBrokeNews.setPicturecount((TextView) convertView.findViewById(R.id.picturecount_text));
					viewHolderBrokeNews
							.setPicturecountImage((ImageView) convertView.findViewById(R.id.picturecount_image));
					convertView.setTag(viewHolderBrokeNews);
					loadViewHolder(position, convertView, viewHolderBrokeNews, type);
				} else {
					loadViewHolder(position, convertView, viewHolderBrokeNews, type);
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
					openDetailActivity(obj.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		setupTabBar();
	}

	private void loadViewHolder(int position, View convertView, ViewHolderBrokeNews viewHolderBrokeNews, int type) {
		viewHolderBrokeNews = (ViewHolderBrokeNews) convertView.getTag();
		try {
			int pos = position;
			if (mHasAdBanner) {
				pos--;
			}
			if (mHasImageBanner) {
				pos += (mArticles.size() < 4) ? (mArticles.size() - 1) : 3;
			}
			JSONObject obj = mArticles.get(pos);
			JSONArray contentArray = obj.optJSONArray("content");
			int imageNum = 0;
			int videoNum = 0;
			String imageUrl = "";
			for (int i = 0; i < contentArray.length(); i++) {
				JSONObject contentObject = contentArray.optJSONObject(i);
				if (contentObject.optString("type").equalsIgnoreCase("image")) {
					if (TextUtils.isEmpty(imageUrl)) {
						imageUrl = contentObject.optString("imgpath");
					}
					imageNum++;
				} else if (contentObject.optString("type").equalsIgnoreCase("video")) {
					videoNum++;
				}
			}
			viewHolderBrokeNews.getImage().setNetImage(imageUrl);
			viewHolderBrokeNews.getHeadicon().setNetImage(obj.optString("logo"));
			viewHolderBrokeNews.getTime().setText(DateParse.individualTime(obj.optString("addtime"), null));
			viewHolderBrokeNews.getCommentcount().setText(obj.optString("commentcount"));
			viewHolderBrokeNews.getUsername().setText(obj.optString("name"));
			viewHolderBrokeNews.getTitle().setText(obj.optString("title"));
			viewHolderBrokeNews.getVideocount().setText(videoNum + "个");
			viewHolderBrokeNews.getPicturecount().setText(imageNum + "张");
		} catch (Exception e) {
			convertView = null;
			return;
		}
		convertView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}

	private void loadMore(final String catalogId, final int pageIndex) {
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
			}
			mOpenLoadingIcon();
		}
		isLoading = true;
		// News.getBrokeArticleList(catalogId, null, BrokeStatus.PUBLISHED,
		// mPageSize, (pageIndex == -1 ? 1 : pageIndex), this, new
		// OnJsonObjectResultListener() {
		News.getBrokeArticleList(null, mUserName, BrokeStatus.PUBLISHED, mPageSize, (pageIndex == -1 ? 1 : pageIndex),
				this, new OnJsonObjectResultListener() {
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
						LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
						params.setMargins(10, 0, 10, 0);
						params.gravity = Gravity.CENTER;
						view.setLayoutParams(params);
						try {
							mCatalogId = mCatalogs.get(i).getString("id");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						((TextView) view.findViewById(R.id.text))
								.setTextColor(getResources().getColor(R.color.home_tab_text_normal));
						view.setBackgroundResource(R.drawable.trans);
						LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
						params.setMargins(10, 0, 10, 0);
						params.gravity = Gravity.CENTER;
						view.setLayoutParams(params);
					}
				}
				onTabChange();
			}
		});
		// loadCatalog();
		onTabChange();
	}

	private void openDetailActivity(String information) {
		Intent intent = new Intent(BrokeMyHomeActivity.this, BrokeNewsDetailActivity.class);
		intent.putExtra("information", information);
		BrokeMyHomeActivity.this.startActivity(intent);
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
}
