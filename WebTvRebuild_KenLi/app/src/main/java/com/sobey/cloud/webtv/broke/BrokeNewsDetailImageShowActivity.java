package com.sobey.cloud.webtv.broke;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dylan.uiparts.photoalbum.HackyViewPager;
import com.dylan.uiparts.photoalbum.PhotoView;
import com.lib.mediachooser.MediaChooserConstants;
import com.sobey.cloud.webtv.kenli.R;

import android.app.Activity;
import android.content.Intent;
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

public class BrokeNewsDetailImageShowActivity extends Activity {

	private ViewPager mViewPager;
	private static PhotoView mPhotoView = null;
	private static View mControlerView = null;
	private ImageButton mBackBtn;
	private JSONArray mInformation;
	private ArrayList<String> mPhotoUrls = new ArrayList<String>();

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
		try {
			String str = getIntent().getStringExtra("information");
			mInformation = new JSONArray(str);
			for (int i = 0; i < mInformation.length(); i++) {
				JSONObject contentObject = mInformation.getJSONObject(i);
				if (contentObject.optString("type").equalsIgnoreCase("image")) {
					mPhotoUrls.add(contentObject.optString("imgpath"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mPhotoUrls.size() == 0) {
			mPhotoUrls.add("");
		}

		mControlerView = LayoutInflater.from(this).inflate(R.layout.activity_broke_news_detail_imageshow_controler, null);
		mBackBtn = (ImageButton) mControlerView.findViewById(R.id.back_btn);

		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResultData();
				finish();
			}
		});

		mViewPager.setAdapter(new SamplePagerAdapter(mPhotoUrls));
		mViewPager.setCurrentItem(0);
	}

	private static class SamplePagerAdapter extends PagerAdapter {
		private ArrayList<String> urls = new ArrayList<String>();
		private int urlsSize;
		private int mPosition = 0;

		public SamplePagerAdapter(ArrayList<String> urls) {
			this.urls = urls;
			urlsSize = urls.size();
		}

		@Override
		public int getCount() {
			return urlsSize;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			if (position >= urlsSize) {
				return null;
			}
			mPhotoView = new PhotoView(container.getContext(), mControlerView, null);
			mPhotoView.setDefaultImage(R.drawable.aboutus_logo);
			mPhotoView.setImageURL(urls.get(position));
			mPhotoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			container.addView(mPhotoView, 0);
			return mPhotoView;
		}

		@Override
		public void setPrimaryItem(View container, int position, Object object) {
			mPosition = position;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@SuppressWarnings("unused")
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
