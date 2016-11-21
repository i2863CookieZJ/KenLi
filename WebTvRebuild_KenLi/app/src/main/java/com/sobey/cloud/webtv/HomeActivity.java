package com.sobey.cloud.webtv;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageCarousel;
import com.baidu.mobstat.StatService;
import com.dylan.common.animation.AnimationController;
import com.dylan.common.utils.ScaleConversion;
import com.dylan.uiparts.listview.DragListView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.obj.JsonCacheObj;
import com.sobey.cloud.webtv.ui.GeneralNewsHome;
import com.sobey.cloud.webtv.ui.PhotoNewsHome;
import com.sobey.cloud.webtv.ui.VideoNewsHome;
import com.sobey.cloud.webtv.utils.AdBanner;
import com.sobey.cloud.webtv.utils.JsonCache;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class HomeActivity extends BaseActivity {
	private CatalogObj mCatalogObj;
	private ArrayList<JSONObject> mCatalogs = new ArrayList<JSONObject>();
	private String mCatalogId = null;
	private BaseAdapter mAdapter;
	private LinearLayout mImageCarouselLayout = null;
	private AdvancedImageCarousel mImageCarousel = null;
	private RelativeLayout mImageCarouselBottomView = null;
	private ImageView mImageCarouselBottomViewIcon = null;
	private TextView mImageCarouselBottomViewTitle = null;
	private RelativeLayout mTitleBarLayout;
	private LayoutInflater inflater;
	private String mVoteInformation;

	@GinInjectView(id = R.id.mListView)
	DragListView mListView;
	@GinInjectView(id = R.id.mTabHost)
	TabHost mTabHost;
	@GinInjectView(id = R.id.mTitleBarAddBtn)
	ImageView mTitleBarAddBtn;
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
	private String state;
	private String title;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_home;
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
			mCatalogId = getIntent().getStringExtra("ids");
			state = getIntent().getStringExtra("state");
			title = getIntent().getStringExtra("title");
			if (TextUtils.isEmpty(state)) {

				titlebar.setVisibility(View.VISIBLE);
			} else {

				mCatalogId = state;
				titlebar.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			if (e != null) {
				Log.i("dzy", e.toString());
			}
			finish();
		}
		initSliding(false);
		if (!TextUtils.isEmpty(title)) {
			setTitle(title);
		} else {
			setTitle(mCatalogObj.name);
		}
		// setModuleMenuSelectedItem(mCatalogObj.index);

		mTitleBarAddBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
			}
		});

		inflater = LayoutInflater.from(this);
		if (mImageCarousel == null) {
			mImageCarousel = new AdvancedImageCarousel(this);
			mImageCarousel.setDefaultImage(R.drawable.default_thumbnail_banner);
			mImageCarousel.setLoadingImage(R.drawable.default_thumbnail_banner);
			mImageCarousel.setErrorImage(R.drawable.default_thumbnail_banner);
			mImageCarousel.setAspectRatio(1.78f);
			mImageCarousel.setScaleType(ScaleType.CENTER_CROP);
			mImageCarousel.setIntervalTime(3000);
			mImageCarousel.setDotViewMargin(0, 0, ScaleConversion.dip2px(this, 10), ScaleConversion.dip2px(this, 8));
		}
		mImageCarousel.removeAllCarouselView();
		mImageCarousel.setVisibility(View.GONE);
		mImageCarouselLayout = new LinearLayout(this);
		mImageCarouselLayout.setGravity(Gravity.CENTER);
		mImageCarouselLayout.addView(mImageCarousel);
		mListView.addHeaderView(mImageCarouselLayout);

		mImageCarouselBottomView = (RelativeLayout) inflater.inflate(R.layout.layout_carousel_bottomview, null);
		mImageCarouselBottomViewIcon = (ImageView) mImageCarouselBottomView.findViewById(R.id.icon);
		mImageCarouselBottomViewTitle = (TextView) mImageCarouselBottomView.findViewById(R.id.title);
		mImageCarousel.addBottomView(mImageCarouselBottomView);

		setupTabBar();

		SharedPreferences adManager = getSharedPreferences("ad_manager", 0);
		if (adManager.getBoolean("banner_enable", false)) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					new AdBanner(HomeActivity.this, mCatalogObj.id, mAdLayout, mAdImage, mAdCloseBtn, false);
				}
			}, 5000);
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
						// ToastUtil.showToast(HomeActivity.this, "便民");
						((TextView) view.findViewById(R.id.text))
								.setTextColor(getResources().getColor(R.color.home_tab_text_focus));
						view.setBackgroundResource(R.drawable.home_tab_background_selected);
						// view.setBackgroundColor(getResources().getColor(R.color.home_tab_text_focus));
						LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
						params.setMargins(10, 0, 10, 0);
						params.gravity = Gravity.CENTER;
						view.setLayoutParams(params);
						try {
							if (TextUtils.isEmpty(state)) {
								mCatalogId = mCatalogs.get(i).getString("id");
							}

							Log.v("便民", mCatalogs.get(i).getString("id") + "");
							if (mCatalogs != null && mCatalogs.size() > 0) {
								initContent(mCatalogs.get(i).optString("appstyle"));
							}
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
			}
		});
		loadCatalogCache();
	}

	private void loadCatalogCache() {
		JsonCacheObj obj = JsonCache.getInstance().get(mCatalogObj.id);
		if (obj != null) {
			try {
				JSONArray result = (JSONArray) obj.getContent();
				for (int i = 0; i < result.length(); i++) {
					mCatalogs.add(result.getJSONObject(i));
					View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.layout_tabitem_text, null);
					((TextView) view.findViewById(R.id.text)).setText(mCatalogs.get(i).getString("name"));
					mTabHost.addTab(mTabHost.newTabSpec(mCatalogs.get(i).getString("name")).setIndicator(view)
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
		News.getCatalogList(mCatalogObj.id, 0, this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					for (int i = 0; i < result.length(); i++) {
						mCatalogs.add(result.getJSONObject(i));
						View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.layout_tabitem_text, null);
						((TextView) view.findViewById(R.id.text)).setText(mCatalogs.get(i).optString("name"));
						mTabHost.addTab(mTabHost.newTabSpec(mCatalogs.get(i).optString("name")).setIndicator(view)
								.setContent(R.id.mListView));
					}
					mTabHost.setCurrentTab(1);
					mTabHost.setCurrentTab(0);
					if (result.length() == 1) {
						mTitleBarLayout = (RelativeLayout) findViewById(R.id.titlebar);
						mTitleBarLayout.setVisibility(View.GONE);
					}
					if (!TextUtils.isEmpty(state)) {
						for (int i = 0; i < mCatalogs.size(); i++) {
							if (mCatalogs.get(i).optString("id").equals(state)) {
								setTitle(mCatalogs.get(i).optString("name"));
								Log.v("标题", mCatalogs.get(i).optString("name"));
								break;
							}
						}
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
	}

	private void initContent(String type) {

		int typeInt = 0;
		try {
			typeInt = Integer.valueOf(type);
		} catch (Exception e) {
			typeInt = 0;
		}
		switch (typeInt) {
		case 2:
			VideoNewsHome videoNewsHome = new VideoNewsHome();
			videoNewsHome.init(this, mCatalogId, mListView, mAdapter, mImageCarousel);
			break;
		case 3:
			PhotoNewsHome photoNewsHome = new PhotoNewsHome();
			photoNewsHome.init(this, mCatalogId, mListView, mAdapter, mImageCarousel);
			break;
		default:
			if (TextUtils.isEmpty(state)) {
				GeneralNewsHome generalNewsHome = new GeneralNewsHome();
				generalNewsHome.init(this, mCatalogId, mListView, mAdapter, mImageCarousel,
						mImageCarouselBottomViewIcon, mImageCarouselBottomViewTitle);
			} else {
				GeneralNewsHome generalNewsHome = new GeneralNewsHome(1);
				generalNewsHome.init(this, mCatalogId, mListView, mAdapter, mImageCarousel,
						mImageCarouselBottomViewIcon, mImageCarouselBottomViewTitle);
			}

			break;
		}
		if (TextUtils.isEmpty(state)) {
			getVote(mCatalogId);
		} else {
			getVote(state);
			Log.v(state + "----", state);
		}

	}

	public void openDetailActivity(int type, String information) {
		switch (type) {
		case MConfig.TypePicture:
			Intent intent = new Intent(HomeActivity.this, PhotoNewsDetailActivity.class);
			intent.putExtra("information", information);
			if (!TextUtils.isEmpty(mVoteInformation)) {
				intent.putExtra("vote", mVoteInformation);
			}
			HomeActivity.this.startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(HomeActivity.this);
			Intent intent1 = new Intent(HomeActivity.this, VideoNewsDetailActivity.class);
			intent1.putExtra("information", information);
			if (!TextUtils.isEmpty(mVoteInformation)) {
				intent1.putExtra("vote", mVoteInformation);
			}
			HomeActivity.this.startActivity(intent1);
			break;
		case MConfig.TypeNews:
			Intent intent2 = new Intent(HomeActivity.this, GeneralNewsDetailActivity.class);
			intent2.putExtra("information", information);
			if (!TextUtils.isEmpty(mVoteInformation)) {
				intent2.putExtra("vote", mVoteInformation);
			}
			HomeActivity.this.startActivity(intent2);
			break;
		default:
			HuiZhouSarft.disposeVideoComponent(HomeActivity.this);
			Intent intent4 = new Intent(HomeActivity.this, VideoNewsDetailActivity.class);
			intent4.putExtra("information", information);
			if (!TextUtils.isEmpty(mVoteInformation)) {
				intent4.putExtra("vote", mVoteInformation);
			}
			HomeActivity.this.startActivity(intent4);
			break;
		}
	}

	private void getVote(String catalogId) {
		if (TextUtils.isEmpty(catalogId)) {
			return;
		}
		News.getCatalogRelaVote(catalogId, this, new OnJsonObjectResultListener() {
			@Override
			public void onOK(JSONObject result) {
				mVoteInformation = result.toString();
			}

			@Override
			public void onNG(String reason) {
				mVoteInformation = null;
			}

			@Override
			public void onCancel() {
				mVoteInformation = null;
			}
		});
	}

	@Override
	protected void onDestroy() {
		mImageCarousel.setIntervalTime(0);
		mImageCarousel.removeAllCarouselView();
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
}
