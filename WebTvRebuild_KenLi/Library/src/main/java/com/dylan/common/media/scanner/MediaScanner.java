package com.dylan.common.media.scanner;

import java.io.File;
import java.lang.ref.WeakReference;

import com.third.library.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

public class MediaScanner {

	private static final int SCAN_MEDIA_START = 1;
	private static final int SCAN_MEDIA_FINISH = 2;
	private static final int SCAN_MEDIA_FILE = 3;
	private static final int SCAN_MEDIA_FILE_FINISH_INT = 4;
	private static final int SCAN_MEDIA_FILE_FAIL_INT = 5;

	private static Context mContext;
	private static File mPhotoFile;
	private static MediaActionReceiver mActionReceiver;
	private static int mRequestCode = 0;
	private static ProgressDialog delLoadingDialog = null;
	private static Handler mHandler = null;

	public MediaScanner(Context context, int MEDIA_SCANNER_ONACIVITYRESULT_REQUESTCODE) {
		mContext = context;
		mRequestCode = MEDIA_SCANNER_ONACIVITYRESULT_REQUESTCODE;
		mActionReceiver = null;
		mPhotoFile = null;
		delLoadingDialog = null;
		mHandler = null;
	}

	public void start(File photoFile) {
		mHandler = new MyHandler((Activity) mContext);
		mPhotoFile = photoFile;
		mActionReceiver = new MediaActionReceiver();
		try {
			IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
			intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
			intentFilter.addDataScheme("file");
			intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			mContext.registerReceiver(mActionReceiver, intentFilter);
		} catch (RuntimeException e) {
		}
		mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mPhotoFile)));
	}

	public class MediaActionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
				mHandler.sendEmptyMessage(SCAN_MEDIA_START);
			}
			if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
				mHandler.sendEmptyMessage(SCAN_MEDIA_FINISH);
			}
			if (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE.equals(action)) {
				mHandler.sendEmptyMessage(SCAN_MEDIA_FILE);
			}
		}
	}

	static class MyHandler extends Handler {
		private WeakReference<Activity> mActivity;

		MyHandler(Activity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}

		public void handleMessage(Message msg) {
			Activity activity = mActivity.get();
			if (activity != null) {
				super.handleMessage(msg);
				switch (msg.what) {
				case SCAN_MEDIA_START:
					delLoadingDialog = onCreateDialogByResId(R.string.loading);
					delLoadingDialog.show();
					break;
				case SCAN_MEDIA_FINISH:
					galleryPhoto();
					break;
				case SCAN_MEDIA_FILE:
					delLoadingDialog = onCreateDialogByResId(R.string.loading);
					delLoadingDialog.show();
					ScanMediaThread sthread = new ScanMediaThread(mContext, 40, 300);
					sthread.run();
					break;
				case SCAN_MEDIA_FILE_FINISH_INT:
					galleryPhoto();
					break;
				case SCAN_MEDIA_FILE_FAIL_INT:
					if (delLoadingDialog != null && delLoadingDialog.isShowing()) {
						delLoadingDialog.dismiss();
					}
					try {
						mContext.unregisterReceiver(mActionReceiver);
					} catch (Exception e) {
					}
					break;
				}
			}
		}

		public class ScanMediaThread extends Thread {
			private int scanCount = 5;
			private int interval = 50;
			private Context cx = null;

			public ScanMediaThread(Context context, int count, int i) {
				scanCount = count;
				interval = i;
				cx = context;
				this.setName(System.currentTimeMillis() + "_ScanMediaThread");
			}

			@Override
			public void run() {
				if (this.cx == null)
					return;
				try {
					int j = 0;
					Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					ContentResolver cr = mContext.getContentResolver();
					Cursor cursor;
					for (j = 0; j < this.scanCount; j++) {
						Thread.sleep(this.interval);
						cursor = cr.query(imgUri, null, MediaStore.Images.Media.DISPLAY_NAME + "='" + mPhotoFile.getName() + "'", null, null);
						if (cursor != null && cursor.getCount() > 0) {
							mHandler.sendEmptyMessage(SCAN_MEDIA_FILE_FINISH_INT);
							break;
						}
					}
					if (j == this.scanCount) {
						mHandler.sendEmptyMessage(SCAN_MEDIA_FILE_FAIL_INT);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void galleryPhoto() {
		try {
			long id = 0;
			Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			ContentResolver cr = mContext.getContentResolver();
			Cursor cursor = cr.query(imgUri, null, MediaStore.Images.Media.DISPLAY_NAME + "='" + mPhotoFile.getName() + "'", null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToLast();
				id = cursor.getLong(0);
				Uri uri = ContentUris.withAppendedId(imgUri, id);// Uri.fromFile(mCurrentPhotoFile);
				final Intent intent = getCropImageIntent(uri);
				if (intent != null) {
					((Activity) mContext).startActivityForResult(intent, mRequestCode);
				}
			}
			if (delLoadingDialog != null && delLoadingDialog.isShowing()) {
				delLoadingDialog.dismiss();
			}
			try {
				mContext.unregisterReceiver(mActionReceiver);
			} catch (Exception e) {
			}
		} catch (Exception ee) {
		}
	}

	/**
	 * 根据资源ID获得ProgressDialog对象
	 * 
	 * @param resId
	 * @return
	 */
	protected static ProgressDialog onCreateDialogByResId(int resId) {
		ProgressDialog dialog = new ProgressDialog(mContext);
		dialog.setMessage(mContext.getResources().getText(resId));
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		return dialog;
	}

	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setData(photoUri);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		return intent;
	}

	public void destory() {
		try {
			mContext.unregisterReceiver(mActionReceiver);
		} catch (Exception e) {
		}
	}
}
