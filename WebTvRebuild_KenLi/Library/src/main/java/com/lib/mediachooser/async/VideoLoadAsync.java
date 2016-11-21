package com.lib.mediachooser.async;

import com.lib.mediachooser.GalleryCache;
import com.lib.mediachooser.GalleryRetainCache;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

public class VideoLoadAsync extends MediaAsync<String, String, String> {
	private Fragment mFragment;
	private ImageView mImageView;
	private static GalleryCache mCache;
	private boolean mIsScrolling;
	private int mWidth;

	public VideoLoadAsync(Fragment fragment, ImageView imageView, boolean isScrolling, int width) {
		mImageView = imageView;
		mFragment = fragment;
		mWidth = width;
		mIsScrolling = isScrolling;

		final int memClass = ((ActivityManager) fragment.getActivity().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		final int size = 1024 * 1024 * memClass / 8;

		GalleryRetainCache c = GalleryRetainCache.getOrCreateRetainableCache();
		mCache = c.mRetainedCache;

		if (mCache == null) {
			mCache = new GalleryCache(size, mWidth, mWidth);
			c.mRetainedCache = mCache;
		}
	}

	@Override
	protected String doInBackground(String... params) {
		String url = params[0].toString();
		return url;
	}

	@Override
	protected void onPostExecute(String result) {
		mCache.loadBitmap(mFragment, result, mImageView, mIsScrolling);
	}
}
