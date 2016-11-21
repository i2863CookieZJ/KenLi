package com.sobey.cloud.webtv.broke;

import com.dylan.common.animation.AnimationController;
import com.dylan.uiparts.mycamera.MyCameraLayout;
import com.dylan.uiparts.mycamera.MyCameraLayout.PictureOKClickListener;
import com.dylan.uiparts.mycamera.MyCameraLayout.VideoOKClickListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.lib.mediachooser.activity.HomeFragmentActivity;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BrokeCameraTakeActivity extends BaseActivity {

	private static final int OPEN_ANIMATION_DELAY = 1000;
	private static final int OPEN_ANIMATION_DURATION = 1000;

	@GinInjectView(id = R.id.mBrokeCameraTakeOpenLayout)
	LinearLayout mBrokeCameraTakeOpenLayout;

	@GinInjectView(id = R.id.mBrokeCameraTakeOpenImageTopPicture)
	ImageView mBrokeCameraTakeOpenImageTopPicture;

	@GinInjectView(id = R.id.mBrokeCameraTakeOpenImageTopVideo)
	ImageView mBrokeCameraTakeOpenImageTopVideo;

	@GinInjectView(id = R.id.mBrokeCameraTakeOpenImageBottom)
	ImageView mBrokeCameraTakeOpenImageBottom;

	@GinInjectView(id = R.id.mBrokeCameraTakeContentLayout)
	MyCameraLayout mBrokeCameraTakeContentLayout;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_broke_camera_take;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		init();
	}

	public void init() {
		boolean videoFlag = false;
		try {
			videoFlag = getIntent().getStringExtra("camera_take_mode").equalsIgnoreCase("video");
		} catch (Exception e) {
			videoFlag = false;
		}
		boolean result = false;
		if (videoFlag) {
			result = mBrokeCameraTakeContentLayout.init(MyCameraLayout.CameraTakeMode.VIDEO);
			mBrokeCameraTakeContentLayout.setOnVideoOKClickListener(new VideoOKClickListener() {
				@Override
				public void onClick(String directoryPath, int count) {
					if (count > 0) {
						Intent intent = new Intent(BrokeCameraTakeActivity.this, HomeFragmentActivity.class);
						intent.putExtra("path", directoryPath);
						intent.putExtra("isImage", false);
						BrokeCameraTakeActivity.this.startActivity(intent);
						finish();
					} else {
						Toast.makeText(BrokeCameraTakeActivity.this, "请先拍摄视频", Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else {
			result = mBrokeCameraTakeContentLayout.init(MyCameraLayout.CameraTakeMode.PICTURE);
			mBrokeCameraTakeContentLayout.setOnPictureOKClickListener(new PictureOKClickListener() {
				@Override
				public void onClick(String directoryPath, int count) {
					if (count > 0) {
						Intent intent = new Intent(BrokeCameraTakeActivity.this, HomeFragmentActivity.class);
						intent.putExtra("path", directoryPath);
						intent.putExtra("isImage", true);
						BrokeCameraTakeActivity.this.startActivity(intent);
						finish();
					} else {
						Toast.makeText(BrokeCameraTakeActivity.this, "请先拍摄照片", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		if (result) {
			openCameraPreview(videoFlag);
		} else {
			this.finish();
		}
	}

	private void openCameraPreview(boolean videoFlag) {
		if (videoFlag) {
			mBrokeCameraTakeOpenImageTopPicture.setVisibility(View.GONE);
			mBrokeCameraTakeOpenImageTopVideo.setVisibility(View.VISIBLE);
		} else {
			mBrokeCameraTakeOpenImageTopPicture.setVisibility(View.VISIBLE);
			mBrokeCameraTakeOpenImageTopVideo.setVisibility(View.GONE);
		}
		AnimationController animationController = new AnimationController();
		if (videoFlag) {
			animationController.slideOutUp(mBrokeCameraTakeOpenImageTopVideo, OPEN_ANIMATION_DURATION,
					OPEN_ANIMATION_DELAY);
		} else {
			animationController.slideOutUp(mBrokeCameraTakeOpenImageTopPicture, OPEN_ANIMATION_DURATION,
					OPEN_ANIMATION_DELAY);
		}
		animationController.slideOutDown(mBrokeCameraTakeOpenImageBottom, OPEN_ANIMATION_DURATION,
				OPEN_ANIMATION_DELAY);
		animationController.hide(mBrokeCameraTakeOpenLayout, OPEN_ANIMATION_DURATION + OPEN_ANIMATION_DELAY);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mBrokeCameraTakeContentLayout.onKeyBack()) {
				return false;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPause() {
		mBrokeCameraTakeContentLayout.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		mBrokeCameraTakeContentLayout.resume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mBrokeCameraTakeContentLayout.destory();
		super.onDestroy();
	}
}
