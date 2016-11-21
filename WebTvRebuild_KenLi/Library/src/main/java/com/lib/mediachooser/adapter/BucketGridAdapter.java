package com.lib.mediachooser.adapter;

import java.util.ArrayList;

import com.lib.mediachooser.BucketEntry;
import com.lib.mediachooser.async.ImageLoadAsync;
import com.lib.mediachooser.async.MediaAsync;
import com.lib.mediachooser.async.VideoLoadAsync;
import com.lib.mediachooser.fragment.BucketVideoFragment;
import com.third.library.R;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BucketGridAdapter extends ArrayAdapter<BucketEntry> {
	private static final int mColumnNum = 4;
	private BucketVideoFragment mBucketVideoFragment;
	private Context mContext;
	private ArrayList<BucketEntry> mBucketEntryList;
	private boolean mIsFromVideo;
	private int mWidth;

	public BucketGridAdapter(Context context, int resource, ArrayList<BucketEntry> categories, boolean isFromVideo) {
		super(context, resource, categories);
		mBucketEntryList = categories;
		mContext = context;
		mIsFromVideo = isFromVideo;
	}

	public int getCount() {
		return mBucketEntryList.size();
	}

	public void setBucketVideoFragment(BucketVideoFragment bucketVideoFragment) {
		mBucketVideoFragment = bucketVideoFragment;
	}

	@Override
	public BucketEntry getItem(int position) {
		return mBucketEntryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
			mWidth = display.getWidth(); // deprecated
			LayoutInflater viewInflater;
			viewInflater = LayoutInflater.from(mContext);
			convertView = viewInflater.inflate(R.layout.view_grid_bucket_item_media_chooser, parent, false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewFromMediaChooserBucketRowView);
			holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextViewFromMediaChooserBucketRowView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		RelativeLayout.LayoutParams imageParams = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
		imageParams.width = mWidth / mColumnNum;
		imageParams.height = mWidth / mColumnNum;

		holder.imageView.setLayoutParams(imageParams);

		if (mIsFromVideo) {
			new VideoLoadAsync(mBucketVideoFragment, holder.imageView, false, mWidth / mColumnNum).executeOnExecutor(MediaAsync.THREAD_POOL_EXECUTOR, mBucketEntryList.get(position).bucketUrl.toString());
		} else {
			new ImageLoadAsync(mContext, holder.imageView, mWidth / mColumnNum).executeOnExecutor(MediaAsync.THREAD_POOL_EXECUTOR, mBucketEntryList.get(position).bucketUrl.toString());
		}

		holder.nameTextView.setText(mBucketEntryList.get(position).bucketName);
		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView nameTextView;
	}
}
