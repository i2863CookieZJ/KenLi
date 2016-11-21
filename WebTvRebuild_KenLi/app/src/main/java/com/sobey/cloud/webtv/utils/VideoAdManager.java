package com.sobey.cloud.webtv.utils;

import org.json.JSONObject;
import org.videolan.libvlc.EventHandler;

import com.appsdk.video.AdvancedVideoView;
import com.appsdk.video.listener.AdvancedVideoViewListener;
import com.appsdk.video.listener.FullscreenAdvertiseListener;
import com.appsdk.video.obj.MediaObj;
import com.sobey.cloud.webtv.api.HttpInvokeIndividualUrl;
import com.sobey.cloud.webtv.api.News;

import android.content.Context;
import android.text.TextUtils;

public class VideoAdManager {
	private static final int mLoadingAdDuration = 5;

	private AdvancedVideoView mAdvancedVideoView;
	private MediaObj mMediaObj;
	private String mPauseAdUrl;
	private String mPauseAdHref;
	private String mEndAdUrl;
	private String mEndAdHref;

	public void setVideoAd(Context context, AdvancedVideoView advancedVideoView, MediaObj mediaObj, int positionId, String catalogId) {
		mAdvancedVideoView = advancedVideoView;
		mMediaObj = mediaObj;
		if (TextUtils.isEmpty(catalogId)) {
			mAdvancedVideoView.addMedia(mMediaObj, true, true);
			return;
		}

		// Loading Ad
		try {
			JSONObject parameter = new JSONObject();
			parameter.put("positionId", positionId);
			parameter.put("cushion", 1);
			parameter.put("catalogId", catalogId);
			News.getVideoAd(parameter, context, new HttpInvokeIndividualUrl.OnJsonObjectResultListener() {
				@Override
				public void onOK(JSONObject result) {
					if(mAdvancedVideoView==null)
						return;
					try {
						if (result.optString("returnCode").equalsIgnoreCase("100")) {
							JSONObject obj = result.optJSONArray("returnData").optJSONObject(0);
							if (obj != null && !TextUtils.isEmpty(obj.optString("src"))) {
								String mediaUrl = obj.optString("src");
								String href = obj.optString("href");
								String extension = mediaUrl.substring(mediaUrl.lastIndexOf(".") + 1);
								int duration = Integer.valueOf(obj.optString("duration", String.valueOf(mLoadingAdDuration)));
								if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif")) {
									mAdvancedVideoView.showFullscreenAdvertiseView(mediaUrl, href, false, duration, new FullscreenAdvertiseListener() {
										@Override
										public void onTimeEnd() {
											try {
												mAdvancedVideoView.hideFullscreenAdvertiseView();
												mAdvancedVideoView.showLoadingView(true);
												mAdvancedVideoView.addMedia(mMediaObj, true, true);
											} catch (Exception e) {
												mAdvancedVideoView.addMedia(mMediaObj, true, true);
											}
										}

										@Override
										public void onClose() {
											mAdvancedVideoView.addMedia(mMediaObj, true, true);
										}
									});
								} else {
									mAdvancedVideoView.addMedia(mMediaObj, true, true);
								}
							} else {
								mAdvancedVideoView.addMedia(mMediaObj, true, true);
							}
						} else {
							mAdvancedVideoView.addMedia(mMediaObj, true, true);
						}
					} catch (Exception e) {
						mAdvancedVideoView.addMedia(mMediaObj, true, true);
					}
				}

				@Override
				public void onNG(String reason) {
					mAdvancedVideoView.addMedia(mMediaObj, true, true);
				}

				@Override
				public void onCancel() {
					mAdvancedVideoView.addMedia(mMediaObj, true, true);
				}
			});
		} catch (Exception e) {
			mAdvancedVideoView.addMedia(mediaObj, true, true);
		}

		// Pause Ad
		try {
			JSONObject parameter = new JSONObject();
			parameter.put("positionId", positionId);
			parameter.put("cushion", 2);
			parameter.put("catalogId", catalogId);
			News.getVideoAd(parameter, context, new HttpInvokeIndividualUrl.OnJsonObjectResultListener() {
				@Override
				public void onOK(JSONObject result) {
					try {
						if (result.optString("returnCode").equalsIgnoreCase("100")) {
							JSONObject obj = result.optJSONArray("returnData").optJSONObject(0);
							if (obj != null && !TextUtils.isEmpty(obj.optString("src"))) {
								String mediaUrl = obj.optString("src");
								String href = obj.optString("href");
								String extension = mediaUrl.substring(mediaUrl.lastIndexOf(".") + 1);
								if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif")) {
									mPauseAdUrl = mediaUrl;
									mPauseAdHref = href;
								}
							} else {
								mPauseAdUrl = null;
								mPauseAdHref = null;
							}
						} else {
							mPauseAdUrl = null;
							mPauseAdHref = null;
						}
					} catch (Exception e) {
						mPauseAdUrl = null;
						mPauseAdHref = null;
					}
				}

				@Override
				public void onNG(String reason) {
					mPauseAdUrl = null;
					mPauseAdHref = null;
				}

				@Override
				public void onCancel() {
					mPauseAdUrl = null;
					mPauseAdHref = null;
				}
			});
		} catch (Exception e) {
			mPauseAdUrl = null;
			mPauseAdHref = null;
		}

		// End Ad
		try {
			JSONObject parameter = new JSONObject();
			parameter.put("positionId", positionId);
			parameter.put("cushion", 3);
			parameter.put("catalogId", catalogId);
			News.getVideoAd(parameter, context, new HttpInvokeIndividualUrl.OnJsonObjectResultListener() {
				@Override
				public void onOK(JSONObject result) {
					try {
						if (result.optString("returnCode").equalsIgnoreCase("100")) {
							JSONObject obj = result.optJSONArray("returnData").optJSONObject(0);
							if (obj != null && !TextUtils.isEmpty(obj.optString("src"))) {
								String mediaUrl = obj.optString("src");
								String href = obj.optString("href");
								String extension = mediaUrl.substring(mediaUrl.lastIndexOf(".") + 1);
								if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif")) {
									mEndAdUrl = mediaUrl;
									mEndAdHref = href;
								}
							} else {
								mEndAdUrl = null;
								mEndAdHref = null;
							}
						} else {
							mEndAdUrl = null;
							mEndAdHref = null;
						}
					} catch (Exception e) {
						mEndAdUrl = null;
						mEndAdHref = null;
					}
				}

				@Override
				public void onNG(String reason) {
					mEndAdUrl = null;
					mEndAdHref = null;
				}

				@Override
				public void onCancel() {
					mEndAdUrl = null;
					mEndAdHref = null;
				}
			});
		} catch (Exception e) {
			mEndAdUrl = null;
			mEndAdHref = null;
		}

		mAdvancedVideoView.setOnAdvancedVideoViewListener(new AdvancedVideoViewListener() {
			@Override
			public void onEvent(AdvancedVideoView videoView, int env) {
				switch (env) {
				case EventHandler.MediaPlayerPaused:
					if (!TextUtils.isEmpty(mPauseAdUrl)) {
						mAdvancedVideoView.showPauseAdvertiseView(mPauseAdUrl, mPauseAdHref, true);
					}
					break;
				case EventHandler.MediaPlayerEndReached:
					if (!TextUtils.isEmpty(mEndAdUrl)) {
						mAdvancedVideoView.showPauseAdvertiseView(mEndAdUrl, mEndAdHref, true);
					}
					break;
				}
			}
		});
	}
}
