package com.lib.mediachooser.activity;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dylan.common.utils.BitmapResize;
import com.dylan.common.utils.ScaleConversion;
import com.dylan.uiparts.photoalbum.HackyViewPager;
import com.dylan.uiparts.photoalbum.PhotoView;
import com.lib.mediachooser.MediaChooserConstants;
import com.third.library.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

public class ImageShowActivity extends Activity {

	private ViewPager mViewPager;
	private static PhotoView mPhotoView = null;
	private static View mControlerView = null;
	private ImageButton mBackBtn;
	private static ImageButton mSelectBtn = null;
	private JSONArray mInformation;
	private ArrayList<String> mPhotoUrls = new ArrayList<String>();
	private static ArrayList<Boolean> mPhotoSelected = new ArrayList<Boolean>();
	private static int mScreenWidth = 0;
	private static int mScreenHeight = 0;
	private static int mControlerViewHeight = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPhotoView = null;
		mControlerView = null;
		mViewPager = new HackyViewPager(this);
		setContentView(mViewPager);
		setupActivity();
	}

	public void setupActivity() {
		int startPosition = 0;
		mScreenWidth = getResources().getDisplayMetrics().widthPixels;
		mScreenHeight = getResources().getDisplayMetrics().heightPixels;
		mControlerViewHeight = ScaleConversion.dip2px(this, 50);
		try {
			mSelectBtn = null;
			mPhotoSelected = new ArrayList<Boolean>();
			startPosition = getIntent().getIntExtra("position", 0);
			String str = getIntent().getStringExtra("information");
			mInformation = new JSONArray(str);
			for (int i = 0; i < mInformation.length(); i++) {
				JSONObject object = mInformation.getJSONObject(i);
				mPhotoUrls.add(object.getString("filepath"));
				mPhotoSelected.add(object.getBoolean("status"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mPhotoUrls.size() == 0) {
			mPhotoUrls.add("");
			mPhotoSelected.add(false);
		}

		mControlerView = LayoutInflater.from(this).inflate(R.layout.activity_imageshow_controler, null);
		mBackBtn = (ImageButton) mControlerView.findViewById(R.id.back_btn);
		mSelectBtn = (ImageButton) mControlerView.findViewById(R.id.select_btn);

		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResultData();
				finish();
			}
		});
		mSelectBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int index = ((SamplePagerAdapter) mViewPager.getAdapter()).getCurrentPosition();
				mPhotoSelected.set(index, !mPhotoSelected.get(index));
				mSelectBtn.setSelected(mPhotoSelected.get(index));
			}
		});

		mViewPager.setAdapter(new SamplePagerAdapter(mPhotoUrls));
		mViewPager.setCurrentItem(startPosition);
	}

	private static class SamplePagerAdapter extends PagerAdapter {
		private ArrayList<String> filePath = new ArrayList<String>();
		private int filePathSize;
		private int mPosition = 0;

		public SamplePagerAdapter(ArrayList<String> filePath) {
			this.filePath = filePath;
			filePathSize = filePath.size();
		}

		@Override
		public int getCount() {
			return filePathSize;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			if (position >= filePathSize) {
				return null;
			}
			try {
				mPhotoView = new PhotoView(container.getContext(), mControlerView, null);
				File file;
				Bitmap bitmapTemp;
				file = new File(filePath.get(position));
				if (file.exists()) {
					bitmapTemp = BitmapResize.getBitmapByName(filePath.get(position), mScreenWidth, mScreenHeight);
					mPhotoView.setImageBitmap(bitmapTemp);
				}
				mPhotoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				mPhotoView.canToggleControler(false);
				mPhotoView.setControlerViewHeight(mControlerViewHeight);
				container.addView(mPhotoView, 0);
				return mPhotoView;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public void setPrimaryItem(View container, int position, Object object) {
			mPosition = position;
			mSelectBtn.setSelected(mPhotoSelected.get(mPosition));
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		public int getCurrentPosition() {
			return mPosition;
		}
	}

	private void setResultData() {
		try {
			JSONArray information = new JSONArray();
			for (int i = 0; i < mPhotoUrls.size(); i++) {
				JSONObject object = new JSONObject();
				object.put("filepath", mPhotoUrls.get(i));
				object.put("status", mPhotoSelected.get(i));
				information.put(object);
			}
			Intent intent = new Intent();
			intent.putExtra("information", information.toString());
			setResult(MediaChooserConstants.MEDIA_TYPE_IMAGE, intent);
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResultData();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (mPhotoView != null) {
			mPhotoView.destroy();
		}
		super.onDestroy();
	}
}
