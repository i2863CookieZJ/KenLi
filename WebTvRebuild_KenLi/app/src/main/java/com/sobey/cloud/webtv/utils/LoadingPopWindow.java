package com.sobey.cloud.webtv.utils;

import com.sobey.cloud.webtv.kenli.R;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class LoadingPopWindow {
	private PopupWindow mLoadingPopWindow = null;
	private View attachView;
	private RelativeLayout mLoadingLayout;
	private int mHeight;
	private int mOffsetY;
	private int mScreenWidth;

	public LoadingPopWindow(final Context context, View attachView, int height, int offset_y, int color) {
		this.attachView = attachView;
		View mLoadingView = LayoutInflater.from(context).inflate(R.layout.layout_popwindow_loading, null);
		mLoadingPopWindow = new PopupWindow(mLoadingView);
		mLoadingPopWindow.setAnimationStyle(R.style.MyPopwindowAnimation);
		mLoadingLayout = (RelativeLayout) mLoadingView.findViewById(R.id.loading_layout);
		mLoadingLayout.setBackgroundColor(color);

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		// mHeight = ScaleConversion.dip2px(context, 300);
		mHeight = height;
		mOffsetY = offset_y;
		mScreenWidth = metrics.widthPixels;
	}

	public void showLoadingWindow() {
		if (!mLoadingPopWindow.isShowing()) {
			mLoadingPopWindow.showAsDropDown(attachView);
			mLoadingPopWindow.update(0, mOffsetY, mScreenWidth, mHeight);
		}
	}

	public void hideLoadingWindow() {
		if (mLoadingPopWindow.isShowing()) {
			if(attachView != null) {
				mLoadingPopWindow.dismiss();
			}
		}
	}

	public boolean isShowing() {
		return mLoadingPopWindow.isShowing();
	}

	public void updataOffset(int x, int y) {
		mLoadingPopWindow.update(x, y, -1, -1);
	}
}
