package com.appsdk.video.gui;

import org.videolan.libvlc.EventHandler;

import com.appsdk.video.AdvancedVideoView;
import com.appsdk.video.listener.AdvancedVideoViewListener;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

public class ErrorView implements AdvancedVideoViewListener {

	// System
	private RelativeLayout mErrorLayout;

	public ErrorView(Context context, AdvancedVideoView videoView, RelativeLayout ErrorLayout) {
		mErrorLayout = ErrorLayout;
	}

	public void showErrorView() {
		if (mErrorLayout.getVisibility() != View.VISIBLE) {
			mErrorLayout.setVisibility(View.VISIBLE);
		}
	}

	public void hideErrorView() {
		if (mErrorLayout.getVisibility() == View.VISIBLE) {
			mErrorLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onEvent(AdvancedVideoView videoView, int env) {
		switch (env) {
		case EventHandler.MediaPlayerEncounteredError:
			showErrorView();
			break;
		case EventHandler.MediaParsedChanged:
		case EventHandler.MediaPlayerPositionChanged:
		case EventHandler.MediaPlayerPlaying:
			hideErrorView();
			break;
		}
	}
}
