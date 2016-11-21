package com.appsdk.video.gui;

import org.videolan.libvlc.EventHandler;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.appsdk.advancedimageview.listener.AdvancedImageViewLoadListener;
import com.appsdk.video.AdvancedVideoView;
import com.appsdk.video.listener.AdvancedVideoViewListener;
import com.third.library.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class PauseAdvertiseView implements AdvancedVideoViewListener {

	// System
	private Context mContext;
	private RelativeLayout mPauseAdvertiseLayout;
	// Wedget
	private AdvancedImageView mAdImage;
	private ImageButton mAdCloseBtn;
	// Variable
	@SuppressWarnings("unused")
	private String mAdImageUrl;
	private String mAdLinkUrl;
	private boolean mCanClose = true;

	public PauseAdvertiseView(Context context, AdvancedVideoView videoView, RelativeLayout PauseAdvertiseLayout) {
		mContext = context;
		mPauseAdvertiseLayout = PauseAdvertiseLayout;
		initWidget();
	}

	public void showPauseAdvertiseView(String adImageUrl, String adLinkUrl, boolean canClose) {
		if (mAdImage == null) {
			return;
		}
		if (!TextUtils.isEmpty(adImageUrl)) {
			mAdImageUrl = adImageUrl;
			mAdImage.setNetImage(adImageUrl);
			mAdLinkUrl = adLinkUrl;
			// Load image success
			mCanClose = canClose;
			mAdImage.setOnloadListener(new AdvancedImageViewLoadListener() {
				@Override
				public void onFinish(AdvancedImageView arg0, boolean arg1, Bitmap arg2) {
					if (mPauseAdvertiseLayout.getVisibility() != View.VISIBLE) {
						mPauseAdvertiseLayout.setVisibility(View.VISIBLE);
					}
					if (mCanClose) {
						mAdCloseBtn.setVisibility(View.VISIBLE);
						mAdCloseBtn.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								hidePauseAdvertiseView();
							}
						});
					} else {
						mAdCloseBtn.setVisibility(View.GONE);
					}
				}
			});
			mAdImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (!TextUtils.isEmpty(mAdLinkUrl)) {
						String url;
						if (mAdLinkUrl.indexOf("http://") < 0) {
							url = "http://" + mAdLinkUrl;
						} else {
							url = mAdLinkUrl;
						}
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						Uri content_url = Uri.parse(url);
						intent.setData(content_url);
						mContext.startActivity(intent);
					}
				}
			});
		}
	}

	public void hidePauseAdvertiseView() {
		if (mPauseAdvertiseLayout.getVisibility() == View.VISIBLE) {
			mPauseAdvertiseLayout.setVisibility(View.GONE);
		}
	}

	private void initWidget() {
		mAdImage = (AdvancedImageView) mPauseAdvertiseLayout.findViewById(R.id.ad_image);
		mAdCloseBtn = (ImageButton) mPauseAdvertiseLayout.findViewById(R.id.ad_close_btn);
	}

	@Override
	public void onEvent(AdvancedVideoView videoView, int env) {
		switch (env) {
		case EventHandler.MediaPlayerStopped:
		case EventHandler.MediaPlayerPaused:
			// showPauseAdvertiseView(mCanClose);
			break;
		case EventHandler.MediaPlayerPositionChanged:
		case EventHandler.MediaPlayerPlaying:
			hidePauseAdvertiseView();
			break;
		}
	}
}
