package com.appsdk.video.gui;

import org.videolan.libvlc.EventHandler;

import com.appsdk.video.AdvancedVideoView;
import com.appsdk.video.listener.AdvancedVideoViewListener;
import com.third.library.R;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class LoadingView implements AdvancedVideoViewListener {

	// System
	private RelativeLayout mLoadingLayout;
	// Wedget
	private ImageView mVolumeInfo;
	private ImageView mSeekbarInfo;
	@SuppressWarnings("unused")
	private ProgressBar mLoadingIcon;
	// Variable
	private boolean mShowInfoImage = true;

	public LoadingView(Context context, AdvancedVideoView videoView, RelativeLayout loadingLayout) {
		mLoadingLayout = loadingLayout;
		initWidget();
	}

	public void showLoadingView(boolean isShowInfoImage) {
		if (mLoadingLayout.getVisibility() != View.VISIBLE) {
			mLoadingLayout.setVisibility(View.VISIBLE);
		}
		mShowInfoImage = isShowInfoImage;
		if (isShowInfoImage) {
			mVolumeInfo.setVisibility(View.VISIBLE);
			mSeekbarInfo.setVisibility(View.VISIBLE);
		} else {
			mVolumeInfo.setVisibility(View.GONE);
			mSeekbarInfo.setVisibility(View.GONE);
		}
	}

	public void hideLoadingView() {
		if (mLoadingLayout.getVisibility() == View.VISIBLE) {
			mLoadingLayout.setVisibility(View.GONE);
		}
	}

	private void initWidget() {
		mVolumeInfo = (ImageView) mLoadingLayout.findViewById(R.id.volume_info);
		mSeekbarInfo = (ImageView) mLoadingLayout.findViewById(R.id.seekbar_info);
		mLoadingIcon = (ProgressBar) mLoadingLayout.findViewById(R.id.loading_icon);
	}

	@Override
	public void onEvent(AdvancedVideoView videoView, int env) {
		switch (env) {
		case EventHandler.MediaPlayerEncounteredError:
			hideLoadingView();
			break;
		case EventHandler.MediaParsedChanged:
			showLoadingView(mShowInfoImage);
			break;
		case EventHandler.MediaPlayerPositionChanged:
		case EventHandler.MediaPlayerPlaying:
			hideLoadingView();
			break;
		}
	}
}
