package com.sobey.cloud.webtv.broke.util;

import java.util.ArrayList;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.sobey.cloud.webtv.kenli.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class BrokeCaptureAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<String> mImageFileList;
	private ArrayList<String> mVideoFileList;
	private OnDeleteIconClickListener mListener;

	public interface OnDeleteIconClickListener {
		void onClick(int position);
	}

	public BrokeCaptureAdapter(Context context, ArrayList<String> imageFileList, ArrayList<String> videoFileList, OnDeleteIconClickListener listener) {
		mContext = context;
		mImageFileList = imageFileList;
		mVideoFileList = videoFileList;
		mListener = listener;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mImageFileList.size() + mVideoFileList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.griditem_broke_capture, null);
			holder = new ViewHolder();
			holder.imagePreview = (AdvancedImageView) convertView.findViewById(R.id.preview_image);
			holder.videoIcon = (ImageView) convertView.findViewById(R.id.video_image);
			holder.deleteIcon = (ImageView) convertView.findViewById(R.id.delete_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position < mVideoFileList.size()) {
			holder.videoIcon.setVisibility(View.VISIBLE);
			holder.imagePreview.setLocalImage(mVideoFileList.get(position));
		} else {
			int positionImage = position - mVideoFileList.size();
			holder.videoIcon.setVisibility(View.GONE);
			holder.imagePreview.setLocalImage(mImageFileList.get(positionImage));
		}

		holder.deleteIcon.setTag(position);
		holder.deleteIcon.setOnClickListener(new OnClickListener() {
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
		AdvancedImageView imagePreview;
		ImageView videoIcon;
		ImageView deleteIcon;
	}

}
