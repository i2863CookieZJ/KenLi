package com.lib.mediachooser.fragment;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lib.mediachooser.MediaChooserConstants;
import com.lib.mediachooser.MediaModel;
import com.lib.mediachooser.activity.ImageShowActivity;
import com.lib.mediachooser.adapter.GridViewAdapter;
import com.lib.mediachooser.adapter.GridViewAdapter.SelectedIconClickListener;
import com.third.library.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class ImageFragment extends Fragment {
	private ArrayList<String> mSelectedItems = new ArrayList<String>();
	private ArrayList<MediaModel> mGalleryModelList;
	private GridView mImageGridView;
	private View mView;
	private OnImageSelectedListener mCallback;
	private GridViewAdapter mImageAdapter;
	private Cursor mImageCursor;

	public interface OnImageSelectedListener {
		public void onImageSelected(int count);
		public void onImageClick(int position);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnImageSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnImageSelectedListener");
		}
	}

	public ImageFragment() {
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.view_grid_layout_media_chooser, container, false);
			mImageGridView = (GridView) mView.findViewById(R.id.gridViewFromMediaChooser);
			if (getArguments() != null && getArguments().getString("name") != null) {
				initPhoneImages(getArguments().getString("name"));
			} else if (getArguments() != null && getArguments().getString("path") != null) {
				initPhoneImagesUsePath(getArguments().getString("path"));
			} else {
				initPhoneImages();
			}
		} else {
			((ViewGroup) mView.getParent()).removeView(mView);
			if (mImageAdapter == null || mImageAdapter.getCount() == 0) {
				Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
			}
		}
		return mView;
	}

	private void initPhoneImages(String bucketName) {
		try {
			final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
			String searchParams = null;
			String bucket = bucketName;
			searchParams = "bucket_display_name = \"" + bucket + "\"";
			final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
			mImageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, searchParams, null, orderBy + " DESC");
			setAdapter(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initPhoneImagesUsePath(String path) {
		try {
			File file = new File(path);
			ArrayList<MediaModel> list = new ArrayList<MediaModel>();
			if (file.isDirectory()) {
				File[] fileArray = file.listFiles();
				if (null != fileArray && 0 != fileArray.length) {
					for (int i = 0; i < fileArray.length; i++) {
						list.add(new MediaModel(fileArray[i].getAbsolutePath(), false));
					}
				}
			}
			setAdapter(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initPhoneImages() {
		try {
			final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
			final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
			mImageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
			setAdapter(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setAdapter(ArrayList<MediaModel> list) {
		if (list == null) {
			if (mImageCursor.getCount() > 0) {
				mGalleryModelList = new ArrayList<MediaModel>();
				for (int i = 0; i < mImageCursor.getCount(); i++) {
					mImageCursor.moveToPosition(i);
					int dataColumnIndex = mImageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
					MediaModel galleryModel = new MediaModel(mImageCursor.getString(dataColumnIndex).toString(), false);
					mGalleryModelList.add(galleryModel);
				}
				mImageAdapter = new GridViewAdapter(getActivity(), 0, mGalleryModelList, false);
				mImageGridView.setAdapter(mImageAdapter);
			} else {
				Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
			}
		} else {
			if (list.size() > 0) {
				mGalleryModelList = list;
				mImageAdapter = new GridViewAdapter(getActivity(), 0, mGalleryModelList, false);
				mImageGridView.setAdapter(mImageAdapter);
			} else {
				Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
			}
		}

		mImageAdapter.setOnSelectedIconClickListener(new SelectedIconClickListener() {
			@Override
			public void onClick(int position) {
				GridViewAdapter adapter = mImageAdapter;
				MediaModel galleryModel = (MediaModel) adapter.getItem(position);
				if (!galleryModel.status) {
					long size = MediaChooserConstants.ChekcMediaFileSize(new File(galleryModel.url), false);
					if (size != 0) {
						Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.file_size_exeeded) + "  " + MediaChooserConstants.SELECTED_IMAGE_SIZE_IN_MB + " " + getActivity().getResources().getString(R.string.mb), Toast.LENGTH_SHORT).show();
						return;
					}
					if (MediaChooserConstants.MAX_IMAGE_LIMIT <= mSelectedItems.size()) {
						Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.max_limit_file), Toast.LENGTH_SHORT).show();
					}
				}

				galleryModel.status = !galleryModel.status;
				mGalleryModelList.get(position).status = galleryModel.status;
				adapter.notifyDataSetChanged();

				if (galleryModel.status) {
					mSelectedItems.add(galleryModel.url);
				} else {
					mSelectedItems.remove(galleryModel.url);
				}

				if (mCallback != null) {
					mCallback.onImageSelected(mSelectedItems.size());
				}
			}
		});

		mImageGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					JSONArray information = new JSONArray();
					for (int i = 0; i < mGalleryModelList.size(); i++) {
						JSONObject object = new JSONObject();
						object.put("filepath", mGalleryModelList.get(i).url);
						object.put("status", mGalleryModelList.get(i).status);
						information.put(object);
					}
					Intent intent = new Intent(getActivity(), ImageShowActivity.class);
					intent.putExtra("information", information.toString());
					intent.putExtra("position", position);
					getActivity().startActivityForResult(intent, 0);
					mCallback.onImageClick(position);
				} catch (Exception e) {
				}
			}
		});
	}

	public ArrayList<String> getSelectedImageList() {
		return mSelectedItems;
	}

	public void addItem(String item) {
		if (mImageAdapter != null) {
			MediaModel model = new MediaModel(item, false);
			mGalleryModelList.add(0, model);
			mImageAdapter.notifyDataSetChanged();
		} else {
			initPhoneImages();
		}
	}
	
	public void refreshImageFragment(ArrayList<MediaModel> list) {
		mGalleryModelList = list;
		mSelectedItems = new ArrayList<String>();
		for(int i=0; i<mGalleryModelList.size(); i++) {
			if(mGalleryModelList.get(i).status) {
				mSelectedItems.add(mGalleryModelList.get(i).url);
			}
		}
		mCallback.onImageSelected(mSelectedItems.size());
		mImageAdapter.refreshAdapter(mGalleryModelList);
	}
}
