package com.sobey.cloud.webtv.personal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.FileModel;
import com.sobey.cloud.webtv.bean.PhotoModel;
import com.sobey.cloud.webtv.utils.FileUtil;
import com.sobey.cloud.webtv.views.group.BaseActivity4Group;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class HeadImageChooseActivity extends BaseActivity4Group implements OnClickListener {
	@GinInjectView(id = R.id.title_left)
	Button titleLeft;
	@GinInjectView(id = R.id.title_right)
	Button titleRight;
	@GinInjectView(id = R.id.title_layout)
	View titleLayout;
	@GinInjectView(id = R.id.title)
	TextView title;
	@GinInjectView(id = R.id.activity_group_choose_photo_gridview)
	GridView gridView;
	@GinInjectView(id = R.id.activity_group_choose_photo_file_dir_layout)
	View moreContainerLayout;
	@GinInjectView(id = R.id.activity_group_choose_photo_file_dir_listview)
	ListView moreListView;
	private Animation anim_in;
	private Animation anim_out;
	private Runnable getDataRunnable;
	private HeadImageChoosephotoAdapter photoAdapter;
	private HeadImageFileChooseDirAdapter dirAdapter;
	private ArrayList<String> choosedPaths;

	@Override
	public int getContentView() {
		return R.layout.activity_headimage_choose;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setUpDatas();
	}

	public void setUpDatas() {
		title.setText(R.string.all_photos);
		// StringBuilder sb = new StringBuilder();
		// sb.append(getResources().getString(R.string.done))
		// .append("(").append(choosedPaths.size())
		// .append("/").append(SobeyConstants.choosePhotoMaxSize).append(")");
		// titleRight.setText(sb.toString());
		titleRight.setVisibility(View.INVISIBLE);
		gridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, false));
		titleLayout.setOnClickListener(this);
		moreContainerLayout.setOnClickListener(this);

		moreListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HeadImageFileChooseDirAdapter adapter = (HeadImageFileChooseDirAdapter) parent.getAdapter();
				List<FileModel> fileModels = adapter.getData();
				FileModel fileModel = fileModels.get(position);
				doTitleTextClickAction();
				// 本身是选中的不变化
				if (fileModel.choosed == 1) {
					return;
				} else {
					for (FileModel fModel : fileModels) {
						if (fModel.dir.equals(fileModel.dir)) {
							fModel.choosed = 1;
							if (getResources().getString(R.string.all_photos).equals(fModel.dir)) {
								photoAdapter.setData(getPhotoModels(fModel.filesPaths));
							} else {
								photoAdapter.setDataNoTakePhoto(getPhotoModels(fModel.filesPaths));
							}
							title.setText(fModel.dir);
							photoAdapter.notifyDataSetChanged();
							gridView.smoothScrollToPositionFromTop(0, 0);
						} else {
							fModel.choosed = 0;
						}
					}
				}
				dirAdapter.setData(fileModels);
				dirAdapter.notifyDataSetChanged();
			}
		});

		initAnim();
		getDatas();
	}

	private void initAnim() {
		anim_in = AnimationUtils.loadAnimation(this, R.anim.top_in);
		anim_out = AnimationUtils.loadAnimation(this, R.anim.top_out);
		anim_out.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				moreListView.setVisibility(View.INVISIBLE);
				moreContainerLayout.setVisibility(View.GONE);
			}
		});
	}

	private void doTitleTextClickAction() {
		Log.i(TAG, "title_layuout clicked!!");
		if (moreListView.getVisibility() == View.VISIBLE) {
			moreListView.startAnimation(anim_out);

		} else {
			moreContainerLayout.setVisibility(View.VISIBLE);
			moreListView.startAnimation(anim_in);
			moreListView.setVisibility(View.VISIBLE);

		}
	}

	private void getDatas() {
		getDataRunnable = new Runnable() {

			@Override
			public void run() {
				// List<String> paths =
				// FileUtil.getGalleryPhotos(GroupChoosePhotoActivity.this);
				Map<String, ArrayList<String>> allFolders = FileUtil
						.getGalleryPhotosWithFolder(HeadImageChooseActivity.this);
				ArrayList<FileModel> fileModels = getFileModels(allFolders);

				Message msg = new Message();
				Bundle data = new Bundle();
				data.putStringArrayList("all", allFolders.get("all*--"));
				data.putParcelableArrayList("fileModels", fileModels);
				msg.what = 0;
				msg.setData(data);
				mHandler.sendMessage(msg);
			}
		};
		new Thread(getDataRunnable).start();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<FileModel> getFileModels(Map<String, ArrayList<String>> allFolders) {
		ArrayList<FileModel> fileModels = new ArrayList<FileModel>();
		Iterator i = allFolders.entrySet().iterator();
		while (i.hasNext()) {
			FileModel fileModel = new FileModel();
			Map.Entry e = (Map.Entry) i.next();
			String key = (String) e.getKey();
			ArrayList<String> filePaths = (ArrayList<String>) e.getValue();
			if ("all*--".equals(key)) {
				fileModel.order = 1;
				key = getResources().getString(R.string.all_photos);
				fileModel.choosed = 1;
			} else {
				fileModel.order = 2;
			}
			fileModel.filesPaths = filePaths;
			fileModel.dir = key;
			fileModel.dirFirstPhotoPath = filePaths.get(0);
			fileModel.filesCount = filePaths.size();
			fileModels.add(fileModel);
		}
		Collections.sort(fileModels);
		return fileModels;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:

			this.finish();

			break;
		case R.id.title_right:
			this.finish();
			break;
		case R.id.title:

			break;
		case R.id.activity_group_choose_photo_file_dir_layout:
		case R.id.title_layout:
			doTitleTextClickAction();
			break;

		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		if (msg.what == 0) {
			Bundle bundle = msg.getData();
			ArrayList<String> paths = (ArrayList<String>) bundle.get("all");
			ArrayList<FileModel> fileModels = (ArrayList<FileModel>) bundle.get("fileModels");

			if (null == photoAdapter) {
				photoAdapter = new HeadImageChoosephotoAdapter(this, mHandler);
				if (null != choosedPaths) {
					photoAdapter.setChoosedPhotoPath(choosedPaths);
				}
			}
			photoAdapter.setData(getPhotoModels(paths));
			gridView.setAdapter(photoAdapter);

			if (null == dirAdapter) {
				dirAdapter = new HeadImageFileChooseDirAdapter(this);
			}
			dirAdapter.setData(fileModels);
			moreListView.setAdapter(dirAdapter);
		} else if (msg.what == 1) {
			// choosedPaths = photoAdapter.getChoosedPhotoPath();
			// StringBuilder sb = new StringBuilder();
			// sb.append(getResources().getString(R.string.done))
			// .append("(").append(choosedPaths.size())
			// .append("/").append(SobeyConstants.choosePhotoMaxSize).append(")");
			// titleRight.setText(sb.toString());
			String chooseFilePath = msg.obj.toString();
			if (!TextUtils.isEmpty(chooseFilePath)) {
				Intent intent = new Intent();
				intent.putExtra("path", chooseFilePath);
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}

	private List<PhotoModel> getPhotoModels(ArrayList<String> paths) {
		List<PhotoModel> datas = new ArrayList<PhotoModel>();
		for (String path : paths) {
			PhotoModel photoModel = new PhotoModel();
			photoModel.path = "file://" + path;
			if (null != choosedPaths && choosedPaths.contains(path)) {
				photoModel.choosed = true;
			} else {
				photoModel.choosed = false;
			}
			datas.add(photoModel);
		}
		return datas;
	}
}