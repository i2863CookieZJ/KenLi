package com.sobey.cloud.webtv.views.activity;

import java.util.ArrayList;

import org.json.JSONArray;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

@SuppressLint("SetJavaScriptEnabled")
public class CampaignOffLineDetailAlbumContentActivity extends BaseActivity {

	private JSONArray mInformation;
	private ArrayList<String> AlbumList = new ArrayList<String>();

	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mCampaigndetailBack;
	@GinInjectView(id = R.id.mCampaigndetailAlbumListLayout)
	LinearLayout mCampaigndetailAlbumListLayout;

	@Override
	public int getContentView() {
		return R.layout.activity_campaign_offline_detail_album_content;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setupNewsDetailActivity();
	}

	public void setupNewsDetailActivity() {
		try {
			mInformation = new JSONArray(getIntent().getStringExtra("information"));
			for (int i = 0; i < mInformation.length(); i++) {
				AlbumList.add(mInformation.optJSONObject(i).optString("ImgUrl"));
			}

			mCampaigndetailBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finishActivity();
				}
			});

			mCampaigndetailAlbumListLayout.removeAllViews();
			for (int i = 0; i < AlbumList.size();) {
				LinearLayout layout = new LinearLayout(this);
				layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				for (int j = 0; j < 3; j++) {
					String url = null;
					if (i < AlbumList.size()) {
						url = AlbumList.get(i);
					}
					AdvancedImageView imageView = new AdvancedImageView(this);
					imageView.setAspectRatio(1.5f);
					imageView.setRondRadius(3);
					imageView.setScaleType(ScaleType.CENTER_CROP);
					imageView.setDefaultImage(R.drawable.default_thumbnail_banner);
					imageView.setLoadingImage(R.drawable.default_thumbnail_banner);
					imageView.setImageDrawable(getResources().getDrawable(R.drawable.default_thumbnail_banner));
					SharedPreferences settings = CampaignOffLineDetailAlbumContentActivity.this
							.getSharedPreferences("settings", 0);
					CheckNetwork network = new CheckNetwork(CampaignOffLineDetailAlbumContentActivity.this);
					boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
							|| network.getWifiState(false) == State.CONNECTED;
					if (TextUtils.isEmpty(url)) {
						imageView.setImageResource(R.drawable.trans);
					} else if (isShowPicture) {
						imageView.setNetImage(url);
					}
					int screenWidth = getResources().getDisplayMetrics().widthPixels;
					int w = screenWidth / 3 - (3 * 5);
					LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
					params.weight = 1;
					params.leftMargin = 5;
					params.rightMargin = 5;
					params.topMargin = 5;
					params.bottomMargin = 5;
					imageView.setLayoutParams(params);
					layout.addView(imageView);
					imageView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							Log.i("dzy", "photo");
							Intent intent = new Intent(CampaignOffLineDetailAlbumContentActivity.this,
									CampaignOffLineDetailAlbumContentSingleActivity.class);
							intent.putExtra("information", mInformation.toString());
							startActivity(intent);
						}
					});
					i++;
					imageView.invalidate();
				}
				layout.invalidate();
				mCampaigndetailAlbumListLayout.addView(layout);
				mCampaigndetailAlbumListLayout.invalidate();
			}
			mCampaigndetailAlbumListLayout.invalidate();
		} catch (Exception e) {
			finishActivity();
		}
	}
}
