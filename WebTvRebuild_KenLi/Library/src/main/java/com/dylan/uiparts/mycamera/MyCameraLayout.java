package com.dylan.uiparts.mycamera;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import com.dylan.common.utils.DateParse;
import com.dylan.common.utils.FileManager;
import com.dylan.common.utils.GSensor;
import com.third.library.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyCameraLayout extends FrameLayout {
	private static final String MYCAMERA_SAVEPATH = "/WebTV";
	private static final String MYCAMERA_SAVEPREFIX = "WebTV_";
	private static final String MYCAMERA_IMAGEPATH = Environment.getExternalStorageDirectory().getPath() + MYCAMERA_SAVEPATH + "/image/";
	private static final String MYCAMERA_TEMPPATH = Environment.getExternalStorageDirectory().getPath() + MYCAMERA_SAVEPATH + "/temp/";
	private static final String MYCAMERA_VIDEOPATH = Environment.getExternalStorageDirectory().getPath() + MYCAMERA_SAVEPATH + "/video/";
	private String mCameraSaveFolder;

	public enum CameraTakeMode {
		PICTURE, VIDEO
	}

	public interface PictureOKClickListener {
		void onClick(String directoryPath, int count);
	}

	public interface VideoOKClickListener {
		void onClick(String directoryPath, int count);
	}

	private Context mContext;
	private MyCameraPreview mMyCameraPreview;
	private Camera mCamera;
	private GSensor mGSensor;
	private CameraTakeMode mCameraTakeMode = CameraTakeMode.PICTURE;
	private int mCameraId = 0;
	private boolean mRecordingFlag = false;
	private MediaRecorder mMediaRecorder;
	private File mMediaRecorderAudioFile;
	private Handler mCameraTimerHandler = new Handler();
	private String mCameraTimerStartTime = null;
	private String mFlashLightMode = Camera.Parameters.FLASH_MODE_AUTO;
	private LinkedList<String> mPicturePathList = new LinkedList<String>();
	private LinkedList<String> mVideoPathList = new LinkedList<String>();
	private Size mPreviewSize;
	private Size mPictureSize;
	private Size mVideoSize;
	private int mRotateDegree = 0;
	private boolean mAutoFocus = false;
	private boolean mOnTake = false;
	private PictureOKClickListener mPictureOKClickListener;
	private VideoOKClickListener mVideoOKClickListener;

	private RelativeLayout mMyCameraLayout;
	private FrameLayout mMyCameraPreviewLayout;
	private ImageView mMyCameraControlerCameraChange;
	private TextView mMyCameraControlerInfoText;
	private TextView mMyCameraControlerFlashlightText;
	private ImageButton mMyCameraPictureControlerCaptureBtn;
	private ImageButton mMyCameraPictureControlerCancelBtn;
	private ImageButton mMyCameraPictureControlerOKBtn;
	private ImageButton mMyCameraVideoControlerCaptureBtn;
	private ImageButton mMyCameraVideoControlerCancelBtn;
	private ImageButton mMyCameraVideoControlerOKBtn;

	public MyCameraLayout(Context context) {
		super(context);
	}

	public MyCameraLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyCameraLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean init(CameraTakeMode cameraTakeMode) {
		try {
			mContext = getContext();
			mCameraTakeMode = cameraTakeMode;
			mGSensor = new GSensor(getContext());
			mCameraSaveFolder = String.valueOf(System.currentTimeMillis()) + "/";

			if (!MyCameraUtil.checkCameraHardware(mContext)) {
				Toast.makeText(mContext, "该手机没有摄像头设备，不能进行该操作！", Toast.LENGTH_SHORT).show();
				return false;
			}

			if (mCamera != null) {
				destory();
			}
			mCamera = MyCameraUtil.getCameraInstance(mCameraId);
			if (mCamera == null) {
				Toast.makeText(mContext, "摄像头初始化失败，请稍后重试！", Toast.LENGTH_SHORT).show();
				return false;
			}
			calculateSize(mCamera);
			if (mPreviewSize == null || mPictureSize == null || mVideoSize == null) {
				Toast.makeText(mContext, "摄像头初始化失败，请稍后重试！", Toast.LENGTH_SHORT).show();
				return false;
			}

			if (mMyCameraLayout == null) {
				mMyCameraLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_mycamera, null);
				this.addView(mMyCameraLayout);
			}
			if (mMyCameraPreview == null) {
				mMyCameraPreview = new MyCameraPreview(mContext, mCamera, mPreviewSize, mPictureSize, mVideoSize);
				mMyCameraPreviewLayout = (FrameLayout) mMyCameraLayout.findViewById(R.id.mycamera_preview_layout);
				mMyCameraPreviewLayout.addView(mMyCameraPreview);
				mMyCameraPreview.refreshPreview(mCamera, mCameraTakeMode);
			} else {
				mMyCameraPreview.refreshPreview(mCamera, mCameraTakeMode);
			}

			if (mCameraTakeMode == CameraTakeMode.PICTURE) {
				headerControlerInit(mMyCameraLayout);
				pictureControlerInit(mMyCameraLayout);
			} else if (mCameraTakeMode == CameraTakeMode.VIDEO) {
				Toast.makeText(mContext, "请横屏拍摄，效果最好", Toast.LENGTH_SHORT).show();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (((Activity) mContext).getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
							((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						}
					}
				}, 1900);
				headerControlerInit(mMyCameraLayout);
				videoControlerInit(mMyCameraLayout);
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void changeCameraTakeMode() {
		if (mCameraTakeMode == CameraTakeMode.PICTURE) {
			mCameraTakeMode = CameraTakeMode.VIDEO;
		} else if (mCameraTakeMode == CameraTakeMode.VIDEO) {
			mCameraTakeMode = CameraTakeMode.PICTURE;
		}
		init(mCameraTakeMode);
	}

	public void setOnVideoOKClickListener(VideoOKClickListener listener) {
		mVideoOKClickListener = listener;
	}

	public void setOnPictureOKClickListener(PictureOKClickListener listener) {
		mPictureOKClickListener = listener;
	}

	private void headerControlerInit(RelativeLayout layout) {
		mMyCameraControlerCameraChange = (ImageView) layout.findViewById(R.id.mycamera_controler_camera_image);
		mMyCameraControlerFlashlightText = (TextView) layout.findViewById(R.id.mycamera_controler_flashlight_text);
		mMyCameraControlerInfoText = (TextView) layout.findViewById(R.id.mycamera_controler_info_text);

		mMyCameraControlerCameraChange.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!changeCamera()) {
					((Activity) mContext).finish();
				}
			}
		});
		setFlashLight(mFlashLightMode);
		mMyCameraControlerFlashlightText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mFlashLightMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF)) {
					mFlashLightMode = Camera.Parameters.FLASH_MODE_AUTO;
				} else if (mFlashLightMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON)) {
					mFlashLightMode = Camera.Parameters.FLASH_MODE_OFF;
				} else if (mFlashLightMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_AUTO)) {
					mFlashLightMode = Camera.Parameters.FLASH_MODE_ON;
				}
				setFlashLight(mFlashLightMode);
			}
		});
		if (mCameraTakeMode == CameraTakeMode.PICTURE) {
			mMyCameraControlerInfoText.setText("已拍" + mPicturePathList.size() + "张");
		} else if (mCameraTakeMode == CameraTakeMode.VIDEO) {
			mMyCameraControlerInfoText.setText("已拍" + mVideoPathList.size() + "个视频");
		}
	}

	private void pictureControlerInit(RelativeLayout layout) {
		((RelativeLayout) layout.findViewById(R.id.mycamera_picture_controler_layout)).setVisibility(View.VISIBLE);
		((RelativeLayout) layout.findViewById(R.id.mycamera_video_controler_layout)).setVisibility(View.GONE);
		mMyCameraPictureControlerCaptureBtn = (ImageButton) layout.findViewById(R.id.mycamera_picture_controler_capture_btn);
		mMyCameraPictureControlerCancelBtn = (ImageButton) layout.findViewById(R.id.mycamera_picture_controler_cancel_btn);
		mMyCameraPictureControlerOKBtn = (ImageButton) layout.findViewById(R.id.mycamera_picture_controler_ok_btn);

		mMyCameraPictureControlerCaptureBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN && mAutoFocus == false) {
					updateRotateDegree();
					mMyCameraPictureControlerCaptureBtn.setBackgroundResource(R.drawable.mycamera_controler_capture_icon_forcus);
					mCamera.autoFocus(null);
					mAutoFocus = true;
					mOnTake = false;
				}
				if (event.getAction() == MotionEvent.ACTION_UP && mAutoFocus && !mOnTake) {
					mOnTake = true;
					mMyCameraPictureControlerCaptureBtn.setBackgroundResource(R.drawable.mycamera_controler_capture_icon_normal);
					mCamera.takePicture(null, null, mPictureCallback);
				}
				return true;
			}
		});
		mMyCameraPictureControlerCancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FileManager.deleteDirectory(MYCAMERA_IMAGEPATH + mCameraSaveFolder);
				FileManager.deleteDirectory(MYCAMERA_VIDEOPATH + mCameraSaveFolder);
				FileManager.deleteDirectory(MYCAMERA_TEMPPATH);
				((Activity) mContext).finish();
			}
		});
		mMyCameraPictureControlerOKBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPictureOKClickListener != null) {
					mPictureOKClickListener.onClick(MYCAMERA_IMAGEPATH + mCameraSaveFolder, mPicturePathList.size());
				}
			}
		});
	}

	private PictureCallback mPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			String saveName = MYCAMERA_SAVEPREFIX + System.currentTimeMillis() + ".png";
			File pictureFile = new File(MYCAMERA_IMAGEPATH + mCameraSaveFolder + saveName);
			if (!pictureFile.getParentFile().exists()) {
				pictureFile.getParentFile().mkdirs();
			}
			if (pictureFile.exists()) {
				pictureFile.delete();
			}
			mMyCameraPreview.requestLayout();
			mCamera.startPreview();
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			Matrix matrix = new Matrix();
			matrix.postRotate(mRotateDegree);
			Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(pictureFile);
				rotateBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.close();
				mPicturePathList.add(MYCAMERA_IMAGEPATH + mCameraSaveFolder + saveName);
				mMyCameraControlerInfoText.setText("已拍" + mPicturePathList.size() + "张");
			} catch (Exception e) {
				Toast.makeText(getContext(), "保存图片失败", Toast.LENGTH_SHORT).show();
				destory();
				init(mCameraTakeMode);
			}
			mAutoFocus = false;
			mOnTake = false;
		}
	};

	private void videoControlerInit(RelativeLayout layout) {
		File file = new File(MYCAMERA_TEMPPATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		((RelativeLayout) layout.findViewById(R.id.mycamera_picture_controler_layout)).setVisibility(View.GONE);
		((RelativeLayout) layout.findViewById(R.id.mycamera_video_controler_layout)).setVisibility(View.VISIBLE);
		mMyCameraVideoControlerCaptureBtn = (ImageButton) layout.findViewById(R.id.mycamera_video_controler_capture_btn);
		mMyCameraVideoControlerCancelBtn = (ImageButton) layout.findViewById(R.id.mycamera_video_controler_cancel_btn);
		mMyCameraVideoControlerOKBtn = (ImageButton) layout.findViewById(R.id.mycamera_video_controler_ok_btn);

		mMyCameraVideoControlerCaptureBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (!mRecordingFlag) {
						mMyCameraVideoControlerCaptureBtn.setBackgroundResource(R.drawable.mycamera_controler_capture_icon_forcus);
						mMyCameraControlerInfoText.setText("00:00:00");
						mCameraTimerStartTime = DateParse.getNowDate(null);
						mCameraTimerHandler.postDelayed(mCameraTimerTask, 1000);
						disableControler();
						updateRotateDegree();
						mRecordingFlag = true;
						if (mMediaRecorder == null) {
							mMediaRecorder = new MediaRecorder();
						} else {
							mMediaRecorder.reset();
						}
						mCamera.unlock();
						mMediaRecorder.setCamera(mCamera);
						mMediaRecorder.setPreviewDisplay(mMyCameraPreview.getHolder().getSurface());
						mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
						mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
						mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
						mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
						mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
						mMediaRecorder.setVideoSize(mVideoSize.width, mVideoSize.height);
//						mMediaRecorder.setVideoFrameRate(15);
						mMediaRecorder.setVideoEncodingBitRate(1280*720);
						mMediaRecorder.setOrientationHint(mRotateDegree);
						mMediaRecorderAudioFile = File.createTempFile("Video", ".3gp", new File(MYCAMERA_TEMPPATH));
						mMediaRecorder.setOutputFile(mMediaRecorderAudioFile.getAbsolutePath());
						mMediaRecorder.prepare();
						mMediaRecorder.start();
					} else {
						mMyCameraVideoControlerCaptureBtn.setBackgroundResource(R.drawable.mycamera_controler_capture_icon_normal);
						mCameraTimerStartTime = null;
						enableControler();
						mRecordingFlag = false;
						mMediaRecorder.stop();
						mMediaRecorder.release();
						mMediaRecorder = null;
						mCamera.lock();
						if (videoMove()) {
							Toast.makeText(mContext, "视频保存成功", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(mContext, "视频保存失败", Toast.LENGTH_SHORT).show();
						}
						mMyCameraControlerInfoText.setText("已拍" + mVideoPathList.size() + "个视频");
						mCamera.startPreview();
					}
				} catch (Exception e) {
					Toast.makeText(mContext, "拍摄视频失败", Toast.LENGTH_SHORT).show();
					mRecordingFlag = false;
					mMediaRecorder.stop();
					mMediaRecorder.release();
					mMediaRecorder = null;
					mCamera.lock();
					destory();
					init(mCameraTakeMode);
				}
			}
		});
		mMyCameraVideoControlerCancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FileManager.deleteDirectory(MYCAMERA_IMAGEPATH + mCameraSaveFolder);
				FileManager.deleteDirectory(MYCAMERA_VIDEOPATH + mCameraSaveFolder);
				FileManager.deleteDirectory(MYCAMERA_TEMPPATH);
				((Activity) mContext).finish();
			}
		});
		mMyCameraVideoControlerOKBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mVideoOKClickListener != null) {
					mVideoOKClickListener.onClick(MYCAMERA_VIDEOPATH + mCameraSaveFolder, mVideoPathList.size());
				}
			}
		});
	}

	private void disableControler() {
		if (mMyCameraControlerCameraChange != null) {
			mMyCameraControlerCameraChange.setClickable(false);
		}
		if (mMyCameraControlerFlashlightText != null) {
			mMyCameraControlerFlashlightText.setClickable(false);
		}
		if (mMyCameraPictureControlerOKBtn != null) {
			mMyCameraPictureControlerOKBtn.setClickable(false);
		}
		if (mMyCameraPictureControlerCancelBtn != null) {
			mMyCameraPictureControlerCancelBtn.setClickable(false);
		}
		if (mMyCameraVideoControlerCancelBtn != null) {
			mMyCameraVideoControlerCancelBtn.setClickable(false);
		}
		if (mMyCameraVideoControlerOKBtn != null) {
			mMyCameraVideoControlerOKBtn.setClickable(false);
		}
	}

	private void enableControler() {
		if (mMyCameraControlerCameraChange != null) {
			mMyCameraControlerCameraChange.setClickable(true);
		}
		if (mMyCameraControlerFlashlightText != null) {
			mMyCameraControlerFlashlightText.setClickable(true);
		}
		if (mMyCameraPictureControlerOKBtn != null) {
			mMyCameraPictureControlerOKBtn.setClickable(true);
		}
		if (mMyCameraPictureControlerCancelBtn != null) {
			mMyCameraPictureControlerCancelBtn.setClickable(true);
		}
		if (mMyCameraVideoControlerCancelBtn != null) {
			mMyCameraVideoControlerCancelBtn.setClickable(true);
		}
		if (mMyCameraVideoControlerOKBtn != null) {
			mMyCameraVideoControlerOKBtn.setClickable(true);
		}
	}

	private Runnable mCameraTimerTask = new Runnable() {
		public void run() {
			if (mCameraTimerStartTime != null) {
				mMyCameraControlerInfoText.setText(DateParse.getHourDif(mCameraTimerStartTime, null));
				mCameraTimerHandler.postDelayed(this, 1000);
			}
		}
	};

	private void setFlashLight(String flashLightMode) {
		Camera.Parameters mParameters;
		mParameters = mCamera.getParameters();
		mParameters.setFlashMode(flashLightMode);
		mCamera.setParameters(mParameters);
		if (flashLightMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF)) {
			mMyCameraControlerFlashlightText.setText("关闭");
		} else if (flashLightMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON)) {
			mMyCameraControlerFlashlightText.setText("开启");
		} else if (flashLightMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_AUTO)) {
			mMyCameraControlerFlashlightText.setText("自动");
		}
	}

	private boolean changeCamera() {
		try {
			int cameraCount = Camera.getNumberOfCameras();
			CameraInfo cameraInfo = new CameraInfo();
			CameraInfo cameraInfoNow = new CameraInfo();
			if (cameraCount < 2) {
				Toast.makeText(getContext(), "该手机不支持前置摄像头功能", Toast.LENGTH_SHORT).show();
				return true;
			}
			Camera.getCameraInfo(mCameraId, cameraInfoNow);
			for (int i = 0; i < cameraCount; i++) {
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfoNow.facing != cameraInfo.facing) {
					destory();
					mCamera = MyCameraUtil.getCameraInstance(i);
					calculateSize(mCamera);
					mMyCameraPreview.refreshPreview(mCamera, mCameraTakeMode);
					mCameraId = i;
					break;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean videoMove() {
		try {
			String fileName = MYCAMERA_SAVEPREFIX + System.currentTimeMillis() + ".3gp";
			File file = new File(MYCAMERA_VIDEOPATH + mCameraSaveFolder);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(MYCAMERA_VIDEOPATH + mCameraSaveFolder, fileName);
			if (mMediaRecorderAudioFile.exists()) {
				mMediaRecorderAudioFile.renameTo(file);
				mVideoPathList.add(file.getPath());
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressLint("NewApi")
	private void calculateSize(Camera camera) {
		Parameters parameters = camera.getParameters();
		List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
		List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
		List<Size> supportedVideoSizes = parameters.getSupportedVideoSizes();
		int screenWidth = ((Activity) getContext()).getResources().getDisplayMetrics().widthPixels;
		int screenHeight = ((Activity) getContext()).getResources().getDisplayMetrics().heightPixels;
		mPreviewSize = camera.new Size(0, 0);
		mPictureSize = null;
		mVideoSize = null;
		for (int i = 0; i < supportedPreviewSizes.size(); i++) {
			if (supportedPreviewSizes.get(i).width > mPreviewSize.width && supportedPreviewSizes.get(i).width <= screenHeight && supportedPreviewSizes.get(i).height > mPreviewSize.height && supportedPreviewSizes.get(i).height <= screenWidth) {
				mPreviewSize = supportedPreviewSizes.get(i);
			}
		}
//		double aspectRatio = (double) mPreviewSize.width / (double) mPreviewSize.height;
		// Max size
//		for (int i = 0; i < supportedPictureSizes.size(); i++) {
//			double aspectRatioTemp = (double) supportedPictureSizes.get(i).width / (double) supportedPictureSizes.get(i).height;
//			if (mPictureSize == null || Math.abs(aspectRatioTemp - aspectRatio) < Math.abs((double) mPictureSize.width / (double) mPictureSize.height - aspectRatio)) {
//				mPictureSize = supportedPictureSizes.get(i);
//				if (Math.abs(aspectRatioTemp - aspectRatio) == 0) {
//					break;
//				}
//			}
//		}
		// width = 500
		for (int i = 0; i < supportedPictureSizes.size(); i++) {
			int longEdge = supportedPictureSizes.get(i).width > supportedPictureSizes.get(i).height ? supportedPictureSizes.get(i).width : supportedPictureSizes.get(i).height;
			if (mPictureSize == null || Math.abs(longEdge - 500) < Math.abs((mPictureSize.width > mPictureSize.height ? mPictureSize.width : mPictureSize.height) - 500)) {
				mPictureSize = supportedPictureSizes.get(i);
			}
		}
		// Max size
//		for (int i = 0; i < supportedVideoSizes.size(); i++) {
//			double aspectRatioTemp = (double) supportedVideoSizes.get(i).width / (double) supportedVideoSizes.get(i).height;
//			if (mVideoSize == null || Math.abs(aspectRatioTemp - aspectRatio) < Math.abs((double) mVideoSize.width / (double) mVideoSize.height - aspectRatio)) {
//				mVideoSize = supportedVideoSizes.get(i);
//				if (Math.abs(aspectRatioTemp - aspectRatio) == 0) {
//					break;
//				}
//			}
//		}
		// size 1280 x 720
		if (supportedVideoSizes == null) {
			for (int i = 0; i < supportedPreviewSizes.size(); i++) {
				if (mVideoSize == null || (Math.pow(supportedPreviewSizes.get(i).width - 1280, 2) + Math.pow(supportedPreviewSizes.get(i).height - 720, 2)) < (Math.pow(mVideoSize.width - 1280, 2) + Math.pow(mVideoSize.height - 720, 2))) {
					mVideoSize = supportedPreviewSizes.get(i);
				}
			}
		} else {
			for (int i = 0; i < supportedVideoSizes.size(); i++) {
				if (mVideoSize == null || (Math.pow(supportedVideoSizes.get(i).width - 1280, 2) + Math.pow(supportedVideoSizes.get(i).height - 720, 2)) < (Math.pow(mVideoSize.width - 1280, 2) + Math.pow(mVideoSize.height - 720, 2))) {
					mVideoSize = supportedVideoSizes.get(i);
				}
			}
		}
	}

	private void updateRotateDegree() {
		if (mGSensor != null) {
			int rotateDegree = mGSensor.getRotateDegree();
			if (rotateDegree > 45 && rotateDegree <= 135) {
				mRotateDegree = 0;
			} else if (rotateDegree > -45 && rotateDegree <= 45) {
				mRotateDegree = 90;
			} else if (rotateDegree > -135 && rotateDegree <= -45) {
				mRotateDegree = 180;
			} else {
				mRotateDegree = 270;
			}
		}
	}

	public boolean onKeyBack() {
		try {
			if (mRecordingFlag) {
				mMyCameraVideoControlerCaptureBtn.setBackgroundResource(R.drawable.mycamera_controler_capture_icon_normal);
				mCameraTimerStartTime = null;
				enableControler();
				mRecordingFlag = false;
				mMediaRecorder.stop();
				mMediaRecorder.release();
				mMediaRecorder = null;
				mCamera.lock();
				if (videoMove()) {
					Toast.makeText(mContext, "视频保存成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "视频保存失败", Toast.LENGTH_SHORT).show();
				}
				mMyCameraControlerInfoText.setText("已拍" + mVideoPathList.size() + "个视频");
				mCamera.startPreview();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Toast.makeText(mContext, "拍摄视频失败", Toast.LENGTH_SHORT).show();
			mRecordingFlag = false;
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
			mCamera.lock();
		}
		return false;
	}

	public void pause() {
		onKeyBack();
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			mMyCameraPreview.refreshPreview(null, mCameraTakeMode);
		}
	}

	public void resume() {
		if (mCamera == null) {
			mCamera = MyCameraUtil.getCameraInstance(mCameraId);
			if (mCamera == null) {
				Toast.makeText(mContext, "摄像头初始化失败，请稍后重试！", Toast.LENGTH_SHORT).show();
				((Activity) mContext).finish();
			}
			mMyCameraPreview.refreshPreview(mCamera, mCameraTakeMode);
			setFlashLight(mFlashLightMode);
		}
	}

	public void destory() {
		if (mCamera != null && mMyCameraPreview != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			mMyCameraPreview.refreshPreview(null, mCameraTakeMode);
		}
	}
}
