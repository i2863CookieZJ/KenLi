package com.appsdk.video.gui;

import org.videolan.libvlc.EventHandler;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.appsdk.advancedimageview.listener.AdvancedImageViewLoadListener;
import com.appsdk.video.AdvancedVideoView;
import com.appsdk.video.listener.AdvancedVideoViewListener;
import com.appsdk.video.listener.FullscreenAdvertiseListener;
import com.third.library.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FullscreenAdvertiseView implements AdvancedVideoViewListener {

	// System
	private Context mContext;
	private FullscreenAdvertiseListener mListener;
	private RelativeLayout mFullscreenAdvertiseLayout;
	// Wedget
	private AdvancedImageView mAdImage;
	private ImageButton mAdCloseBtn;
	private TextView mAdTimeText;
	// Variable
	@SuppressWarnings("unused")
	private String mAdImageUrl;
	private String mAdLinkUrl;
	private boolean mCanClose = true;
	private int mCountDown = 0;

	public FullscreenAdvertiseView(Context context, AdvancedVideoView videoView, RelativeLayout FullscreenAdvertiseLayout) {
		mContext = context;
		mFullscreenAdvertiseLayout = FullscreenAdvertiseLayout;
		initWidget();
	}

	public void showFullscreenAdvertiseView(String adImageUrl, String adLinkUrl, boolean canClose, int countDown, FullscreenAdvertiseListener listener) {
		if (mAdImage == null) {
			return;
		}
		if (!TextUtils.isEmpty(adImageUrl)) {
			mListener = listener;
			// set image
			mAdImageUrl = adImageUrl;
			mAdImage.setNetImage(adImageUrl);
			// set link url
			if (!TextUtils.isEmpty(adLinkUrl)) {
				mAdLinkUrl = adLinkUrl;
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
			// set close btn
			mCanClose = canClose;
			if (mCanClose) {
				mAdCloseBtn.setVisibility(View.VISIBLE);
				mAdCloseBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						hideFullscreenAdvertiseView();
						if (mListener != null) {
							mListener.onClose();
						}
					}
				});
			} else {
				mAdCloseBtn.setVisibility(View.GONE);
			}
			// Load image success
			mCountDown = countDown;
			mAdImage.setOnloadListener(new AdvancedImageViewLoadListener() {
				@Override
				public void onFinish(AdvancedImageView arg0, boolean arg1, Bitmap arg2) {
					// set count down
					if (mCountDown > 0) {
						mAdTimeText.setText(String.valueOf(mCountDown));
						mAdTimeText.setVisibility(View.VISIBLE);
						mHandler.postDelayed(mRunnable, 1000);
					} else {
						mAdTimeText.setVisibility(View.GONE);
					}
					// set visible
					if (mFullscreenAdvertiseLayout.getVisibility() != View.VISIBLE) {
						mFullscreenAdvertiseLayout.setVisibility(View.VISIBLE);
					}
				}
			});
		}
	}

	public void hideFullscreenAdvertiseView() {
		if (mFullscreenAdvertiseLayout.getVisibility() == View.VISIBLE) {
			mFullscreenAdvertiseLayout.setVisibility(View.GONE);
		}
	}

	private void initWidget() {
		mAdImage = (AdvancedImageView) mFullscreenAdvertiseLayout.findViewById(R.id.ad_image);
		mAdCloseBtn = (ImageButton) mFullscreenAdvertiseLayout.findViewById(R.id.ad_close_btn);
		mAdTimeText = (TextView) mFullscreenAdvertiseLayout.findViewById(R.id.ad_timetext);
	}

	@Override
	public void onEvent(AdvancedVideoView videoView, int env) {
		switch (env) {
		case EventHandler.MediaPlayerPositionChanged:
		case EventHandler.MediaPlayerPlaying:
			hideFullscreenAdvertiseView();
			break;
		}
	}

	private Handler mHandler = new Handler();

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			mCountDown--;
			if (mCountDown > 0) {
				mHandler.postDelayed(this, 1000);
				mAdTimeText.setText(String.valueOf(mCountDown));
			} else {
				if (mListener != null) {
					mListener.onTimeEnd();
				}
			}
		}
	};
}
