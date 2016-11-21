package com.sobey.cloud.webtv.personal;

import java.util.ArrayList;
import java.util.List;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.PhotoModel;
import com.sobey.cloud.webtv.utils.Display;
import com.sobey.cloud.webtv.utils.FileUtil;
import com.sobey.cloud.webtv.utils.SobeyConstants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class HeadImageChoosephotoAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private int ImageViewH = 200;
	private int CheckBoxH = 44;
	public static int maxSize = SobeyConstants.choosePhotoMaxSize;
	private ArrayList<String> choosedPhotoPath;
	private List<PhotoModel> datas;
	private Context mContext;
	private final String TAG = this.getClass().getName();
	private Handler mHandler;
	private String save_path;

	public HeadImageChoosephotoAdapter(Context context, Handler handler) {
		mInflater = LayoutInflater.from(context);
		choosedPhotoPath = new ArrayList<String>();
		this.mContext = context;
		this.mHandler = handler;
		initWH();
	}

	private void initWH() {
		ImageViewH = Display.scaleH(ImageViewH);
		CheckBoxH = Display.scaleH(CheckBoxH);
	}

	public void setData(List<PhotoModel> datas) {
		this.datas = datas;
	}

	public void setDataNoTakePhoto(List<PhotoModel> datas) {
		this.datas = datas;
	}

	public void setChoosedPhotoPath(ArrayList<String> choosedPhotoPath) {
		this.choosedPhotoPath = choosedPhotoPath;
	}

	public ArrayList<String> getChoosedPhotoPath() {
		return this.choosedPhotoPath;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public PhotoModel getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		PhotoModel photoModel = getItem(position);
		String path = photoModel.path;
		boolean isChoosed = photoModel.choosed;
		Log.d("zxd", "照片路径:" + path);
		final ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_choose_head_image_gridview, null);
			// holder.iv = (ImageView)
			// convertView.findViewById(R.id.item_choose_photo_iv);
			// holder.layout =
			// convertView.findViewById(R.id.item_choose_photo_choosed_layout);
			// holder.layout2 = (LinearLayout)
			// convertView.findViewById(R.id.item_choose_photo_take_photo_layout);
			// holder.cb = (CheckBox)
			// convertView.findViewById(R.id.item_choose_photo_choose_cb);
			// holder.takePhotoTv = (TextView)
			// convertView.findViewById(R.id.item_choose_photo_take_photo_tv);
			GinInjector.manualInjectView(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Display.setLayoutParams(holder.iv, ImageViewH, ImageViewH);
		Display.setLayoutParams(holder.layout2, ImageViewH, ImageViewH);
		Display.setLayoutParams(holder.layout, ImageViewH, ImageViewH);
		Display.setLayoutParams(holder.cb, CheckBoxH, CheckBoxH);
		holder.iv.clear();
		ImageLoader.getInstance().displayImage(path, holder.iv, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				holder.iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_third_load_bg));
			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				holder.iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ptoto_load_failed));
			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				holder.iv.setImageBitmap(arg2);
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {

			}
		});
		ImageLoader.getInstance().displayImage(path, holder.iv);
		MyOnClickListener clickListener = new MyOnClickListener(photoModel);
		holder.iv.setOnClickListener(clickListener);
		holder.layout2.setOnClickListener(clickListener);

		if (photoModel.type == 1) {
			holder.layout2.setVisibility(View.VISIBLE);
			holder.layout.setVisibility(View.GONE);
			holder.cb.setVisibility(View.GONE);
			holder.iv.setVisibility(View.GONE);
		} else {
			holder.layout2.setVisibility(View.GONE);
			holder.layout.setVisibility(View.VISIBLE);
			holder.cb.setVisibility(View.VISIBLE);
			holder.iv.setVisibility(View.VISIBLE);
		}
		if (isChoosed) {
			holder.layout.setVisibility(View.VISIBLE);

		} else {
			holder.layout.setVisibility(View.GONE);
		}
		holder.cb.setChecked(isChoosed);

		return convertView;
	}

	private class MyOnClickListener implements OnClickListener {
		private PhotoModel photoModel;

		MyOnClickListener(PhotoModel photoModel) {
			this.photoModel = photoModel;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.item_choose_photo_iv:

				doImageClickAction(photoModel);

				break;
			case R.id.item_choose_photo_take_photo_layout:
				// 去拍照
				// takePhoto();

				break;
			default:
				break;
			}
		}

	}

	private void doImageClickAction(PhotoModel photoModel) {
		// 如果等于10张并且是选择的未被选择的图片 返回不再添加
		// if(choosedPhotoPath.size() >= maxSize && !photoModel.choosed){
		// String text =
		// mContext.getResources().getString(R.string.out_of_max_photo_size);
		// Toast.makeText(mContext, String.format(text, maxSize),
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		String path = photoModel.path;
		if (path.contains("file://")) {
			path = path.replace("file://", "");
		}

		if (!FileUtil.isFileEnable(path)) {
			Toast.makeText(mContext, R.string.this_file_not_available, Toast.LENGTH_SHORT).show();
			return;
		}

		// 如果长度大于单张图片的限制 返回不再添加
		// if(FileUtil.isFileOutofLength(path, FileUtil.MAX_PHOTO_SIZE)){
		// Toast.makeText(mContext,
		// R.string.toast_text_choose_photo_failed_more_max_length,
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		photoModel.choosed = !photoModel.choosed;
		if (photoModel.choosed) {
			// if(!choosedPhotoPath.contains(path)){
			// choosedPhotoPath.add(path);
			// }
			Message message = new Message();
			message.what = 1;
			message.obj = path;
			// mHandler.sendEmptyMessage(1);
			mHandler.sendMessage(message);
		} else {
			// choosedPhotoPath.remove(path);
		}
		// mHandler.sendEmptyMessage(1);
		notifyDataSetChanged();
	}

	// private void takePhoto() {
	// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	// String fileDir = FileUtil.PHOTO_APP;
	// File file = new File(fileDir);
	// if (!file.exists()) {
	// file.mkdirs();
	// }
	// String fileName = FileUtil.getPhotoFileName();
	// save_path = fileDir + fileName;
	// Log.i(TAG, "take photo save_path:" + save_path);
	// File file_uri = new File(fileDir, fileName);
	// Uri imageUri = Uri.fromFile(file_uri);
	// intent.putExtra(MediaStore.Images.Media.ORIENTATION, 1);
	// intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
	// intent.putExtra("output", imageUri);
	// ((Activity)mContext).startActivityForResult(intent,
	// GroupPostSubjectActivity.REQ_CODE_FOR_GET_PICS);
	// }
	public String getPhotoSavePath() {
		return save_path;
	}

	private class ViewHolder {
		@GinInjectView(id = R.id.item_choose_photo_iv)
		AdvancedImageView iv;
		@GinInjectView(id = R.id.item_choose_photo_choosed_layout)
		View layout;
		@GinInjectView(id = R.id.item_choose_photo_take_photo_layout)
		LinearLayout layout2;
		@GinInjectView(id = R.id.item_choose_photo_choose_cb)
		CheckBox cb;
		@GinInjectView(id = R.id.item_choose_photo_take_photo_tv)
		TextView takePhotoTv;
	}

}
