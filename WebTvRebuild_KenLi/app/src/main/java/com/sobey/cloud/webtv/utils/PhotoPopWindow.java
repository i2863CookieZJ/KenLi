package com.sobey.cloud.webtv.utils;

import java.io.File;

import com.dylan.common.utils.DateParse;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.personal.HeadImageChooseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoPopWindow {
	public static final int PHOTO_POPWINDOW_TAKE_PHOTO = 1;
	public static final int PHOTO_POPWINDOW_SELECT_PICTURE = 2;

	public static final int CHOOSED_IMAGE_FILE = 3;
	private PopupWindow mPhotoPopupWindow = null;
	private View attachView;
	private TextView mPopwindowPhotoTake;
	private TextView mPopwindowPhotoSelect;
	private TextView mPopwindowPhotoCancel;
	private String mPhotoName;
	private File mPhotoFile;
	private int mHeight;
	private int mScreenWidth;

	public PhotoPopWindow(final Context context, View attachView) {
		this.attachView = attachView;
		View mPhotoView = LayoutInflater.from(context).inflate(R.layout.layout_popwindow_photo, null);
		mPhotoPopupWindow = new PopupWindow(mPhotoView);
		mPopwindowPhotoTake = (TextView) mPhotoView.findViewById(R.id.popwindow_photo_take);
		mPopwindowPhotoSelect = (TextView) mPhotoView.findViewById(R.id.popwindow_photo_select);
		mPopwindowPhotoCancel = (TextView) mPhotoView.findViewById(R.id.popwindow_photo_cancel);

		mPopwindowPhotoTake.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String sdcardPath = Environment.getExternalStorageState();
				if (sdcardPath.equals(Environment.MEDIA_MOUNTED)) {
					File dir = new File(Environment.getExternalStorageDirectory() + MConfig.CachePath);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					mPhotoName = getPhotoFileName();
					mPhotoFile = new File(dir, mPhotoName);
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
					((Activity) context).startActivityForResult(intent, PHOTO_POPWINDOW_TAKE_PHOTO);
				} else {
					Toast.makeText(context, "请为手机安装sd卡后,再进行该操作", Toast.LENGTH_LONG).show();
				}
				hidePhotoWindow();
			}
		});

		mPopwindowPhotoSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//使用系统图库
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				((Activity) context).startActivityForResult(intent, PHOTO_POPWINDOW_SELECT_PICTURE);

				// Intent intent=new Intent(context,
				// HeadImageChooseActivity.class);
				// ((Activity)context).startActivityForResult(intent,
				// CHOOSED_IMAGE_FILE);
				// String sdcardPath = Environment.getExternalStorageState();
				// if (sdcardPath.equals(Environment.MEDIA_MOUNTED)) {
				// Intent intent = new Intent();
				// intent.setType("image/*");
				// intent.setAction(Intent.ACTION_GET_CONTENT);
				// ((Activity) context).startActivityForResult(intent,
				// PHOTO_POPWINDOW_SELECT_PICTURE);
				// } else {
				// Toast.makeText(context, "请为手机安装sd卡后,再进行该操作",
				// Toast.LENGTH_LONG).show();
				// }
				hidePhotoWindow();
			}
		});

		mPopwindowPhotoCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hidePhotoWindow();
			}
		});

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mHeight = metrics.heightPixels;
		mScreenWidth = metrics.widthPixels;
	}

	public void showPhotoWindow() {
		if (!mPhotoPopupWindow.isShowing()) {
			mPhotoPopupWindow.showAtLocation(attachView, Gravity.BOTTOM, 0, 0);
			mPhotoPopupWindow.update(0, 0, mScreenWidth, mHeight);
		}
	}

	public void hidePhotoWindow() {
		if (mPhotoPopupWindow.isShowing()) {
			mPhotoPopupWindow.dismiss();
		}
	}

	public boolean isShowing() {
		return mPhotoPopupWindow.isShowing();
	}

	public File getPhotoFile() {
		return mPhotoFile;
	}

	private String getPhotoFileName() {
		return DateParse.getNowDate("'IMG'_yyyyMMdd_HHmmss") + ".png";
	}
}
