package com.lib.mediachooser.adapter;

import java.util.List;

import com.lib.mediachooser.MediaModel;
import com.lib.mediachooser.async.ImageLoadAsync;
import com.lib.mediachooser.async.MediaAsync;
import com.lib.mediachooser.async.VideoLoadAsync;
import com.lib.mediachooser.fragment.VideoFragment;
import com.third.library.R;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class GridViewAdapter extends ArrayAdapter<MediaModel> {
	private static final int mColumnNum = 4;
	private VideoFragment mVideoFragment;
	private Context mContext;
	private List<MediaModel> mGalleryModelList;
	private int mWidth;
	private boolean mIsFromVideo;
	private SelectedIconClickListener mListener;

	public GridViewAdapter(Context context, int resource, List<MediaModel> categories, boolean isFromVideo) {
		super(context, resource, categories);
		mGalleryModelList = categories;
		mContext = context;
		mIsFromVideo = isFromVideo;
	}

	public int getCount() {
		return mGalleryModelList.size();
	}

	public void setVideoFragment(VideoFragment videoFragment) {
		mVideoFragment = videoFragment;
	}

	@Override
	public MediaModel getItem(int position) {
		return mGalleryModelList.get(position);
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
			viewInflater = LayoutInflater.from(getContext());
			convertView = viewInflater.inflate(R.layout.view_grid_item_media_chooser, parent, false);
			holder = new ViewHolder();
			holder.checkBoxTextView = (ImageView) convertView.findViewById(R.id.checkTextViewFromMediaChooserGridItemRowView);
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewFromMediaChooserGridItemRowView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		LayoutParams imageParams = (LayoutParams) holder.imageView.getLayoutParams();
		imageParams.width = mWidth / mColumnNum;
		imageParams.height = mWidth / mColumnNum;
		holder.imageView.setLayoutParams(imageParams);
		if (mIsFromVideo) {
			new VideoLoadAsync(mVideoFragment, holder.imageView, false, mWidth / mColumnNum).executeOnExecutor(MediaAsync.THREAD_POOL_EXECUTOR, mGalleryModelList.get(position).url.toString());
		} else {
			new ImageLoadAsync(mContext, holder.imageView, mWidth / mColumnNum).executeOnExecutor(MediaAsync.THREAD_POOL_EXECUTOR, mGalleryModelList.get(position).url.toString());
		}
		LayoutParams checkBoxParams = (LayoutParams) holder.checkBoxTextView.getLayoutParams();
		checkBoxParams.width = mWidth / mColumnNum / 3;
		checkBoxParams.height = mWidth / mColumnNum / 3;
		holder.checkBoxTextView.setLayoutParams(checkBoxParams);
		holder.checkBoxTextView.setSelected(mGalleryModelList.get(position).status);
		holder.checkBoxTextView.setTag(position);
		holder.checkBoxTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onClick((Integer) v.getTag());
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		ImageView imageView;
		ImageView checkBoxTextView;
	}

	public void setOnSelectedIconClickListener(SelectedIconClickListener listener) {
		mListener = listener;
	}
	
	public void refreshAdapter(List<MediaModel> list) {
		mGalleryModelList = list;
		notifyDataSetChanged();
	}

	public interface SelectedIconClickListener {
		void onClick(int position);
	}
}
