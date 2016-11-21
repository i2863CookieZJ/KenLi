package com.dylan.uiparts.mycamera;

import com.dylan.uiparts.mycamera.MyCameraLayout.CameraTakeMode;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

public class MyCameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private Size mPreviewSize;
	private Size mPictureSize;
	private Size mVideoSize;
	private int mWidth;
	private int mHeight;
	private CameraTakeMode mCameraTakeMode;

	public MyCameraPreview(Context context) {
		super(context);
	}

	@SuppressWarnings("deprecation")
	public MyCameraPreview(Context context, Camera camera, Size previewSize, Size pictureSize, Size videoSize) {
		super(context);
		mCamera = camera;
		mPreviewSize = previewSize;
		mPictureSize = pictureSize;
		mVideoSize = videoSize;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mHeight = ((Activity) context).getResources().getDisplayMetrics().heightPixels;
	}

	public void refreshPreview(Camera camera, CameraTakeMode cameraTakeMode) {
		try {
			mCameraTakeMode = cameraTakeMode;
			mCamera = camera;
			if (mCamera == null) {
				return;
			}
			mCamera.stopPreview();
			setCameraSettings();
			mCamera.setPreviewDisplay(mHolder);
			requestLayout();
			mCamera.startPreview();
		} catch (Exception e) {
			return;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (mHolder.getSurface() == null) {
			return;
		}
		refreshPreview(mCamera, mCameraTakeMode);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		refreshPreview(mCamera, mCameraTakeMode);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	private void setCameraSettings() {
		if (mCameraTakeMode == CameraTakeMode.PICTURE) {
			mCamera.setDisplayOrientation(90);
		} else if (mCameraTakeMode == CameraTakeMode.VIDEO) {
			mCamera.setDisplayOrientation(0);
		}
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
		mCamera.setParameters(parameters);
		if (mCameraTakeMode == CameraTakeMode.PICTURE) {
			setLayout(mPictureSize);
		} else if (mCameraTakeMode == CameraTakeMode.VIDEO) {
			setLayout(mVideoSize);
		}
	}

	private void setLayout(Size size) {
		if (size.width > size.height) {
			mWidth = (int) (((double) size.height / (double) size.width) * (double) mHeight);
		} else {
			mWidth = (int) (((double) size.width / (double) size.height) * (double) mHeight);
		}
		if (mCameraTakeMode == CameraTakeMode.PICTURE) {
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mWidth, mHeight);
			setLayoutParams(params);
		} else if (mCameraTakeMode == CameraTakeMode.VIDEO) {
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mHeight, mWidth);
			setLayoutParams(params);
		}
	}
}
