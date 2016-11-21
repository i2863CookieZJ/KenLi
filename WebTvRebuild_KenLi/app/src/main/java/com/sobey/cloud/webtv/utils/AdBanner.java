package com.sobey.cloud.webtv.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageCarousel;
import com.appsdk.advancedimageview.listener.AdvancedImageCarouselClickListener;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.AdvJumpPageActivity;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.obj.AdObj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class AdBanner {

	private static final int DEFAULT_INTERVAL_TIME = 5000;
	private static final float DEFAULT_ASPECT_RATIO = 5.0f;
	private Context mContext;
	@SuppressWarnings("unused")
	private String mCatalogId;
	private RelativeLayout mAdLayout;
	private AdvancedImageCarousel mAdImage;
	private ImageButton mAdCloseBtn;
	private ArrayList<AdObj> mAdObjList = new ArrayList<AdObj>();

	public AdBanner(Context context, String catalogId, RelativeLayout adLayout, AdvancedImageCarousel adImage, ImageButton adCloseBtn,boolean supportTouch) {
		mContext = context;
		mCatalogId = catalogId;
		mAdLayout = adLayout;
		mAdImage = adImage;
		mAdCloseBtn = adCloseBtn;
		init(supportTouch);
	}

	private void init(boolean supportTouch) {
		SharedPreferences adManager = mContext.getSharedPreferences("ad_manager", 0);
		boolean needShow=adManager.getBoolean("banner_enable", false);
		if(!needShow)
			return;
		mAdImage.setScaleType(ScaleType.FIT_XY);
		mAdImage.setIntervalTime(DEFAULT_INTERVAL_TIME);
		mAdImage.setDotFocus(R.drawable.trans);
		mAdImage.setDotNormal(R.drawable.trans);
		mAdImage.canTouchSwitch(supportTouch);
		mAdCloseBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideAdLayout();
			}
		});

		News.getAdvertisement(mContext, null, "banner", new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					if (result.length() > 0) {
						JSONObject obj1 = result.optJSONObject(0);
						String canClose=obj1.optString("status");
						if("1".equals(canClose))
							mAdCloseBtn.setVisibility(View.VISIBLE);
						else
							mAdCloseBtn.setVisibility(View.INVISIBLE);
						String id = obj1.optString("id");
						for (int i = 0; i < obj1.optJSONArray("advertisement").length(); i++) {
							JSONObject obj2 = obj1.optJSONArray("advertisement").optJSONObject(i);
							String name = obj2.optString("adname");
							String picUrl = obj2.optJSONObject("adcontent").optString("UploadFilePath1");
							String linkUrl = obj2.optJSONObject("adcontent").optString("outurl");
							if (!TextUtils.isEmpty(picUrl) && !TextUtils.isEmpty(linkUrl)) {
								AdObj obj = new AdObj(id, name, picUrl, linkUrl);
								mAdObjList.add(obj);
							}
						}
						if (mAdObjList.size() > 0) {
							for (int i = 0; i < mAdObjList.size(); i++) {
								mAdImage.addCarouselViewByUrl(mAdObjList.get(i).getPicUrl());
							}
							mAdImage.setOnCarouselClickListener(new AdvancedImageCarouselClickListener() {
								@Override
								public void onImageClick(int position) {
									if (position < mAdObjList.size() && !TextUtils.isEmpty(mAdObjList.get(position).getLinkUrl())) {
										String url;
										if (mAdObjList.get(position).getLinkUrl().indexOf("http://") < 0) {
											url = "http://" + mAdObjList.get(position).getLinkUrl();
										} else {
											url = mAdObjList.get(position).getLinkUrl();
										}
										Intent intent = new Intent();
//										intent.setAction("android.intent.action.VIEW");
//										Uri content_url = Uri.parse(url);
//										intent.setData(content_url);
										intent.putExtra("url", url);
										intent.putExtra("name", mAdObjList.get(position).getName());
										intent.setClass(mContext, AdvJumpPageActivity.class);
										mContext.startActivity(intent);
									}
								}
							});
							showAdLayout();
						}
					}
				} catch (Exception e) {
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

	private void hideAdLayout() {
		mAdLayout.setVisibility(View.GONE);
		SharedPreferences adManager = mContext.getSharedPreferences("ad_manager", 0);
		Editor editor = adManager.edit();
		editor.putBoolean("banner_enable", false);
		editor.commit();
	}

	private void showAdLayout() {
		mAdLayout.setVisibility(View.VISIBLE);
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		LayoutParams layoutParams = mAdLayout.getLayoutParams();
		layoutParams.width = width;
		layoutParams.height = (int) (width / DEFAULT_ASPECT_RATIO);
		mAdLayout.setLayoutParams(layoutParams);
		mAdLayout.invalidate();
	}
}
