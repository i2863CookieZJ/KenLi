package com.sobey.cloud.webtv.views.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.appsdk.video.AdvancedVideoView;
import com.appsdk.video.obj.MediaObj;
import com.appsdk.video.obj.ResolutionList;
import com.appsdk.video.obj.ResolutionObj;
import com.dylan.common.animation.AnimationController;
import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.VideoAdManager;
import com.sobey.cloud.webtv.widgets.CustomTitleView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CampaignPlayerArticleDetailActivity extends BaseActivity {

	private ArrayList<ResolutionObj> resolutionObjs = new ArrayList<ResolutionObj>();
	private MediaObj mediaObj;
	private VideoAdManager mVideoAdManager = new VideoAdManager();
	private boolean isShowVideoPlayer = true;
	private JSONObject mInformation;

	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mBackRl;
	@GinInjectView(id = R.id.header_ctv)
	CustomTitleView mHeaderCtv;
	@GinInjectView(id = R.id.mCampaignPlayerArticleVideoView)
	AdvancedVideoView mCampaignPlayerArticleVideoView;
	@GinInjectView(id = R.id.mCampaignPlayerArticleImageLayout)
	LinearLayout mCampaignPlayerArticleImageLayout;

	@Override
	public int getContentView() {
		return R.layout.activity_campaign_player_article_detail;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setupNewsDetailActivity();
	}

	public void setupNewsDetailActivity() {
		mOpenLoadingIcon();

		try {
			String str = getIntent().getStringExtra("information");
			if (!TextUtils.isEmpty(str)) {
				mInformation = new JSONObject(str);
			} else {
				finishActivity();
			}
		} catch (Exception e) {
			finishActivity();
		}

		mHeaderCtv.setTitle(mInformation.optString("title"));

		mBackRl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishActivity();
			}
		});

		try {
			News.getActivityPlayerArticleDetail(mInformation.optString("player_id"), mInformation.optString("id"), this,
					new OnJsonArrayResultListener() {
						@Override
						public void onOK(JSONArray result) {
							try {
								mInformation = result.optJSONObject(0);
								loadContent();
								if (isShowVideoPlayer) {
									initVideo();
									CheckNetwork checkNetwork = new CheckNetwork(
											CampaignPlayerArticleDetailActivity.this);
									if (checkNetwork.check3GOnly(false, null) == CheckNetwork.MOBILE_ONLY) {
										AlertDialog.Builder builder = new AlertDialog.Builder(
												CampaignPlayerArticleDetailActivity.this);
										builder.setTitle("您现在使用的是3G网络，将耗费流量").setMessage("是否继续观看视频?");
										builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.dismiss();
												getOnlineVideoPath();
											}
										}).setNegativeButton("否", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.dismiss();
											}
										}).show();
									} else {
										getOnlineVideoPath();
									}
								} else {
									mCampaignPlayerArticleVideoView.setVisibility(View.GONE);
								}
							} catch (Exception e) {
							} finally {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Detail
	private void loadContent() {
		try {
			if (mInformation != null) {
				mCampaignPlayerArticleImageLayout.removeAllViews();
				if (!TextUtils.isEmpty(mInformation.optString("summary"))) {
					TextView textView = new TextView(this);
					textView.setTextColor(0xff000000);
					textView.setTextSize(18);
					textView.setPadding(15, 15, 15, 15);
					textView.setText(mInformation.optString("summary"));
					mCampaignPlayerArticleImageLayout.addView(textView);
				}

				if (!TextUtils.isEmpty(mInformation.optString("imagepath"))) {
					JSONArray imageArray = mInformation.optJSONArray("imagepath");
					for (int i = 0; i < imageArray.length(); i++) {
						JSONObject contentObject = imageArray.optJSONObject(i);
						String imageUrl = contentObject.optString("imagePath");
						if (!TextUtils.isEmpty(imageUrl)) {
							AdvancedImageView imageView = new AdvancedImageView(
									CampaignPlayerArticleDetailActivity.this);
							imageView.setLayoutParams(
									new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
							imageView.setLoadingImage(R.drawable.default_thumbnail_banner);
							SharedPreferences settings = this.getSharedPreferences("settings", 0);
							imageView.setImageDrawable(getResources().getDrawable(R.drawable.default_thumbnail_banner));
							CheckNetwork network = new CheckNetwork(this);
							boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
									|| network.getWifiState(false) == State.CONNECTED;
							if (isShowPicture)
								imageView.setNetImage(imageUrl);
							imageView.setFitWidth(true);
							imageView.setPadding(15, 8, 15, 8);
							mCampaignPlayerArticleImageLayout.addView(imageView);
						}
					}
				}

				if (!TextUtils.isEmpty(mInformation.optString("content"))) {
					JSONArray contentArray = mInformation.optJSONArray("content");
					if (contentArray.length() > 0) {
						isShowVideoPlayer = true;
					} else {
						isShowVideoPlayer = false;
					}
				} else {
					isShowVideoPlayer = false;
				}
			} else {
				isShowVideoPlayer = false;
			}
		} catch (Exception e) {
			isShowVideoPlayer = false;
		}
	}

	// Video
	private void initVideo() {
		try {
			mCampaignPlayerArticleVideoView.init();
			mCampaignPlayerArticleVideoView.showLoadingView(true);
		} catch (Exception e) {
		}
	}

	@Override
	public void onPause() {
		if (mCampaignPlayerArticleVideoView != null) {
			mCampaignPlayerArticleVideoView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (mCampaignPlayerArticleVideoView != null) {
			mCampaignPlayerArticleVideoView.onDestory();
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		if (mCampaignPlayerArticleVideoView != null) {
			mCampaignPlayerArticleVideoView.onResume(true);
		}
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mCampaignPlayerArticleVideoView != null) {
			mCampaignPlayerArticleVideoView.onConfigurationChanged();
		}
		super.onConfigurationChanged(newConfig);
	}

	private void getOnlineVideoPath() {
		JSONArray videoArray;
		try {
			if (mInformation != null) {
				videoArray = mInformation.getJSONArray("content");
				for (int i = 0; i < videoArray.length(); i++) {
					JSONObject contentObject = videoArray.optJSONObject(i);
					News.getVideoPath(contentObject.optString("videoid"), this, new OnJsonObjectResultListener() {
						@Override
						public void onOK(JSONObject result) {
							try {
								String str = result.getString("playerUrl");
								str = str.substring(str.indexOf("{"));
								JSONObject obj = new JSONObject(str);
								JSONArray formatUriArray = ((JSONObject) obj.getJSONArray("clips").get(0))
										.getJSONArray("urls");
								JSONArray formatArray = obj.getJSONArray("formats");
								String host = obj.getString("host");
								String p2p = obj.getString("p2p");
								for (int i = 0; i < formatUriArray.length(); i++) {
									resolutionObjs.add(new ResolutionObj(formatArray.get(i).toString(),
											(host + ((String) formatUriArray.get(i)) + p2p)));
								}
								mediaObj = new MediaObj(obj.optString("title"), new ResolutionList(resolutionObjs, 0),
										true);
								mVideoAdManager.setVideoAd(CampaignPlayerArticleDetailActivity.this,
										mCampaignPlayerArticleVideoView, mediaObj, result.optInt("positionId", 0),
										result.optString("catalogId"));
							} catch (Exception e) {
								e.printStackTrace();
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
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mCampaignPlayerArticleVideoView != null && mCampaignPlayerArticleVideoView.isFullScreen()
					&& keyCode == KeyEvent.KEYCODE_BACK) {
				mCampaignPlayerArticleVideoView.toggleFullScreen();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mLoadingIconLayout);
		}
	}

	public void mCloseLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.fadeOut(mLoadingIconLayout, 1000, 0);
		}
	}
}
