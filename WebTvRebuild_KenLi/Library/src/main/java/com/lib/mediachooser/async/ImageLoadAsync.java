package com.lib.mediachooser.async;

import java.io.File;

import com.squareup.picasso.Picasso;
import com.third.library.R;

import android.content.Context;
import android.widget.ImageView;

public class ImageLoadAsync extends MediaAsync<String, String, String> {
	private ImageView mImageView;
	private Context mContext;
	private int mWidth;

	public ImageLoadAsync(Context context, ImageView imageView, int width) {
		mImageView = imageView;
		mContext = context;
		mWidth = width;
	}

	@Override
	protected String doInBackground(String... params) {
		String url = params[0].toString();
		return url;
	}

	@Override
	protected void onPostExecute(String result) {
		Picasso.with(mContext).load(new File(result)).resize(mWidth, mWidth).centerCrop().placeholder(R.drawable.loading).into(mImageView);
	}
}
