package com.lib.mediachooser.fragment;

import java.io.File;
import java.util.ArrayList;

import com.lib.mediachooser.MediaChooserConstants;
import com.lib.mediachooser.MediaModel;
import com.lib.mediachooser.adapter.GridViewAdapter;
import com.lib.mediachooser.adapter.GridViewAdapter.SelectedIconClickListener;
import com.third.library.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class VideoFragment extends Fragment implements OnScrollListener {

	private final static Uri MEDIA_EXTERNAL_CONTENT_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	private final static String MEDIA_DATA = MediaStore.Video.Media.DATA;

	private GridViewAdapter mVideoAdapter;
	private GridView mVideoGridView;
	private Cursor mCursor;
	private int mDataColumnIndex;
	private ArrayList<String> mSelectedItems = new ArrayList<String>();
	private ArrayList<MediaModel> mGalleryModelList;
	private View mView;
	private OnVideoSelectedListener mCallback;

	public interface OnVideoSelectedListener {
		public void onVideoSelected(int count);

		public void onVideoClick(int position);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mCallback = (OnVideoSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnVideoSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public VideoFragment() {
		setRetainInstance(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.view_grid_layout_media_chooser, container, false);
			mVideoGridView = (GridView) mView.findViewById(R.id.gridViewFromMediaChooser);
			if (getArguments() != null && getArguments().getString("name") != null) {
				initVideos(getArguments().getString("name"));
			} else if (getArguments() != null && getArguments().getString("path") != null) {
				initVideosUsePath(getArguments().getString("path"));
			} else {
				initVideos();
			}
		} else {
			((ViewGroup) mView.getParent()).removeView(mView);
			if (mVideoAdapter == null || mVideoAdapter.getCount() == 0) {
				Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
			}
		}
		return mView;
	};

	private void initVideos(String bucketName) {
		try {
			final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
			String searchParams = null;
			searchParams = "bucket_display_name = \"" + bucketName + "\"";
			final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Video.Media._ID };
			mCursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, searchParams, null, orderBy + " DESC");
			setAdapter(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initVideosUsePath(String path) {
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

	private void initVideos() {
		try {
			final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
			String[] proj = { MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID };
			mCursor = getActivity().getContentResolver().query(MEDIA_EXTERNAL_CONTENT_URI, proj, null, null, orderBy + " DESC");
			setAdapter(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setAdapter(ArrayList<MediaModel> list) {
		if (list == null) {
			int count = mCursor.getCount();
			if (count > 0) {
				mDataColumnIndex = mCursor.getColumnIndex(MEDIA_DATA);
				mCursor.moveToFirst();
				mGalleryModelList = new ArrayList<MediaModel>();
				for (int i = 0; i < count; i++) {
					mCursor.moveToPosition(i);
					String url = mCursor.getString(mDataColumnIndex);
					mGalleryModelList.add(new MediaModel(url, false));
				}
				mVideoAdapter = new GridViewAdapter(getActivity(), 0, mGalleryModelList, true);
				mVideoAdapter.setVideoFragment(this);
				mVideoGridView.setAdapter(mVideoAdapter);
				mVideoGridView.setOnScrollListener(this);
			} else {
				Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
			}
		} else {
			if (list.size() > 0) {
				mGalleryModelList = list;
				mVideoAdapter = new GridViewAdapter(getActivity(), 0, mGalleryModelList, true);
				mVideoAdapter.setVideoFragment(this);
				mVideoGridView.setAdapter(mVideoAdapter);
				mVideoGridView.setOnScrollListener(this);
			} else {
				Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
			}
		}

		mVideoAdapter.setOnSelectedIconClickListener(new SelectedIconClickListener() {
			@Override
			public void onClick(int position) {
				GridViewAdapter adapter = mVideoAdapter;
				MediaModel galleryModel = (MediaModel) adapter.getItem(position);
				if (!galleryModel.status) {
					long size = MediaChooserConstants.ChekcMediaFileSize(new File(galleryModel.url.toString()), true);
					if (size != 0) {
						Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.file_size_exeeded) + "  " + MediaChooserConstants.SELECTED_VIDEO_SIZE_IN_MB + " " + getActivity().getResources().getString(R.string.mb), Toast.LENGTH_SHORT).show();
						return;
					}
					if (MediaChooserConstants.MAX_VIDEO_LIMIT <= mSelectedItems.size()) {
						Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.max_limit_file), Toast.LENGTH_SHORT).show();
					}
				}

				galleryModel.status = !galleryModel.status;
				mGalleryModelList.get(position).status = galleryModel.status;
				adapter.notifyDataSetChanged();

				if (galleryModel.status) {
					mSelectedItems.add(galleryModel.url.toString());
				} else {
					mSelectedItems.remove(galleryModel.url.toString().trim());
				}

				if (mCallback != null) {
					mCallback.onVideoSelected(mSelectedItems.size());
				}
			}
		});

		mVideoGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse(mGalleryModelList.get(position).url);
				intent.setDataAndType(uri, "video/mp4");
				startActivity(intent);
				mCallback.onVideoClick(position);
			}
		});

	}

	public void addItem(String item) {
		if (mVideoAdapter != null) {
			MediaModel model = new MediaModel(item, false);
			mGalleryModelList.add(0, model);
			mVideoAdapter.notifyDataSetChanged();
		} else {
			initVideos();
		}
	}

	public GridViewAdapter getAdapter() {
		if (mVideoAdapter != null) {
			return mVideoAdapter;
		}
		return null;
	}

	public ArrayList<String> getSelectedVideoList() {
		return mSelectedItems;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (view == mVideoGridView) {
			if (scrollState == SCROLL_STATE_FLING) {
			} else {
				mVideoAdapter.notifyDataSetChanged();
			}
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}
}
